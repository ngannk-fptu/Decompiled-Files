/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.BCodec;
import org.apache.commons.codec.net.QCodec;

public class Rfc2047Helper {
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static String encode(String value) {
        return Rfc2047Helper.encode(value, DEFAULT_CHARSET, Codec.B);
    }

    public static String encode(String value, String charset) {
        return Rfc2047Helper.encode(value, charset, Codec.B);
    }

    public static String encode(String value, String charset, Codec codec) {
        if (value == null) {
            return null;
        }
        try {
            switch (codec) {
                case Q: {
                    return new QCodec(charset).encode(value);
                }
            }
            return new BCodec(charset).encode(value);
        }
        catch (Exception e) {
            return value;
        }
    }

    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return new BCodec().decode(value);
        }
        catch (DecoderException de) {
            try {
                return new QCodec().decode(value);
            }
            catch (Exception ex) {
                return value;
            }
        }
        catch (Exception e) {
            return value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Codec {
        B,
        Q;

    }
}

