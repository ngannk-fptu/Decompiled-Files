/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.mail.internet.ContentType
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.mail.internet.ContentType;
import org.apache.axiom.attachments.IncomingAttachmentStreams;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMException;

abstract class AttachmentsDelegate {
    AttachmentsDelegate() {
    }

    abstract ContentType getContentType();

    abstract LifecycleManager getLifecycleManager();

    abstract void setLifecycleManager(LifecycleManager var1);

    abstract DataHandler getDataHandler(String var1);

    abstract void addDataHandler(String var1, DataHandler var2);

    abstract void removeDataHandler(String var1);

    abstract InputStream getRootPartInputStream(boolean var1) throws OMException;

    abstract String getRootPartContentID();

    abstract String getRootPartContentType();

    abstract IncomingAttachmentStreams getIncomingAttachmentStreams();

    abstract Set getContentIDs(boolean var1);

    abstract Map getMap();

    abstract long getContentLength() throws IOException;
}

