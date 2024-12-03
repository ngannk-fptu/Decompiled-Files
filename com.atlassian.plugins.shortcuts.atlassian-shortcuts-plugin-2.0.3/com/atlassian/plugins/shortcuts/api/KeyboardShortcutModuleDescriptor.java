/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.shortcuts.api;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcut;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardShortcutModuleDescriptor
extends AbstractModuleDescriptor<KeyboardShortcut> {
    public static final String XML_ELEMENT_NAME = "keyboard-shortcut";
    private static final Logger log = LoggerFactory.getLogger(KeyboardShortcutModuleDescriptor.class);
    private static final Pattern JSON_VALUE_STRING = Pattern.compile("^\".*\"$");
    private static final Pattern JSON_VALUE_ARRAY = Pattern.compile("^\\[.*\\]$");
    private KeyboardShortcut keyboardShortcut;

    public KeyboardShortcutModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    private static List<String> parseShortcut(String shortcut) throws PluginParseException {
        List<String> result = new ArrayList<String>();
        try {
            if (JSON_VALUE_ARRAY.matcher(shortcut).matches()) {
                JSONArray array = new JSONArray(shortcut);
                for (int i = 0; i < array.length(); ++i) {
                    result.add(array.getString(i));
                }
            } else if (JSON_VALUE_STRING.matcher(shortcut).matches()) {
                String key = "shortcut";
                JSONObject json = new JSONObject(String.format("{ \"%s\": %s }", "shortcut", shortcut));
                result = Collections.singletonList(json.getString("shortcut"));
            } else {
                for (char c : shortcut.toCharArray()) {
                    result.add(String.valueOf(c));
                }
            }
        }
        catch (JSONException e) {
            throw new PluginParseException("The <shortcut> element did not provide a valid keyboard shortcut definition");
        }
        if (result.size() <= 0) {
            throw new PluginParseException("The <shortcut> element did not provide a keyboard shortcut definition");
        }
        return result;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        String descriptionKey = this.parseDescriptionKey(element);
        String defaultDescription = this.parseDefaultDescription(element);
        boolean hidden = this.parseHidden(element);
        String context = this.parseContext(element);
        Set<List<String>> shortcuts = this.parseShortcuts(element);
        int order = this.parseOrder(element);
        KeyboardShortcutOperation operation = this.parseOperation(element);
        this.keyboardShortcut = new KeyboardShortcut(context, operation, order, shortcuts, descriptionKey, defaultDescription, hidden);
    }

    private String parseDescriptionKey(Element element) {
        Element descriptionEl = element.element("description");
        if (descriptionEl != null && descriptionEl.attribute("key") != null) {
            return StringUtils.trim((String)descriptionEl.attributeValue("key"));
        }
        throw new PluginParseException("<description> i18n 'key' attribute is a required attribute for a keyboard shortcut plugin module");
    }

    private String parseDefaultDescription(Element element) {
        Element descriptionEl = element.element("description");
        if (descriptionEl != null && !StringUtils.isBlank((CharSequence)descriptionEl.getText())) {
            return StringUtils.trim((String)descriptionEl.getText());
        }
        return null;
    }

    private Set<List<String>> parseShortcuts(Element element) throws PluginParseException {
        HashSet<List<String>> shortcuts = new HashSet<List<String>>();
        List shortcutEls = element.elements("shortcut");
        if (shortcutEls.size() <= 0) {
            throw new PluginParseException("<shortcut> is a required element for a keyboard shortcut plugin module");
        }
        for (Element shortcutEl : shortcutEls) {
            shortcuts.add(KeyboardShortcutModuleDescriptor.parseShortcut(shortcutEl.getTextTrim()));
        }
        return shortcuts;
    }

    private String parseContext(Element element) {
        String context = "global";
        Element contextEl = element.element("context");
        if (contextEl != null) {
            context = StringUtils.trim((String)contextEl.getText());
        }
        return context;
    }

    private boolean parseHidden(Element element) {
        Attribute hiddenAttr = element.attribute("hidden");
        if (hiddenAttr != null && StringUtils.isNotEmpty((CharSequence)hiddenAttr.getText())) {
            return Boolean.parseBoolean(hiddenAttr.getText());
        }
        return false;
    }

    private int parseOrder(Element element) {
        if (element.element("order") == null) {
            return Integer.MAX_VALUE;
        }
        String orderString = element.element("order").getTextTrim();
        if (StringUtils.isBlank((CharSequence)orderString)) {
            throw new PluginParseException("Invalid order element: cannot be empty");
        }
        try {
            return Integer.parseInt(orderString);
        }
        catch (NumberFormatException e) {
            log.warn("Invalid order specified: " + element.element("order").getTextTrim() + ". Should be an integer.", (Throwable)e);
            return Integer.MAX_VALUE;
        }
    }

    public KeyboardShortcut getModule() {
        return this.keyboardShortcut;
    }

    public int getOrder() {
        return this.keyboardShortcut.getOrder();
    }

    public boolean isHidden() {
        return this.keyboardShortcut.isHidden();
    }

    private KeyboardShortcutOperation parseOperation(Element element) {
        Element operationEl = element.element("operation");
        if (operationEl == null) {
            throw new PluginParseException("<operation> is a required element for a keyboard shortcut plugin module");
        }
        String operationType = operationEl.attribute("type").getText();
        String operationParam = StringUtils.trim((String)operationEl.getText());
        return new KeyboardShortcutOperation(operationType, operationParam);
    }
}

