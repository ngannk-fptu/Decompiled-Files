/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.domain.JfrPropertiesDto;

public interface JfrPropertiesService {
    public JfrPropertiesDto getProperties();

    public void setProperty(String var1, String var2);
}

