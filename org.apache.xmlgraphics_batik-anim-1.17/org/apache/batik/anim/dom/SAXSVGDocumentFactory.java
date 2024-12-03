/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGDocumentFactory
 *  org.apache.batik.dom.util.SAXDocumentFactory
 *  org.apache.batik.util.MimeTypeConstants
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.anim.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.MissingResourceException;
import java.util.Properties;
import org.apache.batik.anim.dom.SVG12DOMImplementation;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.util.MimeTypeConstants;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SAXSVGDocumentFactory
extends SAXDocumentFactory
implements SVGDocumentFactory {
    public static final Object LOCK = new Object();
    public static final String KEY_PUBLIC_IDS = "publicIds";
    public static final String KEY_SKIPPABLE_PUBLIC_IDS = "skippablePublicIds";
    public static final String KEY_SKIP_DTD = "skipDTD";
    public static final String KEY_SYSTEM_ID = "systemId.";
    protected static final String DTDIDS = "org.apache.batik.anim.dom.resources.dtdids";
    protected static final String HTTP_CHARSET = "charset";
    protected static String dtdids;
    protected static String skippable_dtdids;
    protected static String skip_dtd;
    protected static Properties dtdProps;

    public SAXSVGDocumentFactory(String parser) {
        super(SVGDOMImplementation.getDOMImplementation(), parser);
    }

    public SAXSVGDocumentFactory(String parser, boolean dd) {
        super(SVGDOMImplementation.getDOMImplementation(), parser, dd);
    }

    public SVGDocument createSVGDocument(String uri) throws IOException {
        return (SVGDocument)this.createDocument(uri);
    }

    public SVGDocument createSVGDocument(String uri, InputStream inp) throws IOException {
        return (SVGDocument)this.createDocument(uri, inp);
    }

    public SVGDocument createSVGDocument(String uri, Reader r) throws IOException {
        return (SVGDocument)this.createDocument(uri, r);
    }

    public Document createDocument(String uri) throws IOException {
        int i;
        int eqIdx;
        ParsedURL purl = new ParsedURL(uri);
        InputStream is = purl.openStream(MimeTypeConstants.MIME_TYPES_SVG_LIST.iterator());
        uri = purl.getPostConnectionURL();
        InputSource isrc = new InputSource(is);
        String contentType = purl.getContentType();
        int cindex = -1;
        if (contentType != null) {
            contentType = contentType.toLowerCase();
            cindex = contentType.indexOf(HTTP_CHARSET);
        }
        String charset = null;
        if (cindex != -1 && (eqIdx = contentType.indexOf(61, i = cindex + HTTP_CHARSET.length())) != -1) {
            int idx = contentType.indexOf(44, ++eqIdx);
            int semiIdx = contentType.indexOf(59, eqIdx);
            if (semiIdx != -1 && (semiIdx < idx || idx == -1)) {
                idx = semiIdx;
            }
            charset = idx != -1 ? contentType.substring(eqIdx, idx) : contentType.substring(eqIdx);
            charset = charset.trim();
            isrc.setEncoding(charset);
        }
        isrc.setSystemId(uri);
        SVGOMDocument doc = (SVGOMDocument)((Object)super.createDocument("http://www.w3.org/2000/svg", "svg", uri, isrc));
        doc.setParsedURL(new ParsedURL(uri));
        doc.setDocumentInputEncoding(charset);
        doc.setXmlStandalone(this.isStandalone);
        doc.setXmlVersion(this.xmlVersion);
        return doc;
    }

    public Document createDocument(String uri, InputStream inp) throws IOException {
        Document doc;
        InputSource is = new InputSource(inp);
        is.setSystemId(uri);
        try {
            doc = super.createDocument("http://www.w3.org/2000/svg", "svg", uri, is);
            if (uri != null) {
                ((SVGOMDocument)((Object)doc)).setParsedURL(new ParsedURL(uri));
            }
            AbstractDocument d = (AbstractDocument)doc;
            d.setDocumentURI(uri);
            d.setXmlStandalone(this.isStandalone);
            d.setXmlVersion(this.xmlVersion);
        }
        catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }

    public Document createDocument(String uri, Reader r) throws IOException {
        Document doc;
        InputSource is = new InputSource(r);
        is.setSystemId(uri);
        try {
            doc = super.createDocument("http://www.w3.org/2000/svg", "svg", uri, is);
            if (uri != null) {
                ((SVGOMDocument)((Object)doc)).setParsedURL(new ParsedURL(uri));
            }
            AbstractDocument d = (AbstractDocument)doc;
            d.setDocumentURI(uri);
            d.setXmlStandalone(this.isStandalone);
            d.setXmlVersion(this.xmlVersion);
        }
        catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }

    public Document createDocument(String ns, String root, String uri) throws IOException {
        if (!"http://www.w3.org/2000/svg".equals(ns) || !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return this.createDocument(uri);
    }

    public Document createDocument(String ns, String root, String uri, InputStream is) throws IOException {
        if (!"http://www.w3.org/2000/svg".equals(ns) || !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return this.createDocument(uri, is);
    }

    public Document createDocument(String ns, String root, String uri, Reader r) throws IOException {
        if (!"http://www.w3.org/2000/svg".equals(ns) || !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return this.createDocument(uri, r);
    }

    public DOMImplementation getDOMImplementation(String ver) {
        if (ver == null || ver.length() == 0 || ver.equals("1.0") || ver.equals("1.1")) {
            return SVGDOMImplementation.getDOMImplementation();
        }
        if (ver.equals("1.2")) {
            return SVG12DOMImplementation.getDOMImplementation();
        }
        throw new RuntimeException("Unsupport SVG version '" + ver + "'");
    }

    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            String localSystemId;
            Object object = LOCK;
            synchronized (object) {
                if (dtdProps == null) {
                    dtdProps = new Properties();
                    try {
                        Class<SAXSVGDocumentFactory> cls = SAXSVGDocumentFactory.class;
                        InputStream is = cls.getResourceAsStream("resources/dtdids.properties");
                        dtdProps.load(is);
                    }
                    catch (IOException ioe) {
                        throw new SAXException(ioe);
                    }
                }
                if (dtdids == null) {
                    dtdids = dtdProps.getProperty(KEY_PUBLIC_IDS);
                }
                if (skippable_dtdids == null) {
                    skippable_dtdids = dtdProps.getProperty(KEY_SKIPPABLE_PUBLIC_IDS);
                }
                if (skip_dtd == null) {
                    skip_dtd = dtdProps.getProperty(KEY_SKIP_DTD);
                }
            }
            if (publicId == null) {
                return null;
            }
            if (!this.isValidating && skippable_dtdids.indexOf(publicId) != -1) {
                return new InputSource(new StringReader(skip_dtd));
            }
            if (dtdids.indexOf(publicId) != -1 && (localSystemId = dtdProps.getProperty(KEY_SYSTEM_ID + publicId.replace(' ', '_'))) != null && !"".equals(localSystemId)) {
                return new InputSource(((Object)((Object)this)).getClass().getResource(localSystemId).toString());
            }
        }
        catch (MissingResourceException e) {
            throw new SAXException(e);
        }
        return null;
    }
}

