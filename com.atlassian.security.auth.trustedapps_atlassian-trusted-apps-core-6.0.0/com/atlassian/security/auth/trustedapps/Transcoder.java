/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.encoders.Base64
 */
package com.atlassian.security.auth.trustedapps;

import java.io.UnsupportedEncodingException;
import org.bouncycastle.util.encoders.Base64;

interface Transcoder {
    public String encode(byte[] var1);

    public byte[] decode(String var1);

    public byte[] getBytes(String var1);

    public static class Base64Transcoder
    implements Transcoder {
        @Override
        public String encode(byte[] data) {
            try {
                return new String(Base64.encode((byte[])data), "utf-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public byte[] decode(String encoded) {
            return this.decode(this.getBytes(encoded));
        }

        byte[] decode(byte[] encoded) {
            return Base64.decode((byte[])encoded);
        }

        @Override
        public byte[] getBytes(String data) {
            try {
                return data.getBytes("utf-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new AssertionError((Object)e);
            }
        }
    }
}

