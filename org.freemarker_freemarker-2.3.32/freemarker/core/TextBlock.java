/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Assignment;
import freemarker.core.AssignmentInstruction;
import freemarker.core.Comment;
import freemarker.core.Environment;
import freemarker.core.LibraryLoad;
import freemarker.core.Macro;
import freemarker.core.MixedContent;
import freemarker.core.ParameterRole;
import freemarker.core.PropertySetting;
import freemarker.core.TemplateElement;
import freemarker.core.TrimInstruction;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

public final class TextBlock
extends TemplateElement {
    private char[] text;
    private final boolean unparsed;

    public TextBlock(String text) {
        this(text, false);
    }

    public TextBlock(String text, boolean unparsed) {
        this(text.toCharArray(), unparsed);
    }

    TextBlock(char[] text, boolean unparsed) {
        this.text = text;
        this.unparsed = unparsed;
    }

    void replaceText(String text) {
        this.text = text.toCharArray();
    }

    @Override
    public TemplateElement[] accept(Environment env) throws IOException {
        env.getOut().write(this.text);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            String text = new String(this.text);
            if (this.unparsed) {
                return "<#noparse>" + text + "</#noparse>";
            }
            return text;
        }
        return "text " + StringUtil.jQuote(new String(this.text));
    }

    @Override
    String getNodeTypeSymbol() {
        return "#text";
    }

    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return new String(this.text);
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.CONTENT;
    }

    @Override
    TemplateElement postParseCleanup(boolean stripWhitespace) {
        if (this.text.length == 0) {
            return this;
        }
        int openingCharsToStrip = 0;
        int trailingCharsToStrip = 0;
        boolean deliberateLeftTrim = this.deliberateLeftTrim();
        boolean deliberateRightTrim = this.deliberateRightTrim();
        if (!stripWhitespace || this.text.length == 0) {
            return this;
        }
        TemplateElement parentElement = this.getParentElement();
        if (this.isTopLevelTextIfParentIs(parentElement) && this.previousSibling() == null) {
            return this;
        }
        if (!deliberateLeftTrim) {
            trailingCharsToStrip = this.trailingCharsToStrip();
        }
        if (!deliberateRightTrim) {
            openingCharsToStrip = this.openingCharsToStrip();
        }
        if (openingCharsToStrip == 0 && trailingCharsToStrip == 0) {
            return this;
        }
        this.text = TextBlock.substring(this.text, openingCharsToStrip, this.text.length - trailingCharsToStrip);
        if (openingCharsToStrip > 0) {
            ++this.beginLine;
            this.beginColumn = 1;
        }
        if (trailingCharsToStrip > 0) {
            this.endColumn = 0;
        }
        return this;
    }

    private boolean deliberateLeftTrim() {
        boolean result = false;
        for (TemplateElement elem = this.nextTerminalNode(); elem != null && elem.beginLine == this.endLine; elem = elem.nextTerminalNode()) {
            if (!(elem instanceof TrimInstruction)) continue;
            TrimInstruction ti = (TrimInstruction)elem;
            if (!ti.left && !ti.right) {
                result = true;
            }
            if (!ti.left) continue;
            result = true;
            int lastNewLineIndex = this.lastNewLineIndex();
            if (lastNewLineIndex < 0 && this.beginColumn != 1) continue;
            char[] firstPart = TextBlock.substring(this.text, 0, lastNewLineIndex + 1);
            char[] lastLine = TextBlock.substring(this.text, 1 + lastNewLineIndex);
            if (StringUtil.isTrimmableToEmpty(lastLine)) {
                this.text = firstPart;
                this.endColumn = 0;
                continue;
            }
            int i = 0;
            while (Character.isWhitespace(lastLine[i])) {
                ++i;
            }
            char[] printablePart = TextBlock.substring(lastLine, i);
            this.text = TextBlock.concat(firstPart, printablePart);
        }
        return result;
    }

    private boolean deliberateRightTrim() {
        boolean result = false;
        for (TemplateElement elem = this.prevTerminalNode(); elem != null && elem.endLine == this.beginLine; elem = elem.prevTerminalNode()) {
            if (!(elem instanceof TrimInstruction)) continue;
            TrimInstruction ti = (TrimInstruction)elem;
            if (!ti.left && !ti.right) {
                result = true;
            }
            if (!ti.right) continue;
            result = true;
            int firstLineIndex = this.firstNewLineIndex() + 1;
            if (firstLineIndex == 0) {
                return false;
            }
            if (this.text.length > firstLineIndex && this.text[firstLineIndex - 1] == '\r' && this.text[firstLineIndex] == '\n') {
                ++firstLineIndex;
            }
            char[] trailingPart = TextBlock.substring(this.text, firstLineIndex);
            char[] openingPart = TextBlock.substring(this.text, 0, firstLineIndex);
            if (StringUtil.isTrimmableToEmpty(openingPart)) {
                this.text = trailingPart;
                ++this.beginLine;
                this.beginColumn = 1;
                continue;
            }
            int lastNonWS = openingPart.length - 1;
            while (Character.isWhitespace(this.text[lastNonWS])) {
                --lastNonWS;
            }
            char[] printablePart = TextBlock.substring(this.text, 0, lastNonWS + 1);
            if (StringUtil.isTrimmableToEmpty(trailingPart)) {
                boolean trimTrailingPart = true;
                for (TemplateElement te = this.nextTerminalNode(); te != null && te.beginLine == this.endLine; te = te.nextTerminalNode()) {
                    if (te.heedsOpeningWhitespace()) {
                        trimTrailingPart = false;
                    }
                    if (!(te instanceof TrimInstruction) || !((TrimInstruction)te).left) continue;
                    trimTrailingPart = true;
                    break;
                }
                if (trimTrailingPart) {
                    trailingPart = CollectionUtils.EMPTY_CHAR_ARRAY;
                }
            }
            this.text = TextBlock.concat(printablePart, trailingPart);
        }
        return result;
    }

    private int firstNewLineIndex() {
        char[] text = this.text;
        for (int i = 0; i < text.length; ++i) {
            char c = text[i];
            if (c != '\r' && c != '\n') continue;
            return i;
        }
        return -1;
    }

    private int lastNewLineIndex() {
        char[] text = this.text;
        for (int i = text.length - 1; i >= 0; --i) {
            char c = text[i];
            if (c != '\r' && c != '\n') continue;
            return i;
        }
        return -1;
    }

    private int openingCharsToStrip() {
        int newlineIndex = this.firstNewLineIndex();
        if (newlineIndex == -1 && this.beginColumn != 1) {
            return 0;
        }
        if (this.text.length > ++newlineIndex && newlineIndex > 0 && this.text[newlineIndex - 1] == '\r' && this.text[newlineIndex] == '\n') {
            ++newlineIndex;
        }
        if (!StringUtil.isTrimmableToEmpty(this.text, 0, newlineIndex)) {
            return 0;
        }
        for (TemplateElement elem = this.prevTerminalNode(); elem != null && elem.endLine == this.beginLine; elem = elem.prevTerminalNode()) {
            if (!elem.heedsOpeningWhitespace()) continue;
            return 0;
        }
        return newlineIndex;
    }

    private int trailingCharsToStrip() {
        int lastNewlineIndex = this.lastNewLineIndex();
        if (lastNewlineIndex == -1 && this.beginColumn != 1) {
            return 0;
        }
        if (!StringUtil.isTrimmableToEmpty(this.text, lastNewlineIndex + 1)) {
            return 0;
        }
        for (TemplateElement elem = this.nextTerminalNode(); elem != null && elem.beginLine == this.endLine; elem = elem.nextTerminalNode()) {
            if (!elem.heedsTrailingWhitespace()) continue;
            return 0;
        }
        return this.text.length - (lastNewlineIndex + 1);
    }

    @Override
    boolean heedsTrailingWhitespace() {
        if (this.isIgnorable(true)) {
            return false;
        }
        for (int i = 0; i < this.text.length; ++i) {
            char c = this.text[i];
            if (c == '\n' || c == '\r') {
                return false;
            }
            if (Character.isWhitespace(c)) continue;
            return true;
        }
        return true;
    }

    @Override
    boolean heedsOpeningWhitespace() {
        if (this.isIgnorable(true)) {
            return false;
        }
        for (int i = this.text.length - 1; i >= 0; --i) {
            char c = this.text[i];
            if (c == '\n' || c == '\r') {
                return false;
            }
            if (Character.isWhitespace(c)) continue;
            return true;
        }
        return true;
    }

    @Override
    boolean isIgnorable(boolean stripWhitespace) {
        if (this.text == null || this.text.length == 0) {
            return true;
        }
        if (stripWhitespace) {
            if (!StringUtil.isTrimmableToEmpty(this.text)) {
                return false;
            }
            TemplateElement parentElement = this.getParentElement();
            boolean atTopLevel = this.isTopLevelTextIfParentIs(parentElement);
            TemplateElement prevSibling = this.previousSibling();
            TemplateElement nextSibling = this.nextSibling();
            return (prevSibling == null && atTopLevel || this.nonOutputtingType(prevSibling)) && (nextSibling == null && atTopLevel || this.nonOutputtingType(nextSibling));
        }
        return false;
    }

    private boolean isTopLevelTextIfParentIs(TemplateElement parentElement) {
        return parentElement == null || parentElement.getParentElement() == null && parentElement instanceof MixedContent;
    }

    private boolean nonOutputtingType(TemplateElement element) {
        return element instanceof Macro || element instanceof Assignment || element instanceof AssignmentInstruction || element instanceof PropertySetting || element instanceof LibraryLoad || element instanceof Comment;
    }

    private static char[] substring(char[] c, int from, int to) {
        char[] c2 = new char[to - from];
        System.arraycopy(c, from, c2, 0, c2.length);
        return c2;
    }

    private static char[] substring(char[] c, int from) {
        return TextBlock.substring(c, from, c.length);
    }

    private static char[] concat(char[] c1, char[] c2) {
        char[] c = new char[c1.length + c2.length];
        System.arraycopy(c1, 0, c, 0, c1.length);
        System.arraycopy(c2, 0, c, c1.length, c2.length);
        return c;
    }

    @Override
    boolean isOutputCacheable() {
        return true;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

