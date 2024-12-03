/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.dom4j.Element
 */
package com.atlassian.plugin.clientsideextensions.moduletype;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.clientsideextensions.ExtensionPageServlet;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.dom4j.Element;

@ParametersAreNonnullByDefault
public class WebPageModuleDescriptor
extends ServletModuleDescriptor {
    public static final String MODULE_TYPE = "web-page";
    @VisibleForTesting
    static final String DEPENDENCY_ELEM = "dependency";
    @VisibleForTesting
    static final String EXTENSION_KEY_ATTR = "extension-key";
    @VisibleForTesting
    static final String EXTENSION_POINT_ATTR = "extension-point";
    @VisibleForTesting
    static final String PAGE_DATA_PROVIDER_KEY_ATTR = "page-data-provider-key";
    @VisibleForTesting
    static final String PAGE_DECORATOR_ATTR = "page-decorator";
    @VisibleForTesting
    static final String PAGE_TITLE_ELEM = "page-title";
    @VisibleForTesting
    static final String PAGE_TITLE_KEY_ATTR = "key";
    @VisibleForTesting
    static final String URL_PATTERN_ELEM = "url-pattern";
    private final Map<String, String> initParams = new HashMap<String, String>();
    private final List<String> urlPatterns = new ArrayList<String>();

    public WebPageModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, @ComponentImport ServletModuleManager servletModuleManager) {
        super(moduleFactory, servletModuleManager);
    }

    public void init(Plugin plugin, Element webPageElement) {
        super.init(plugin, webPageElement);
        this.moduleClassName = ExtensionPageServlet.class.getName();
        this.parseServletInitParams(webPageElement);
        this.parseUrlPatterns(webPageElement);
    }

    private void parseServletInitParams(Element webPageElement) {
        this.initParams.put(EXTENSION_KEY_ATTR, webPageElement.attributeValue(EXTENSION_KEY_ATTR));
        this.initParams.put(EXTENSION_POINT_ATTR, webPageElement.attributeValue(EXTENSION_POINT_ATTR));
        this.initParams.put(PAGE_DATA_PROVIDER_KEY_ATTR, webPageElement.attributeValue(PAGE_DATA_PROVIDER_KEY_ATTR));
        this.initParams.put(PAGE_DECORATOR_ATTR, webPageElement.attributeValue(PAGE_DECORATOR_ATTR));
        this.initParams.put(PAGE_TITLE_ELEM, webPageElement.elementTextTrim(PAGE_TITLE_ELEM));
        this.initParams.put("web-resources", this.getWebResources(webPageElement));
        this.parsePageTitleKey(webPageElement).ifPresent(key -> this.initParams.put("page-title-key", (String)key));
    }

    private Optional<String> parsePageTitleKey(Element webPageElement) {
        return Optional.ofNullable(webPageElement.element(PAGE_TITLE_ELEM)).map(pageTitleElement -> pageTitleElement.attributeValue(PAGE_TITLE_KEY_ATTR));
    }

    private String getWebResources(Element webPageElement) {
        List elements = webPageElement.elements(DEPENDENCY_ELEM);
        return elements.stream().filter(Element.class::isInstance).map(Element.class::cast).map(Element::getTextTrim).collect(Collectors.joining(","));
    }

    private void parseUrlPatterns(Element webPageElement) {
        for (Object object : webPageElement.elements(URL_PATTERN_ELEM)) {
            if (!(object instanceof Element)) continue;
            Element element = (Element)object;
            this.urlPatterns.add(element.getTextTrim());
        }
    }

    public Map<String, String> getInitParams() {
        return Collections.unmodifiableMap(this.initParams);
    }

    public List<String> getPaths() {
        return Collections.unmodifiableList(this.urlPatterns);
    }
}

