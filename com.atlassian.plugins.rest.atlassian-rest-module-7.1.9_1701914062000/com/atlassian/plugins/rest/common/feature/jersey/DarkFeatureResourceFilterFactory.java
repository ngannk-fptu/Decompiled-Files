/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.feature.jersey;

import com.atlassian.plugins.rest.common.feature.RequiresDarkFeature;
import com.atlassian.plugins.rest.common.feature.jersey.DarkFeatureResourceFilter;
import com.atlassian.plugins.rest.common.util.ReflectionUtils;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DarkFeatureResourceFilterFactory
implements ResourceFilterFactory {
    private static final Logger log = LoggerFactory.getLogger(DarkFeatureResourceFilterFactory.class);
    private final DarkFeatureManager darkFeatureManager;

    public DarkFeatureResourceFilterFactory(@Nonnull DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        if (ReflectionUtils.getAnnotation(RequiresDarkFeature.class, am) != null || ReflectionUtils.getAnnotation(RequiresDarkFeature.class, am.getResource()) != null) {
            log.debug("RequiresDarkFeature annotation found - creating filter");
            return Collections.singletonList(new DarkFeatureResourceFilter(am, this.darkFeatureManager));
        }
        log.debug("No RequiresDarkFeature annotation found - not creating filter");
        return Collections.emptyList();
    }
}

