/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.oauth2.client.rest.api;

import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class RestErrorCollection {
    @XmlElement
    private final List<String> errors;
    @XmlElement
    private final Map<String, List<String>> fieldErrors;

    public RestErrorCollection() {
        this(Collections.emptyList(), Collections.emptyMap());
    }

    public RestErrorCollection(@Nonnull ErrorCollection errorCollection) {
        this(errorCollection.getErrors(), errorCollection.getFieldErrors());
    }

    public RestErrorCollection(@Nonnull List<String> errors, @Nonnull Map<String, List<String>> fieldErrors) {
        this.errors = ImmutableList.copyOf(errors);
        this.fieldErrors = ImmutableMap.copyOf((Map)Maps.transformValues(fieldErrors, ImmutableList::copyOf));
    }

    @Nonnull
    public List<String> getErrors() {
        return this.errors;
    }

    @Nonnull
    public Map<String, List<String>> getFieldErrors() {
        return this.fieldErrors;
    }
}

