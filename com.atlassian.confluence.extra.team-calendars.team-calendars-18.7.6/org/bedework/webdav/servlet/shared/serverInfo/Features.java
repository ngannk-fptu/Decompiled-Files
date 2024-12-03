/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared.serverInfo;

import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.serverInfo.Feature;

public class Features {
    private List<Feature> features;

    public void setFeatures(List<Feature> val) {
        this.features = val;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    public void addFeature(Feature val) {
        if (this.features == null) {
            this.features = new ArrayList<Feature>();
        }
        this.features.add(val);
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(WebdavTags.features);
        if (!Util.isEmpty(this.getFeatures())) {
            for (Feature f : this.getFeatures()) {
                f.toXml(xml);
            }
        }
        xml.closeTag(WebdavTags.features);
    }
}

