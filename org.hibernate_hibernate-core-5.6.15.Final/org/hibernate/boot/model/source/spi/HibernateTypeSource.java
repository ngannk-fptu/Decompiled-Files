/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Map;
import org.hibernate.boot.model.JavaTypeDescriptor;

public interface HibernateTypeSource {
    public String getName();

    public Map<String, String> getParameters();

    public JavaTypeDescriptor getJavaType();
}

