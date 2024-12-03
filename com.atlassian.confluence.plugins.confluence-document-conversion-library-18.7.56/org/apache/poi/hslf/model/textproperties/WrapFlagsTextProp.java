/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import org.apache.poi.hslf.model.textproperties.BitMaskTextProp;

public class WrapFlagsTextProp
extends BitMaskTextProp {
    public static final int CHAR_WRAP_IDX = 0;
    public static final int WORD_WRAO_IDX = 1;
    public static final int OVERFLOW_IDX = 2;
    public static final String NAME = "wrapFlags";

    public WrapFlagsTextProp() {
        super(2, 917504, NAME, "charWrap", "wordWrap", "overflow");
    }

    public WrapFlagsTextProp(WrapFlagsTextProp other) {
        super(other);
    }

    @Override
    public WrapFlagsTextProp copy() {
        return new WrapFlagsTextProp(this);
    }
}

