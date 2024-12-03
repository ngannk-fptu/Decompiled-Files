/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared.serverInfo;

import javax.xml.namespace.QName;
import org.bedework.util.xml.XmlEmit;

public class Feature {
    private QName featureName;

    public Feature(QName featureName) {
        this.setFeatureName(featureName);
    }

    public void setFeatureName(QName val) {
        this.featureName = val;
    }

    public QName getFeatureName() {
        return this.featureName;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.emptyTag(this.getFeatureName());
    }
}

