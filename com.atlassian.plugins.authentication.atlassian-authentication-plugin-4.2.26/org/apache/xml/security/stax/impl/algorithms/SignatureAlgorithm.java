/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.algorithms;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.exceptions.XMLSecurityException;

public interface SignatureAlgorithm {
    public void engineUpdate(byte[] var1) throws XMLSecurityException;

    public void engineUpdate(byte var1) throws XMLSecurityException;

    public void engineUpdate(byte[] var1, int var2, int var3) throws XMLSecurityException;

    public void engineInitSign(Key var1) throws XMLSecurityException;

    public void engineInitSign(Key var1, SecureRandom var2) throws XMLSecurityException;

    public void engineInitSign(Key var1, AlgorithmParameterSpec var2) throws XMLSecurityException;

    public byte[] engineSign() throws XMLSecurityException;

    public void engineInitVerify(Key var1) throws XMLSecurityException;

    public boolean engineVerify(byte[] var1) throws XMLSecurityException;

    public void engineSetParameter(AlgorithmParameterSpec var1) throws XMLSecurityException;
}

