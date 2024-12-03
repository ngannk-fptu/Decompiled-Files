/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.renderer.macro.Macro
 *  com.atlassian.renderer.v2.macro.Macro
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.radeox.macro.Macro
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.confluence.plugin.descriptor.MacroMetadataParser;
import com.atlassian.confluence.plugin.descriptor.MacroModuleDescriptor;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.renderer.v2.macros.RadeoxCompatibilityMacro;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.UserLocaleAware;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.renderer.macro.Macro;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CustomMacroModuleDescriptor
extends AbstractModuleDescriptor<com.atlassian.renderer.v2.macro.Macro>
implements MacroModuleDescriptor,
ApplicationContextAware,
PluginModuleFactory<com.atlassian.renderer.v2.macro.Macro>,
UserLocaleAware,
MacroMetadataSource {
    private static final Logger log = LoggerFactory.getLogger(CustomMacroModuleDescriptor.class);
    private static final String RESOURCE_TYPE_VELOCITY = "velocity";
    private ApplicationContext applicationContext;
    private I18NBeanFactory i18NBeanFactory;
    private final MacroMetadataParser macroMetadataParser;
    private PluginModuleHolder<com.atlassian.renderer.v2.macro.Macro> macro;
    private ResourceDescriptor helpDescriptor;
    private MacroMetadata macroMetadata;
    private static final String HELP_RESOURCE_NAME = "help";
    private Macro.BodyType bodyType;

    public CustomMacroModuleDescriptor(ModuleFactory moduleFactory, MacroMetadataParser macroMetadataParser) {
        super(moduleFactory);
        this.macroMetadataParser = macroMetadataParser;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.macro = PluginModuleHolder.getInstance(this);
        this.macroMetadata = this.macroMetadataParser.parseMacro(this, element);
        this.bodyType = this.getBodyTypeEnum(element.attributeValue("body-type"));
    }

    public com.atlassian.renderer.v2.macro.Macro getModule() {
        return this.macro.getModule();
    }

    @Override
    public com.atlassian.renderer.v2.macro.Macro createModule() {
        Object underlyingMacro;
        if (this.plugin instanceof ContainerManagedPlugin) {
            underlyingMacro = LegacySpringContainerAccessor.createBean(this.plugin, this.getModuleClass());
        } else {
            AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
            underlyingMacro = beanFactory.createBean(this.getModuleClass(), 1, false);
        }
        return underlyingMacro instanceof org.radeox.macro.Macro ? new RadeoxCompatibilityMacro((Macro)underlyingMacro) : (com.atlassian.renderer.v2.macro.Macro)underlyingMacro;
    }

    public void enabled() {
        super.enabled();
        this.macro.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.macro.disabled();
        super.disabled();
    }

    public boolean hasHelp() {
        if (this.helpDescriptor == null) {
            this.helpDescriptor = this.getHelpDescriptor();
        }
        return this.helpDescriptor != null;
    }

    public String getHelpSection() {
        if (!this.hasHelp()) {
            return null;
        }
        return this.helpDescriptor.getParameter("help-section");
    }

    public ResourceDescriptor getHelpDescriptor() {
        return this.getResourceDescriptor(RESOURCE_TYPE_VELOCITY, HELP_RESOURCE_NAME);
    }

    @HtmlSafe
    public String getHelp() {
        return this.renderResource(this.getHelpDescriptor());
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public MacroMetadata getMacroMetadata() {
        return this.macroMetadata;
    }

    private String renderResource(ResourceDescriptor resource) {
        if (resource == null) {
            return null;
        }
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        context.put("i18n", this.i18NBeanFactory.getI18NBean());
        try {
            if (StringUtils.isNotEmpty((CharSequence)resource.getLocation())) {
                return VelocityUtils.getRenderedTemplate(resource.getLocation(), context);
            }
            return VelocityUtils.getRenderedContent(resource.getContent(), context);
        }
        catch (Exception e) {
            log.error("Error while rendering velocity template for '" + resource + "'.", (Throwable)e);
            return "";
        }
    }

    @Override
    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    Macro.BodyType getBodyTypeEnum(String bodyTypeString) {
        Macro.BodyType result = null;
        if (StringUtils.isNotBlank((CharSequence)bodyTypeString)) {
            bodyTypeString = bodyTypeString.trim().toUpperCase();
            try {
                result = Macro.BodyType.valueOf(Macro.BodyType.class, bodyTypeString);
            }
            catch (IllegalArgumentException e) {
                throw new PluginParseException("Invalid body-type attribute value '" + bodyTypeString + "' in '" + this.getName() + "' of macro descriptor.");
            }
        }
        return result;
    }

    public Macro.BodyType getBodyType() {
        return this.bodyType;
    }

    public boolean hasBody() {
        return this.innerHasBody(this.getModuleClass());
    }

    boolean innerHasBody(Class macroClass) {
        if (macroClass == null) {
            return false;
        }
        com.atlassian.renderer.v2.macro.Macro macroInstance = null;
        Constructor<?>[] declaredConstructors = macroClass.getDeclaredConstructors();
        for (int i = 0; i < declaredConstructors.length && macroInstance == null; ++i) {
            Constructor<?> constructor = declaredConstructors[i];
            try {
                macroInstance = (com.atlassian.renderer.v2.macro.Macro)constructor.newInstance(new Object[constructor.getParameterTypes().length]);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (macroInstance == null) {
            return false;
        }
        boolean hasBody = false;
        try {
            return macroInstance.hasBody();
        }
        catch (Exception exception) {
            return hasBody;
        }
    }
}

