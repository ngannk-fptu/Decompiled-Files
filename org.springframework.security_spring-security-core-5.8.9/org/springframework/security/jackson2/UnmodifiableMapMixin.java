/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.springframework.security.jackson2.UnmodifiableMapDeserializer;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using=UnmodifiableMapDeserializer.class)
class UnmodifiableMapMixin {
    @JsonCreator
    UnmodifiableMapMixin(Map<?, ?> map) {
    }
}

