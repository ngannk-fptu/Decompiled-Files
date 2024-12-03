/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.annotations.GraphQLExtensions
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.spi.CombinedGraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLExtensions
 *  com.atlassian.graphql.spi.GraphQLProviders
 *  com.atlassian.graphql.spi.GraphQLTypeContributor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang.StringUtils
 *  org.dom4j.Element
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.wiring.BundleWiring
 */
package com.atlassian.plugins.graphql.api;

import com.atlassian.graphql.annotations.GraphQLExtensions;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.spi.CombinedGraphQLExtensions;
import com.atlassian.graphql.spi.GraphQLProviders;
import com.atlassian.graphql.spi.GraphQLTypeContributor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

public class GraphQLProvidersModuleDescriptor
extends AbstractModuleDescriptor<GraphQLProviders> {
    private static Pattern RESOURCE_SUFFIX_REGEX = Pattern.compile("\\.class$");
    private GraphQLProviders graphQLProviders;
    private String path;
    private List<Class> providerClasses;
    private OsgiPlugin osgiPlugin;

    public GraphQLProvidersModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Class<GraphQLProviders> getModuleClass() {
        return GraphQLProviders.class;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.osgiPlugin = (OsgiPlugin)plugin;
        Set<String> packageNames = this.parsePackages(element);
        this.path = element.attributeValue("path");
        this.providerClasses = this.loadProviderClasses(packageNames, this.osgiPlugin);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"package").withError("At least one 'package' element is required")});
    }

    public GraphQLProviders getModule() {
        if (this.graphQLProviders == null) {
            List<Object> providers = this.createProviderObjects(GraphQLProvider.class);
            List<Object> extensions = this.createProviderObjects(GraphQLExtensions.class);
            this.graphQLProviders = new GraphQLProviders(this.path, providers, GraphQLProvidersModuleDescriptor.toGraphQLExtensions(extensions));
        }
        return this.graphQLProviders;
    }

    private <T extends Annotation> List<Object> createProviderObjects(Class<T> annotationType) {
        return this.providerClasses.stream().filter(providerClass -> GraphQLProvidersModuleDescriptor.hasAnnotation(providerClass, annotationType)).map(providerClass -> this.osgiPlugin.getContainerAccessor().createBean(providerClass)).collect(Collectors.toList());
    }

    private static com.atlassian.graphql.spi.GraphQLExtensions toGraphQLExtensions(List<Object> extensions) {
        ArrayList<com.atlassian.graphql.spi.GraphQLExtensions> list = new ArrayList<com.atlassian.graphql.spi.GraphQLExtensions>();
        for (Object obj : extensions) {
            if (obj instanceof com.atlassian.graphql.spi.GraphQLExtensions) {
                list.add((com.atlassian.graphql.spi.GraphQLExtensions)obj);
                continue;
            }
            if (obj instanceof GraphQLTypeContributor) {
                list.add(com.atlassian.graphql.spi.GraphQLExtensions.of((GraphQLTypeContributor)((GraphQLTypeContributor)obj)));
                continue;
            }
            throw new RuntimeException(String.format("Class '%s' must implement interface %s or %s", obj.getClass().getName(), GraphQLTypeContributor.class.getName(), com.atlassian.graphql.spi.GraphQLExtensions.class.getName()));
        }
        return CombinedGraphQLExtensions.combine(list);
    }

    private List<Class> loadProviderClasses(Iterable<String> packageNames, OsgiPlugin osgiPlugin) {
        ArrayList<Class> list = new ArrayList<Class>();
        BundleWiring bundleWiring = (BundleWiring)osgiPlugin.getBundle().adapt(BundleWiring.class);
        Collection bundleResourceNames = bundleWiring.listResources("/", "*.class", 3);
        for (String packageName : packageNames) {
            List<Class> classesInPackage = GraphQLProvidersModuleDescriptor.loadClassesInPackage(packageName, osgiPlugin.getBundle(), bundleResourceNames);
            for (Class providerClass : classesInPackage) {
                if (!this.isGraphQLProviderClass(providerClass)) continue;
                list.add(providerClass);
            }
        }
        return list;
    }

    private static List<Class> loadClassesInPackage(String packageName, Bundle pluginBundle, Iterable<String> bundleResourceNames) {
        String packagePrefix = GraphQLProvidersModuleDescriptor.getResourceNamePrefixForPackage(packageName);
        return StreamSupport.stream(bundleResourceNames.spliterator(), false).filter(name -> name.startsWith(packagePrefix)).sorted().map(className -> GraphQLProvidersModuleDescriptor.loadClass(pluginBundle, className)).collect(Collectors.toList());
    }

    private static Class loadClass(Bundle pluginBundle, String providerName) throws PluginParseException {
        try {
            String className = RESOURCE_SUFFIX_REGEX.matcher(providerName).replaceAll("").replace('/', '.');
            return pluginBundle.loadClass(className);
        }
        catch (ClassNotFoundException ex) {
            throw new PluginParseException((Throwable)ex);
        }
    }

    private boolean isGraphQLProviderClass(Class providerClass) {
        return GraphQLProvidersModuleDescriptor.hasAnnotation(providerClass, GraphQLProvider.class) || GraphQLProvidersModuleDescriptor.hasAnnotation(providerClass, GraphQLExtensions.class);
    }

    private Set<String> parsePackages(Element element) {
        List packageElements = element.elements("package");
        return packageElements.stream().map(Element::getTextTrim).collect(Collectors.toSet());
    }

    private static String getResourceNamePrefixForPackage(String packageName) {
        String providerClassNamePrefix = StringUtils.replaceChars((String)packageName, (char)'.', (char)'/');
        return !providerClassNamePrefix.endsWith("/") ? providerClassNamePrefix + '/' : providerClassNamePrefix;
    }

    private static <T extends Annotation> boolean hasAnnotation(AnnotatedElement annotated, Class<T> annotationClass) {
        return Arrays.stream(annotated.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getName().equals(annotationClass.getName()));
    }
}

