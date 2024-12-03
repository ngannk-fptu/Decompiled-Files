/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.SpringProperties
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.UrlResource
 *  org.springframework.core.io.support.PropertiesLoaderUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.context.index;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.core.SpringProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

public final class CandidateComponentsIndexLoader {
    public static final String COMPONENTS_RESOURCE_LOCATION = "META-INF/spring.components";
    public static final String IGNORE_INDEX = "spring.index.ignore";
    private static final boolean shouldIgnoreIndex = SpringProperties.getFlag((String)"spring.index.ignore");
    private static final Log logger = LogFactory.getLog(CandidateComponentsIndexLoader.class);
    private static final ConcurrentMap<ClassLoader, CandidateComponentsIndex> cache = new ConcurrentReferenceHashMap();

    private CandidateComponentsIndexLoader() {
    }

    @Nullable
    public static CandidateComponentsIndex loadIndex(@Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = CandidateComponentsIndexLoader.class.getClassLoader();
        }
        return cache.computeIfAbsent(classLoaderToUse, CandidateComponentsIndexLoader::doLoadIndex);
    }

    @Nullable
    private static CandidateComponentsIndex doLoadIndex(ClassLoader classLoader) {
        if (shouldIgnoreIndex) {
            return null;
        }
        try {
            int totalCount;
            Enumeration<URL> urls = classLoader.getResources(COMPONENTS_RESOURCE_LOCATION);
            if (!urls.hasMoreElements()) {
                return null;
            }
            ArrayList<Properties> result = new ArrayList<Properties>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = PropertiesLoaderUtils.loadProperties((Resource)new UrlResource(url));
                result.add(properties);
            }
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Loaded " + result.size() + " index(es)"));
            }
            return (totalCount = result.stream().mapToInt(Hashtable::size).sum()) > 0 ? new CandidateComponentsIndex(result) : null;
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unable to load indexes from location [META-INF/spring.components]", ex);
        }
    }
}

