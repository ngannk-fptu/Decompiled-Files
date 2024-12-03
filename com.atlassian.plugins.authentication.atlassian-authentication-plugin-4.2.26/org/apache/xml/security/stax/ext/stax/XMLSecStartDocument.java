/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext.stax;

import javax.xml.stream.events.StartDocument;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface XMLSecStartDocument
extends XMLSecEvent,
StartDocument {
    public XMLSecStartDocument asStartDocument();
}

