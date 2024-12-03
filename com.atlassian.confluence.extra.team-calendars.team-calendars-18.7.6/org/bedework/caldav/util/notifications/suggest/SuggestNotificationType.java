/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.suggest;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.suggest.SuggestBaseNotificationType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class SuggestNotificationType
extends SuggestBaseNotificationType {
    @Override
    public QName getElementName() {
        return BedeworkServerTags.suggest;
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(BedeworkServerTags.suggest);
        this.bodyToXml(xml);
        xml.closeTag(BedeworkServerTags.suggest);
    }
}

