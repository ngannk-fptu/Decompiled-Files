/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import java.io.FileNotFoundException;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMCache;
import org.apache.xalan.xsltc.DOMEnhancedForDTM;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.MultiDOM;
import org.apache.xalan.xsltc.dom.SingletonIterator;
import org.apache.xalan.xsltc.dom.UnionIterator;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.trax.TemplatesImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.EmptyIterator;
import org.apache.xml.utils.SystemIDResolver;

public final class LoadDocument {
    private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";

    public static DTMAxisIterator documentF(Object arg1, DTMAxisIterator arg2, String xslURI, AbstractTranslet translet, DOM dom) throws TransletException {
        String baseURI = null;
        int arg2FirstNode = arg2.next();
        if (arg2FirstNode == -1) {
            return EmptyIterator.getInstance();
        }
        baseURI = dom.getDocumentURI(arg2FirstNode);
        if (!SystemIDResolver.isAbsoluteURI(baseURI)) {
            baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
        }
        try {
            if (arg1 instanceof String) {
                if (((String)arg1).length() == 0) {
                    return LoadDocument.document(xslURI, "", translet, dom);
                }
                return LoadDocument.document((String)arg1, baseURI, translet, dom);
            }
            if (arg1 instanceof DTMAxisIterator) {
                return LoadDocument.document((DTMAxisIterator)arg1, baseURI, translet, dom);
            }
            String err = "document(" + arg1.toString() + ")";
            throw new IllegalArgumentException(err);
        }
        catch (Exception e) {
            throw new TransletException(e);
        }
    }

    public static DTMAxisIterator documentF(Object arg, String xslURI, AbstractTranslet translet, DOM dom) throws TransletException {
        try {
            if (arg instanceof String) {
                String href;
                if (xslURI == null) {
                    xslURI = "";
                }
                String baseURI = xslURI;
                if (!SystemIDResolver.isAbsoluteURI(xslURI)) {
                    baseURI = SystemIDResolver.getAbsoluteURIFromRelative(xslURI);
                }
                if ((href = (String)arg).length() == 0) {
                    href = "";
                    TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
                    DOM sdom = null;
                    if (templates != null) {
                        sdom = templates.getStylesheetDOM();
                    }
                    if (sdom != null) {
                        return LoadDocument.document(sdom, translet, dom);
                    }
                    return LoadDocument.document(href, baseURI, translet, dom, true);
                }
                return LoadDocument.document(href, baseURI, translet, dom);
            }
            if (arg instanceof DTMAxisIterator) {
                return LoadDocument.document((DTMAxisIterator)arg, null, translet, dom);
            }
            String err = "document(" + arg.toString() + ")";
            throw new IllegalArgumentException(err);
        }
        catch (Exception e) {
            throw new TransletException(e);
        }
    }

    private static DTMAxisIterator document(String uri, String base, AbstractTranslet translet, DOM dom) throws Exception {
        return LoadDocument.document(uri, base, translet, dom, false);
    }

    private static DTMAxisIterator document(String uri, String base, AbstractTranslet translet, DOM dom, boolean cacheDOM) throws Exception {
        DOM newdom;
        DOM newDom;
        String originalUri = uri;
        MultiDOM multiplexer = (MultiDOM)dom;
        if (base != null && base.length() != 0) {
            uri = SystemIDResolver.getAbsoluteURI(uri, base);
        }
        if (uri == null || uri.length() == 0) {
            return EmptyIterator.getInstance();
        }
        int mask = multiplexer.getDocumentMask(uri);
        if (mask != -1 && (newDom = ((DOMAdapter)multiplexer.getDOMAdapter(uri)).getDOMImpl()) instanceof DOMEnhancedForDTM) {
            return new SingletonIterator(((DOMEnhancedForDTM)newDom).getDocument(), true);
        }
        DOMCache cache = translet.getDOMCache();
        mask = multiplexer.nextMask();
        if (cache != null) {
            newdom = cache.retrieveDocument(base, originalUri, translet);
            if (newdom == null) {
                FileNotFoundException e = new FileNotFoundException(originalUri);
                throw new TransletException(e);
            }
        } else {
            TemplatesImpl templates;
            XSLTCDTMManager dtmManager = (XSLTCDTMManager)multiplexer.getDTMManager();
            DOMEnhancedForDTM enhancedDOM = (DOMEnhancedForDTM)((Object)dtmManager.getDTM(new StreamSource(uri), false, null, true, false, translet.hasIdCall(), cacheDOM));
            newdom = enhancedDOM;
            if (cacheDOM && (templates = (TemplatesImpl)translet.getTemplates()) != null) {
                templates.setStylesheetDOM(enhancedDOM);
            }
            translet.prepassDocument(enhancedDOM);
            enhancedDOM.setDocumentURI(uri);
        }
        DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
        multiplexer.addDOMAdapter(domAdapter);
        translet.buildKeys(domAdapter, null, null, newdom.getDocument());
        return new SingletonIterator(newdom.getDocument(), true);
    }

    private static DTMAxisIterator document(DTMAxisIterator arg1, String baseURI, AbstractTranslet translet, DOM dom) throws Exception {
        UnionIterator union = new UnionIterator(dom);
        int node = -1;
        while ((node = arg1.next()) != -1) {
            String uri = dom.getStringValueX(node);
            if (baseURI == null && !SystemIDResolver.isAbsoluteURI(baseURI = dom.getDocumentURI(node))) {
                baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
            }
            union.addIterator(LoadDocument.document(uri, baseURI, translet, dom));
        }
        return union;
    }

    private static DTMAxisIterator document(DOM newdom, AbstractTranslet translet, DOM dom) throws Exception {
        DTMManager dtmManager = ((MultiDOM)dom).getDTMManager();
        if (dtmManager != null && newdom instanceof DTM) {
            ((DTM)((Object)newdom)).migrateTo(dtmManager);
        }
        translet.prepassDocument(newdom);
        DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
        ((MultiDOM)dom).addDOMAdapter(domAdapter);
        translet.buildKeys(domAdapter, null, null, newdom.getDocument());
        return new SingletonIterator(newdom.getDocument(), true);
    }
}

