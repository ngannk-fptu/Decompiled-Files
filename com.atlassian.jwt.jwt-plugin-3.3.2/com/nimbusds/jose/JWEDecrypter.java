/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEProvider;
import com.nimbusds.jose.util.Base64URL;

public interface JWEDecrypter
extends JWEProvider {
    public byte[] decrypt(JWEHeader var1, Base64URL var2, Base64URL var3, Base64URL var4, Base64URL var5) throws JOSEException;
}

