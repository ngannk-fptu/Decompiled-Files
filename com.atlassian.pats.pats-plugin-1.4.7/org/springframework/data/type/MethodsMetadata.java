/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.ClassMetadata
 *  org.springframework.core.type.MethodMetadata
 */
package org.springframework.data.type;

import java.util.Set;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;

public interface MethodsMetadata
extends ClassMetadata {
    public Set<MethodMetadata> getMethods();

    public Set<MethodMetadata> getMethods(String var1);
}

