/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColourSchemeModuleDescriptor
extends AbstractModuleDescriptor {
    private static final Logger log = LoggerFactory.getLogger(ColourSchemeModuleDescriptor.class);
    private static final Map<String, String> TRANSLATOR = new HashMap<String, String>();
    private Map<String, String> colours = new HashMap<String, String>();

    public ColourSchemeModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Object getModule() {
        Class clazz = this.getModuleClass();
        try {
            Constructor ctor = clazz.getConstructor(Map.class);
            return ctor.newInstance(new HashMap<String, String>(this.colours));
        }
        catch (Exception e) {
            log.error("Unable to instantiate plugin colour scheme key: " + this.getCompleteKey() + " of class: " + ((Object)((Object)this)).getClass().getName() + " message: " + e.toString(), (Throwable)e);
            return null;
        }
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.extractColours(element, "color");
        this.extractColours(element, "colour");
    }

    private void extractColours(Element element, String tagName) throws PluginParseException {
        List definedColours = element.elements(tagName);
        for (Element colour : definedColours) {
            String key = colour.attributeValue("key");
            String value = colour.attributeValue("value");
            if (!StringUtils.isNotEmpty((CharSequence)key) || !StringUtils.isNotEmpty((CharSequence)value)) {
                throw new PluginParseException("Malformed colour module: colours must have key and value");
            }
            if (TRANSLATOR.containsKey(key)) {
                key = TRANSLATOR.get(key);
            }
            if (this.colours.containsKey(key)) {
                log.warn("Colour scheme " + this.getCompleteKey() + " contains multiple definitions for colour: " + colour.attributeValue("key"));
            }
            if (log.isDebugEnabled()) {
                log.debug(this.getCompleteKey() + " " + key + ": " + value);
            }
            this.colours.put(key, value);
        }
    }

    static {
        TRANSLATOR.put("topbar", "property.style.topbarcolour");
        TRANSLATOR.put("spacename", "property.style.spacenamecolour");
        TRANSLATOR.put("headingtext", "property.style.headingtextcolour");
        TRANSLATOR.put("link", "property.style.linkcolour");
        TRANSLATOR.put("border", "property.style.bordercolour");
        TRANSLATOR.put("navbg", "property.style.navbgcolour");
        TRANSLATOR.put("navtext", "property.style.navtextcolour");
        TRANSLATOR.put("navselectedbg", "property.style.navselectedbgcolour");
        TRANSLATOR.put("navselectedtext", "property.style.navselectedtextcolour");
    }
}

