/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.boot.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;
import org.hibernate.boot.archive.internal.ArchiveHelper;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.xsd.ConfigXsdSupport;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.internal.util.ConfigurationHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PersistenceXmlParser {
    private static final EntityManagerMessageLogger LOG = HEMLogging.messageLogger(PersistenceXmlParser.class);
    private final ClassLoaderService classLoaderService;
    private final PersistenceUnitTransactionType defaultTransactionType;
    private final Map<String, ParsedPersistenceXmlDescriptor> persistenceUnits;
    private DocumentBuilderFactory documentBuilderFactory;

    public static List<ParsedPersistenceXmlDescriptor> locatePersistenceUnits(Map integration) {
        PersistenceXmlParser parser = new PersistenceXmlParser(ClassLoaderServiceImpl.fromConfigSettings(integration), PersistenceUnitTransactionType.RESOURCE_LOCAL);
        parser.doResolve(integration);
        return new ArrayList<ParsedPersistenceXmlDescriptor>(parser.persistenceUnits.values());
    }

    public static ParsedPersistenceXmlDescriptor locateIndividualPersistenceUnit(URL persistenceXmlUrl) {
        return PersistenceXmlParser.locateIndividualPersistenceUnit(persistenceXmlUrl, Collections.emptyMap());
    }

    public static ParsedPersistenceXmlDescriptor locateIndividualPersistenceUnit(URL persistenceXmlUrl, Map integration) {
        return PersistenceXmlParser.locateIndividualPersistenceUnit(persistenceXmlUrl, PersistenceUnitTransactionType.RESOURCE_LOCAL, integration);
    }

    public static ParsedPersistenceXmlDescriptor locateIndividualPersistenceUnit(URL persistenceXmlUrl, PersistenceUnitTransactionType transactionType, Map integration) {
        PersistenceXmlParser parser = new PersistenceXmlParser(ClassLoaderServiceImpl.fromConfigSettings(integration), transactionType);
        parser.parsePersistenceXml(persistenceXmlUrl, integration);
        assert (parser.persistenceUnits.size() == 1);
        return parser.persistenceUnits.values().iterator().next();
    }

    public static ParsedPersistenceXmlDescriptor locateNamedPersistenceUnit(URL persistenceXmlUrl, String name) {
        return PersistenceXmlParser.locateNamedPersistenceUnit(persistenceXmlUrl, name, Collections.emptyMap());
    }

    public static ParsedPersistenceXmlDescriptor locateNamedPersistenceUnit(URL persistenceXmlUrl, String name, Map integration) {
        return PersistenceXmlParser.locateNamedPersistenceUnit(persistenceXmlUrl, name, PersistenceUnitTransactionType.RESOURCE_LOCAL, integration);
    }

    public static ParsedPersistenceXmlDescriptor locateNamedPersistenceUnit(URL persistenceXmlUrl, String name, PersistenceUnitTransactionType transactionType, Map integration) {
        assert (StringHelper.isNotEmpty(name));
        PersistenceXmlParser parser = new PersistenceXmlParser(ClassLoaderServiceImpl.fromConfigSettings(integration), transactionType);
        parser.parsePersistenceXml(persistenceXmlUrl, integration);
        assert (parser.persistenceUnits.containsKey(name));
        return parser.persistenceUnits.get(name);
    }

    public static Map<String, ParsedPersistenceXmlDescriptor> parse(URL persistenceXmlUrl, PersistenceUnitTransactionType transactionType) {
        return PersistenceXmlParser.parse(persistenceXmlUrl, transactionType, Collections.emptyMap());
    }

    public static Map<String, ParsedPersistenceXmlDescriptor> parse(URL persistenceXmlUrl, PersistenceUnitTransactionType transactionType, Map integration) {
        PersistenceXmlParser parser = new PersistenceXmlParser(ClassLoaderServiceImpl.fromConfigSettings(integration), transactionType);
        parser.doResolve(integration);
        return parser.persistenceUnits;
    }

    protected PersistenceXmlParser(ClassLoaderService classLoaderService, PersistenceUnitTransactionType defaultTransactionType) {
        this.classLoaderService = classLoaderService;
        this.defaultTransactionType = defaultTransactionType;
        this.persistenceUnits = new ConcurrentHashMap<String, ParsedPersistenceXmlDescriptor>();
    }

    protected List<ParsedPersistenceXmlDescriptor> getResolvedPersistenceUnits() {
        return new ArrayList<ParsedPersistenceXmlDescriptor>(this.persistenceUnits.values());
    }

    private void doResolve(Map integration) {
        List<URL> xmlUrls = this.classLoaderService.locateResources("META-INF/persistence.xml");
        if (xmlUrls.isEmpty()) {
            LOG.unableToFindPersistenceXmlInClasspath();
        } else {
            this.parsePersistenceXml(xmlUrls, integration);
        }
    }

    private void parsePersistenceXml(List<URL> xmlUrls, Map integration) {
        for (URL xmlUrl : xmlUrls) {
            this.parsePersistenceXml(xmlUrl, integration);
        }
    }

    protected void parsePersistenceXml(URL xmlUrl, Map integration) {
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Attempting to parse persistence.xml file : %s", xmlUrl.toExternalForm());
        }
        Document doc = this.loadUrl(xmlUrl);
        Element top = doc.getDocumentElement();
        NodeList children = top.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            String transactionType;
            Element element;
            String tag;
            if (children.item(i).getNodeType() != 1 || !(tag = (element = (Element)children.item(i)).getTagName()).equals("persistence-unit")) continue;
            URL puRootUrl = ArchiveHelper.getJarURLFromURLEntry(xmlUrl, "/META-INF/persistence.xml");
            ParsedPersistenceXmlDescriptor persistenceUnit = new ParsedPersistenceXmlDescriptor(puRootUrl);
            this.bindPersistenceUnit(persistenceUnit, element);
            if (this.persistenceUnits.containsKey(persistenceUnit.getName())) {
                LOG.duplicatedPersistenceUnitName(persistenceUnit.getName());
                continue;
            }
            if (integration.containsKey("javax.persistence.provider")) {
                persistenceUnit.setProviderClassName((String)integration.get("javax.persistence.provider"));
            } else if (integration.containsKey("jakarta.persistence.provider")) {
                persistenceUnit.setProviderClassName((String)integration.get("jakarta.persistence.provider"));
            }
            if (integration.containsKey("javax.persistence.transactionType")) {
                transactionType = (String)integration.get("javax.persistence.transactionType");
                persistenceUnit.setTransactionType(PersistenceXmlParser.parseTransactionType(transactionType));
            } else if (integration.containsKey("jakarta.persistence.transactionType")) {
                transactionType = (String)integration.get("jakarta.persistence.transactionType");
                persistenceUnit.setTransactionType(PersistenceXmlParser.parseTransactionType(transactionType));
            }
            if (integration.containsKey("javax.persistence.jtaDataSource")) {
                persistenceUnit.setJtaDataSource(integration.get("javax.persistence.jtaDataSource"));
            } else if (integration.containsKey("jakarta.persistence.jtaDataSource")) {
                persistenceUnit.setJtaDataSource(integration.get("jakarta.persistence.jtaDataSource"));
            }
            if (integration.containsKey("javax.persistence.nonJtaDataSource")) {
                persistenceUnit.setNonJtaDataSource(integration.get("javax.persistence.nonJtaDataSource"));
            } else if (integration.containsKey("jakarta.persistence.nonJtaDataSource")) {
                persistenceUnit.setNonJtaDataSource(integration.get("jakarta.persistence.nonJtaDataSource"));
            }
            this.decodeTransactionType(persistenceUnit);
            Properties properties = persistenceUnit.getProperties();
            ConfigurationHelper.overrideProperties(properties, integration);
            this.persistenceUnits.put(persistenceUnit.getName(), persistenceUnit);
        }
    }

    private void decodeTransactionType(ParsedPersistenceXmlDescriptor persistenceUnit) {
        if (persistenceUnit.getTransactionType() != null) {
            return;
        }
        if (persistenceUnit.getJtaDataSource() != null) {
            persistenceUnit.setTransactionType(PersistenceUnitTransactionType.JTA);
        } else if (persistenceUnit.getNonJtaDataSource() != null) {
            persistenceUnit.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        } else {
            persistenceUnit.setTransactionType(this.defaultTransactionType);
        }
    }

    private void bindPersistenceUnit(ParsedPersistenceXmlDescriptor persistenceUnit, Element persistenceUnitElement) {
        PersistenceUnitTransactionType transactionType;
        String name = persistenceUnitElement.getAttribute("name");
        if (StringHelper.isNotEmpty(name)) {
            LOG.tracef("Persistence unit name from persistence.xml : %s", name);
            persistenceUnit.setName(name);
        }
        if ((transactionType = PersistenceXmlParser.parseTransactionType(persistenceUnitElement.getAttribute("transaction-type"))) != null) {
            persistenceUnit.setTransactionType(transactionType);
        }
        NodeList children = persistenceUnitElement.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i).getNodeType() != 1) continue;
            Element element = (Element)children.item(i);
            String tag = element.getTagName();
            if (tag.equals("non-jta-data-source")) {
                persistenceUnit.setNonJtaDataSource(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (tag.equals("jta-data-source")) {
                persistenceUnit.setJtaDataSource(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (tag.equals("provider")) {
                persistenceUnit.setProviderClassName(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (tag.equals("class")) {
                persistenceUnit.addClasses(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (tag.equals("mapping-file")) {
                persistenceUnit.addMappingFiles(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (tag.equals("jar-file")) {
                persistenceUnit.addJarFileUrl(ArchiveHelper.getURLFromPath(PersistenceXmlParser.extractContent(element)));
                continue;
            }
            if (tag.equals("exclude-unlisted-classes")) {
                persistenceUnit.setExcludeUnlistedClasses(PersistenceXmlParser.extractBooleanContent(element, true));
                continue;
            }
            if (tag.equals("delimited-identifiers")) {
                persistenceUnit.setUseQuotedIdentifiers(true);
                continue;
            }
            if (tag.equals("validation-mode")) {
                persistenceUnit.setValidationMode(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (tag.equals("shared-cache-mode")) {
                persistenceUnit.setSharedCacheMode(PersistenceXmlParser.extractContent(element));
                continue;
            }
            if (!tag.equals("properties")) continue;
            NodeList props = element.getChildNodes();
            for (int j = 0; j < props.getLength(); ++j) {
                Element propElement;
                if (props.item(j).getNodeType() != 1 || !"property".equals((propElement = (Element)props.item(j)).getTagName())) continue;
                String propName = propElement.getAttribute("name").trim();
                String propValue = propElement.getAttribute("value").trim();
                if (propValue.isEmpty()) {
                    propValue = PersistenceXmlParser.extractContent(propElement, "");
                }
                persistenceUnit.getProperties().put(propName, propValue);
            }
        }
    }

    private static String extractContent(Element element) {
        return PersistenceXmlParser.extractContent(element, null);
    }

    private static String extractContent(Element element, String defaultStr) {
        if (element == null) {
            return defaultStr;
        }
        NodeList children = element.getChildNodes();
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i).getNodeType() != 3 && children.item(i).getNodeType() != 4) continue;
            result.append(children.item(i).getNodeValue());
        }
        return result.toString().trim();
    }

    private static boolean extractBooleanContent(Element element, boolean defaultBool) {
        String content = PersistenceXmlParser.extractContent(element);
        if (content != null && content.length() > 0) {
            return Boolean.valueOf(content);
        }
        return defaultBool;
    }

    private static PersistenceUnitTransactionType parseTransactionType(String value) {
        if (StringHelper.isEmpty(value)) {
            return null;
        }
        if (value.equalsIgnoreCase("JTA")) {
            return PersistenceUnitTransactionType.JTA;
        }
        if (value.equalsIgnoreCase("RESOURCE_LOCAL")) {
            return PersistenceUnitTransactionType.RESOURCE_LOCAL;
        }
        throw new PersistenceException("Unknown persistence unit transaction type : " + value);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private Document loadUrl(URL xmlUrl) {
        String resourceName = xmlUrl.toExternalForm();
        try {
            URLConnection conn = xmlUrl.openConnection();
            conn.setUseCaches(false);
            try (InputStream inputStream = conn.getInputStream();){
                InputSource inputSource = new InputSource(inputStream);
                DocumentBuilder documentBuilder = this.documentBuilderFactory().newDocumentBuilder();
                try {
                    Document document = documentBuilder.parse(inputSource);
                    this.validate(document);
                    Document document2 = document;
                    return document2;
                }
                catch (IOException | SAXException e) {
                    try {
                        throw new PersistenceException("Unexpected error parsing [" + resourceName + "]", (Throwable)e);
                    }
                    catch (ParserConfigurationException e2) {
                        throw new PersistenceException("Unable to generate javax.xml.parsers.DocumentBuilder instance", (Throwable)e2);
                    }
                }
            }
            catch (IOException e) {
                throw new PersistenceException("Unable to obtain input stream from [" + resourceName + "]", (Throwable)e);
            }
        }
        catch (IOException e) {
            throw new PersistenceException("Unable to access [" + resourceName + "]", (Throwable)e);
        }
    }

    private void validate(Document document) {
        String version = document.getDocumentElement().getAttribute("version");
        Validator validator = new ConfigXsdSupport().jpaXsd(version).getSchema().newValidator();
        ArrayList<SAXException> errors = new ArrayList<SAXException>();
        validator.setErrorHandler(new ErrorHandlerImpl(errors));
        try {
            validator.validate(new DOMSource(document));
        }
        catch (SAXException e) {
            errors.add(e);
        }
        catch (IOException e) {
            throw new PersistenceException("Unable to validate persistence.xml", (Throwable)e);
        }
        if (errors.size() != 0) {
            StringBuilder errorMessage = new StringBuilder();
            for (SAXException error : errors) {
                errorMessage.append(PersistenceXmlParser.extractInfo(error)).append('\n');
            }
            throw new PersistenceException("Invalid persistence.xml.\n" + errorMessage.toString());
        }
    }

    private DocumentBuilderFactory documentBuilderFactory() {
        if (this.documentBuilderFactory == null) {
            this.documentBuilderFactory = this.buildDocumentBuilderFactory();
        }
        return this.documentBuilderFactory;
    }

    private DocumentBuilderFactory buildDocumentBuilderFactory() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory;
    }

    private static String extractInfo(SAXException error) {
        if (error instanceof SAXParseException) {
            return "Error parsing XML [line : " + ((SAXParseException)error).getLineNumber() + ", column : " + ((SAXParseException)error).getColumnNumber() + "] : " + error.getMessage();
        }
        return "Error parsing XML : " + error.getMessage();
    }

    public static class ErrorHandlerImpl
    implements ErrorHandler {
        private List<SAXException> errors;

        ErrorHandlerImpl(List<SAXException> errors) {
            this.errors = errors;
        }

        @Override
        public void error(SAXParseException error) {
            this.errors.add(error);
        }

        @Override
        public void fatalError(SAXParseException error) {
            this.errors.add(error);
        }

        @Override
        public void warning(SAXParseException warn) {
            LOG.trace(PersistenceXmlParser.extractInfo(warn));
        }
    }
}

