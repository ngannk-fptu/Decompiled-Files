/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.WireFeedParser;
import com.sun.syndication.io.impl.PluginManager;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;

public class ModuleParsers
extends PluginManager {
    public ModuleParsers(String propertyKey, WireFeedParser parentParser) {
        super(propertyKey, parentParser, null);
    }

    public String getKey(Object obj) {
        return ((ModuleParser)obj).getNamespaceUri();
    }

    public List getModuleNamespaces() {
        return this.getKeys();
    }

    public List parseModules(Element root) {
        List parsers = this.getPlugins();
        ArrayList<Module> modules = null;
        for (int i = 0; i < parsers.size(); ++i) {
            Module module;
            ModuleParser parser = (ModuleParser)parsers.get(i);
            String namespaceUri = parser.getNamespaceUri();
            Namespace namespace = Namespace.getNamespace((String)namespaceUri);
            if (!this.hasElementsFrom(root, namespace) || (module = parser.parse(root)) == null) continue;
            if (modules == null) {
                modules = new ArrayList<Module>();
            }
            modules.add(module);
        }
        return modules;
    }

    private boolean hasElementsFrom(Element root, Namespace namespace) {
        boolean hasElements = false;
        if (!hasElements) {
            List children = root.getChildren();
            for (int i = 0; !hasElements && i < children.size(); ++i) {
                Element child = (Element)children.get(i);
                hasElements = namespace.equals((Object)child.getNamespace());
            }
        }
        return hasElements;
    }
}

