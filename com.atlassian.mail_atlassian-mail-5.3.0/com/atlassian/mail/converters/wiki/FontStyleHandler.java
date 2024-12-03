/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 */
package com.atlassian.mail.converters.wiki;

import com.atlassian.mail.converters.wiki.BlockStyleHandler;
import com.atlassian.mail.converters.wiki.DocumentUtilities;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

@ParametersAreNonnullByDefault
final class FontStyleHandler {
    public static final String HTML_B = "b";
    public static final String HTML_STRONG = "strong";
    public static final String HTML_I = "i";
    public static final String HTML_EM = "em";
    public static final String HTML_U = "u";
    public static final String HTML_INS = "ins";
    public static final String HTML_STRIKE = "strike";
    public static final String HTML_DEL = "del";
    public static final String HTML_S = "s";
    public static final String HTML_Q = "q";
    public static final String HTML_CITE = "cite";
    private static final String NEWLINE = "\n";
    private static final Map<String, String> styleMap = ImmutableMap.builder().put((Object)"b", (Object)"*").put((Object)"strong", (Object)"*").put((Object)"i", (Object)"_").put((Object)"em", (Object)"_").put((Object)"u", (Object)"+").put((Object)"ins", (Object)"+").put((Object)"strike", (Object)"-").put((Object)"del", (Object)"-").put((Object)"s", (Object)"-").put((Object)"q", (Object)"\"").put((Object)"cite", (Object)"??").build();
    private static final Comparator<Map.Entry<String, Integer>> COMPARATOR = new Comparator<Map.Entry<String, Integer>>(){

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            int y;
            int x = o1 == null ? 0 : o1.getValue();
            int n = y = o2 == null ? 0 : o2.getValue();
            return x < y ? -1 : (x == y ? 0 : 1);
        }
    };
    private final Map<String, Integer> depthMap = Maps.newHashMap();
    private final Map<String, Boolean> reportedStartMap = Maps.newHashMap();
    private final BlockStyleHandler blockStyleHandler;

    public FontStyleHandler(BlockStyleHandler blockStyleHandler) {
        this.blockStyleHandler = blockStyleHandler;
    }

    public String enter(Node node, String name, int depth) {
        String val;
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        String wiki = styleMap.get(name);
        if (StringUtils.isBlank((CharSequence)wiki)) {
            return "";
        }
        if (this.depthMap.containsKey(wiki)) {
            val = "";
        } else {
            boolean hasText = false;
            if (node instanceof Element) {
                Element element = (Element)node;
                hasText = element.hasText();
            }
            if (hasText) {
                val = wiki;
                this.depthMap.put(wiki, depth);
                this.reportedStartMap.put(wiki, false);
            } else {
                val = "";
            }
        }
        return val;
    }

    public String exit(String name, int depth) {
        String val;
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        String wiki = styleMap.get(name);
        if (StringUtils.isBlank((CharSequence)wiki)) {
            return "";
        }
        Integer startDepth = this.depthMap.get(wiki);
        if (startDepth == null) {
            return "";
        }
        if (startDepth != depth) {
            val = "";
        } else {
            val = wiki;
            this.depthMap.remove(wiki);
            this.reportedStartMap.remove(wiki);
        }
        return val;
    }

    public boolean isAnyAtStart(int depth) {
        Boolean val = false;
        for (Map.Entry<String, Integer> element : this.depthMap.entrySet()) {
            Boolean temp;
            String key = element.getKey();
            Integer value = element.getValue();
            if (value == null || depth < value || (temp = this.reportedStartMap.get(key)) == null || temp.booleanValue()) continue;
            this.reportedStartMap.put(key, true);
            val = true;
        }
        return val;
    }

    public void resetAllCurrentStartValues() {
        for (Map.Entry<String, Boolean> entry : this.reportedStartMap.entrySet()) {
            entry.setValue(false);
        }
    }

    public boolean isPrecededByStyle(String html) {
        if (StringUtils.isBlank((CharSequence)html)) {
            return false;
        }
        for (String wiki : styleMap.values()) {
            if (!StringUtils.endsWith((CharSequence)html, (CharSequence)wiki)) continue;
            return true;
        }
        return false;
    }

    public void wrapStylesAround(StringBuilder accum, String text) {
        if (this.depthMap.isEmpty() || !StringUtils.endsWith((CharSequence)text, (CharSequence)NEWLINE)) {
            accum.append(text);
            return;
        }
        this.stopStylings(accum);
        accum.append(text);
        this.startStylings(accum);
    }

    public void stopStylings(StringBuilder accum) {
        String currentStyles = this.getCurrentStyles(false);
        if (StringUtils.isNotBlank((CharSequence)currentStyles)) {
            String removedWhitespace = DocumentUtilities.removeTrailingWhitespace(accum);
            while (accum.length() > 0 && currentStyles.length() > 0 && accum.charAt(accum.length() - 1) == currentStyles.charAt(0)) {
                accum.deleteCharAt(accum.length() - 1);
                currentStyles = StringUtils.substring((String)currentStyles, (int)1);
                removedWhitespace = DocumentUtilities.removeTrailingWhitespace(accum) + removedWhitespace;
            }
            accum.append(currentStyles);
            accum.append(removedWhitespace);
        }
    }

    public void startStylings(StringBuilder sb) {
        String currentStyles = this.getCurrentStyles(true);
        if (StringUtils.isNotBlank((CharSequence)currentStyles)) {
            sb.append(" ");
            sb.append(currentStyles);
            this.resetAllCurrentStartValues();
        }
    }

    private String getCurrentStyles(boolean start) {
        ArrayList entries = Lists.newArrayList(this.depthMap.entrySet());
        Collections.sort(entries, COMPARATOR);
        if (!start) {
            Collections.reverse(entries);
        }
        return StringUtils.join((Iterable)Lists.transform((List)entries, (Function)new Function<Map.Entry<String, Integer>, String>(){

            @Nullable
            public String apply(@Nullable Map.Entry<String, Integer> input) {
                if (input != null) {
                    return input.getKey();
                }
                return "";
            }
        }), (String)"");
    }
}

