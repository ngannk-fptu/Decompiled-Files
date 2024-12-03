/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Optional
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.util.ClassUtils
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.troubleshooting.stp.properties.appenders.CrowdDirectorySupportData;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

@Deprecated
public class OptionalCrowdDirectorySupportInfo
extends RootLevelSupportDataAppender
implements ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(OptionalCrowdDirectorySupportInfo.class);
    private static final String CROWD_DIRECTORY_SERVICE_CLASS_NAME = "com.atlassian.crowd.embedded.api.CrowdDirectoryService";
    private Optional<CrowdDirectorySupportData> delegate = Optional.absent();

    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        if (this.delegate.isPresent()) {
            ((CrowdDirectorySupportData)this.delegate.get()).addSupportData(builder);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.delegate = Optional.absent();
        if (this.isCrowdDirectoryServiceClassPresent()) {
            Map crowdDirectoryServices = applicationContext.getBeansOfType(CrowdDirectoryService.class);
            switch (crowdDirectoryServices.size()) {
                case 0: {
                    LOG.debug("No beans of type {} found in context. Support zip will not contain info about user directories.", (Object)CROWD_DIRECTORY_SERVICE_CLASS_NAME);
                    break;
                }
                case 1: {
                    this.delegate = Optional.of((Object)new CrowdDirectorySupportData((CrowdDirectoryService)Iterables.getOnlyElement(crowdDirectoryServices.values())));
                    break;
                }
                default: {
                    throw new IllegalStateException("Expected a singleton CrowdDirectoryService, got " + crowdDirectoryServices);
                }
            }
        }
    }

    @VisibleForTesting
    boolean isCrowdDirectoryServiceClassPresent() {
        return ClassUtils.isPresent((String)CROWD_DIRECTORY_SERVICE_CLASS_NAME, (ClassLoader)this.getClass().getClassLoader());
    }
}

