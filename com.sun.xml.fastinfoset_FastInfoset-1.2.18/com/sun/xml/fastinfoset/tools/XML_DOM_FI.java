/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;
import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class XML_DOM_FI
extends TransformInputOutput {
    @Override
    public void parse(InputStream document, OutputStream finf, String workingDirectory) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        if (workingDirectory != null) {
            db.setEntityResolver(XML_DOM_FI.createRelativePathResolver(workingDirectory));
        }
        Document d = db.parse(document);
        DOMDocumentSerializer s = new DOMDocumentSerializer();
        s.setOutputStream(finf);
        s.serialize(d);
    }

    @Override
    public void parse(InputStream document, OutputStream finf) throws Exception {
        this.parse(document, finf, null);
    }

    public static void main(String[] args) throws Exception {
        XML_DOM_FI p = new XML_DOM_FI();
        p.parse(args);
    }
}

