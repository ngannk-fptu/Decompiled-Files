/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.PersistenceUnitTransactionType
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.jdbc.datasource.lookup.DataSourceLookup
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ResourceUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 *  org.springframework.util.xml.SimpleSaxErrorHandler
 */
package org.springframework.orm.jpa.persistenceunit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.persistenceunit.SpringPersistenceUnitInfo;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

final class PersistenceUnitReader {
    private static final String PERSISTENCE_VERSION = "version";
    private static final String PERSISTENCE_UNIT = "persistence-unit";
    private static final String UNIT_NAME = "name";
    private static final String MAPPING_FILE_NAME = "mapping-file";
    private static final String JAR_FILE_URL = "jar-file";
    private static final String MANAGED_CLASS_NAME = "class";
    private static final String PROPERTIES = "properties";
    private static final String PROVIDER = "provider";
    private static final String TRANSACTION_TYPE = "transaction-type";
    private static final String JTA_DATA_SOURCE = "jta-data-source";
    private static final String NON_JTA_DATA_SOURCE = "non-jta-data-source";
    private static final String EXCLUDE_UNLISTED_CLASSES = "exclude-unlisted-classes";
    private static final String SHARED_CACHE_MODE = "shared-cache-mode";
    private static final String VALIDATION_MODE = "validation-mode";
    private static final String META_INF = "META-INF";
    private static final Log logger = LogFactory.getLog(PersistenceUnitReader.class);
    private final ResourcePatternResolver resourcePatternResolver;
    private final DataSourceLookup dataSourceLookup;

    public PersistenceUnitReader(ResourcePatternResolver resourcePatternResolver, DataSourceLookup dataSourceLookup) {
        Assert.notNull((Object)resourcePatternResolver, (String)"ResourceLoader must not be null");
        Assert.notNull((Object)dataSourceLookup, (String)"DataSourceLookup must not be null");
        this.resourcePatternResolver = resourcePatternResolver;
        this.dataSourceLookup = dataSourceLookup;
    }

    public SpringPersistenceUnitInfo[] readPersistenceUnitInfos(String persistenceXmlLocation) {
        return this.readPersistenceUnitInfos(new String[]{persistenceXmlLocation});
    }

