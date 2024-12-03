/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.core.util;

import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbSerializationUtil {
    public static final String EMPTY_JSON_LIST = "[]";
    private static final Logger logger = LoggerFactory.getLogger(DbSerializationUtil.class);

    private DbSerializationUtil() {
    }

    public static List<DiagnosticDescription> deserializeDiagnosticDescriptions(String serializedDiagnosticDescriptions) {
        try {
            return (List)new ObjectMapper().readValue(serializedDiagnosticDescriptions, (TypeReference)new TypeReference<List<DiagnosticDescription>>(){});
        }
        catch (Exception e) {
            logger.warn(String.format("Could not deserialize diagnostic description; %s", serializedDiagnosticDescriptions));
            logger.debug("Reason why diagnostic description couldn't be deserialized", (Throwable)e);
            return Collections.emptyList();
        }
    }

    public static String serializeDiagnosticDescriptions(@Nullable List<DiagnosticDescription> diagnosticDescriptions) {
        if (diagnosticDescriptions == null) {
            return EMPTY_JSON_LIST;
        }
        try {
            return new ObjectMapper().writeValueAsString(diagnosticDescriptions);
        }
        catch (Exception e) {
            return EMPTY_JSON_LIST;
        }
    }

    public static List<OptedOutEntity> deserializeOptedOutEntities(String serializedEntityOptOutIdentifiers) {
        if (StringUtils.isBlank((CharSequence)serializedEntityOptOutIdentifiers)) {
            return Collections.emptyList();
        }
        try {
            return (List)new ObjectMapper().readValue(serializedEntityOptOutIdentifiers, (TypeReference)new TypeReference<List<OptedOutEntity>>(){});
        }
        catch (Exception e) {
            logger.warn(String.format("Could not deserialize opted out entities; %s", serializedEntityOptOutIdentifiers));
            logger.debug("Reason why opted out entity couldn't be deserialized", (Throwable)e);
            return Collections.emptyList();
        }
    }

    public static String serializeOptedOutEntities(@Nullable List<OptedOutEntity> optedOutEntities) {
        if (optedOutEntities == null) {
            return EMPTY_JSON_LIST;
        }
        try {
            return new ObjectMapper().writeValueAsString(optedOutEntities);
        }
        catch (Exception e) {
            return EMPTY_JSON_LIST;
        }
    }
}

