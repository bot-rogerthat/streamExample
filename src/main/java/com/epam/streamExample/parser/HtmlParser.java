package com.epam.streamExample.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {
    private String htmlPage;

    public HtmlParser(String htmlPage) {
        this.htmlPage = htmlPage;
    }

    public String parseBashPage() {
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
