/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroBody;
import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.plugin.descriptor.MacroMetadataParser;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.renderer.UserMacroConfig;
import com.atlassian.confluence.util.i18n.DocumentationLink;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.ArrayList;
import java.util.HashSet;
import org.dom4j.Element;

public final class UserMacroModuleDescriptor
extends AbstractModuleDescriptor<Macro>
implements PluginModuleFactory<Macro>,
MacroMetadataSource {
    private final MacroMetadataParser macroMetadataParser;
    private PluginModuleHolder<Macro> module;
    private UserMacroConfig config;
    private MacroMetadata macroMetadata;

    public UserMacroModuleDescriptor(ModuleFactory moduleFactory, MacroMetadataParser macroMetadataParser) {
        super(moduleFactory);
        this.macroMetadataParser = macroMetadataParser;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.config = this.parseUserMacroConfig(element);
        this.module = PluginModuleHolder.getInstance(this);
        this.macroMetadata = this.macroMetadataParser.parseMacro((ModuleDescriptor)this, element);
        if (this.macroMetadata != null) {
            MacroFormDetails formDetails;
            this.config.setCategories(new HashSet<String>(this.macroMetadata.getCategories()));
            MacroIcon icon = this.macroMetadata.getIcon();
            if (icon != null) {
                this.config.setIconLocation(icon.getLocation());
            }
            if ((formDetails = this.macroMetadata.getFormDetails()) != null) {
                DocumentationLink documentationLink = formDetails.getDocumentationLink();
                this.config.setDocumentationUrl(documentationLink != null ? documentationLink.getKey() : null);
                this.config.setParameters(new ArrayList<MacroParameter>(formDetails.getParameters()));
                MacroBody body = formDetails.getBody();
                if (body == null) {
                    body = new MacroBody(this.macroMetadata.getPluginKey(), this.macroMetadata.getMacroName());
                    formDetails.setBody(body);
                }
                body.setBodyType(UserMacroConfig.deriveBodyType(this.config).toString());
            }
        }
    }

    private UserMacroConfig parseUserMacroConfig(Element element) {
        UserMacroConfig config = new UserMacroConfig();
        Element template = element.element("template");
        if (template == null) {
            throw new PluginParseException("User macro has no template");
        }
        config.setName(this.getName());
        config.setDescription(this.getDescription());
        config.setTemplate(template.getText().trim());
        config.setHasBody("true".equalsIgnoreCase(element.attributeValue("hasBody", "false")));
        String defaultBodyType = config.isHasBody() ? "raw" : "none";
        config.setBodyType(element.attributeValue("bodyType", defaultBodyType));
        config.setOutputType(element.attributeValue("outputType", "html"));
        return config;
    }

    public UserMacroConfig getUserMacroConfig() {
        return this.config;
    }

    @Override
    public MacroMetadata getMacroMetadata() {
        return this.macroMetadata;
    }

    @Override
    public Macro createModule() {
        return this.config.toMacro();
    }

    public Macro getModule() {
        return this.module.getModule();
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }
}

