/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 *  org.jdom2.Parent
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.WireFeedGenerator;
import com.rometools.rome.io.impl.ModuleGenerators;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Parent;

public abstract class BaseWireFeedGenerator
implements WireFeedGenerator {
    private static final String FEED_MODULE_GENERATORS_POSFIX_KEY = ".feed.ModuleGenerator.classes";
    private static final String ITEM_MODULE_GENERATORS_POSFIX_KEY = ".item.ModuleGenerator.classes";
    private static final String PERSON_MODULE_GENERATORS_POSFIX_KEY = ".person.ModuleGenerator.classes";
    private final String type;
    private final ModuleGenerators feedModuleGenerators;
    private final ModuleGenerators itemModuleGenerators;
    private final ModuleGenerators personModuleGenerators;
    private final Namespace[] allModuleNamespaces;

    protected BaseWireFeedGenerator(String type) {
        this.type = type;
        this.feedModuleGenerators = new ModuleGenerators(type + FEED_MODULE_GENERATORS_POSFIX_KEY, this);
        this.itemModuleGenerators = new ModuleGenerators(type + ITEM_MODULE_GENERATORS_POSFIX_KEY, this);
        this.personModuleGenerators = new ModuleGenerators(type + PERSON_MODULE_GENERATORS_POSFIX_KEY, this);
        HashSet<Namespace> allModuleNamespaces = new HashSet<Namespace>();
        for (Namespace namespace : this.feedModuleGenerators.getAllNamespaces()) {
            allModuleNamespaces.add(namespace);
        }
        for (Namespace namespace : this.itemModuleGenerators.getAllNamespaces()) {
            allModuleNamespaces.add(namespace);
        }
        for (Namespace namespace : this.personModuleGenerators.getAllNamespaces()) {
            allModuleNamespaces.add(namespace);
        }
        this.allModuleNamespaces = new Namespace[allModuleNamespaces.size()];
        allModuleNamespaces.toArray(this.allModuleNamespaces);
    }

    @Override
    public String getType() {
        return this.type;
    }

    protected void generateModuleNamespaceDefs(Element root) {
        for (Namespace allModuleNamespace : this.allModuleNamespaces) {
            root.addNamespaceDeclaration(allModuleNamespace);
        }
    }

    protected void generateFeedModules(List<Module> modules, Element feed) {
        this.feedModuleGenerators.generateModules(modules, feed);
    }

    public void generateItemModules(List<Module> modules, Element item) {
        this.itemModuleGenerators.generateModules(modules, item);
    }

    public void generatePersonModules(List<Module> modules, Element person) {
        this.personModuleGenerators.generateModules(modules, person);
    }

    protected void generateForeignMarkup(Element element, List<Element> foreignElements) {
        if (foreignElements != null) {
            for (Element foreignElement : foreignElements) {
                Parent parent = foreignElement.getParent();
                if (parent != null) {
                    parent.removeContent((Content)foreignElement);
                }
                element.addContent((Content)foreignElement);
            }
        }
    }

    protected static void purgeUnusedNamespaceDeclarations(Element root) {
        HashSet<String> usedPrefixes = new HashSet<String>();
        BaseWireFeedGenerator.collectUsedPrefixes(root, usedPrefixes);
        List list = root.getAdditionalNamespaces();
        ArrayList additionalNamespaces = new ArrayList();
        additionalNamespaces.addAll(list);
        for (Namespace ns : additionalNamespaces) {
            String prefix = ns.getPrefix();
            if (prefix == null || prefix.length() <= 0 || usedPrefixes.contains(prefix)) continue;
            root.removeNamespaceDeclaration(ns);
        }
    }

    private static void collectUsedPrefixes(Element el, Set<String> collector) {
        String prefix = el.getNamespacePrefix();
        if (prefix != null && prefix.length() > 0 && !collector.contains(prefix)) {
            collector.add(prefix);
        }
        List kids = el.getChildren();
        for (Element kid : kids) {
            BaseWireFeedGenerator.collectUsedPrefixes(kid, collector);
        }
    }
}

