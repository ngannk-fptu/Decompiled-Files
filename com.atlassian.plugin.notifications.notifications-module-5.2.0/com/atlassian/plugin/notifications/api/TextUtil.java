/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.api;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class TextUtil {
    public String truncate(String text) {
        return this.truncate(text, 50, true, true);
    }

    public String truncate(String text, int length, boolean removeNewLines, boolean addEllipsis) {
        if (StringUtils.isBlank((CharSequence)text)) {
            return "";
        }
        String newText = removeNewLines ? this.removeNewLines(text).trim() : text.trim();
        if (newText.length() <= length) {
            return removeNewLines ? this.removeNewLines(newText) : newText;
        }
        if (addEllipsis && newText.length() <= length + 3) {
            return removeNewLines ? this.removeNewLines(newText) : newText;
        }
        if (addEllipsis) {
            return newText.substring(0, length - 3).trim() + "...";
        }
        return newText.substring(0, length).trim();
    }

    private String removeNewLines(String shortText) {
        return shortText.replaceAll("\\s*[\\n|\\r]+\\s*", " ");
    }

    public static String replaceMacroKeys(String text, Map<String, Object> context, MacroKeyReplacer replacer) {
        int endPos;
        if (StringUtils.isBlank((CharSequence)text)) {
            return "";
        }
        String newString = text;
        int pos = newString.indexOf("{");
        while (pos > -1 && (endPos = newString.indexOf("}")) != -1 && endPos > pos) {
            String macro = newString.substring(pos + 1, endPos);
            newString = newString.substring(0, pos) + replacer.replace(macro, context) + newString.substring(endPos + 1);
            pos = newString.indexOf("{");
        }
        return newString;
    }

    public static interface MacroKeyReplacer {
        public String replace(String var1, Map<String, Object> var2);
    }
}

