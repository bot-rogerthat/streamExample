package com.epam.streamExample.parser;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {
    private byte[] page;
    private String decodePage;

    public HtmlParser(byte[] page) {
        this.page = page;
        this.decodePage = new String(page);
    }

    public boolean isChunked() {
        Pattern pattern = Pattern.compile("^Transfer-Encoding: (.*)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(decodePage);
        return matcher.find();
    }

    public String findCharset() {
        Pattern pattern = Pattern.compile("charset=\"(.*)\" />$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(decodePage);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "utf-8";
        }
    }

    public void replaceCharset(String charset) throws UnsupportedEncodingException {
        decodePage = new String(page, charset);
    }

    public String findHttpStatus() {
        Pattern pattern = Pattern.compile("^HTTP\\/1.1 (.*)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(decodePage);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "404";
        }
    }

    public String parseBashPage() {
        if (decodePage == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (decodePage.isEmpty()) {
            throw new IllegalArgumentException("value is empty");
        }
        Pattern pattern = Pattern.compile("<div class=\"text\">(.+)</div>");
        Matcher matcher = pattern.matcher(decodePage);
        if (matcher.find()) {
            return matcher.group(1).replace("<br>", "\n").replace("&quot;", "\"");
        } else {
            return "not found";
        }
    }
}
