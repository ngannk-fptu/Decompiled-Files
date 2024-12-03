/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.attachments;

import javax.activation.DataHandler;

public interface Part {
    public DataHandler getDataHandler();

    public long getSize();

    public String getContentType();

    public String getContentID();

    public String getHeader(String var1);
}

