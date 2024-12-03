/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.regex.Pattern;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.Formula;

public interface GuideIf
extends Formula {
    public static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public String getName();

    public void setName(String var1);

    public String getFmla();

    public void setFmla(String var1);

    @Override
    default public double evaluate(Context ctx) {
        return this.evaluateGuide(ctx);
    }

    default public double evaluateGuide(Context ctx) {
        Op op;
        String[] operands = WHITESPACE.split(this.getFmla());
        switch (operands[0]) {
            case "*/": {
                op = Op.muldiv;
                break;
            }
            case "+-": {
                op = Op.addsub;
                break;
            }
            case "+/": {
                op = Op.adddiv;
                break;
            }
            case "?:": {
                op = Op.ifelse;
                break;
            }
            default: {
                op = Op.valueOf(operands[0]);
            }
        }
        double x = operands.length > 1 ? ctx.getValue(operands[1]) : 0.0;
        double y = operands.length > 2 ? ctx.getValue(operands[2]) : 0.0;
        double z = operands.length > 3 ? ctx.getValue(operands[3]) : 0.0;
        switch (op) {
            case abs: {
                return Math.abs(x);
            }
            case adddiv: {
                return z == 0.0 ? 0.0 : (x + y) / z;
            }
            case addsub: {
                return x + y - z;
            }
            case at2: {
                return Math.toDegrees(Math.atan2(y, x)) * 60000.0;
            }
            case cos: {
                return x * Math.cos(Math.toRadians(y / 60000.0));
            }
            case cat2: {
                return x * Math.cos(Math.atan2(z, y));
            }
            case ifelse: {
                return x > 0.0 ? y : z;
            }
            case val: {
                return x;
            }
            case max: {
                return Math.max(x, y);
            }
            case min: {
                return Math.min(x, y);
            }
            case mod: {
                return Math.sqrt(x * x + y * y + z * z);
            }
            case muldiv: {
                return z == 0.0 ? 0.0 : x * y / z;
            }
            case pin: {
                return Math.max(x, Math.min(y, z));
            }
            case sat2: {
                return x * Math.sin(Math.atan2(z, y));
            }
            case sin: {
                return x * Math.sin(Math.toRadians(y / 60000.0));
            }
            case sqrt: {
                return Math.sqrt(x);
            }
            case tan: {
                return x * Math.tan(Math.toRadians(y / 60000.0));
            }
        }
        return 0.0;
    }

    public static enum Op {
        muldiv,
        addsub,
        adddiv,
        ifelse,
        val,
        abs,
        sqrt,
        max,
        min,
        at2,
        sin,
        cos,
        tan,
        cat2,
        sat2,
        pin,
        mod;

    }
}

