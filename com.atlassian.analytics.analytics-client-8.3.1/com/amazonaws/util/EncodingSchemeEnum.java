/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.Base16;
import com.amazonaws.util.Base32;
import com.amazonaws.util.Base64;
import com.amazonaws.util.EncodingScheme;

public enum EncodingSchemeEnum implements EncodingScheme
{
    BASE16{

        @Override
        public String encodeAsString(byte[] bytes) {
            return Base16.encodeAsString(bytes);
        }

        @Override
        public byte[] decode(String encoded) {
            return Base16.decode(encoded);
        }
    }
    ,
    BASE32{

        @Override
        public String encodeAsString(byte[] bytes) {
            return Base32.encodeAsString(bytes);
        }

        @Override
        public byte[] decode(String encoded) {
            return Base32.decode(encoded);
        }
    }
    ,
    BASE64{

        @Override
        public String encodeAsString(byte[] bytes) {
            return Base64.encodeAsString(bytes);
        }

        @Override
        public byte[] decode(String encoded) {
            return Base64.decode(encoded);
        }
    };


    @Override
    public abstract String encodeAsString(byte[] var1);
}

