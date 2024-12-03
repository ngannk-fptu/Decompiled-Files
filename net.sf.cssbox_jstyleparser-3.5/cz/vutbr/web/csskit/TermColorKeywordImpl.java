/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.csskit.Color;
import cz.vutbr.web.csskit.TermImpl;

public class TermColorKeywordImpl
extends TermImpl<Color>
implements TermColor {
    private TermColor.Keyword keyword;

    protected TermColorKeywordImpl(TermColor.Keyword keyword, int r, int g, int b, int a) {
        this.keyword = keyword;
        this.value = new Color(r, g, b, a);
    }

    protected TermColorKeywordImpl(TermColor.Keyword keyword, Color value) {
        this.keyword = keyword;
        this.value = value;
    }

    @Override
    public TermColor.Keyword getKeyword() {
        return this.keyword;
    }

    @Override
    public boolean isTransparent() {
        return this.keyword == TermColor.Keyword.TRANSPARENT || ((Color)this.value).getAlpha() == 0;
    }

    @Override
    public String toString() {
        if (this.operator != null) {
            return this.operator.value() + this.keyword.toString();
        }
        return this.keyword.toString();
    }
}

