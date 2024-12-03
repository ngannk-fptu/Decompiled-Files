/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.plugin.AutodetectModuleFactoryHolder;
import com.atlassian.confluence.plugin.descriptor.MacroMetadataParser;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class XhtmlMacroModuleDescriptor
extends AbstractModuleDescriptor<Macro>
implements MacroMetadataSource {
    private static final Set<String> DEFAULT_SUPPORTED_DEVICE_TYPES = Set.of("desktop");
    private static final String OUTPUT_DEVICE_TYPE_ELEMENT_NAME = "device-type";
    private static final String RESOURCE_TYPE_VELOCITY = "velocity";
    private static final String HELP_RESOURCE_NAME = "help";
    private boolean alwaysShowConfig = false;
    private final MacroMetadataParser macroMetadataParser;
    protected Set<String> supportedDeviceTypes = new HashSet<String>(DEFAULT_SUPPORTED_DEVICE_TYPES);
    private final PluginModuleHolder<Macro> module = PluginModuleHolder.getInstance(() -> (Macro)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this));
    private final PluginModuleHolder<ResourceDescriptor> helpDescriptor = PluginModuleHolder.getInstance(this::getHelpDescriptor);
    private MacroMetadata macroMetadata;

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
        this.helpDescriptor.enabled(ResourceDescriptor.class);
    }

    public void disabled() {
        this.module.disabled();
        this.helpDescriptor.disabled();
        super.disabled();
    }

    public XhtmlMacroModuleDescriptor(AutodetectModuleFactoryHolder moduleFactoryHolder, MacroMetadataParser metadataParser) {
        super(moduleFactoryHolder.get());
        this.macroMetadataParser = metadataParser;
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        List elements = element.elements(OUTPUT_DEVICE_TYPE_ELEMENT_NAME);
        for (Element el : elements) {
            this.supportedDeviceTypes.add(el.getText());
        }
        Element showConfig = element.element("alwaysShowConfig");
        if (showConfig != null) {
            this.alwaysShowConfig = Boolean.parseBoolean(showConfig.attributeValue("value"));
        }
        this.macroMetadata = this.macroMetadataParser.parseMacro((ModuleDescriptor)this, element);
    }

    public Macro getModule() {
        return this.module.getModule();
    }

    @Override
    public MacroMetadata getMacroMetadata() {
        return this.macroMetadata;
    }

    public boolean isAlwaysShowConfig() {
        return this.alwaysShowConfig;
    }

    public boolean isOutputDeviceTypeSupported(String deviceType) {
        return this.supportedDeviceTypes.contains(deviceType);
    }

    public boolean hasHelp() {
        return this.helpDescriptor.getModule() != null;
    }

    public String getHelpSection() {
        if (!this.hasHelp()) {
            throw new IllegalStateException("Should not be invoked when hasHelp() is false.");
        }
        return this.helpDescriptor.getModule().getParameter("help-section");
    }

    public ResourceDescriptor getHelpDescriptor() {
        return this.getResourceDescriptor(RESOURCE_TYPE_VELOCITY, HELP_RESOURCE_NAME);
    }
}

