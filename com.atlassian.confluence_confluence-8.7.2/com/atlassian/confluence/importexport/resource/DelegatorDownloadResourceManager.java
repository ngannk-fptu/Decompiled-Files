/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceManager;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatorDownloadResourceManager
implements PartialDownloadResourceManager {
    private static final Logger log = LoggerFactory.getLogger(DelegatorDownloadResourceManager.class);
    private List<DownloadResourceManager> downloadResourceManagers;

    @Override
    public boolean matches(String resourcePath) {
        return !this.managersForResource(resourcePath).isEmpty();
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws DownloadResourceNotFoundException, UnauthorizedDownloadResourceException {
        List<DownloadResourceManager> matchedManagers = this.managersForResource(resourcePath);
        if (matchedManagers.isEmpty()) {
            return null;
        }
        return matchedManagers.get(0).getResourceReader(userName, resourcePath, parameters);
    }

    @Override
    public PartialDownloadResourceReader getPartialResourceReader(String userName, String resourcePath, Map parameters, String requestRange) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException, RangeNotSatisfiableException {
        List matchedManagers = this.managersForResource(resourcePath).stream().filter(manager -> manager instanceof PartialDownloadResourceManager).collect(Collectors.toList());
        if (matchedManagers.isEmpty()) {
            return null;
        }
        return ((PartialDownloadResourceManager)matchedManagers.get(0)).getPartialResourceReader(userName, resourcePath, parameters, Objects.requireNonNull(requestRange));
    }

    public void setDownloadResourceManagers(List<DownloadResourceManager> downloadResourceManagers) {
        this.downloadResourceManagers = downloadResourceManagers;
    }

    private List<DownloadResourceManager> managersForResource(String resourcePath) {
        return this.downloadResourceManagers.stream().filter(manager -> manager.matches(resourcePath) || manager.matches(resourcePath.toLowerCase())).collect(Collectors.toList());
    }
}

