/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.common.properties;

import com.atlassian.oauth2.common.properties.AbstractSystemProperty;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerSystemProperty
extends AbstractSystemProperty<Integer> {
    private static final Logger log = LoggerFactory.getLogger(IntegerSystemProperty.class);

    public IntegerSystemProperty(@Nonnull String propertyName, int defaultValue) {
        super(propertyName, defaultValue);
    }

    @Override
    @Nonnull
    public Integer getValue() {
        return (Integer)Optional.ofNullable(System.getProperty(this.propertyName)).flatMap(this::tryToParseInteger).orElse(this.defaultValue);
    }

    private Optional<Integer> tryToParseInteger(String systemPropValue) {
        try {
            return Optional.of(Integer.parseInt(systemPropValue));
        }
        catch (NumberFormatException e) {
            log.warn("System property [" + this.propertyName + "] was not in the expected Integer format");
            return Optional.empty();
        }
    }
}

