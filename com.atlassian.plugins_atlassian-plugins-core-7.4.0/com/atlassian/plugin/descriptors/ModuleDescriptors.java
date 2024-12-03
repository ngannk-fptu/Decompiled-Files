/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.apache.commons.lang3.builder.EqualsBuilder
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import java.util.Objects;
import javax.annotation.concurrent.NotThreadSafe;

public final class ModuleDescriptors {

    @NotThreadSafe
    public static class HashCodeBuilder {
        private ModuleDescriptor descriptor;

        public HashCodeBuilder descriptor(ModuleDescriptor descriptor) {
            Objects.requireNonNull(descriptor, "Tried to calculate the hash code of a null module descriptor.");
            this.descriptor = descriptor;
            return this;
        }

        public int toHashCode() {
            Objects.requireNonNull(this.descriptor, "Tried to calculate the hash code of a null module descriptor.");
            return this.descriptor.getCompleteKey() == null ? 0 : this.descriptor.getCompleteKey().hashCode();
        }

        public int hashCode() {
            return this.toHashCode();
        }
    }

    @NotThreadSafe
    public static class EqualsBuilder {
        private ModuleDescriptor descriptor;

        public EqualsBuilder descriptor(ModuleDescriptor descriptor) {
            Objects.requireNonNull(descriptor, "Tried to build an equals implementation for a null module descriptor. This is not allowed.");
            this.descriptor = descriptor;
            return this;
        }

        public boolean isEqualTo(Object obj) {
            Objects.requireNonNull(this.descriptor, "Tried to build an equals implementation for a null module descriptor. This is not allowed.");
            if (this.descriptor == obj) {
                return true;
            }
            if (!(obj instanceof ModuleDescriptor)) {
                return false;
            }
            ModuleDescriptor rhs = (ModuleDescriptor)obj;
            return new org.apache.commons.lang3.builder.EqualsBuilder().append((Object)this.descriptor.getCompleteKey(), (Object)rhs.getCompleteKey()).isEquals();
        }
    }
}

