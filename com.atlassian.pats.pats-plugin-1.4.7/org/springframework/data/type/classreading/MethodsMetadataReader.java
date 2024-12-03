/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.classreading.MetadataReader
 */
package org.springframework.data.type.classreading;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.data.type.MethodsMetadata;

public interface MethodsMetadataReader
extends MetadataReader {
    public MethodsMetadata getMethodsMetadata();
}

