/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import com.google.inject.internal.cglib.core.$EmitUtils;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.proxy.$CallbackGenerator;
import java.util.Iterator;
import java.util.List;

class $NoOpGenerator
implements $CallbackGenerator {
    public static final $NoOpGenerator INSTANCE = new $NoOpGenerator();

    $NoOpGenerator() {
    }

    public void generate($ClassEmitter ce, $CallbackGenerator.Context context, List methods) {
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            $MethodInfo method = ($MethodInfo)it.next();
            if (!$TypeUtils.isBridge(method.getModifiers()) && (!$TypeUtils.isProtected(context.getOriginalModifiers(method)) || !$TypeUtils.isPublic(method.getModifiers()))) continue;
            $CodeEmitter e = $EmitUtils.begin_method(ce, method);
            e.load_this();
            e.load_args();
            context.emitInvoke(e, method);
            e.return_value();
            e.end_method();
        }
    }

    public void generateStatic($CodeEmitter e, $CallbackGenerator.Context context, List methods) {
    }
}

