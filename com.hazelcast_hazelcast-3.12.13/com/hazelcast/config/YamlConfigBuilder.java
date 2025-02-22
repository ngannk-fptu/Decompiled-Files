/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractYamlConfigBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigBuilder;
import com.hazelcast.config.ConfigSections;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.YamlConfigLocator;
import com.hazelcast.config.YamlMemberDomConfigProcessor;
import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.config.yaml.YamlDomChecker;
import com.hazelcast.internal.yaml.YamlLoader;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlUtil;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.w3c.dom.Node;

public class YamlConfigBuilder
extends AbstractYamlConfigBuilder
implements ConfigBuilder {
    private final InputStream in;
    private File configurationFile;
    private URL configurationUrl;

    public YamlConfigBuilder(String yamlFileName) throws FileNotFoundException {
        this(new FileInputStream(yamlFileName));
        this.configurationFile = new File(yamlFileName);
    }

    public YamlConfigBuilder(InputStream inputStream) {
        Preconditions.checkTrue(inputStream != null, "inputStream can't be null");
        this.in = inputStream;
    }

    public YamlConfigBuilder(URL url) throws IOException {
        Preconditions.checkNotNull(url, "URL is null!");
        this.in = url.openStream();
        this.configurationUrl = url;
    }

    public YamlConfigBuilder() {
        this((YamlConfigLocator)null);
    }

    public YamlConfigBuilder(YamlConfigLocator locator) {
        if (locator == null) {
            locator = new YamlConfigLocator(true);
            locator.locateEverywhere();
        }
        this.in = locator.getIn();
        this.configurationFile = locator.getConfigurationFile();
        this.configurationUrl = locator.getConfigurationUrl();
    }

    @Override
    public Config build() {
        return this.build(new Config());
    }

    Config build(Config config) {
        YamlUtil.ensureRunningOnJava8OrHigher();
        config.setConfigurationFile(this.configurationFile);
        config.setConfigurationUrl(this.configurationUrl);
        try {
            this.parseAndBuildConfig(config);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        finally {
            IOUtil.closeResource(this.in);
        }
        return config;
    }

    private void parseAndBuildConfig(Config config) throws Exception {
        YamlMapping yamlRootNode;
        try {
            yamlRootNode = (YamlMapping)YamlLoader.load(this.in);
        }
        catch (Exception ex) {
            throw new InvalidConfigurationException("Invalid YAML configuration", ex);
        }
        YamlMapping imdgRoot = yamlRootNode.childAsMapping(ConfigSections.HAZELCAST.name);
        if (imdgRoot == null) {
            throw new InvalidConfigurationException("No mapping with hazelcast key is found in the provided configuration");
        }
        YamlDomChecker.check(imdgRoot);
        Node w3cRootNode = W3cDomUtil.asW3cNode(imdgRoot);
        this.replaceVariables(w3cRootNode);
        this.importDocuments(imdgRoot);
        new YamlMemberDomConfigProcessor(true, config).buildConfig(w3cRootNode);
    }

    public YamlConfigBuilder setProperties(Properties properties) {
        this.setPropertiesInternal(properties);
        return this;
    }

    @Override
    protected String getConfigRoot() {
        return ConfigSections.HAZELCAST.name;
    }
}

