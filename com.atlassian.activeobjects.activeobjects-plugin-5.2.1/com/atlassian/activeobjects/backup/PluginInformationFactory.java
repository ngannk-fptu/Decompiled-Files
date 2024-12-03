/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.PluginInformation
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.admin.PluginInfo;
import com.atlassian.activeobjects.admin.PluginToTablesMapping;
import com.atlassian.activeobjects.plugin.ActiveObjectModuleDescriptor;
import com.atlassian.activeobjects.spi.PluginInformation;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.google.common.base.Preconditions;
import java.util.Collection;

public final class PluginInformationFactory {
    private final PluginToTablesMapping pluginToTablesMapping;
    private final PluginAccessor pluginAccessor;

    public PluginInformationFactory(PluginToTablesMapping pluginToTablesMapping, PluginAccessor pluginAccessor) {
        this.pluginToTablesMapping = (PluginToTablesMapping)Preconditions.checkNotNull((Object)pluginToTablesMapping);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
    }

    public PluginInformation getPluginInformation(String tableName) {
        if (tableName == null) {
            return new NotAvailablePluginInformation();
        }
        PluginInfo pluginInfo = this.pluginToTablesMapping.get(tableName);
        if (pluginInfo != null) {
            return new AvailablePluginInformation(pluginInfo);
        }
        ActiveObjectModuleDescriptor aomd = this.getModuleDescriptor(tableName);
        if (aomd != null) {
            return new AvailablePluginInformation(aomd.getPlugin());
        }
        return new NotAvailablePluginInformation();
    }

    private ActiveObjectModuleDescriptor getModuleDescriptor(String tableName) {
        Collection<ModuleDescriptor<Object>> moduleDescriptors = this.findModuleDescriptors(tableName);
        return moduleDescriptors.isEmpty() ? null : (ActiveObjectModuleDescriptor)moduleDescriptors.iterator().next();
    }

    private Collection<ModuleDescriptor<Object>> findModuleDescriptors(final String tableName) {
        return this.pluginAccessor.getModuleDescriptors((ModuleDescriptorPredicate)new ModuleDescriptorPredicate<Object>(){

            public boolean matches(ModuleDescriptor<? extends Object> moduleDescriptor) {
                return moduleDescriptor instanceof ActiveObjectModuleDescriptor && ((ActiveObjectModuleDescriptor)moduleDescriptor).getConfiguration().getTableNamePrefix().isStarting(tableName, false);
            }
        });
    }

    private static final class AvailablePluginInformation
    implements PluginInformation {
        private final String name;
        private final String key;
        private final String version;
        private final String vendorName;
        private final String vendorUrl;

        public AvailablePluginInformation(Plugin plugin) {
            this(((Plugin)Preconditions.checkNotNull((Object)plugin)).getName(), plugin.getKey(), plugin.getPluginInformation().getVersion(), plugin.getPluginInformation().getVendorName(), plugin.getPluginInformation().getVendorUrl());
        }

        public AvailablePluginInformation(PluginInfo pluginInfo) {
            this(((PluginInfo)Preconditions.checkNotNull((Object)pluginInfo)).name, pluginInfo.key, pluginInfo.version, pluginInfo.vendorName, pluginInfo.vendorUrl);
        }

        private AvailablePluginInformation(String name, String key, String version, String vendorName, String vendorUrl) {
            this.name = name;
            this.key = key;
            this.version = version;
            this.vendorName = vendorName;
            this.vendorUrl = vendorUrl;
        }

        public boolean isAvailable() {
            return true;
        }

        public String getPluginName() {
            return this.name;
        }

        public String getPluginKey() {
            return this.key;
        }

        public String getPluginVersion() {
            return this.version;
        }

        public String getVendorName() {
            return this.vendorName;
        }

        public String getVendorUrl() {
            return this.vendorUrl;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AvailablePluginInformation that = (AvailablePluginInformation)o;
            if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
                return false;
            }
            if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
                return false;
            }
            if (this.vendorName != null ? !this.vendorName.equals(that.vendorName) : that.vendorName != null) {
                return false;
            }
            if (this.vendorUrl != null ? !this.vendorUrl.equals(that.vendorUrl) : that.vendorUrl != null) {
                return false;
            }
            return !(this.version != null ? !this.version.equals(that.version) : that.version != null);
        }

        public int hashCode() {
            int result = this.name != null ? this.name.hashCode() : 1;
            result = 31 * result + (this.key != null ? this.key.hashCode() : 1);
            result = 31 * result + (this.version != null ? this.version.hashCode() : 1);
            result = 31 * result + (this.vendorName != null ? this.vendorName.hashCode() : 1);
            result = 31 * result + (this.vendorUrl != null ? this.vendorUrl.hashCode() : 1);
            return result;
        }

        public String toString() {
            return "plugin " + this.getPluginName() + "(" + this.getPluginKey() + ") #" + this.getPluginVersion();
        }
    }

    private static final class NotAvailablePluginInformation
    implements PluginInformation {
        private NotAvailablePluginInformation() {
        }

        public boolean isAvailable() {
            return false;
        }

        public String getPluginName() {
            return null;
        }

        public String getPluginKey() {
            return null;
        }

        public String getPluginVersion() {
            return null;
        }

        public String getVendorName() {
            return null;
        }

        public String getVendorUrl() {
            return null;
        }

        public boolean equals(Object o) {
            return o != null && o instanceof NotAvailablePluginInformation;
        }

        public int hashCode() {
            return 0;
        }

        public String toString() {
            return "<unknown plugin>";
        }
    }
}

