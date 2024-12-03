/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.StepProgressProperties;
import com.atlassian.migration.agent.json.JsonSerializingException;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.store.StepProgressPropertiesStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepProgressPropertiesStoreImpl
implements StepProgressPropertiesStore {
    private static final Logger logger = LoggerFactory.getLogger(StepProgressPropertiesStoreImpl.class);
    private final EntityManagerTemplate tmpl;
    private static final String STEP_ID_PARAM = "stepId";
    private static final String PROGRESS_PROPERTIES_QUERY = "select stepprogressproperties from StepProgressProperties stepprogressproperties where stepprogressproperties.id=:stepId";

    public StepProgressPropertiesStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public Map<String, Object> getStepProgressProperties(String stepId) {
        try {
            return this.tmpl.query(StepProgressProperties.class, PROGRESS_PROPERTIES_QUERY).param(STEP_ID_PARAM, (Object)stepId).first().map(StepProgressProperties::getProgressProperties).filter(StringUtils::isNotBlank).map(StepProgressPropertiesStoreImpl::convertSafely).orElseGet(Collections::emptyMap);
        }
        catch (Exception e) {
            logger.error("There was an error retrieving progress properties for step: " + stepId, (Throwable)e);
            return Collections.emptyMap();
        }
    }

    private static Map<String, Object> convertSafely(String rawProgressProperties) {
        try {
            return Jsons.readValue(rawProgressProperties, Map.class);
        }
        catch (JsonSerializingException e) {
            logger.error("Error when deserializing progress properties", (Throwable)e);
            return Collections.emptyMap();
        }
    }

    @Override
    public void storeStepProgressProperties(String stepId, Map<String, Object> progressProperties) {
        Optional<StepProgressProperties> stepProgressProperties = this.tmpl.query(StepProgressProperties.class, PROGRESS_PROPERTIES_QUERY).param(STEP_ID_PARAM, (Object)stepId).first();
        String rawProgressProperties = Jsons.valueAsString(progressProperties);
        if (stepProgressProperties.isPresent()) {
            StepProgressProperties entity = stepProgressProperties.get();
            entity.setProgressProperties(rawProgressProperties);
            this.tmpl.merge(entity);
        } else {
            StepProgressProperties entity = new StepProgressProperties();
            entity.setStepId(stepId);
            entity.setProgressProperties(rawProgressProperties);
            this.tmpl.persist(entity);
            this.tmpl.flush();
        }
    }
}

