/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.resolver.Catalog
 *  org.apache.xml.resolver.CatalogManager
 *  org.apache.xml.resolver.readers.CatalogReader
 *  org.apache.xml.resolver.readers.SAXCatalogReader
 */
package org.apache.xerces.util;

import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.apache.xerces.util.URI;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.readers.CatalogReader;
import org.apache.xml.resolver.readers.SAXCatalogReader;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class XMLCatalogResolver
implements XMLEntityResolver,
EntityResolver2,
LSResourceResolver {
    private CatalogManager fResolverCatalogManager = null;
    private Catalog fCatalog = null;
    private String[] fCatalogsList = null;
    private boolean fCatalogsChanged = true;
    private boolean fPreferPublic = true;
    private boolean fUseLiteralSystemId = true;

    public XMLCatalogResolver() {
        this(null, true);
    }

    public XMLCatalogResolver(String[] stringArray) {
        this(stringArray, true);
    }

    public XMLCatalogResolver(String[] stringArray, boolean bl) {
        this.init(stringArray, bl);
    }

    public final synchronized String[] getCatalogList() {
        return this.fCatalogsList != null ? (String[])this.fCatalogsList.clone() : null;
    }

    public final synchronized void setCatalogList(String[] stringArray) {
        this.fCatalogsChanged = true;
        this.fCatalogsList = stringArray != null ? (String[])stringArray.clone() : null;
    }

    public final synchronized void clear() {
        this.fCatalog = null;
    }

    public final boolean getPreferPublic() {
        return this.fPreferPublic;
    }

    public final void setPreferPublic(boolean bl) {
        this.fPreferPublic = bl;
        this.fResolverCatalogManager.setPreferPublic(bl);
    }

    public final boolean getUseLiteralSystemId() {
        return this.fUseLiteralSystemId;
    }

    public final void setUseLiteralSystemId(boolean bl) {
        this.fUseLiteralSystemId = bl;
    }

    @Override
    public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
        String string3 = null;
        if (string != null && string2 != null) {
            string3 = this.resolvePublic(string, string2);
        } else if (string2 != null) {
            string3 = this.resolveSystem(string2);
        }
        if (string3 != null) {
            InputSource inputSource = new InputSource(string3);
            inputSource.setPublicId(string);
            return inputSource;
        }
        return null;
    }

    @Override
    public InputSource resolveEntity(String string, String string2, String string3, String string4) throws SAXException, IOException {
        Object object;
        String string5 = null;
        if (!this.getUseLiteralSystemId() && string3 != null) {
            try {
                object = new URI(new URI(string3), string4);
                string4 = ((URI)object).toString();
            }
            catch (URI.MalformedURIException malformedURIException) {
                // empty catch block
            }
        }
        if (string2 != null && string4 != null) {
            string5 = this.resolvePublic(string2, string4);
        } else if (string4 != null) {
            string5 = this.resolveSystem(string4);
        }
        if (string5 != null) {
            object = new InputSource(string5);
            ((InputSource)object).setPublicId(string2);
            return object;
        }
        return null;
    }

    @Override
    public InputSource getExternalSubset(String string, String string2) throws SAXException, IOException {
        return null;
    }

    @Override
    public LSInput resolveResource(String string, String string2, String string3, String string4, String string5) {
        String string6 = null;
        try {
            if (string2 != null) {
                string6 = this.resolveURI(string2);
            }
            if (!this.getUseLiteralSystemId() && string5 != null) {
                try {
                    URI uRI = new URI(new URI(string5), string4);
                    string4 = uRI.toString();
                }
                catch (URI.MalformedURIException malformedURIException) {
                    // empty catch block
                }
            }
            if (string6 == null) {
                if (string3 != null && string4 != null) {
                    string6 = this.resolvePublic(string3, string4);
                } else if (string4 != null) {
                    string6 = this.resolveSystem(string4);
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (string6 != null) {
            return new DOMInputImpl(string3, string6, string5);
        }
        return null;
    }

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier xMLResourceIdentifier) throws XNIException, IOException {
        String string = this.resolveIdentifier(xMLResourceIdentifier);
        if (string != null) {
            return new XMLInputSource(xMLResourceIdentifier.getPublicId(), string, xMLResourceIdentifier.getBaseSystemId());
        }
        return null;
    }

    public String resolveIdentifier(XMLResourceIdentifier xMLResourceIdentifier) throws IOException, XNIException {
        String string = null;
        String string2 = xMLResourceIdentifier.getNamespace();
        if (string2 != null) {
            string = this.resolveURI(string2);
        }
        if (string == null) {
            String string3;
            String string4 = xMLResourceIdentifier.getPublicId();
            String string5 = string3 = this.getUseLiteralSystemId() ? xMLResourceIdentifier.getLiteralSystemId() : xMLResourceIdentifier.getExpandedSystemId();
            if (string4 != null && string3 != null) {
                string = this.resolvePublic(string4, string3);
            } else if (string3 != null) {
                string = this.resolveSystem(string3);
            }
        }
        return string;
    }

    public final synchronized String resolveSystem(String string) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return this.fCatalog != null ? this.fCatalog.resolveSystem(string) : null;
    }

    public final synchronized String resolvePublic(String string, String string2) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return this.fCatalog != null ? this.fCatalog.resolvePublic(string, string2) : null;
    }

    public final synchronized String resolveURI(String string) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return this.fCatalog != null ? this.fCatalog.resolveURI(string) : null;
    }

    private void init(String[] stringArray, boolean bl) {
        this.fCatalogsList = stringArray != null ? (String[])stringArray.clone() : null;
        this.fPreferPublic = bl;
        this.fResolverCatalogManager = new CatalogManager();
        this.fResolverCatalogManager.setAllowOasisXMLCatalogPI(false);
        this.fResolverCatalogManager.setCatalogClassName("org.apache.xml.resolver.Catalog");
        this.fResolverCatalogManager.setCatalogFiles("");
        this.fResolverCatalogManager.setIgnoreMissingProperties(true);
        this.fResolverCatalogManager.setPreferPublic(this.fPreferPublic);
        this.fResolverCatalogManager.setRelativeCatalogs(false);
        this.fResolverCatalogManager.setUseStaticCatalog(false);
        this.fResolverCatalogManager.setVerbosity(0);
    }

    private void parseCatalogs() throws IOException {
        if (this.fCatalogsList != null) {
            this.fCatalog = new Catalog(this.fResolverCatalogManager);
            this.attachReaderToCatalog(this.fCatalog);
            for (int i = 0; i < this.fCatalogsList.length; ++i) {
                String string = this.fCatalogsList[i];
                if (string == null || string.length() <= 0) continue;
                this.fCatalog.parseCatalog(string);
            }
        } else {
            this.fCatalog = null;
        }
    }

    private void attachReaderToCatalog(Catalog catalog) {
        SAXParserFactoryImpl sAXParserFactoryImpl = new SAXParserFactoryImpl();
        sAXParserFactoryImpl.setNamespaceAware(true);
        sAXParserFactoryImpl.setValidating(false);
        SAXCatalogReader sAXCatalogReader = new SAXCatalogReader((SAXParserFactory)sAXParserFactoryImpl);
        sAXCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "org.apache.xml.resolver.readers.OASISXMLCatalogReader");
        catalog.addReader("application/xml", (CatalogReader)sAXCatalogReader);
    }
}

