/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyServerFunction
 */
package com.atlassian.confluence.notifications.batch.soy;

import com.atlassian.soy.renderer.SoyServerFunction;

public abstract class BatchTemplateFunction<T>
implements SoyServerFunction<T> {
    protected <K> K checkArgument(Object arg, Class<K> clazz) {
        if (arg != null) {
            if (!clazz.isAssignableFrom(arg.getClass())) {
                throw new ClassCastException("argument 0 is not of type '" + clazz.getName() + "' in '" + this.getName() + "' soy function : " + arg.getClass().getName());
            }
            return clazz.cast(arg);
        }
        throw new NullPointerException("argument 0 is null in '" + this.getName() + "' soy function");
    }
}

