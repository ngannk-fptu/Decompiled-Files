/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit.fn;

import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.csskit.fn.GenericRadialGradient;

public class RepeatingRadialGradientImpl
extends GenericRadialGradient
implements TermFunction.RadialGradient {
    @Override
    public boolean isRepeating() {
        return true;
    }
}

