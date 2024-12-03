/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleGenerator;
import com.rometools.rome.io.impl.BaseWireFeedGenerator;
import com.rometools.rome.io.impl.PluginManager;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class ModuleGenerators
extends PluginManager<ModuleGenerator> {
    private Set<Namespace> allNamespaces;

    public ModuleGenerators(String propertyKey, BaseWireFeedGenerator parentGenerator) {
        super(propertyKey, null, parentGenerator);
    }

    public ModuleGenerator getGenerator(String uri) {
        return (ModuleGenerator)this.getPlugin(uri);
    }

    @Override
    protected String getKey(ModuleGenerator obj) {
        return obj.getNamespaceUri();
    }

    public List<String> getModuleNamespaces() {
        return this.getKeys();
    }

    public void generateModules(List<Module> modules, Element element) {
        Map generators = this.getPluginMap();
        for (Module module : modules) {
            String namespaceUri = module.getUri();
            ModuleGenerator generator = (ModuleGenerator)generators.get(namespaceUri);
            if (generator == null) continue;
            generator.generate(module, element);
        }
    }

    public Set<Namespace> getAllNamespaces() {
        if (this.allNamespaces == null) {
            this.allNamespaces = new HashSet<Namespace>();
            List<String> mUris = this.getModuleNamespaces();
            for (String mUri : mUris) {
                ModuleGenerator mGen = this.getGenerator(mUri);
                this.allNamespaces.addAll(mGen.getNamespaces());
            }
        }
        return this.allNamespaces;
    }
}

