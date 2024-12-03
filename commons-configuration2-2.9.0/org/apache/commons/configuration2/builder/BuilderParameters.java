/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;

public interface BuilderParameters {
    public static final String RESERVED_PARAMETER_PREFIX = "config-";

    public Map<String, Object> getParameters();
}

