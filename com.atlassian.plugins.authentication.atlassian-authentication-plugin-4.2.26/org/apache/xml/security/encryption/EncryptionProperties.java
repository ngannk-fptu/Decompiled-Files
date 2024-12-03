/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.apache.xml.security.encryption.EncryptionProperty;

public interface EncryptionProperties {
    public String getId();

    public void setId(String var1);

    public Iterator<EncryptionProperty> getEncryptionProperties();

    public void addEncryptionProperty(EncryptionProperty var1);

    public void removeEncryptionProperty(EncryptionProperty var1);
}

