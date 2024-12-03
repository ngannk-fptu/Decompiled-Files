/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.impl.plugin.descriptor.search.AbstractLuceneMapperModuleDescriptor;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneMapperClassPredicate;
import com.atlassian.plugin.ModuleDescriptor;

@Deprecated
public class LuceneMapperPredicate
extends LuceneMapperClassPredicate {
    private final String queryKey;

    public LuceneMapperPredicate(Class<? extends AbstractLuceneMapperModuleDescriptor> moduleClass, String queryKey) {
        super(moduleClass);
        this.queryKey = queryKey;
    }

    @Override
    public boolean matches(ModuleDescriptor moduleDescriptor) {
        return super.matches(moduleDescriptor) && ((AbstractLuceneMapperModuleDescriptor)moduleDescriptor).handles(this.queryKey);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        LuceneMapperPredicate that = (LuceneMapperPredicate)o;
        return !(this.queryKey != null ? !this.queryKey.equals(that.queryKey) : that.queryKey != null);
    }

    @Override
    public int hashCode() {
        int code = super.hashCode();
        return code + (this.queryKey != null ? this.queryKey.hashCode() : 0);
    }
}

