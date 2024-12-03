/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface EncryptionProperty {
    public String getTarget();

    public void setTarget(String var1);

    public String getId();

    public void setId(String var1);

    public String getAttribute(String var1);

    public void setAttribute(String var1, String var2);

    public Iterator<Element> getEncryptionInformation();

    public void addEncryptionInformation(Element var1);

    public void removeEncryptionInformation(Element var1);
}

