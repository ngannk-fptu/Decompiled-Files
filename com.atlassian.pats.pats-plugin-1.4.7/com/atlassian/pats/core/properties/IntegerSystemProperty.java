/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.core.properties;

import com.atlassian.pats.core.properties.AbstractSystemProperty;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerSystemProperty
extends AbstractSystemProperty<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(IntegerSystemProperty.class);

    IntegerSystemProperty(@Nonnull String propertyName, int defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    @Nonnull
    public Integer getValue() {
        String rawValue = System.getProperty(this.propertyName);
        try {
            return Optional.ofNullable(System.getProperty(this.propertyName)).map(Integer::parseInt).orElse((Integer)this.defaultValue);
        }
        catch (NumberFormatException e) {
            logger.warn("Illegal value of system property " + this.propertyName + ", expected an integer but was: " + rawValue);
            return (Integer)this.defaultValue;
        }
    }
}

