/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.applinks.core.plugin;

import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.plugin.AbstractAppLinksTypeModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class EntityTypeModuleDescriptor
extends AbstractAppLinksTypeModuleDescriptor<EntityType> {
    public EntityTypeModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }
}

