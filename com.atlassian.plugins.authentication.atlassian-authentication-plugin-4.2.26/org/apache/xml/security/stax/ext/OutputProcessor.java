/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface OutputProcessor {
    public void setXMLSecurityProperties(XMLSecurityProperties var1);

    public void setAction(XMLSecurityConstants.Action var1, int var2);

    public XMLSecurityConstants.Action getAction();

    public int getActionOrder();

    public void init(OutputProcessorChain var1) throws XMLSecurityException;

    public void addBeforeProcessor(Class<? extends OutputProcessor> var1);

    public Set<Class<? extends OutputProcessor>> getBeforeProcessors();

    public void addAfterProcessor(Class<? extends OutputProcessor> var1);

    public Set<Class<? extends OutputProcessor>> getAfterProcessors();

    public XMLSecurityConstants.Phase getPhase();

    public void processEvent(XMLSecEvent var1, OutputProcessorChain var2) throws XMLStreamException, XMLSecurityException;

    public void doFinal(OutputProcessorChain var1) throws XMLStreamException, XMLSecurityException;
}

