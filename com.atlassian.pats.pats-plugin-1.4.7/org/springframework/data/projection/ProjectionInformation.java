/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.projection;

import java.beans.PropertyDescriptor;
import java.util.List;

public interface ProjectionInformation {
    public Class<?> getType();

    public List<PropertyDescriptor> getInputProperties();

    public boolean isClosed();
}

