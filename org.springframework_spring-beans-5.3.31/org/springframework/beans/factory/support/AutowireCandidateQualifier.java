/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.util.Assert;

public class AutowireCandidateQualifier
extends BeanMetadataAttributeAccessor {
    public static final String VALUE_KEY = "value";
    private final String typeName;

    public AutowireCandidateQualifier(Class<?> type) {
        this(type.getName());
    }

    public AutowireCandidateQualifier(String typeName) {
        Assert.notNull((Object)typeName, (String)"Type name must not be null");
        this.typeName = typeName;
    }

    public AutowireCandidateQualifier(Class<?> type, Object value) {
        this(type.getName(), value);
    }

    public AutowireCandidateQualifier(String typeName, Object value) {
        Assert.notNull((Object)typeName, (String)"Type name must not be null");
        this.typeName = typeName;
        this.setAttribute(VALUE_KEY, value);
    }

    public String getTypeName() {
        return this.typeName;
    }
}