    public SpringPersistenceUnitInfo[] readPersistenceUnitInfos(String[] persistenceXmlLocations) {
        SimpleSaxErrorHandler handler = new SimpleSaxErrorHandler(logger);
        ArrayList<SpringPersistenceUnitInfo> infos = new ArrayList<SpringPersistenceUnitInfo>(1);
        String resourceLocation = null;
        try {
            for (String location : persistenceXmlLocations) {
                Resource[] resources;
                for (Resource resource : resources = this.resourcePatternResolver.getResources(location)) {
                    resourceLocation = resource.toString();
                    try (InputStream stream = resource.getInputStream();){
                        Document document = this.buildDocument((ErrorHandler)handler, stream);
                        this.parseDocument(resource, document, infos);
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Cannot parse persistence unit from " + resourceLocation, ex);
        }
        catch (SAXException ex) {
            throw new IllegalArgumentException("Invalid XML in persistence unit from " + resourceLocation, ex);
        }
        catch (ParserConfigurationException ex) {
            throw new IllegalArgumentException("Internal error parsing persistence unit from " + resourceLocation);
        }
        return infos.toArray(new SpringPersistenceUnitInfo[0]);
    }

    protected Document buildDocument(ErrorHandler handler, InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder parser = dbf.newDocumentBuilder();
        parser.setErrorHandler(handler);
        return parser.parse(stream);
    }

    protected List<SpringPersistenceUnitInfo> parseDocument(Resource resource, Document document, List<SpringPersistenceUnitInfo> infos) throws IOException {
        Element persistence = document.getDocumentElement();
        String version = persistence.getAttribute(PERSISTENCE_VERSION);
        URL rootUrl = PersistenceUnitReader.determinePersistenceUnitRootUrl(resource);
        List units = DomUtils.getChildElementsByTagName((Element)persistence, (String)PERSISTENCE_UNIT);
        for (Element unit : units) {
            infos.add(this.parsePersistenceUnitInfo(unit, version, rootUrl));
        }
        return infos;
    }

    protected SpringPersistenceUnitInfo parsePersistenceUnitInfo(Element persistenceUnit, String version, @Nullable URL rootUrl) throws IOException {
        String validationMode;
        String cacheMode;
        Element excludeUnlistedClasses;
        String provider;
        String nonJtaDataSource;
        String jtaDataSource;
        SpringPersistenceUnitInfo unitInfo = new SpringPersistenceUnitInfo();
        unitInfo.setPersistenceXMLSchemaVersion(version);
        unitInfo.setPersistenceUnitRootUrl(rootUrl);
        unitInfo.setPersistenceUnitName(persistenceUnit.getAttribute(UNIT_NAME).trim());
        String txType = persistenceUnit.getAttribute(TRANSACTION_TYPE).trim();
        if (StringUtils.hasText((String)txType)) {
            unitInfo.setTransactionType(PersistenceUnitTransactionType.valueOf((String)txType));
        }
        if (StringUtils.hasText((String)(jtaDataSource = DomUtils.getChildElementValueByTagName((Element)persistenceUnit, (String)JTA_DATA_SOURCE)))) {
            unitInfo.setJtaDataSource(this.dataSourceLookup.getDataSource(jtaDataSource.trim()));
        }
        if (StringUtils.hasText((String)(nonJtaDataSource = DomUtils.getChildElementValueByTagName((Element)persistenceUnit, (String)NON_JTA_DATA_SOURCE)))) {
            unitInfo.setNonJtaDataSource(this.dataSourceLookup.getDataSource(nonJtaDataSource.trim()));
        }
        if (StringUtils.hasText((String)(provider = DomUtils.getChildElementValueByTagName((Element)persistenceUnit, (String)PROVIDER)))) {
            unitInfo.setPersistenceProviderClassName(provider.trim());
        }
        if ((excludeUnlistedClasses = DomUtils.getChildElementByTagName((Element)persistenceUnit, (String)EXCLUDE_UNLISTED_CLASSES)) != null) {
            String excludeText = DomUtils.getTextValue((Element)excludeUnlistedClasses);
            unitInfo.setExcludeUnlistedClasses(!StringUtils.hasText((String)excludeText) || Boolean.parseBoolean(excludeText));
        }
        if (StringUtils.hasText((String)(cacheMode = DomUtils.getChildElementValueByTagName((Element)persistenceUnit, (String)SHARED_CACHE_MODE)))) {
            unitInfo.setSharedCacheMode(SharedCacheMode.valueOf((String)cacheMode));
        }
        if (StringUtils.hasText((String)(validationMode = DomUtils.getChildElementValueByTagName((Element)persistenceUnit, (String)VALIDATION_MODE)))) {
            unitInfo.setValidationMode(ValidationMode.valueOf((String)validationMode));
        }
        this.parseProperties(persistenceUnit, unitInfo);
        this.parseManagedClasses(persistenceUnit, unitInfo);
        this.parseMappingFiles(persistenceUnit, unitInfo);
        this.parseJarFiles(persistenceUnit, unitInfo);
        return unitInfo;
    }

    protected void parseProperties(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) {
        Element propRoot = DomUtils.getChildElementByTagName((Element)persistenceUnit, (String)PROPERTIES);
        if (propRoot == null) {
            return;
        }
        List properties = DomUtils.getChildElementsByTagName((Element)propRoot, (String)"property");
        for (Element property : properties) {
            String name = property.getAttribute(UNIT_NAME);
            String value = property.getAttribute("value");
            unitInfo.addProperty(name, value);
        }
    }

    protected void parseManagedClasses(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) {
        List classes = DomUtils.getChildElementsByTagName((Element)persistenceUnit, (String)MANAGED_CLASS_NAME);
        for (Element element : classes) {
            String value = DomUtils.getTextValue((Element)element).trim();
            if (!StringUtils.hasText((String)value)) continue;
            unitInfo.addManagedClassName(value);
        }
    }

    protected void parseMappingFiles(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) {
        List files = DomUtils.getChildElementsByTagName((Element)persistenceUnit, (String)MAPPING_FILE_NAME);
        for (Element element : files) {
            String value = DomUtils.getTextValue((Element)element).trim();
            if (!StringUtils.hasText((String)value)) continue;
            unitInfo.addMappingFileName(value);
        }
    }

    protected void parseJarFiles(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) throws IOException {
        List jars = DomUtils.getChildElementsByTagName((Element)persistenceUnit, (String)JAR_FILE_URL);
        for (Element element : jars) {
            String value = DomUtils.getTextValue((Element)element).trim();
            if (!StringUtils.hasText((String)value)) continue;
            Resource[] resources = this.resourcePatternResolver.getResources(value);
            boolean found = false;
            for (Resource resource : resources) {
                if (!resource.exists()) continue;
                found = true;
                unitInfo.addJarFileUrl(resource.getURL());
            }
            if (found) continue;
            URL rootUrl = unitInfo.getPersistenceUnitRootUrl();
            if (rootUrl != null) {
                unitInfo.addJarFileUrl(new URL(rootUrl, value));
                continue;
            }
            logger.warn((Object)("Cannot resolve jar-file entry [" + value + "] in persistence unit '" + unitInfo.getPersistenceUnitName() + "' without root URL"));
        }
    }

    @Nullable
    static URL determinePersistenceUnitRootUrl(Resource resource) throws IOException {
        URL originalURL = resource.getURL();
        if (ResourceUtils.isJarURL((URL)originalURL)) {
            return ResourceUtils.extractJarFileURL((URL)originalURL);
        }
        String urlToString = originalURL.toExternalForm();
        if (!urlToString.contains(META_INF)) {
            if (logger.isInfoEnabled()) {
                logger.info((Object)(resource.getFilename() + " should be located inside META-INF directory; cannot determine persistence unit root URL for " + resource));
            }
            return null;
        }
        if (urlToString.lastIndexOf(META_INF) == urlToString.lastIndexOf(47) - (1 + META_INF.length())) {
            if (logger.isInfoEnabled()) {
                logger.info((Object)(resource.getFilename() + " is not located in the root of META-INF directory; cannot determine persistence unit root URL for " + resource));
            }
            return null;
        }
        String persistenceUnitRoot = urlToString.substring(0, urlToString.lastIndexOf(META_INF));
        if (persistenceUnitRoot.endsWith("/")) {
            persistenceUnitRoot = persistenceUnitRoot.substring(0, persistenceUnitRoot.length() - 1);
        }
        return new URL(persistenceUnitRoot);
    }
}

