/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.List;
import java.util.Map;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;

public interface DocumentContext {
    public String getEncoding();

    public String getBaseURI();

    public void setIsInEncryptedContent(int var1, Object var2);

    public void unsetIsInEncryptedContent(Object var1);

    public boolean isInEncryptedContent();

    public void setIsInSignedContent(int var1, Object var2);

    public void unsetIsInSignedContent(Object var1);

    public boolean isInSignedContent();

    public List<XMLSecurityConstants.ContentType> getProtectionOrder();

    public Map<Integer, XMLSecurityConstants.ContentType> getContentTypeMap();
}

