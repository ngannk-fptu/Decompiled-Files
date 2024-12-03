/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.avatar.AvatarProvider
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.atlassian.crowd.model.user.User
 *  com.google.common.base.Charsets
 *  javax.annotation.Nullable
 *  org.apache.commons.codec.binary.Hex
 */
package com.atlassian.crowd.manager.avatar;

import com.atlassian.crowd.manager.avatar.AvatarProvider;
import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.Charsets;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.commons.codec.binary.Hex;

public class WebServiceAvatarProvider
implements AvatarProvider {
    private final PropertyManager propertyManager;
    private final URI base;

    public WebServiceAvatarProvider(PropertyManager propertyManager, URI base) {
        this.propertyManager = propertyManager;
        this.base = base;
    }

    @Nullable
    public URI getUserAvatar(User user, int sizeHint) {
        String email;
        if (this.propertyManager.isUseWebAvatars() && (email = user.getEmailAddress()) != null) {
            return this.gravatarUrlForEmail(email, sizeHint);
        }
        return null;
    }

    public URI getHostedUserAvatarUrl(long applicationId, String username, int sizeHint) {
        return null;
    }

    static String hashOfEmail(String address) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            return new String(Hex.encodeHex((byte[])digest.digest(address.toLowerCase(Locale.ROOT).trim().getBytes(Charsets.UTF_8))));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 digest unexpectedly not available", e);
        }
    }

    URI gravatarUrlForEmail(String address, int sizeHint) {
        String hash = WebServiceAvatarProvider.hashOfEmail(address);
        return this.base.resolve(hash + "?s=" + sizeHint);
    }
}

