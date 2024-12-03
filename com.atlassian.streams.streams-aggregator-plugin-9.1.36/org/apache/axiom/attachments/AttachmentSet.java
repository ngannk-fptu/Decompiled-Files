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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.mail.internet.ContentType;
import org.apache.axiom.attachments.AttachmentsDelegate;
import org.apache.axiom.attachments.IncomingAttachmentStreams;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMException;

class AttachmentSet
extends AttachmentsDelegate {
    private final Map attachmentsMap = new LinkedHashMap();

    AttachmentSet() {
    }

    ContentType getContentType() {
        return null;
    }

    LifecycleManager getLifecycleManager() {
        return null;
    }

    void setLifecycleManager(LifecycleManager manager) {
    }

    DataHandler getDataHandler(String contentID) {
        return (DataHandler)this.attachmentsMap.get(contentID);
    }

    void addDataHandler(String contentID, DataHandler dataHandler) {
        this.attachmentsMap.put(contentID, dataHandler);
    }

    void removeDataHandler(String blobContentID) {
        this.attachmentsMap.remove(blobContentID);
    }

    InputStream getRootPartInputStream(boolean preserve) throws OMException {
        throw new OMException("Invalid operation. Attachments are created programatically.");
    }

    String getRootPartContentID() {
        return null;
    }

    String getRootPartContentType() {
        throw new OMException("The attachments map was created programatically. Unsupported operation.");
    }

    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        throw new IllegalStateException("The attachments map was created programatically. No streams are available.");
    }

    Set getContentIDs(boolean fetchAll) {
        return this.attachmentsMap.keySet();
    }

    Map getMap() {
        return Collections.unmodifiableMap(this.attachmentsMap);
    }

    long getContentLength() throws IOException {
        return -1L;
    }
}

