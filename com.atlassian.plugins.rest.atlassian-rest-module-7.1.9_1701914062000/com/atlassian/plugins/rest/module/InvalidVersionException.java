/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugins.rest.module.RestModuleDescriptor;

public class InvalidVersionException
extends RuntimeException {
    private final String invalidVersion;
    private final Plugin plugin;
    private final RestModuleDescriptor moduleDescriptor;

    InvalidVersionException(String invalidVersion) {
        this(null, null, invalidVersion);
    }

    InvalidVersionException(Plugin plugin, RestModuleDescriptor moduleDescriptor, InvalidVersionException e) {
        this(plugin, moduleDescriptor, e.getInvalidVersion());
    }

    private InvalidVersionException(Plugin plugin, RestModuleDescriptor moduleDescriptor, String invalidVersion) {
        this.plugin = plugin;
        this.moduleDescriptor = moduleDescriptor;
        this.invalidVersion = invalidVersion;
    }

    public String getInvalidVersion() {
        return this.invalidVersion;
    }

    @Override
    public String getMessage() {
        if (this.invalidVersion == null) {
            return "The REST module descriptor '" + (Object)((Object)this.moduleDescriptor) + "'defined by plugin '" + this.plugin + "' doesn't specify a version, this is a required attribute. Please sepcify a version in the format 'major[.minor][.micro][.classifier]'";
        }
        return "The version (" + this.invalidVersion + ")set on the REST module descriptor '" + (Object)((Object)this.moduleDescriptor) + "' of plugin '" + this.plugin + "' is not valid. It must follow the following pattern 'major[.minor][.micro][.classifier]'";
    }
}

