/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.config.JfrProperty;
import com.atlassian.troubleshooting.jfr.domain.JfrPropertyDto;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonProperty;

public class JfrPropertiesDto {
    @JsonProperty
    private final List<JfrPropertyDto> propertyList;

    JfrPropertiesDto(List<JfrPropertyDto> propertyList) {
        this.propertyList = propertyList;
    }

    public List<JfrPropertyDto> getJfrProperties() {
        return ImmutableList.copyOf(this.propertyList);
    }

    public static JfrPropertiesDto create(JfrProperties jfrProperties) {
        return new JfrPropertiesDto(Arrays.stream(JfrProperty.values()).map(property -> JfrPropertyDto.create(property, jfrProperties)).collect(Collectors.toList()));
    }

    public static JfrPropertiesDto empty() {
        return new JfrPropertiesDto(Collections.emptyList());
    }
}

