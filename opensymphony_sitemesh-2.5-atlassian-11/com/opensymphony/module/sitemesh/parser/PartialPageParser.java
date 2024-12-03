/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.parser.PartialPageParserHtmlPage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PartialPageParser
implements PageParser {
    public Page parse(char[] buffer) throws IOException {
        return this.parse(new DefaultSitemeshBuffer(buffer));
    }

    public Page parse(SitemeshBuffer buffer) throws IOException {
        char[] data = buffer.getCharArray();
        int length = buffer.getBufferLength();
        int position = 0;
        while (position < data.length) {
            if (data[position++] != '<' || position < data.length && data[position] == '!') continue;
            if (PartialPageParser.compareLowerCase(data, length, position, "html")) {
                return this.parseHtmlPage(buffer, position);
            }
            return new PartialPageParserHtmlPage(buffer);
        }
        return new PartialPageParserHtmlPage(buffer);
    }

    private Page parseHtmlPage(SitemeshBuffer buffer, int position) {
        char[] data = buffer.getCharArray();
        int length = buffer.getBufferLength();
        int bodyStart = -1;
        int bodyLength = -1;
        int headStart = -1;
        int headLength = -1;
        Map<String, String> bodyProperties = null;
        while (position < length) {
            if (data[position++] != '<') continue;
            if (PartialPageParser.compareLowerCase(data, length, position, "head")) {
                headStart = position = PartialPageParser.findEndOf(data, length, position + 4, ">");
                position = this.findEndTag(position, data, length, "head");
                headLength = position - headStart;
                position += 7;
                continue;
            }
            if (!PartialPageParser.compareLowerCase(data, length, position, "body")) continue;
            HashSimpleMap map = new HashSimpleMap();
            bodyStart = PartialPageParser.parseProperties(data, length, position + 4, map);
            bodyProperties = map.getMap();
            break;
        }
        if (bodyStart < 0) {
            bodyStart = length;
            bodyLength = 0;
        } else {
            for (int i = length - 8; i > bodyStart; --i) {
                if (!PartialPageParser.compareLowerCase(data, length, i, "</body>")) continue;
                bodyLength = i - bodyStart;
                break;
            }
            if (bodyLength == -1) {
                bodyLength = length - bodyStart;
            }
        }
        if (headLength > 0) {
            int idx = headStart;
            int headEnd = headStart + headLength;
            String title = null;
            TreeMap<Integer, Integer> deletions = new TreeMap<Integer, Integer>();
            HashMap<String, String> metaAttributes = new HashMap<String, String>();
            while (idx < headEnd) {
                if (data[idx++] != '<' || !PartialPageParser.compareLowerCase(data, headEnd, idx, "meta")) continue;
                MetaTagSimpleMap map = new MetaTagSimpleMap();
                idx = PartialPageParser.parseProperties(data, headEnd, idx + 4, map);
                if (map.getName() == null || map.getContent() == null) continue;
                metaAttributes.put(map.getName(), map.getContent());
            }
            HashMap<String, String> pageProperties = new HashMap<String, String>();
            for (int i = headStart; i < headEnd; ++i) {
                char c = data[i];
                if (c != '<') continue;
                if (PartialPageParser.compareLowerCase(data, headEnd, i + 1, "title")) {
                    int titleStart = PartialPageParser.findEndOf(data, headEnd, i + 6, ">");
                    int titleEnd = PartialPageParser.findStartOf(data, headEnd, titleStart, "<");
                    title = new String(data, titleStart, titleEnd - titleStart);
                    int titleTagEnd = titleEnd + "</title>".length();
                    deletions.put(i, titleTagEnd - i);
                    i = titleTagEnd - 1;
                    continue;
                }
                if (!PartialPageParser.compareLowerCase(data, headEnd, i + 1, "content")) continue;
                ContentTagSimpleMap map = new ContentTagSimpleMap();
                int contentStart = PartialPageParser.parseProperties(data, headEnd, i + 8, map);
                int contentEnd = PartialPageParser.findStartOf(data, headEnd, contentStart, "</content>");
                pageProperties.put(map.getTag(), new String(data, contentStart, contentEnd - contentStart));
                int contentTagEnd = contentEnd + "</content>".length();
                deletions.put(i, contentTagEnd - i);
                i = contentTagEnd - 1;
            }
            return new PartialPageParserHtmlPage(buffer, new SitemeshBufferFragment(buffer, bodyStart, bodyLength), bodyProperties, new SitemeshBufferFragment(buffer, headStart, headEnd - headStart, deletions), title, metaAttributes, pageProperties);
        }
        return new PartialPageParserHtmlPage(buffer, new SitemeshBufferFragment(buffer, bodyStart, bodyLength), bodyProperties);
    }

    private int findEndTag(int position, char[] data, int dataEnd, String tagName) {
        String endTag = "</" + tagName + ">";
        int remainingTagLength = endTag.length() - 1;
        for (int i = position; i < dataEnd - remainingTagLength; ++i) {
            if (data[i] != '<' || data[i + remainingTagLength] != '>') continue;
            if (data[i + 1] == '/' && PartialPageParser.compareLowerCase(data, dataEnd, i, endTag)) {
                return i;
            }
            i += remainingTagLength;
        }
        return dataEnd;
    }

    private static boolean compareLowerCase(char[] data, int dataEnd, int position, String token) {
        int l = position + token.length();
        if (l > dataEnd) {
            return false;
        }
        for (int i = 0; i < token.length(); ++i) {
            char potential = data[position + i];
            char needed = token.charAt(i);
            if (!(PartialPageParser.isUpperCaseAscii(potential) ? (potential | 0x20) != needed : potential != needed)) continue;
            return false;
        }
        return true;
    }

    private static boolean isUpperCaseAscii(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static int findEndOf(char[] data, int dataEnd, int position, String token) {
        for (int i = position; i < dataEnd - token.length(); ++i) {
            if (!PartialPageParser.compareLowerCase(data, dataEnd, i, token)) continue;
            return i + token.length();
        }
        return dataEnd;
    }

    private static int findStartOf(char[] data, int dataEnd, int position, String token) {
        for (int i = position; i < dataEnd - token.length(); ++i) {
            if (!PartialPageParser.compareLowerCase(data, dataEnd, i, token)) continue;
            return i;
        }
        return dataEnd;
    }

    private static int parseProperties(char[] data, int dataEnd, int position, SimpleMap map) {
        int idx = position;
        while (idx < dataEnd) {
            int endValue;
            while (idx < dataEnd && Character.isWhitespace(data[idx])) {
                ++idx;
            }
            if (idx == dataEnd || data[idx] == '>' || data[idx] == '/') break;
            int startAttr = idx;
            while (idx < dataEnd && !Character.isWhitespace(data[idx]) && data[idx] != '=' && data[idx] != '>') {
                ++idx;
            }
            if (idx == dataEnd || data[idx] != '=') continue;
            String attrName = new String(data, startAttr, idx - startAttr);
            if (++idx == dataEnd) break;
            int startValue = idx;
            if (data[idx] == '\"') {
                startValue = ++idx;
                while (idx < dataEnd && data[idx] != '\"') {
                    ++idx;
                }
                if (idx == dataEnd) break;
                endValue = idx++;
            } else if (data[idx] == '\'') {
                startValue = ++idx;
                while (idx < dataEnd && data[idx] != '\'') {
                    ++idx;
                }
                if (idx == dataEnd) break;
                endValue = idx++;
            } else {
                while (idx < dataEnd && !Character.isWhitespace(data[idx]) && data[idx] != '/' && data[idx] != '>') {
                    ++idx;
                }
                endValue = idx;
            }
            String attrValue = new String(data, startValue, endValue - startValue);
            map.put(attrName, attrValue);
        }
        while (idx < dataEnd && data[idx] != '>') {
            ++idx;
        }
        if (idx == dataEnd) {
            return idx;
        }
        return idx + 1;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class HashSimpleMap
    implements SimpleMap {
        private final Map<String, String> map = new HashMap<String, String>();

        @Override
        public void put(String key, String value) {
            this.map.put(key, value);
        }

        public Map<String, String> getMap() {
            return this.map;
        }
    }

    public static class ContentTagSimpleMap
    implements SimpleMap {
        private String tag;

        public void put(String key, String value) {
            if (key.equals("tag")) {
                this.tag = value;
            }
        }

        public String getTag() {
            return this.tag;
        }
    }

    public static class MetaTagSimpleMap
    implements SimpleMap {
        private String name;
        private String content;

        public void put(String key, String value) {
            if (key.equals("name")) {
                this.name = value;
            } else if (key.equals("content")) {
                this.content = value;
            }
        }

        public String getName() {
            return this.name;
        }

        public String getContent() {
            return this.content;
        }
    }

    public static interface SimpleMap {
        public void put(String var1, String var2);
    }
}

