/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.extend.ContentFunction;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.layout.TextUtil;
import org.xhtmlrenderer.layout.breaker.BreakPointsProvider;
import org.xhtmlrenderer.layout.breaker.Breaker;

public class InlineBox
implements Styleable {
    private Element _element;
    private String _originalText;
    private String _text;
    private boolean _removableWhitespace;
    private boolean _startsHere;
    private boolean _endsHere;
    private CalculatedStyle _style;
    private ContentFunction _contentFunction;
    private FSFunction _function;
    private boolean _minMaxCalculated;
    private int _maxWidth;
    private int _minWidth;
    private int _firstLineWidth;
    private String _pseudoElementOrClass;
    private final Text _textNode;

    public InlineBox(String text, Text textNode) {
        this._text = text;
        this._originalText = text;
        this._textNode = textNode;
    }

    public String getText() {
        return this._text;
    }

    public void setText(String text) {
        this._text = text;
        this._originalText = text;
    }

    public void applyTextTransform() {
        this._text = this._originalText;
        this._text = TextUtil.transformText(this._text, this.getStyle());
    }

    public boolean isRemovableWhitespace() {
        return this._removableWhitespace;
    }

    public void setRemovableWhitespace(boolean removeableWhitespace) {
        this._removableWhitespace = removeableWhitespace;
    }

    public boolean isEndsHere() {
        return this._endsHere;
    }

    public void setEndsHere(boolean endsHere) {
        this._endsHere = endsHere;
    }

    public boolean isStartsHere() {
        return this._startsHere;
    }

    public void setStartsHere(boolean startsHere) {
        this._startsHere = startsHere;
    }

    @Override
    public CalculatedStyle getStyle() {
        return this._style;
    }

    @Override
    public void setStyle(CalculatedStyle style) {
        this._style = style;
    }

    @Override
    public Element getElement() {
        return this._element;
    }

    @Override
    public void setElement(Element element) {
        this._element = element;
    }

    public ContentFunction getContentFunction() {
        return this._contentFunction;
    }

    public void setContentFunction(ContentFunction contentFunction) {
        this._contentFunction = contentFunction;
    }

    public boolean isDynamicFunction() {
        return this._contentFunction != null;
    }

    private int getTextWidth(LayoutContext c, String s) {
        return c.getTextRenderer().getWidth(c.getFontContext(), c.getFont(this.getStyle().getFont(c)), s);
    }

    private int getMaxCharWidth(LayoutContext c, String s) {
        char[] chars = s.toCharArray();
        int result = 0;
        for (int i = 0; i < chars.length; ++i) {
            int width = this.getTextWidth(c, Character.toString(chars[i]));
            if (width <= result) continue;
            result = width;
        }
        return result;
    }

    private void calcMaxWidthFromLineLength(LayoutContext c, int cbWidth, boolean trim) {
        int length;
        String target;
        int last = 0;
        int current = 0;
        while ((current = this._text.indexOf("\n", last)) != -1) {
            target = this._text.substring(last, current);
            if (trim) {
                target = target.trim();
            }
            length = this.getTextWidth(c, target);
            if (last == 0) {
                length += this.getStyle().getMarginBorderPadding(c, cbWidth, 1);
            }
            if (length > this._maxWidth) {
                this._maxWidth = length;
            }
            if (last == 0) {
                this._firstLineWidth = length;
            }
            last = current + 1;
        }
        target = this._text.substring(last);
        if (trim) {
            target = target.trim();
        }
        length = this.getTextWidth(c, target);
        if ((length += this.getStyle().getMarginBorderPadding(c, cbWidth, 2)) > this._maxWidth) {
            this._maxWidth = length;
        }
        if (last == 0) {
            this._firstLineWidth = length;
        }
    }

    public int getSpaceWidth(LayoutContext c) {
        return c.getTextRenderer().getWidth(c.getFontContext(), this.getStyle().getFSFont(c), " ");
    }

    public int getTrailingSpaceWidth(LayoutContext c) {
        if (this._text.length() > 0 && this._text.charAt(this._text.length() - 1) == ' ') {
            return this.getSpaceWidth(c);
        }
        return 0;
    }

    private int calcMinWidthFromWordLength(LayoutContext c, int cbWidth, boolean trimLeadingSpace, boolean includeWS) {
        int i;
        int minWordWidth;
        int wordWidth;
        String currentWord;
        int spaceWidth = this.getSpaceWidth(c);
        int last = 0;
        int current = 0;
        int maxWidth = 0;
        int spaceCount = 0;
        boolean haveFirstWord = false;
        int firstWord = 0;
        int lastWord = 0;
        String text = this.getText(trimLeadingSpace);
        BreakPointsProvider breakIterator = Breaker.getBreakPointsProvider(text, c, this.getElement(), this.getStyle());
        while ((current = breakIterator.next().getPosition()) != -1) {
            currentWord = text.substring(last, current);
            wordWidth = this.getTextWidth(c, currentWord);
            minWordWidth = this.getStyle().getWordWrap() == IdentValue.BREAK_WORD ? this.getMaxCharWidth(c, currentWord) : wordWidth;
            if (spaceCount > 0) {
                if (includeWS) {
                    for (i = 0; i < spaceCount; ++i) {
                        wordWidth += spaceWidth;
                        minWordWidth += spaceWidth;
                    }
                } else {
                    maxWidth += spaceWidth;
                }
                spaceCount = 0;
            }
            if (minWordWidth > 0) {
                if (!haveFirstWord) {
                    firstWord = minWordWidth;
                }
                lastWord = minWordWidth;
            }
            if (minWordWidth > this._minWidth) {
                this._minWidth = minWordWidth;
            }
            maxWidth += wordWidth;
            last = current;
            for (i = current; i < text.length() && text.charAt(i) == ' '; ++i) {
                ++spaceCount;
                ++last;
            }
        }
        currentWord = text.substring(last);
        wordWidth = this.getTextWidth(c, currentWord);
        minWordWidth = this.getStyle().getWordWrap() == IdentValue.BREAK_WORD ? this.getMaxCharWidth(c, currentWord) : wordWidth;
        if (spaceCount > 0) {
            if (includeWS) {
                for (i = 0; i < spaceCount; ++i) {
                    wordWidth += spaceWidth;
                    minWordWidth += spaceWidth;
                }
            } else {
                maxWidth += spaceWidth;
            }
            spaceCount = 0;
        }
        if (minWordWidth > 0) {
            if (!haveFirstWord) {
                firstWord = minWordWidth;
            }
            lastWord = minWordWidth;
        }
        if (minWordWidth > this._minWidth) {
            this._minWidth = minWordWidth;
        }
        maxWidth += wordWidth;
        if (this.isStartsHere()) {
            int leftMBP = this.getStyle().getMarginBorderPadding(c, cbWidth, 1);
            if (firstWord + leftMBP > this._minWidth) {
                this._minWidth = firstWord + leftMBP;
            }
            maxWidth += leftMBP;
        }
        if (this.isEndsHere()) {
            int rightMBP = this.getStyle().getMarginBorderPadding(c, cbWidth, 2);
            if (lastWord + rightMBP > this._minWidth) {
                this._minWidth = lastWord + rightMBP;
            }
            maxWidth += rightMBP;
        }
        return maxWidth;
    }

    private String getText(boolean trimLeadingSpace) {
        if (!trimLeadingSpace) {
            return this.getText();
        }
        if (this._text.length() > 0 && this._text.charAt(0) == ' ') {
            return this._text.substring(1);
        }
        return this._text;
    }

    private int getInlineMBP(LayoutContext c, int cbWidth) {
        return this.getStyle().getMarginBorderPadding(c, cbWidth, 1) + this.getStyle().getMarginBorderPadding(c, cbWidth, 2);
    }

    public void calcMinMaxWidth(LayoutContext c, int cbWidth, boolean trimLeadingSpace) {
        if (!this._minMaxCalculated) {
            IdentValue whitespace = this.getStyle().getWhitespace();
            if (whitespace == IdentValue.NOWRAP) {
                this._minWidth = this._maxWidth = this.getInlineMBP(c, cbWidth) + this.getTextWidth(c, this.getText(trimLeadingSpace));
            } else if (whitespace == IdentValue.PRE) {
                this.calcMaxWidthFromLineLength(c, cbWidth, false);
                this._minWidth = this._maxWidth;
            } else if (whitespace == IdentValue.PRE_WRAP) {
                this.calcMinWidthFromWordLength(c, cbWidth, false, true);
                this.calcMaxWidthFromLineLength(c, cbWidth, false);
            } else if (whitespace == IdentValue.PRE_LINE) {
                this.calcMinWidthFromWordLength(c, cbWidth, trimLeadingSpace, false);
                this.calcMaxWidthFromLineLength(c, cbWidth, true);
            } else {
                this._maxWidth = this.calcMinWidthFromWordLength(c, cbWidth, trimLeadingSpace, false);
            }
            this._minWidth = Math.min(this._maxWidth, this._minWidth);
            this._minMaxCalculated = true;
        }
    }

    public int getMaxWidth() {
        return this._maxWidth;
    }

    public int getMinWidth() {
        return this._minWidth;
    }

    public int getFirstLineWidth() {
        return this._firstLineWidth;
    }

    @Override
    public String getPseudoElementOrClass() {
        return this._pseudoElementOrClass;
    }

    public void setPseudoElementOrClass(String pseudoElementOrClass) {
        this._pseudoElementOrClass = pseudoElementOrClass;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("InlineBox: ");
        if (this.getElement() != null) {
            result.append("<");
            result.append(this.getElement().getNodeName());
            result.append("> ");
        } else {
            result.append("(anonymous) ");
        }
        if (this.getPseudoElementOrClass() != null) {
            result.append(':');
            result.append(this.getPseudoElementOrClass());
            result.append(' ');
        }
        if (this.isStartsHere() || this.isEndsHere()) {
            result.append("(");
            if (this.isStartsHere()) {
                result.append("S");
            }
            if (this.isEndsHere()) {
                result.append("E");
            }
            result.append(") ");
        }
        this.appendPositioningInfo(result);
        result.append("(");
        result.append(this.shortText());
        result.append(") ");
        return result.toString();
    }

    protected void appendPositioningInfo(StringBuffer result) {
        if (this.getStyle().isRelative()) {
            result.append("(relative) ");
        }
        if (this.getStyle().isFixed()) {
            result.append("(fixed) ");
        }
        if (this.getStyle().isAbsolute()) {
            result.append("(absolute) ");
        }
        if (this.getStyle().isFloated()) {
            result.append("(floated) ");
        }
    }

    private String shortText() {
        if (this._text == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < this._text.length() && i < 40; ++i) {
            char c = this._text.charAt(i);
            if (c == '\n') {
                result.append(' ');
                continue;
            }
            result.append(c);
        }
        if (result.length() == 40) {
            result.append("...");
        }
        return result.toString();
    }

    public FSFunction getFunction() {
        return this._function;
    }

    public void setFunction(FSFunction function) {
        this._function = function;
    }

    public void truncateText() {
        this._text = "";
        this._originalText = "";
    }

    public Text getTextNode() {
        return this._textNode;
    }
}

