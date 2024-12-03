/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.internal.StringUtil
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 *  org.jsoup.nodes.TextNode
 */
package com.atlassian.mail.converters.wiki;

import com.atlassian.mail.converters.wiki.BlockStyleHandler;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

@ParametersAreNonnullByDefault
final class TableHandler {
    public static final String HTML_TABLE = "table";
    public static final String HTML_TH = "th";
    public static final String HTML_TR = "tr";
    public static final String HTML_TD = "td";
    private static final String WIKI_HEADER = "||";
    private static final String WIKI_DATA = "|";
    private static final String WIKI_ROW_END = "|";
    private static final String NEWLINE = "\n";
    private final BlockStyleHandler blockStyleHandler;
    private boolean inTable;
    private boolean inRow;
    private boolean firstTableRowData;

    public TableHandler(BlockStyleHandler blockStyleHandler) {
        this.blockStyleHandler = blockStyleHandler;
    }

    public String enter(String name) {
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        if (HTML_TABLE.equals(name)) {
            this.inTable = true;
            return NEWLINE;
        }
        if (this.inTable && HTML_TR.equals(name)) {
            this.inRow = true;
            this.firstTableRowData = true;
            return "";
        }
        if (this.inRow && HTML_TH.equals(name)) {
            return WIKI_HEADER;
        }
        if (this.inRow && HTML_TD.equals(name)) {
            return "|";
        }
        return "";
    }

    public String exit(String name) {
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        if (HTML_TABLE.equals(name)) {
            this.inTable = false;
            return NEWLINE;
        }
        if (this.inRow && HTML_TR.equals(name)) {
            this.inRow = false;
            return "|\n";
        }
        if (StringUtil.in((String)name, (String[])new String[]{HTML_TD, HTML_TH})) {
            this.firstTableRowData = false;
        }
        return "";
    }

    public boolean isFirstTableRowData() {
        return this.inTable && this.inRow && this.firstTableRowData;
    }

    public boolean isEndOfRow() {
        return this.inTable && !this.inRow;
    }

    public boolean isInTable() {
        return this.inTable;
    }

    public boolean isStartOfTableData(Node node) {
        Node parent = node.parent();
        if (parent != null && parent.childNodeSize() > 0) {
            int index = 0;
            Node node1 = null;
            while (node1 == null && index < parent.childNodeSize() && !node.equals((Object)(node1 = parent.childNode(index++)))) {
                Element element;
                if (node1 instanceof TextNode) {
                    if (StringUtils.isNotBlank((CharSequence)((TextNode)node1).text())) break;
                    node1 = null;
                    continue;
                }
                if (!(node1 instanceof Element) || !StringUtil.in((String)(element = (Element)node1).nodeName(), (String[])new String[]{"p", "div", "br"})) continue;
                node1 = null;
            }
            return node.equals(node1) && StringUtil.in((String)parent.nodeName(), (String[])new String[]{HTML_TD, HTML_TH});
        }
        return false;
    }
}

