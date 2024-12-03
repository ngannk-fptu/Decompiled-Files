/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.SyModule;
import com.sun.syndication.io.ModuleGenerator;
import com.sun.syndication.io.impl.DateParser;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;

public class SyModuleGenerator
implements ModuleGenerator {
    private static final String SY_URI = "http://purl.org/rss/1.0/modules/syndication/";
    private static final Namespace SY_NS = Namespace.getNamespace((String)"sy", (String)"http://purl.org/rss/1.0/modules/syndication/");
    private static final Set NAMESPACES;

    public String getNamespaceUri() {
        return SY_URI;
    }

    public Set getNamespaces() {
        return NAMESPACES;
    }

    public void generate(Module module, Element element) {
        SyModule syModule = (SyModule)module;
        if (syModule.getUpdatePeriod() != null) {
            Element updatePeriodElement = new Element("updatePeriod", SY_NS);
            updatePeriodElement.addContent(syModule.getUpdatePeriod());
            element.addContent((Content)updatePeriodElement);
        }
        Element updateFrequencyElement = new Element("updateFrequency", SY_NS);
        updateFrequencyElement.addContent(String.valueOf(syModule.getUpdateFrequency()));
        element.addContent((Content)updateFrequencyElement);
        if (syModule.getUpdateBase() != null) {
            Element updateBaseElement = new Element("updateBase", SY_NS);
            updateBaseElement.addContent(DateParser.formatW3CDateTime(syModule.getUpdateBase()));
            element.addContent((Content)updateBaseElement);
        }
    }

    static {
        HashSet<Namespace> nss = new HashSet<Namespace>();
        nss.add(SY_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }
}

