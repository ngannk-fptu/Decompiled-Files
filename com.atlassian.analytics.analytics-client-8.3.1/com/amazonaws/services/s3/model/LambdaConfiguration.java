/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.S3Event;
import java.io.Serializable;
import java.util.EnumSet;

public class LambdaConfiguration
extends NotificationConfiguration
implements Serializable {
    private final String functionARN;

    public LambdaConfiguration(String functionARN, EnumSet<S3Event> events) {
        super(events);
        this.functionARN = functionARN;
    }

    public LambdaConfiguration(String functionARN, String ... events) {
        super(events);
        this.functionARN = functionARN;
    }

    public String getFunctionARN() {
        return this.functionARN;
    }
}

