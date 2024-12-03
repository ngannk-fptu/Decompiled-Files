/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleParser;
import com.rometools.rome.io.WireFeedParser;
import com.rometools.rome.io.impl.PluginManager;
import com.rometools.utils.Lists;
import java.util.List;
import java.util.Locale;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class ModuleParsers
extends PluginManager<ModuleParser> {
    public ModuleParsers(String propertyKey, WireFeedParser parentParser) {
        super(propertyKey, parentParser, null);
    }

    @Override
    public String getKey(ModuleParser obj) {
        return obj.getNamespaceUri();
    }

    public List<String> getModuleNamespaces() {
        return this.getKeys();
    }

    public List<Module> parseModules(Element root, Locale locale) {
        List parsers = this.getPlugins();
        List modules = null;
        for (ModuleParser parser : parsers) {
            Module module;
            String namespaceUri = parser.getNamespaceUri();
            Namespace namespace = Namespace.getNamespace((String)namespaceUri);
            if (!this.hasElementsFrom(root, namespace) || (module = parser.parse(root, locale)) == null) continue;
            modules = Lists.createWhenNull(modules);
            modules.add(module);
        }
        return modules;
    }

    private boolean hasElementsFrom(Element root, Namespace namespace) {
        boolean hasElements = false;
        for (Element child : root.getChildren()) {
            Namespace childNamespace = child.getNamespace();
            if (!namespace.equals((Object)childNamespace)) continue;
            hasElements = true;
            break;
        }
        return hasElements;
    }
}

