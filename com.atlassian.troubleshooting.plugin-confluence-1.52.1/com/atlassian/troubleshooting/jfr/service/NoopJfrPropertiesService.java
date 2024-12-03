/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.domain.JfrPropertiesDto;
import com.atlassian.troubleshooting.jfr.service.JfrPropertiesService;

public class NoopJfrPropertiesService
implements JfrPropertiesService {
    @Override
    public JfrPropertiesDto getProperties() {
        return JfrPropertiesDto.empty();
    }

    @Override
    public void setProperty(String propertyName, String propertyValue) {
    }
}

