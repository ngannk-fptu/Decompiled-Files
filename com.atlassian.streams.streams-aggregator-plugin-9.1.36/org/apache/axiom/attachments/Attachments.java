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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.mail.internet.ContentType;
import org.apache.axiom.attachments.AttachmentSet;
import org.apache.axiom.attachments.AttachmentsDelegate;
import org.apache.axiom.attachments.IncomingAttachmentStreams;
import org.apache.axiom.attachments.MIMEMessage;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;

public class Attachments
implements OMAttachmentAccessor {
    private final AttachmentsDelegate delegate;
    private String applicationType;

    public LifecycleManager getLifecycleManager() {
        return this.delegate.getLifecycleManager();
    }

    public void setLifecycleManager(LifecycleManager manager) {
        this.delegate.setLifecycleManager(manager);
    }

    public Attachments(LifecycleManager manager, InputStream inStream, String contentTypeString, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold) throws OMException {
        this(manager, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
    }

    public Attachments(LifecycleManager manager, InputStream inStream, String contentTypeString, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, int contentLength) throws OMException {
        int fileStorageThreshold = fileThreshold != null && !"".equals(fileThreshold) ? Integer.parseInt(fileThreshold) : 1;
        this.delegate = new MIMEMessage(manager, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileStorageThreshold, contentLength);
    }

    public Attachments(InputStream inStream, String contentTypeString, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold) throws OMException {
        this(null, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
    }

    public Attachments(InputStream inStream, String contentTypeString, boolean fileCacheEnable, String attachmentRepoDir, String fileThreshold, int contentLength) throws OMException {
        this(null, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, contentLength);
    }

    public Attachments(InputStream inStream, String contentTypeString) throws OMException {
        this(null, inStream, contentTypeString, false, null, null);
    }

    public Attachments() {
        this.delegate = new AttachmentSet();
    }

    public String getAttachmentSpecType() {
        if (this.applicationType == null) {
            ContentType contentType = this.delegate.getContentType();
            if (contentType == null) {
                throw new OMException("Unable to determine the attachment spec type because the Attachments object doesn't have a known content type");
            }
            this.applicationType = contentType.getParameter("type");
            if ("application/xop+xml".equalsIgnoreCase(this.applicationType)) {
                this.applicationType = "application/xop+xml";
            } else if ("text/xml".equalsIgnoreCase(this.applicationType)) {
                this.applicationType = "text/xml";
            } else if ("application/soap+xml".equalsIgnoreCase(this.applicationType)) {
                this.applicationType = "application/soap+xml";
            } else {
                throw new OMException("Invalid Application type. Support available for MTOM & SwA only.");
            }
        }
        return this.applicationType;
    }

    public DataHandler getDataHandler(String contentID) {
        return this.delegate.getDataHandler(contentID);
    }

    public void addDataHandler(String contentID, DataHandler dataHandler) {
        this.delegate.addDataHandler(contentID, dataHandler);
    }

    public void removeDataHandler(String blobContentID) {
        this.delegate.removeDataHandler(blobContentID);
    }

    public InputStream getSOAPPartInputStream() throws OMException {
        return this.getRootPartInputStream();
    }

    public String getSOAPPartContentID() {
        return this.getRootPartContentID();
    }

    public String getSOAPPartContentType() {
        return this.getRootPartContentType();
    }

    public InputStream getRootPartInputStream() throws OMException {
        return this.delegate.getRootPartInputStream(true);
    }

    public InputStream getRootPartInputStream(boolean preserve) throws OMException {
        return this.delegate.getRootPartInputStream(preserve);
    }

    public String getRootPartContentID() {
        return this.delegate.getRootPartContentID();
    }

    public String getRootPartContentType() {
        return this.delegate.getRootPartContentType();
    }

    public IncomingAttachmentStreams getIncomingAttachmentStreams() throws IllegalStateException {
        return this.delegate.getIncomingAttachmentStreams();
    }

    public String[] getAllContentIDs() {
        Set cids = this.delegate.getContentIDs(true);
        return cids.toArray(new String[cids.size()]);
    }

    public Set getContentIDSet() {
        return this.delegate.getContentIDs(true);
    }

    public Map getMap() {
        return this.delegate.getMap();
    }

    public List getContentIDList() {
        return new ArrayList(this.delegate.getContentIDs(false));
    }

    public long getContentLength() throws IOException {
        return this.delegate.getContentLength();
    }

    public InputStream getIncomingAttachmentsAsSingleStream() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }
}

