/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Term;
import cz.vutbr.web.csskit.Color;

public interface TermColor
extends Term<Color> {
    public Keyword getKeyword();

    public boolean isTransparent();

    public static enum Keyword {
        none(""),
        TRANSPARENT("transparent"),
        CURRENT_COLOR("currentColor");

        private String text;

        private Keyword(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }
    }
}

