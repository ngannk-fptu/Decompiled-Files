/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments;

import java.io.InputStream;
import org.apache.axiom.attachments.PartContent;
import org.apache.axiom.attachments.PartContentOnFile;
import org.apache.axiom.attachments.PartContentOnMemory;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class PartContentFactory {
    private static final Log log = LogFactory.getLog(PartContentFactory.class);

    PartContentFactory() {
    }

    static PartContent createPartContent(LifecycleManager manager, InputStream in, boolean isRootPart, int thresholdSize, String attachmentDir, int messageContentLength) throws OMException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start createPart()");
            log.debug((Object)("  isRootPart=" + isRootPart));
            log.debug((Object)("  thresholdSize= " + thresholdSize));
            log.debug((Object)("  attachmentDir=" + attachmentDir));
            log.debug((Object)("  messageContentLength " + messageContentLength));
        }
        try {
            if (isRootPart || thresholdSize <= 0 || messageContentLength > 0 && messageContentLength < thresholdSize) {
                BAAOutputStream baaos = new BAAOutputStream();
                BufferUtils.inputStream2OutputStream(in, baaos);
                return new PartContentOnMemory(baaos.buffers(), baaos.length());
            }
            BAAOutputStream baaos = new BAAOutputStream();
            int count = BufferUtils.inputStream2OutputStream(in, baaos, thresholdSize);
            if (count < thresholdSize) {
                return new PartContentOnMemory(baaos.buffers(), baaos.length());
            }
            BAAInputStream baais = new BAAInputStream(baaos.buffers(), baaos.length());
            return new PartContentOnFile(manager, baais, in, attachmentDir);
        }
        catch (StreamCopyException ex) {
            if (ex.getOperation() == 1) {
                throw new OMException("Failed to fetch the MIME part content", ex.getCause());
            }
            throw new OMException("Failed to write the MIME part content to temporary storage", ex.getCause());
        }
        catch (Exception e) {
            throw new OMException(e);
        }
    }
}

