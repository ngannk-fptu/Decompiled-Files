/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.avatar.AvatarReference$BlobAvatar
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.mapper.AttributeContextMapper;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;

public class JpegPhotoContextMapper
extends AttributeContextMapper<AvatarReference.BlobAvatar> {
    private static final Logger logger = LoggerFactory.getLogger(JpegPhotoContextMapper.class);

    public JpegPhotoContextMapper() {
        this("jpegPhoto");
    }

    public JpegPhotoContextMapper(String propertyName) {
        super(propertyName);
    }

    @Override
    public AvatarReference.BlobAvatar mapFromContext(Object ctx) {
        DirContextAdapter dc = (DirContextAdapter)ctx;
        Object obj = dc.getObjectAttribute(this.propertyName);
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return new AvatarReference.BlobAvatar("image/jpeg", (byte[])obj);
        }
        logger.debug("Unexpected type for jpegPhoto: {}", obj.getClass());
        return null;
    }
}

