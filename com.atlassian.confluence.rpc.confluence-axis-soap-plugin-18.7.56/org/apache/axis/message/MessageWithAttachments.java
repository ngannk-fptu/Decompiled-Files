/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.Hashtable;

public interface MessageWithAttachments {
    public boolean hasAttachments();

    public Hashtable getAttachments();

    public Object getAttachment(String var1);

    public Object getAttachment(int var1);
}

