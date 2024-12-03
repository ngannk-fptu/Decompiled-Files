/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.io.ClassPathResource
 */
package com.atlassian.troubleshooting.jfr.config;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.util.concurrent.Supplier;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.text.ParseException;
import java.util.Objects;
import jdk.jfr.Configuration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

public class JfrConfigurationRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(JfrConfigurationRegistry.class);
    private static final String JFR_TEMPLATE_RESOURCE_NAME = "/jfr/%s.jfc";
    private static final String JFR_ACTIVE_CONFIGURATION_RESOURCE_NAME = "active_configuration.jfc";
    private final JfrProperties jfrProperties;
    private final SupportApplicationInfo applicationInfo;
    private final I18nResolver i18nResolver;
    private final Supplier<Configuration> configurationTemplate = this::createConfiguration;
    private final Supplier<Configuration> activeConfiguration = this::retrieveActiveConfiguration;

    @Autowired
    public JfrConfigurationRegistry(JfrProperties jfrProperties, SupportApplicationInfo applicationInfo, I18nResolver i18nResolver) {
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
    }

    public Configuration getConfigurationTemplate() {
        return this.configurationTemplate.get();
    }

    public Configuration getActiveConfiguration() {
        return this.activeConfiguration.get();
    }

    public void storeActiveConfiguration(Configuration activeConfiguration) {
        try {
            this.writeActiveConfiguration(activeConfiguration);
        }
        catch (IOException exc) {
            LOG.error("Failed to store active configuration", (Throwable)exc);
        }
    }

    public Path getActiveRecordingConfigurationPath() {
        String localApplicationHome = this.applicationInfo.getLocalApplicationHome();
        String recordingPath = this.jfrProperties.getRecordingPath();
        return Paths.get(localApplicationHome, recordingPath, JFR_ACTIVE_CONFIGURATION_RESOURCE_NAME);
    }

    public static Configuration getConfiguration(Path pathToConfiguration) throws IOException, ParseException {
        if (Files.isRegularFile(pathToConfiguration, new LinkOption[0])) {
            try (BufferedReader reader = Files.newBufferedReader(pathToConfiguration);){
                Configuration configuration = Configuration.create(reader);
                return configuration;
            }
        }
        throw new JfrException("Invalid path to custom configuration template: " + pathToConfiguration);
    }

    private Configuration createConfiguration() {
        return StringUtils.isNotBlank((CharSequence)this.jfrProperties.getJfrTemplatePath()) ? this.createCustomConfiguration() : this.createDefaultConfiguration();
    }

    private Configuration createCustomConfiguration() {
        Path jfrTemplatePath = Paths.get(this.applicationInfo.getApplicationHome(), (String)ObjectUtils.defaultIfNull((Object)this.jfrProperties.getJfrTemplatePath(), (Object)""));
        try {
            return JfrConfigurationRegistry.getConfiguration(jfrTemplatePath);
        }
        catch (IOException exc) {
            LOG.error("Failed to read custom configuration template: " + jfrTemplatePath, (Throwable)exc);
        }
        catch (ParseException exc) {
            LOG.error("Failed to parse custom configuration template: " + jfrTemplatePath, (Throwable)exc);
        }
        String reason = this.i18nResolver.getText("stp.jfr.error.invalid.configuration.path");
        throw new JfrException(reason + jfrTemplatePath);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Configuration createDefaultConfiguration() {
        String resourceName = String.format(JFR_TEMPLATE_RESOURCE_NAME, RecordingTemplate.DEFAULT.getTemplateName());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(resourceName, this.getClass().getClassLoader()).getInputStream()));){
            Configuration configuration = Configuration.create(reader);
            return configuration;
        }
        catch (IOException exc) {
            String reason = this.i18nResolver.getText("stp.jfr.error.read.default.configuration.template");
            throw new JfrException(reason + resourceName, exc);
        }
        catch (ParseException exc) {
            String reason = this.i18nResolver.getText("stp.jfr.error.parse.default.configuration.template");
            throw new JfrException(reason + resourceName, exc);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Configuration retrieveActiveConfiguration() {
        Path activeRecordingJfcPath = this.getActiveRecordingConfigurationPath();
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(activeRecordingJfcPath, new OpenOption[0]));){
            Configuration configuration = Configuration.create(reader);
            return configuration;
        }
        catch (IOException | ParseException exc) {
            String reason = this.i18nResolver.getText("stp.jfr.error.read.active.configuration.template");
            throw new JfrException(reason + JFR_ACTIVE_CONFIGURATION_RESOURCE_NAME, exc);
        }
    }

    private void writeActiveConfiguration(Configuration configuration) throws IOException {
        Path activeRecordingJfcPath = this.getActiveRecordingConfigurationPath();
        this.ensureFileExists(activeRecordingJfcPath);
        this.writeToFile(activeRecordingJfcPath, configuration.getContents());
    }

    private void ensureFileExists(Path path) throws IOException {
        if (Files.notExists(path, new LinkOption[0])) {
            Files.createFile(path, new FileAttribute[0]);
        }
    }

    private void writeToFile(Path path, String content) throws IOException {
        try (BufferedWriter fileWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]);){
            fileWriter.write(content);
        }
    }
}

