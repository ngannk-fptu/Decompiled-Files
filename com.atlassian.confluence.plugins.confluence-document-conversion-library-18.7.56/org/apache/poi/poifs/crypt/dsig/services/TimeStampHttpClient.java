/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.services;

import java.io.IOException;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;

public interface TimeStampHttpClient {
    public void init(SignatureConfig var1);

    public void setContentTypeIn(String var1);

    public void setContentTypeOut(String var1);

    public void setBasicAuthentication(String var1, String var2);

    public TimeStampHttpClientResponse post(String var1, byte[] var2) throws IOException;

    public TimeStampHttpClientResponse get(String var1) throws IOException;

    public boolean isIgnoreHttpsCertificates();

    public void setIgnoreHttpsCertificates(boolean var1);

    public boolean isFollowRedirects();

    public void setFollowRedirects(boolean var1);

    public static interface TimeStampHttpClientResponse {
        default public boolean isOK() {
            return this.getResponseCode() == 200;
        }

        public int getResponseCode();

        public byte[] getResponseBytes();
    }
}

