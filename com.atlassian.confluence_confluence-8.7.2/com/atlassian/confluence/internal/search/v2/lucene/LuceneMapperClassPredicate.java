/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.impl.plugin.descriptor.search.AbstractLuceneMapperModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;

@Deprecated
public class LuceneMapperClassPredicate
implements ModuleDescriptorPredicate {
    private final Class<? extends AbstractLuceneMapperModuleDescriptor> moduleClass;

    public LuceneMapperClassPredicate(Class<? extends AbstractLuceneMapperModuleDescriptor> moduleClass) {
        this.moduleClass = moduleClass;
    }

    public boolean matches(ModuleDescriptor moduleDescriptor) {
        return this.moduleClass.isAssignableFrom(moduleDescriptor.getClass());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LuceneMapperClassPredicate that = (LuceneMapperClassPredicate)o;
        return that.moduleClass.equals(this.moduleClass);
    }

    public int hashCode() {
        return this.moduleClass.hashCode();
    }
}

