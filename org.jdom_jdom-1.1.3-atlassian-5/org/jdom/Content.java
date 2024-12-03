/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.io.Serializable;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;

public abstract class Content
implements Cloneable,
Serializable {
    protected Parent parent = null;

    protected Content() {
    }

    public Content detach() {
        if (this.parent != null) {
            this.parent.removeContent(this);
        }
        return this;
    }

    public Parent getParent() {
        return this.parent;
    }

    public Element getParentElement() {
        Parent parent = this.getParent();
        return (Element)(parent instanceof Element ? parent : null);
    }

    protected Content setParent(Parent parent) {
        this.parent = parent;
        return this;
    }

    public Document getDocument() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getDocument();
    }

    public abstract String getValue();

    public Object clone() {
        try {
            Content c = (Content)super.clone();
            c.parent = null;
            return c;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public final int hashCode() {
        return super.hashCode();
    }
}

