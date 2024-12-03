/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.AbstractPageCallback;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.PageCallback;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;

public class AlertCallbacks {
    private AlertCallbacks() {
    }

    public static <T> PageCallback<T, List<T>> collectingAll() {
        return new AbstractPageCallback<T, List<T>>(Lists.newArrayList()){

            @Override
            @Nonnull
            public CallbackResult onItem(@Nonnull T item) {
                ((List)this.value).add(item);
                return CallbackResult.CONTINUE;
            }
        };
    }
}

