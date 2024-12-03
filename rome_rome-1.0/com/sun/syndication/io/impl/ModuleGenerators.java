/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;
import com.sun.syndication.io.impl.BaseWireFeedGenerator;
import com.sun.syndication.io.impl.PluginManager;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Element;

public class ModuleGenerators
extends PluginManager {
    private Set _allNamespaces;

    public ModuleGenerators(String propertyKey, BaseWireFeedGenerator parentGenerator) {
        super(propertyKey, null, parentGenerator);
    }

    public ModuleGenerator getGenerator(String uri) {
        return (ModuleGenerator)this.getPlugin(uri);
    }

    protected String getKey(Object obj) {
        return ((ModuleGenerator)obj).getNamespaceUri();
    }

    public List getModuleNamespaces() {
        return this.getKeys();
    }

    public void generateModules(List modules, Element element) {
        Map generators = this.getPluginMap();
        for (int i = 0; i < modules.size(); ++i) {
            Module module = (Module)modules.get(i);
            String namespaceUri = module.getUri();
            ModuleGenerator generator = (ModuleGenerator)generators.get(namespaceUri);
            if (generator == null) continue;
            generator.generate(module, element);
        }
    }

    public Set getAllNamespaces() {
        if (this._allNamespaces == null) {
            this._allNamespaces = new HashSet();
            List mUris = this.getModuleNamespaces();
            for (int i = 0; i < mUris.size(); ++i) {
                ModuleGenerator mGen = this.getGenerator((String)mUris.get(i));
                this._allNamespaces.addAll(mGen.getNamespaces());
            }
        }
        return this._allNamespaces;
    }
}

