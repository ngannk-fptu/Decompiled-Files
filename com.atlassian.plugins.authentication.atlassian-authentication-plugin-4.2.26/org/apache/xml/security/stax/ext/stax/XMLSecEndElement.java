/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.EndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface XMLSecEndElement
extends XMLSecEvent,
EndElement {
    @Override
    public XMLSecEndElement asEndElement();
}

