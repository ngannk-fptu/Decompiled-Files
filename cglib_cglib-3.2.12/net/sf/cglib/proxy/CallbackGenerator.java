/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.util.List;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.Signature;

interface CallbackGenerator {
    public void generate(ClassEmitter var1, Context var2, List var3) throws Exception;

    public void generateStatic(CodeEmitter var1, Context var2, List var3) throws Exception;

    public static interface Context {
        public ClassLoader getClassLoader();

        public CodeEmitter beginMethod(ClassEmitter var1, MethodInfo var2);

        public int getOriginalModifiers(MethodInfo var1);

        public int getIndex(MethodInfo var1);

        public void emitCallback(CodeEmitter var1, int var2);

        public Signature getImplSignature(MethodInfo var1);

        public void emitLoadArgsAndInvoke(CodeEmitter var1, MethodInfo var2);
    }
}

