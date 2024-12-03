/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import com.atlassian.troubleshooting.jfr.config.JfrServiceProductSupport;
import com.atlassian.troubleshooting.jfr.domain.JfrPropertiesDto;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.jfr.exception.TranslatableException;
import com.atlassian.troubleshooting.jfr.service.JfrPropertiesService;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJfrPropertiesService
implements JfrPropertiesService {
    private static final String INVALID_NAME_I18N = "stp.jfr.property.name.invalid";
    private final JfrProperties jfrProperties;
    private final Optional<JfrServiceProductSupport> jfrServiceProductSupport;
    private final I18nResolver i18nResolver;

    @Autowired
    public DefaultJfrPropertiesService(JfrProperties jfrProperties, Optional<JfrServiceProductSupport> jfrServiceProductSupport, I18nResolver i18nResolver) {
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
        this.jfrServiceProductSupport = Objects.requireNonNull(jfrServiceProductSupport);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
    }

    @Override
    public JfrPropertiesDto getProperties() {
        if (!this.isJfrFeatureEnabled()) {
            return JfrPropertiesDto.empty();
        }
        return JfrPropertiesDto.create(this.jfrProperties);
    }

    @Override
    public void setProperty(String propertyName, String propertyValue) {
        if (!this.isJfrFeatureEnabled()) {
            return;
        }
        JfrProperty jfrProperty = JfrProperty.fromPropertyName(propertyName).orElseThrow(() -> new JfrException(this.i18nResolver.getText(INVALID_NAME_I18N)));
        try {
            this.jfrProperties.setProperty(jfrProperty, propertyValue);
        }
        catch (TranslatableException exc) {
            throw new JfrException(this.i18nResolver.getText(exc.getI18nKey()), exc);
        }
    }

    private boolean isJfrFeatureEnabled() {
        return this.jfrServiceProductSupport.map(JfrServiceProductSupport::isSupported).orElse(false);
    }
}

