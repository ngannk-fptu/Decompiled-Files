/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public class XMLStreamIOException
extends IOException {
    private static final long serialVersionUID = -2209565480803762583L;

    public XMLStreamIOException(XMLStreamException cause) {
        this.initCause(cause);
    }

    public XMLStreamException getXMLStreamException() {
        return (XMLStreamException)this.getCause();
    }
}

