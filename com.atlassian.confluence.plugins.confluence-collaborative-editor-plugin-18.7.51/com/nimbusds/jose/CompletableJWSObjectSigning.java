/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;

public interface CompletableJWSObjectSigning {
    public Base64URL complete() throws JOSEException;
}

