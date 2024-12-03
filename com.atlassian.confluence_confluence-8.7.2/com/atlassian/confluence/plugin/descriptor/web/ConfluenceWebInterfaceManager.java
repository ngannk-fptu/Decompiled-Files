/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.api.DynamicWebInterfaceManager
 *  com.atlassian.plugin.web.api.WebItem
 *  com.atlassian.plugin.web.api.WebSection
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.api.DynamicWebInterfaceManager;
import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugin.web.api.WebSection;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceWebInterfaceManager
implements DynamicWebInterfaceManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceWebInterfaceManager.class);
    @Deprecated
    public static final String CONTEXT_KEY_HELPER = "helper";
    private DynamicWebInterfaceManager decoratedManager;

    public List<WebPanelModuleDescriptor> getDisplayableWebPanelDescriptors(String s, Map<String, Object> stringObjectMap) {
        return this.decoratedManager.getDisplayableWebPanelDescriptors(s, stringObjectMap);
    }

    public List<WebPanelModuleDescriptor> getWebPanelDescriptors(String s) {
        return this.decoratedManager.getWebPanelDescriptors(s);
    }

    public WebFragmentHelper getWebFragmentHelper() {
        return this.decoratedManager.getWebFragmentHelper();
    }

    public boolean hasSectionsForLocation(String location) {
        return this.decoratedManager.hasSectionsForLocation(location);
    }

    public List<WebSectionModuleDescriptor> getSections(String location) {
        return this.decoratedManager.getSections(location);
    }

    public List<WebSectionModuleDescriptor> getDisplayableSections(String key, Map<String, Object> params) {
        return this.decoratedManager.getDisplayableSections(key, params);
    }

    public List<WebSectionModuleDescriptor> getDisplayableSections(String key, WebInterfaceContext context) {
        return this.decoratedManager.getDisplayableSections(key, context.toMap());
    }

    public List<WebItemModuleDescriptor> getItems(String section) {
        return this.decoratedManager.getItems(section);
    }

    public List<WebItemModuleDescriptor> getDisplayableItems(String key, Map<String, Object> params) {
        return this.decoratedManager.getDisplayableItems(key, params);
    }

    public List<WebItemModuleDescriptor> getDisplayableItems(String key, WebInterfaceContext context) {
        return this.decoratedManager.getDisplayableItems(key, context.toMap());
    }

    public void refresh() {
        this.decoratedManager.refresh();
    }

    public void setWebInterfaceManager(DynamicWebInterfaceManager webInterfaceManager) {
        this.decoratedManager = webInterfaceManager;
    }

    public List<WebPanel> getWebPanels(String location) {
        return this.decoratedManager.getWebPanels(location);
    }

    public List<WebPanel> getDisplayableWebPanels(String location, Map<String, Object> context) {
        return Lists.transform((List)this.decoratedManager.getDisplayableWebPanels(location, context), from -> new ExceptionHandlingWebPanel((WebPanel)from));
    }

    public Iterable<WebItem> getWebItems(String section, Map<String, Object> context) {
        return this.decoratedManager.getWebItems(section, context);
    }

    public Iterable<WebItem> getDisplayableWebItems(String section, Map<String, Object> context) {
        return this.decoratedManager.getDisplayableWebItems(section, context);
    }

    public Iterable<WebSection> getWebSections(String location, Map<String, Object> context) {
        return this.decoratedManager.getWebSections(location, context);
    }

    public Iterable<WebSection> getDisplayableWebSections(String location, Map<String, Object> context) {
        return this.decoratedManager.getDisplayableWebSections(location, context);
    }

    private static class ExceptionHandlingWebPanel
    implements WebPanel {
        private final WebPanel delegate;

        public ExceptionHandlingWebPanel(WebPanel delegate) {
            this.delegate = delegate;
        }

        public String getHtml(Map<String, Object> parameters) {
            try {
                return this.delegate.getHtml(parameters);
            }
            catch (Exception e) {
                log.error("Failed to render web panel: " + this.delegate, (Throwable)e);
                return "";
            }
            catch (NoClassDefFoundError error) {
                if (ConfluenceSystemProperties.isDevMode()) {
                    log.error("Failed to render web panel: " + this.delegate + ". Failure details: ", (Throwable)error);
                } else {
                    log.error("Failed to render web panel: " + this.delegate);
                    log.debug("Failed to render web panel: " + this.delegate + ". Failure details: ", (Throwable)error);
                }
                return "";
            }
        }

        public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
            try {
                this.delegate.writeHtml(writer, context);
            }
            catch (Exception e) {
                log.error("Failed to render web panel: " + this.delegate, (Throwable)e);
            }
        }
    }
}

