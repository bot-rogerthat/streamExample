package com.epam.streamExample.htmlParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {
    public HtmlParser() {
    }

    public String parseBashPage(String value) {
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
