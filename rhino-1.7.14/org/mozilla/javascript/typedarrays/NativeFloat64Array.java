/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.ByteIo;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeTypedArrayView;

public class NativeFloat64Array
extends NativeTypedArrayView<Double> {
    private static final long serialVersionUID = -1255405650050639335L;
    private static final String CLASS_NAME = "Float64Array";
    private static final int BYTES_PER_ELEMENT = 8;

    public NativeFloat64Array() {
    }

    public NativeFloat64Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 8);
    }

    public NativeFloat64Array(int len) {
        this(new NativeArrayBuffer((double)len * 8.0), 0, len);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeFloat64Array a = new NativeFloat64Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    protected NativeFloat64Array construct(NativeArrayBuffer ab, int off, int len) {
        return new NativeFloat64Array(ab, off, len);
    }

    @Override
    public int getBytesPerElement() {
        return 8;
    }

    protected NativeFloat64Array realThis(Scriptable thisObj, IdFunctionObject f) {
        return NativeFloat64Array.ensureType(thisObj, NativeFloat64Array.class, f);
    }

    @Override
    protected Object js_get(int index) {
        if (this.checkIndex(index)) {
            return Undefined.instance;
        }
        long base = ByteIo.readUint64Primitive(this.arrayBuffer.buffer, index * 8 + this.offset, NativeFloat64Array.useLittleEndian());
        return Double.longBitsToDouble(base);
    }

    @Override
    protected Object js_set(int index, Object c) {
        if (this.checkIndex(index)) {
            return Undefined.instance;
        }
        double val = ScriptRuntime.toNumber(c);
        long base = Double.doubleToLongBits(val);
        ByteIo.writeUint64(this.arrayBuffer.buffer, index * 8 + this.offset, base, NativeFloat64Array.useLittleEndian());
        return null;
    }

    @Override
    public Double get(int i) {
        if (this.checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Double)this.js_get(i);
    }

    @Override
    public Double set(int i, Double aByte) {
        if (this.checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Double)this.js_set(i, aByte);
    }
}

