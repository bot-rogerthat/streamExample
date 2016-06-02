package com.epam.streamExample.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlReceiver {
    private String host;
    private int port;
    private String additionalUrl;
    private Map<String, String> requestHeaders;
    private StringBuilder messageBody;

    public HtmlReceiver(String host, int port, String additionalUrl) {
        this.host = host;
        this.port = port;
        this.additionalUrl = additionalUrl;
        requestHeaders = new HashMap<>();
        messageBody = new StringBuilder();
        initHtmlHeaders();
    }

    private String getMethodGET() {
        return "GET /" + additionalUrl + " HTTP/1.1" + "\n"
                + "Host: " + host + "\n"
                + "\n";
    }

    private void initHtmlHeaders() {
        try (Socket socket = new Socket(host, port);
             OutputStream os = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            os.write(getMethodGET().getBytes());
            os.flush();
            setHttpStatus(reader.readLine());
            String header = reader.readLine();
            while (header.length() > 0) {
                appendHeaderParameter(header);
                header = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHttpStatus(String s) {
        requestHeaders.put("HttpStatus", s.substring(s.indexOf(" ") + 1));
    }

    private void appendHeaderParameter(String header) {
        int index = header.indexOf(":");
        if (index == -1) {
            throw new IllegalArgumentException("Invalid Header Parameter: " + header);
        }
        requestHeaders.put(header.substring(0, index), header.substring(index + 1, header.length()).trim());
    }

    private String findCharset() {
        String charset = requestHeaders.get("Content-Type");
        Pattern pattern = Pattern.compile("charset=(.*)");
        Matcher matcher = pattern.matcher(charset);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "UTF-8";
        }
    }

    private void readChunk(BufferedReader reader) throws IOException {
        while (true) {
            String str = reader.readLine();
            if (str == null) {
                throw new IOException();
            }
            if (str.length() == 0) {
                continue;
            }
            int toread;
            try {
                toread = Integer.parseInt(str, 16);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Number format error: " + str);
            }
            if (toread == 0) {
                break;
            }
            char[] data = new char[toread];
            int read = 0;
            while (read != toread) {
                read += reader.read(data, read, toread - read);
            }
            messageBody.append(data, 0, read);
        }
    }

    private void readNotChunk(BufferedReader reader) throws IOException {
        String str = reader.readLine();
        while (str != null) {
            messageBody.append(str);
            str = reader.readLine();
        }
    }

    public String getHtmlPage() {
        String charset = findCharset();
        try (Socket socket = new Socket(host, port);
             OutputStream os = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset))) {

            os.write(getMethodGET().getBytes());
            os.flush();
            while (true) {
                String str = reader.readLine();
                if ("".equals(str)) {
                    break;
                }
            }
            if ("200 OK".equals(requestHeaders.get("HttpStatus"))) {
                if ("chunked".equals(requestHeaders.get("Transfer-Encoding"))) {
                    readChunk(reader);
                } else {
                    readNotChunk(reader);
                }
            } else {
                throw new IllegalArgumentException("HttpStatus: " + requestHeaders.get("HttpStatus"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageBody.toString();
    }
}
