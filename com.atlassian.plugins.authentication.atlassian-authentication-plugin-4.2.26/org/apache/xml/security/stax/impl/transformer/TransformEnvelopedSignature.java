/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.transformer;

import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;

public class TransformEnvelopedSignature
extends TransformIdentity {
    private int curLevel;
    private int sigElementLevel = -1;

    @Override
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent: {
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            }
            case InputStream: {
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            }
        }
        throw new IllegalArgumentException("Unsupported class " + forInput.name());
    }

    @Override
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        if (1 == xmlSecEvent.getEventType()) {
            ++this.curLevel;
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
            if (XMLSecurityConstants.TAG_dsig_Signature.equals(xmlSecStartElement.getName())) {
                this.sigElementLevel = this.curLevel;
                return;
            }
        } else if (2 == xmlSecEvent.getEventType()) {
            XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
            if (this.sigElementLevel == this.curLevel && XMLSecurityConstants.TAG_dsig_Signature.equals(xmlSecEndElement.getName())) {
                this.sigElementLevel = -1;
                return;
            }
            --this.curLevel;
        }
        if (this.sigElementLevel == -1) {
            super.transform(xmlSecEvent);
        }
    }

    @Override
    public void transform(InputStream inputStream) throws XMLStreamException {
        throw new UnsupportedOperationException("transform(InputStream) not supported");
    }
}

