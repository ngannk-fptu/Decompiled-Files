/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import com.google.inject.internal.cglib.core.$Constants;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$Signature;
import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.proxy.$CallbackGenerator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

class $LazyLoaderGenerator
implements $CallbackGenerator {
    public static final $LazyLoaderGenerator INSTANCE = new $LazyLoaderGenerator();
    private static final $Signature LOAD_OBJECT = $TypeUtils.parseSignature("Object loadObject()");
    private static final $Type LAZY_LOADER = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$LazyLoader");

    $LazyLoaderGenerator() {
    }

    public void generate($ClassEmitter ce, $CallbackGenerator.Context context, List methods) {
        $CodeEmitter e;
        HashSet<Integer> indexes = new HashSet<Integer>();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            $MethodInfo method = ($MethodInfo)it.next();
            if ($TypeUtils.isProtected(method.getModifiers())) continue;
            int index = context.getIndex(method);
            indexes.add(new Integer(index));
            e = context.beginMethod(ce, method);
            e.load_this();
            e.dup();
            e.invoke_virtual_this(this.loadMethod(index));
            e.checkcast(method.getClassInfo().getType());
            e.load_args();
            e.invoke(method);
            e.return_value();
            e.end_method();
        }
        it = indexes.iterator();
        while (it.hasNext()) {
            int index = (Integer)it.next();
            String delegate = "CGLIB$LAZY_LOADER_" + index;
            ce.declare_field(2, delegate, $Constants.TYPE_OBJECT, null);
            e = ce.begin_method(50, this.loadMethod(index), null);
            e.load_this();
            e.getfield(delegate);
            e.dup();
            $Label end = e.make_label();
            e.ifnonnull(end);
            e.pop();
            e.load_this();
            context.emitCallback(e, index);
            e.invoke_interface(LAZY_LOADER, LOAD_OBJECT);
            e.dup_x1();
            e.putfield(delegate);
            e.mark(end);
            e.return_value();
            e.end_method();
        }
    }

    private $Signature loadMethod(int index) {
        return new $Signature("CGLIB$LOAD_PRIVATE_" + index, $Constants.TYPE_OBJECT, $Constants.TYPES_EMPTY);
    }

    public void generateStatic($CodeEmitter e, $CallbackGenerator.Context context, List methods) {
    }
}

