/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMAttachmentAccessor;

public interface OMXMLStreamReader
extends XMLStreamReader,
OMAttachmentAccessor {
    public boolean isInlineMTOM();

    public void setInlineMTOM(boolean var1);
}

