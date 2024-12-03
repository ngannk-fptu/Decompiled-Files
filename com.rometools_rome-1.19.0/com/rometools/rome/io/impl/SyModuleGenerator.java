/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.SyModule;
import com.rometools.rome.io.ModuleGenerator;
import com.rometools.rome.io.impl.DateParser;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class SyModuleGenerator
implements ModuleGenerator {
    private static final String SY_URI = "http://purl.org/rss/1.0/modules/syndication/";
    private static final Namespace SY_NS = Namespace.getNamespace((String)"sy", (String)"http://purl.org/rss/1.0/modules/syndication/");
    private static final Set<Namespace> NAMESPACES;

    @Override
    public String getNamespaceUri() {
        return SY_URI;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }

    @Override
    public void generate(Module module, Element element) {
        SyModule syModule = (SyModule)module;
        String updatePeriod = syModule.getUpdatePeriod();
        if (updatePeriod != null) {
            Element updatePeriodElement = new Element("updatePeriod", SY_NS);
            updatePeriodElement.addContent(updatePeriod);
            element.addContent((Content)updatePeriodElement);
        }
        Element updateFrequencyElement = new Element("updateFrequency", SY_NS);
        updateFrequencyElement.addContent(String.valueOf(syModule.getUpdateFrequency()));
        element.addContent((Content)updateFrequencyElement);
        Date updateBase = syModule.getUpdateBase();
        if (updateBase != null) {
            Element updateBaseElement = new Element("updateBase", SY_NS);
            updateBaseElement.addContent(DateParser.formatW3CDateTime(updateBase, Locale.US));
            element.addContent((Content)updateBaseElement);
        }
    }

    static {
        HashSet<Namespace> nss = new HashSet<Namespace>();
        nss.add(SY_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }
}

