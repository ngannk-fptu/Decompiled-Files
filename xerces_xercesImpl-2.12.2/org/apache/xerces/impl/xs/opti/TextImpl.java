/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.opti.DefaultText;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class TextImpl
extends DefaultText {
    String fData = null;
    SchemaDOM fSchemaDOM = null;
    int fRow;
    int fCol;

    public TextImpl(StringBuffer stringBuffer, SchemaDOM schemaDOM, int n, int n2) {
        this.fData = stringBuffer.toString();
        this.fSchemaDOM = schemaDOM;
        this.fRow = n;
        this.fCol = n2;
        this.uri = null;
        this.localpart = null;
        this.prefix = null;
        this.rawname = null;
        this.nodeType = (short)3;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    @Override
    public Node getParentNode() {
        return this.fSchemaDOM.relations[this.fRow][0];
    }

    @Override
    public Node getPreviousSibling() {
        if (this.fCol == 1) {
            return null;
        }
        return this.fSchemaDOM.relations[this.fRow][this.fCol - 1];
    }

    @Override
    public Node getNextSibling() {
        if (this.fCol == this.fSchemaDOM.relations[this.fRow].length - 1) {
            return null;
        }
        return this.fSchemaDOM.relations[this.fRow][this.fCol + 1];
    }

    @Override
    public String getData() throws DOMException {
        return this.fData;
    }

    @Override
    public int getLength() {
        if (this.fData == null) {
            return 0;
        }
        return this.fData.length();
    }

    @Override
    public String substringData(int n, int n2) throws DOMException {
        if (this.fData == null) {
            return null;
        }
        if (n2 < 0 || n < 0 || n > this.fData.length()) {
            throw new DOMException(1, "parameter error");
        }
        if (n + n2 >= this.fData.length()) {
            return this.fData.substring(n);
        }
        return this.fData.substring(n, n + n2);
    }
}

