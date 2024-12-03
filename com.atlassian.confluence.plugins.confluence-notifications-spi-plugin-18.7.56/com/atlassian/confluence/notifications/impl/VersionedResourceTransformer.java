/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.io.FilenameUtils
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.VersionedResource;
import com.atlassian.confluence.notifications.impl.VersionedResourceCompilation;
import com.atlassian.confluence.notifications.impl.VersionedResourceContext;
import com.atlassian.confluence.notifications.impl.VersionedResourceDependency;
import com.atlassian.confluence.notifications.impl.VersionedResourceNode;
import com.atlassian.confluence.notifications.impl.VersionedResourceRoot;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.Plugin;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.net.URL;
import java.util.LinkedHashMap;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionedResourceTransformer {
    private static final Logger log = LoggerFactory.getLogger(VersionedResourceTransformer.class);
    private final Element rootDescriptor;
    private final VersionedResourceRoot root;

    private VersionedResourceTransformer(Element descriptor, VersionedResourceContext context) {
        this.rootDescriptor = descriptor;
        this.root = new VersionedResourceRoot(descriptor, context);
    }

    public static VersionedResourceTransformer parse(Element descriptor, VersionedResourceContext context) {
        try {
            return new VersionedResourceTransformer(descriptor, context);
        }
        catch (RuntimeException e) {
            throw new RuntimeException(String.format("Parsing descriptor [%s] of plugin [%s] failed, see cause.", descriptor, context.getPlugin().getKey()), e);
        }
    }

    public Pair<Element, Iterable<Element>> transform() {
        Element rootWebResourceDescriptor = this.createWebResourceDescriptorFromNode(this.root);
        LinkedHashMap<String, Element> additionalWebResourceDescriptors = new LinkedHashMap<String, Element>();
        for (VersionedResourceCompilation compilation : this.root.compilations) {
            Element compilationWebResourceDescriptor = this.createWebResourceDescriptorFromNode(compilation);
            for (VersionedResourceDependency dependency : compilation.dependencies) {
                compilationWebResourceDescriptor.add(this.createDependencyElementFromNode(dependency));
            }
            for (VersionedResource resource : compilation.resources) {
                Element webResourceDescriptor = this.createWebResourceDescriptorFromResource(resource);
                additionalWebResourceDescriptors.put(webResourceDescriptor.attributeValue("key"), webResourceDescriptor);
                compilationWebResourceDescriptor.add(this.createDependencyElementFromNode(resource));
            }
            if (compilation.version().equals((Object)this.root.version())) {
                rootWebResourceDescriptor.add(this.createDependencyElementFromNode(compilation));
            }
            additionalWebResourceDescriptors.put(compilationWebResourceDescriptor.attributeValue("key"), compilationWebResourceDescriptor);
        }
        for (Element resourceElement : this.rootDescriptor.elements("resource")) {
            if (resourceElement.attributeValue("location") == null) continue;
            rootWebResourceDescriptor.add((Element)resourceElement.clone());
        }
        return Pair.pair((Object)rootWebResourceDescriptor, (Object)ImmutableList.copyOf(additionalWebResourceDescriptors.values()));
    }

    protected Element createElement(String name) {
        return DocumentHelper.createElement((String)name);
    }

    protected Element createWebResourceDescriptorFromNode(VersionedResourceNode node) {
        Element webResourceElement = this.createElement("web-resource");
        webResourceElement.addAttribute("key", node.key().getModuleKey());
        return webResourceElement;
    }

    protected Element createDependencyElementFromNode(VersionedResourceNode resource) {
        Element dependencyElement = this.createElement("dependency");
        dependencyElement.setText(resource.key().getCompleteKey());
        return dependencyElement;
    }

    private Element createWebResourceDescriptorFromResource(VersionedResource resource) {
        String resourceName = resource.name;
        String resourceBasePath = resource.base();
        Preconditions.checkArgument((!resourceName.contains("\\") && !resourceName.contains("/") ? 1 : 0) != 0, (Object)String.format("Resource names should not contain '' or '/'. Resource: {path:'%s', name: '%s'}", resourceBasePath, resourceName));
        Preconditions.checkArgument((!resourceBasePath.contains("..") ? 1 : 0) != 0, (Object)String.format("Resource bases should not contain '..'. Resource: {path:'%s', name: '%s'}", resourceBasePath, resourceName));
        Element webResourceDescriptor = this.createWebResourceDescriptorFromNode(resource);
        String templatePath = FilenameUtils.concat((String)resourceBasePath, (String)(resourceName + "-" + resource.version() + "." + resource.type())).replace("\\", "/");
        Plugin plugin = resource.context.getPlugin();
        URL templateUrl = plugin.getResource(templatePath);
        Preconditions.checkNotNull((Object)templateUrl, (String)"Path [%s] does not exist in plugin [%s]: atlassian-plugin.xml location [%s]", (Object)templatePath, (Object)plugin.getKey(), (Object)plugin.getResource("atlassian-plugin.xml"));
        for (VersionedResourceDependency dependency : resource.dependencies) {
            webResourceDescriptor.add(this.createDependencyElementFromNode(dependency));
        }
        Element resourceElement = DocumentHelper.createElement((String)"resource");
        resourceElement.addAttribute("name", resourceName);
        resourceElement.addAttribute("location", templatePath);
        resourceElement.addAttribute("type", resource.type());
        webResourceDescriptor.add(resourceElement);
        return webResourceDescriptor;
    }
}

