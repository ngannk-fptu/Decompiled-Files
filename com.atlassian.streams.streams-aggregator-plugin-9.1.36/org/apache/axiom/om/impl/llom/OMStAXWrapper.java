/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.stream.util.StreamReaderDelegate;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;
import org.apache.axiom.om.impl.llom.SwitchingWrapper;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class OMStAXWrapper
extends StreamReaderDelegate
implements OMXMLStreamReaderEx {
    private static final Log log = LogFactory.getLog(OMStAXWrapper.class);
    private final SwitchingWrapper switchingWrapper;
    private XOPEncodingStreamReader xopEncoder;

    public OMStAXWrapper(OMXMLParserWrapper builder, OMContainer startNode) {
        this(builder, startNode, false, false);
    }

    public OMStAXWrapper(OMXMLParserWrapper builder, OMContainer startNode, boolean cache, boolean preserveNamespaceContext) {
        this.switchingWrapper = new SwitchingWrapper(builder, startNode, cache, preserveNamespaceContext);
        this.setParent(this.switchingWrapper);
    }

    public boolean isInlineMTOM() {
        return this.xopEncoder == null;
    }

    public void setInlineMTOM(boolean value) {
        if (value) {
            if (this.xopEncoder != null) {
                this.xopEncoder = null;
                this.setParent(this.switchingWrapper);
            }
        } else if (this.xopEncoder == null) {
            this.xopEncoder = new XOPEncodingStreamReader(this.switchingWrapper, ContentIDGenerator.DEFAULT, OptimizationPolicy.ALL);
            this.setParent(this.xopEncoder);
        }
    }

    public DataHandler getDataHandler(String contentID) {
        if (contentID.startsWith("cid:")) {
            log.warn((Object)"Invalid usage of OMStAXWrapper#getDataHandler(String): the argument must be a content ID, not an href; see OMAttachmentAccessor.");
            contentID = contentID.substring(4);
        }
        if (this.xopEncoder == null) {
            throw new IllegalStateException("The wrapper is in inlineMTOM=true mode");
        }
        if (this.xopEncoder.getContentIDs().contains(contentID)) {
            try {
                return this.xopEncoder.getDataHandler(contentID);
            }
            catch (IOException ex) {
                throw new OMException(ex);
            }
        }
        return null;
    }

    public boolean isClosed() {
        return this.switchingWrapper.isClosed();
    }

    public void releaseParserOnClose(boolean value) {
        this.switchingWrapper.releaseParserOnClose(value);
    }

    public OMDataSource getDataSource() {
        return this.switchingWrapper.getDataSource();
    }

    public void enableDataSourceEvents(boolean value) {
        this.switchingWrapper.enableDataSourceEvents(value);
    }
}

