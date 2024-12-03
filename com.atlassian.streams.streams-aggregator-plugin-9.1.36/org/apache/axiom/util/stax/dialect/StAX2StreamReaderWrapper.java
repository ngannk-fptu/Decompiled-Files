/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.codehaus.stax2.DTDInfo;

class StAX2StreamReaderWrapper
extends XMLStreamReaderWrapper
implements DTDReader {
    public StAX2StreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        }
        return super.getProperty(name);
    }

    public String getRootName() {
        return ((DTDInfo)((Object)this.getParent())).getDTDRootName();
    }

    public String getPublicId() {
        return ((DTDInfo)((Object)this.getParent())).getDTDPublicId();
    }

    public String getSystemId() {
        return ((DTDInfo)((Object)this.getParent())).getDTDSystemId();
    }
}

