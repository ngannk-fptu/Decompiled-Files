/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.ByteArrayResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.extra.flyingpdf.config.CustomFontInstalledEvent;
import com.atlassian.confluence.extra.flyingpdf.config.CustomFontRemovedEvent;
import com.atlassian.confluence.extra.flyingpdf.config.FontDao;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class CustomFontClusterEventListener
implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(CustomFontClusterEventListener.class);
    private static final Class[] HANDLED_CLASSES = new Class[]{ClusterEventWrapper.class};
    private FontDao fontDao;

    public void handleEvent(Event event) {
        if (event instanceof ClusterEventWrapper) {
            ClusterEventWrapper clusterEventWrapper = (ClusterEventWrapper)event;
            Event wrappedEvent = clusterEventWrapper.getEvent();
            if (wrappedEvent instanceof CustomFontInstalledEvent) {
                this.handleInstallEvent((CustomFontInstalledEvent)wrappedEvent);
            } else if (wrappedEvent instanceof CustomFontRemovedEvent) {
                this.handleRemoveEvent((CustomFontRemovedEvent)wrappedEvent);
            }
        }
    }

    private void handleRemoveEvent(CustomFontRemovedEvent event) {
        try {
            this.fontDao.removeFont(event.getFontName());
        }
        catch (IOException e) {
            log.error("Unable to remove custom PDF font: " + event.getFontName() + " - " + e.getMessage(), (Throwable)e);
        }
    }

    private void handleInstallEvent(CustomFontInstalledEvent event) {
        ByteArrayResource resource = new ByteArrayResource(event.getFontData());
        try {
            this.fontDao.saveFont(event.getFontName(), (Resource)resource);
        }
        catch (IOException ex) {
            log.error("Unable to install custom PDF font: " + event.getFontName() + " - " + ex.getMessage(), (Throwable)ex);
        }
    }

    public Class[] getHandledEventClasses() {
        return HANDLED_CLASSES;
    }

    public void setPdfExportFontsDirectoryFontDao(FontDao pdfExportFontsDirectoryFontDao) {
        this.fontDao = pdfExportFontsDirectoryFontDao;
    }
}

