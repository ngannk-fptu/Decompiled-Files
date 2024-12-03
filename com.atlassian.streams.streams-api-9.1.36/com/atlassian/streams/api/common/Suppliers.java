/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.streams.api.common;

import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicBoolean;

public class Suppliers {
    @Deprecated
    public static Supplier<Boolean> forAtomicBoolean(final AtomicBoolean a) {
        return new Supplier<Boolean>(){

            public Boolean get() {
                return a.get();
            }
        };
    }
}

