/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.util.Map;

public class EnvSupportDataAppender
extends RootLevelSupportDataAppender {
    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        builder = builder.addCategory("stp.properties.environment.variables");
        for (Map.Entry<String, String> envVariable : System.getenv().entrySet()) {
            builder.addValue(envVariable.getKey(), envVariable.getValue());
        }
    }
}

