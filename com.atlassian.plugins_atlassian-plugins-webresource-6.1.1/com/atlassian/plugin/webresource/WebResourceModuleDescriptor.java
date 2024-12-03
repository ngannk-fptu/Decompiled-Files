/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.WebResourceTransformation;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.condition.UrlReadingConditionElementParser;
import com.atlassian.plugin.webresource.data.WebResourceDataProviderParser;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.snapshot.Deprecation;
import com.atlassian.plugin.webresource.transformer.TransformerCache;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerModuleDescriptor;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class WebResourceModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    private final HostContainer hostContainer;
    private boolean isRootPage;
    private List<String> dependencies = Collections.emptyList();
    private Set<String> contextDependencies = Collections.emptySet();
    private Set<String> contexts = Collections.emptySet();
    private boolean disableMinification;
    private WebResourceDataProviderParser dataProviderParser;
    private Deprecation deprecation;
    private Map<String, WebResourceDataProvider> dataProviders = Collections.emptyMap();
    private List<WebResourceTransformation> webResourceTransformations = Collections.emptyList();
    private UrlReadingConditionElementParser conditionElementParser;
    private Element element;
    private DecoratingCondition condition;

    @Deprecated
    public WebResourceModuleDescriptor(HostContainer hostContainer) {
        this(ModuleFactory.LEGACY_MODULE_FACTORY, hostContainer);
    }

    public WebResourceModuleDescriptor(ModuleFactory moduleFactory, HostContainer hostContainer) {
        super(moduleFactory);
        this.conditionElementParser = new UrlReadingConditionElementParser(hostContainer);
        this.hostContainer = hostContainer;
    }

    private static String resolveLocalDependency(String rawKey, Plugin plugin) {
        if (Config.isWebResourceKey(rawKey) && rawKey.startsWith(":")) {
            return plugin.getKey() + rawKey;
        }
        return rawKey;
    }

    private static List<String> resolveLocalDependencyList(List<String> dependencies, Plugin plugin) {
        return Collections.unmodifiableList(dependencies.stream().map(key -> WebResourceModuleDescriptor.resolveLocalDependency(key, plugin)).collect(Collectors.toList()));
    }

    private static Set<String> resolveLocalDependencySet(Set<String> dependencies, Plugin plugin) {
        return Collections.unmodifiableSet(dependencies.stream().map(key -> WebResourceModuleDescriptor.resolveLocalDependency(key, plugin)).collect(Collectors.toSet()));
    }

    private static Deprecation parseDeprecation(Element element, @Nonnull String key) {
        Element depEl = element.element("deprecated");
        Deprecation depNotice = null;
        if (depEl != null) {
            depNotice = new Deprecation(key);
            depNotice.setSinceVersion(depEl.attributeValue("since"));
            depNotice.setRemoveInVersion(depEl.attributeValue("remove"));
            depNotice.setAlternative(depEl.attributeValue("alternative"));
            depNotice.setExtraInfo(depEl.getText());
        }
        return depNotice;
    }

    public static List<WebResourceTransformation> parseTransformations(Element element) {
        List transformations = element.elements("transformation");
        if (!transformations.isEmpty()) {
            ArrayList<WebResourceTransformation> trans = new ArrayList<WebResourceTransformation>(transformations.size());
            for (Element e : transformations) {
                trans.add(new WebResourceTransformation(e));
            }
            return ImmutableList.copyOf(trans);
        }
        return Collections.emptyList();
    }

    public static DecoratingCondition parseCondition(UrlReadingConditionElementParser conditionElementParser, Plugin plugin, Element element) {
        try {
            return (DecoratingCondition)conditionElementParser.makeConditions(plugin, element, 1);
        }
        catch (PluginParseException e) {
            throw new RuntimeException("Unable to enable web resource due to issue processing condition", e);
        }
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.dependencies = WebResourceModuleDescriptor.resolveLocalDependencyList(this.parseDependencies(element, "web-resource", "dependency"), plugin);
        this.contextDependencies = this.parseDependencySet(element, "dependencies", "context");
        this.isRootPage = element.element("root-page") != null;
        LinkedHashSet<String> ctxs = new LinkedHashSet<String>(this.contexts.size());
        ctxs.add(this.getCompleteKey());
        List contexts = element.elements("context");
        if (!contexts.isEmpty()) {
            for (Element contextElement : contexts) {
                ctxs.add(contextElement.getTextTrim());
            }
        }
        this.contexts = Collections.unmodifiableSet(ctxs);
        this.webResourceTransformations = WebResourceModuleDescriptor.parseTransformations(element);
        this.deprecation = WebResourceModuleDescriptor.parseDeprecation(element, this.getCompleteKey());
        this.dataProviderParser = new WebResourceDataProviderParser(this.hostContainer, element.elements("data"));
        Attribute minifiedAttribute = element.attribute("disable-minification");
        this.disableMinification = minifiedAttribute == null ? false : Boolean.valueOf(minifiedAttribute.getValue());
        this.element = element;
    }

    private Set<String> parseDependencySet(Element element, String parentElementName, String subElementName) {
        return Collections.unmodifiableSet(Stream.of(element.element(parentElementName)).filter(e -> e != null).flatMap(parentElement -> parentElement.elements(subElementName).stream()).map(Element::getTextTrim).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private List<String> parseDependencies(Element element, String name, String legacyName) {
        List webResourceDependencyElement = element.elements(legacyName);
        List webResourceDependencies = webResourceDependencyElement.stream().map(Element::getTextTrim).collect(Collectors.toList());
        Option dependenciesElementOpt = Option.option((Object)element.element("dependencies"));
        List dependenciesElementWebResources = (List)dependenciesElementOpt.map(dependenciesElement -> {
            List webResources = dependenciesElement.elements(name);
            return webResources.stream().map(Element::getTextTrim).collect(Collectors.toList());
        }).getOrElse(new ArrayList());
        ArrayList allWebResourceDependencies = new ArrayList(webResourceDependencies.size() + dependenciesElementWebResources.size());
        allWebResourceDependencies.addAll(webResourceDependencies);
        allWebResourceDependencies.addAll(dependenciesElementWebResources);
        return ImmutableList.copyOf(allWebResourceDependencies);
    }

    public Void getModule() {
        throw new UnsupportedOperationException("There is no module for Web Resources");
    }

    public void enabled() {
        super.enabled();
        this.condition = WebResourceModuleDescriptor.parseCondition(this.conditionElementParser, this.plugin, this.element);
        try {
            this.dataProviders = this.dataProviderParser.createDataProviders(this.plugin, ((Object)((Object)this)).getClass());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to enable web resource due to an issue processing data-provider", e);
        }
        catch (PluginParseException e) {
            throw new RuntimeException("Unable to enable web resource due to an issue processing data-provider", e);
        }
    }

    public void disabled() {
        super.disabled();
        this.condition = null;
        this.dataProviders = Collections.emptyMap();
    }

    public boolean isDeprecated() {
        return this.deprecation != null;
    }

    public Deprecation getDeprecation() {
        return this.deprecation;
    }

    public Set<String> getContexts() {
        return this.contexts;
    }

    public List<String> getDependencies() {
        return this.dependencies;
    }

    public boolean isRootPage() {
        return this.isRootPage;
    }

    public Set<String> getContextDependencies() {
        return this.contextDependencies;
    }

    public List<WebResourceTransformation> getTransformations() {
        return this.webResourceTransformations;
    }

    public DecoratingCondition getCondition() {
        return this.condition;
    }

    public boolean isDisableMinification() {
        return this.disableMinification;
    }

    public boolean canEncodeStateIntoUrl() {
        return this.getCondition() == null || this.getCondition().canEncodeStateIntoUrl();
    }

    public boolean shouldDisplay(QueryParams params) {
        return this.getCondition() == null || this.getCondition().shouldDisplay(params);
    }

    public boolean shouldDisplayImmediate() {
        UrlBuildingStrategy urlBuilderStrategy = UrlBuildingStrategy.normal();
        return this.getCondition() == null || this.getCondition().shouldDisplayImmediate((Map<String, Object>)ImmutableMap.of(), urlBuilderStrategy);
    }

    public Map<String, WebResourceDataProvider> getDataProviders() {
        return this.dataProviders;
    }

    @Deprecated
    public Set<String> getDeprecatedConditionKeys() {
        final HashSet<String> allConditions = new HashSet<String>();
        UrlReadingConditionElementParser parser = new UrlReadingConditionElementParser(this.hostContainer){

            @Override
            protected DecoratingCondition makeConditionImplementation(Plugin plugin, Element element) throws PluginParseException {
                DecoratingCondition condition = super.makeConditionImplementation(plugin, element);
                if (!condition.canEncodeStateIntoUrl()) {
                    allConditions.add(element.attributeValue("class"));
                }
                return condition;
            }
        };
        parser.makeConditions(this.plugin, this.element, 1);
        return allConditions;
    }

    @Deprecated
    public Set<String> getDeprecatedTransformKeys(TransformerCache transformerCache) {
        HashSet<String> allTransforms = new HashSet<String>();
        for (WebResourceTransformation transformation : this.getTransformations()) {
            for (WebResourceTransformerModuleDescriptor descriptor : transformation.getDeprecatedTransformers(transformerCache)) {
                allTransforms.add(descriptor.getCompleteKey());
            }
        }
        return allTransforms;
    }
}

