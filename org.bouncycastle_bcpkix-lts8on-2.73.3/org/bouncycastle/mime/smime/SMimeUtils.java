/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mime.smime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.util.Strings;

class SMimeUtils {
    private static final Map RFC5751_MICALGS;
    private static final Map RFC3851_MICALGS;
    private static final Map STANDARD_MICALGS;
    private static final Map forMic;
    private static final byte[] nl;

    SMimeUtils() {
    }

    static String lessQuotes(String in) {
        if (in == null || in.length() <= 1) {
            return in;
        }
        if (in.charAt(0) == '\"' && in.charAt(in.length() - 1) == '\"') {
            return in.substring(1, in.length() - 1);
        }
        return in;
    }

    static String getParameter(String startsWith, List<String> parameters) {
        for (String param : parameters) {
            if (!param.startsWith(startsWith)) continue;
            return param;
        }
        return null;
    }

    static ASN1ObjectIdentifier getDigestOID(String alg) {
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)forMic.get(Strings.toLowerCase((String)alg));
        if (oid == null) {
            throw new IllegalArgumentException("unknown micalg passed: " + alg);
        }
        return oid;
    }

    static InputStream autoBuffer(InputStream input) {
        if (input instanceof FileInputStream) {
            return new BufferedInputStream(input);
        }
        return input;
    }

    static OutputStream autoBuffer(OutputStream output) {
        if (output instanceof FileOutputStream) {
            return new BufferedOutputStream(output);
        }
        return output;
    }

    static OutputStream createUnclosable(OutputStream destination) {
        return new FilterOutputStream(destination){

            @Override
            public void write(byte[] buf, int off, int len) throws IOException {
                if (buf == null) {
                    throw new NullPointerException();
                }
                if ((off | len | buf.length - (len + off) | off + len) < 0) {
                    throw new IndexOutOfBoundsException();
                }
                this.out.write(buf, off, len);
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    static {
        nl = new byte[2];
        SMimeUtils.nl[0] = 13;
        SMimeUtils.nl[1] = 10;
        HashMap<ASN1ObjectIdentifier, String> stdMicAlgs = new HashMap<ASN1ObjectIdentifier, String>();
        stdMicAlgs.put(CMSAlgorithm.MD5, "md5");
        stdMicAlgs.put(CMSAlgorithm.SHA1, "sha-1");
        stdMicAlgs.put(CMSAlgorithm.SHA224, "sha-224");
        stdMicAlgs.put(CMSAlgorithm.SHA256, "sha-256");
        stdMicAlgs.put(CMSAlgorithm.SHA384, "sha-384");
        stdMicAlgs.put(CMSAlgorithm.SHA512, "sha-512");
        stdMicAlgs.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        stdMicAlgs.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        stdMicAlgs.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC5751_MICALGS = Collections.unmodifiableMap(stdMicAlgs);
        HashMap<ASN1ObjectIdentifier, String> oldMicAlgs = new HashMap<ASN1ObjectIdentifier, String>();
        oldMicAlgs.put(CMSAlgorithm.MD5, "md5");
        oldMicAlgs.put(CMSAlgorithm.SHA1, "sha1");
        oldMicAlgs.put(CMSAlgorithm.SHA224, "sha224");
        oldMicAlgs.put(CMSAlgorithm.SHA256, "sha256");
        oldMicAlgs.put(CMSAlgorithm.SHA384, "sha384");
        oldMicAlgs.put(CMSAlgorithm.SHA512, "sha512");
        oldMicAlgs.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        oldMicAlgs.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        oldMicAlgs.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC3851_MICALGS = Collections.unmodifiableMap(oldMicAlgs);
        STANDARD_MICALGS = RFC5751_MICALGS;
        TreeMap<String, ASN1ObjectIdentifier> mic = new TreeMap<String, ASN1ObjectIdentifier>(String.CASE_INSENSITIVE_ORDER);
        for (Object key : STANDARD_MICALGS.keySet()) {
            mic.put(STANDARD_MICALGS.get(key).toString(), (ASN1ObjectIdentifier)key);
        }
        for (Object key : RFC3851_MICALGS.keySet()) {
            mic.put(RFC3851_MICALGS.get(key).toString(), (ASN1ObjectIdentifier)key);
        }
        forMic = Collections.unmodifiableMap(mic);
    }
}

