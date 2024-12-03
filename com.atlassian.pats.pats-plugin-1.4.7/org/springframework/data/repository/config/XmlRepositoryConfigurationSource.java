/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.beans.factory.support.DefaultBeanNameGenerator
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.context.annotation.AnnotationBeanNameGenerator
 *  org.springframework.core.env.Environment
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.config;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.data.config.TypeFilterParser;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationSourceSupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.util.ParsingUtils;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class XmlRepositoryConfigurationSource
extends RepositoryConfigurationSourceSupport {
    private static final String QUERY_LOOKUP_STRATEGY = "query-lookup-strategy";
    private static final String BASE_PACKAGE = "base-package";
    private static final String NAMED_QUERIES_LOCATION = "named-queries-location";
    private static final String REPOSITORY_IMPL_POSTFIX = "repository-impl-postfix";
    private static final String REPOSITORY_FACTORY_BEAN_CLASS_NAME = "factory-class";
    private static final String REPOSITORY_BASE_CLASS_NAME = "base-class";
    private static final String CONSIDER_NESTED_REPOSITORIES = "consider-nested-repositories";
    private static final String BOOTSTRAP_MODE = "bootstrap-mode";
    private final Element element;
    private final ParserContext context;
    private final Collection<TypeFilter> includeFilters;
    private final Collection<TypeFilter> excludeFilters;

    public XmlRepositoryConfigurationSource(Element element, ParserContext context, Environment environment) {
        super(environment, ConfigurationUtils.getRequiredClassLoader(context.getReaderContext()), context.getRegistry(), XmlRepositoryConfigurationSource.defaultBeanNameGenerator(context.getReaderContext().getReader().getBeanNameGenerator()));
        Assert.notNull((Object)element, (String)"Element must not be null!");
        this.element = element;
        this.context = context;
        TypeFilterParser parser = new TypeFilterParser(context.getReaderContext());
        this.includeFilters = parser.parseTypeFilters(element, TypeFilterParser.Type.INCLUDE);
        this.excludeFilters = parser.parseTypeFilters(element, TypeFilterParser.Type.EXCLUDE);
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.context.extractSource((Object)this.element);
    }

    @Override
    public Streamable<String> getBasePackages() {
        String attribute = this.element.getAttribute(BASE_PACKAGE);
        return Streamable.of(StringUtils.delimitedListToStringArray((String)attribute, (String)",", (String)" "));
    }

    @Override
    public Optional<Object> getQueryLookupStrategyKey() {
        return this.getNullDefaultedAttribute(this.element, QUERY_LOOKUP_STRATEGY).map(QueryLookupStrategy.Key::create);
    }

    @Override
    public Optional<String> getNamedQueryLocation() {
        return this.getNullDefaultedAttribute(this.element, NAMED_QUERIES_LOCATION);
    }

    public Element getElement() {
        return this.element;
    }

    @Override
    public Streamable<TypeFilter> getExcludeFilters() {
        return Streamable.of(this.excludeFilters);
    }

    @Override
    protected Iterable<TypeFilter> getIncludeFilters() {
        return this.includeFilters;
    }

    @Override
    public Optional<String> getRepositoryImplementationPostfix() {
        return this.getNullDefaultedAttribute(this.element, REPOSITORY_IMPL_POSTFIX);
    }

    public Optional<String> getRepositoryFactoryBeanName() {
        return this.getNullDefaultedAttribute(this.element, REPOSITORY_FACTORY_BEAN_CLASS_NAME);
    }

    @Override
    public Optional<String> getRepositoryBaseClassName() {
        return this.getNullDefaultedAttribute(this.element, REPOSITORY_BASE_CLASS_NAME);
    }

    @Override
    public Optional<String> getRepositoryFactoryBeanClassName() {
        return this.getNullDefaultedAttribute(this.element, REPOSITORY_FACTORY_BEAN_CLASS_NAME);
    }

    private Optional<String> getNullDefaultedAttribute(Element element, String attributeName) {
        String attribute = element.getAttribute(attributeName);
        return StringUtils.hasText((String)attribute) ? Optional.of(attribute) : Optional.empty();
    }

    @Override
    public boolean shouldConsiderNestedRepositories() {
        return this.getNullDefaultedAttribute(this.element, CONSIDER_NESTED_REPOSITORIES).map(Boolean::parseBoolean).orElse(false);
    }

    @Override
    public Optional<String> getAttribute(String name) {
        String xmlAttributeName = ParsingUtils.reconcatenateCamelCase(name, "-");
        String attribute = this.element.getAttribute(xmlAttributeName);
        return StringUtils.hasText((String)attribute) ? Optional.of(attribute) : Optional.empty();
    }

    @Override
    public <T> Optional<T> getAttribute(String name, Class<T> type) {
        Assert.isAssignable(String.class, type, (String)"Only String attribute lookups are allowed for XML namespaces!");
        return this.getAttribute(name);
    }

    @Override
    public boolean usesExplicitFilters() {
        return !this.includeFilters.isEmpty() || !this.excludeFilters.isEmpty();
    }

    @Override
    public BootstrapMode getBootstrapMode() {
        String attribute = this.element.getAttribute(BOOTSTRAP_MODE);
        return StringUtils.hasText((String)attribute) ? BootstrapMode.valueOf(attribute.toUpperCase(Locale.US)) : BootstrapMode.DEFAULT;
    }

    @Override
    @NonNull
    public String getResourceDescription() {
        Object source = this.getSource();
        return source == null ? "" : source.toString();
    }

    private static BeanNameGenerator defaultBeanNameGenerator(@Nullable BeanNameGenerator generator) {
        return generator == null || DefaultBeanNameGenerator.class.equals(generator.getClass()) ? new AnnotationBeanNameGenerator() : generator;
    }
}

