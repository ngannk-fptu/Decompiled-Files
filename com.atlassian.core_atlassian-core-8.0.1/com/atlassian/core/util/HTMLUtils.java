/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import java.util.ArrayList;
import java.util.List;

public class HTMLUtils {
    private int currentIndex;

    public static String stripTags(String html) {
        StringBuilder detagged = new StringBuilder();
        boolean intag = false;
        for (int count = 0; count < html.length(); ++count) {
            char current = html.charAt(count);
            if (current == '>') {
                intag = false;
                continue;
            }
            if (current == '<') {
                intag = true;
                continue;
            }
            if (intag) continue;
            detagged.append(current);
        }
        return detagged.toString();
    }

    public static String stripOuterHtmlTags(String html) {
        ArrayList<String[]> tags = new ArrayList<String[]>();
        tags.add(new String[]{"html", "true", "0"});
        tags.add(new String[]{"head", "false", "0"});
        tags.add(new String[]{"body", "true", "0"});
        String result = HTMLUtils.stripOuterTags(html, tags, 0);
        return result.trim();
    }

    private static String stripOuterTags(String html, String tag, boolean inclusive) {
        ArrayList<String[]> tags = new ArrayList<String[]>();
        tags.add(new String[]{tag, new Boolean(inclusive).toString(), "0"});
        return HTMLUtils.stripOuterTags(html, tags, 0);
    }

    private static String stripOuterTags(String html, List<String[]> tagIncs, int listValue) {
        String[] tagInc = tagIncs.get(listValue);
        String tag = tagInc[0];
        boolean inclusive = new Boolean(tagInc[1]);
        int initialCount = new Integer(tagInc[2]);
        String[] previousInc = null;
        if (listValue != 0) {
            previousInc = tagIncs.get(listValue - 1);
        }
        String[] nextInc = null;
        if (listValue < tagIncs.size() - 1) {
            nextInc = tagIncs.get(listValue + 1);
        }
        StringBuilder detagged = new StringBuilder();
        boolean tagValue = false;
        for (int count = initialCount; count < html.length(); ++count) {
            int newCounter;
            char current = html.charAt(count);
            if (tagValue) {
                if (current == '<') {
                    newCounter = HTMLUtils.foundTag(html, count, tag, false);
                    if (newCounter != -1) {
                        tagValue = false;
                        count = newCounter;
                        if (previousInc != null) {
                            previousInc[2] = new Integer(newCounter).toString();
                            tagIncs.add(listValue - 1, previousInc);
                            tagIncs.remove(listValue);
                        }
                        if (inclusive) {
                            return detagged.toString();
                        }
                        tagInc = tagIncs.get(++listValue);
                        tag = tagInc[0];
                        inclusive = new Boolean(tagInc[1]);
                        if (listValue < tagIncs.size() - 1) {
                            nextInc = tagIncs.get(listValue + 1);
                            continue;
                        }
                        nextInc = null;
                        continue;
                    }
                    if (!inclusive) continue;
                    detagged.append(current);
                    continue;
                }
                if (!inclusive) continue;
                detagged.append(current);
                continue;
            }
            if (current == '<') {
                newCounter = HTMLUtils.foundTag(html, count, tag, true);
                if (newCounter != -1) {
                    tagValue = true;
                    count = newCounter;
                    if (nextInc == null || !inclusive) continue;
                    nextInc[2] = new Integer(count).toString();
                    tagIncs.remove(listValue + 1);
                    tagIncs.add(listValue + 1, nextInc);
                    detagged.append(HTMLUtils.stripOuterTags(html, tagIncs, listValue + 1));
                    String[] tempTagIncs = tagIncs.get(listValue);
                    count = new Integer(tempTagIncs[2]);
                    continue;
                }
                detagged.append(current);
                continue;
            }
            detagged.append(current);
        }
        return detagged.toString();
    }

    private static int foundTag(String html, int count, String tag, boolean opening) {
        String tagToFind = tag;
        if (!opening) {
            tagToFind = "/" + tagToFind;
        }
        int htmlCounter = count;
        int htmlFound = 0;
        boolean inQuotes = false;
        for (htmlCounter = count; htmlCounter < html.length(); ++htmlCounter) {
            char current2 = html.charAt(htmlCounter);
            if (current2 == '\"') {
                inQuotes = !inQuotes;
                htmlFound = 0;
                continue;
            }
            if (inQuotes) continue;
            if (current2 == '>') break;
            if (htmlFound == tagToFind.length()) continue;
            if (tagToFind.toLowerCase().charAt(htmlFound) == current2 || tagToFind.toUpperCase().charAt(htmlFound) == current2) {
                ++htmlFound;
                continue;
            }
            htmlFound = 0;
        }
        if (htmlFound == tagToFind.length()) {
            return htmlCounter + 1;
        }
        return -1;
    }
}

