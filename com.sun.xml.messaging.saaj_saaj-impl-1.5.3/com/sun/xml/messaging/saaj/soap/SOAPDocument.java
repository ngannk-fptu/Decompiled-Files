/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;

public interface SOAPDocument {
    public SOAPPartImpl getSOAPPart();

    public SOAPDocumentImpl getDocument();
}

