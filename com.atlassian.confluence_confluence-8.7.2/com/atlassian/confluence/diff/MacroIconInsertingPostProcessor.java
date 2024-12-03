/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.jdom.Attribute
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.Text
 *  org.jdom.filter.ContentFilter
 *  org.jdom.filter.Filter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.DiffPostProcessor;
import com.atlassian.confluence.macro.browser.MacroIconManager;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroIconInsertingPostProcessor
implements DiffPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(MacroIconInsertingPostProcessor.class);
    private final MacroMetadataManager macroMetadataManager;
    private final MacroIconManager macroIconManager;
    private final I18NBeanFactory i18NBeanFactory;

    public MacroIconInsertingPostProcessor(MacroMetadataManager macroMetadataManager, MacroIconManager macroIconManager, I18NBeanFactory i18NBeanFactory) {
        this.macroMetadataManager = macroMetadataManager;
        this.macroIconManager = macroIconManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public Document process(Document document) {
        List<Element> macroElements = this.findMacroHeaderElements(document);
        this.processMacroHeaderElements(macroElements, this.i18NBeanFactory.getI18NBean());
        return document;
    }

    private void processMacroHeaderElements(List<Element> macroElements, I18NBean i18NBean) {
        for (Element element : macroElements) {
            String macroName = element.getText();
            MacroMetadata macroMetadata = this.macroMetadataManager.getMacroMetadataByName(macroName);
            if (macroMetadata == null) {
                log.warn("Could not find macro with name '{}'", (Object)macroName);
                continue;
            }
            element.setText(this.localiseMacroTitle(macroMetadata, i18NBean));
            Element icon = new Element("span");
            icon.setText(" ");
            this.addOrSetClassAttribute(icon, "icon macro-placeholder-icon");
            String iconUrl = this.getMacroIconUrl(macroMetadata);
            String backgroundImageStyle = "background-image: url(" + iconUrl + ");";
            this.addOrSetStyle(icon, backgroundImageStyle);
            element.addContent(0, (Content)icon);
        }
    }

    private void addOrSetStyle(Element element, String style) {
        Attribute styleAttribute = element.getAttribute("style");
        if (styleAttribute == null || StringUtils.isEmpty((CharSequence)styleAttribute.getValue())) {
            styleAttribute = new Attribute("style", style);
            element.setAttribute(styleAttribute);
        } else {
            styleAttribute.setValue(styleAttribute.getValue() + "; " + style);
        }
    }

    private void addOrSetClassAttribute(Element element, String value) {
        Attribute classAttribute = element.getAttribute("class");
        if (classAttribute == null || StringUtils.isEmpty((CharSequence)classAttribute.getValue())) {
            classAttribute = new Attribute("class", value);
            element.setAttribute(classAttribute);
        } else {
            classAttribute.setValue(classAttribute.getValue() + " " + value);
        }
    }

    private String getMacroIconUrl(MacroMetadata macroMetadata) {
        return this.macroIconManager.getExternalSmallIconUrl(macroMetadata);
    }

    private List<Element> findMacroHeaderElements(Document document) {
        ArrayList<Element> macroElements = new ArrayList<Element>();
        Iterator descendants = document.getRootElement().getDescendants((Filter)new ContentFilter(1));
        while (descendants.hasNext()) {
            Attribute classAttribute;
            Element element = (Element)descendants.next();
            if (!element.getName().equals("th") || (classAttribute = element.getAttribute("class")) == null || classAttribute.getValue() == null || !classAttribute.getValue().contains("diff-macro-title") || (element = this.skipDiffSpans(element)) == null) continue;
            macroElements.add(element);
        }
        return macroElements;
    }

    private Element skipDiffSpans(Element element) {
        List contents = element.getContent();
        if (contents.size() < 1) {
            return element;
        }
        Content first = (Content)contents.get(0);
        if (first instanceof Text) {
            return element;
        }
        if (first instanceof Element) {
            return this.skipDiffSpans((Element)first);
        }
        return element;
    }

    private String localiseMacroTitle(MacroMetadata metadata, I18NBean i18NBean) {
        String title = i18NBean.getText(metadata.getTitle());
        if (title == null || title.equals(metadata.getTitle().getKey())) {
            return StringUtils.capitalize((String)metadata.getMacroName()).replace('-', ' ');
        }
        return title;
    }
}

