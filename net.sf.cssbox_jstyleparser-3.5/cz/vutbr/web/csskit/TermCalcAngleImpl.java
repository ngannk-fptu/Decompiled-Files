/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermCalc;
import cz.vutbr.web.csskit.CalcArgs;
import cz.vutbr.web.csskit.OutputUtil;
import cz.vutbr.web.csskit.TermAngleImpl;

public class TermCalcAngleImpl
extends TermAngleImpl
implements TermCalc {
    private CalcArgs args;

    public TermCalcAngleImpl(CalcArgs args) {
        this.args = args;
    }

    @Override
    public CalcArgs getArgs() {
        return this.args;
    }

    @Override
    public Float getValue() {
        return Float.valueOf(0.0f);
    }

    @Override
    public String toString() {
        return OutputUtil.appendCalcArgs(new StringBuilder("calc"), this.args).toString();
    }
}

