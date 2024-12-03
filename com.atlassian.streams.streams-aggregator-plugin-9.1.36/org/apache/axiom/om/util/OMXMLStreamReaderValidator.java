/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.util;

import javax.activation.DataHandler;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.util.stax.debug.XMLStreamReaderValidator;

public class OMXMLStreamReaderValidator
extends XMLStreamReaderValidator
implements OMXMLStreamReader {
    public OMXMLStreamReaderValidator(OMXMLStreamReader delegate, boolean throwExceptions) {
        super(delegate, throwExceptions);
    }

    public DataHandler getDataHandler(String blobcid) {
        return ((OMXMLStreamReader)this.getParent()).getDataHandler(blobcid);
    }

    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)this.getParent()).isInlineMTOM();
    }

    public void setInlineMTOM(boolean value) {
        ((OMXMLStreamReader)this.getParent()).setInlineMTOM(value);
    }
}

