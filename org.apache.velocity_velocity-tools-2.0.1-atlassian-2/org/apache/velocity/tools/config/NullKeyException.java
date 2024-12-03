/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationException;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.ToolConfiguration;

public class NullKeyException
extends ConfigurationException {
    public NullKeyException(Data data) {
        super(data, "Key is null for data with value of '" + data.getValue() + '\'');
    }

    public NullKeyException(ToolConfiguration tool) {
        super((Configuration)tool, "Key is null for tool whose class is '" + tool.getClassname() + '\'');
    }
}

