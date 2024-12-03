/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.impl.plugin.descriptor.search;

import com.atlassian.confluence.impl.plugin.descriptor.search.AbstractLuceneMapperModuleDescriptor;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.plugin.module.ModuleFactory;

public class LuceneQueryMapperModuleDescriptor
extends AbstractLuceneMapperModuleDescriptor<LuceneQueryMapper> {
    public LuceneQueryMapperModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }
}

