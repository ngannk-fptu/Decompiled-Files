/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.Map;
import org.hibernate.boot.jaxb.hbm.spi.TypeContainer;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.JavaTypeDescriptorResolvable;

public class HibernateTypeSourceImpl
implements HibernateTypeSource,
JavaTypeDescriptorResolvable {
    private final String name;
    private final Map<String, String> parameters;
    private JavaTypeDescriptor javaTypeDescriptor;

    public HibernateTypeSourceImpl(TypeContainer typeContainer) {
        if (typeContainer.getTypeAttribute() != null) {
            this.name = typeContainer.getTypeAttribute();
            this.parameters = null;
        } else if (typeContainer.getType() != null) {
            this.name = typeContainer.getType().getName();
            this.parameters = Helper.extractParameters(typeContainer.getType().getConfigParameters());
        } else {
            this.name = null;
            this.parameters = null;
        }
    }

    public HibernateTypeSourceImpl(String name) {
        this(name, Collections.emptyMap());
    }

    public HibernateTypeSourceImpl(String name, Map<String, String> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public HibernateTypeSourceImpl(JavaTypeDescriptor javaTypeDescriptor) {
        this(null, javaTypeDescriptor);
    }

    public HibernateTypeSourceImpl(String name, JavaTypeDescriptor javaTypeDescriptor) {
        this(name);
        this.javaTypeDescriptor = javaTypeDescriptor;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Override
    public JavaTypeDescriptor getJavaType() {
        return this.javaTypeDescriptor;
    }

    @Override
    public void resolveJavaTypeDescriptor(JavaTypeDescriptor descriptor) {
        if (this.javaTypeDescriptor != null && this.javaTypeDescriptor != descriptor) {
            throw new IllegalStateException("Attempt to resolve an already resolved JavaTypeDescriptor");
        }
        this.javaTypeDescriptor = descriptor;
    }
}

