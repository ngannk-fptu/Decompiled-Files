/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Permissions
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.descriptors.CannotDisable
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.plugin.schema.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Permissions;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.schema.descriptor.DescribedModuleDescriptorFactory;
import com.atlassian.plugin.schema.impl.DescribedModuleTypeDescribedModuleDescriptorFactory;
import com.atlassian.plugin.schema.spi.DocumentBasedSchema;
import com.atlassian.plugin.schema.spi.Schema;
import com.atlassian.plugin.schema.spi.SchemaFactory;
import com.atlassian.plugin.schema.spi.SchemaTransformer;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.osgi.framework.BundleContext;

@CannotDisable
public final class DescribedModuleTypeModuleDescriptor
extends AbstractModuleDescriptor<DescribedModuleDescriptorFactory> {
    private static final String[] PUBLIC_INTERFACES = new String[]{ModuleDescriptorFactory.class.getName(), ListableModuleDescriptorFactory.class.getName(), DescribedModuleDescriptorFactory.class.getName()};
    private String schemaFactoryClassName;
    private String type;
    private String schemaTransformerClassName;
    private String maxOccurs;
    private Iterable<String> requiredPermissions;
    private Iterable<String> optionalPermissions;

    public DescribedModuleTypeModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        Preconditions.checkState((boolean)(plugin instanceof OsgiPlugin), (String)"Described module types can only be declared in OSGi Plugins, %s is not such a plugin", (Object)plugin.getKey());
        super.init(plugin, element);
        this.type = DescribedModuleTypeModuleDescriptor.getOptionalAttribute(element, "type", this.getKey());
        this.schemaFactoryClassName = DescribedModuleTypeModuleDescriptor.getOptionalAttribute(element, "schema-factory-class", null);
        this.schemaTransformerClassName = DescribedModuleTypeModuleDescriptor.getOptionalAttribute(element, "schema-transformer-class", null);
        this.maxOccurs = DescribedModuleTypeModuleDescriptor.getOptionalAttribute(element, "max-occurs", "unbounded");
        this.requiredPermissions = DescribedModuleTypeModuleDescriptor.getPermissions(element.element("required-permissions"));
        this.optionalPermissions = DescribedModuleTypeModuleDescriptor.getPermissions(element.element("optional-permissions"));
    }

    private static Iterable<String> getPermissions(Element element) {
        return Optional.ofNullable(element).map(e -> Iterables.transform(DescribedModuleTypeModuleDescriptor.getElements(e, "permission"), Element::getTextTrim)).orElseGet(Collections::emptyList);
    }

    private static List<Element> getElements(Element element, String name) {
        return element.elements(name);
    }

    public void enabled() {
        Preconditions.checkState((boolean)(this.plugin instanceof OsgiPlugin), (String)"Described module types can only be declared in OSGi Plugins, %s is not such a plugin", (Object)this.plugin.getKey());
        super.enabled();
        SchemaTransformer schemaTransformer = this.schemaTransformerClassName != null ? this.create(this.findClass(this.schemaTransformerClassName, SchemaTransformer.class)) : SchemaTransformer.IDENTITY;
        Class<ModuleDescriptor> moduleClass = this.findClass(this.moduleClassName, ModuleDescriptor.class);
        SchemaFactory schemaFactory = this.schemaFactoryClassName != null ? this.create(this.findClass(this.schemaFactoryClassName, SchemaFactory.class)) : this.buildSingleton(DocumentBasedSchema.builder(this.type).setResourceLoader(new AlternativePluginResourceLoader(this.plugin)).setName(this.getDisplayName()).setDescription(this.getDescription() != null ? this.getDescription() : "").setTransformer(schemaTransformer).setMaxOccurs(this.maxOccurs).setRequiredPermissions(this.getModuleRequiredPermissions(moduleClass)).setOptionalPermissions(this.optionalPermissions).build());
        DescribedModuleTypeDescribedModuleDescriptorFactory<ModuleDescriptor> factory = new DescribedModuleTypeDescribedModuleDescriptorFactory<ModuleDescriptor>((ContainerManagedPlugin)this.plugin, this.type, moduleClass, schemaFactory);
        this.getBundleContext().registerService(PUBLIC_INTERFACES, factory, null);
    }

    private Iterable<String> getModuleRequiredPermissions(Class<? extends ModuleDescriptor> moduleClass) {
        return ImmutableSet.builder().addAll(this.requiredPermissions).addAll((Iterable)Permissions.getRequiredPermissions(moduleClass)).build();
    }

    private BundleContext getBundleContext() {
        return ((OsgiPlugin)this.plugin).getBundle().getBundleContext();
    }

    private <T> T create(Class<? extends T> type) {
        return (T)((ContainerManagedPlugin)this.plugin).getContainerAccessor().createBean(type);
    }

    private <T> Class<? extends T> findClass(String className, Class<T> castTo) {
        Class clazz;
        Preconditions.checkNotNull((Object)className);
        try {
            clazz = this.plugin.loadClass(className, ((Object)((Object)this)).getClass());
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException("Unable to find class " + className);
        }
        return clazz.asSubclass(castTo);
    }

    public DescribedModuleDescriptorFactory getModule() {
        return (DescribedModuleDescriptorFactory)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    private SchemaFactory buildSingleton(Schema schema) {
        return () -> schema;
    }

    public static String getOptionalAttribute(Element e, String name, Object defaultValue) {
        String value = e.attributeValue(name);
        if (value != null) {
            return value;
        }
        return Objects.toString(defaultValue, null);
    }

    private static final class AlternativePluginResourceLoader
    implements AlternativeResourceLoader {
        private final Plugin plugin;

        public AlternativePluginResourceLoader(Plugin plugin) {
            this.plugin = (Plugin)Preconditions.checkNotNull((Object)plugin);
        }

        public URL getResource(String path) {
            return this.plugin.getResource(path);
        }

        public InputStream getResourceAsStream(String name) {
            return this.plugin.getResourceAsStream(name);
        }
    }
}

