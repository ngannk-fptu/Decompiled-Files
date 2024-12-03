/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

@Deprecated
@Internal
public class EhCacheManagementConfig {
    static final boolean DEFAULT_REPORT_SIZE_IN_BYTES = false;
    private final Map<String, Boolean> reportBytesLocalHeap;

    private EhCacheManagementConfig(Map<String, Boolean> reportBytesLocalHeap) {
        this.reportBytesLocalHeap = reportBytesLocalHeap;
    }

    private static Map<String, Boolean> parseManagementConfig(Document configDoc) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        List cacheElements = configDoc.getRootElement().elements("cache");
        for (Element cacheElement : cacheElements) {
            String cacheName = cacheElement.attributeValue("name");
            String reportBytesLocalHeap = cacheElement.attributeValue("reportBytesLocalHeap");
            if (StringUtils.trimToNull((String)reportBytesLocalHeap) == null) continue;
            builder.put((Object)cacheName, (Object)Boolean.valueOf(reportBytesLocalHeap));
        }
        return builder.build();
    }

    public static EhCacheManagementConfig loadDefaultManagementConfig() throws DocumentException {
        URL managementConfigLocation = ClassLoaderUtils.getResource((String)"ehcache-management.xml", EhCacheManagementConfig.class);
        return EhCacheManagementConfig.loadManagementConfig(managementConfigLocation);
    }

    static EhCacheManagementConfig loadManagementConfig(URL managementConfigLocation) throws DocumentException {
        Preconditions.checkNotNull((Object)managementConfigLocation, (Object)"Cannot locate ehcache management config");
        Document read = new SAXReader().read(managementConfigLocation);
        return new EhCacheManagementConfig(EhCacheManagementConfig.parseManagementConfig(read));
    }

    public boolean reportBytesLocalHeap(String cacheName) {
        if (this.reportBytesLocalHeap.containsKey(cacheName)) {
            return this.reportBytesLocalHeap.get(cacheName);
        }
        return false;
    }
}

