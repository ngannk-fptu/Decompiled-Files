/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.fluent;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Configurations {
    private final Parameters parameters;

    public Configurations() {
        this(null);
    }

    public Configurations(Parameters params) {
        this.parameters = params != null ? params : new Parameters();
    }

    public Parameters getParameters() {
        return this.parameters;
    }

    public <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> fileBasedBuilder(Class<T> configClass, File file) {
        return this.createFileBasedBuilder(configClass, this.fileParams(file));
    }

    public <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> fileBasedBuilder(Class<T> configClass, URL url) {
        return this.createFileBasedBuilder(configClass, this.fileParams(url));
    }

    public <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> fileBasedBuilder(Class<T> configClass, String path) {
        return this.createFileBasedBuilder(configClass, this.fileParams(path));
    }

    public <T extends FileBasedConfiguration> T fileBased(Class<T> configClass, File file) throws ConfigurationException {
        return (T)((FileBasedConfiguration)this.fileBasedBuilder(configClass, file).getConfiguration());
    }

    public <T extends FileBasedConfiguration> T fileBased(Class<T> configClass, URL url) throws ConfigurationException {
        return (T)((FileBasedConfiguration)this.fileBasedBuilder(configClass, url).getConfiguration());
    }

    public <T extends FileBasedConfiguration> T fileBased(Class<T> configClass, String path) throws ConfigurationException {
        return (T)((FileBasedConfiguration)this.fileBasedBuilder(configClass, path).getConfiguration());
    }

    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder() {
        return this.createFileBasedBuilder(PropertiesConfiguration.class);
    }

    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(File file) {
        return this.fileBasedBuilder(PropertiesConfiguration.class, file);
    }

    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(PropertiesBuilderParameters parameters) {
        return this.propertiesBuilder().configure(new BuilderParameters[]{parameters});
    }

    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(URL url) {
        return this.fileBasedBuilder(PropertiesConfiguration.class, url);
    }

    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(String path) {
        return this.fileBasedBuilder(PropertiesConfiguration.class, path);
    }

    public PropertiesConfiguration properties(File file) throws ConfigurationException {
        return (PropertiesConfiguration)this.propertiesBuilder(file).getConfiguration();
    }

    public PropertiesConfiguration properties(URL url) throws ConfigurationException {
        return (PropertiesConfiguration)this.propertiesBuilder(url).getConfiguration();
    }

    public PropertiesConfiguration properties(String path) throws ConfigurationException {
        return (PropertiesConfiguration)this.propertiesBuilder(path).getConfiguration();
    }

    public FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder(File file) {
        return this.fileBasedBuilder(XMLConfiguration.class, file);
    }

    public FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder(URL url) {
        return this.fileBasedBuilder(XMLConfiguration.class, url);
    }

    public FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder(String path) {
        return this.fileBasedBuilder(XMLConfiguration.class, path);
    }

    public XMLConfiguration xml(File file) throws ConfigurationException {
        return (XMLConfiguration)this.xmlBuilder(file).getConfiguration();
    }

    public XMLConfiguration xml(URL url) throws ConfigurationException {
        return (XMLConfiguration)this.xmlBuilder(url).getConfiguration();
    }

    public XMLConfiguration xml(String path) throws ConfigurationException {
        return (XMLConfiguration)this.xmlBuilder(path).getConfiguration();
    }

    public FileBasedConfigurationBuilder<INIConfiguration> iniBuilder(File file) {
        return this.fileBasedBuilder(INIConfiguration.class, file);
    }

    public FileBasedConfigurationBuilder<INIConfiguration> iniBuilder(URL url) {
        return this.fileBasedBuilder(INIConfiguration.class, url);
    }

    public FileBasedConfigurationBuilder<INIConfiguration> iniBuilder(String path) {
        return this.fileBasedBuilder(INIConfiguration.class, path);
    }

    public INIConfiguration ini(File file) throws ConfigurationException {
        return (INIConfiguration)this.iniBuilder(file).getConfiguration();
    }

    public INIConfiguration ini(URL url) throws ConfigurationException {
        return (INIConfiguration)this.iniBuilder(url).getConfiguration();
    }

    public INIConfiguration ini(String path) throws ConfigurationException {
        return (INIConfiguration)this.iniBuilder(path).getConfiguration();
    }

    public CombinedConfigurationBuilder combinedBuilder(File file) {
        return new CombinedConfigurationBuilder().configure(this.fileParams(file));
    }

    public CombinedConfigurationBuilder combinedBuilder(URL url) {
        return new CombinedConfigurationBuilder().configure(this.fileParams(url));
    }

    public CombinedConfigurationBuilder combinedBuilder(String path) {
        return new CombinedConfigurationBuilder().configure(this.fileParams(path));
    }

    public CombinedConfiguration combined(File file) throws ConfigurationException {
        return (CombinedConfiguration)this.combinedBuilder(file).getConfiguration();
    }

    public CombinedConfiguration combined(URL url) throws ConfigurationException {
        return (CombinedConfiguration)this.combinedBuilder(url).getConfiguration();
    }

    public CombinedConfiguration combined(String path) throws ConfigurationException {
        return (CombinedConfiguration)this.combinedBuilder(path).getConfiguration();
    }

    private <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> createFileBasedBuilder(Class<T> configClass) {
        return new FileBasedConfigurationBuilder<T>(configClass);
    }

    private <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> createFileBasedBuilder(Class<T> configClass, FileBasedBuilderParameters params) {
        return this.createFileBasedBuilder(configClass).configure(new BuilderParameters[]{params});
    }

    private FileBasedBuilderParameters fileParams() {
        return this.getParameters().fileBased();
    }

    private FileBasedBuilderParameters fileParams(File file) {
        return (FileBasedBuilderParameters)this.fileParams().setFile(file);
    }

    private FileBasedBuilderParameters fileParams(URL url) {
        return (FileBasedBuilderParameters)this.fileParams().setURL(url);
    }

    private FileBasedBuilderParameters fileParams(String path) {
        return (FileBasedBuilderParameters)this.fileParams().setFileName(path);
    }
}

