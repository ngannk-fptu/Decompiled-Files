/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._Java8;
import java.lang.reflect.Method;

public class _Java8Impl
implements _Java8 {
    public static final _Java8 INSTANCE = new _Java8Impl();

    private _Java8Impl() {
    }

    @Override
    public boolean isDefaultMethod(Method method) {
        return method.isDefault();
    }
}

