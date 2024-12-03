/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.dynamictasklist2.util;

import com.atlassian.confluence.extra.dynamictasklist2.util.Base32;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;

public final class TaskListUtil {
    public static String sanitizeTaskName(String name) {
        char[] chars;
        StringBuilder sb = new StringBuilder(name.length());
        for (char c : chars = name.trim().toCharArray()) {
            if (c == '|' || c == '\\') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String desanitizeTaskName(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        char[] chars = name.trim().toCharArray();
        boolean escaped = false;
        for (char c : chars) {
            if (c == '|' && escaped) {
                sb.append(c);
                escaped = false;
                continue;
            }
            if (c == '\\') {
                if (escaped) {
                    sb.append(c);
                }
                escaped = !escaped;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static int incrementOccurrenceInPageContext(String listName, PageContext pageContext) {
        String encodedListName;
        try {
            encodedListName = Base32.encode(StringUtils.defaultString((String)listName).getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String occurrenceParam = "dynamictasklist.occurance." + encodedListName;
        int occurrence = 1;
        if (pageContext.getParam((Object)occurrenceParam) != null) {
            occurrence = (Integer)pageContext.getParam((Object)occurrenceParam) + 1;
        }
        pageContext.addParam((Object)occurrenceParam, (Object)occurrence);
        return occurrence;
    }

    @Deprecated
    public static int incrementOccuranceInPageContext(String listName, PageContext pageContext) {
        return TaskListUtil.incrementOccurrenceInPageContext(listName, pageContext);
    }

    public static String getTaskListName(MacroDefinition macroDefinition) {
        String result = (String)macroDefinition.getParameters().get("title");
        if (result == null) {
            result = macroDefinition.getDefaultParameterValue();
        }
        return StringUtils.defaultString((String)result);
    }

    public static boolean isAdgEnabled() {
        return true;
    }
}

