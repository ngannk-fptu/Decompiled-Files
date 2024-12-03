/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;

public class PropType {
    private List<QName> qnames;

    public List<QName> getQnames() {
        if (this.qnames == null) {
            this.qnames = new ArrayList<QName>();
        }
        return this.qnames;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(WebdavTags.prop);
        for (QName qn : this.getQnames()) {
            xml.emptyTag(qn);
        }
        xml.closeTag(WebdavTags.prop);
    }

    protected void toStringSegment(ToString ts) {
        for (QName qn : this.getQnames()) {
            ts.append(qn);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

