/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.ParseException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.james.mime4j.MimeException
 *  org.apache.james.mime4j.stream.EntityState
 *  org.apache.james.mime4j.stream.Field
 *  org.apache.james.mime4j.stream.MimeConfig
 *  org.apache.james.mime4j.stream.MimeTokenStream
 *  org.apache.james.mime4j.stream.RecursionMode
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import org.apache.axiom.attachments.AttachmentsDelegate;
import org.apache.axiom.attachments.IncomingAttachmentStreams;
import org.apache.axiom.attachments.MultipartAttachmentStreams;
import org.apache.axiom.attachments.Part;
import org.apache.axiom.attachments.PartImpl;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.LifecycleManagerImpl;
import org.apache.axiom.mime.Header;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.DetachableInputStream;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.MimeTokenStream;
import org.apache.james.mime4j.stream.RecursionMode;

class MIMEMessage
extends AttachmentsDelegate {
    private static final Log log = LogFactory.getLog(MIMEMessage.class);
    private final ContentType contentType;
    private final int contentLength;
    private final DetachableInputStream filterIS;
    private final MimeTokenStream parser;
    private final Map attachmentsMap = new LinkedHashMap();
    private int partIndex = 0;
    private PartImpl currentPart;
    private IncomingAttachmentStreams streams;
    private boolean streamsRequested;
    private boolean partsRequested;
    private String firstPartId;
    private final boolean fileCacheEnable;
    private final String attachmentRepoDir;
    private final int fileStorageThreshold;
    private LifecycleManager manager;

    MIMEMessage(LifecycleManager manager, InputStream inStream, String contentTypeString, boolean fileCacheEnable, String attachmentRepoDir, int fileStorageThreshold, int contentLength) throws OMException {
        this.manager = manager;
        this.contentLength = contentLength;
        this.attachmentRepoDir = attachmentRepoDir;
        this.fileCacheEnable = fileCacheEnable;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Attachments contentLength=" + contentLength + ", contentTypeString=" + contentTypeString));
        }
        this.fileStorageThreshold = fileStorageThreshold;
        try {
            this.contentType = new ContentType(contentTypeString);
        }
        catch (ParseException e) {
            throw new OMException("Invalid Content Type Field in the Mime Message", e);
        }
        InputStream is = inStream;
        if (contentLength <= 0) {
            this.filterIS = new DetachableInputStream(inStream);
            is = this.filterIS;
        } else {
            this.filterIS = null;
        }
        MimeConfig config = new MimeConfig();
        config.setStrictParsing(true);
        this.parser = new MimeTokenStream(config);
        this.parser.setRecursionMode(RecursionMode.M_NO_RECURSE);
        this.parser.parseHeadless(is, contentTypeString);
        while (this.parser.getState() != EntityState.T_START_BODYPART) {
            try {
                this.parser.next();
            }
            catch (IOException ex) {
                throw new OMException(ex);
            }
            catch (MimeException ex) {
                throw new OMException(ex);
            }
        }
        this.getDataHandler(this.getRootPartContentID());
        this.partsRequested = false;
    }

    ContentType getContentType() {
        return this.contentType;
    }

    LifecycleManager getLifecycleManager() {
        if (this.manager == null) {
            this.manager = new LifecycleManagerImpl();
        }
        return this.manager;
    }

    void setLifecycleManager(LifecycleManager manager) {
        this.manager = manager;
    }

    DataHandler getDataHandler(String contentID) {
        do {
            DataHandler dataHandler;
            if ((dataHandler = (DataHandler)this.attachmentsMap.get(contentID)) == null) continue;
            return dataHandler;
        } while (this.getNextPartDataHandler() != null);
        return null;
    }

    void addDataHandler(String contentID, DataHandler dataHandler) {
        this.attachmentsMap.put(contentID, dataHandler);
    }

    void removeDataHandler(String blobContentID) {
        do {
            if (this.attachmentsMap.remove(blobContentID) == null) continue;
            return;
        } while (this.getNextPartDataHandler() != null);
    }

    InputStream getRootPartInputStream(boolean preserve) throws OMException {
        try {
            DataHandler dh = this.getDataHandler(this.getRootPartContentID());
            if (dh == null) {
                throw new OMException("Mandatory root MIME part is missing");
            }
            if (!preserve && dh instanceof DataHandlerExt) {
                return ((DataHandlerExt)dh).readOnce();
            }
            return dh.getInputStream();
        }
        catch (IOException e) {
            throw new OMException("Problem with DataHandler of the Root Mime Part. ", e);
        }
    }

    String getRootPartContentID() {
        String rootContentID = this.contentType.getParameter("start");
        if (log.isDebugEnabled()) {
            log.debug((Object)("getRootPartContentID rootContentID=" + rootContentID));
        }
        if (rootContentID == null) {
            if (this.partIndex == 0) {
                this.getNextPartDataHandler();
            }
            rootContentID = this.firstPartId;
        } else if ((rootContentID = rootContentID.trim()).indexOf("<") > -1 & rootContentID.indexOf(">") > -1) {
            rootContentID = rootContentID.substring(1, rootContentID.length() - 1);
        }
        if (rootContentID.length() > 4 && "cid:".equalsIgnoreCase(rootContentID.substring(0, 4))) {
            rootContentID = rootContentID.substring(4);
        }
        return rootContentID;
    }

    String getRootPartContentType() {
        String rootPartContentID = this.getRootPartContentID();
        if (rootPartContentID == null) {
            throw new OMException("Unable to determine the content ID of the root part");
        }
        DataHandler rootPart = this.getDataHandler(rootPartContentID);
        if (rootPart == null) {
            throw new OMException("Unable to locate the root part; content ID was " + rootPartContentID);
        }
        return rootPart.getContentType();
    }

    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        if (this.partsRequested) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        this.streamsRequested = true;
        if (this.streams == null) {
            this.streams = new MultipartAttachmentStreams(this.parser);
        }
        return this.streams;
    }

    private void fetchAllParts() {
        while (this.getNextPartDataHandler() != null) {
        }
    }

    Set getContentIDs(boolean fetchAll) {
        if (fetchAll) {
            this.fetchAllParts();
        }
        return this.attachmentsMap.keySet();
    }

    Map getMap() {
        this.fetchAllParts();
        return Collections.unmodifiableMap(this.attachmentsMap);
    }

    long getContentLength() throws IOException {
        if (this.contentLength > 0) {
            return this.contentLength;
        }
        this.fetchAllParts();
        return this.filterIS.length();
    }

    private DataHandler getNextPartDataHandler() throws OMException {
        if (this.currentPart != null) {
            this.currentPart.fetch();
            this.currentPart = null;
        }
        if (this.parser.getState() == EntityState.T_END_MULTIPART) {
            return null;
        }
        Part nextPart = this.getPart();
        String partContentID = nextPart.getContentID();
        if (partContentID == null & this.partIndex == 1) {
            String id;
            this.firstPartId = id = "firstPart_" + UIDGenerator.generateContentId();
            DataHandler dataHandler = nextPart.getDataHandler();
            this.addDataHandler(id, dataHandler);
            return dataHandler;
        }
        if (partContentID == null) {
            throw new OMException("Part content ID cannot be blank for non root MIME parts");
        }
        if (partContentID.indexOf("<") > -1 & partContentID.indexOf(">") > -1) {
            partContentID = partContentID.substring(1, partContentID.length() - 1);
        }
        if (this.partIndex == 1) {
            this.firstPartId = partContentID;
        }
        if (this.attachmentsMap.containsKey(partContentID)) {
            throw new OMException("Two MIME parts with the same Content-ID not allowed.");
        }
        DataHandler dataHandler = nextPart.getDataHandler();
        this.addDataHandler(partContentID, dataHandler);
        return dataHandler;
    }

    private Part getPart() throws OMException {
        if (this.streamsRequested) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        this.partsRequested = true;
        boolean isRootPart = this.partIndex == 0;
        try {
            List headers = this.readHeaders();
            ++this.partIndex;
            this.currentPart = new PartImpl(this, isRootPart, headers, this.parser);
            return this.currentPart;
        }
        catch (IOException ex) {
            throw new OMException(ex);
        }
        catch (MimeException ex) {
            throw new OMException(ex);
        }
    }

    int getThreshold() {
        return this.fileCacheEnable ? this.fileStorageThreshold : 0;
    }

    String getAttachmentRepoDir() {
        return this.attachmentRepoDir;
    }

    int getContentLengthIfKnown() {
        return this.contentLength;
    }

    private List readHeaders() throws IOException, MimeException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"readHeaders");
        }
        MIMEMessage.checkParserState(this.parser.next(), EntityState.T_START_HEADER);
        ArrayList<Header> headers = new ArrayList<Header>();
        while (this.parser.next() == EntityState.T_FIELD) {
            Field field = this.parser.getField();
            String name = field.getName();
            String value = field.getBody();
            if (log.isDebugEnabled()) {
                log.debug((Object)("addHeader: (" + name + ") value=(" + value + ")"));
            }
            headers.add(new Header(name, value));
        }
        MIMEMessage.checkParserState(this.parser.next(), EntityState.T_BODY);
        return headers;
    }

    private static void checkParserState(EntityState state, EntityState expected) throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException("Internal error: expected parser to be in state " + expected + ", but got " + state);
        }
    }
}

