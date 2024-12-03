/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.tools.TransformInputOutput;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.jvnet.fastinfoset.FastInfosetResult;
import org.w3c.dom.Document;

public class XML_DOM_SAX_FI
extends TransformInputOutput {
    @Override
    public void parse(InputStream document, OutputStream finf, String workingDirectory) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        if (workingDirectory != null) {
            db.setEntityResolver(XML_DOM_SAX_FI.createRelativePathResolver(workingDirectory));
        }
        Document d = db.parse(document);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(new DOMSource(d), new FastInfosetResult(finf));
    }

    @Override
    public void parse(InputStream document, OutputStream finf) throws Exception {
        this.parse(document, finf, null);
    }

    public static void main(String[] args) throws Exception {
        XML_DOM_SAX_FI p = new XML_DOM_SAX_FI();
        p.parse(args);
    }
}

