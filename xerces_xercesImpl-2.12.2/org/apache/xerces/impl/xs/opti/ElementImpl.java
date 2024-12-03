/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.opti.DefaultElement;
import org.apache.xerces.impl.xs.opti.NamedNodeMapImpl;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementImpl
extends DefaultElement {
    SchemaDOM schemaDOM;
    Attr[] attrs;
    int row = -1;
    int col = -1;
    int parentRow = -1;
    int line;
    int column;
    int charOffset;
    String fAnnotation;
    String fSyntheticAnnotation;

    public ElementImpl(int n, int n2, int n3) {
        this.nodeType = 1;
        this.line = n;
        this.column = n2;
        this.charOffset = n3;
    }

    public ElementImpl(int n, int n2) {
        this(n, n2, -1);
    }

    public ElementImpl(String string, String string2, String string3, String string4, int n, int n2, int n3) {
        super(string, string2, string3, string4, (short)1);
        this.line = n;
        this.column = n2;
        this.charOffset = n3;
    }

    public ElementImpl(String string, String string2, String string3, String string4, int n, int n2) {
        this(string, string2, string3, string4, n, n2, -1);
    }

    @Override
    public Document getOwnerDocument() {
        return this.schemaDOM;
    }

    @Override
    public Node getParentNode() {
        return this.schemaDOM.relations[this.row][0];
    }

    @Override
    public boolean hasChildNodes() {
        return this.parentRow != -1;
    }

    @Override
    public Node getFirstChild() {
        if (this.parentRow == -1) {
            return null;
        }
        return this.schemaDOM.relations[this.parentRow][1];
    }

    @Override
    public Node getLastChild() {
        int n;
        if (this.parentRow == -1) {
            return null;
        }
        for (n = 1; n < this.schemaDOM.relations[this.parentRow].length; ++n) {
            if (this.schemaDOM.relations[this.parentRow][n] != null) continue;
            return this.schemaDOM.relations[this.parentRow][n - 1];
        }
        if (n == 1) {
            ++n;
        }
        return this.schemaDOM.relations[this.parentRow][n - 1];
    }

    @Override
    public Node getPreviousSibling() {
        if (this.col == 1) {
            return null;
        }
        return this.schemaDOM.relations[this.row][this.col - 1];
    }

    @Override
    public Node getNextSibling() {
        if (this.col == this.schemaDOM.relations[this.row].length - 1) {
            return null;
        }
        return this.schemaDOM.relations[this.row][this.col + 1];
    }

    @Override
    public NamedNodeMap getAttributes() {
        return new NamedNodeMapImpl(this.attrs);
    }

    @Override
    public boolean hasAttributes() {
        return this.attrs.length != 0;
    }

    @Override
    public String getTagName() {
        return this.rawname;
    }

    @Override
    public String getAttribute(String string) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string)) continue;
            return this.attrs[i].getValue();
        }
        return "";
    }

    @Override
    public Attr getAttributeNode(String string) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string)) continue;
            return this.attrs[i];
        }
        return null;
    }

    @Override
    public String getAttributeNS(String string, String string2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getLocalName().equals(string2) || !ElementImpl.nsEquals(this.attrs[i].getNamespaceURI(), string)) continue;
            return this.attrs[i].getValue();
        }
        return "";
    }

    @Override
    public Attr getAttributeNodeNS(String string, String string2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string2) || !ElementImpl.nsEquals(this.attrs[i].getNamespaceURI(), string)) continue;
            return this.attrs[i];
        }
        return null;
    }

    @Override
    public boolean hasAttribute(String string) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasAttributeNS(String string, String string2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string2) || !ElementImpl.nsEquals(this.attrs[i].getNamespaceURI(), string)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setAttribute(String string, String string2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (!this.attrs[i].getName().equals(string)) continue;
            this.attrs[i].setValue(string2);
            return;
        }
    }

    public int getLineNumber() {
        return this.line;
    }

    public int getColumnNumber() {
        return this.column;
    }

    public int getCharacterOffset() {
        return this.charOffset;
    }

    public String getAnnotation() {
        return this.fAnnotation;
    }

    public String getSyntheticAnnotation() {
        return this.fSyntheticAnnotation;
    }

    private static boolean nsEquals(String string, String string2) {
        if (string == null) {
            return string2 == null;
        }
        return string.equals(string2);
    }
}

