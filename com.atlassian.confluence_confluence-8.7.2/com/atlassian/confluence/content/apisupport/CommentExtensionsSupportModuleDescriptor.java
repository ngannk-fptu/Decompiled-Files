/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.content.apisupport.CommentExtensionsSupport;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.util.concurrent.ResettableLazyReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class CommentExtensionsSupportModuleDescriptor
extends AbstractModuleDescriptor<CommentExtensionsSupport> {
    private final ResettableLazyReference<CommentExtensionsSupport> moduleRef = new ResettableLazyReference<CommentExtensionsSupport>(){

        protected CommentExtensionsSupport create() throws Exception {
            return (CommentExtensionsSupport)CommentExtensionsSupportModuleDescriptor.this.moduleFactory.createModule(CommentExtensionsSupportModuleDescriptor.this.moduleClassName, (ModuleDescriptor)CommentExtensionsSupportModuleDescriptor.this);
        }
    };

    public CommentExtensionsSupportModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public CommentExtensionsSupport getModule() {
        return (CommentExtensionsSupport)this.moduleRef.get();
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
    }
}

