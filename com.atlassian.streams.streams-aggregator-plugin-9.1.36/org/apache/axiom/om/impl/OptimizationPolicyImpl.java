/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl;

import java.io.IOException;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class OptimizationPolicyImpl
implements OptimizationPolicy {
    private static final Log log = LogFactory.getLog(OptimizationPolicyImpl.class);
    private final OMOutputFormat format;
    private static final int UNSUPPORTED = -1;
    private static final int EXCEED_LIMIT = 1;

    public OptimizationPolicyImpl(OMOutputFormat format) {
        this.format = format;
    }

    public boolean isOptimized(DataHandler dataHandler, boolean optimize) {
        if (!optimize) {
            return false;
        }
        log.debug((Object)"Start MTOMXMLStreamWriter.isOptimizedThreshold()");
        int optimized = -1;
        if (dataHandler != null) {
            log.debug((Object)"DataHandler fetched, starting optimized Threshold processing");
            optimized = BufferUtils.doesDataHandlerExceedLimit(dataHandler, this.format.getOptimizedThreshold());
        }
        if (optimized == -1 || optimized == 1) {
            log.debug((Object)"node should be added to binart NodeList for optimization");
            return true;
        }
        return false;
    }

    public boolean isOptimized(DataHandlerProvider dataHandlerProvider, boolean optimize) throws IOException {
        if (!optimize) {
            return false;
        }
        if (this.format.getOptimizedThreshold() == 0) {
            return true;
        }
        return this.isOptimized(dataHandlerProvider.getDataHandler(), optimize);
    }
}

