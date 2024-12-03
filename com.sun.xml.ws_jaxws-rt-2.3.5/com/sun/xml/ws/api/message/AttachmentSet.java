/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Attachment;

public interface AttachmentSet
extends Iterable<Attachment> {
    @Nullable
    public Attachment get(String var1);

    public boolean isEmpty();

    public void add(Attachment var1);
}

