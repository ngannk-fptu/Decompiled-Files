/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.spi.manifest.ManifestProducer
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  org.dom4j.Element
 */
package com.atlassian.applinks.core.plugin;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.plugin.AbstractAppLinksTypeModuleDescriptor;
import com.atlassian.applinks.spi.manifest.ManifestProducer;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.util.Objects;
import org.dom4j.Element;

public class ApplicationTypeModuleDescriptor
extends AbstractAppLinksTypeModuleDescriptor<ApplicationType> {
    private Class<ManifestProducer> manifestProducerClass = null;
    private String manifestProducerClassName;

    public ApplicationTypeModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"manifest-producer/@class").withError("No " + ManifestProducer.class.getSimpleName() + "  class specified.")});
    }

    public void enabled() {
        super.enabled();
        try {
            this.manifestProducerClass = this.plugin.loadClass(this.manifestProducerClassName, this.getModuleClass());
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Unable to load this application type's " + ManifestProducer.class.getSimpleName() + " class.", cnfe);
        }
    }

    public void disabled() {
        this.manifestProducerClass = null;
        super.disabled();
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        Objects.requireNonNull(plugin, "plugin can't be null");
        Objects.requireNonNull(element, "element can't be null");
        super.init(plugin, element);
        this.manifestProducerClassName = element.element("manifest-producer").attributeValue("class");
    }

    protected Class<ManifestProducer> getManifestProducerClass() {
        return this.manifestProducerClass;
    }

    public ManifestProducer getManifestProducer() {
        return (ManifestProducer)((ContainerManagedPlugin)this.plugin).getContainerAccessor().createBean(this.manifestProducerClass);
    }
}

