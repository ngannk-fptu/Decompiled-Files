/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.plugins.navlink.producer.capabilities.plugin;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugins.navlink.producer.capabilities.Capability;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class CapabilityModuleDescriptor
extends AbstractModuleDescriptor<Capability> {
    private Element element;
    private boolean enabled;

    public CapabilityModuleDescriptor(@Nonnull ModuleFactory moduleFactory) {
        super((ModuleFactory)Preconditions.checkNotNull((Object)moduleFactory));
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init((Plugin)Preconditions.checkNotNull((Object)plugin), (Element)Preconditions.checkNotNull((Object)element));
        this.element = element;
        this.enabled = false;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"name").withError("name tag is mandatory"), ValidationPattern.test((String)"string-length(normalize-space(name)) > 0").withError("name tag requires a name as content"), ValidationPattern.test((String)"url").withError("url tag is mandatory"), ValidationPattern.test((String)"string-length(normalize-space(url)) > 0").withError("url tag requires a url as content"), ValidationPattern.test((String)"not(@type) or string-length(normalize-space(@type)) > 0").withError("type attribute of the capability tag is empty")});
    }

    public void enabled() {
        super.enabled();
        this.enabled = true;
    }

    public void disabled() {
        super.disabled();
        this.enabled = false;
    }

    public Capability getModule() {
        return this.enabled ? this.parseCapability() : null;
    }

    @Nonnull
    private Capability parseCapability() {
        String type = this.parseType();
        String name = this.parseName();
        String url = this.parseUrl();
        return new Capability(type, name, url);
    }

    @Nonnull
    private String parseType() {
        Attribute typeAttribute = this.element.attribute("type");
        return typeAttribute != null ? typeAttribute.getValue().trim() : "";
    }

    @Nonnull
    private String parseName() {
        return this.parseElementContent(this.element.element("name"));
    }

    @Nonnull
    private String parseUrl() {
        return this.parseElementContent(this.element.element("url"));
    }

    @Nonnull
    private String parseElementContent(@Nullable Element element) {
        String content = element != null ? element.getTextTrim().trim() : null;
        return Strings.nullToEmpty((String)content);
    }
}

