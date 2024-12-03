/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.verifier.statics.Pass1Verifier;
import org.apache.bcel.verifier.statics.Pass2Verifier;
import org.apache.bcel.verifier.statics.Pass3aVerifier;
import org.apache.bcel.verifier.structurals.Pass3bVerifier;
import org.apache.commons.lang3.ArrayUtils;

public class Verifier {
    static final String NAME = "Apache Commons BCEL";
    static final String BANNER = "Apache Commons BCEL\nhttps://commons.apache.org/bcel\n";
    static final Verifier[] EMPTY_ARRAY = new Verifier[0];
    private final String className;
    private Pass1Verifier p1v;
    private Pass2Verifier p2v;
    private final Map<String, Pass3aVerifier> p3avs = new HashMap<String, Pass3aVerifier>();
    private final Map<String, Pass3bVerifier> p3bvs = new HashMap<String, Pass3bVerifier>();

    public static void main(String[] args) {
        System.out.println(BANNER);
        for (int index = 0; index < args.length; ++index) {
            try {
                int dotclasspos;
                if (args[index].endsWith(".class") && (dotclasspos = args[index].lastIndexOf(".class")) != -1) {
                    args[index] = args[index].substring(0, dotclasspos);
                }
                args[index] = Utility.pathToPackage(args[index]);
                System.out.println("Now verifying: " + args[index] + "\n");
                Verifier.verifyType(args[index]);
                Repository.clearCache();
                System.gc();
                continue;
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    static void verifyType(String fullyQualifiedClassName) throws ClassNotFoundException {
        Verifier verifier = VerifierFactory.getVerifier(fullyQualifiedClassName);
        VerificationResult verificationResult = verifier.doPass1();
        System.out.println("Pass 1:\n" + verificationResult);
        verificationResult = verifier.doPass2();
        System.out.println("Pass 2:\n" + verificationResult);
        if (verificationResult == VerificationResult.VR_OK) {
            JavaClass jc = Repository.lookupClass(fullyQualifiedClassName);
            for (int i = 0; i < jc.getMethods().length; ++i) {
                verificationResult = verifier.doPass3a(i);
                System.out.println("Pass 3a, method number " + i + " ['" + jc.getMethods()[i] + "']:\n" + verificationResult);
                verificationResult = verifier.doPass3b(i);
                System.out.println("Pass 3b, method number " + i + " ['" + jc.getMethods()[i] + "']:\n" + verificationResult);
            }
        }
        System.out.println("Warnings:");
        String[] warnings = verifier.getMessages();
        if (warnings.length == 0) {
            System.out.println("<none>");
        }
        for (String warning : warnings) {
            System.out.println(warning);
        }
        System.out.println("\n");
        verifier.flush();
    }

    Verifier(String fullyQualifiedClassName) {
        this.className = fullyQualifiedClassName;
    }

    public VerificationResult doPass1() {
        if (this.p1v == null) {
            this.p1v = new Pass1Verifier(this);
        }
        return this.p1v.verify();
    }

    public VerificationResult doPass2() {
        if (this.p2v == null) {
            this.p2v = new Pass2Verifier(this);
        }
        return this.p2v.verify();
    }

    public VerificationResult doPass3a(int methodNo) {
        return this.p3avs.computeIfAbsent(Integer.toString(methodNo), k -> new Pass3aVerifier(this, methodNo)).verify();
    }

    public VerificationResult doPass3b(int methodNo) {
        return this.p3bvs.computeIfAbsent(Integer.toString(methodNo), k -> new Pass3bVerifier(this, methodNo)).verify();
    }

    public void flush() {
        this.p1v = null;
        this.p2v = null;
        this.p3avs.clear();
        this.p3bvs.clear();
    }

    public final String getClassName() {
        return this.className;
    }

    public String[] getMessages() throws ClassNotFoundException {
        int meth;
        ArrayList<String> messages = new ArrayList<String>();
        if (this.p1v != null) {
            this.p1v.getMessagesList().forEach(element -> messages.add("Pass 1: " + element));
        }
        if (this.p2v != null) {
            this.p2v.getMessagesList().forEach(element -> messages.add("Pass 2: " + element));
        }
        for (Pass3aVerifier pass3aVerifier : this.p3avs.values()) {
            meth = pass3aVerifier.getMethodNo();
            for (String element2 : pass3aVerifier.getMessages()) {
                messages.add("Pass 3a, method " + meth + " ('" + Repository.lookupClass(this.className).getMethods()[meth] + "'): " + element2);
            }
        }
        for (Pass3bVerifier pass3bVerifier : this.p3bvs.values()) {
            meth = pass3bVerifier.getMethodNo();
            for (String element2 : pass3bVerifier.getMessages()) {
                messages.add("Pass 3b, method " + meth + " ('" + Repository.lookupClass(this.className).getMethods()[meth] + "'): " + element2);
            }
        }
        return messages.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }
}

