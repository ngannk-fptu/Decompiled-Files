/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.jaxp.validation.JAXPValidationMessageFormatter;
import org.apache.xerces.jaxp.validation.Util;
import org.apache.xerces.jaxp.validation.ValidatorHelper;
import org.apache.xerces.jaxp.validation.XMLSchemaValidatorComponentManager;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.xml.sax.SAXException;

final class StreamValidatorHelper
implements ValidatorHelper {
    private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private SoftReference fConfiguration = new SoftReference<Object>(null);
    private final XMLSchemaValidator fSchemaValidator;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private SoftReference fParser = new SoftReference<Object>(null);
    private SerializerFactory fSerializerFactory;

    public StreamValidatorHelper(XMLSchemaValidatorComponentManager xMLSchemaValidatorComponentManager) {
        this.fComponentManager = xMLSchemaValidatorComponentManager;
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty(SCHEMA_VALIDATOR);
    }

    @Override
    public void validate(Source source, Result result) throws SAXException, IOException {
        if (result instanceof StreamResult || result == null) {
            StreamSource streamSource = (StreamSource)source;
            StreamResult streamResult = (StreamResult)result;
            XMLInputSource xMLInputSource = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), null);
            xMLInputSource.setByteStream(streamSource.getInputStream());
            xMLInputSource.setCharacterStream(streamSource.getReader());
            boolean bl = false;
            XMLParserConfiguration xMLParserConfiguration = (XMLParserConfiguration)this.fConfiguration.get();
            if (xMLParserConfiguration == null) {
                xMLParserConfiguration = this.initialize();
                bl = true;
            } else if (this.fComponentManager.getFeature(PARSER_SETTINGS)) {
                xMLParserConfiguration.setProperty(ENTITY_RESOLVER, this.fComponentManager.getProperty(ENTITY_RESOLVER));
                xMLParserConfiguration.setProperty(ERROR_HANDLER, this.fComponentManager.getProperty(ERROR_HANDLER));
                xMLParserConfiguration.setProperty(SECURITY_MANAGER, this.fComponentManager.getProperty(SECURITY_MANAGER));
            }
            this.fComponentManager.reset();
            if (streamResult != null) {
                Object object;
                Serializer serializer;
                if (this.fSerializerFactory == null) {
                    this.fSerializerFactory = SerializerFactory.getSerializerFactory("xml");
                }
                if (streamResult.getWriter() != null) {
                    serializer = this.fSerializerFactory.makeSerializer(streamResult.getWriter(), new OutputFormat());
                } else if (streamResult.getOutputStream() != null) {
                    serializer = this.fSerializerFactory.makeSerializer(streamResult.getOutputStream(), new OutputFormat());
                } else if (streamResult.getSystemId() != null) {
                    object = streamResult.getSystemId();
                    OutputStream outputStream = XMLEntityManager.createOutputStream((String)object);
                    serializer = this.fSerializerFactory.makeSerializer(outputStream, new OutputFormat());
                } else {
                    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "StreamResultNotInitialized", null));
                }
                object = (SAXParser)this.fParser.get();
                if (bl || object == null) {
                    object = new SAXParser(xMLParserConfiguration);
                    this.fParser = new SoftReference<Object>(object);
                } else {
                    ((AbstractSAXParser)object).reset();
                }
                xMLParserConfiguration.setDocumentHandler(this.fSchemaValidator);
                this.fSchemaValidator.setDocumentHandler((XMLDocumentHandler)object);
                ((AbstractSAXParser)object).setContentHandler(serializer.asContentHandler());
            } else {
                this.fSchemaValidator.setDocumentHandler(null);
            }
            try {
                xMLParserConfiguration.parse(xMLInputSource);
            }
            catch (XMLParseException xMLParseException) {
                throw Util.toSAXParseException(xMLParseException);
            }
            catch (XNIException xNIException) {
                throw Util.toSAXException(xNIException);
            }
            finally {
                this.fSchemaValidator.setDocumentHandler(null);
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
    }

    private XMLParserConfiguration initialize() {
        XML11Configuration xML11Configuration = new XML11Configuration();
        xML11Configuration.setProperty(ENTITY_RESOLVER, this.fComponentManager.getProperty(ENTITY_RESOLVER));
        xML11Configuration.setProperty(ERROR_HANDLER, this.fComponentManager.getProperty(ERROR_HANDLER));
        XMLErrorReporter xMLErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty(ERROR_REPORTER);
        xML11Configuration.setProperty(ERROR_REPORTER, xMLErrorReporter);
        if (xMLErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
            xMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
            xMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
        }
        xML11Configuration.setProperty(SYMBOL_TABLE, this.fComponentManager.getProperty(SYMBOL_TABLE));
        xML11Configuration.setProperty(VALIDATION_MANAGER, this.fComponentManager.getProperty(VALIDATION_MANAGER));
        xML11Configuration.setProperty(SECURITY_MANAGER, this.fComponentManager.getProperty(SECURITY_MANAGER));
        xML11Configuration.setDocumentHandler(this.fSchemaValidator);
        xML11Configuration.setDTDHandler(null);
        xML11Configuration.setDTDContentModelHandler(null);
        this.fConfiguration = new SoftReference<XML11Configuration>(xML11Configuration);
        return xML11Configuration;
    }
}

