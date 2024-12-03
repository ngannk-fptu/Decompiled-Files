/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.CharConversionException;
import java.io.IOException;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.parsers.AbstractDOMParser;
import org.apache.xerces.parsers.ObjectFactory;
import org.apache.xerces.util.EntityResolver2Wrapper;
import org.apache.xerces.util.EntityResolverWrapper;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.LocatorImpl;

public class DOMParser
extends AbstractDOMParser {
    protected static final String USE_ENTITY_RESOLVER2 = "http://xml.org/sax/features/use-entity-resolver2";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool"};
    protected boolean fUseEntityResolver2 = true;

    public DOMParser(XMLParserConfiguration xMLParserConfiguration) {
        super(xMLParserConfiguration);
    }

    public DOMParser() {
        this(null, null);
    }

    public DOMParser(SymbolTable symbolTable) {
        this(symbolTable, null);
    }

    public DOMParser(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
        }
        if (xMLGrammarPool != null) {
            this.fConfiguration.setProperty(XMLGRAMMAR_POOL, xMLGrammarPool);
        }
    }

    public void parse(String string) throws SAXException, IOException {
        XMLInputSource xMLInputSource = new XMLInputSource(null, string, null);
        try {
            this.parse(xMLInputSource);
        }
        catch (XMLParseException xMLParseException) {
            Exception exception = xMLParseException.getException();
            if (exception == null || exception instanceof CharConversionException) {
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(xMLParseException.getPublicId());
                locatorImpl.setSystemId(xMLParseException.getExpandedSystemId());
                locatorImpl.setLineNumber(xMLParseException.getLineNumber());
                locatorImpl.setColumnNumber(xMLParseException.getColumnNumber());
                throw exception == null ? new SAXParseException(xMLParseException.getMessage(), locatorImpl) : new SAXParseException(xMLParseException.getMessage(), locatorImpl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (XNIException xNIException) {
            xNIException.printStackTrace();
            Exception exception = xNIException.getException();
            if (exception == null) {
                throw new SAXException(xNIException.getMessage());
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
    }

    public void parse(InputSource inputSource) throws SAXException, IOException {
        try {
            XMLInputSource xMLInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
            xMLInputSource.setByteStream(inputSource.getByteStream());
            xMLInputSource.setCharacterStream(inputSource.getCharacterStream());
            xMLInputSource.setEncoding(inputSource.getEncoding());
            this.parse(xMLInputSource);
        }
        catch (XMLParseException xMLParseException) {
            Exception exception = xMLParseException.getException();
            if (exception == null || exception instanceof CharConversionException) {
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(xMLParseException.getPublicId());
                locatorImpl.setSystemId(xMLParseException.getExpandedSystemId());
                locatorImpl.setLineNumber(xMLParseException.getLineNumber());
                locatorImpl.setColumnNumber(xMLParseException.getColumnNumber());
                throw exception == null ? new SAXParseException(xMLParseException.getMessage(), locatorImpl) : new SAXParseException(xMLParseException.getMessage(), locatorImpl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (XNIException xNIException) {
            Exception exception = xNIException.getException();
            if (exception == null) {
                throw new SAXException(xNIException.getMessage());
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        try {
            XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (this.fUseEntityResolver2 && entityResolver instanceof EntityResolver2) {
                if (xMLEntityResolver instanceof EntityResolver2Wrapper) {
                    EntityResolver2Wrapper entityResolver2Wrapper = (EntityResolver2Wrapper)xMLEntityResolver;
                    entityResolver2Wrapper.setEntityResolver((EntityResolver2)entityResolver);
                } else {
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)entityResolver));
                }
            } else if (xMLEntityResolver instanceof EntityResolverWrapper) {
                EntityResolverWrapper entityResolverWrapper = (EntityResolverWrapper)xMLEntityResolver;
                entityResolverWrapper.setEntityResolver(entityResolver);
            } else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(entityResolver));
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    public EntityResolver getEntityResolver() {
        EntityResolver entityResolver = null;
        try {
            XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (xMLEntityResolver != null) {
                if (xMLEntityResolver instanceof EntityResolverWrapper) {
                    entityResolver = ((EntityResolverWrapper)xMLEntityResolver).getEntityResolver();
                } else if (xMLEntityResolver instanceof EntityResolver2Wrapper) {
                    entityResolver = ((EntityResolver2Wrapper)xMLEntityResolver).getEntityResolver();
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        return entityResolver;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        try {
            XMLErrorHandler xMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xMLErrorHandler instanceof ErrorHandlerWrapper) {
                ErrorHandlerWrapper errorHandlerWrapper = (ErrorHandlerWrapper)xMLErrorHandler;
                errorHandlerWrapper.setErrorHandler(errorHandler);
            } else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            XMLErrorHandler xMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xMLErrorHandler != null && xMLErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xMLErrorHandler).getErrorHandler();
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        return errorHandler;
    }

    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (string.equals(USE_ENTITY_RESOLVER2)) {
                if (bl != this.fUseEntityResolver2) {
                    this.fUseEntityResolver2 = bl;
                    this.setEntityResolver(this.getEntityResolver());
                }
                return;
            }
            this.fConfiguration.setFeature(string, bl);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (string.equals(USE_ENTITY_RESOLVER2)) {
                return this.fUseEntityResolver2;
            }
            return this.fConfiguration.getFeature(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fConfiguration.setProperty(string, object);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string.equals("http://apache.org/xml/properties/dom/current-element-node")) {
            boolean bl = false;
            try {
                bl = this.getFeature("http://apache.org/xml/features/dom/defer-node-expansion");
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                // empty catch block
            }
            if (bl) {
                throw new SAXNotSupportedException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotQueryDeferredNode", null));
            }
            return this.fCurrentNode != null && this.fCurrentNode.getNodeType() == 1 ? this.fCurrentNode : null;
        }
        try {
            return this.fConfiguration.getProperty(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    public XMLParserConfiguration getXMLParserConfiguration() {
        return this.fConfiguration;
    }
}

