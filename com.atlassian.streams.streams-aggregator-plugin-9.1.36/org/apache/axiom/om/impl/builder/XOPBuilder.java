/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.builder;

import javax.activation.DataHandler;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;

public interface XOPBuilder
extends OMAttachmentAccessor {
    public DataHandler getDataHandler(String var1) throws OMException;

    public Attachments getAttachments();
}

