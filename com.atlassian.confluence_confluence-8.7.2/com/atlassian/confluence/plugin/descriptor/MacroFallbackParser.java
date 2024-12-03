/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.plugin.descriptor.MacroMetadataParser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class MacroFallbackParser {
    private static final String PLUGIN_ELEMENT = "atlassian-plugin";
    private static final String MACRO_ELEMENT = "macro";
    private static final String KEY_ATTRIBUTE = "key";
    private volatile Map<String, MacroMetadata> macroMetadata;
    private String filePath;
    private MacroMetadataParser macroMetadataParser;

    public MacroFallbackParser(String filePath, MacroMetadataParser macroMetadataParser) {
        this.filePath = filePath;
        this.macroMetadataParser = macroMetadataParser;
    }

    private void checkInit() {
        if (this.macroMetadata == null) {
            InputStream source = ClassLoaderUtils.getResourceAsStream((String)this.filePath, this.getClass());
            Objects.requireNonNull(source, "XML descriptor source cannot be null");
            Document document = this.createDocument(source);
            this.macroMetadata = Collections.unmodifiableMap(this.parse(document.getRootElement()));
        }
    }

    private Document createDocument(InputStream source) throws PluginParseException {
        SAXReader reader = new SAXReader();
        try {
            return reader.read(source);
        }
        catch (DocumentException e) {
            throw new PluginParseException("Cannot parse XML plugin descriptor", (Throwable)e);
        }
    }

    private Map<String, MacroMetadata> parse(Element pluginsElement) {
        HashMap<String, MacroMetadata> macroMetadata = new HashMap<String, MacroMetadata>();
        Iterator iterator = pluginsElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            if (!PLUGIN_ELEMENT.equalsIgnoreCase(element.getName())) continue;
            this.parsePlugin(element, macroMetadata);
        }
        return macroMetadata;
    }

    private void parsePlugin(Element pluginElement, Map<String, MacroMetadata> macroMetadata) {
        String pluginKey = pluginElement.attributeValue(KEY_ATTRIBUTE);
        Iterator iterator = pluginElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element)iterator.next();
            if (!MACRO_ELEMENT.equalsIgnoreCase(element.getName())) continue;
            MacroMetadata m = this.macroMetadataParser.parseMacro(pluginKey, element);
            macroMetadata.put(m.getMacroName(), m);
        }
    }

    public Map<String, MacroMetadata> getMetadata() {
        this.checkInit();
        return this.macroMetadata;
    }
}

