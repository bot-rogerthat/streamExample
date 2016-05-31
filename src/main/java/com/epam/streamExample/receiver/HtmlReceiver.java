package com.epam.streamExample.receiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlReceiver {
    private String host;
    private int port;

    public HtmlReceiver(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String getCharset(String value) {
        Pattern pattern = Pattern.compile("charset=\"(.*)\" />$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "utf-8";
        }
    }

    private String getHttpStatus(String value) {
        Pattern pattern = Pattern.compile("^HTTP\\/1.1 (.*)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "404";
        }
    }

    private boolean checkChunked(String value) {
        Pattern pattern = Pattern.compile("^Transfer-Encoding: (.*)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }

    private String getRequest(String additionalUrl, long additionalNumber) {
        return "GET /" + additionalUrl + "/" + additionalNumber + " HTTP/1.1" + "\n"
                + "Host: " + host + "\n"
                + "\n";
    }

    public String getHtmlPage(String additionalUrl, long additionalNumber) {
        String result = "";
        try (Socket socket = new Socket(host, port);
             OutputStream os = socket.getOutputStream();
             InputStream is = socket.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            os.write(getRequest(additionalUrl, additionalNumber).getBytes());
            os.flush();
            if (is != null) {
                byte[] buffer = new byte[64 * 1024];
                while (true) {
                    int readCount = is.read(buffer);
                    if (readCount == -1) {
                        break;
                    }
                    baos.write(buffer, 0, readCount);
                }
                String charset = getCharset(baos.toString());
                result = baos.toString(charset);
            }
            if (!checkChunked(result)) {
                throw new IllegalArgumentException("Transfer-Encoding not chunked");
            }
            if (!"200".equals(getHttpStatus(result))) {
                throw new IllegalArgumentException("http status: " + getHttpStatus(result));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
