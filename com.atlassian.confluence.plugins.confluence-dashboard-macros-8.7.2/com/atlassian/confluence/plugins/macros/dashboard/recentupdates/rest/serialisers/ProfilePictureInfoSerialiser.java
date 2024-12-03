/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.serialisers;

import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ProfilePictureInfoSerialiser
extends JsonSerializer<ProfilePictureInfo> {
    public void serialize(ProfilePictureInfo profilePicInfo, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("fileName", profilePicInfo.getFileName());
        jgen.writeStringField("downloadPath", profilePicInfo.getDownloadPath());
        jgen.writeStringField("uriReference", profilePicInfo.getUriReference());
        jgen.writeBooleanField("default", profilePicInfo.isDefault());
        jgen.writeBooleanField("anonymous", profilePicInfo.isAnonymousPicture());
        jgen.writeBooleanField("uploaded", profilePicInfo.isUploaded());
        jgen.writeEndObject();
    }
}

