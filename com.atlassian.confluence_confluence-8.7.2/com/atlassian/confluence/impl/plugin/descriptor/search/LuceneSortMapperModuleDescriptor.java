/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.impl.plugin.descriptor.search;

import com.atlassian.confluence.impl.plugin.descriptor.search.AbstractLuceneMapperModuleDescriptor;
import com.atlassian.confluence.impl.search.v2.mappers.CaseInsensitiveSortMapper;
import com.atlassian.confluence.impl.search.v2.mappers.DefaultSortMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.plugin.module.DefaultPluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class LuceneSortMapperModuleDescriptor
extends AbstractLuceneMapperModuleDescriptor<LuceneSortMapper> {
    private String sortField;
    private String type;

    public LuceneSortMapperModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.sortField = element.attributeValue("sortField");
        this.type = element.attributeValue("type");
    }

    @Override
    protected PluginModuleFactory<LuceneSortMapper> getModuleFactory() {
        return new DefaultPluginModuleFactory<LuceneSortMapper>((ModuleDescriptor)this){

            @Override
            public LuceneSortMapper createModule() {
                if (DefaultSortMapper.class.equals((Object)LuceneSortMapperModuleDescriptor.this.getModuleClass())) {
                    return new DefaultSortMapper(LuceneSortMapperModuleDescriptor.this.sortField, LuceneSortMapperModuleDescriptor.this.type);
                }
                if (CaseInsensitiveSortMapper.class.equals((Object)LuceneSortMapperModuleDescriptor.this.getModuleClass())) {
                    return new CaseInsensitiveSortMapper(LuceneSortMapperModuleDescriptor.this.sortField);
                }
                return (LuceneSortMapper)super.createModule();
            }
        };
    }
}

