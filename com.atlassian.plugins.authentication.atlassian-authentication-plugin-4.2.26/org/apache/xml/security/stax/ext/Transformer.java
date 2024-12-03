/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public interface Transformer {
    public void setOutputStream(OutputStream var1) throws XMLSecurityException;

    public void setTransformer(Transformer var1) throws XMLSecurityException;

    public void setProperties(Map<String, Object> var1) throws XMLSecurityException;

    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod var1);

    public void transform(XMLSecEvent var1) throws XMLStreamException;

    public void transform(InputStream var1) throws XMLStreamException;

    public void doFinal() throws XMLStreamException;
}

