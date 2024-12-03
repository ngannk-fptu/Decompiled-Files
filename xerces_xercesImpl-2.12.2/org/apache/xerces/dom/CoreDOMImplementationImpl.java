/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.lang.ref.SoftReference;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMOutputImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ObjectFactory;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.parsers.DOMParserImpl;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public class CoreDOMImplementationImpl
implements DOMImplementation,
DOMImplementationLS {
    private static final int SIZE = 2;
    private SoftReference[] schemaValidators = new SoftReference[2];
    private SoftReference[] xml10DTDValidators = new SoftReference[2];
    private SoftReference[] xml11DTDValidators = new SoftReference[2];
    private int freeSchemaValidatorIndex = -1;
    private int freeXML10DTDValidatorIndex = -1;
    private int freeXML11DTDValidatorIndex = -1;
    private int schemaValidatorsCurrentSize = 2;
    private int xml10DTDValidatorsCurrentSize = 2;
    private int xml11DTDValidatorsCurrentSize = 2;
    private SoftReference[] xml10DTDLoaders = new SoftReference[2];
    private SoftReference[] xml11DTDLoaders = new SoftReference[2];
    private int freeXML10DTDLoaderIndex = -1;
    private int freeXML11DTDLoaderIndex = -1;
    private int xml10DTDLoaderCurrentSize = 2;
    private int xml11DTDLoaderCurrentSize = 2;
    private int docAndDoctypeCounter = 0;
    static final CoreDOMImplementationImpl singleton = new CoreDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public boolean hasFeature(String string, String string2) {
        boolean bl;
        boolean bl2 = bl = string2 == null || string2.length() == 0;
        if (string.equalsIgnoreCase("+XPath") && (bl || string2.equals("3.0"))) {
            try {
                Class clazz = ObjectFactory.findProviderClass("org.apache.xpath.domapi.XPathEvaluatorImpl", ObjectFactory.findClassLoader(), true);
                Class<?>[] classArray = clazz.getInterfaces();
                for (int i = 0; i < classArray.length; ++i) {
                    if (!classArray[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) continue;
                    return true;
                }
            }
            catch (Exception exception) {
                return false;
            }
            return true;
        }
        if (string.startsWith("+")) {
            string = string.substring(1);
        }
        return string.equalsIgnoreCase("Core") && (bl || string2.equals("1.0") || string2.equals("2.0") || string2.equals("3.0")) || string.equalsIgnoreCase("XML") && (bl || string2.equals("1.0") || string2.equals("2.0") || string2.equals("3.0")) || string.equalsIgnoreCase("XMLVersion") && (bl || string2.equals("1.0") || string2.equals("1.1")) || string.equalsIgnoreCase("LS") && (bl || string2.equals("3.0")) || string.equalsIgnoreCase("ElementTraversal") && (bl || string2.equals("1.0"));
    }

    @Override
    public DocumentType createDocumentType(String string, String string2, String string3) {
        this.checkQName(string);
        return new DocumentTypeImpl(null, string, string2, string3);
    }

    final void checkQName(String string) {
        int n;
        int n2 = string.indexOf(58);
        int n3 = string.lastIndexOf(58);
        int n4 = string.length();
        if (n2 == 0 || n2 == n4 - 1 || n3 != n2) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException(14, string2);
        }
        int n5 = 0;
        if (n2 > 0) {
            if (!XMLChar.isNCNameStart(string.charAt(n5))) {
                String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, string3);
            }
            for (n = 1; n < n2; ++n) {
                if (XMLChar.isNCName(string.charAt(n))) continue;
                String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, string4);
            }
            n5 = n2 + 1;
        }
        if (!XMLChar.isNCNameStart(string.charAt(n5))) {
            String string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string5);
        }
        for (n = n5 + 1; n < n4; ++n) {
            if (XMLChar.isNCName(string.charAt(n))) continue;
            String string6 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string6);
        }
    }

    @Override
    public Document createDocument(String string, String string2, DocumentType documentType) throws DOMException {
        if (documentType != null && documentType.getOwnerDocument() != null) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, string3);
        }
        CoreDocumentImpl coreDocumentImpl = this.createDocument(documentType);
        if (string2 != null || string != null) {
            Element element = coreDocumentImpl.createElementNS(string, string2);
            coreDocumentImpl.appendChild(element);
        }
        return coreDocumentImpl;
    }

    protected CoreDocumentImpl createDocument(DocumentType documentType) {
        return new CoreDocumentImpl(documentType);
    }

    @Override
    public Object getFeature(String string, String string2) {
        if (singleton.hasFeature(string, string2)) {
            if (string.equalsIgnoreCase("+XPath")) {
                try {
                    Class clazz = ObjectFactory.findProviderClass("org.apache.xpath.domapi.XPathEvaluatorImpl", ObjectFactory.findClassLoader(), true);
                    Class<?>[] classArray = clazz.getInterfaces();
                    for (int i = 0; i < classArray.length; ++i) {
                        if (!classArray[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) continue;
                        return clazz.newInstance();
                    }
                }
                catch (Exception exception) {
                    return null;
                }
            } else {
                return singleton;
            }
        }
        return null;
    }

    @Override
    public LSParser createLSParser(short s, String string) throws DOMException {
        if (s != 1 || string != null && !"http://www.w3.org/2001/XMLSchema".equals(string) && !"http://www.w3.org/TR/REC-xml".equals(string)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string2);
        }
        if (string != null && string.equals("http://www.w3.org/TR/REC-xml")) {
            return new DOMParserImpl("org.apache.xerces.parsers.XML11DTDConfiguration", string);
        }
        return new DOMParserImpl("org.apache.xerces.parsers.XIncludeAwareParserConfiguration", string);
    }

    @Override
    public LSSerializer createLSSerializer() {
        try {
            Class clazz = ObjectFactory.findProviderClass("org.apache.xml.serializer.dom3.LSSerializerImpl", ObjectFactory.findClassLoader(), true);
            return (LSSerializer)clazz.newInstance();
        }
        catch (Exception exception) {
            return new DOMSerializerImpl();
        }
    }

    @Override
    public LSInput createLSInput() {
        return new DOMInputImpl();
    }

    synchronized RevalidationHandler getValidator(String string, String string2) {
        if (string == "http://www.w3.org/2001/XMLSchema") {
            while (this.freeSchemaValidatorIndex >= 0) {
                SoftReference softReference = this.schemaValidators[this.freeSchemaValidatorIndex];
                RevalidationHandlerHolder revalidationHandlerHolder = (RevalidationHandlerHolder)softReference.get();
                if (revalidationHandlerHolder != null && revalidationHandlerHolder.handler != null) {
                    RevalidationHandler revalidationHandler = revalidationHandlerHolder.handler;
                    revalidationHandlerHolder.handler = null;
                    --this.freeSchemaValidatorIndex;
                    return revalidationHandler;
                }
                this.schemaValidators[this.freeSchemaValidatorIndex--] = null;
            }
            return (RevalidationHandler)ObjectFactory.newInstance("org.apache.xerces.impl.xs.XMLSchemaValidator", ObjectFactory.findClassLoader(), true);
        }
        if (string == "http://www.w3.org/TR/REC-xml") {
            if ("1.1".equals(string2)) {
                while (this.freeXML11DTDValidatorIndex >= 0) {
                    SoftReference softReference = this.xml11DTDValidators[this.freeXML11DTDValidatorIndex];
                    RevalidationHandlerHolder revalidationHandlerHolder = (RevalidationHandlerHolder)softReference.get();
                    if (revalidationHandlerHolder != null && revalidationHandlerHolder.handler != null) {
                        RevalidationHandler revalidationHandler = revalidationHandlerHolder.handler;
                        revalidationHandlerHolder.handler = null;
                        --this.freeXML11DTDValidatorIndex;
                        return revalidationHandler;
                    }
                    this.xml11DTDValidators[this.freeXML11DTDValidatorIndex--] = null;
                }
                return (RevalidationHandler)ObjectFactory.newInstance("org.apache.xerces.impl.dtd.XML11DTDValidator", ObjectFactory.findClassLoader(), true);
            }
            while (this.freeXML10DTDValidatorIndex >= 0) {
                SoftReference softReference = this.xml10DTDValidators[this.freeXML10DTDValidatorIndex];
                RevalidationHandlerHolder revalidationHandlerHolder = (RevalidationHandlerHolder)softReference.get();
                if (revalidationHandlerHolder != null && revalidationHandlerHolder.handler != null) {
                    RevalidationHandler revalidationHandler = revalidationHandlerHolder.handler;
                    revalidationHandlerHolder.handler = null;
                    --this.freeXML10DTDValidatorIndex;
                    return revalidationHandler;
                }
                this.xml10DTDValidators[this.freeXML10DTDValidatorIndex--] = null;
            }
            return (RevalidationHandler)ObjectFactory.newInstance("org.apache.xerces.impl.dtd.XMLDTDValidator", ObjectFactory.findClassLoader(), true);
        }
        return null;
    }

    synchronized void releaseValidator(String string, String string2, RevalidationHandler revalidationHandler) {
        if (string == "http://www.w3.org/2001/XMLSchema") {
            RevalidationHandlerHolder revalidationHandlerHolder;
            SoftReference[] softReferenceArray;
            ++this.freeSchemaValidatorIndex;
            if (this.schemaValidators.length == this.freeSchemaValidatorIndex) {
                this.schemaValidatorsCurrentSize += 2;
                softReferenceArray = new SoftReference[this.schemaValidatorsCurrentSize];
                System.arraycopy(this.schemaValidators, 0, softReferenceArray, 0, this.schemaValidators.length);
                this.schemaValidators = softReferenceArray;
            }
            if ((softReferenceArray = this.schemaValidators[this.freeSchemaValidatorIndex]) != null && (revalidationHandlerHolder = (RevalidationHandlerHolder)softReferenceArray.get()) != null) {
                revalidationHandlerHolder.handler = revalidationHandler;
                return;
            }
            this.schemaValidators[this.freeSchemaValidatorIndex] = new SoftReference<RevalidationHandlerHolder>(new RevalidationHandlerHolder(revalidationHandler));
        } else if (string == "http://www.w3.org/TR/REC-xml") {
            if ("1.1".equals(string2)) {
                RevalidationHandlerHolder revalidationHandlerHolder;
                SoftReference[] softReferenceArray;
                ++this.freeXML11DTDValidatorIndex;
                if (this.xml11DTDValidators.length == this.freeXML11DTDValidatorIndex) {
                    this.xml11DTDValidatorsCurrentSize += 2;
                    softReferenceArray = new SoftReference[this.xml11DTDValidatorsCurrentSize];
                    System.arraycopy(this.xml11DTDValidators, 0, softReferenceArray, 0, this.xml11DTDValidators.length);
                    this.xml11DTDValidators = softReferenceArray;
                }
                if ((softReferenceArray = this.xml11DTDValidators[this.freeXML11DTDValidatorIndex]) != null && (revalidationHandlerHolder = (RevalidationHandlerHolder)softReferenceArray.get()) != null) {
                    revalidationHandlerHolder.handler = revalidationHandler;
                    return;
                }
                this.xml11DTDValidators[this.freeXML11DTDValidatorIndex] = new SoftReference<RevalidationHandlerHolder>(new RevalidationHandlerHolder(revalidationHandler));
            } else {
                RevalidationHandlerHolder revalidationHandlerHolder;
                SoftReference[] softReferenceArray;
                ++this.freeXML10DTDValidatorIndex;
                if (this.xml10DTDValidators.length == this.freeXML10DTDValidatorIndex) {
                    this.xml10DTDValidatorsCurrentSize += 2;
                    softReferenceArray = new SoftReference[this.xml10DTDValidatorsCurrentSize];
                    System.arraycopy(this.xml10DTDValidators, 0, softReferenceArray, 0, this.xml10DTDValidators.length);
                    this.xml10DTDValidators = softReferenceArray;
                }
                if ((softReferenceArray = this.xml10DTDValidators[this.freeXML10DTDValidatorIndex]) != null && (revalidationHandlerHolder = (RevalidationHandlerHolder)softReferenceArray.get()) != null) {
                    revalidationHandlerHolder.handler = revalidationHandler;
                    return;
                }
                this.xml10DTDValidators[this.freeXML10DTDValidatorIndex] = new SoftReference<RevalidationHandlerHolder>(new RevalidationHandlerHolder(revalidationHandler));
            }
        }
    }

    final synchronized XMLDTDLoader getDTDLoader(String string) {
        if ("1.1".equals(string)) {
            while (this.freeXML11DTDLoaderIndex >= 0) {
                SoftReference softReference = this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex];
                XMLDTDLoaderHolder xMLDTDLoaderHolder = (XMLDTDLoaderHolder)softReference.get();
                if (xMLDTDLoaderHolder != null && xMLDTDLoaderHolder.loader != null) {
                    XMLDTDLoader xMLDTDLoader = xMLDTDLoaderHolder.loader;
                    xMLDTDLoaderHolder.loader = null;
                    --this.freeXML11DTDLoaderIndex;
                    return xMLDTDLoader;
                }
                this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex--] = null;
            }
            return (XMLDTDLoader)ObjectFactory.newInstance("org.apache.xerces.impl.dtd.XML11DTDProcessor", ObjectFactory.findClassLoader(), true);
        }
        while (this.freeXML10DTDLoaderIndex >= 0) {
            SoftReference softReference = this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex];
            XMLDTDLoaderHolder xMLDTDLoaderHolder = (XMLDTDLoaderHolder)softReference.get();
            if (xMLDTDLoaderHolder != null && xMLDTDLoaderHolder.loader != null) {
                XMLDTDLoader xMLDTDLoader = xMLDTDLoaderHolder.loader;
                xMLDTDLoaderHolder.loader = null;
                --this.freeXML10DTDLoaderIndex;
                return xMLDTDLoader;
            }
            this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex--] = null;
        }
        return new XMLDTDLoader();
    }

    final synchronized void releaseDTDLoader(String string, XMLDTDLoader xMLDTDLoader) {
        if ("1.1".equals(string)) {
            XMLDTDLoaderHolder xMLDTDLoaderHolder;
            SoftReference[] softReferenceArray;
            ++this.freeXML11DTDLoaderIndex;
            if (this.xml11DTDLoaders.length == this.freeXML11DTDLoaderIndex) {
                this.xml11DTDLoaderCurrentSize += 2;
                softReferenceArray = new SoftReference[this.xml11DTDLoaderCurrentSize];
                System.arraycopy(this.xml11DTDLoaders, 0, softReferenceArray, 0, this.xml11DTDLoaders.length);
                this.xml11DTDLoaders = softReferenceArray;
            }
            if ((softReferenceArray = this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex]) != null && (xMLDTDLoaderHolder = (XMLDTDLoaderHolder)softReferenceArray.get()) != null) {
                xMLDTDLoaderHolder.loader = xMLDTDLoader;
                return;
            }
            this.xml11DTDLoaders[this.freeXML11DTDLoaderIndex] = new SoftReference<XMLDTDLoaderHolder>(new XMLDTDLoaderHolder(xMLDTDLoader));
        } else {
            XMLDTDLoaderHolder xMLDTDLoaderHolder;
            SoftReference[] softReferenceArray;
            ++this.freeXML10DTDLoaderIndex;
            if (this.xml10DTDLoaders.length == this.freeXML10DTDLoaderIndex) {
                this.xml10DTDLoaderCurrentSize += 2;
                softReferenceArray = new SoftReference[this.xml10DTDLoaderCurrentSize];
                System.arraycopy(this.xml10DTDLoaders, 0, softReferenceArray, 0, this.xml10DTDLoaders.length);
                this.xml10DTDLoaders = softReferenceArray;
            }
            if ((softReferenceArray = this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex]) != null && (xMLDTDLoaderHolder = (XMLDTDLoaderHolder)softReferenceArray.get()) != null) {
                xMLDTDLoaderHolder.loader = xMLDTDLoader;
                return;
            }
            this.xml10DTDLoaders[this.freeXML10DTDLoaderIndex] = new SoftReference<XMLDTDLoaderHolder>(new XMLDTDLoaderHolder(xMLDTDLoader));
        }
    }

    protected synchronized int assignDocumentNumber() {
        return ++this.docAndDoctypeCounter;
    }

    protected synchronized int assignDocTypeNumber() {
        return ++this.docAndDoctypeCounter;
    }

    @Override
    public LSOutput createLSOutput() {
        return new DOMOutputImpl();
    }

    static final class XMLDTDLoaderHolder {
        XMLDTDLoader loader;

        XMLDTDLoaderHolder(XMLDTDLoader xMLDTDLoader) {
            this.loader = xMLDTDLoader;
        }
    }

    static final class RevalidationHandlerHolder {
        RevalidationHandler handler;

        RevalidationHandlerHolder(RevalidationHandler revalidationHandler) {
            this.handler = revalidationHandler;
        }
    }
}

