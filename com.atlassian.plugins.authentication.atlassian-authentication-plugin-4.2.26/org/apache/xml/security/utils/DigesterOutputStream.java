/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils;

import java.io.ByteArrayOutputStream;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigesterOutputStream
extends ByteArrayOutputStream {
    private static final Logger LOG = LoggerFactory.getLogger(DigesterOutputStream.class);
    final MessageDigestAlgorithm mda;

    public DigesterOutputStream(MessageDigestAlgorithm mda) {
        this.mda = mda;
    }

    @Override
    public void write(byte[] arg0) {
        this.write(arg0, 0, arg0.length);
    }

    @Override
    public void write(int arg0) {
        this.mda.update((byte)arg0);
    }

    @Override
    public void write(byte[] arg0, int arg1, int arg2) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Pre-digested input:");
            StringBuilder sb = new StringBuilder(arg2);
            for (int i = arg1; i < arg1 + arg2; ++i) {
                sb.append((char)arg0[i]);
            }
            LOG.debug(sb.toString());
        }
        this.mda.update(arg0, arg1, arg2);
    }

    public byte[] getDigestValue() {
        return this.mda.digest();
    }
}

