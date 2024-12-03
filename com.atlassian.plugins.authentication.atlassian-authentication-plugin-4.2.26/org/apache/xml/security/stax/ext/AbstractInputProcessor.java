/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public abstract class AbstractInputProcessor
implements InputProcessor {
    private final XMLSecurityProperties securityProperties;
    private XMLSecurityConstants.Phase phase = XMLSecurityConstants.Phase.PROCESSING;
    private Set<Object> beforeProcessors;
    private Set<Object> afterProcessors;

    public AbstractInputProcessor(XMLSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public XMLSecurityConstants.Phase getPhase() {
        return this.phase;
    }

    public void setPhase(XMLSecurityConstants.Phase phase) {
        this.phase = phase;
    }

    @Override
    public void addBeforeProcessor(Object processor) {
        this.beforeProcessors = new HashSet<Object>();
        this.beforeProcessors.add(processor);
    }

    @Override
    public Set<Object> getBeforeProcessors() {
        if (this.beforeProcessors == null) {
            return Collections.emptySet();
        }
        return this.beforeProcessors;
    }

    @Override
    public void addAfterProcessor(Object processor) {
        this.afterProcessors = new HashSet<Object>();
        this.afterProcessors.add(processor);
    }

    @Override
    public Set<Object> getAfterProcessors() {
        if (this.afterProcessors == null) {
            return Collections.emptySet();
        }
        return this.afterProcessors;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        inputProcessorChain.doFinal();
    }

    public XMLSecurityProperties getSecurityProperties() {
        return this.securityProperties;
    }

    public Attribute getReferenceIDAttribute(XMLSecStartElement xmlSecStartElement) {
        return xmlSecStartElement.getAttributeByName(this.securityProperties.getIdAttributeNS());
    }
}

