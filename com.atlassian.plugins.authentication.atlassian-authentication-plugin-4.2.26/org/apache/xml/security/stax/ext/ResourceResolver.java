/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.io.InputStream;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public interface ResourceResolver {
    public boolean isSameDocumentReference();

    public boolean matches(XMLSecStartElement var1);

    public InputStream getInputStreamFromExternalReference() throws XMLSecurityException;
}

