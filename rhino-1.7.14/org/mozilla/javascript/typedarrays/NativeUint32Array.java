/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.ByteIo;
import org.mozilla.javascript.typedarrays.Conversions;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeTypedArrayView;

public class NativeUint32Array
extends NativeTypedArrayView<Long> {
    private static final long serialVersionUID = -7987831421954144244L;
    private static final String CLASS_NAME = "Uint32Array";
    private static final int BYTES_PER_ELEMENT = 4;

    public NativeUint32Array() {
    }

    public NativeUint32Array(NativeArrayBuffer ab, int off, int len) {
        super(ab, off, len, len * 4);
    }

    public NativeUint32Array(int len) {
        this(new NativeArrayBuffer((double)len * 4.0), 0, len);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        NativeUint32Array a = new NativeUint32Array();
        a.exportAsJSClass(6, scope, sealed);
    }

    protected NativeUint32Array construct(NativeArrayBuffer ab, int off, int len) {
        return new NativeUint32Array(ab, off, len);
    }

    @Override
    public int getBytesPerElement() {
        return 4;
    }

    protected NativeUint32Array realThis(Scriptable thisObj, IdFunctionObject f) {
        return NativeUint32Array.ensureType(thisObj, NativeUint32Array.class, f);
    }

    @Override
    protected Object js_get(int index) {
        if (this.checkIndex(index)) {
            return Undefined.instance;
        }
        return ByteIo.readUint32(this.arrayBuffer.buffer, index * 4 + this.offset, NativeUint32Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int index, Object c) {
        if (this.checkIndex(index)) {
            return Undefined.instance;
        }
        long val = Conversions.toUint32(c);
        ByteIo.writeUint32(this.arrayBuffer.buffer, index * 4 + this.offset, val, NativeUint32Array.useLittleEndian());
        return null;
    }

    @Override
    public Long get(int i) {
        if (this.checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Long)this.js_get(i);
    }

    @Override
    public Long set(int i, Long aByte) {
        if (this.checkIndex(i)) {
            throw new IndexOutOfBoundsException();
        }
        return (Long)this.js_set(i, aByte);
    }
}

