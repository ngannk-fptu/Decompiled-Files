/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.lang.reflect.AccessibleObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.InterfaceAdapter;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Scriptable;

public abstract class VMBridge {
    static final VMBridge instance = VMBridge.makeInstance();

    private static VMBridge makeInstance() {
        String[] classNames = new String[]{"org.mozilla.javascript.VMBridge_custom", "org.mozilla.javascript.jdk18.VMBridge_jdk18"};
        for (int i = 0; i != classNames.length; ++i) {
            VMBridge bridge;
            String className = classNames[i];
            Class<?> cl = Kit.classOrNull(className);
            if (cl == null || (bridge = (VMBridge)Kit.newInstanceOrNull(cl)) == null) continue;
            return bridge;
        }
        throw new IllegalStateException("Failed to create VMBridge instance");
    }

    protected abstract Object getThreadContextHelper();

    protected abstract Context getContext(Object var1);

    protected abstract void setContext(Object var1, Context var2);

    protected abstract boolean tryToMakeAccessible(AccessibleObject var1);

    protected abstract Object getInterfaceProxyHelper(ContextFactory var1, Class<?>[] var2);

    protected abstract Object newInterfaceProxy(Object var1, ContextFactory var2, InterfaceAdapter var3, Object var4, Scriptable var5);
}

