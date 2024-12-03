/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.caldav.util.notifications.BaseEntityChangeType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class CreatedType
extends BaseEntityChangeType {
    public CreatedType copyForAlias(String collectionHref) {
        CreatedType copy = new CreatedType();
        this.copyForAlias(copy, collectionHref);
        return copy;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.created);
        this.toXmlSegment(xml);
        xml.closeTag(AppleServerTags.created);
    }
}

