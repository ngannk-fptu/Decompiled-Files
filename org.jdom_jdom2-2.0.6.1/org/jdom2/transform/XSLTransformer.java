/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.transform;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.JDOMFactory;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
import org.jdom2.transform.XSLTransformException;
import org.xml.sax.EntityResolver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XSLTransformer {
    private Templates templates;
    private JDOMFactory factory = null;

    private XSLTransformer(Source stylesheet) throws XSLTransformException {
        try {
            this.templates = TransformerFactory.newInstance().newTemplates(stylesheet);
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not construct XSLTransformer", e);
        }
    }

    public XSLTransformer(String stylesheetSystemId) throws XSLTransformException {
        this(new StreamSource(stylesheetSystemId));
    }

    public XSLTransformer(InputStream stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    public XSLTransformer(Reader stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    public XSLTransformer(File stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    public XSLTransformer(Document stylesheet) throws XSLTransformException {
        this(new JDOMSource(stylesheet));
    }

    public List<Content> transform(List<Content> inputNodes) throws XSLTransformException {
        JDOMSource source = new JDOMSource(inputNodes);
        JDOMResult result = new JDOMResult();
        result.setFactory(this.factory);
        try {
            this.templates.newTransformer().transform(source, result);
            return result.getResult();
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not perform transformation", e);
        }
    }

    public Document transform(Document inputDoc) throws XSLTransformException {
        return this.transform(inputDoc, null);
    }

    public Document transform(Document inputDoc, EntityResolver resolver) throws XSLTransformException {
        JDOMSource source = new JDOMSource(inputDoc, resolver);
        JDOMResult result = new JDOMResult();
        result.setFactory(this.factory);
        try {
            this.templates.newTransformer().transform(source, result);
            return result.getDocument();
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not perform transformation", e);
        }
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }
}

