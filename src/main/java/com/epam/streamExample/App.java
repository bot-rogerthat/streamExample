package com.epam.streamExample;

import com.epam.streamExample.parser.HtmlParser;
import com.epam.streamExample.receiver.HtmlReceiver;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        String host = "bash.im";
        int port = 80;
        long number = checkArgs(args);
        String getRequest = "GET /quote/"+ number + " HTTP/1.1\nHost: " + host + "\n\n";

        HtmlReceiver htmlReceiver = new HtmlReceiver(host, port, getRequest);
        String htmlPage = htmlReceiver.getHtmlPage();
        HtmlParser parser = new HtmlParser(htmlPage);
        System.out.println(parser.parseBashPage());
    }

    private static long checkArgs(String[] args) {
        if (args.length > 0) {
            return Long.parseLong(args[0]);
        } else {
            throw new IllegalArgumentException("no parameters");
        }
    }
}
