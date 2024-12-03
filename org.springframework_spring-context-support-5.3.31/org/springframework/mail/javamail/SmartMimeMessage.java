/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.FileTypeMap
 *  javax.mail.Session
 *  javax.mail.internet.MimeMessage
 *  org.springframework.lang.Nullable
 */
package org.springframework.mail.javamail;

import javax.activation.FileTypeMap;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.springframework.lang.Nullable;

class SmartMimeMessage
extends MimeMessage {
    @Nullable
    private final String defaultEncoding;
    @Nullable
    private final FileTypeMap defaultFileTypeMap;

    public SmartMimeMessage(Session session, @Nullable String defaultEncoding, @Nullable FileTypeMap defaultFileTypeMap) {
        super(session);
        this.defaultEncoding = defaultEncoding;
        this.defaultFileTypeMap = defaultFileTypeMap;
    }

    @Nullable
    public final String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    @Nullable
    public final FileTypeMap getDefaultFileTypeMap() {
        return this.defaultFileTypeMap;
    }
}

