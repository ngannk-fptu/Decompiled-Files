/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.services;

import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.services.RevocationData;

public interface TimeStampService {
    public byte[] timeStamp(SignatureInfo var1, byte[] var2, RevocationData var3) throws Exception;
}

