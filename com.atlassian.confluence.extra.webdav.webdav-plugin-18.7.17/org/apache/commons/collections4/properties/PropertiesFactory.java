/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections4.properties;

import java.util.Properties;
import org.apache.commons.collections4.properties.AbstractPropertiesFactory;

public class PropertiesFactory
extends AbstractPropertiesFactory<Properties> {
    public static final PropertiesFactory INSTANCE = new PropertiesFactory();

    private PropertiesFactory() {
    }

    @Override
    protected Properties createProperties() {
        return new Properties();
    }
}

