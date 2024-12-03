/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.template.soy.data.SoyAbstractMap
 *  com.google.template.soy.data.SoyDataException
 *  com.google.template.soy.data.SoyDict
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.SoyValueConverter
 *  com.google.template.soy.data.SoyValueProvider
 *  com.google.template.soy.data.restricted.StringData
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.impl.data;

import com.atlassian.soy.impl.data.AccessorSoyValueProvider;
import com.atlassian.soy.impl.data.JavaBeanAccessorResolver;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyAbstractMap;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueConverter;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.restricted.StringData;
import java.lang.reflect.Method;
import java.util.Map;
import javax.annotation.Nonnull;

public class JavaBeanSoyDict
extends SoyAbstractMap
implements SoyDict {
    private final JavaBeanAccessorResolver accessorResolver;
    private final SoyValueConverter converter;
    private final Supplier<?> supplier;
    private volatile Map<String, Method> accessors;

    public JavaBeanSoyDict(JavaBeanAccessorResolver accessorResolver, SoyValueConverter converter, Supplier<?> supplier) {
        this.accessorResolver = (JavaBeanAccessorResolver)Preconditions.checkNotNull((Object)accessorResolver, (Object)"accessorResolver");
        this.converter = (SoyValueConverter)Preconditions.checkNotNull((Object)converter, (Object)"converter");
        this.supplier = Suppliers.memoize((Supplier)((Supplier)Preconditions.checkNotNull(supplier, (Object)"supplier")));
    }

    @Nonnull
    public Map<String, ? extends SoyValueProvider> asJavaStringMap() {
        return Maps.transformValues(this.getAccessors(), (Function)new Function<Method, SoyValueProvider>(){

            public SoyValueProvider apply(Method accessorMethod) {
                return new AccessorSoyValueProvider(JavaBeanSoyDict.this.converter, JavaBeanSoyDict.this.getDelegate(), accessorMethod);
            }
        });
    }

    @Nonnull
    public Map<String, ? extends SoyValue> asResolvedJavaStringMap() {
        return Maps.transformValues(this.asJavaStringMap(), (Function)new Function<SoyValueProvider, SoyValue>(){

            public SoyValue apply(SoyValueProvider soyValueProvider) {
                return soyValueProvider.resolve();
            }
        });
    }

    public Object getDelegate() {
        return this.supplier.get();
    }

    public int getItemCnt() {
        return this.getAccessors().size();
    }

    @Nonnull
    public Iterable<? extends SoyValue> getItemKeys() {
        return Iterables.transform(this.getAccessors().keySet(), (Function)new Function<String, SoyValue>(){

            public SoyValue apply(String value) {
                return StringData.forValue((String)value);
            }
        });
    }

    public boolean hasItem(@Nonnull SoyValue key) {
        return this.getAccessors().containsKey(this.stringValue(key));
    }

    public SoyValueProvider getItemProvider(@Nonnull SoyValue key) {
        return this.getFieldProvider(this.stringValue(key));
    }

    public boolean hasField(@Nonnull String name) {
        return this.getAccessors().containsKey(name);
    }

    public SoyValue getField(@Nonnull String name) {
        SoyValueProvider fieldProvider = this.getFieldProvider(name);
        return fieldProvider == null ? null : fieldProvider.resolve();
    }

    public SoyValueProvider getFieldProvider(@Nonnull String name) {
        Method accessorMethod = this.getAccessors().get(name);
        return accessorMethod == null ? null : new AccessorSoyValueProvider(this.converter, this.getDelegate(), accessorMethod);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, Method> getAccessors() {
        Map<String, Method> accessors = this.accessors;
        if (accessors == null) {
            JavaBeanSoyDict javaBeanSoyDict = this;
            synchronized (javaBeanSoyDict) {
                accessors = this.accessors;
                if (accessors == null) {
                    accessors = this.accessors = this.accessorResolver.resolveAccessors(this.getDelegate().getClass());
                }
            }
        }
        return accessors;
    }

    private String stringValue(SoyValue key) {
        try {
            return ((StringData)key).getValue();
        }
        catch (ClassCastException e) {
            throw new SoyDataException("SoyDict accessed with non-string key (got key type " + key.getClass().getName() + ").");
        }
    }
}

