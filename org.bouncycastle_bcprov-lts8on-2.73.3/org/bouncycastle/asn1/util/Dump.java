/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.util;

import java.io.FileInputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

public class Dump {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("usage: Dump [-v] filename");
            System.exit(1);
        }
        boolean verbose = false;
        int argsPos = 0;
        if (args.length > 1) {
            verbose = "-v".equals(args[argsPos++]);
        }
        try (FileInputStream fIn = new FileInputStream(args[argsPos++]);){
            ASN1Primitive obj;
            ASN1InputStream bIn = new ASN1InputStream(fIn);
            while ((obj = bIn.readObject()) != null) {
                System.out.println(ASN1Dump.dumpAsString(obj, verbose));
            }
        }
    }
}

