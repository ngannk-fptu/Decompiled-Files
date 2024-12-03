/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyEasyList;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.ListBackedList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class EasyListImpl
extends ListBackedList
implements SoyEasyList {
    private final SoyValueHelper valueHelper;
    private boolean isMutable;

    public EasyListImpl(SoyValueHelper valueHelper) {
        super(Lists.newArrayList());
        this.valueHelper = valueHelper;
        this.isMutable = true;
    }

    private List<SoyValueProvider> getMutableList() {
        return this.providerList;
    }

    @Override
    public void add(SoyValueProvider valueProvider) {
        Preconditions.checkState((boolean)this.isMutable, (Object)"Cannot modify immutable SoyEasyList.");
        this.getMutableList().add((SoyValueProvider)Preconditions.checkNotNull((Object)valueProvider));
    }

    @Override
    public void add(@Nullable Object value) {
        this.add(this.valueHelper.convert(value));
    }

    @Override
    public void add(int index, SoyValueProvider valueProvider) {
        Preconditions.checkState((boolean)this.isMutable, (Object)"Cannot modify immutable SoyEasyList.");
        this.getMutableList().add(index, (SoyValueProvider)Preconditions.checkNotNull((Object)valueProvider));
    }

    @Override
    public void add(int index, @Nullable Object value) {
        this.add(index, this.valueHelper.convert(value));
    }

    @Override
    public void set(int index, SoyValueProvider valueProvider) {
        Preconditions.checkState((boolean)this.isMutable, (Object)"Cannot modify immutable SoyEasyList.");
        this.getMutableList().set(index, (SoyValueProvider)Preconditions.checkNotNull((Object)valueProvider));
    }

    @Override
    public void set(int index, @Nullable Object value) {
        this.set(index, this.valueHelper.convert(value));
    }

    @Override
    public void del(int index) {
        Preconditions.checkState((boolean)this.isMutable, (Object)"Cannot modify immutable SoyEasyList.");
        this.providerList.remove(index);
    }

    @Override
    public void addAllFromList(SoyList list) {
        for (SoyValueProvider soyValueProvider : list.asJavaList()) {
            this.add(soyValueProvider);
        }
    }

    @Override
    public void addAllFromJavaIterable(Iterable<?> javaIterable) {
        for (Object value : javaIterable) {
            this.add(value);
        }
    }

    @Override
    public SoyEasyList makeImmutable() {
        this.isMutable = false;
        return this;
    }
}

