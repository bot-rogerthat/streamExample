package com.epam.streamExample.receiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HtmlReceiver {
    private String host;
    private int port;

    public HtmlReceiver(String host, int port) {
        this.host = host;
        this.port = port;
    }


    private String getRequest(String additionalUrl, long additionalNumber) {
        return "GET /" + additionalUrl + "/" + additionalNumber + " HTTP/1.1" + "\n"
                + "Host: " + host + "\n"
                + "\n";
    }

    public byte[] getHtmlPage(String additionalUrl, long additionalNumber) {
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
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
