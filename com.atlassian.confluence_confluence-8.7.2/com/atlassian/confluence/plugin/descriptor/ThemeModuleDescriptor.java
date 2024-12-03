/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.Resourced
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.JavascriptWebResource
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.PluginThemeResource;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.atlassian.confluence.themes.VelocityResultOverride;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemeModuleDescriptor
extends AbstractModuleDescriptor<Theme> {
    private static final Logger log = LoggerFactory.getLogger(ThemeModuleDescriptor.class);
    private final PluginAccessor pluginAccessor;
    private final WebResourceIntegration webResourceIntegration;
    private PluginModuleHolder<Theme> theme;
    private boolean disableSitemesh;
    private String colourSchemeKey;
    private List<String> layoutKeys = new ArrayList<String>();
    private List<VelocityResultOverride> velocityResultOverrides = new ArrayList<VelocityResultOverride>();
    private String bodyClass;
    private String topNavLocation;
    private boolean hasSpaceSideBar;

    public ThemeModuleDescriptor(ModuleFactory moduleFactory, PluginAccessor pluginAccessor, WebResourceIntegration webResourceIntegration) {
        super(moduleFactory);
        this.pluginAccessor = pluginAccessor;
        this.webResourceIntegration = webResourceIntegration;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        if (element.attribute("disable-sitemesh") != null) {
            this.disableSitemesh = Boolean.valueOf(element.attributeValue("disable-sitemesh"));
        }
        Iterator it = element.elementIterator();
        while (it.hasNext()) {
            Element child = (Element)it.next();
            if (child.getName().equals("colour-scheme") || child.getName().equals("color-scheme")) {
                if (StringUtils.isNotEmpty((CharSequence)this.colourSchemeKey)) {
                    log.warn(this.getCompleteKey() + " should not define multiple colour schemes. All but one ignored.");
                }
                this.colourSchemeKey = child.attributeValue("key");
            }
            if (child.getName().equals("layout")) {
                this.layoutKeys.add(child.attributeValue("key"));
            }
            if (child.getName().equals("xwork-velocity-name-override")) {
                this.velocityResultOverrides.add(new TemplateOverride(child.attributeValue("template"), child.attributeValue("override")));
            }
            if (child.getName().equals("xwork-velocity-result-override")) {
                this.velocityResultOverrides.add(new ActionResultOverride(child.attributeValue("package"), child.attributeValue("action"), child.attributeValue("result"), child.attributeValue("override")));
            }
            if (child.getName().equals("body-class")) {
                this.bodyClass = child.getText();
            }
            if (child.getName().equals("top-navigation")) {
                this.topNavLocation = child.attributeValue("location");
            }
            if (!child.getName().equals("space-ia")) continue;
            this.hasSpaceSideBar = Boolean.valueOf(child.attributeValue("value"));
        }
        this.theme = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public Theme getModule() {
        return this.theme.getModule();
    }

    public void enabled() {
        super.enabled();
        this.theme.enabled(this.getModuleClass());
        this.theme.getModule().init(this);
    }

    public void disabled() {
        this.theme.disabled();
        super.disabled();
    }

    public ColourScheme getColourScheme() {
        if (this.colourSchemeKey == null) {
            return null;
        }
        ModuleDescriptor colourSchemeDesc = this.pluginAccessor.getPluginModule(this.colourSchemeKey);
        if (colourSchemeDesc == null) {
            log.error(this.getCompleteKey() + "  unable to locate colour scheme " + this.colourSchemeKey);
            return null;
        }
        ((StateAware)colourSchemeDesc).enabled();
        Object colourScheme = colourSchemeDesc.getModule();
        if (!(colourScheme instanceof ColourScheme)) {
            log.error(this.getCompleteKey() + " unable to load colour scheme " + this.colourSchemeKey + " wrong type: " + colourScheme.getClass().getName());
            return null;
        }
        return (ColourScheme)colourScheme;
    }

    public List<ThemedDecorator> getLayouts() {
        ArrayList<ThemedDecorator> layouts = new ArrayList<ThemedDecorator>(this.layoutKeys.size());
        for (String layoutKey : this.layoutKeys) {
            this.addLayout(layouts, layoutKey);
        }
        return layouts;
    }

    private void addLayout(List<ThemedDecorator> layouts, String key) {
        ModuleDescriptor desc = this.pluginAccessor.getPluginModule(key);
        if (desc == null) {
            log.error(this.getCompleteKey() + " unable to locate layout " + key);
            return;
        }
        ((StateAware)desc).enabled();
        Object layout = desc.getModule();
        if (layout instanceof ThemedDecorator) {
            layouts.add((ThemedDecorator)layout);
        } else {
            log.error(this.getCompleteKey() + " unable to load layout " + key + " wrong type: " + layout.getClass().getName());
        }
    }

    public Collection<ThemeResource> getStylesheets() {
        LinkedList<ThemeResource> result = new LinkedList<ThemeResource>();
        Iterator<ResourceDescriptor> iterator = this.getStylesheetResourceDescriptors((Resourced)this).iterator();
        while (iterator.hasNext()) {
            ResourceDescriptor o;
            ResourceDescriptor resource = o = iterator.next();
            PluginThemeResource stylesheet = PluginThemeResource.css(this.getCompleteKey(), resource);
            result.add(stylesheet);
        }
        return result;
    }

    public Iterable<ThemeResource> getJavascript() {
        JavascriptWebResource jsFilter = new JavascriptWebResource();
        return this.getResourceDescriptors().stream().filter(r -> "download".equalsIgnoreCase(r.getType())).filter(r -> jsFilter.matches(r.getName())).map(r -> PluginThemeResource.javascript(this.getCompleteKey(), r)).collect(Collectors.toList());
    }

    public boolean isDisableSitemesh() {
        return this.disableSitemesh;
    }

    private Collection<ResourceDescriptor> getStylesheetResourceDescriptors(Resourced resourced) {
        if (resourced == null) {
            return Collections.emptyList();
        }
        List resources = resourced.getResourceDescriptors();
        if (resources == null) {
            return Collections.emptyList();
        }
        ArrayList<ResourceDescriptor> result = new ArrayList<ResourceDescriptor>(resources);
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            ResourceDescriptor descriptor = (ResourceDescriptor)iter.next();
            if (descriptor.getType().equals("stylesheet") || descriptor.getName().endsWith(".css")) continue;
            iter.remove();
        }
        return result;
    }

    public List<VelocityResultOverride> getVelocityResultOverrides() {
        return this.velocityResultOverrides;
    }

    public String getBodyClass() {
        return this.bodyClass;
    }

    public String getTopNavLocation() {
        return this.topNavLocation;
    }

    public boolean hasSpaceSideBar() {
        return this.hasSpaceSideBar;
    }

    private static class ActionResultOverride
    implements VelocityResultOverride {
        private final String packageName;
        private final String actionName;
        private final String result;
        private final String newTemplatePath;

        private ActionResultOverride(String packageName, String actionName, String result, String newTemplatePath) {
            Validate.notEmpty((CharSequence)packageName, (String)"element package is not set on xwork-velocity-result-override", (Object[])new Object[0]);
            Validate.notEmpty((CharSequence)result, (String)"element result is not set on xwork-velocity-result-override", (Object[])new Object[0]);
            Validate.notEmpty((CharSequence)actionName, (String)"element action is not set on xwork-velocity-result-override", (Object[])new Object[0]);
            Validate.notEmpty((CharSequence)newTemplatePath, (String)"element override is not set on xwork-velocity-result-override", (Object[])new Object[0]);
            this.packageName = packageName;
            this.actionName = actionName;
            this.result = result;
            this.newTemplatePath = newTemplatePath;
        }

        @Override
        public String getOverridePath(String packageName, String actionName, String result, String templatePath) {
            if (this.packageName.equals(packageName) && this.actionName.equals(actionName) && this.result.equals(result)) {
                return this.newTemplatePath;
            }
            return templatePath;
        }
    }

    private static class TemplateOverride
    implements VelocityResultOverride {
        private final String oldTemplatePath;
        private final String newTemplatePath;

        private TemplateOverride(String oldTemplatePath, String newTemplatePath) {
            Validate.notEmpty((CharSequence)oldTemplatePath, (String)"element template is not set on xwork-velocity-name-override", (Object[])new Object[0]);
            Validate.notEmpty((CharSequence)newTemplatePath, (String)"element override is not set xwork-velocity-name-override", (Object[])new Object[0]);
            this.oldTemplatePath = oldTemplatePath;
            this.newTemplatePath = newTemplatePath;
        }

        @Override
        public String getOverridePath(String packageName, String actionName, String result, String templatePath) {
            if (this.oldTemplatePath.equals(templatePath)) {
                return this.newTemplatePath;
            }
            return templatePath;
        }
    }
}

