/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.internal.StringUtil
 */
package com.atlassian.mail.converters.wiki;

import com.atlassian.mail.converters.wiki.BlockStyleHandler;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

@ParametersAreNonnullByDefault
final class ListHandler {
    public static final String HTML_OL = "ol";
    public static final String HTML_UL = "ul";
    public static final String HTML_LI = "li";
    public static final String HTML_DL = "dl";
    public static final String HTML_DT = "dt";
    public static final String HTML_DD = "dd";
    private static final String WIKI_OL_LI = "#";
    private static final String WIKI_UL_LI = "*";
    private static final String WIKI_DT = "";
    private static final String WIKI_DD = ".... ";
    private static final String NEWLINE = "\n";
    private final BlockStyleHandler blockStyleHandler;
    private boolean inDescription;
    private final Deque<String> listWikiStack = new ArrayDeque<String>();

    public ListHandler(BlockStyleHandler blockStyleHandler) {
        this.blockStyleHandler = blockStyleHandler;
    }

    public String enter(String name) {
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return WIKI_DT;
        }
        if (HTML_OL.equals(name)) {
            this.listWikiStack.addFirst(WIKI_OL_LI);
            return NEWLINE;
        }
        if (HTML_UL.equals(name)) {
            this.listWikiStack.addFirst(WIKI_UL_LI);
            return NEWLINE;
        }
        if (HTML_DL.equals(name)) {
            this.inDescription = true;
            return NEWLINE;
        }
        if (!this.listWikiStack.isEmpty() && HTML_LI.equals(name)) {
            return StringUtils.repeat((String)this.listWikiStack.peekFirst(), (int)this.listWikiStack.size()) + " ";
        }
        if (this.inDescription) {
            if (HTML_DD.equals(name)) {
                return WIKI_DD;
            }
            if (HTML_DT.equals(name)) {
                return WIKI_DT;
            }
        }
        return WIKI_DT;
    }

    public String exit(String name) {
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return WIKI_DT;
        }
        if (HTML_OL.equals(name)) {
            this.listWikiStack.pollFirst();
            return NEWLINE;
        }
        if (HTML_UL.equals(name)) {
            this.listWikiStack.pollFirst();
            return NEWLINE;
        }
        if (HTML_DL.equals(name)) {
            this.inDescription = false;
            return NEWLINE;
        }
        if (StringUtil.in((String)name, (String[])new String[]{HTML_LI, HTML_DD, HTML_DT})) {
            return NEWLINE;
        }
        return WIKI_DT;
    }
}

