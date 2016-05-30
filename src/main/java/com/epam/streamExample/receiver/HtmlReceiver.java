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

    private String getRequest(String additionalUrl, long additionalNumber) {
        return "GET /" + additionalUrl + "/" + additionalNumber + " HTTP/1.1" + "\n"
                + "Host: " + host + "\n"
                + "\n";
    }

    public String getHtmlPage(String additionalUrl, long additionalNumber) {
        Socket socket = null;
        OutputStream os = null;
        InputStream is = null;
        String result = "";
        try {
            socket = new Socket(host, port);
            os = socket.getOutputStream();
            os.write(getRequest(additionalUrl, additionalNumber).getBytes());
            os.flush();
            is = socket.getInputStream();
            if (is != null) {
                byte[] buffer = new byte[64 * 1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
