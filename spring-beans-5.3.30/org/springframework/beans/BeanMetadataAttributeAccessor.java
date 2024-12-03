/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.AttributeAccessorSupport
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.core.AttributeAccessorSupport;
import org.springframework.lang.Nullable;

public class BeanMetadataAttributeAccessor
extends AttributeAccessorSupport
implements BeanMetadataElement {
    @Nullable
    private Object source;

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void addMetadataAttribute(BeanMetadataAttribute attribute) {
        super.setAttribute(attribute.getName(), (Object)attribute);
    }

    @Nullable
    public BeanMetadataAttribute getMetadataAttribute(String name) {
        return (BeanMetadataAttribute)super.getAttribute(name);
    }

    public void setAttribute(String name, @Nullable Object value) {
        super.setAttribute(name, (Object)new BeanMetadataAttribute(name, value));
    }

    @Nullable
    public Object getAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute)super.getAttribute(name);
        return attribute != null ? attribute.getValue() : null;
    }

    @Nullable
    public Object removeAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute)super.removeAttribute(name);
        return attribute != null ? attribute.getValue() : null;
    }
}

