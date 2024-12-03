/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import javax.validation.metadata.CascadableDescriptor;
import javax.validation.metadata.ContainerDescriptor;
import javax.validation.metadata.ElementDescriptor;

public interface ParameterDescriptor
extends ElementDescriptor,
CascadableDescriptor,
ContainerDescriptor {
    public int getIndex();

    public String getName();
}

