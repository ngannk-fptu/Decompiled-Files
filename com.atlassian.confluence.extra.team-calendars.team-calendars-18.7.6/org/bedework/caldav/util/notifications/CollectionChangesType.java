/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.BaseEntityChangeType;
import org.bedework.caldav.util.notifications.ChangedByType;
import org.bedework.caldav.util.notifications.ChildCreatedType;
import org.bedework.caldav.util.notifications.ChildDeletedType;
import org.bedework.caldav.util.notifications.ChildUpdatedType;
import org.bedework.caldav.util.notifications.PropType;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class CollectionChangesType
extends BaseEntityChangeType {
    private List<ChangedByType> changedByList;
    private PropType prop;
    private ChildCreatedType childCreated;
    private ChildUpdatedType childUpdated;
    private ChildDeletedType childDeleted;

    public List<ChangedByType> getChangedByList() {
        if (this.changedByList != null) {
            return this.changedByList;
        }
        this.changedByList = new ArrayList<ChangedByType>();
        return this.changedByList;
    }

    public void setProp(PropType val) {
        this.prop = val;
    }

    public PropType getProp() {
        return this.prop;
    }

    public void setChildCreated(ChildCreatedType val) {
        this.childCreated = val;
    }

    public ChildCreatedType getChildCreated() {
        return this.childCreated;
    }

    public void setChildUpdated(ChildUpdatedType val) {
        this.childUpdated = val;
    }

    public ChildUpdatedType getChildUpdated() {
        return this.childUpdated;
    }

    public void setChildDeleted(ChildDeletedType val) {
        this.childDeleted = val;
    }

    public ChildDeletedType getChildDeleted() {
        return this.childDeleted;
    }

    public CollectionChangesType copyForAlias(String collectionHref) {
        CollectionChangesType copy = new CollectionChangesType();
        this.copyForAlias(copy, collectionHref);
        if (!Util.isEmpty(this.changedByList)) {
            copy.changedByList = new ArrayList<ChangedByType>(this.changedByList);
        }
        copy.prop = this.prop;
        copy.childCreated = this.childCreated;
        copy.childDeleted = this.childDeleted;
        copy.childUpdated = this.childUpdated;
        return copy;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.collectionChanges);
        this.toXmlSegment(xml);
        if (this.getProp() != null) {
            this.getProp().toXml(xml);
        }
        if (this.getChildCreated() != null) {
            this.getChildCreated().toXml(xml);
        }
        if (this.getChildUpdated() != null) {
            this.getChildUpdated().toXml(xml);
        }
        if (this.getChildDeleted() != null) {
            this.getChildDeleted().toXml(xml);
        }
        xml.closeTag(AppleServerTags.collectionChanges);
    }

    @Override
    protected void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        if (this.getProp() != null) {
            this.getProp().toStringSegment(ts);
        }
        if (this.getChildCreated() != null) {
            this.getChildCreated().toStringSegment(ts);
        }
        if (this.getChildUpdated() != null) {
            this.getChildUpdated().toStringSegment(ts);
        }
        if (this.getChildDeleted() != null) {
            this.getChildDeleted().toStringSegment(ts);
        }
    }
}

