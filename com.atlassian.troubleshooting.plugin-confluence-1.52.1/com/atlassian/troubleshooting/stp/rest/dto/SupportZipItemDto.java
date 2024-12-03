/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.rest.dto;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.persistence.ZipConfiguration;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SupportZipItemDto {
    @JsonProperty
    private final String description;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String category;
    @JsonProperty
    private final boolean required;
    @JsonProperty
    private final boolean selected;

    @JsonCreator
    private SupportZipItemDto(@JsonProperty(value="description") String description, @JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="category") String category, @JsonProperty(value="required") boolean required, @JsonProperty(value="selected") boolean selected) {
        this.description = Objects.requireNonNull(description);
        this.key = Objects.requireNonNull(key);
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
        this.required = required;
        this.selected = selected;
    }

    public static SupportZipItemDto supportZipOption(SupportZipBundle bundle, ZipConfiguration zipConfiguration) {
        return new SupportZipItemDto(bundle.getDescription(), bundle.getKey(), bundle.getTitle(), bundle.getCategory().getName(), bundle.isRequired(), zipConfiguration.isBundleSelected(bundle.getKey()));
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isSelected() {
        return this.selected;
    }
}

