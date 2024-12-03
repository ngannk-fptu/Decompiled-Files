/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.dialect.XLXPStreamReaderWrapper;

class XLXP1StreamReaderWrapper
extends XLXPStreamReaderWrapper {
    public XLXP1StreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    public String getEncoding() {
        String encoding = super.getEncoding();
        return encoding == null || encoding.length() == 0 ? null : encoding;
    }
}

