/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.ActiveObjectsException
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.joda.time.DateTimeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceService;
import com.atlassian.troubleshooting.healthcheck.rest.HealthCheckPropertiesRepresentation;
import com.atlassian.troubleshooting.stp.persistence.SupportHealthcheckSchema;
import java.util.Arrays;
import java.util.Iterator;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthStatusPropertiesPersistenceServiceImpl
implements HealthStatusPropertiesPersistenceService {
    public static final String LAST_RUN = "last-run";
    private static final Logger LOG = LoggerFactory.getLogger(HealthStatusPropertiesPersistenceServiceImpl.class);
    private final ActiveObjects ao;

    @Autowired
    public HealthStatusPropertiesPersistenceServiceImpl(ActiveObjects ao) {
        this.ao = ao;
    }

    public void storeProperties(String propertyName, String propertyValue) {
        try {
            Iterable<SupportHealthcheckSchema.SupportHealthStatusProperties> properties = this.getPropertiesValue(propertyName);
            if (properties.iterator().hasNext()) {
                this.updateProperties(properties, propertyValue);
            } else {
                SupportHealthcheckSchema.SupportHealthStatusProperties newProperty = (SupportHealthcheckSchema.SupportHealthStatusProperties)this.ao.create(SupportHealthcheckSchema.SupportHealthStatusProperties.class, new DBParam[]{new DBParam("PROPERTY_NAME", (Object)propertyName), new DBParam("PROPERTY_VALUE", (Object)propertyValue)});
                newProperty.save();
            }
        }
        catch (ActiveObjectsException ex) {
            LOG.error("There's a problem persisting the property {} into the database", (Object)propertyName, (Object)ex);
        }
    }

    public void updateProperties(Iterable<SupportHealthcheckSchema.SupportHealthStatusProperties> properties, String propertyValue) {
        try {
            for (SupportHealthcheckSchema.SupportHealthStatusProperties entity : properties) {
                entity.setPropertyValue(propertyValue);
                entity.save();
            }
        }
        catch (ActiveObjectsException ex) {
            LOG.error("There's a problem updating the property {} in the database", (Object)properties.iterator().next().getPropertyName(), (Object)ex);
        }
    }

    public Iterable<SupportHealthcheckSchema.SupportHealthStatusProperties> getPropertiesValue(String propertyName) {
        return Arrays.asList(this.ao.find(SupportHealthcheckSchema.SupportHealthStatusProperties.class, Query.select().where("PROPERTY_NAME = ?", new Object[]{propertyName})));
    }

    public HealthCheckPropertiesRepresentation getPropertiesRepresentation(String propertyName) {
        Iterator<SupportHealthcheckSchema.SupportHealthStatusProperties> properties = this.getPropertiesValue(propertyName).iterator();
        if (properties.hasNext()) {
            SupportHealthcheckSchema.SupportHealthStatusProperties entity = properties.next();
            return new HealthCheckPropertiesRepresentation(entity.getPropertyName(), entity.getPropertyValue());
        }
        return null;
    }

    public void removeProperties(String propertyName) {
        for (SupportHealthcheckSchema.SupportHealthStatusProperties entity : this.getPropertiesValue(propertyName)) {
            this.ao.delete(new RawEntity[]{entity});
        }
    }

    @Override
    public void storeLastRun() {
        this.storeProperties(LAST_RUN, String.valueOf(DateTimeUtils.currentTimeMillis()));
    }

    @Override
    public HealthCheckPropertiesRepresentation getLastRun() {
        return this.getPropertiesRepresentation(LAST_RUN);
    }
}

