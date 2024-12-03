/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HTMLSearchableTextUtil
 *  com.google.common.collect.ImmutableSortedSet
 */
package com.atlassian.confluence.plugins.tasklist.service.util;

import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

public class TaskContentUtils {
    private static final Pattern BLANK_PATTERN = Pattern.compile("[\u00a0\\s]*");
    private static final Pattern TAGS_PATTERN = Pattern.compile("<([^\\s/>]+)");
    private static final Set<String> WHITE_TAGS = ImmutableSortedSet.of((Comparable)((Object)"br"), (Comparable)((Object)"h1"), (Comparable)((Object)"h2"), (Comparable)((Object)"h3"), (Comparable)((Object)"h4"), (Comparable)((Object)"h5"), (Comparable[])new String[]{"h6", "code", "div", "em", "p", "pre", "s", "span", "strong", "sub", "sup", "u", "ac:placeholder"});
    private static final String[] IGNORED_TAGS_LIST = new String[]{"placeholder"};

    public static boolean isBlankContent(String content) {
        if (content == null) {
            return true;
        }
        Matcher matcher = TAGS_PATTERN.matcher(content);
        while (matcher.find()) {
            String tag = matcher.group(1);
            if (TaskContentUtils.isWhiteTag(tag)) continue;
            return false;
        }
        try {
            String strippedTagsContent = HTMLSearchableTextUtil.stripTags((String)content, (String[])IGNORED_TAGS_LIST);
            return BLANK_PATTERN.matcher(strippedTagsContent).matches();
        }
        catch (SAXException e) {
            return false;
        }
    }

    private static boolean isWhiteTag(String tagName) {
        return WHITE_TAGS.contains(tagName);
    }
}

