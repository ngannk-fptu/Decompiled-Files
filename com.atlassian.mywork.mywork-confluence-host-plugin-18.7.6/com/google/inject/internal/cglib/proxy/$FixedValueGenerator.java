/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm.$Type;
import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$Signature;
import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.proxy.$CallbackGenerator;
import java.util.Iterator;
import java.util.List;

class $FixedValueGenerator
implements $CallbackGenerator {
    public static final $FixedValueGenerator INSTANCE = new $FixedValueGenerator();
    private static final $Type FIXED_VALUE = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$FixedValue");
    private static final $Signature LOAD_OBJECT = $TypeUtils.parseSignature("Object loadObject()");

    $FixedValueGenerator() {
    }

    public void generate($ClassEmitter ce, $CallbackGenerator.Context context, List methods) {
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            $MethodInfo method = ($MethodInfo)it.next();
            $CodeEmitter e = context.beginMethod(ce, method);
            context.emitCallback(e, context.getIndex(method));
            e.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
            e.unbox_or_zero(e.getReturnType());
            e.return_value();
            e.end_method();
        }
    }

    public void generateStatic($CodeEmitter e, $CallbackGenerator.Context context, List methods) {
    }
}

