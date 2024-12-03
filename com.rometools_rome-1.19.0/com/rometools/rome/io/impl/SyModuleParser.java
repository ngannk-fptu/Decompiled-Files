/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.SyModuleImpl;
import com.rometools.rome.io.ModuleParser;
import com.rometools.rome.io.impl.DateParser;
import java.util.Locale;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class SyModuleParser
implements ModuleParser {
    @Override
    public String getNamespaceUri() {
        return "http://purl.org/rss/1.0/modules/syndication/";
    }

    private Namespace getDCNamespace() {
        return Namespace.getNamespace((String)"http://purl.org/rss/1.0/modules/syndication/");
    }

    @Override
    public Module parse(Element syndRoot, Locale locale) {
        Element updateBase;
        Element updateFrequency;
        boolean foundSomething = false;
        SyModuleImpl sm = new SyModuleImpl();
        Element updatePeriod = syndRoot.getChild("updatePeriod", this.getDCNamespace());
        if (updatePeriod != null) {
            foundSomething = true;
            sm.setUpdatePeriod(updatePeriod.getText().trim());
        }
        if ((updateFrequency = syndRoot.getChild("updateFrequency", this.getDCNamespace())) != null) {
            foundSomething = true;
            sm.setUpdateFrequency(Integer.parseInt(updateFrequency.getText().trim()));
        }
        if ((updateBase = syndRoot.getChild("updateBase", this.getDCNamespace())) != null) {
            foundSomething = true;
            sm.setUpdateBase(DateParser.parseDate(updateBase.getText(), locale));
        }
        if (foundSomething) {
            return sm;
        }
        return null;
    }
}

