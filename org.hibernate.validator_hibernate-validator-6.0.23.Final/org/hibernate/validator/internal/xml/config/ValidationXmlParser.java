/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.BootstrapConfiguration
 */
package org.hibernate.validator.internal.xml.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.validation.BootstrapConfiguration;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.internal.xml.CloseIgnoringInputStream;
import org.hibernate.validator.internal.xml.XmlParserHelper;
import org.hibernate.validator.internal.xml.config.BootstrapConfigurationImpl;
import org.hibernate.validator.internal.xml.config.ResourceLoaderHelper;
import org.hibernate.validator.internal.xml.config.ValidationConfigStaxBuilder;
import org.xml.sax.SAXException;

public class ValidationXmlParser {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String VALIDATION_XML_FILE = "META-INF/validation.xml";
    private static final Map<String, String> SCHEMAS_BY_VERSION = Collections.unmodifiableMap(ValidationXmlParser.getSchemasByVersion());
    private final ClassLoader externalClassLoader;

    private static Map<String, String> getSchemasByVersion() {
        HashMap<String, String> schemasByVersion = CollectionHelper.newHashMap(3);
        schemasByVersion.put("1.0", "META-INF/validation-configuration-1.0.xsd");
        schemasByVersion.put("1.1", "META-INF/validation-configuration-1.1.xsd");
        schemasByVersion.put("2.0", "META-INF/validation-configuration-2.0.xsd");
        return schemasByVersion;
    }

    public ValidationXmlParser(ClassLoader externalClassLoader) {
        this.externalClassLoader = externalClassLoader;
    }

    public final BootstrapConfiguration parseValidationXml() {
        InputStream in = this.getValidationXmlInputStream();
        if (in == null) {
            return BootstrapConfigurationImpl.getDefaultBootstrapConfiguration();
        }
        ClassLoader previousTccl = ValidationXmlParser.run(GetClassLoader.fromContext());
        try {
            ValidationXmlParser.run(SetContextClassLoader.action(ValidationXmlParser.class.getClassLoader()));
            XmlParserHelper xmlParserHelper = new XmlParserHelper();
            in.mark(Integer.MAX_VALUE);
            XMLEventReader xmlEventReader = xmlParserHelper.createXmlEventReader(VALIDATION_XML_FILE, new CloseIgnoringInputStream(in));
            String schemaVersion = xmlParserHelper.getSchemaVersion(VALIDATION_XML_FILE, xmlEventReader);
            xmlEventReader.close();
            in.reset();
            Schema schema = this.getSchema(xmlParserHelper, schemaVersion);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new CloseIgnoringInputStream(in)));
            in.reset();
            xmlEventReader = xmlParserHelper.createXmlEventReader(VALIDATION_XML_FILE, new CloseIgnoringInputStream(in));
            ValidationConfigStaxBuilder validationConfigStaxBuilder = new ValidationConfigStaxBuilder(xmlEventReader);
            xmlEventReader.close();
            in.reset();
            BootstrapConfiguration bootstrapConfiguration = validationConfigStaxBuilder.build();
            return bootstrapConfiguration;
        }
        catch (IOException | XMLStreamException | SAXException e) {
            throw LOG.getUnableToParseValidationXmlFileException(VALIDATION_XML_FILE, e);
        }
        finally {
            ValidationXmlParser.run(SetContextClassLoader.action(previousTccl));
            this.closeStream(in);
        }
    }

    private InputStream getValidationXmlInputStream() {
        LOG.debugf("Trying to load %s for XML based Validator configuration.", VALIDATION_XML_FILE);
        InputStream inputStream = ResourceLoaderHelper.getResettableInputStreamForPath(VALIDATION_XML_FILE, this.externalClassLoader);
        if (inputStream != null) {
            return inputStream;
        }
        LOG.debugf("No %s found. Using annotation based configuration only.", VALIDATION_XML_FILE);
        return null;
    }

    private Schema getSchema(XmlParserHelper xmlParserHelper, String schemaVersion) {
        String schemaResource = SCHEMAS_BY_VERSION.get(schemaVersion);
        if (schemaResource == null) {
            throw LOG.getUnsupportedSchemaVersionException(VALIDATION_XML_FILE, schemaVersion);
        }
        Schema schema = xmlParserHelper.getSchema(schemaResource);
        if (schema == null) {
            throw LOG.unableToGetXmlSchema(schemaResource);
        }
        return schema;
    }

    private void closeStream(InputStream inputStream) {
        try {
            inputStream.close();
        }
        catch (IOException io) {
            LOG.unableToCloseXMLFileInputStream(VALIDATION_XML_FILE);
        }
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

