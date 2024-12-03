/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.Category
 *  com.atlassian.gadgets.plugins.DashboardItemModule
 *  com.atlassian.gadgets.plugins.DashboardItemModule$Author
 *  com.atlassian.gadgets.plugins.DashboardItemModule$DirectoryDefinition
 *  com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.NoOpContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.conditions.AlwaysDisplayCondition
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.util.concurrent.NotNull
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.gadgets.publisher;

import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.plugins.DashboardItemModule;
import com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor;
import com.atlassian.gadgets.publisher.ClientSideDashboardItemModule;
import com.atlassian.gadgets.publisher.ResourceBackedDashboardItemModule;
import com.atlassian.gadgets.publisher.internal.impl.GadgetConditionElementParser;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.NoOpContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.conditions.AlwaysDisplayCondition;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.util.concurrent.NotNull;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class DashboardItemModuleDescriptorImpl
extends AbstractModuleDescriptor<DashboardItemModule>
implements DashboardItemModuleDescriptor {
    private final PluginAccessor pluginAccessor;
    private final WebFragmentHelper webFragmentHelper;
    private final GadgetConditionElementParser conditionElementParser;
    private Option<String> gadgetToReplace = Option.none();
    private Option<String> amdModule = Option.none();
    private Option<String> webResourceKey = Option.none();
    private ContextProvider contextProvider;
    private Condition condition;
    private Element element;
    private boolean configurable = false;
    private Option<DashboardItemModule.DirectoryDefinition> definition;
    private Predicate<? super ResourceDescriptor> usesSoyTemplate = new Predicate<ResourceDescriptor>(){

        public boolean apply(ResourceDescriptor input) {
            return "view".equalsIgnoreCase(input.getName());
        }
    };

    public DashboardItemModuleDescriptorImpl(ModuleFactory moduleFactory, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport WebFragmentHelper webFragmentHelper, GadgetConditionElementParser gadgetConditionElementParser) {
        super(moduleFactory);
        this.pluginAccessor = pluginAccessor;
        this.webFragmentHelper = webFragmentHelper;
        this.conditionElementParser = gadgetConditionElementParser;
    }

    public void init(@NotNull Plugin plugin, @NotNull Element element) throws PluginParseException {
        Element webResourceKeyElement;
        Element amdModuleElement;
        super.init(plugin, element);
        Element replaceGadgetElement = element.element("replace-gadget-spec-uri");
        if (replaceGadgetElement != null) {
            this.gadgetToReplace = Option.option((Object)StringUtils.trim((String)replaceGadgetElement.getStringValue()));
        }
        if ((amdModuleElement = element.element("amd-module")) != null) {
            this.amdModule = Option.option((Object)StringUtils.trim((String)amdModuleElement.getText()));
        }
        if ((webResourceKeyElement = element.element("web-resource-key")) != null) {
            this.webResourceKey = Option.option((Object)StringUtils.trim((String)webResourceKeyElement.getText()));
        }
        this.definition = Option.option((Object)element.element("definition")).map((Function)new Function<Element, DashboardItemModule.DirectoryDefinition>(){

            @Override
            public DashboardItemModule.DirectoryDefinition apply(Element element) {
                return DirectoryDefinitionImpl.fromXMLElement(element);
            }
        });
        if (this.definition.isEmpty() && this.gadgetToReplace.isEmpty()) {
            throw new PluginParseException("Dashboard item has to provide a definition or define a definition which it replaces.");
        }
        this.configurable = Boolean.parseBoolean(element.attributeValue("configurable"));
        this.element = element;
    }

    public DashboardItemModule getModule() {
        Option resourceDescriptor = Option.option((Object)Iterables.find((Iterable)this.resources.getResourceDescriptors(), this.usesSoyTemplate, null));
        if (resourceDescriptor.isDefined()) {
            return new ResourceBackedDashboardItemModule(this.pluginAccessor, (ResourceDescriptor)resourceDescriptor.get(), this.plugin, this.contextProvider, this.amdModule, this.configurable, this.definition, this.condition, this.webResourceKey);
        }
        return new ClientSideDashboardItemModule(this.amdModule, this.configurable, this.definition, this.condition, this.webResourceKey);
    }

    public void enabled() {
        super.enabled();
        this.contextProvider = this.getContextProvider();
        this.condition = this.buildCondition();
    }

    public Option<String> getGadgetSpecUriToReplace() {
        return this.gadgetToReplace;
    }

    public Option<DashboardItemModule.DirectoryDefinition> getDirectoryDefinition() {
        return this.definition;
    }

    private ContextProvider getContextProvider() {
        try {
            Element contextProviderElement = this.element.element("context-provider");
            if (contextProviderElement == null) {
                return new NoOpContextProvider();
            }
            ContextProvider context = this.webFragmentHelper.loadContextProvider(contextProviderElement.attributeValue("class"), this.plugin);
            context.init(LoaderUtils.getParams((Element)contextProviderElement));
            return context;
        }
        catch (ClassCastException e) {
            throw new PluginParseException("Configured context-provider class does not implement the ContextProvider interface", (Throwable)e);
        }
        catch (ConditionLoadingException cle) {
            throw new PluginParseException("Unable to load the module's display conditions: " + cle.getMessage(), (Throwable)cle);
        }
    }

    private Condition buildCondition() {
        Element condition = this.element.element("condition");
        Element compositeConditions = this.element.element("conditions");
        if (condition != null && compositeConditions != null) {
            throw new PluginParseException("You can't provide a composite condition and a single condition within single DashboardItemModule.");
        }
        if (condition != null) {
            return this.conditionElementParser.makeDashboardItemCondition(condition, this.plugin);
        }
        if (compositeConditions != null) {
            return this.conditionElementParser.makeDashboardItemConditions(compositeConditions, this.plugin);
        }
        return new AlwaysDisplayCondition();
    }

    private static final class DirectoryDefinitionImpl
    implements DashboardItemModule.DirectoryDefinition {
        private final String title;
        private final Option<String> i18nTitleKey;
        private final ImmutableSet<Category> categories;
        private final Option<URI> thumbnail;
        private final DashboardItemModule.Author author;

        public static DirectoryDefinitionImpl fromXMLElement(Element specificationElement) {
            Element title = DirectoryDefinitionImpl.checkSpecificationElementNotNull("title", specificationElement);
            Option i18nTitleKey = Option.option((Object)title.attributeValue("key"));
            Element authorElement = DirectoryDefinitionImpl.checkSpecificationElementNotNull("author", specificationElement);
            AuthorImpl author = AuthorImpl.fromXMLElement(authorElement);
            ImmutableSet<Category> categories = DirectoryDefinitionImpl.parseCategories(specificationElement.element("categories"));
            return new DirectoryDefinitionImpl(StringUtils.trim((String)title.getText()), (Option<String>)i18nTitleKey, categories, DirectoryDefinitionImpl.getThumbnail(specificationElement), author);
        }

        private static Option<URI> getThumbnail(Element root) {
            return Option.option((Object)root.element("thumbnail")).flatMap((Function)new Function<Element, Option<URI>>(){

                @Override
                public Option<URI> apply(Element input) {
                    Attribute location = (Attribute)Preconditions.checkNotNull((Object)input.attribute("location"), (Object)"location attribute is required for thumbnail element");
                    return Option.some((Object)URI.create(location.getValue()));
                }
            });
        }

        private DirectoryDefinitionImpl(String title, Option<String> i18nTitleKey, ImmutableSet<Category> categories, Option<URI> thumbnail, DashboardItemModule.Author author) {
            this.i18nTitleKey = i18nTitleKey;
            this.title = (String)Preconditions.checkNotNull((Object)title);
            this.categories = (ImmutableSet)Preconditions.checkNotNull(categories);
            this.thumbnail = (Option)Preconditions.checkNotNull(thumbnail);
            this.author = (DashboardItemModule.Author)Preconditions.checkNotNull((Object)author);
        }

        public String getTitle() {
            return this.title;
        }

        public Option<String> getTitleI18nKey() {
            return this.i18nTitleKey;
        }

        public DashboardItemModule.Author getAuthor() {
            return this.author;
        }

        public Set<Category> getCategories() {
            return this.categories;
        }

        public Option<URI> getThumbnail() {
            return this.thumbnail;
        }

        private static ImmutableSet<Category> parseCategories(Element categories) {
            if (categories == null) {
                return ImmutableSet.of();
            }
            List categoriesList = categories.elements("category");
            return ImmutableSet.copyOf((Iterable)io.atlassian.fugue.Iterables.transform((Iterable)categoriesList, (Function)new Function<Element, Category>(){

                @Override
                public Category apply(Element categoryElement) {
                    return Category.named((String)StringUtils.trim((String)categoryElement.getText()));
                }
            }));
        }

        private static Element checkSpecificationElementNotNull(String elementName, Element element) {
            Element childElement = element.element(elementName);
            if (childElement == null) {
                throw new PluginParseException(String.format("%s of the dashboard item is required", elementName));
            }
            return childElement;
        }
    }

    private static final class AuthorImpl
    implements DashboardItemModule.Author {
        private final String name;
        private final Option<String> email;

        public static AuthorImpl fromXMLElement(Element author) {
            final Element authorName = author.element("name");
            if (authorName == null) {
                throw new PluginParseException("Author's name is a required attribute");
            }
            return (AuthorImpl)Option.option((Object)author.element("email")).fold((Supplier)new Supplier<AuthorImpl>(){

                @Override
                public AuthorImpl get() {
                    return new AuthorImpl(StringUtils.trim((String)authorName.getText()), Option.none());
                }
            }, (Function)new Function<Element, AuthorImpl>(){

                @Override
                public AuthorImpl apply(Element email) {
                    return new AuthorImpl(StringUtils.trim((String)authorName.getText()), Option.option((Object)StringUtils.trim((String)email.getText())));
                }
            });
        }

        private AuthorImpl(String name, Option<String> email) {
            this.name = name;
            this.email = email;
        }

        public String getFullname() {
            return this.name;
        }

        public Option<String> getEmail() {
            return this.email;
        }
    }
}

