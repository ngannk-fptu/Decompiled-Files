/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import com.opensymphony.util.MailUtils;
import com.opensymphony.util.UrlUtils;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TextUtils {
    public static final String[] SINGLE_TAGS = new String[]{"br", "p", "hr"};

    public static final String br(String s) {
        s = TextUtils.noNull(s);
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '\n') {
                str.append("<br/>");
            }
            str.append(s.charAt(i));
        }
        return str.toString();
    }

    public static final String closeTags(String str) {
        HashMap<String, Integer> openTags = new HashMap<String, Integer>();
        str = TextUtils.noNull(str);
        boolean inTag = false;
        boolean inTagName = false;
        boolean inOpenTag = true;
        String tagName = "";
        List<String> singleTags = Arrays.asList(SINGLE_TAGS);
        char[] strA = str.toCharArray();
        for (int i = 0; i < strA.length; ++i) {
            char c = strA[i];
            if (!inTag) {
                if (c != '<') continue;
                inTag = true;
                inTagName = true;
                inOpenTag = true;
                tagName = "";
                continue;
            }
            if (tagName.length() == 0 && c == '/') {
                inOpenTag = false;
                continue;
            }
            if (inTagName && (c == ' ' || c == '>' || c == '/')) {
                int tagCount;
                inTagName = false;
                if (inOpenTag && !singleTags.contains(tagName.toLowerCase())) {
                    if (openTags.get(tagName) == null) {
                        openTags.put(tagName, new Integer(1));
                    } else {
                        tagCount = (Integer)openTags.get(tagName);
                        openTags.put(tagName, new Integer(tagCount + 1));
                    }
                } else if (openTags.get(tagName) != null) {
                    tagCount = (Integer)openTags.get(tagName);
                    if (tagCount > 1) {
                        openTags.put(tagName, new Integer(tagCount - 1));
                    } else {
                        openTags.remove(tagName);
                    }
                }
                if (c != '>') continue;
                inTag = false;
                continue;
            }
            if (inTagName) {
                tagName = tagName + c;
                continue;
            }
            if (c != '>') continue;
            inTag = false;
        }
        Iterator openTagNames = openTags.keySet().iterator();
        StringBuffer closedString = new StringBuffer(str);
        while (openTagNames.hasNext()) {
            String openTagName = (String)openTagNames.next();
            for (int i = 0; i < (Integer)openTags.get(openTagName); ++i) {
                closedString.append("</").append(openTagName).append('>');
            }
        }
        return closedString.toString();
    }

    public static final String colorToHex(Color c) {
        String r = Integer.toHexString(c.getRed());
        String g = Integer.toHexString(c.getGreen());
        String b = Integer.toHexString(c.getBlue());
        if (r.length() < 2) {
            r = '0' + r;
        }
        if (g.length() < 2) {
            g = '0' + g;
        }
        if (b.length() < 2) {
            b = '0' + b;
        }
        return '#' + r + g + b;
    }

    public static final byte[] decodeBytes(String str) throws IOException {
        return MailUtils.decodeBytes(str);
    }

    public static final String encodeBytes(byte[] data) throws IOException {
        return MailUtils.encodeBytes(data);
    }

    public static final String encodeObject(Object o) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(bytes);
        stream.writeObject(o);
        stream.close();
        bytes.flush();
        return MailUtils.encodeBytes(bytes.toByteArray());
    }

    public static final String extractNumber(String in) {
        if (in == null) {
            return "0";
        }
        StringBuffer result = new StringBuffer();
        boolean seenDot = false;
        boolean seenMinus = false;
        boolean seenNumber = false;
        for (int i = 0; i < in.length(); ++i) {
            char c = in.charAt(i);
            if (c == '.') {
                if (seenDot) continue;
                seenDot = true;
                if (!seenNumber) {
                    result.append('0');
                }
                result.append('.');
                continue;
            }
            if (c == '-') {
                if (seenMinus) continue;
                seenMinus = true;
                result.append('-');
                continue;
            }
            if (c != '0' && (c < '1' || c > '9')) continue;
            seenNumber = true;
            result.append(c);
        }
        int length = result.length();
        if (length > 0 && result.charAt(length - 1) == '.') {
            result.deleteCharAt(length - 1);
        }
        return result.length() == 0 ? "0" : result.toString();
    }

    public static final Color hexToColor(String color) {
        try {
            if (color.charAt(0) == '#') {
                color = color.substring(1, 7);
            }
            int[] col = new int[3];
            for (int i = 0; i < 3; ++i) {
                col[i] = Integer.parseInt(color.substring(i * 2, i * 2 + 2), 16);
            }
            return new Color(col[0], col[1], col[2]);
        }
        catch (Exception e) {
            return Color.black;
        }
    }

    public static final String html(String s) {
        return TextUtils.htmlEncode(s, true);
    }

    public static final String htmlEncode(String s) {
        return TextUtils.htmlEncode(s, false);
    }

    public static final String htmlEncode(String s, boolean encodeSpecialChars) {
        s = TextUtils.noNull(s);
        StringBuilder str = new StringBuilder();
        for (int j = 0; j < s.length(); ++j) {
            char c = s.charAt(j);
            if (c < '\u0080') {
                switch (c) {
                    case '\"': {
                        str.append("&quot;");
                        break;
                    }
                    case '\'': {
                        str.append("&#39;");
                        break;
                    }
                    case '&': {
                        str.append("&amp;");
                        break;
                    }
                    case '<': {
                        str.append("&lt;");
                        break;
                    }
                    case '>': {
                        str.append("&gt;");
                        break;
                    }
                    default: {
                        str.append(c);
                        break;
                    }
                }
                continue;
            }
            if (encodeSpecialChars && c <= '\u00ff') {
                String hexChars = "0123456789ABCDEF";
                int a = c % 16;
                int b = (c - a) / 16;
                str.append("&#x").append(hexChars.charAt(b)).append(hexChars.charAt(a)).append(";");
                continue;
            }
            str.append(c);
        }
        return str.toString();
    }

    public static final String hyperlink(String text) {
        return TextUtils.hyperlink(text, null);
    }

    public static final String hyperlink(String text, String target) {
        text = TextUtils.noNull(text);
        StringBuffer sb = new StringBuffer((int)((double)text.length() * 1.1));
        sb.append(text);
        TextUtils.linkEmail(sb);
        TextUtils.linkURL(sb, target);
        return sb.toString();
    }

    public static final String indent(String string, int indentSize, boolean initialLine) {
        int i;
        String indent;
        if (indentSize == 0) {
            indent = "\t";
        } else {
            StringBuffer s = new StringBuffer();
            for (i = 0; i < indentSize; ++i) {
                s.append(' ');
            }
            indent = s.toString();
        }
        StringBuffer result = new StringBuffer();
        if (initialLine) {
            result.append(indent);
        }
        for (i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            result.append(c);
            if (c != '\n') continue;
            result.append(indent);
        }
        return result.toString();
    }

    public static final String innerTrim(String s) {
        int l;
        StringBuffer b = new StringBuffer(s);
        int index = 0;
        while (b.length() != 0 && b.charAt(0) == ' ') {
            b.deleteCharAt(0);
        }
        while (index < b.length()) {
            if (Character.isWhitespace(b.charAt(index)) && index + 1 < b.length() && Character.isWhitespace(b.charAt(index + 1))) {
                b.deleteCharAt(index + 1);
                --index;
            }
            ++index;
        }
        if (b.length() > 0 && b.charAt(l = b.length() - 1) == ' ') {
            b.setLength(l);
        }
        String result = b.toString();
        return result;
    }

    public static final String join(String glue, Iterator pieces) {
        StringBuffer s = new StringBuffer();
        while (pieces.hasNext()) {
            s.append(pieces.next().toString());
            if (!pieces.hasNext()) continue;
            s.append(glue);
        }
        return s.toString();
    }

    public static final String join(String glue, String[] pieces) {
        return TextUtils.join(glue, Arrays.asList(pieces).iterator());
    }

    public static final String join(String glue, Collection pieces) {
        return TextUtils.join(glue, pieces.iterator());
    }

    public static final String leadingSpaces(String s) {
        s = TextUtils.noNull(s);
        StringBuffer str = new StringBuffer();
        boolean justAfterLineBreak = true;
        for (int i = 0; i < s.length(); ++i) {
            if (justAfterLineBreak) {
                if (s.charAt(i) == ' ') {
                    str.append("&nbsp;");
                    continue;
                }
                if (s.charAt(i) == '\n') {
                    str.append(s.charAt(i));
                    continue;
                }
                str.append(s.charAt(i));
                justAfterLineBreak = false;
                continue;
            }
            if (s.charAt(i) == '\n') {
                justAfterLineBreak = true;
            }
            str.append(s.charAt(i));
        }
        return str.toString();
    }

    public static final String left(String s, int n) {
        if (n >= s.length()) {
            return s;
        }
        return s.substring(0, n);
    }

    public static final String linkEmail(String string) {
        StringBuffer str = new StringBuffer((int)((double)string.length() * 1.05));
        str.append(string);
        TextUtils.linkEmail(str);
        return str.toString();
    }

    public static final String linkURL(String str) {
        return TextUtils.linkURL(str, null);
    }

    public static final String linkURL(String str, String target) {
        StringBuffer sb = new StringBuffer((int)((double)str.length() * 1.05));
        sb.append(str);
        TextUtils.linkURL(sb, target);
        return sb.toString();
    }

    public static final String list(String str) {
        str = TextUtils.noNull(str);
        String strToRet = "";
        boolean inList = false;
        if (str.startsWith("-") || str.startsWith("*")) {
            str = '\n' + str;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '\n') {
                if (i != str.length() - 1) {
                    if (str.charAt(i + 1) == '-' || str.charAt(i + 1) == '*') {
                        if (!inList) {
                            strToRet = strToRet + "<ul>";
                            inList = true;
                        } else {
                            strToRet = strToRet + "</li>";
                        }
                        strToRet = strToRet + "<li>";
                        ++i;
                        continue;
                    }
                    if (inList) {
                        strToRet = strToRet + "</li></ul>";
                        inList = false;
                        continue;
                    }
                    strToRet = strToRet + str.charAt(i);
                    continue;
                }
                if (!inList) continue;
                strToRet = strToRet + "</li></ul>";
                continue;
            }
            strToRet = strToRet + str.charAt(i);
        }
        if (inList) {
            strToRet = strToRet + "</li></ul>";
        }
        return strToRet;
    }

    public static final String noNull(String string, String defaultString) {
        return TextUtils.stringSet(string) ? string : defaultString;
    }

    public static final String noNull(String string) {
        return TextUtils.noNull(string, "");
    }

    public static final boolean parseBoolean(String in) {
        if ((in = TextUtils.noNull(in)).length() == 0) {
            return false;
        }
        switch (in.charAt(0)) {
            case '1': 
            case 'T': 
            case 'Y': 
            case 't': 
            case 'y': {
                return true;
            }
        }
        return false;
    }

    public static final Date parseDate(String year, String month, String day) {
        year = TextUtils.noNull(year);
        month = TextUtils.noNull(month);
        day = TextUtils.noNull(day);
        int y = TextUtils.parseInt(year);
        int m = TextUtils.parseInt(month) - 1;
        int d = TextUtils.parseInt(TextUtils.extractNumber(day));
        if (m == -1) {
            String str;
            if (month.length() < 3) {
                month = month + "   ";
            }
            m = (str = month.toLowerCase().substring(0, 3)).equals("jan") ? 0 : (str.equals("feb") ? 1 : (str.equals("mar") ? 2 : (str.equals("apr") ? 3 : (str.equals("may") ? 4 : (str.equals("jun") ? 5 : (str.equals("jul") ? 6 : (str.equals("aug") ? 7 : (str.equals("sep") ? 8 : (str.equals("oct") ? 9 : (str.equals("nov") ? 10 : (str.equals("dec") ? 11 : 0)))))))))));
        }
        if (d == 0) {
            d = 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(y, m, d);
        return cal.getTime();
    }

    public static final double parseDouble(String in) {
        double d = 0.0;
        try {
            d = Double.parseDouble(in);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return d;
    }

    public static final float parseFloat(String in) {
        float f = 0.0f;
        try {
            f = Float.parseFloat(in);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return f;
    }

    public static final int parseInt(String in) {
        int i;
        try {
            i = Integer.parseInt(in);
        }
        catch (Exception e) {
            i = (int)TextUtils.parseFloat(in);
        }
        return i;
    }

    public static final long parseLong(String in) {
        long l;
        try {
            l = Long.parseLong(in);
        }
        catch (Exception e) {
            l = (long)TextUtils.parseDouble(in);
        }
        return l;
    }

    public static final String plainTextToHtml(String str) {
        return TextUtils.plainTextToHtml(str, null);
    }

    public static final String plainTextToHtml(String str, boolean encodeSpecialChars) {
        return TextUtils.plainTextToHtml(str, null, encodeSpecialChars);
    }

    public static final String plainTextToHtml(String str, String target) {
        return TextUtils.plainTextToHtml(str, target, true);
    }

    public static final String plainTextToHtml(String str, String target, boolean encodeSpecialChars) {
        str = TextUtils.noNull(str);
        str = TextUtils.htmlEncode(str, encodeSpecialChars);
        str = TextUtils.leadingSpaces(str);
        str = TextUtils.br(str);
        str = TextUtils.hyperlink(str, target);
        return str;
    }

    public static final String removeAndInsert(String str, int removeAndInsertStart, int removeEnd, String insertStr) {
        String partBefore = str.substring(0, removeAndInsertStart);
        String partAfter = str.substring(removeEnd);
        str = partBefore + insertStr + partAfter;
        return str;
    }

    public static final String slashes(String s) {
        s = TextUtils.noNull(s);
        StringBuffer str = new StringBuffer();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '\\' || chars[i] == '\"' || chars[i] == '\'') {
                str.append('\\');
            }
            str.append(chars[i]);
        }
        return str.toString();
    }

    public static final boolean stringSet(String string) {
        return string != null && !"".equals(string);
    }

    public static final String trimToEndingChar(String str, int len) {
        boolean inTag = false;
        boolean anyTags = false;
        String result = "";
        int goodChars = 0;
        int lastEndingCharPos = -1;
        if (str.length() < len) {
            return str;
        }
        char[] strA = str.toCharArray();
        for (int i = 0; i < strA.length; ++i) {
            if (strA[i] == '<' && !inTag) {
                anyTags = true;
                inTag = true;
            }
            if (strA[i] == '>' && inTag) {
                inTag = false;
            }
            if (!inTag) {
                if (TextUtils.isEndingChar(strA[i])) {
                    lastEndingCharPos = i;
                }
                ++goodChars;
            }
            result = result + strA[i];
            if (goodChars == len) break;
        }
        if (lastEndingCharPos + 1 != result.length()) {
            if (lastEndingCharPos != -1) {
                result = result.substring(0, lastEndingCharPos + 1);
            } else {
                int spacePos = result.lastIndexOf(32);
                if (spacePos != -1) {
                    result = result.substring(0, spacePos);
                }
            }
        }
        if (anyTags) {
            return TextUtils.closeTags(result);
        }
        return result;
    }

    public static final boolean verifyEmail(String email) {
        return MailUtils.verifyEmail(email);
    }

    public static final boolean verifyUrl(String url) {
        if (url == null) {
            return false;
        }
        if (url.startsWith("https://")) {
            url = "http://" + url.substring(8);
        }
        try {
            new URL(url);
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    public static final String wrapParagraph(String s) {
        s = TextUtils.noNull(s);
        StringBuffer result = new StringBuffer();
        result.append("<p>");
        int lastC = 0;
        for (int i = 0; i < s.length(); ++i) {
            char thisC = s.charAt(i);
            if (thisC == '\n' && lastC == 10) {
                result.append("</p>\n\n<p>");
            } else {
                result.append(thisC);
            }
            lastC = thisC;
        }
        result.append("</p>");
        return result.toString();
    }

    private static final boolean isEndingChar(char c) {
        return c == '.' || c == '!' || c == ',' || c == '?';
    }

    private static final int getStartUrl(StringBuffer str, int startIndex) {
        int schemeIndex = TextUtils.getSchemeIndex(str, startIndex);
        int wwwIndex = str.indexOf("www.", startIndex + 1);
        if (schemeIndex == -1 && wwwIndex == -1) {
            return -1;
        }
        if (schemeIndex == -1) {
            return wwwIndex;
        }
        if (wwwIndex == -1) {
            return schemeIndex;
        }
        return Math.min(schemeIndex, wwwIndex);
    }

    private static final void linkEmail(StringBuffer str) {
        int atIndex;
        int lastEndIndex = -1;
        block0: while ((atIndex = str.indexOf("@", lastEndIndex + 1)) != -1) {
            char lastChar;
            String partBeforeAt = "";
            int linkStartIndex = atIndex;
            boolean reachedStart = false;
            while (!reachedStart) {
                if (--linkStartIndex < 0) {
                    reachedStart = true;
                    continue;
                }
                char c = str.charAt(linkStartIndex);
                if (c == '?' || c == '&' || c == '=' || c == '/' || c == '%') {
                    lastEndIndex = atIndex + 1;
                    continue block0;
                }
                if (UrlUtils.isValidEmailChar(c)) {
                    partBeforeAt = c + partBeforeAt;
                    continue;
                }
                reachedStart = true;
            }
            ++linkStartIndex;
            String partAfterAt = "";
            int linkEndIndex = atIndex;
            boolean reachedEnd = false;
            while (!reachedEnd) {
                if (++linkEndIndex == str.length()) {
                    reachedEnd = true;
                    continue;
                }
                char c = str.charAt(linkEndIndex);
                if (UrlUtils.isValidEmailChar(c)) {
                    partAfterAt = partAfterAt + c;
                    continue;
                }
                reachedEnd = true;
            }
            --linkEndIndex;
            String emailStr = partBeforeAt + '@' + partAfterAt;
            while ((lastChar = emailStr.charAt(emailStr.length() - 1)) == '.' || lastChar == ':' || lastChar == '-' || lastChar == '/' || lastChar == '~') {
                emailStr = emailStr.substring(0, emailStr.length() - 1);
                --linkEndIndex;
            }
            if (TextUtils.verifyEmail(emailStr)) {
                String emailLink = "<a href='mailto:" + emailStr + "'>" + emailStr + "</a>";
                str.replace(linkStartIndex, linkEndIndex + 1, emailLink);
                lastEndIndex = linkStartIndex - 1 + emailLink.length();
                continue;
            }
            lastEndIndex = linkStartIndex - 1 + emailStr.length();
        }
    }

    private static final void linkURL(StringBuffer str, String target) {
        int linkStartIndex;
        String targetString;
        int lastEndIndex = -1;
        String string = targetString = target == null || target.trim().length() == 0 ? "" : " target=\"" + target.trim() + '\"';
        while ((linkStartIndex = TextUtils.getStartUrl(str, lastEndIndex)) != -1) {
            char lastChar;
            String suffix;
            String prefix;
            int linkEndIndex = linkStartIndex;
            String urlStr = "";
            do {
                if (str.charAt(linkEndIndex) == '&') {
                    if (linkEndIndex + 6 <= str.length() && "&quot;".equals(str.substring(linkEndIndex, linkEndIndex + 6))) break;
                    if (linkEndIndex + 5 <= str.length() && "&amp;".equals(str.substring(linkEndIndex, linkEndIndex + 5))) {
                        str.replace(linkEndIndex, linkEndIndex + 5, "&");
                    }
                }
                if (!UrlUtils.isValidURLChar(str.charAt(linkEndIndex))) break;
                urlStr = urlStr + str.charAt(linkEndIndex);
            } while (++linkEndIndex != str.length());
            if (linkStartIndex >= 6 && "href=\"".equals(prefix = str.substring(linkStartIndex - 6, linkStartIndex))) {
                lastEndIndex = linkEndIndex;
                continue;
            }
            if (str.length() >= linkEndIndex + 4 && "</a>".equals(suffix = str.substring(linkEndIndex, linkEndIndex + 4))) {
                lastEndIndex = linkEndIndex + 4;
                continue;
            }
            --linkEndIndex;
            while ((lastChar = urlStr.charAt(urlStr.length() - 1)) == '.') {
                urlStr = urlStr.substring(0, urlStr.length() - 1);
                --linkEndIndex;
            }
            lastChar = urlStr.charAt(urlStr.length() - 1);
            if (lastChar == ')') {
                if (linkStartIndex > 0 && '(' == str.charAt(linkStartIndex - 1)) {
                    urlStr = urlStr.substring(0, urlStr.length() - 1);
                    --linkEndIndex;
                }
            } else if (lastChar == '\'') {
                if (linkStartIndex > 0 && '\'' == str.charAt(linkStartIndex - 1)) {
                    urlStr = urlStr.substring(0, urlStr.length() - 1);
                    --linkEndIndex;
                }
            } else if (lastChar == ';') {
                String endingStr;
                if (urlStr.length() > 6 && "&quot;".equalsIgnoreCase(urlStr.substring(urlStr.length() - 6))) {
                    urlStr = urlStr.substring(0, urlStr.length() - 6);
                    linkEndIndex -= 6;
                } else if (urlStr.length() > 4 && ("&lt;".equalsIgnoreCase(endingStr = urlStr.substring(urlStr.length() - 4)) || "&gt;".equalsIgnoreCase(endingStr))) {
                    urlStr = urlStr.substring(0, urlStr.length() - 4);
                    linkEndIndex -= 4;
                }
            }
            String urlToDisplay = TextUtils.htmlEncode(urlStr);
            if (urlStr.toLowerCase().startsWith("www.")) {
                urlStr = "http://" + urlStr;
            }
            if (UrlUtils.verifyHierachicalURI(urlStr, new String[]{"javascript"})) {
                String urlLink = "<a href=\"" + urlStr + '\"' + targetString + '>' + urlToDisplay + "</a>";
                str.replace(linkStartIndex, linkEndIndex + 1, urlLink);
                lastEndIndex = linkStartIndex - 1 + urlLink.length();
                continue;
            }
            lastEndIndex = linkStartIndex - 1 + urlStr.length();
        }
    }

    private static int getSchemeIndex(StringBuffer str, int startIndex) {
        char currentChar;
        int schemeStart;
        int schemeIndex = str.indexOf("://", startIndex + 1);
        if (schemeIndex <= 0) {
            return -1;
        }
        for (schemeStart = schemeIndex - 1; schemeStart >= 0 && UrlUtils.isValidSchemeChar(currentChar = str.charAt(schemeStart)); --schemeStart) {
        }
        return ++schemeStart;
    }
}

