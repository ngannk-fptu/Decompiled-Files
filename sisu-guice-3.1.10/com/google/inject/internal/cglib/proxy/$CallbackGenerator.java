/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import com.google.inject.internal.cglib.core.$MethodInfo;
import com.google.inject.internal.cglib.core.$Signature;
import java.util.List;

interface $CallbackGenerator {
    public void generate($ClassEmitter var1, Context var2, List var3) throws Exception;

    public void generateStatic($CodeEmitter var1, Context var2, List var3) throws Exception;

    public static interface Context {
        public ClassLoader getClassLoader();

        public $CodeEmitter beginMethod($ClassEmitter var1, $MethodInfo var2);

        public int getOriginalModifiers($MethodInfo var1);

        public int getIndex($MethodInfo var1);

        public void emitCallback($CodeEmitter var1, int var2);

        public $Signature getImplSignature($MethodInfo var1);

        public void emitInvoke($CodeEmitter var1, $MethodInfo var2);
    }
}

