/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.render.InlineBox;

public class WhitespaceStripper {
    public static final String SPACE = " ";
    public static final String EOL = "\n";
    public static final char EOLC = '\n';
    public static final Pattern linefeed_space_collapse = Pattern.compile("\\s+\\n\\s+");
    public static final Pattern linefeed_to_space = Pattern.compile("\\n");
    public static final Pattern tab_to_space = Pattern.compile("\\t");
    public static final Pattern space_collapse = Pattern.compile("(?: )+");
    public static final Pattern space_before_linefeed_collapse = Pattern.compile("[\\s&&[^\\n]]\\n");

    public static void stripInlineContent(List inlineContent) {
        boolean collapse = false;
        boolean allWhitespace = true;
        for (Styleable node : inlineContent) {
            if (node.getStyle().isInline()) {
                InlineBox iB = (InlineBox)node;
                boolean collapseNext = WhitespaceStripper.stripWhitespace(iB, collapse);
                if (!iB.isRemovableWhitespace()) {
                    allWhitespace = false;
                }
                collapse = collapseNext;
                continue;
            }
            if (WhitespaceStripper.canCollapseThrough(node)) continue;
            allWhitespace = false;
            collapse = false;
        }
        if (allWhitespace) {
            WhitespaceStripper.stripTextContent(inlineContent);
        }
    }

    private static boolean canCollapseThrough(Styleable styleable) {
        CalculatedStyle style = styleable.getStyle();
        return style.isFloated() || style.isAbsolute() || style.isFixed() || style.isRunning();
    }

    private static void stripTextContent(List stripped) {
        boolean onlyAnonymous = true;
        for (Styleable node : stripped) {
            if (!node.getStyle().isInline()) continue;
            InlineBox iB = (InlineBox)node;
            if (iB.getElement() != null) {
                onlyAnonymous = false;
            }
            iB.truncateText();
        }
        if (onlyAnonymous) {
            Iterator i = stripped.iterator();
            while (i.hasNext()) {
                Styleable node;
                node = (Styleable)i.next();
                if (!node.getStyle().isInline()) continue;
                i.remove();
            }
        }
    }

    private static boolean stripWhitespace(InlineBox iB, boolean collapseLeading) {
        IdentValue whitespace = iB.getStyle().getIdent(CSSName.WHITE_SPACE);
        String text = iB.getText();
        boolean collapseNext = (text = WhitespaceStripper.collapseWhitespace(iB, whitespace, text, collapseLeading)).endsWith(SPACE) && (whitespace == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP || whitespace == IdentValue.PRE);
        iB.setText(text);
        if (text.trim().equals("")) {
            if (whitespace == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP) {
                iB.setRemovableWhitespace(true);
            } else if (whitespace == IdentValue.PRE) {
                iB.setRemovableWhitespace(false);
            } else if (text.indexOf(EOL) < 0) {
                iB.setRemovableWhitespace(true);
            }
        }
        return text.equals("") ? collapseLeading : collapseNext;
    }

    private static String collapseWhitespace(InlineBox iB, IdentValue whitespace, String text, boolean collapseLeading) {
        if (whitespace == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP) {
            text = linefeed_space_collapse.matcher(text).replaceAll(EOL);
        } else if (whitespace == IdentValue.PRE) {
            text = space_before_linefeed_collapse.matcher(text).replaceAll(EOL);
        }
        if (whitespace == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP) {
            text = linefeed_to_space.matcher(text).replaceAll(SPACE);
            text = tab_to_space.matcher(text).replaceAll(SPACE);
            text = space_collapse.matcher(text).replaceAll(SPACE);
        } else if (whitespace == IdentValue.PRE || whitespace == IdentValue.PRE_WRAP) {
            int tabSize = (int)iB.getStyle().asFloat(CSSName.TAB_SIZE);
            char[] tabs = new char[tabSize];
            Arrays.fill(tabs, ' ');
            text = tab_to_space.matcher(text).replaceAll(new String(tabs));
        } else if (whitespace == IdentValue.PRE_LINE) {
            text = tab_to_space.matcher(text).replaceAll(SPACE);
            text = space_collapse.matcher(text).replaceAll(SPACE);
        }
        if ((whitespace == IdentValue.NORMAL || whitespace == IdentValue.NOWRAP) && text.startsWith(SPACE) && collapseLeading) {
            text = text.substring(1, text.length());
        }
        return text;
    }
}

