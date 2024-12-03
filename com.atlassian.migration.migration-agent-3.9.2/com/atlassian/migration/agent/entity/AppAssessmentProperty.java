/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.migration.agent.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public enum AppAssessmentProperty {
    MIGRATION_NOTES("migrationNotes"),
    MIGRATION_STATUS("migrationStatus"),
    ALTERNATIVE_APP_KEY("alternativeAppKey"),
    CONSENT_STATUS("consentStatus");

    private static final Map<String, AppAssessmentProperty> supportedProperties;
    private final String name;

    private AppAssessmentProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static boolean isSupported(String propName) {
        return supportedProperties.containsKey(propName.toLowerCase());
    }

    public static AppAssessmentProperty getAppAssessmentPropertyByName(String propName) {
        return supportedProperties.get(propName.toLowerCase());
    }

    static {
        supportedProperties = ImmutableMap.copyOf(Arrays.stream(AppAssessmentProperty.values()).collect(Collectors.toMap(e -> e.name.toLowerCase(), UnaryOperator.identity())));
    }
}

