/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import java.util.Collection;

public interface Extractor2 {
    public StringBuilder extractText(Object var1);

    public Collection<FieldDescriptor> extractFields(Object var1);
}

