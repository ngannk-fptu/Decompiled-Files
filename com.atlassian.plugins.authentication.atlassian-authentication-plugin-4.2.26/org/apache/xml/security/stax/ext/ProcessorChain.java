/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;

public interface ProcessorChain {
    public void reset();

    public void doFinal() throws XMLStreamException, XMLSecurityException;
}

