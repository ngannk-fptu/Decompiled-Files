/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.spi;

import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamsUriBuilder {
    private static final UUID URL_UUID_NAMESPACE_ID = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
    private static final String UUID_URN_NAMESPACE = "urn:uuid:";
    private static final Logger log = LoggerFactory.getLogger(StreamsUriBuilder.class);
    private String url;
    private Date timestamp;

    public URI getUri() {
        Preconditions.checkNotNull((Object)this.url, (Object)"url");
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeLong(URL_UUID_NAMESPACE_ID.getMostSignificantBits());
            dos.writeLong(URL_UUID_NAMESPACE_ID.getLeastSignificantBits());
            String tmpUrl = this.url;
            if (this.timestamp != null) {
                tmpUrl = tmpUrl.contains("?") ? tmpUrl + "&activityTimestamp=" + this.timestamp.getTime() : tmpUrl + "?activityTimestamp=" + this.timestamp.getTime();
            }
            dos.writeUTF(tmpUrl);
            dos.flush();
            UUID uuid = UUID.nameUUIDFromBytes(os.toByteArray());
            return URI.create(UUID_URN_NAMESPACE + uuid.toString());
        }
        catch (IOException ioe) {
            log.error("Error writing to byte array output stream", (Throwable)ioe);
            return null;
        }
    }

    public StreamsUriBuilder setUrl(String url) {
        this.url = (String)Preconditions.checkNotNull((Object)url, (Object)"url");
        return this;
    }

    public StreamsUriBuilder setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}

