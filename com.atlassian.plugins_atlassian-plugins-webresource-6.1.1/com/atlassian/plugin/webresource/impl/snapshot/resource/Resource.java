/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.ContentProviderStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;

public class Resource {
    private static final Set<String> HTTP_PARAM_NAMES_SET = new HashSet<String>(Arrays.asList(Config.HTTP_PARAM_NAMES));
    @VisibleForTesting
    final StreamStrategy streamStrategy;
    private final Bundle parent;
    private final String nameType;
    private final String locationType;
    private final ResourceLocation resourceLocation;
    private final ContentTypeStrategy contentTypeStrategy;
    private final PathStrategy pathStrategy;
    private final ContentProviderStrategy contentProviderStrategy;

    Resource(Bundle parent, ResourceLocation resourceLocation, String nameType, String locationType, ContentTypeStrategy contentTypeStrategy, StreamStrategy streamStrategy, PathStrategy pathStrategy, ContentProviderStrategy contentProviderStrategy) {
        this.parent = parent;
        this.nameType = nameType;
        this.locationType = locationType;
        this.resourceLocation = Resource.sanitizeResourceLocation(resourceLocation);
        this.contentTypeStrategy = contentTypeStrategy;
        this.streamStrategy = streamStrategy;
        this.pathStrategy = pathStrategy;
        this.contentProviderStrategy = contentProviderStrategy;
    }

    public static Map<String, String> getUrlParamsStatic(Map<String, String> params) {
        HashMap<String, String> urlParams = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().equals("allow-public-use") && entry.getValue().equals(Boolean.FALSE.toString()) || !HTTP_PARAM_NAMES_SET.contains(entry.getKey())) continue;
            urlParams.put(entry.getKey(), entry.getValue());
        }
        return urlParams;
    }

    public static boolean isCacheableStatic(Map<String, String> params) {
        return !"false".equalsIgnoreCase(params.get("cache"));
    }

    public static String getPrebuiltSourcePath(String resourceName) {
        String fileExtension = Files.getFileExtension((String)resourceName);
        String baseName = resourceName.substring(0, resourceName.length() - fileExtension.length() - 1);
        return baseName + "-source." + fileExtension;
    }

    public static boolean isPrebuiltSourceName(String name) {
        return name.contains("-source.");
    }

    public static String getResourceNameFromPrebuiltSourceName(String prebuiltSourcePath) {
        return prebuiltSourcePath.replace("-source.", ".");
    }

    static ResourceLocation sanitizeResourceLocation(ResourceLocation res) {
        boolean forIeOnly = BooleanUtils.toBoolean((String)((String)res.getParams().get("ieonly")));
        if (!forIeOnly) {
            Map newParams = Maps.filterEntries((Map)res.getParams(), input -> !"ieonly".equals(input.getKey()));
            res = new ResourceLocation(res.getLocation(), res.getName(), res.getType(), res.getContentType(), res.getContent(), (Map)ImmutableMap.copyOf((Map)newParams));
        }
        return res;
    }

    public Bundle getParent() {
        return this.parent;
    }

    public String getName() {
        return this.resourceLocation.getName();
    }

    public String getFullName() {
        return this.getParent().getKey() + "/" + this.resourceLocation.getName();
    }

    public String getLocation() {
        return this.resourceLocation.getLocation();
    }

    public boolean isRedirect() {
        return "webContext".equalsIgnoreCase(this.resourceLocation.getParameter("source"));
    }

    public String getNameType() {
        return this.nameType;
    }

    public String getNameOrLocationType() {
        return this.nameType.isEmpty() ? this.locationType : this.nameType;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public String getContentType() {
        return this.contentTypeStrategy.getContentType();
    }

    public boolean isBatchable() {
        return !this.isRedirect() && !"false".equalsIgnoreCase(this.resourceLocation.getParameter("batch"));
    }

    public InputStream getStreamFor(String path) {
        return this.streamStrategy.getInputStream(path);
    }

    public String getPath() {
        return this.pathStrategy.getPath();
    }

    public boolean isBatchable(Map<String, String> batchParams) {
        if (!this.isBatchable()) {
            return false;
        }
        for (String key : Config.HTTP_PARAM_NAMES) {
            if (Support.equals(batchParams.get(key), this.getParams().get(key))) continue;
            return false;
        }
        return true;
    }

    public Map<String, String> getParams() {
        return this.resourceLocation.getParams();
    }

    public Map<String, String> getUrlParams() {
        return Resource.getUrlParamsStatic(this.getParams());
    }

    public boolean isTransformable() {
        return this.parent.isTransformable();
    }

    public String getVersion() {
        return this.parent.getVersion();
    }

    public String getKey() {
        return this.parent.getKey();
    }

    public boolean isCacheable() {
        return Resource.isCacheableStatic(this.getUrlParams());
    }

    public Content getContent() {
        return this.contentProviderStrategy.getContent();
    }

    @Deprecated
    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Resource resource = (Resource)o;
        return this.parent.equals(resource.parent) && this.getName().equals(resource.getName());
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.parent, this.getName()});
    }

    public String toString() {
        return "{" + this.getName() + (!this.isBatchable() ? " isNotBatchable" : "") + "}";
    }
}

