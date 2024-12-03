/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.csskit.fn.GenericLinearGradient;

public class RepeatingLinearGradientImpl
extends GenericLinearGradient
implements TermFunction.LinearGradient {
    @Override
    public boolean isRepeating() {
        return true;
    }
}

