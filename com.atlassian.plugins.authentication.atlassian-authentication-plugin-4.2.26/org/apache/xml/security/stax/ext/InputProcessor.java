/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface InputProcessor {
    public void addBeforeProcessor(Object var1);

    public Set<Object> getBeforeProcessors();

    public void addAfterProcessor(Object var1);

    public Set<Object> getAfterProcessors();

    public XMLSecurityConstants.Phase getPhase();

    public XMLSecEvent processHeaderEvent(InputProcessorChain var1) throws XMLStreamException, XMLSecurityException;

    public XMLSecEvent processEvent(InputProcessorChain var1) throws XMLStreamException, XMLSecurityException;

    public void doFinal(InputProcessorChain var1) throws XMLStreamException, XMLSecurityException;
}

