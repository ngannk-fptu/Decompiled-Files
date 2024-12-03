/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.customware.confluence.plugin.toc.OutputHandler;

public class ListHandler
implements OutputHandler {
    private static final String ALPHA_NUMERIC_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Set<Character> UNENCODED_SET;
    private String style;
    private String indent;
    private LinkedList<HierarchyEvent> indentationStack;
    private boolean prefixWritten = false;

    public ListHandler(String style, String indent) {
        this.style = style;
        this.indent = indent;
        this.indentationStack = new LinkedList();
    }

    @Override
    public String appendStyle(Appendable out) throws IOException {
        if (this.style != null || this.indent != null) {
            String styleClass = "rbtoc" + System.currentTimeMillis();
            out.append("<style type='text/css'>/*<![CDATA[*/\n");
            out.append("div.").append(styleClass).append(" {");
            out.append("padding: 0px;");
            out.append("}\n");
            out.append("div.").append(styleClass).append(" ul {");
            if (this.style != null && !"default".equalsIgnoreCase(this.style.trim())) {
                out.append("list-style: ").append(this.encode(this.style)).append(" !important").append(";");
            }
            out.append("margin-left: 0px;");
            if (this.indent != null) {
                out.append("padding-left: ").append(this.encode(this.indent)).append(";");
            }
            out.append("}\n");
            out.append("div.").append(styleClass).append(" li {");
            out.append("margin-left: 0px;");
            out.append("padding-left: 0px;");
            out.append("}\n");
            out.append("\n/*]]>*/</style>");
            return styleClass;
        }
        return null;
    }

    public String encode(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            sb.append(this.encodeCharacter(Character.valueOf(c)));
        }
        return sb.toString();
    }

    public String encodeCharacter(Character c) {
        char ch = c.charValue();
        if (UNENCODED_SET.contains(c)) {
            return c.toString();
        }
        if (ch == '\u0000') {
            throw new IllegalArgumentException("Character value zero is not valid in CSS");
        }
        String temp = Integer.toHexString(ch);
        return "\\" + temp.toUpperCase() + " ";
    }

    @Override
    public void appendIncLevel(Appendable out) throws IOException {
        this.appendOpenUITag(out);
        this.indentationStack.push(HierarchyEvent.LEVEL);
    }

    @Override
    public void appendDecLevel(Appendable out) throws IOException {
        HierarchyEvent lastEvent;
        HierarchyEvent hierarchyEvent = lastEvent = this.indentationStack.isEmpty() ? null : this.indentationStack.pop();
        if (HierarchyEvent.HEADING.equals((Object)lastEvent)) {
            out.append("</li>");
        }
        out.append("\n</ul>\n");
    }

    @Override
    public void appendPrefix(Appendable out) throws IOException {
        this.prefixWritten = true;
        this.appendOpenUITag(out);
    }

    private void appendOpenUITag(Appendable out) throws IOException {
        out.append("\n<ul class='toc-indentation'>\n");
    }

    @Override
    public void appendPostfix(Appendable out) throws IOException {
        if (this.prefixWritten) {
            this.appendDecLevel(out);
        }
    }

    @Override
    public void appendSeparator(Appendable out) {
    }

    @Override
    public void appendHeading(Appendable out, String string) throws IOException {
        HierarchyEvent lastEvent;
        HierarchyEvent hierarchyEvent = lastEvent = this.indentationStack.isEmpty() ? null : this.indentationStack.pop();
        if (HierarchyEvent.HEADING.equals((Object)lastEvent)) {
            out.append("</li>\n");
        }
        out.append("<li>");
        out.append(string);
        this.indentationStack.push(HierarchyEvent.HEADING);
    }

    static {
        HashSet<Character> unencodedSet = new HashSet<Character>();
        char[] cArray = ALPHA_NUMERIC_STR.toCharArray();
        int n = cArray.length;
        for (int i = 0; i < n; ++i) {
            Character c = Character.valueOf(cArray[i]);
            unencodedSet.add(c);
        }
        UNENCODED_SET = Collections.unmodifiableSet(unencodedSet);
    }

    private static enum HierarchyEvent {
        LEVEL,
        HEADING;

    }
}

