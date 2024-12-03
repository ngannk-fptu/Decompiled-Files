/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.services;

import java.security.cert.X509Certificate;
import java.util.List;
import org.apache.poi.poifs.crypt.dsig.services.RevocationData;

public interface TimeStampServiceValidator {
    public void validate(List<X509Certificate> var1, RevocationData var2) throws Exception;
}

