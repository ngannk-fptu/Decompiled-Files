/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data.internal;

import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyAbstractList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.Transforms;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract class ListBackedList
extends SoyAbstractList {
    protected final List<? extends SoyValueProvider> providerList;

    protected ListBackedList(List<? extends SoyValueProvider> providerList) {
        this.providerList = providerList;
    }

    @Override
    public final int length() {
        return this.providerList.size();
    }

    @Override
    @Nonnull
    public final List<? extends SoyValueProvider> asJavaList() {
        return Collections.unmodifiableList(this.providerList);
    }

    @Nonnull
    public final List<SoyValue> asResolvedJavaList() {
        return Lists.transform(this.asJavaList(), Transforms.RESOLVE_FUNCTION);
    }

    @Override
    public final SoyValueProvider getProvider(int index) {
        try {
            return this.providerList.get(index);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}

