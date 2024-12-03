/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment$Type
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapper
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  com.atlassian.plugins.rest.common.expand.parameter.Indexes
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.rest.entities.AttachmentEntity;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapper;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="attachments")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AttachmentEntityList
implements ListWrapper<AttachmentEntity> {
    @XmlAttribute
    private String expand;
    @XmlAttribute
    private final int size;
    @XmlElement(name="attachment")
    @Expandable
    private List<AttachmentEntity> attachments;
    @XmlTransient
    private final ListWrapperCallback<AttachmentEntity> callback;

    public AttachmentEntityList() {
        this.size = 0;
        this.callback = null;
    }

    public AttachmentEntityList(int size, ListWrapperCallback<AttachmentEntity> callback) {
        this.size = size;
        this.callback = callback;
    }

    public ListWrapperCallback<AttachmentEntity> getCallback() {
        return this.callback;
    }

    public int getSize() {
        return this.size;
    }

    public void reverse() {
        Collections.reverse(this.attachments);
    }

    public List<AttachmentEntity> getAttachments() {
        return this.attachments;
    }

    public void buildAttachmentListFromWrapper(final int start, int max) {
        int endIndex = start + max;
        final int end = Math.min(endIndex, this.size) - 1;
        this.attachments = this.callback.getItems(new Indexes(){

            public boolean isRange() {
                return true;
            }

            public int getMinIndex(int size) {
                return start;
            }

            public int getMaxIndex(int size) {
                return end;
            }

            public boolean contains(int index, int size) {
                return index < size;
            }

            public SortedSet<Integer> getIndexes(int size) {
                throw new UnsupportedOperationException("Not supported");
            }
        });
    }

    public void buildFilteredByMimeTypeAttachmentList(int start, int maxSize, Set<String> includeMimeTypes) {
        List attach = this.callback.getItems(new Indexes(){

            public boolean isRange() {
                return true;
            }

            public int getMinIndex(int size) {
                return 0;
            }

            public int getMaxIndex(int si) {
                return AttachmentEntityList.this.size - 1;
            }

            public boolean contains(int index, int size) {
                return index < size;
            }

            public SortedSet<Integer> getIndexes(int size) {
                throw new UnsupportedOperationException("Not supported");
            }
        });
        this.attachments = new ArrayList<AttachmentEntity>(maxSize);
        for (AttachmentEntity entity : attach) {
            if (!includeMimeTypes.contains(entity.getContentType())) continue;
            this.attachments.add(entity);
        }
        int maxIndex = Math.min(start + maxSize, this.attachments.size());
        this.attachments = this.attachments.subList(start, maxIndex);
    }

    public void buildFilteredByNiceTypeAttachmentList(int start, int maxSize, Set<String> niceTypes) {
        List attach = this.callback.getItems(new Indexes(){

            public boolean isRange() {
                return true;
            }

            public int getMinIndex(int size) {
                return 0;
            }

            public int getMaxIndex(int si) {
                return AttachmentEntityList.this.size - 1;
            }

            public boolean contains(int index, int size) {
                return index < size;
            }

            public SortedSet<Integer> getIndexes(int size) {
                throw new UnsupportedOperationException("Not supported");
            }
        });
        this.attachments = new ArrayList<AttachmentEntity>(maxSize);
        Set types = Attachment.Type.getTypes(niceTypes);
        for (AttachmentEntity entity : attach) {
            if (!types.contains(Attachment.Type.getForMimeType((String)entity.getContentType(), (String)entity.getFileExtension()))) continue;
            this.attachments.add(entity);
        }
        int maxIndex = Math.min(start + maxSize, this.attachments.size());
        this.attachments = this.attachments.subList(start, maxIndex);
    }

    public String toString() {
        return new StringJoiner(", ", AttachmentEntityList.class.getSimpleName() + "[", "]").add("expand='" + this.expand + "'").add("size=" + this.size).add("attachments=" + this.attachments).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttachmentEntityList)) {
            return false;
        }
        AttachmentEntityList that = (AttachmentEntityList)o;
        return this.size == that.size && Objects.equals(this.expand, that.expand) && Objects.equals(this.attachments, that.attachments);
    }

    public int hashCode() {
        return Objects.hash(this.size, this.expand, this.attachments);
    }
}

