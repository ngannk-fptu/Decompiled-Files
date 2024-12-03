/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.HashRegistryCache
 *  com.atlassian.modzdetector.Modifications
 *  com.atlassian.modzdetector.ModzRegistryException
 *  com.atlassian.sal.api.component.ComponentLocator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.pup;

import com.atlassian.confluence.compat.struts2.servletactioncontext.ServletActionContextCompatManager;
import com.atlassian.confluence.status.service.HashRegistryCache;
import com.atlassian.modzdetector.Modifications;
import com.atlassian.modzdetector.ModzRegistryException;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.troubleshooting.confluence.pup.ServletContextConfig;
import com.atlassian.troubleshooting.preupgrade.modz.Modification;
import com.atlassian.troubleshooting.preupgrade.modz.ModzDetection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceModzDetection
implements ModzDetection {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluenceModzDetection.class);
    private static final String RELATIVE_PATH = "confluence/WEB-INF/classes";
    private final HashRegistryCache hashRegistryCache;
    private final ServletContextConfig contextConfig;
    private final ServletActionContextCompatManager servletActionContextManagerCompat;

    @Autowired
    ConfluenceModzDetection(ServletContextConfig contextConfig, ServletActionContextCompatManager servletActionContextManagerCompat) {
        this.contextConfig = Objects.requireNonNull(contextConfig);
        this.servletActionContextManagerCompat = Objects.requireNonNull(servletActionContextManagerCompat);
        this.hashRegistryCache = (HashRegistryCache)ComponentLocator.getComponent(HashRegistryCache.class);
    }

    @Override
    public List<Modification> getModifiedFiles() {
        return this.convertModifications(value -> value.modifiedFiles);
    }

    @Override
    public List<Modification> getRemovedFiles() {
        return this.convertModifications(value -> value.removedFiles);
    }

    private List<Modification> convertModifications(Function<Modifications, List<String>> mapper) {
        return this.getModifications().map(mapper).map(files -> files.stream().map(file -> new Modification((String)file, RELATIVE_PATH)).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Modifications> getModifications() {
        try {
            this.populateServletActionContext();
            return Optional.of(this.hashRegistryCache.getModifications());
        }
        catch (ModzRegistryException e) {
            LOG.error("Error checking for modified files", (Throwable)e);
            return Optional.empty();
        }
    }

    private void populateServletActionContext() {
        if (this.servletActionContextManagerCompat.getServletConfig() == null || this.servletActionContextManagerCompat.getServletConfig().getServletContext() == null) {
            this.servletActionContextManagerCompat.setServletConfig(this.contextConfig);
        }
    }
}

