/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.adapters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jdom2.DocType;
import org.jdom2.JDOMException;
import org.jdom2.adapters.DOMAdapter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public abstract class AbstractDOMAdapter
implements DOMAdapter {
    public Document createDocument(DocType doctype) throws JDOMException {
        if (doctype == null) {
            return this.createDocument();
        }
        DOMImplementation domImpl = this.createDocument().getImplementation();
        DocumentType domDocType = domImpl.createDocumentType(doctype.getElementName(), doctype.getPublicID(), doctype.getSystemID());
        this.setInternalSubset(domDocType, doctype.getInternalSubset());
        Document ret = domImpl.createDocument("http://temporary", doctype.getElementName(), domDocType);
        Element root = ret.getDocumentElement();
        if (root != null) {
            ret.removeChild(root);
        }
        return ret;
    }

    protected void setInternalSubset(DocumentType dt, String s) {
        if (dt == null || s == null) {
            return;
        }
        try {
            Class<?> dtclass = dt.getClass();
            Method setInternalSubset = dtclass.getMethod("setInternalSubset", String.class);
            setInternalSubset.invoke((Object)dt, s);
        }
        catch (InvocationTargetException invocationTargetException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (SecurityException securityException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }
}

