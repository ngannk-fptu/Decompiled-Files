/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import org.apache.poi.hslf.model.textproperties.BitMaskTextProp;

public final class ParagraphFlagsTextProp
extends BitMaskTextProp {
    public static final int BULLET_IDX = 0;
    public static final int BULLET_HARDFONT_IDX = 1;
    public static final int BULLET_HARDCOLOR_IDX = 2;
    public static final int BULLET_HARDSIZE_IDX = 4;
    public static final String NAME = "paragraph_flags";

    public ParagraphFlagsTextProp() {
        super(2, 15, NAME, "bullet", "bullet.hardfont", "bullet.hardcolor", "bullet.hardsize");
    }

    public ParagraphFlagsTextProp(ParagraphFlagsTextProp other) {
        super(other);
    }

    @Override
    public ParagraphFlagsTextProp copy() {
        return new ParagraphFlagsTextProp(this);
    }
}

