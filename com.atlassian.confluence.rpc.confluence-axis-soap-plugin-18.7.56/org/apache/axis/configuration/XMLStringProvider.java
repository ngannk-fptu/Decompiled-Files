/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.configuration;

import java.io.ByteArrayInputStream;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.configuration.FileProvider;

public class XMLStringProvider
extends FileProvider {
    String xmlConfiguration;

    public XMLStringProvider(String xmlConfiguration) {
        super(new ByteArrayInputStream(xmlConfiguration.getBytes()));
        this.xmlConfiguration = xmlConfiguration;
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        this.setInputStream(new ByteArrayInputStream(this.xmlConfiguration.getBytes()));
        super.configureEngine(engine);
    }
}

