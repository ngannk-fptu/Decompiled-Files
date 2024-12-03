/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;

public interface RuleMargin
extends RuleBlock<Declaration>,
PrettyOutput {
    public MarginArea getMarginArea();

    public static enum MarginArea {
        TOPLEFTCORNER("top-left-corner"),
        TOPLEFT("top-left"),
        TOPCENTER("top-center"),
        TOPRIGHT("top-right"),
        TOPRIGHTCORNER("top-right-corner"),
        BOTTOMLEFTCORNER("bottom-left-corner"),
        BOTTOMLEFT("bottom-left"),
        BOTTOMCENTER("bottom-center"),
        BOTTOMRIGHT("bottom-right"),
        BOTTOMRIGHTCORNER("bottom-right-corner"),
        LEFTTOP("left-top"),
        LEFTMIDDLE("left-middle"),
        LEFTBOTTOM("left-bottom"),
        RIGHTTOP("right-top"),
        RIGHTMIDDLE("right-middle"),
        RIGHTBOTTOM("right-bottom");

        public final String value;

        private MarginArea(String value) {
            this.value = value;
        }
    }
}

