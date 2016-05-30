package com.epam.streamExample;

import com.epam.streamExample.receiver.HtmlReceiver;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println(parsePage(htmlPage));
    }

    public static String parsePage(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("value is empty");
        }
        Pattern pattern = Pattern.compile("<div class=\"text\">(.+)</div>");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(1).replace("<br>", "\n").replace("&quot;", "\"");
        } else {
            return "not found";
        }
    }
}
