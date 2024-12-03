/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Attachment;
import java.util.Iterator;

public interface AttachmentEx
extends Attachment {
    @NotNull
    public Iterator<MimeHeader> getMimeHeaders();

    public static interface MimeHeader {
        public String getName();

        public String getValue();
    }
}

