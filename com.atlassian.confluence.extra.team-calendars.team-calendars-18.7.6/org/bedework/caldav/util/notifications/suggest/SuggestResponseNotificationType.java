/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.suggest;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.suggest.SuggestBaseNotificationType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class SuggestResponseNotificationType
extends SuggestBaseNotificationType {
    private boolean accepted;

    public void setAccepted(boolean val) {
        this.accepted = val;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    @Override
    public QName getElementName() {
        return BedeworkServerTags.suggestReply;
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(this.getElementName());
        this.bodyToXml(xml);
        xml.property(BedeworkServerTags.accepted, String.valueOf(this.getAccepted()));
        xml.closeTag(this.getElementName());
    }

    @Override
    protected void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        ts.append("accepted", this.getAccepted());
    }
}

