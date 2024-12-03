/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared.serverInfo;

import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.serverInfo.Feature;
import org.bedework.webdav.servlet.shared.serverInfo.Features;

public class Application {
    private String name;
    private Features features = new Features();

    public Application(String name) {
        this.setName(name);
    }

    public void setName(String val) {
        this.name = val;
    }

    public String getName() {
        return this.name;
    }

    public void addFeature(Feature val) {
        this.features.addFeature(val);
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(WebdavTags.application);
        xml.property(WebdavTags.name, this.getName());
        this.features.toXml(xml);
        xml.closeTag(WebdavTags.application);
    }
}

