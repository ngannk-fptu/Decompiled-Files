/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.process.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.model.process.spi.ManagedResources;
import org.hibernate.boot.spi.BootstrapContext;

public class ManagedResourcesImpl
implements ManagedResources {
    private Map<Class, AttributeConverterInfo> attributeConverterInfoMap = new HashMap<Class, AttributeConverterInfo>();
    private Set<Class> annotatedClassReferences = new LinkedHashSet<Class>();
    private Set<String> annotatedClassNames = new LinkedHashSet<String>();
    private Set<String> annotatedPackageNames = new LinkedHashSet<String>();
    private List<Binding> mappingFileBindings = new ArrayList<Binding>();

    public static ManagedResourcesImpl baseline(MetadataSources sources, BootstrapContext bootstrapContext) {
        ManagedResourcesImpl impl = new ManagedResourcesImpl();
        bootstrapContext.getAttributeConverters().forEach(impl::addAttributeConverterDefinition);
        impl.annotatedClassReferences.addAll(sources.getAnnotatedClasses());
        impl.annotatedClassNames.addAll(sources.getAnnotatedClassNames());
        impl.annotatedPackageNames.addAll(sources.getAnnotatedPackages());
        impl.mappingFileBindings.addAll(sources.getXmlBindings());
        return impl;
    }

    private ManagedResourcesImpl() {
    }

    @Override
    public Collection<AttributeConverterInfo> getAttributeConverterDefinitions() {
        return Collections.unmodifiableCollection(this.attributeConverterInfoMap.values());
    }

    @Override
    public Collection<Class> getAnnotatedClassReferences() {
        return Collections.unmodifiableSet(this.annotatedClassReferences);
    }

    @Override
    public Collection<String> getAnnotatedClassNames() {
        return Collections.unmodifiableSet(this.annotatedClassNames);
    }

    @Override
    public Collection<String> getAnnotatedPackageNames() {
        return Collections.unmodifiableSet(this.annotatedPackageNames);
    }

    @Override
    public Collection<Binding> getXmlMappingBindings() {
        return Collections.unmodifiableList(this.mappingFileBindings);
    }

    void addAttributeConverterDefinition(AttributeConverterInfo converterInfo) {
        this.attributeConverterInfoMap.put(converterInfo.getConverterClass(), converterInfo);
    }

    void addAnnotatedClassReference(Class annotatedClassReference) {
        this.annotatedClassReferences.add(annotatedClassReference);
    }

    void addAnnotatedClassName(String annotatedClassName) {
        this.annotatedClassNames.add(annotatedClassName);
    }

    void addAnnotatedPackageName(String annotatedPackageName) {
        this.annotatedPackageNames.add(annotatedPackageName);
    }

    void addXmlBinding(Binding binding) {
        this.mappingFileBindings.add(binding);
    }
}

