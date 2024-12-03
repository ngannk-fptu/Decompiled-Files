/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;

public abstract class XOPEncodingStreamWrapper
implements MimePartProvider {
    private final Map dataHandlerObjects = new LinkedHashMap();
    private final ContentIDGenerator contentIDGenerator;
    private final OptimizationPolicy optimizationPolicy;

    public XOPEncodingStreamWrapper(ContentIDGenerator contentIDGenerator, OptimizationPolicy optimizationPolicy) {
        this.contentIDGenerator = contentIDGenerator;
        this.optimizationPolicy = optimizationPolicy;
    }

    private String addDataHandler(Object dataHandlerObject, String existingContentID) {
        String contentID = this.contentIDGenerator.generateContentID(existingContentID);
        this.dataHandlerObjects.put(contentID, dataHandlerObject);
        return contentID;
    }

    protected String processDataHandler(DataHandler dataHandler, String existingContentID, boolean optimize) throws IOException {
        if (this.optimizationPolicy.isOptimized(dataHandler, optimize)) {
            return this.addDataHandler(dataHandler, existingContentID);
        }
        return null;
    }

    protected String processDataHandler(DataHandlerProvider dataHandlerProvider, String existingContentID, boolean optimize) throws IOException {
        if (this.optimizationPolicy.isOptimized(dataHandlerProvider, optimize)) {
            return this.addDataHandler(dataHandlerProvider, existingContentID);
        }
        return null;
    }

    public Set getContentIDs() {
        return Collections.unmodifiableSet(this.dataHandlerObjects.keySet());
    }

    public boolean isLoaded(String contentID) {
        Object dataHandlerObject = this.dataHandlerObjects.get(contentID);
        if (dataHandlerObject == null) {
            throw new IllegalArgumentException("No DataHandler object found for content ID '" + contentID + "'");
        }
        if (dataHandlerObject instanceof DataHandler) {
            return true;
        }
        return ((DataHandlerProvider)dataHandlerObject).isLoaded();
    }

    public DataHandler getDataHandler(String contentID) throws IOException {
        Object dataHandlerObject = this.dataHandlerObjects.get(contentID);
        if (dataHandlerObject == null) {
            throw new IllegalArgumentException("No DataHandler object found for content ID '" + contentID + "'");
        }
        if (dataHandlerObject instanceof DataHandler) {
            return (DataHandler)dataHandlerObject;
        }
        return ((DataHandlerProvider)dataHandlerObject).getDataHandler();
    }
}

