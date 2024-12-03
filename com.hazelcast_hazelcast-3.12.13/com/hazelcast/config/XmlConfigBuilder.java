/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractXmlConfigBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigBuilder;
import com.hazelcast.config.ConfigSections;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MemberDomConfigProcessor;
import com.hazelcast.config.XmlConfigLocator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlConfigBuilder
extends AbstractXmlConfigBuilder
implements ConfigBuilder {
    private static final ILogger LOGGER = Logger.getLogger(XmlConfigBuilder.class);
    private final InputStream in;
    private File configurationFile;
    private URL configurationUrl;

    public XmlConfigBuilder(String xmlFileName) throws FileNotFoundException {
        this(new FileInputStream(xmlFileName));
        this.configurationFile = new File(xmlFileName);
    }

    public XmlConfigBuilder(InputStream inputStream) {
        Preconditions.checkTrue(inputStream != null, "inputStream can't be null");
        this.in = inputStream;
    }

    public XmlConfigBuilder(URL url) throws IOException {
        Preconditions.checkNotNull(url, "URL is null!");
        this.in = url.openStream();
        this.configurationUrl = url;
    }

    public XmlConfigBuilder() {
        this((XmlConfigLocator)null);
    }

    public XmlConfigBuilder(XmlConfigLocator locator) {
        if (locator == null) {
            locator = new XmlConfigLocator();
            locator.locateEverywhere();
        }
        this.in = locator.getIn();
        this.configurationFile = locator.getConfigurationFile();
        this.configurationUrl = locator.getConfigurationUrl();
    }

    public XmlConfigBuilder setProperties(Properties properties) {
        super.setPropertiesInternal(properties);
        return this;
    }

    @Override
    protected AbstractXmlConfigBuilder.ConfigType getConfigType() {
        return AbstractXmlConfigBuilder.ConfigType.SERVER;
    }

    @Override
    public Config build() {
        return this.build(new Config());
    }

    Config build(Config config) {
        config.setConfigurationFile(this.configurationFile);
        config.setConfigurationUrl(this.configurationUrl);
        try {
            this.parseAndBuildConfig(config);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        return config;
    }

    private void parseAndBuildConfig(Config config) throws Exception {
        Document doc = this.parse(this.in);
        Element root = doc.getDocumentElement();
        this.checkRootElement(root);
        try {
            root.getTextContent();
        }
        catch (Throwable e) {
            this.domLevel3 = false;
        }
        this.process(root);
        if (this.shouldValidateTheSchema()) {
            this.schemaValidation(root.getOwnerDocument());
        }
        new MemberDomConfigProcessor(this.domLevel3, config).buildConfig(root);
    }

    private void checkRootElement(Element root) {
        String rootNodeName = root.getNodeName();
        if (!ConfigSections.HAZELCAST.isEqual(rootNodeName)) {
            throw new InvalidConfigurationException("Invalid root element in xml configuration! Expected: <" + ConfigSections.HAZELCAST.name + ">, Actual: <" + rootNodeName + ">.");
        }
    }

    private boolean shouldValidateTheSchema() {
        return System.getProperty("hazelcast.internal.override.version") == null;
    }

    @Override
    protected Document parse(InputStream is) throws Exception {
        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        try {
            doc = builder.parse(is);
        }
        catch (Exception e) {
            if (this.configurationFile != null) {
                String msg = "Failed to parse " + this.configurationFile + StringUtil.LINE_SEPARATOR + "Exception: " + e.getMessage() + StringUtil.LINE_SEPARATOR + "Hazelcast startup interrupted.";
                LOGGER.severe(msg);
            } else if (this.configurationUrl != null) {
                String msg = "Failed to parse " + this.configurationUrl + StringUtil.LINE_SEPARATOR + "Exception: " + e.getMessage() + StringUtil.LINE_SEPARATOR + "Hazelcast startup interrupted.";
                LOGGER.severe(msg);
            } else {
                String msg = "Failed to parse the inputstream" + StringUtil.LINE_SEPARATOR + "Exception: " + e.getMessage() + StringUtil.LINE_SEPARATOR + "Hazelcast startup interrupted.";
                LOGGER.severe(msg);
            }
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
        finally {
            IOUtil.closeResource(is);
        }
        return doc;
    }
}

