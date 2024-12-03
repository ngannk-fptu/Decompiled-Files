/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.converter.Separation;
import java.util.HashMap;
import java.util.Map;

class TypeBasedSeparation
implements Separation {
    private static final Map<NodePair, Separation> map;
    private final NodePair nodePair;
    private final String separator;
    private final String tableSeparator;
    private final String listSeparator;

    public static Separation getSeparation(String previous, String current) {
        Separation separation = map.get(new NodePair(previous, current));
        return separation != null ? separation : Separation.ALWAYS_EMPTY;
    }

    public TypeBasedSeparation(String previous, String current, String separator, String tableSeparator, String listSeparator) {
        this.nodePair = new NodePair(previous, current);
        this.separator = separator;
        this.tableSeparator = tableSeparator;
        this.listSeparator = listSeparator;
    }

    public TypeBasedSeparation(String previous, String current, String separator, String tableSeparator) {
        this.nodePair = new NodePair(previous, current);
        this.separator = separator;
        this.tableSeparator = tableSeparator;
        this.listSeparator = separator;
    }

    @Override
    public String getSeparator() {
        return this.separator;
    }

    @Override
    public String getTableSeparator() {
        return this.tableSeparator;
    }

    @Override
    public String getListSeparator() {
        return this.listSeparator;
    }

    public NodePair getNodePair() {
        return this.nodePair;
    }

    static {
        TypeBasedSeparation[] separators;
        map = new HashMap<NodePair, Separation>();
        for (TypeBasedSeparation separator : separators = new TypeBasedSeparation[]{new TypeBasedSeparation("img", "br", "\n", "\\\\\n "), new TypeBasedSeparation("list", "heading", "\n", null), new TypeBasedSeparation("list", "p", "\n\n", null), new TypeBasedSeparation("list", "list", "\n\n", null), new TypeBasedSeparation("list", "div", "\n\n", null), new TypeBasedSeparation("list", "a", "\n\n", null), new TypeBasedSeparation("list", "text", "\n\n", "\n"), new TypeBasedSeparation("list", "table", "\n\n", null), new TypeBasedSeparation("list", "br", "\n\n", "\\\\\n "), new TypeBasedSeparation("list", "userNewline", "\n", null), new TypeBasedSeparation("list", "li", "\n", "\n"), new TypeBasedSeparation("table", "table", "\n\n", null), new TypeBasedSeparation("table", "div", "\n", null), new TypeBasedSeparation("table", "a", "\n", null), new TypeBasedSeparation("table", "p", "\n", null), new TypeBasedSeparation("table", "userNewline", "\n", null), new TypeBasedSeparation("table", "br", "\n", "\\\\\n "), new TypeBasedSeparation("table", "forcedNewline", "\n", "TEXTSEP"), new TypeBasedSeparation("table", "text", "\n", null), new TypeBasedSeparation("table", "list", "\n", null), new TypeBasedSeparation("table", "heading", "\n", "", ""), new TypeBasedSeparation("text", "table", "\n", null), new TypeBasedSeparation("text", "list", "\n", "\n"), new TypeBasedSeparation("text", "forcedNewline", "\n", "TEXTSEP"), new TypeBasedSeparation("text", "heading", "\n", "", ""), new TypeBasedSeparation("text", "div", "\n", "\n", "\n"), new TypeBasedSeparation("text", "p", "\n", null, null), new TypeBasedSeparation("text", "br", "\n", "\\\\\n "), new TypeBasedSeparation("blockquote", "p", "\n", null), new TypeBasedSeparation("blockquote", "br", "\n", "\\\\\n "), new TypeBasedSeparation("p", "list", "\n", "\n"), new TypeBasedSeparation("p", "table", "\n", null), new TypeBasedSeparation("p", "text", "\n\n", null), new TypeBasedSeparation("p", "p", "\n\n", "\\\\\n", "\\\\\n"), new TypeBasedSeparation("p", "userNewline", "\n", "\\\\\n"), new TypeBasedSeparation("p", "heading", "\n", "", ""), new TypeBasedSeparation("p", "div", "\n", "", "\n"), new TypeBasedSeparation("p", "br", "\n", "\\\\\n "), new TypeBasedSeparation("heading", "table", "\n\n", null), new TypeBasedSeparation("heading", "div", "\n\n", "", ""), new TypeBasedSeparation("heading", "span", "\n\n", null, "\n"), new TypeBasedSeparation("heading", "text", "\n\n", null, "\n"), new TypeBasedSeparation("heading", "heading", "\n\n", null), new TypeBasedSeparation("heading", "p", "\n\n", "\n", "\n"), new TypeBasedSeparation("heading", "list", "\n\n", "\n", "\n"), new TypeBasedSeparation("heading", "br", "\n\n", "\\\\\n ", "\n"), new TypeBasedSeparation("heading", "userNewline", "\n", "\\\\\n "), new TypeBasedSeparation("tr", "tr", "\n", "\n"), new TypeBasedSeparation("a", "list", "\n", "\n"), new TypeBasedSeparation("a", "br", "\n", "\\\\\n "), new TypeBasedSeparation("br", "list", "", ""), new TypeBasedSeparation("br", "br", "\n", "\\\\\n"), new TypeBasedSeparation("div", "list", "\n", "\n"), new TypeBasedSeparation("div", "table", "\n", "\n"), new TypeBasedSeparation("div", "imagelink", "\n", "\n"), new TypeBasedSeparation("div", "text", "\n", "\n"), new TypeBasedSeparation("div", "p", "\n", null), new TypeBasedSeparation("div", "userNewline", "\n", null), new TypeBasedSeparation("div", "heading", "\n", "", ""), new TypeBasedSeparation("div", "div", "\n", "", "\n"), new TypeBasedSeparation("div", "br", "\n", "\\\\\n "), new TypeBasedSeparation("forcedNewline", "list", "\n", "\n"), new TypeBasedSeparation("forcedNewline", "li", "", ""), new TypeBasedSeparation("forcedNewline", "text", "\n", "\n"), new TypeBasedSeparation("forcedNewline", "forcedNewline", "\n", "\n"), new TypeBasedSeparation("forcedNewline", "a", "\n", "\n"), new TypeBasedSeparation("forcedNewline", "div", "\n", "\n"), new TypeBasedSeparation("forcedNewline", "br", "\n\n", "\n\\\\\n "), new TypeBasedSeparation("pre", "text", "\n", null), new TypeBasedSeparation("pre", "pre", "\n", null), new TypeBasedSeparation("pre", "br", "\n", "\\\\\n "), new TypeBasedSeparation("li", "li", "\n", "\n"), new TypeBasedSeparation("li", "list", "\n", "\n"), new TypeBasedSeparation("span", "heading", "\n", "", ""), new TypeBasedSeparation("span", "div", "\n", "\n", "\n"), new TypeBasedSeparation("span", "p", "\n", null, null), new TypeBasedSeparation("span", "br", "\n", "\\\\\n "), new TypeBasedSeparation("hr", "imagelink", "\n", "\n", "\n"), new TypeBasedSeparation("hr", "div", "\n", "\n", "\n"), new TypeBasedSeparation("hr", "list", "\n", "\n", "\n"), new TypeBasedSeparation("hr", "table", "\n", "\n", "\n"), new TypeBasedSeparation("hr", "p", "\n", null, null), new TypeBasedSeparation("hr", "text", "\n", null, null), new TypeBasedSeparation("span", "br", "\n", "\\\\\n "), new TypeBasedSeparation("font", "list", "\n", "\n", "\n"), new TypeBasedSeparation("font", "br", "\n", "\\\\\n "), new TypeBasedSeparation("emoticon", "text", " ", " "), new TypeBasedSeparation("emoticon", "br", "\n", "\\\\\n ")}) {
            map.put(separator.getNodePair(), separator);
        }
    }

    public static class NodePair {
        private final String previous;
        private final String current;

        public NodePair(String previous, String current) {
            this.previous = previous;
            this.current = current;
        }

        public String getPrevious() {
            return this.previous;
        }

        public String getCurrent() {
            return this.current;
        }

        public boolean equals(Object obj) {
            if (obj instanceof NodePair) {
                NodePair otherNodePair = (NodePair)obj;
                return this.previous.equals(otherNodePair.previous) && this.current.equals(otherNodePair.current);
            }
            return false;
        }

        public int hashCode() {
            return 43 * (this.previous == null ? 1 : this.previous.hashCode()) + (this.current == null ? 1 : this.current.hashCode());
        }
    }
}

