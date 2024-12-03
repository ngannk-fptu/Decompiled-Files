/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.notifications.CollectionChangesType;
import org.bedework.caldav.util.notifications.CreatedType;
import org.bedework.caldav.util.notifications.DeletedType;
import org.bedework.caldav.util.notifications.UpdatedType;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;

public class ResourceChangeType
extends BaseNotificationType {
    private String uid;
    private String name;
    private CreatedType created;
    private DeletedType deleted;
    private CollectionChangesType collectionChanges;
    private List<UpdatedType> updated;
    private List<BaseNotificationType.AttributeType> attrs;

    public void setCreated(CreatedType val) {
        this.created = val;
        if (val != null) {
            this.checkName(val.getHref());
        }
    }

    public CreatedType getCreated() {
        return this.created;
    }

    public void setDeleted(DeletedType val) {
        this.deleted = val;
        if (val != null) {
            this.checkName(val.getHref());
        }
    }

    public DeletedType getDeleted() {
        return this.deleted;
    }

    public void setCollectionChanges(CollectionChangesType val) {
        this.collectionChanges = val;
        if (val != null) {
            this.checkName(val.getHref());
        }
    }

    public CollectionChangesType getCollectionChanges() {
        return this.collectionChanges;
    }

    public List<UpdatedType> getUpdated() {
        if (this.updated == null) {
            this.updated = new ArrayList<UpdatedType>();
        }
        return Collections.unmodifiableList(this.updated);
    }

    public void addUpdate(UpdatedType val) {
        if (this.updated == null) {
            this.updated = new ArrayList<UpdatedType>();
        }
        this.updated.add(val);
        this.checkName(val.getHref());
    }

    public void clearUpdated() {
        if (this.updated == null) {
            return;
        }
        this.updated.clear();
    }

    @Override
    public QName getElementName() {
        return AppleServerTags.resourceChange;
    }

    @Override
    public void setName(String val) {
        this.uid = val;
    }

    @Override
    public String getName() {
        return this.uid;
    }

    @Override
    public void setEncoding(String val) {
        this.name = val;
    }

    @Override
    public String getEncoding() {
        return this.name;
    }

    @Override
    public List<BaseNotificationType.AttributeType> getElementAttributes() {
        if (this.attrs != null) {
            return this.attrs;
        }
        this.attrs = new ArrayList<BaseNotificationType.AttributeType>();
        return this.attrs;
    }

    @Override
    public void prefixHrefs(UrlPrefixer prefixer) throws Throwable {
        if (this.getCreated() != null) {
            this.getCreated().prefixHrefs(prefixer);
            return;
        }
        if (this.getDeleted() != null) {
            this.getDeleted().prefixHrefs(prefixer);
            return;
        }
        if (this.getCollectionChanges() != null) {
            this.getCollectionChanges().prefixHrefs(prefixer);
            return;
        }
        for (UpdatedType u : this.getUpdated()) {
            u.prefixHrefs(prefixer);
        }
    }

    @Override
    public void unprefixHrefs(UrlUnprefixer unprefixer) throws Throwable {
        if (this.getCreated() != null) {
            this.getCreated().unprefixHrefs(unprefixer);
            return;
        }
        if (this.getDeleted() != null) {
            this.getDeleted().unprefixHrefs(unprefixer);
            return;
        }
        if (this.getCollectionChanges() != null) {
            this.getCollectionChanges().unprefixHrefs(unprefixer);
            return;
        }
        for (UpdatedType u : this.getUpdated()) {
            u.unprefixHrefs(unprefixer);
        }
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.resourceChange);
        if (this.getCreated() != null) {
            this.getCreated().toXml(xml);
        } else if (!this.getUpdated().isEmpty()) {
            for (UpdatedType u : this.getUpdated()) {
                u.toXml(xml);
            }
        } else if (this.getDeleted() != null) {
            this.getDeleted().toXml(xml);
        } else if (this.getCollectionChanges() != null) {
            this.getCollectionChanges().toXml(xml);
        }
        xml.closeTag(AppleServerTags.resourceChange);
    }

    public boolean sameHref(String val) {
        String bval = Base64.encodeBase64String(val.getBytes());
        return bval.equals(this.getEncoding());
    }

    public void setHref(String val) {
        String bval = Base64.encodeBase64String(val.getBytes());
        this.setEncoding(bval);
    }

    protected void toStringSegment(ToString ts) {
        if (this.getCollectionChanges() != null) {
            this.getCollectionChanges().toStringSegment(ts);
            return;
        }
        if (this.getCreated() != null) {
            this.getCreated().toStringSegment(ts);
        }
        for (UpdatedType u : this.getUpdated()) {
            u.toStringSegment(ts);
        }
        if (this.getDeleted() != null) {
            this.getDeleted().toStringSegment(ts);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }

    public ResourceChangeType copyForAlias(String collectionHref) {
        ResourceChangeType copy = new ResourceChangeType();
        copy.uid = this.uid;
        copy.name = this.name;
        if (this.created != null) {
            copy.created = this.created.copyForAlias(collectionHref);
        }
        if (this.deleted != null) {
            copy.deleted = this.deleted.copyForAlias(collectionHref);
        }
        if (this.collectionChanges != null) {
            copy.collectionChanges = this.collectionChanges.copyForAlias(collectionHref);
        }
        if (!Util.isEmpty(this.updated)) {
            copy.updated = new ArrayList<UpdatedType>(this.updated.size());
            for (UpdatedType u : this.updated) {
                copy.updated.add(u.copyForAlias(collectionHref));
            }
        }
        if (!Util.isEmpty(this.attrs)) {
            copy.attrs = new ArrayList<BaseNotificationType.AttributeType>(this.attrs);
        }
        return copy;
    }

    private void checkName(String val) {
        String bval = Base64.encodeBase64String(val.getBytes());
        if (this.getEncoding() == null) {
            this.setEncoding(bval);
        } else if (!this.getEncoding().equals(bval)) {
            throw new RuntimeException("Attempt to store different href in change notification. Old: " + this.getEncoding() + " new: " + val);
        }
    }
}

