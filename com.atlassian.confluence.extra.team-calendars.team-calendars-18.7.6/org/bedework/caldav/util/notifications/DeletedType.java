/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.caldav.util.notifications.BaseEntityChangeType;
import org.bedework.caldav.util.notifications.DeletedDetailsType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class DeletedType
extends BaseEntityChangeType {
    private DeletedDetailsType deletedDetails;

    public void setDeletedDetails(DeletedDetailsType val) {
        this.deletedDetails = val;
    }

    public DeletedDetailsType getDeletedDetails() {
        return this.deletedDetails;
    }

    public DeletedType copyForAlias(String collectionHref) {
        DeletedType copy = new DeletedType();
        this.copyForAlias(copy, collectionHref);
        copy.deletedDetails = this.deletedDetails;
        return copy;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.deleted);
        this.toXmlSegment(xml);
        this.getDeletedDetails().toXml(xml);
        xml.closeTag(AppleServerTags.deleted);
    }

    @Override
    protected void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        this.getDeletedDetails().toStringSegment(ts);
    }
}

