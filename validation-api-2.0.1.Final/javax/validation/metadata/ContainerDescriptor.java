/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import java.util.Set;
import javax.validation.metadata.ContainerElementTypeDescriptor;

public interface ContainerDescriptor {
    public Set<ContainerElementTypeDescriptor> getConstrainedContainerElementTypes();
}

