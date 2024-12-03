/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.services;

import java.security.cert.X509Certificate;
import java.util.List;
import org.apache.poi.poifs.crypt.dsig.services.RevocationData;

public interface RevocationDataService {
    public RevocationData getRevocationData(List<X509Certificate> var1);
}

