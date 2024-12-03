/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.logging.log4j.core.config.plugins.validation.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class NotBlankValidator
implements ConstraintValidator<NotBlank> {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private NotBlank annotation;

    @Override
    public void initialize(NotBlank anAnnotation) {
        this.annotation = anAnnotation;
    }

    @Override
    public boolean isValid(String name, Object value) {
        return Strings.isNotBlank((String)name) || this.err(name);
    }

    private boolean err(String name) {
        LOGGER.error(this.annotation.message(), (Object)name);
        return false;
    }
}

