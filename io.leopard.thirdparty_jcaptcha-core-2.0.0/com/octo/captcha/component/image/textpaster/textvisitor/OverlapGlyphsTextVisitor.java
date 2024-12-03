/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.textpaster.textvisitor;

import com.octo.captcha.component.image.textpaster.MutableAttributedString;
import com.octo.captcha.component.image.textpaster.textvisitor.TextVisitor;

public class OverlapGlyphsTextVisitor
implements TextVisitor {
    private int overlapPixs = 0;

    public OverlapGlyphsTextVisitor(int overlapPixs) {
        this.overlapPixs = overlapPixs;
    }

    @Override
    public void visit(MutableAttributedString mas) {
        mas.overlap(this.overlapPixs);
    }
}

