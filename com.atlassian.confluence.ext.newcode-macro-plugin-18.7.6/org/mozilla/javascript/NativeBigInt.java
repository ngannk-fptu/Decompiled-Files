/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.math.BigInteger;
import java.util.Arrays;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

final class NativeBigInt
extends IdScriptableObject {
    private static final long serialVersionUID = 1335609231306775449L;
    private static final Object BIG_INT_TAG = "BigInt";
    private static final int ConstructorId_asIntN = -1;
    private static final int ConstructorId_asUintN = -2;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toLocaleString = 3;
    private static final int Id_toSource = 4;
    private static final int Id_valueOf = 5;
    private static final int SymbolId_toStringTag = 6;
    private static final int MAX_PROTOTYPE_ID = 6;
    private BigInteger bigIntValue;

    static void init(Scriptable scope, boolean sealed) {
        NativeBigInt obj = new NativeBigInt(BigInteger.ZERO);
        obj.exportAsJSClass(6, scope, sealed);
    }

    NativeBigInt(BigInteger bigInt) {
        this.bigIntValue = bigInt;
    }

    @Override
    public String getClassName() {
        return "BigInt";
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        this.addIdFunctionProperty(ctor, BIG_INT_TAG, -1, "asIntN", 2);
        this.addIdFunctionProperty(ctor, BIG_INT_TAG, -2, "asUintN", 2);
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        if (id == 6) {
            this.initPrototypeValue(6, SymbolKey.TO_STRING_TAG, (Object)this.getClassName(), 3);
            return;
        }
        switch (id) {
            case 1: {
                arity = 1;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 0;
                s = "toString";
                break;
            }
            case 3: {
                arity = 0;
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
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(BIG_INT_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(BIG_INT_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == 1) {
            if (thisObj == null) {
                throw ScriptRuntime.typeErrorById("msg.not.ctor", BIG_INT_TAG);
            }
            BigInteger val = args.length >= 1 ? ScriptRuntime.toBigInt(args[0]) : BigInteger.ZERO;
            return val;
        }
        if (id < 1) {
            return NativeBigInt.execConstructorCall(id, args);
        }
        BigInteger value = NativeBigInt.ensureType((Object)thisObj, NativeBigInt.class, (IdFunctionObject)f).bigIntValue;
        switch (id) {
            case 2: 
            case 3: {
                int base = args.length == 0 || args[0] == Undefined.instance ? 10 : ScriptRuntime.toInt32(args[0]);
                return ScriptRuntime.bigIntToString(value, base);
            }
            case 4: {
                return "(new BigInt(" + ScriptRuntime.toString(value) + "))";
            }
            case 5: {
                return value;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    private static Object execConstructorCall(int id, Object[] args) {
        switch (id) {
            case -2: 
            case -1: {
                int bits = ScriptRuntime.toIndex(args.length < 1 ? Undefined.instance : args[0]);
                BigInteger bigInt = ScriptRuntime.toBigInt(args.length < 2 ? Undefined.instance : args[1]);
                if (bits == 0) {
                    return BigInteger.ZERO;
                }
                int newBytesLen = bits / 8 + 1;
                byte[] bytes = bigInt.toByteArray();
                if (newBytesLen > bytes.length) {
                    return bigInt;
                }
                byte[] newBytes = Arrays.copyOfRange(bytes, bytes.length - newBytesLen, bytes.length);
                int mod = bits % 8;
                switch (id) {
                    case -1: {
                        if (mod == 0) {
                            newBytes[0] = newBytes[1] < 0 ? -1 : 0;
                            break;
                        }
                        if ((newBytes[0] & 1 << mod - 1) != 0) {
                            newBytes[0] = (byte)(newBytes[0] | -1 << mod);
                            break;
                        }
                        newBytes[0] = (byte)(newBytes[0] & (1 << mod) - 1);
                        break;
                    }
                    case -2: {
                        newBytes[0] = (byte)(newBytes[0] & (1 << mod) - 1);
                    }
                }
                return new BigInteger(newBytes);
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public String toString() {
        return ScriptRuntime.bigIntToString(this.bigIntValue, 10);
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.TO_STRING_TAG.equals(k)) {
            return 6;
        }
        return 0;
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
            default: {
                id = 0;
            }
        }
        return id;
    }
}

