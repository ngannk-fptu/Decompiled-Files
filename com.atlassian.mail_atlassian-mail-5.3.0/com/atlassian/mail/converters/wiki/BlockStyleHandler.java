/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultiset
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multiset
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.internal.StringUtil
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 */
package com.atlassian.mail.converters.wiki;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

@ParametersAreNonnullByDefault
final class BlockStyleHandler {
    private static final String NEWLINE = "\n";
    public static final String HTML_BLOCKQUOTE = "blockquote";
    public static final String HTML_PRE = "pre";
    public static final String HTML_CODE = "code";
    private static final Map<String, String> styleMap = ImmutableMap.builder().put((Object)"blockquote", (Object)"{quote}").put((Object)"pre", (Object)"{noformat}").put((Object)"code", (Object)"{code}").build();
    private final Map<String, Integer> depthMap = Maps.newHashMap();
    private final Multiset<String> depthCounter = HashMultiset.create();

    BlockStyleHandler() {
    }

    public String enter(Node node, String name, int depth) {
        Element element;
        Element firstChild;
        String val = styleMap.get(name);
        if (StringUtils.isBlank((CharSequence)val)) {
            return "";
        }
        int currentDepth = this.getCurrentDepth(val);
        if (HTML_PRE.equals(name) && node instanceof Element && (firstChild = (element = (Element)node).children().first()) != null && HTML_CODE.equals(firstChild.nodeName())) {
            return "";
        }
        if (StringUtil.in((String)name, (String[])new String[]{HTML_PRE, HTML_CODE, HTML_BLOCKQUOTE})) {
            if (currentDepth >= 0) {
                if (currentDepth == depth) {
                    this.depthCounter.add((Object)val);
                }
                val = NEWLINE;
            } else {
                this.depthMap.put(val, depth);
                this.depthCounter.add((Object)val);
                val = NEWLINE + val;
            }
        }
        return val;
    }

    public String exit(String name, int depth) {
        String val = styleMap.get(name);
        if (StringUtils.isBlank((CharSequence)val)) {
            return "";
        }
        int currentDepth = this.getCurrentDepth(val);
        if (StringUtil.in((String)name, (String[])new String[]{HTML_PRE, HTML_CODE, HTML_BLOCKQUOTE})) {
            if (currentDepth == depth) {
                this.depthCounter.remove((Object)val);
                if (this.depthCounter.count((Object)val) == 0) {
                    this.depthMap.remove(val);
                    val = val + NEWLINE;
                } else {
                    val = NEWLINE;
                }
            } else {
                val = NEWLINE;
            }
        }
        return val;
    }

    public boolean isFormattingPossible() {
        int currentPreDepth = this.getCurrentDepth(styleMap.get(HTML_PRE));
        int currentCodeDepth = this.getCurrentDepth(styleMap.get(HTML_CODE));
        return currentPreDepth == -1 && currentCodeDepth == -1;
    }

    private int getCurrentDepth(String wikiVal) {
        if (StringUtils.isBlank((CharSequence)wikiVal)) {
            return -1;
        }
        return this.depthMap.containsKey(wikiVal) ? this.depthMap.get(wikiVal) : -1;
    }
}

