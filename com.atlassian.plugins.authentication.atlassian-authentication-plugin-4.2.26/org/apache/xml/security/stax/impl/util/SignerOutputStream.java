/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.impl.util;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignerOutputStream
extends OutputStream {
    protected static final transient Logger LOG = LoggerFactory.getLogger(SignerOutputStream.class);
    protected static final transient boolean isDebugEnabled = LOG.isDebugEnabled();
    private final SignatureAlgorithm signatureAlgorithm;
    private StringBuilder stringBuilder;

    public SignerOutputStream(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        if (isDebugEnabled) {
            this.stringBuilder = new StringBuilder();
        }
    }

    @Override
    public void write(byte[] arg0) {
        this.write(arg0, 0, arg0.length);
    }

    @Override
    public void write(int arg0) {
        try {
            byte asByte = (byte)arg0;
            this.signatureAlgorithm.engineUpdate(asByte);
            if (isDebugEnabled) {
                this.stringBuilder.append((char)asByte);
            }
        }
        catch (XMLSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(byte[] arg0, int arg1, int arg2) {
        try {
            this.signatureAlgorithm.engineUpdate(arg0, arg1, arg2);
            if (isDebugEnabled) {
                this.stringBuilder.append(new String(arg0, arg1, arg2, StandardCharsets.UTF_8));
            }
        }
        catch (XMLSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(byte[] signatureValue) throws XMLSecurityException {
        if (isDebugEnabled) {
            LOG.debug("Pre Signed: ");
            LOG.debug(this.stringBuilder.toString());
            LOG.debug("End pre Signed ");
            this.stringBuilder = new StringBuilder();
        }
        return this.signatureAlgorithm.engineVerify(signatureValue);
    }

    public byte[] sign() throws XMLSecurityException {
        if (isDebugEnabled) {
            LOG.debug("Pre Signed: ");
            LOG.debug(this.stringBuilder.toString());
            LOG.debug("End pre Signed ");
            this.stringBuilder = new StringBuilder();
        }
        return this.signatureAlgorithm.engineSign();
    }
}

