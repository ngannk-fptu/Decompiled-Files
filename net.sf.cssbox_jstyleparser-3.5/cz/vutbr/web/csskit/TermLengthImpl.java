/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.csskit.TermFloatValueImpl;

public class TermLengthImpl
extends TermFloatValueImpl
implements TermLength {
    protected TermLengthImpl() {
    }

    @Override
    public boolean isPercentage() {
        return false;
    }
}

