/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;

public interface CascadableDescriptor {
    public boolean isCascaded();

    public Set<GroupConversionDescriptor> getGroupConversions();
}

