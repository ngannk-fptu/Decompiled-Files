/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.MethodWrapper;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

class MixinEmitter
extends ClassEmitter {
    private static final String FIELD_NAME = "CGLIB$DELEGATES";
    private static final Signature CSTRUCT_OBJECT_ARRAY = TypeUtils.parseConstructor("Object[]");
    private static final Type MIXIN = TypeUtils.parseType("net.sf.cglib.proxy.Mixin");
    private static final Signature NEW_INSTANCE = new Signature("newInstance", MIXIN, new Type[]{Constants.TYPE_OBJECT_ARRAY});

    public MixinEmitter(ClassVisitor v, String className, Class[] classes, int[] route) {
        super(v);
        this.begin_class(46, 1, className, MIXIN, TypeUtils.getTypes(this.getInterfaces(classes)), "<generated>");
        EmitUtils.null_constructor(this);
        EmitUtils.factory_method(this, NEW_INSTANCE);
        this.declare_field(2, FIELD_NAME, Constants.TYPE_OBJECT_ARRAY, null);
        CodeEmitter e = this.begin_method(1, CSTRUCT_OBJECT_ARRAY, null);
        e.load_this();
        e.super_invoke_constructor();
        e.load_this();
        e.load_arg(0);
        e.putfield(FIELD_NAME);
        e.return_value();
        e.end_method();
        HashSet<Object> unique = new HashSet<Object>();
        for (int i = 0; i < classes.length; ++i) {
            Method[] methods = this.getMethods(classes[i]);
            for (int j = 0; j < methods.length; ++j) {
                if (!unique.add(MethodWrapper.create(methods[j]))) continue;
                MethodInfo method = ReflectUtils.getMethodInfo(methods[j]);
                e = EmitUtils.begin_method(this, method, 1);
                e.load_this();
                e.getfield(FIELD_NAME);
                e.aaload(route != null ? route[i] : i);
                e.checkcast(method.getClassInfo().getType());
                e.load_args();
                e.invoke(method);
                e.return_value();
                e.end_method();
            }
        }
        this.end_class();
    }

    protected Class[] getInterfaces(Class[] classes) {
        return classes;
    }

    protected Method[] getMethods(Class type) {
        return type.getMethods();
    }
}

