/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
 *  org.jdom.Element
 *  org.jdom.Namespace
 *  org.jdom.Parent
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.impl.ModuleGenerators;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;

public abstract class BaseWireFeedGenerator
implements WireFeedGenerator {
    private static final String FEED_MODULE_GENERATORS_POSFIX_KEY = ".feed.ModuleGenerator.classes";
    private static final String ITEM_MODULE_GENERATORS_POSFIX_KEY = ".item.ModuleGenerator.classes";
    private static final String PERSON_MODULE_GENERATORS_POSFIX_KEY = ".person.ModuleGenerator.classes";
    private String _type;
    private ModuleGenerators _feedModuleGenerators;
    private ModuleGenerators _itemModuleGenerators;
    private ModuleGenerators _personModuleGenerators;
    private Namespace[] _allModuleNamespaces;

    protected BaseWireFeedGenerator(String type) {
        this._type = type;
        this._feedModuleGenerators = new ModuleGenerators(type + FEED_MODULE_GENERATORS_POSFIX_KEY, this);
        this._itemModuleGenerators = new ModuleGenerators(type + ITEM_MODULE_GENERATORS_POSFIX_KEY, this);
        this._personModuleGenerators = new ModuleGenerators(type + PERSON_MODULE_GENERATORS_POSFIX_KEY, this);
        HashSet allModuleNamespaces = new HashSet();
        Iterator i = this._feedModuleGenerators.getAllNamespaces().iterator();
        while (i.hasNext()) {
            allModuleNamespaces.add(i.next());
        }
        i = this._itemModuleGenerators.getAllNamespaces().iterator();
        while (i.hasNext()) {
            allModuleNamespaces.add(i.next());
        }
        i = this._personModuleGenerators.getAllNamespaces().iterator();
        while (i.hasNext()) {
            allModuleNamespaces.add(i.next());
        }
        this._allModuleNamespaces = new Namespace[allModuleNamespaces.size()];
        allModuleNamespaces.toArray(this._allModuleNamespaces);
    }

    public String getType() {
        return this._type;
    }

    protected void generateModuleNamespaceDefs(Element root) {
        for (int i = 0; i < this._allModuleNamespaces.length; ++i) {
            root.addNamespaceDeclaration(this._allModuleNamespaces[i]);
        }
    }

    protected void generateFeedModules(List modules, Element feed) {
        this._feedModuleGenerators.generateModules(modules, feed);
    }

    public void generateItemModules(List modules, Element item) {
        this._itemModuleGenerators.generateModules(modules, item);
    }

    public void generatePersonModules(List modules, Element person) {
        this._personModuleGenerators.generateModules(modules, person);
    }

    protected void generateForeignMarkup(Element e, List foreignMarkup) {
        if (foreignMarkup != null) {
            Iterator elems = foreignMarkup.iterator();
            while (elems.hasNext()) {
                Element elem = (Element)elems.next();
                Parent parent = elem.getParent();
                if (parent != null) {
                    parent.removeContent((Content)elem);
                }
                e.addContent((Content)elem);
            }
        }
    }

    protected static void purgeUnusedNamespaceDeclarations(Element root) {
        HashSet usedPrefixes = new HashSet();
        BaseWireFeedGenerator.collectUsedPrefixes(root, usedPrefixes);
        List list = root.getAdditionalNamespaces();
        ArrayList additionalNamespaces = new ArrayList();
        additionalNamespaces.addAll(list);
        for (int i = 0; i < additionalNamespaces.size(); ++i) {
            Namespace ns = (Namespace)additionalNamespaces.get(i);
            String prefix = ns.getPrefix();
            if (prefix == null || prefix.length() <= 0 || usedPrefixes.contains(prefix)) continue;
            root.removeNamespaceDeclaration(ns);
        }
    }

    private static void collectUsedPrefixes(Element el, Set collector) {
        String prefix = el.getNamespacePrefix();
        if (prefix != null && prefix.length() > 0 && !collector.contains(prefix)) {
            collector.add(prefix);
        }
        List kids = el.getChildren();
        for (int i = 0; i < kids.size(); ++i) {
            BaseWireFeedGenerator.collectUsedPrefixes((Element)kids.get(i), collector);
        }
    }
}

