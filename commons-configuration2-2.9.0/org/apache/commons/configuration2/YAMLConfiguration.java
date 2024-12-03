/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.yaml.snakeyaml.DumperOptions
 *  org.yaml.snakeyaml.DumperOptions$FlowStyle
 *  org.yaml.snakeyaml.LoaderOptions
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.constructor.BaseConstructor
 *  org.yaml.snakeyaml.constructor.SafeConstructor
 *  org.yaml.snakeyaml.representer.Representer
 */
package org.apache.commons.configuration2;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.configuration2.AbstractYAMLBasedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.InputStreamSupport;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

public class YAMLConfiguration
extends AbstractYAMLBasedConfiguration
implements FileBasedConfiguration,
InputStreamSupport {
    public YAMLConfiguration() {
    }

    public YAMLConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }

    @Override
    public void read(Reader in) throws ConfigurationException {
        try {
            Yaml yaml = YAMLConfiguration.createYamlForReading(new LoaderOptions());
            Map map = (Map)yaml.load(in);
            this.load(map);
        }
        catch (Exception e) {
            YAMLConfiguration.rethrowException(e);
        }
    }

    public void read(Reader in, LoaderOptions options) throws ConfigurationException {
        try {
            Yaml yaml = YAMLConfiguration.createYamlForReading(options);
            Map map = (Map)yaml.load(in);
            this.load(map);
        }
        catch (Exception e) {
            YAMLConfiguration.rethrowException(e);
        }
    }

    @Override
    public void write(Writer out) throws ConfigurationException, IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.dump(out, options);
    }

    public void dump(Writer out, DumperOptions options) throws ConfigurationException, IOException {
        Yaml yaml = new Yaml(options);
        yaml.dump(this.constructMap(this.getNodeModel().getNodeHandler().getRootNode()), out);
    }

    @Override
    public void read(InputStream in) throws ConfigurationException {
        try {
            Yaml yaml = YAMLConfiguration.createYamlForReading(new LoaderOptions());
            Map map = (Map)yaml.load(in);
            this.load(map);
        }
        catch (Exception e) {
            YAMLConfiguration.rethrowException(e);
        }
    }

    public void read(InputStream in, LoaderOptions options) throws ConfigurationException {
        try {
            Yaml yaml = YAMLConfiguration.createYamlForReading(options);
            Map map = (Map)yaml.load(in);
            this.load(map);
        }
        catch (Exception e) {
            YAMLConfiguration.rethrowException(e);
        }
    }

    private static Yaml createYamlForReading(LoaderOptions options) {
        return new Yaml((BaseConstructor)new SafeConstructor(options), new Representer(new DumperOptions()), new DumperOptions(), options);
    }
}

