/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.apache.xml.security.keys.KeyInfo;
import org.w3c.dom.Element;

public interface AgreementMethod {
    public byte[] getKANonce();

    public void setKANonce(byte[] var1);

    public Iterator<Element> getAgreementMethodInformation();

    public void addAgreementMethodInformation(Element var1);

    public void revoveAgreementMethodInformation(Element var1);

    public KeyInfo getOriginatorKeyInfo();

    public void setOriginatorKeyInfo(KeyInfo var1);

    public KeyInfo getRecipientKeyInfo();

    public void setRecipientKeyInfo(KeyInfo var1);

    public String getAlgorithm();
}

