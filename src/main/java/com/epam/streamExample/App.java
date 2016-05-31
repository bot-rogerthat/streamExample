package com.epam.streamExample;

import com.epam.streamExample.htmlParser.HtmlParser;
import com.epam.streamExample.receiver.HtmlReceiver;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        String host = "bash.im";
        int port = 80;
        String additionalURL = "quote";
        long additionalNumber = checkArgs(args);

        HtmlReceiver htmlReceiver = new HtmlReceiver(host, port);
        byte[] htmlPage = htmlReceiver.getHtmlPage(additionalURL, additionalNumber);
        HtmlParser parser = new HtmlParser(htmlPage);
        if (parser.isChunked()) {
            if (!"200 OK".equals(parser.findHttpStatus())) {
                throw new IllegalArgumentException(parser.findHttpStatus());
            }
            parser.replaceCharset(parser.findCharset());
            System.out.println(parser.parseBashPage());
        } else {
            throw new IllegalArgumentException("Transfer-Encoding not chunked");
        }
    }

    private static long checkArgs(String[] args) {
        if (args.length > 0) {
            return Long.parseLong(args[0]);
        } else {
            throw new IllegalArgumentException("no parameters");
        }
    }
}
