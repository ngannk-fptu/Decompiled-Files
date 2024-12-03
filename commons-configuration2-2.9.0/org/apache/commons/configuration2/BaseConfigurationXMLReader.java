/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationXMLReader;
import org.apache.commons.configuration2.HierarchicalConfigurationConverter;

public class BaseConfigurationXMLReader
extends ConfigurationXMLReader {
    private Configuration config;

    public BaseConfigurationXMLReader() {
    }

    public BaseConfigurationXMLReader(Configuration conf) {
        this();
        this.setConfiguration(conf);
    }

    public Configuration getConfiguration() {
        return this.config;
    }

    public void setConfiguration(Configuration conf) {
        this.config = conf;
    }

    @Override
    public Configuration getParsedConfiguration() {
        return this.getConfiguration();
    }

    @Override
    protected void processKeys() {
        this.fireElementStart(this.getRootName(), null);
        new SAXConverter().process(this.getConfiguration());
        this.fireElementEnd(this.getRootName());
    }

    class SAXConverter
    extends HierarchicalConfigurationConverter {
        SAXConverter() {
        }

        @Override
        protected void elementStart(String name, Object value) {
            BaseConfigurationXMLReader.this.fireElementStart(name, null);
            if (value != null) {
                BaseConfigurationXMLReader.this.fireCharacters(value.toString());
            }
        }

        @Override
        protected void elementEnd(String name) {
            BaseConfigurationXMLReader.this.fireElementEnd(name);
        }
    }
}

