/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.impl.xs.opti.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class DefaultText
extends NodeImpl
implements Text {
    @Override
    public String getData() throws DOMException {
        return null;
    }

    @Override
    public void setData(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public String substringData(int n, int n2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void appendData(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void insertData(int n, String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void deleteData(int n, int n2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void replaceData(int n, int n2, String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Text splitText(int n) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public boolean isElementContentWhitespace() {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String getWholeText() {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Text replaceWholeText(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }
}

