package com.epam.streamExample;

import com.epam.streamExample.parser.HtmlParser;
import com.epam.streamExample.receiver.HtmlReceiver;

public class App {
    public static void main(String[] args) {
        long number = checkArgs(args);
        String url = "bash.im/quote/" + number;

        HtmlReceiver htmlReceiver = new HtmlReceiver();
        String htmlPage = htmlReceiver.getHtmlPage(url);
        HtmlParser parser = new HtmlParser();
        System.out.println(parser.parseBashPage(htmlPage));
    }

    private static long checkArgs(String[] args) {
        if (args.length > 0) {
            return Long.parseLong(args[0]);
        } else {
            throw new IllegalArgumentException("no parameters");
        }
    }
}
