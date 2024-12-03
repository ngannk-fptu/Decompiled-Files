/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javanet.staxutils.SimpleNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public class ElementContext
extends SimpleNamespaceContext {
    private QName name;
    private ElementContext parent;
    private List attributeNames;
    private Map attributes;
    private List namespacePrefixes;
    private boolean isEmpty;
    private boolean readOnly;

    public ElementContext(QName name) {
        this.name = name;
    }

    public ElementContext(QName name, boolean isEmpty) {
        this.name = name;
        this.isEmpty = isEmpty;
    }

    public ElementContext(QName name, NamespaceContext context) {
        super(context);
        this.name = name;
    }

    public ElementContext(QName name, ElementContext parent) {
        super(parent);
        this.name = name;
        this.parent = parent;
    }

    public ElementContext(QName name, ElementContext parent, boolean isEmpty) {
        super(parent);
        this.name = name;
        this.parent = parent;
        this.isEmpty = isEmpty;
    }

    public ElementContext getParentContext() {
        return this.parent;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public QName getName() {
        return this.name;
    }

    public String getPath() {
        return this.appendPath(new StringBuffer()).toString();
    }

    public String toString() {
        return this.getPath();
    }

    public StringBuffer appendPath(StringBuffer buffer) {
        if (this.parent != null) {
            this.parent.appendPath(buffer);
        }
        return buffer.append('/').append(this.name);
    }

    public int getDepth() {
        if (this.parent == null) {
            return 0;
        }
        return this.parent.getDepth() + 1;
    }

    public ElementContext newSubContext(QName name) {
        if (!this.isEmpty()) {
            return new ElementContext(name, this);
        }
        throw new IllegalStateException("ElementContext is empty");
    }

    public ElementContext newSubContext(QName name, boolean isEmpty) {
        if (!this.isEmpty()) {
            return new ElementContext(name, this, isEmpty);
        }
        throw new IllegalStateException("ElementContext is empty");
    }

    public void putAttribute(QName name, String value) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("ElementContext is readOnly");
        }
        if (this.attributes == null) {
            this.attributes = new HashMap();
            this.attributeNames = new ArrayList();
        }
        this.attributeNames.add(name);
        this.attributes.put(name, value);
    }

    public void putNamespace(String prefix, String nsURI) {
        if (this.isReadOnly()) {
            throw new IllegalStateException("ElementContext is readOnly");
        }
        if (this.namespacePrefixes == null) {
            this.namespacePrefixes = new ArrayList();
        }
        if (prefix.length() == 0) {
            this.namespacePrefixes.add(prefix);
            super.setDefaultNamespace(nsURI);
        } else {
            this.namespacePrefixes.add(prefix);
            super.setPrefix(prefix, nsURI);
        }
    }

    public int attributeCount() {
        if (this.attributes != null) {
            return this.attributes.size();
        }
        return 0;
    }

    public String getAttribute(int idx) {
        return this.getAttribute(this.getAttributeName(idx));
    }

    public QName getAttributeName(int idx) {
        if (this.attributeNames != null) {
            return (QName)this.attributeNames.get(idx);
        }
        throw new IndexOutOfBoundsException("Attribute index " + idx + " doesn't exist");
    }

    public String getAttribute(QName name) {
        if (this.attributes != null) {
            return (String)this.attributes.get(name);
        }
        return null;
    }

    public boolean attributeExists(QName name) {
        if (this.attributes != null) {
            return this.attributes.containsKey(name);
        }
        return false;
    }

    public Iterator attributeNames() {
        if (this.attributeNames != null) {
            return Collections.unmodifiableList(this.attributeNames).iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public int namespaceCount() {
        if (this.namespacePrefixes != null) {
            return this.namespacePrefixes.size();
        }
        return 0;
    }

    public String getNamespaceURI(int idx) {
        return this.getNamespaceURI(this.getNamespacePrefix(idx));
    }

    public String getNamespacePrefix(int idx) {
        if (this.namespacePrefixes != null) {
            return (String)this.namespacePrefixes.get(idx);
        }
        throw new IndexOutOfBoundsException("Namespace index " + idx + " doesn't exist");
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }
}

