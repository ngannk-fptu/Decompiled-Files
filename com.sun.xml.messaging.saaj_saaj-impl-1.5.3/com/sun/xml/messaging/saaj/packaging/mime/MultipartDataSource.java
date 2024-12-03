/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.packaging.mime;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import javax.activation.DataSource;

public interface MultipartDataSource
extends DataSource {
    public int getCount();

    public MimeBodyPart getBodyPart(int var1) throws MessagingException;
}

