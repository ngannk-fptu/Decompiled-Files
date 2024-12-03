/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.internal.ExposureBuilder;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.PrivateElements;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PrivateElementsImpl
implements PrivateElements {
    private final Object source;
    private List<Element> elementsMutable = Lists.newArrayList();
    private List<ExposureBuilder<?>> exposureBuilders = Lists.newArrayList();
    private ImmutableList<Element> elements;
    private ImmutableMap<Key<?>, Object> exposedKeysToSources;
    private Injector injector;

    public PrivateElementsImpl(Object source) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public List<Element> getElements() {
        if (this.elements == null) {
            this.elements = ImmutableList.copyOf(this.elementsMutable);
            this.elementsMutable = null;
        }
        return this.elements;
    }

    @Override
    public Injector getInjector() {
        return this.injector;
    }

    public void initInjector(Injector injector) {
        Preconditions.checkState((this.injector == null ? 1 : 0) != 0, (Object)"injector already initialized");
        this.injector = (Injector)Preconditions.checkNotNull((Object)injector, (Object)"injector");
    }

    @Override
    public Set<Key<?>> getExposedKeys() {
        if (this.exposedKeysToSources == null) {
            LinkedHashMap exposedKeysToSourcesMutable = Maps.newLinkedHashMap();
            for (ExposureBuilder<?> exposureBuilder : this.exposureBuilders) {
                exposedKeysToSourcesMutable.put(exposureBuilder.getKey(), exposureBuilder.getSource());
            }
            this.exposedKeysToSources = ImmutableMap.copyOf((Map)exposedKeysToSourcesMutable);
            this.exposureBuilders = null;
        }
        return this.exposedKeysToSources.keySet();
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public List<Element> getElementsMutable() {
        return this.elementsMutable;
    }

    public void addExposureBuilder(ExposureBuilder<?> exposureBuilder) {
        this.exposureBuilders.add(exposureBuilder);
    }

    @Override
    public void applyTo(Binder binder) {
        PrivateBinder privateBinder = binder.withSource(this.source).newPrivateBinder();
        for (Element element : this.getElements()) {
            element.applyTo(privateBinder);
        }
        this.getExposedKeys();
        for (Map.Entry entry : this.exposedKeysToSources.entrySet()) {
            privateBinder.withSource(entry.getValue()).expose((Key)entry.getKey());
        }
    }

    @Override
    public Object getExposedSource(Key<?> key) {
        this.getExposedKeys();
        Object source = this.exposedKeysToSources.get(key);
        Preconditions.checkArgument((source != null ? 1 : 0) != 0, (String)"%s not exposed by %s.", (Object[])new Object[]{key, this});
        return source;
    }

    public String toString() {
        return Objects.toStringHelper(PrivateElements.class).add("exposedKeys", this.getExposedKeys()).add("source", this.getSource()).toString();
    }
}

