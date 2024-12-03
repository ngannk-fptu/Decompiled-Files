/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.builder;

import java.io.IOException;
import javax.activation.DataHandler;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.util.stax.xop.MimePartProvider;

public class OMAttachmentAccessorMimePartProvider
implements MimePartProvider {
    private final OMAttachmentAccessor attachments;

    public OMAttachmentAccessorMimePartProvider(OMAttachmentAccessor attachments) {
        this.attachments = attachments;
    }

    public boolean isLoaded(String contentID) {
        return false;
    }

    public DataHandler getDataHandler(String contentID) throws IOException {
        DataHandler dh = this.attachments.getDataHandler(contentID);
        if (dh == null) {
            throw new IllegalArgumentException("No attachment found for content ID '" + contentID + "'");
        }
        return dh;
    }
}

