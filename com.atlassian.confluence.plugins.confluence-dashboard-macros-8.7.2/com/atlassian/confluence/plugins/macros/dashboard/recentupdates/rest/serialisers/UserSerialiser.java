/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.serialisers;

import com.atlassian.user.User;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class UserSerialiser
extends JsonSerializer<User> {
    public void serialize(User user, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("name", user.getName());
        jgen.writeStringField("fullName", user.getFullName());
        jgen.writeEndObject();
    }
}

