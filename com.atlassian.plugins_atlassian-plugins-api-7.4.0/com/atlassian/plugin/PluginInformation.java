/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.CopyOnWriteMap
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginPermission;
import com.atlassian.plugin.util.JavaVersionUtils;
import io.atlassian.util.concurrent.CopyOnWriteMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PluginInformation {
    private String description = "";
    private String descriptionKey;
    private String version = "0.0";
    private String vendorName = "(unknown)";
    private String vendorUrl;
    private Optional<String> scopeKey;
    private Float minJavaVersion;
    private Set<PluginPermission> permissions = Collections.emptySet();
    private final Map<String, String> parameters = CopyOnWriteMap.builder().stableViews().newHashMap();
    private String startup;
    private Set<String> moduleScanFolders;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setVendorUrl(String vendorUrl) {
        this.vendorUrl = vendorUrl;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public String getVendorUrl() {
        return this.vendorUrl;
    }

    public void setScopeKey(Optional<String> scopeKey) {
        this.scopeKey = scopeKey;
    }

    public Optional<String> getScopeKey() {
        return this.scopeKey;
    }

    public Float getMinJavaVersion() {
        return this.minJavaVersion;
    }

    public void setMinJavaVersion(Float minJavaVersion) {
        this.minJavaVersion = minJavaVersion;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }

    public Set<PluginPermission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<PluginPermission> permissions) {
        this.permissions = Collections.unmodifiableSet(new HashSet<PluginPermission>(permissions));
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public boolean satisfiesMinJavaVersion() {
        return this.minJavaVersion == null || JavaVersionUtils.satisfiesMinVersion(this.minJavaVersion.floatValue());
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public String getDescriptionKey() {
        return this.descriptionKey;
    }

    public String getStartup() {
        return this.startup;
    }

    public void setStartup(String startup) {
        this.startup = startup;
    }

    public Set<String> getModuleScanFolders() {
        return this.moduleScanFolders;
    }

    public void setModuleScanFolders(Iterable<String> moduleScanFolders) {
        this.moduleScanFolders = Collections.unmodifiableSet(StreamSupport.stream(moduleScanFolders.spliterator(), false).collect(Collectors.toSet()));
    }
}

