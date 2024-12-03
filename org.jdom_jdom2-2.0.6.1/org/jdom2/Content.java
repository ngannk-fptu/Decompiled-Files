/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jdom2.CloneBase;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.NamespaceAware;
import org.jdom2.Parent;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Content
extends CloneBase
implements Serializable,
NamespaceAware {
    private static final long serialVersionUID = 200L;
    protected transient Parent parent = null;
    protected final CType ctype;

    protected Content(CType type) {
        this.ctype = type;
    }

    public final CType getCType() {
        return this.ctype;
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

    public final Element getParentElement() {
        Parent pnt = this.getParent();
        return (Element)(pnt instanceof Element ? pnt : null);
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

    @Override
    public Content clone() {
        Content c = (Content)super.clone();
        c.parent = null;
        return c;
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public List<Namespace> getNamespacesInScope() {
        Element emt = this.getParentElement();
        if (emt == null) {
            return Collections.singletonList(Namespace.XML_NAMESPACE);
        }
        return emt.getNamespacesInScope();
    }

    @Override
    public List<Namespace> getNamespacesIntroduced() {
        return Collections.emptyList();
    }

    @Override
    public List<Namespace> getNamespacesInherited() {
        return this.getNamespacesInScope();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CType {
        Comment,
        Element,
        ProcessingInstruction,
        EntityRef,
        Text,
        CDATA,
        DocType;

    }
}

