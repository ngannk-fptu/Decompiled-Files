/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Type
 */
package net.sf.cglib.transform.impl;

import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.transform.ClassEmitterTransformer;
import org.objectweb.asm.Type;

public class AccessFieldTransformer
extends ClassEmitterTransformer {
    private Callback callback;

    public AccessFieldTransformer(Callback callback) {
        this.callback = callback;
    }

    public void declare_field(int access, String name, Type type, Object value) {
        super.declare_field(access, name, type, value);
        String property = TypeUtils.upperFirst(this.callback.getPropertyName(this.getClassType(), name));
        if (property != null) {
            CodeEmitter e = this.begin_method(1, new Signature("get" + property, type, Constants.TYPES_EMPTY), null);
            e.load_this();
            e.getfield(name);
            e.return_value();
            e.end_method();
            e = this.begin_method(1, new Signature("set" + property, Type.VOID_TYPE, new Type[]{type}), null);
            e.load_this();
            e.load_arg(0);
            e.putfield(name);
            e.return_value();
            e.end_method();
        }
    }

    public static interface Callback {
        public String getPropertyName(Type var1, String var2);
    }
}

