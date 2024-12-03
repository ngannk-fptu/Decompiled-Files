/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.EndDocument;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface XMLSecEndDocument
extends XMLSecEvent,
EndDocument {
    public XMLSecEndDocument asEndEndDocument();
}

