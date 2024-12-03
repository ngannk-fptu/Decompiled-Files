/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.trax;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.trax.ObjectFactory;
import org.apache.xalan.xsltc.trax.TrAXFilter;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.xml.sax.XMLFilter;

public class SmartTransformerFactoryImpl
extends SAXTransformerFactory {
    private static final String CLASS_NAME = "SmartTransformerFactoryImpl";
    private SAXTransformerFactory _xsltcFactory = null;
    private SAXTransformerFactory _xalanFactory = null;
    private SAXTransformerFactory _currFactory = null;
    private ErrorListener _errorlistener = null;
    private URIResolver _uriresolver = null;
    private boolean featureSecureProcessing = false;

    private void createXSLTCTransformerFactory() {
        this._currFactory = this._xsltcFactory = new TransformerFactoryImpl();
    }

    private void createXalanTransformerFactory() {
        String xalanMessage = "org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl could not create an org.apache.xalan.processor.TransformerFactoryImpl.";
        try {
            Class xalanFactClass = ObjectFactory.findProviderClass("org.apache.xalan.processor.TransformerFactoryImpl", ObjectFactory.findClassLoader(), true);
            this._xalanFactory = (SAXTransformerFactory)xalanFactClass.newInstance();
        }
        catch (ClassNotFoundException e) {
            System.err.println("org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl could not create an org.apache.xalan.processor.TransformerFactoryImpl.");
        }
        catch (InstantiationException e) {
            System.err.println("org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl could not create an org.apache.xalan.processor.TransformerFactoryImpl.");
        }
        catch (IllegalAccessException e) {
            System.err.println("org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl could not create an org.apache.xalan.processor.TransformerFactoryImpl.");
        }
        this._currFactory = this._xalanFactory;
    }

    @Override
    public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
        this._errorlistener = listener;
    }

    @Override
    public ErrorListener getErrorListener() {
        return this._errorlistener;
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        if (name.equals("translet-name") || name.equals("debug")) {
            if (this._xsltcFactory == null) {
                this.createXSLTCTransformerFactory();
            }
            return this._xsltcFactory.getAttribute(name);
        }
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        return this._xalanFactory.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        if (name.equals("translet-name") || name.equals("debug")) {
            if (this._xsltcFactory == null) {
                this.createXSLTCTransformerFactory();
            }
            this._xsltcFactory.setAttribute(name, value);
        } else {
            if (this._xalanFactory == null) {
                this.createXalanTransformerFactory();
            }
            this._xalanFactory.setAttribute(name, value);
        }
    }

    @Override
    public void setFeature(String name, boolean value) throws TransformerConfigurationException {
        if (name == null) {
            ErrorMsg err = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.featureSecureProcessing = value;
            return;
        }
        ErrorMsg err = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", name);
        throw new TransformerConfigurationException(err.toString());
    }

    @Override
    public boolean getFeature(String name) {
        String[] features = new String[]{"http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature"};
        if (name == null) {
            ErrorMsg err = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        for (int i = 0; i < features.length; ++i) {
            if (!name.equals(features[i])) continue;
            return true;
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.featureSecureProcessing;
        }
        return false;
    }

    @Override
    public URIResolver getURIResolver() {
        return this._uriresolver;
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        this._uriresolver = resolver;
    }

    @Override
    public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
        if (this._currFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        return this._currFactory.getAssociatedStylesheet(source, media, title, charset);
    }

    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        this._currFactory = this._xalanFactory;
        return this._currFactory.newTransformer();
    }

    @Override
    public Transformer newTransformer(Source source) throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        this._currFactory = this._xalanFactory;
        return this._currFactory.newTransformer(source);
    }

    @Override
    public Templates newTemplates(Source source) throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        this._currFactory = this._xsltcFactory;
        return this._currFactory.newTemplates(source);
    }

    @Override
    public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        return this._xsltcFactory.newTemplatesHandler();
    }

    @Override
    public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        return this._xalanFactory.newTransformerHandler();
    }

    @Override
    public TransformerHandler newTransformerHandler(Source src) throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        return this._xalanFactory.newTransformerHandler(src);
    }

    @Override
    public TransformerHandler newTransformerHandler(Templates templates) throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        return this._xsltcFactory.newTransformerHandler(templates);
    }

    @Override
    public XMLFilter newXMLFilter(Source src) throws TransformerConfigurationException {
        Templates templates;
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        if ((templates = this._xsltcFactory.newTemplates(src)) == null) {
            return null;
        }
        return this.newXMLFilter(templates);
    }

    @Override
    public XMLFilter newXMLFilter(Templates templates) throws TransformerConfigurationException {
        try {
            return new TrAXFilter(templates);
        }
        catch (TransformerConfigurationException e1) {
            ErrorListener errorListener;
            if (this._xsltcFactory == null) {
                this.createXSLTCTransformerFactory();
            }
            if ((errorListener = this._xsltcFactory.getErrorListener()) != null) {
                try {
                    errorListener.fatalError(e1);
                    return null;
                }
                catch (TransformerException e2) {
                    new TransformerConfigurationException(e2);
                }
            }
            throw e1;
        }
    }
}

