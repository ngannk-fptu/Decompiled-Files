/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.user.configuration.xml;

import com.atlassian.user.configuration.CacheConfiguration;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DefaultCacheConfiguration;
import com.atlassian.user.configuration.DefaultRepositoryConfiguration;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.configuration.RepositoryProcessor;
import com.atlassian.user.configuration.xml.XMLConfigUtil;
import com.atlassian.user.configuration.xml.XMLDefaultsParser;
import com.atlassian.user.repository.DefaultRepositoryIdentifier;
import com.atlassian.user.repository.RepositoryIdentifier;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XMLConfigurationParser {
    private static final Logger log = Logger.getLogger(XMLConfigurationParser.class);
    private final XMLDefaultsParser defaultsParser;
    private List<RepositoryIdentifier> repositoryIdentifiers = new ArrayList<RepositoryIdentifier>();
    private Map<RepositoryIdentifier, RepositoryConfiguration> repositoryConfigurations = new HashMap<RepositoryIdentifier, RepositoryConfiguration>();

    public XMLConfigurationParser() throws ConfigurationException {
        this("atlassian-user-defaults.xml");
    }

    public XMLConfigurationParser(String defaultsFileName) throws ConfigurationException {
        try {
            this.defaultsParser = new XMLDefaultsParser(defaultsFileName);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to load atlassian-user configuration parser: " + e.getMessage(), e);
        }
    }

    public void parse(InputStream docIS) throws ConfigurationException {
        try {
            Document doc;
            if (docIS == null) {
                throw new ConfigurationException("Null inputstream: cannot locate atlassian-user.xml");
            }
            SAXReader reader = new SAXReader();
            try {
                doc = reader.read(docIS);
            }
            catch (DocumentException e) {
                throw new ConfigurationException(e);
            }
            Node delegationNode = doc.selectSingleNode("//delegation");
            Node repositoriesNode = doc.selectSingleNode("//repositories");
            this.parseRepositories(repositoriesNode);
            if (delegationNode != null) {
                this.parseDelegation(delegationNode);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to load atlassian-user configuration: " + e.getMessage(), e);
        }
    }

    protected void parseRepositories(Node repositoriesNode) throws ConfigurationException, DocumentException, IOException {
        List repositoryElements = repositoriesNode.selectNodes("*");
        if (repositoryElements.isEmpty()) {
            throw new ConfigurationException("Nothing to init. There are no repositories specified.");
        }
        for (Object repositoryElement1 : repositoryElements) {
            Element repositoryElement = (Element)repositoryElement1;
            String repositoryType = repositoryElement.getName();
            Map<String, String> defaultComponentClassNames = this.defaultsParser.getDefaultClassesConfigForKey(repositoryType);
            Map<String, String> defaultComponents = this.defaultsParser.getDefaultParameterConfigForKey(repositoryType);
            RepositoryIdentifier identifier = this.parseRepositoryIdentifier(repositoryElement);
            if (this.repositoryIdentifiers.contains(identifier)) {
                throw new ConfigurationException("Repository keys must be unique. Please check that you have not used the key '" + identifier.getKey() + "' more than once in your atlassian-user.xml file.");
            }
            HashMap<String, String> componentClassNames = XMLConfigUtil.parseRepositoryElementForClassNames(repositoryElement);
            Map<String, String> components = XMLConfigUtil.parseRepositoryElementForStringData(repositoryElement);
            for (String o : defaultComponentClassNames.keySet()) {
                String componentClassName = o;
                if (componentClassNames.containsKey(componentClassName)) continue;
                componentClassNames.put(componentClassName, defaultComponentClassNames.get(componentClassName));
            }
            for (String componentName : defaultComponents.keySet()) {
                if (components.containsKey(componentName)) continue;
                components.put(componentName, defaultComponents.get(componentName));
            }
            RepositoryProcessor processor = this.instantiateProcessor(componentClassNames);
            DefaultRepositoryConfiguration configuration = new DefaultRepositoryConfiguration(identifier, processor, components, componentClassNames);
            if (this.isCachingEnabled(repositoryElement)) {
                configuration.setCacheConfiguration(this.parseCacheConfiguration());
            }
            this.repositoryIdentifiers.add(identifier);
            this.repositoryConfigurations.put(identifier, configuration);
        }
    }

    private RepositoryIdentifier parseRepositoryIdentifier(Element repositoryElement) {
        String key = repositoryElement.attributeValue("key");
        if (key == null) {
            throw new RuntimeException("Cannot specify repository without a key");
        }
        String name = repositoryElement.attributeValue("name", "Unnamed repository");
        return new DefaultRepositoryIdentifier(key, name);
    }

    private CacheConfiguration parseCacheConfiguration() throws DocumentException, IOException {
        Map<String, String> classNames = this.defaultsParser.getDefaultClassesConfigForKey("cache");
        return new DefaultCacheConfiguration(classNames);
    }

    private boolean isCachingEnabled(Element repositoryElement) {
        String cache = repositoryElement.attributeValue("cache");
        return cache != null && cache.equalsIgnoreCase("true");
    }

    private void parseDelegation(Node delegationNode) {
        LinkedList<RepositoryIdentifier> delegationOrder = new LinkedList<RepositoryIdentifier>();
        block0: for (Object o : delegationNode.selectNodes("key")) {
            String delegationKey = ((Element)o).getText();
            for (RepositoryIdentifier identifier : this.repositoryIdentifiers) {
                if (!delegationKey.equals(identifier.getKey())) continue;
                delegationOrder.add(identifier);
                continue block0;
            }
        }
        this.repositoryIdentifiers = delegationOrder;
    }

    private RepositoryProcessor instantiateProcessor(Map processorInfo) {
        String processorClassName = (String)processorInfo.get("processor");
        RepositoryProcessor processor = null;
        try {
            processor = (RepositoryProcessor)Class.forName(processorClassName).newInstance();
        }
        catch (Exception e) {
            log.error((Object)("Could not instantiate processor: " + e.getMessage()));
        }
        return processor;
    }

    public List<RepositoryConfiguration> getRepositoryConfigurations() {
        LinkedList<RepositoryConfiguration> result = new LinkedList<RepositoryConfiguration>();
        for (RepositoryIdentifier identifier : this.repositoryIdentifiers) {
            result.add(this.repositoryConfigurations.get(identifier));
        }
        return Collections.unmodifiableList(result);
    }
}

