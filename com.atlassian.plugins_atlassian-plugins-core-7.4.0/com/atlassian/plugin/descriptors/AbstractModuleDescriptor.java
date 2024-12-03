/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Permissions
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.Resources
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.module.LegacyModuleFactory
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.util.JavaVersionUtils
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModulePermissionException;
import com.atlassian.plugin.Permissions;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.Resources;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.ModuleDescriptors;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.module.LegacyModuleFactory;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.module.PrefixDelegatingModuleFactory;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.util.ClassUtils;
import com.atlassian.plugin.util.JavaVersionUtils;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModuleDescriptor<T>
implements ModuleDescriptor<T>,
StateAware {
    private static final Logger log = LoggerFactory.getLogger(AbstractModuleDescriptor.class);
    protected Plugin plugin;
    protected String key;
    protected String name;
    protected String moduleClassName;
    protected Class<T> moduleClass;
    private String description;
    private boolean enabledByDefault = true;
    private boolean systemModule = false;
    private Optional<String> scopeKey = Optional.empty();
    private Map<String, String> params;
    protected Resources resources = Resources.EMPTY_RESOURCES;
    private Float minJavaVersion;
    private String i18nNameKey;
    private String descriptionKey;
    private String completeKey;
    private boolean enabled = false;
    protected final ModuleFactory moduleFactory;
    private boolean broken = false;

    public AbstractModuleDescriptor(ModuleFactory moduleFactory) {
        this.moduleFactory = Objects.requireNonNull(moduleFactory, "Module creator factory cannot be null");
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        this.validate(element);
        this.plugin = (Plugin)Assertions.notNull((String)"plugin", (Object)plugin);
        this.key = element.attributeValue("key");
        this.name = element.attributeValue("name");
        this.i18nNameKey = element.attributeValue("i18n-name-key");
        this.completeKey = this.buildCompleteKey(plugin, this.key);
        this.description = element.elementTextTrim("description");
        this.moduleClassName = element.attributeValue("class");
        Element descriptionElement = element.element("description");
        this.descriptionKey = descriptionElement != null ? descriptionElement.attributeValue("key") : null;
        String scopedAttribute = element.attributeValue("scoped");
        this.scopeKey = scopedAttribute == null || Boolean.TRUE.toString().equalsIgnoreCase(scopedAttribute) ? this.plugin.getScopeKey() : Optional.empty();
        this.params = LoaderUtils.getParams((Element)element);
        if ("disabled".equalsIgnoreCase(element.attributeValue("state"))) {
            this.enabledByDefault = false;
        }
        if ("true".equalsIgnoreCase(element.attributeValue("system"))) {
            this.systemModule = true;
        }
        if (element.element("java-version") != null) {
            this.minJavaVersion = Float.valueOf(element.element("java-version").attributeValue("min"));
        }
        this.resources = Resources.fromXml((Element)element);
    }

    protected final void checkPermissions() {
        if (this.plugin.hasAllPermissions() || this.isSystemModule()) {
            return;
        }
        HashSet<String> allRequiredPermissions = new HashSet<String>(this.getAllRequiredPermissions());
        allRequiredPermissions.removeAll(this.plugin.getActivePermissions());
        if (!allRequiredPermissions.isEmpty()) {
            throw new ModulePermissionException(this.getCompleteKey(), allRequiredPermissions);
        }
    }

    private Set<String> getAllRequiredPermissions() {
        HashSet<String> permissions = new HashSet<String>();
        permissions.addAll(Permissions.getRequiredPermissions(this.getClass()));
        permissions.addAll(this.getRequiredPermissions());
        return Collections.unmodifiableSet(permissions);
    }

    protected Set<String> getRequiredPermissions() {
        return Collections.emptySet();
    }

    private void validate(Element element) {
        Assertions.notNull((String)"element", (Object)element);
        ValidationPattern pattern = ValidationPattern.createPattern();
        this.provideValidationRules(pattern);
        pattern.evaluate((Node)element);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        pattern.rule(ValidationPattern.test("@key").withError("The key is required"));
    }

    protected void loadClass(Plugin plugin, String clazz) {
        if (this.moduleClassName != null) {
            if (this.moduleFactory instanceof LegacyModuleFactory) {
                this.moduleClass = ((LegacyModuleFactory)this.moduleFactory).getModuleClass(this.moduleClassName, (ModuleDescriptor)this);
            } else if (this.moduleFactory instanceof PrefixDelegatingModuleFactory) {
                this.moduleClass = ((PrefixDelegatingModuleFactory)this.moduleFactory).guessModuleClass(this.moduleClassName, this);
            }
        } else {
            this.moduleClass = Void.class;
        }
        if (this.moduleClass == null) {
            try {
                this.moduleClass = this.getModuleTypeClass();
            }
            catch (ClassCastException ex) {
                throw new IllegalStateException("The module class must be defined in a concrete instance of ModuleDescriptor and not as another generic type.");
            }
            if (this.moduleClass == null) {
                throw new IllegalStateException("The module class cannot be determined, likely because it needs a concrete module type defined in the generic type it passes to AbstractModuleDescriptor");
            }
        }
    }

    private Class getModuleTypeClass() {
        try {
            return ClassUtils.getTypeArguments(AbstractModuleDescriptor.class, this.getClass()).get(0);
        }
        catch (RuntimeException ex) {
            log.debug("Unable to get generic type, usually due to Class.forName() problems", (Throwable)ex);
            return this.getModuleReturnClass();
        }
    }

    Class<?> getModuleReturnClass() {
        try {
            return this.getClass().getMethod("getModule", new Class[0]).getReturnType();
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("The getModule() method is missing (!) on " + this.getClass());
        }
    }

    private String buildCompleteKey(Plugin plugin, String moduleKey) {
        if (plugin == null) {
            return null;
        }
        return plugin.getKey() + ":" + moduleKey;
    }

    public void destroy() {
        if (this.enabled) {
            this.disabled();
        }
    }

    public boolean isEnabledByDefault() {
        return this.enabledByDefault && this.satisfiesMinJavaVersion();
    }

    public boolean isSystemModule() {
        return this.systemModule;
    }

    protected final void assertModuleClassImplements(Class<T> requiredModuleClazz) {
        if (!this.enabled) {
            throw new PluginParseException("Plugin module " + this.getKey() + " not enabled");
        }
        if (!requiredModuleClazz.isAssignableFrom(this.getModuleClass())) {
            throw new PluginParseException("Given module class: " + this.getModuleClass().getName() + " does not implement " + requiredModuleClazz.getName());
        }
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public String getPluginKey() {
        return this.getPlugin().getKey();
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getModuleClass() {
        return this.moduleClass;
    }

    public abstract T getModule();

    public Optional<String> getScopeKey() {
        return this.scopeKey;
    }

    public String getDescription() {
        return this.description;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getI18nNameKey() {
        return this.i18nNameKey;
    }

    public String getDescriptionKey() {
        return this.descriptionKey;
    }

    public List<ResourceDescriptor> getResourceDescriptors() {
        return this.resources.getResourceDescriptors();
    }

    public ResourceLocation getResourceLocation(String type, String name) {
        return this.resources.getResourceLocation(type, name);
    }

    public ResourceDescriptor getResourceDescriptor(String type, String name) {
        return this.resources.getResourceDescriptor(type, name);
    }

    public Float getMinJavaVersion() {
        return this.minJavaVersion;
    }

    public boolean satisfiesMinJavaVersion() {
        if (this.minJavaVersion != null) {
            return JavaVersionUtils.satisfiesMinVersion((float)this.minJavaVersion.floatValue());
        }
        return true;
    }

    public void setPlugin(Plugin plugin) {
        this.completeKey = this.buildCompleteKey(plugin, this.key);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean equals(Object obj) {
        return new ModuleDescriptors.EqualsBuilder().descriptor(this).isEqualTo(obj);
    }

    public int hashCode() {
        return new ModuleDescriptors.HashCodeBuilder().descriptor(this).toHashCode();
    }

    public String toString() {
        return this.getCompleteKey() + " (" + this.getDescription() + ")";
    }

    public void enabled() {
        this.loadClass(this.plugin, this.moduleClassName);
        this.enabled = true;
        this.broken = false;
    }

    public void disabled() {
        this.enabled = false;
        this.moduleClass = null;
    }

    protected String getModuleClassName() {
        return this.moduleClassName;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setBroken() {
        this.broken = true;
    }

    public boolean isBroken() {
        return this.broken;
    }
}

