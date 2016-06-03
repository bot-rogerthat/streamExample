package com.epam.streamExample.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

    public HtmlParser() {
    }

    public String parseBashPage(String htmlPage) {
        if (htmlPage == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (htmlPage.isEmpty()) {
            throw new IllegalArgumentException("value is empty");
        }
        Pattern pattern = Pattern.compile("<div class=\"text\">(.+)</div>");
        Matcher matcher = pattern.matcher(htmlPage);
        if (matcher.find()) {
            return matcher.group(1).replace("<br>", "\n").replace("&quot;", "\"");
        } else {
            return "not found";
        }
    }
}
