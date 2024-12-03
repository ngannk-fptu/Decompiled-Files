/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.resource.repository;

import aQute.bnd.service.repository.SearchableRepository;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResourceDescriptorImpl
extends SearchableRepository.ResourceDescriptor
implements Comparable<ResourceDescriptorImpl> {
    public Set<String> repositories = new HashSet<String>();

    public ResourceDescriptorImpl() {
    }

    public ResourceDescriptorImpl(SearchableRepository.ResourceDescriptor ref) throws IllegalAccessException {
        for (Field f : ref.getClass().getFields()) {
            f.set(this, f.get(ref));
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.id);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ResourceDescriptorImpl other = (ResourceDescriptorImpl)obj;
        return Arrays.equals(this.id, other.id);
    }

    @Override
    public int compareTo(ResourceDescriptorImpl var0) {
        for (int i = 0; i < this.id.length; ++i) {
            if (i >= var0.id.length) {
                return 1;
            }
            if (this.id[i] > var0.id[i]) {
                return 1;
            }
            if (this.id[i] >= var0.id[i]) continue;
            return -1;
        }
        if (var0.id.length > this.id.length) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return this.bsn + "-" + this.version;
    }
}

