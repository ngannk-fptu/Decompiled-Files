/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.module.PluginProvidedDecoratorModule;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

public class DecoratorModuleDescriptor
extends AbstractModuleDescriptor<PluginProvidedDecoratorModule> {
    static final String SINGLE_PATTERN = "pattern";
    private List<String> patterns = new ArrayList<String>();
    private String page;

    public DecoratorModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.initPageElement(element);
        this.initPatternElements(element);
    }

    private void initPageElement(Element element) {
        if (element.attribute("page") == null) {
            throw new PluginParseException("No 'page' attribute specified for decorator module " + this.getName());
        }
        this.page = element.attributeValue("page");
    }

    private void initPatternElements(Element parent) {
        Iterator it = parent.elementIterator();
        while (it.hasNext()) {
            Element elem = (Element)it.next();
            if (!SINGLE_PATTERN.equals(elem.getName())) continue;
            if (!this.isSimplePattern(elem.getStringValue())) {
                throw new PluginParseException("incorrect syntax for pattern element : " + elem.getStringValue());
            }
            this.patterns.add(elem.getStringValue());
        }
    }

    private boolean isSimplePattern(String pattern) {
        int asteriskCount = pattern.length() - pattern.replace("*", "").length();
        if (asteriskCount > 1) {
            return false;
        }
        if (asteriskCount == 1) {
            return pattern.endsWith("*") || pattern.startsWith("*") || pattern.contains("*.");
        }
        return true;
    }

    private boolean isMatch(String pattern, String target) {
        int asteriskIndex = pattern.indexOf("*");
        if (asteriskIndex != -1) {
            String prefix = pattern.substring(0, asteriskIndex);
            String suffix = pattern.substring(asteriskIndex + 1);
            if (pattern.endsWith("*")) {
                return target.startsWith(prefix);
            }
            if (pattern.startsWith("*")) {
                return target.endsWith(suffix);
            }
            return target.endsWith(suffix) && target.startsWith(prefix);
        }
        return target.equals(pattern);
    }

    public PluginProvidedDecoratorModule getModule() {
        return new PluginProvidedDecoratorModule(){

            @Override
            public boolean matches(String path) {
                return DecoratorModuleDescriptor.this.patterns.stream().anyMatch(p -> DecoratorModuleDescriptor.this.isMatch((String)p, path));
            }

            @Override
            public String getTemplate() {
                return DecoratorModuleDescriptor.this.page;
            }

            @Override
            public String key() {
                return DecoratorModuleDescriptor.this.getCompleteKey();
            }
        };
    }
}

