package com.epam.streamExample;

import com.epam.streamExample.htmlParser.HtmlParser;
import com.epam.streamExample.receiver.HtmlReceiver;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        String host = "bash.im";
        int port = 80;
        String additionalURL = "quote";
        long additionalNumber;
        if (args.length > 0) {
            additionalNumber = Long.parseLong(args[0]);
        } else {
            throw new IllegalArgumentException("no parameters");
        }

        HtmlReceiver htmlReceiver = new HtmlReceiver(host, port);
        String htmlPage = htmlReceiver.getHtmlPage(additionalURL, additionalNumber);
        HtmlParser parser = new HtmlParser();
        System.out.println(parser.parseBashPage(htmlPage));
    }
}
