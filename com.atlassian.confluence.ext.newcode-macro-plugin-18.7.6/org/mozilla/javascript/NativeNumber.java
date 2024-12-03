/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.DToA;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

final class NativeNumber
extends IdScriptableObject {
    private static final long serialVersionUID = 3504516769741512101L;
    public static final double MAX_SAFE_INTEGER = 9.007199254740991E15;
    private static final Object NUMBER_TAG = "Number";
    private static final int MAX_PRECISION = 100;
    private static final double MIN_SAFE_INTEGER = -9.007199254740991E15;
    private static final double EPSILON = 2.220446049250313E-16;
    private static final int ConstructorId_isFinite = -1;
    private static final int ConstructorId_isNaN = -2;
    private static final int ConstructorId_isInteger = -3;
    private static final int ConstructorId_isSafeInteger = -4;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toLocaleString = 3;
    private static final int Id_toSource = 4;
    private static final int Id_valueOf = 5;
    private static final int Id_toFixed = 6;
    private static final int Id_toExponential = 7;
    private static final int Id_toPrecision = 8;
    private static final int MAX_PROTOTYPE_ID = 8;
    private double doubleValue;

    static void init(Scriptable scope, boolean sealed) {
        NativeNumber obj = new NativeNumber(0.0);
        obj.exportAsJSClass(8, scope, sealed);
    }

    NativeNumber(double number) {
        this.doubleValue = number;
    }

    @Override
    public String getClassName() {
        return "Number";
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        Object parseInt;
        int attr = 7;
        ctor.defineProperty("NaN", (Object)ScriptRuntime.NaNobj, 7);
        ctor.defineProperty("POSITIVE_INFINITY", (Object)ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), 7);
        ctor.defineProperty("NEGATIVE_INFINITY", (Object)ScriptRuntime.wrapNumber(Double.NEGATIVE_INFINITY), 7);
        ctor.defineProperty("MAX_VALUE", (Object)ScriptRuntime.wrapNumber(Double.MAX_VALUE), 7);
        ctor.defineProperty("MIN_VALUE", (Object)ScriptRuntime.wrapNumber(Double.MIN_VALUE), 7);
        ctor.defineProperty("MAX_SAFE_INTEGER", (Object)ScriptRuntime.wrapNumber(9.007199254740991E15), 7);
        ctor.defineProperty("MIN_SAFE_INTEGER", (Object)ScriptRuntime.wrapNumber(-9.007199254740991E15), 7);
        ctor.defineProperty("EPSILON", (Object)ScriptRuntime.wrapNumber(2.220446049250313E-16), 7);
        this.addIdFunctionProperty(ctor, NUMBER_TAG, -1, "isFinite", 1);
        this.addIdFunctionProperty(ctor, NUMBER_TAG, -2, "isNaN", 1);
        this.addIdFunctionProperty(ctor, NUMBER_TAG, -3, "isInteger", 1);
        this.addIdFunctionProperty(ctor, NUMBER_TAG, -4, "isSafeInteger", 1);
        Object parseFloat = ScriptRuntime.getTopLevelProp(ctor, "parseFloat");
        if (parseFloat instanceof IdFunctionObject) {
            ((IdFunctionObject)parseFloat).addAsProperty(ctor);
        }
        if ((parseInt = ScriptRuntime.getTopLevelProp(ctor, "parseInt")) instanceof IdFunctionObject) {
            ((IdFunctionObject)parseInt).addAsProperty(ctor);
        }
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 1: {
                arity = 1;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 1;
                s = "toString";
                break;
            }
            case 3: {
                arity = 1;
                s = "toLocaleString";
                break;
            }
            case 4: {
                arity = 0;
                s = "toSource";
                break;
            }
            case 5: {
                arity = 0;
                s = "valueOf";
                break;
            }
            case 6: {
                arity = 1;
                s = "toFixed";
                break;
            }
            case 7: {
                arity = 1;
                s = "toExponential";
                break;
            }
            case 8: {
                arity = 1;
                s = "toPrecision";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(NUMBER_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(NUMBER_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == 1) {
            double val;
            double d = val = args.length >= 1 ? ScriptRuntime.toNumeric(args[0]).doubleValue() : 0.0;
            if (thisObj == null) {
                return new NativeNumber(val);
            }
            return ScriptRuntime.wrapNumber(val);
        }
        if (id < 1) {
            return NativeNumber.execConstructorCall(id, args);
        }
        double value = NativeNumber.ensureType((Object)thisObj, NativeNumber.class, (IdFunctionObject)f).doubleValue;
        switch (id) {
            case 2: 
            case 3: {
                int base = args.length == 0 || Undefined.isUndefined(args[0]) ? 10 : ScriptRuntime.toInt32(args[0]);
                return ScriptRuntime.numberToString(value, base);
            }
            case 4: {
                return "(new Number(" + ScriptRuntime.toString(value) + "))";
            }
            case 5: {
                return ScriptRuntime.wrapNumber(value);
            }
            case 6: {
                int precisionMin = cx.version < 200 ? -20 : 0;
                return NativeNumber.num_to(value, args, 2, 2, precisionMin, 0);
            }
            case 7: {
                if (Double.isNaN(value)) {
                    return "NaN";
                }
                if (Double.isInfinite(value)) {
                    if (value >= 0.0) {
                        return "Infinity";
                    }
                    return "-Infinity";
                }
                return NativeNumber.num_to(value, args, 1, 3, 0, 1);
            }
            case 8: {
                if (args.length == 0 || Undefined.isUndefined(args[0])) {
                    return ScriptRuntime.numberToString(value, 10);
                }
                if (Double.isNaN(value)) {
                    return "NaN";
                }
                if (Double.isInfinite(value)) {
                    if (value >= 0.0) {
                        return "Infinity";
                    }
                    return "-Infinity";
                }
                return NativeNumber.num_to(value, args, 0, 4, 1, 0);
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    private static Object execConstructorCall(int id, Object[] args) {
        switch (id) {
            case -1: {
                if (args.length == 0 || Undefined.isUndefined(args[0])) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return NativeNumber.isFinite(args[0]);
                }
                return Boolean.FALSE;
            }
            case -2: {
                if (args.length == 0 || Undefined.isUndefined(args[0])) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return NativeNumber.isNaN((Number)args[0]);
                }
                return Boolean.FALSE;
            }
            case -3: {
                if (args.length == 0 || Undefined.isUndefined(args[0])) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return NativeNumber.isInteger((Number)args[0]);
                }
                return Boolean.FALSE;
            }
            case -4: {
                if (args.length == 0 || Undefined.instance == args[0]) {
                    return Boolean.FALSE;
                }
                if (args[0] instanceof Number) {
                    return NativeNumber.isSafeInteger((Number)args[0]);
                }
                return Boolean.FALSE;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public String toString() {
        return ScriptRuntime.numberToString(this.doubleValue, 10);
    }

    private static String num_to(double val, Object[] args, int zeroArgMode, int oneArgMode, int precisionMin, int precisionOffset) {
        int precision;
        if (args.length == 0) {
            precision = 0;
            oneArgMode = zeroArgMode;
        } else {
            double p = ScriptRuntime.toInteger(args[0]);
            if (p < (double)precisionMin || p > 100.0) {
                String msg = ScriptRuntime.getMessageById("msg.bad.precision", ScriptRuntime.toString(args[0]));
                throw ScriptRuntime.rangeError(msg);
            }
            precision = ScriptRuntime.toInt32(p);
        }
        StringBuilder sb = new StringBuilder();
        DToA.JS_dtostr(sb, oneArgMode, precision + precisionOffset, val);
        return sb.toString();
    }

    static Object isFinite(Object val) {
        double d = ScriptRuntime.toNumber(val);
        Double nd = d;
        return ScriptRuntime.wrapBoolean(!nd.isInfinite() && !nd.isNaN());
    }

    private static Boolean isNaN(Number val) {
        if (val instanceof Double) {
            return ((Double)val).isNaN();
        }
        double d = val.doubleValue();
        return Double.isNaN(d);
    }

    private static boolean isInteger(Number val) {
        if (val instanceof Double) {
            return NativeNumber.isDoubleInteger((Double)val);
        }
        return NativeNumber.isDoubleInteger(val.doubleValue());
    }

    private static boolean isDoubleInteger(Double d) {
        return !d.isInfinite() && !d.isNaN() && Math.floor(d) == d;
    }

    private static boolean isDoubleInteger(double d) {
        return !Double.isInfinite(d) && !Double.isNaN(d) && Math.floor(d) == d;
    }

    private static boolean isSafeInteger(Number val) {
        if (val instanceof Double) {
            return NativeNumber.isDoubleSafeInteger((Double)val);
        }
        return NativeNumber.isDoubleSafeInteger(val.doubleValue());
    }

    private static boolean isDoubleSafeInteger(Double d) {
        return NativeNumber.isDoubleInteger(d) && d <= 9.007199254740991E15 && d >= -9.007199254740991E15;
    }

    private static boolean isDoubleSafeInteger(double d) {
        return NativeNumber.isDoubleInteger(d) && d <= 9.007199254740991E15 && d >= -9.007199254740991E15;
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "toString": {
                id = 2;
                break;
            }
            case "toLocaleString": {
                id = 3;
                break;
            }
            case "toSource": {
                id = 4;
                break;
            }
            case "valueOf": {
                id = 5;
                break;
            }
            case "toFixed": {
                id = 6;
                break;
            }
            case "toExponential": {
                id = 7;
                break;
            }
            case "toPrecision": {
                id = 8;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

