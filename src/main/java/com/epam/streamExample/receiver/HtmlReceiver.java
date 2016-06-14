package com.epam.streamExample.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private int port = 80;
    private String httpStatus;
    private Map<String, String> requestHeaders = new HashMap<>();
    private StringBuilder messageBody = new StringBuilder();
    private static final Logger log = LoggerFactory.getLogger(HtmlReceiver.class);

    public HtmlReceiver() {
    }

    private String methodGet(String host, String tail) {
        return "GET " + tail + " HTTP/1.1\nHost: " + host + "\n\n";
    }

    private void initHtmlHeaders(String host, String request) {
        try (Socket socket = new Socket(host, port);
             OutputStream os = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            os.write(request.getBytes());
            os.flush();
            setHttpStatus(reader.readLine());
            String header = reader.readLine();
            while (header.length() > 0) {
                appendHeaderParameter(header);
                header = reader.readLine();
            }
        } catch (IOException e) {
            log.error("can not get http headers", e);
        }
    }

    private void setHttpStatus(String str) {
        Pattern pattern = Pattern.compile(" \\d+ ");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            httpStatus = matcher.group().trim();
        } else {
            httpStatus = "404";
        }
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
        if (requestHeaders.containsKey("Content-Lenght")) {
            while (true) {
                int toread = Integer.parseInt(requestHeaders.get("Content-Lenght"));
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
        } else {
            String str = reader.readLine();
            while (str != null) {
                messageBody.append(str);
                str = reader.readLine();
            }
        }
    }

    public String getHtmlPage(String url) {
        String host = url.substring(0, url.indexOf("/"));
        String tail = url.substring(url.indexOf("/"));
        String request = methodGet(host, tail);
        initHtmlHeaders(host, request);
        String charset = findCharset();
        try (Socket socket = new Socket(host, port);
             OutputStream os = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), charset))) {

            os.write(request.getBytes());
            os.flush();
            while (true) {
                String str = reader.readLine();
                if ("".equals(str)) {
                    break;
                }
            }
            if ("200".equals(httpStatus)) {
                if ("chunked".equals(requestHeaders.get("Transfer-Encoding"))) {
                    readChunk(reader);
                } else {
                    readNotChunk(reader);
                }
            } else {
                throw new IllegalArgumentException("HttpStatus: " + httpStatus);
            }
        } catch (IOException e) {
            log.error("can not get html page", e);
        }
        return messageBody.toString();
    }
}
