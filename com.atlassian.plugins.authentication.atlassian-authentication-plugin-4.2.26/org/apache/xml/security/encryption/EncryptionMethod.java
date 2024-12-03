/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface EncryptionMethod {
    public String getAlgorithm();

    public int getKeySize();

    public void setKeySize(int var1);

    public byte[] getOAEPparams();

    public void setOAEPparams(byte[] var1);

    public void setDigestAlgorithm(String var1);

    public String getDigestAlgorithm();

    public void setMGFAlgorithm(String var1);

    public String getMGFAlgorithm();

    public Iterator<Element> getEncryptionMethodInformation();

    public void addEncryptionMethodInformation(Element var1);

    public void removeEncryptionMethodInformation(Element var1);
}

