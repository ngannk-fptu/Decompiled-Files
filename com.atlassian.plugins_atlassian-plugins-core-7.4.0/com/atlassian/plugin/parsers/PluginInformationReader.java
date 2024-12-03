/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.InstallationMode
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.parsers.PluginDescriptorReader;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

public final class PluginInformationReader {
    static final String PLUGIN_INFO = "plugin-info";
    static final String DEFAULT_SCAN_FOLDER = "META-INF/atlassian";
    private final Element pluginInfo;
    private final Set<Application> applications;
    private final int pluginsVersion;

    PluginInformationReader(Element pluginInfo, Set<Application> applications, int pluginsVersion) {
        this.pluginsVersion = pluginsVersion;
        this.pluginInfo = pluginInfo;
        this.applications = ImmutableSet.copyOf((Collection)((Collection)Preconditions.checkNotNull(applications)));
    }

    public Optional<String> getDescription() {
        return this.getDescriptionElement().map(Element::getTextTrim);
    }

    public Optional<String> getDescriptionKey() {
        return this.getDescriptionElement().map(description -> description.attributeValue("key"));
    }

    public Optional<String> getVersion() {
        return this.childElement("version").map(Element::getTextTrim);
    }

    private Optional<Element> childElement(String name) {
        return this.pluginInfo == null ? Optional.empty() : Optional.ofNullable(this.pluginInfo.element(name));
    }

    private Stream<Element> childElements(String name) {
        return this.pluginInfo == null ? Stream.empty() : this.pluginInfo.elements(name).stream();
    }

    public Optional<String> getVendorName() {
        return this.getVendorElement().map(vendor -> vendor.attributeValue("name"));
    }

    public Optional<String> getVendorUrl() {
        return this.getVendorElement().map(vendor -> vendor.attributeValue("url"));
    }

    @Deprecated
    public Optional<String> getScopeKey() {
        Optional<String> scopeKey = this.getScopeElement().map(el -> el.attributeValue("key"));
        Preconditions.checkArgument((scopeKey.map(String::isEmpty).orElse(false) == false ? 1 : 0) != 0, (Object)"Value of scope key can't be blank");
        return scopeKey;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.getParamElements().collect(Collectors.toMap(param -> param.attribute("name").getData().toString(), Element::getText)));
    }

    public Optional<Float> getMinVersion() {
        return this.getApplicationVersionElement().flatMap(new GetAttributeFunction("min")).map(new ParseAttributeValueAsFloatFunction());
    }

    public Optional<Float> getMaxVersion() {
        return this.getApplicationVersionElement().flatMap(new GetAttributeFunction("max")).map(new ParseAttributeValueAsFloatFunction());
    }

    public Optional<Float> getMinJavaVersion() {
        return this.childElement("java-version").flatMap(new GetAttributeFunction("min")).map(new ParseAttributeValueAsFloatFunction());
    }

    public Map<String, Optional<String>> getPermissions() {
        return Collections.unmodifiableMap(this.getPermissionElements().collect(Collectors.toMap(Element::getTextTrim, perm -> Optional.ofNullable(perm.attributeValue("installation-mode")))));
    }

    public boolean hasAllPermissions() {
        return this.getPermissions().isEmpty() && this.pluginsVersion < 3;
    }

    public Set<String> getPermissions(InstallationMode installationMode) {
        return ImmutableSet.copyOf(Maps.filterValues(this.getPermissions(), permInstallMode -> permInstallMode.flatMap(InstallationMode::of).map(installMode -> installMode.equals((Object)installationMode)).orElse(true)).keySet());
    }

    public Optional<String> getStartup() {
        return this.childElement("startup").map(Element::getTextTrim);
    }

    public Iterable<String> getModuleScanFolders() {
        LinkedHashSet scanFolders = Sets.newLinkedHashSet();
        return this.childElement("scan-modules").map(scanModules -> {
            List<Element> elements = PluginDescriptorReader.elements(scanModules, "folder");
            if (elements.isEmpty()) {
                scanFolders.add(DEFAULT_SCAN_FOLDER);
            }
            return elements;
        }).map(folders -> {
            for (Element folder : folders) {
                scanFolders.add(folder.getTextTrim());
            }
            return scanFolders;
        }).orElseGet(Collections::emptyList);
    }

    private Stream<Element> getPermissionElements() {
        return PluginInformationReader.streamOptional(this.childElement("permissions")).flatMap(permissions -> PluginDescriptorReader.elements(permissions, "permission").stream()).filter(new ElementWithForApplicationsPredicate(this.applications)).filter(element -> StringUtils.isNotBlank((CharSequence)element.getTextTrim()));
    }

    private Optional<Element> getApplicationVersionElement() {
        return this.childElement("application-version");
    }

    private Stream<Element> getParamElements() {
        return this.childElements("param").filter(param -> param.attribute("name") != null);
    }

    private Optional<Element> getVendorElement() {
        return this.childElement("vendor");
    }

    private Optional<Element> getScopeElement() {
        return this.childElement("scope");
    }

    private Optional<Element> getDescriptionElement() {
        return this.childElement("description");
    }

    private static <T> Stream<T> streamOptional(Optional<T> value) {
        return value.map(Stream::of).orElseGet(Stream::empty);
    }

    private static final class ElementWithForApplicationsPredicate
    implements Predicate<Element> {
        private final Set<Application> applications;

        private ElementWithForApplicationsPredicate(Set<Application> applications) {
            this.applications = (Set)Preconditions.checkNotNull(applications);
        }

        @Override
        public boolean test(Element el) {
            String appName = el.attributeValue("application");
            return appName == null || this.applications.stream().anyMatch(app -> app != null && appName.equals(app.getKey()));
        }
    }

    private static final class GetAttributeFunction
    implements Function<Element, Optional<Attribute>> {
        private final String name;

        private GetAttributeFunction(String name) {
            this.name = name;
        }

        @Override
        public Optional<Attribute> apply(Element applicationVersion) {
            return Optional.ofNullable(applicationVersion.attribute(this.name));
        }
    }

    private static final class ParseAttributeValueAsFloatFunction
    implements Function<Attribute, Float> {
        private ParseAttributeValueAsFloatFunction() {
        }

        @Override
        public Float apply(Attribute attr) {
            return Float.valueOf(Float.parseFloat(attr.getValue()));
        }
    }
}

