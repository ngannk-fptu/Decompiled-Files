/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.io.UnsupportedEncodingException;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.util.URI;

public class AnyURIDV
extends TypeValidator {
    private static final URI BASE_URI;
    private static boolean[] gNeedEscaping;
    private static char[] gAfterEscaping1;
    private static char[] gAfterEscaping2;
    private static char[] gHexChs;

    @Override
    public short getAllowedFacets() {
        return 2079;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            if (string.length() != 0) {
                String string2 = AnyURIDV.encode(string);
                new URI(BASE_URI, string2);
            }
        }
        catch (URI.MalformedURIException malformedURIException) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "anyURI"});
        }
        return string;
    }

    private static String encode(String string) {
        int n;
        int n2;
        int n3 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n3 * 3);
        for (n2 = 0; n2 < n3 && (n = string.charAt(n2)) < 128; ++n2) {
            if (gNeedEscaping[n]) {
                stringBuffer.append('%');
                stringBuffer.append(gAfterEscaping1[n]);
                stringBuffer.append(gAfterEscaping2[n]);
                continue;
            }
            stringBuffer.append((char)n);
        }
        if (n2 < n3) {
            byte[] byArray = null;
            try {
                byArray = string.substring(n2).getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                return string;
            }
            n3 = byArray.length;
            for (n2 = 0; n2 < n3; ++n2) {
                byte by = byArray[n2];
                if (by < 0) {
                    n = by + 256;
                    stringBuffer.append('%');
                    stringBuffer.append(gHexChs[n >> 4]);
                    stringBuffer.append(gHexChs[n & 0xF]);
                    continue;
                }
                if (gNeedEscaping[by]) {
                    stringBuffer.append('%');
                    stringBuffer.append(gAfterEscaping1[by]);
                    stringBuffer.append(gAfterEscaping2[by]);
                    continue;
                }
                stringBuffer.append((char)by);
            }
        }
        if (stringBuffer.length() != n3) {
            return stringBuffer.toString();
        }
        return string;
    }

    static {
        URI uRI = null;
        try {
            uRI = new URI("abc://def.ghi.jkl");
        }
        catch (URI.MalformedURIException malformedURIException) {
            // empty catch block
        }
        BASE_URI = uRI;
        gNeedEscaping = new boolean[128];
        gAfterEscaping1 = new char[128];
        gAfterEscaping2 = new char[128];
        gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0; i <= 31; ++i) {
            AnyURIDV.gNeedEscaping[i] = true;
            AnyURIDV.gAfterEscaping1[i] = gHexChs[i >> 4];
            AnyURIDV.gAfterEscaping2[i] = gHexChs[i & 0xF];
        }
        AnyURIDV.gNeedEscaping[127] = true;
        AnyURIDV.gAfterEscaping1[127] = 55;
        AnyURIDV.gAfterEscaping2[127] = 70;
        for (char c : new char[]{' ', '<', '>', '\"', '{', '}', '|', '\\', '^', '~', '`'}) {
            AnyURIDV.gNeedEscaping[c] = true;
            AnyURIDV.gAfterEscaping1[c] = gHexChs[c >> 4];
            AnyURIDV.gAfterEscaping2[c] = gHexChs[c & 0xF];
        }
    }
}

