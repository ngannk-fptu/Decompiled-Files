/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.filter.AbstractFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ContentFilter
extends AbstractFilter<Content> {
    private static final long serialVersionUID = 200L;
    public static final int ELEMENT = 1;
    public static final int CDATA = 2;
    public static final int TEXT = 4;
    public static final int COMMENT = 8;
    public static final int PI = 16;
    public static final int ENTITYREF = 32;
    public static final int DOCUMENT = 64;
    public static final int DOCTYPE = 128;
    private int filterMask;

    public ContentFilter() {
        this.setDefaultMask();
    }

    public ContentFilter(boolean allVisible) {
        if (allVisible) {
            this.setDefaultMask();
        } else {
            this.filterMask &= ~this.filterMask;
        }
    }

    public ContentFilter(int mask) {
        this.setFilterMask(mask);
    }

    public int getFilterMask() {
        return this.filterMask;
    }

    public void setFilterMask(int mask) {
        this.setDefaultMask();
        this.filterMask &= mask;
    }

    public void setDefaultMask() {
        this.filterMask = 255;
    }

    public void setDocumentContent() {
        this.filterMask = 153;
    }

    public void setElementContent() {
        this.filterMask = 63;
    }

    public void setElementVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 1) : (this.filterMask &= 0xFFFFFFFE);
    }

    public void setCDATAVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 2) : (this.filterMask &= 0xFFFFFFFD);
    }

    public void setTextVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 4) : (this.filterMask &= 0xFFFFFFFB);
    }

    public void setCommentVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 8) : (this.filterMask &= 0xFFFFFFF7);
    }

    public void setPIVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 0x10) : (this.filterMask &= 0xFFFFFFEF);
    }

    public void setEntityRefVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 0x20) : (this.filterMask &= 0xFFFFFFDF);
    }

    public void setDocTypeVisible(boolean visible) {
        this.filterMask = visible ? (this.filterMask |= 0x80) : (this.filterMask &= 0xFFFFFF7F);
    }

    @Override
    public Content filter(Object obj) {
        if (obj == null || !Content.class.isInstance(obj)) {
            return null;
        }
        Content content = (Content)obj;
        if (content instanceof Element) {
            return (this.filterMask & 1) != 0 ? content : null;
        }
        if (content instanceof CDATA) {
            return (this.filterMask & 2) != 0 ? content : null;
        }
        if (content instanceof Text) {
            return (this.filterMask & 4) != 0 ? content : null;
        }
        if (content instanceof Comment) {
            return (this.filterMask & 8) != 0 ? content : null;
        }
        if (content instanceof ProcessingInstruction) {
            return (this.filterMask & 0x10) != 0 ? content : null;
        }
        if (content instanceof EntityRef) {
            return (this.filterMask & 0x20) != 0 ? content : null;
        }
        if (content instanceof DocType) {
            return (this.filterMask & 0x80) != 0 ? content : null;
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContentFilter)) {
            return false;
        }
        ContentFilter filter = (ContentFilter)obj;
        return this.filterMask == filter.filterMask;
    }

    public int hashCode() {
        return this.filterMask;
    }
}

