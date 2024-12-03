/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.Deque;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface XMLSecurityHeaderHandler {
    public void handle(InputProcessorChain var1, XMLSecurityProperties var2, Deque<XMLSecEvent> var3, Integer var4) throws XMLSecurityException;
}

