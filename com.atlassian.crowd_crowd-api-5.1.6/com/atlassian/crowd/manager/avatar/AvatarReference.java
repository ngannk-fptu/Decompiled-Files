/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.crowd.manager.avatar;

import com.google.common.base.Preconditions;
import java.net.URI;
import org.apache.commons.codec.binary.Base64;

public interface AvatarReference {
    public URI toUri();

    public static class BlobAvatar
    implements AvatarReference {
        private final String contentType;
        private final byte[] content;

        public BlobAvatar(String contentType, byte[] content) {
            this.contentType = (String)Preconditions.checkNotNull((Object)contentType);
            this.content = (byte[])Preconditions.checkNotNull((Object)content);
        }

        public String getContentType() {
            return this.contentType;
        }

        public byte[] getContent() {
            return this.content;
        }

        @Override
        public URI toUri() {
            return URI.create("data:" + this.getContentType() + ";base64," + Base64.encodeBase64String((byte[])this.getContent()));
        }
    }

    public static class UriAvatarReference
    implements AvatarReference {
        private final URI uri;

        public UriAvatarReference(URI uri) {
            this.uri = (URI)Preconditions.checkNotNull((Object)uri);
        }

        public URI getUri() {
            return this.uri;
        }

        @Override
        public URI toUri() {
            return this.uri;
        }
    }
}

