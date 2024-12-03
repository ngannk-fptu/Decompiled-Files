/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Content
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 *  org.jdom2.ProcessingInstruction
 *  org.jdom2.filter.ContentFilter
 *  org.jdom2.filter.Filter
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.Extendable;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.WireFeedParser;
import com.rometools.rome.io.impl.ModuleParsers;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.filter.ContentFilter;
import org.jdom2.filter.Filter;

public abstract class BaseWireFeedParser
implements WireFeedParser {
    private static final String FEED_MODULE_PARSERS_POSFIX_KEY = ".feed.ModuleParser.classes";
    private static final String ITEM_MODULE_PARSERS_POSFIX_KEY = ".item.ModuleParser.classes";
    private static final String PERSON_MODULE_PARSERS_POSFIX_KEY = ".person.ModuleParser.classes";
    private final String type;
    private final ModuleParsers feedModuleParsers;
    private final ModuleParsers itemModuleParsers;
    private final ModuleParsers personModuleParsers;
    private final Namespace namespace;

    protected BaseWireFeedParser(String type, Namespace namespace) {
        this.type = type;
        this.namespace = namespace;
        this.feedModuleParsers = new ModuleParsers(type + FEED_MODULE_PARSERS_POSFIX_KEY, this);
        this.itemModuleParsers = new ModuleParsers(type + ITEM_MODULE_PARSERS_POSFIX_KEY, this);
        this.personModuleParsers = new ModuleParsers(type + PERSON_MODULE_PARSERS_POSFIX_KEY, this);
    }

    @Override
    public String getType() {
        return this.type;
    }

    protected List<Module> parseFeedModules(Element feedElement, Locale locale) {
        return this.feedModuleParsers.parseModules(feedElement, locale);
    }

    protected List<Module> parseItemModules(Element itemElement, Locale locale) {
        return this.itemModuleParsers.parseModules(itemElement, locale);
    }

    protected List<Module> parsePersonModules(Element itemElement, Locale locale) {
        return this.personModuleParsers.parseModules(itemElement, locale);
    }

    protected List<Element> extractForeignMarkup(Element e, Extendable ext, Namespace namespace) {
        ArrayList<Element> foreignElements = new ArrayList<Element>();
        for (Element element : e.getChildren()) {
            if (namespace.equals((Object)element.getNamespace()) || ext.getModule(element.getNamespaceURI()) != null) continue;
            foreignElements.add(element.clone());
        }
        for (Element foreignElement : foreignElements) {
            foreignElement.detach();
        }
        return foreignElements;
    }

    protected Attribute getAttribute(Element e, String attributeName) {
        Attribute attribute = e.getAttribute(attributeName);
        if (attribute == null) {
            attribute = e.getAttribute(attributeName, this.namespace);
        }
        return attribute;
    }

    protected String getAttributeValue(Element e, String attributeName) {
        Attribute attr = this.getAttribute(e, attributeName);
        if (attr != null) {
            return attr.getValue();
        }
        return null;
    }

    protected String getStyleSheet(Document doc) {
        String styleSheet = null;
        for (Content c : doc.getContent((Filter)new ContentFilter(16))) {
            ProcessingInstruction pi = (ProcessingInstruction)c;
            if (!"text/xsl".equals(pi.getPseudoAttributeValue("type"))) continue;
            styleSheet = pi.getPseudoAttributeValue("href");
            break;
        }
        return styleSheet;
    }
}

