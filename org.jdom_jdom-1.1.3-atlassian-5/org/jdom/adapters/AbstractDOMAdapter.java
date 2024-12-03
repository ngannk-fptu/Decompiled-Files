/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import org.jdom.DocType;
import org.jdom.JDOMException;
import org.jdom.adapters.DOMAdapter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public abstract class AbstractDOMAdapter
implements DOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: AbstractDOMAdapter.java,v $ $Revision: 1.21 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";

    @Override
    public Document getDocument(File filename, boolean validate) throws IOException, JDOMException {
        return this.getDocument(new FileInputStream(filename), validate);
    }

    @Override
    public abstract Document getDocument(InputStream var1, boolean var2) throws IOException, JDOMException;

    @Override
    public abstract Document createDocument() throws JDOMException;

    @Override
    public Document createDocument(DocType doctype) throws JDOMException {
        if (doctype == null) {
            return this.createDocument();
        }
        DOMImplementation domImpl = this.createDocument().getImplementation();
        DocumentType domDocType = domImpl.createDocumentType(doctype.getElementName(), doctype.getPublicID(), doctype.getSystemID());
        this.setInternalSubset(domDocType, doctype.getInternalSubset());
        return domImpl.createDocument("http://temporary", doctype.getElementName(), domDocType);
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
        catch (Exception exception) {
            // empty catch block
        }
    }
}

