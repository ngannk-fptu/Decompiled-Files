/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PermissionOption
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.crowd.embedded.admin.plugin;

import com.atlassian.crowd.embedded.api.PermissionOption;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class SupportedDirectoryPermissionOptionsModuleDescriptor
extends AbstractModuleDescriptor<Set<PermissionOption>> {
    private static final String READ_ONLY = "read-only";
    private static final String READ_ONLY_LOCAL_GROUPS = "read-only-local-groups";
    private static final String READ_WRITE = "read-write";
    private Set<PermissionOption> allowedPermissionOptions;

    public SupportedDirectoryPermissionOptionsModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        HashSet<PermissionOption> opts = new HashSet<PermissionOption>();
        if (this.hasChild(element, READ_ONLY)) {
            opts.add(PermissionOption.READ_ONLY);
        }
        if (this.hasChild(element, READ_ONLY_LOCAL_GROUPS)) {
            opts.add(PermissionOption.READ_ONLY_LOCAL_GROUPS);
        }
        if (this.hasChild(element, READ_WRITE)) {
            opts.add(PermissionOption.READ_WRITE);
        }
        this.allowedPermissionOptions = EnumSet.copyOf(opts);
    }

    public Set<PermissionOption> getModule() {
        return this.allowedPermissionOptions;
    }

    private boolean hasChild(Element element, String childName) {
        return element.element(childName) != null;
    }
}

