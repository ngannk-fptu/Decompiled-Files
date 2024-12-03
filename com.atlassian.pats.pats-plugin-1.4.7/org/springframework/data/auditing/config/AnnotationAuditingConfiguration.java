/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.Assert
 */
package org.springframework.data.auditing.config;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.auditing.config.AuditingConfiguration;
import org.springframework.util.Assert;

public class AnnotationAuditingConfiguration
implements AuditingConfiguration {
    private static final String MISSING_ANNOTATION_ATTRIBUTES = "Couldn't find annotation attributes for %s in %s!";
    private final AnnotationAttributes attributes;

    public AnnotationAuditingConfiguration(AnnotationMetadata metadata, Class<? extends Annotation> annotation) {
        Assert.notNull((Object)metadata, (String)"AnnotationMetadata must not be null!");
        Assert.notNull(annotation, (String)"Annotation must not be null!");
        Map attributesSource = metadata.getAnnotationAttributes(annotation.getName());
        if (attributesSource == null) {
            throw new IllegalArgumentException(String.format(MISSING_ANNOTATION_ATTRIBUTES, annotation, metadata));
        }
        this.attributes = new AnnotationAttributes(attributesSource);
    }

    @Override
    public String getAuditorAwareRef() {
        return this.attributes.getString("auditorAwareRef");
    }

    @Override
    public boolean isSetDates() {
        return this.attributes.getBoolean("setDates");
    }

    @Override
    public String getDateTimeProviderRef() {
        return this.attributes.getString("dateTimeProviderRef");
    }

    @Override
    public boolean isModifyOnCreate() {
        return this.attributes.getBoolean("modifyOnCreate");
    }
}

