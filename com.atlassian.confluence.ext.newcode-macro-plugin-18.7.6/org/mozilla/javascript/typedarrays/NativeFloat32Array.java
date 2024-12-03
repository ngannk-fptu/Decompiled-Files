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

public class NativeFloat32Array
extends NativeTypedArrayView<Float> {
    private static final long serialVersionUID = -8963461831950499340L;
    private static final String CLASS_NAME = "Float32Array";
    private static final int BYTES_PER_ELEMENT = 4;

    public NativeFloat32Array() {
    }

    public NativeFloat32Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 4);
    }

    public NativeFloat32Array(int len) {
        this(new NativeArrayBuffer((double)len * 4.0), 0, len);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeFloat32Array a = new NativeFloat32Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    protected NativeFloat32Array construct(NativeArrayBuffer ab, int off, int len) {
        return new NativeFloat32Array(ab, off, len);
    }

    @Override
    public int getBytesPerElement() {
        return 4;
    }

    protected NativeFloat32Array realThis(Scriptable thisObj, IdFunctionObject f) {
        return NativeFloat32Array.ensureType(thisObj, NativeFloat32Array.class, f);
    }

    @Override
    protected Object js_get(int index) {
        if (this.checkIndex(index)) {
            return Undefined.instance;
        }
        return ByteIo.readFloat32(this.arrayBuffer.buffer, index * 4 + this.offset, NativeFloat32Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int index, Object c) {
        if (this.checkIndex(index)) {
            return Undefined.instance;
        }
        double val = ScriptRuntime.toNumber(c);
        ByteIo.writeFloat32(this.arrayBuffer.buffer, index * 4 + this.offset, val, NativeFloat32Array.useLittleEndian());
        return null;
    }

    @Override
    public Float get(int i) {
        if (this.checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Float)this.js_get(i);
    }

    @Override
    public Float set(int i, Float aByte) {
        if (this.checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Float)this.js_set(i, aByte);
    }
}

