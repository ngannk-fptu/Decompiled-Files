/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.applinks.internal.rest.model;

import com.atlassian.applinks.internal.rest.model.IllegalRestRepresentationStateException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
@org.codehaus.jackson.map.annotate.JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
@org.codehaus.jackson.annotate.JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplinksRestRepresentation {
    protected final String validateString(@Nonnull String key, @Nullable String value) {
        if (StringUtils.isEmpty((CharSequence)value)) {
            throw new IllegalRestRepresentationStateException(key);
        }
        return value;
    }

    protected final URI validateURI(@Nonnull String key, @Nullable URI value) {
        if (value == null) {
            throw new IllegalRestRepresentationStateException(key);
        }
        return value;
    }
}

