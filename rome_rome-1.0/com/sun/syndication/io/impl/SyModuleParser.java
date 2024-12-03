/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.impl.DateParser;
import org.jdom.Element;
import org.jdom.Namespace;

public class SyModuleParser
implements ModuleParser {
    public String getNamespaceUri() {
        return "http://purl.org/rss/1.0/modules/syndication/";
    }

    private Namespace getDCNamespace() {
        return Namespace.getNamespace((String)"http://purl.org/rss/1.0/modules/syndication/");
    }

    public Module parse(Element syndRoot) {
        boolean foundSomething = false;
        SyModuleImpl sm = new SyModuleImpl();
        Element e = syndRoot.getChild("updatePeriod", this.getDCNamespace());
        if (e != null) {
            foundSomething = true;
            sm.setUpdatePeriod(e.getText());
        }
        if ((e = syndRoot.getChild("updateFrequency", this.getDCNamespace())) != null) {
            foundSomething = true;
            sm.setUpdateFrequency(Integer.parseInt(e.getText().trim()));
        }
        if ((e = syndRoot.getChild("updateBase", this.getDCNamespace())) != null) {
            foundSomething = true;
            sm.setUpdateBase(DateParser.parseDate(e.getText()));
        }
        return foundSomething ? sm : null;
    }
}

