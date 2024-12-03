/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.verifier.VerifierFactoryObserver;

public class TransitiveHull
implements VerifierFactoryObserver {
    private int indent;

    public static void main(String[] args) {
        int dotclasspos;
        if (args.length != 1) {
            System.out.println("Need exactly one argument: The root class to verify.");
            System.exit(1);
        }
        if ((dotclasspos = args[0].lastIndexOf(".class")) != -1) {
            args[0] = args[0].substring(0, dotclasspos);
        }
        args[0] = Utility.pathToPackage(args[0]);
        TransitiveHull th = new TransitiveHull();
        VerifierFactory.attach(th);
        VerifierFactory.getVerifier(args[0]);
        VerifierFactory.detach(th);
    }

    private TransitiveHull() {
    }

    @Override
    public void update(String className) {
        System.gc();
        for (int i = 0; i < this.indent; ++i) {
            System.out.print(" ");
        }
        System.out.println(className);
        ++this.indent;
        Verifier v = VerifierFactory.getVerifier(className);
        VerificationResult vr = v.doPass1();
        if (vr != VerificationResult.VR_OK) {
            System.out.println("Pass 1:\n" + vr);
        }
        if ((vr = v.doPass2()) != VerificationResult.VR_OK) {
            System.out.println("Pass 2:\n" + vr);
        }
        if (vr == VerificationResult.VR_OK) {
            try {
                JavaClass jc = Repository.lookupClass(v.getClassName());
                for (int i = 0; i < jc.getMethods().length; ++i) {
                    vr = v.doPass3a(i);
                    if (vr != VerificationResult.VR_OK) {
                        System.out.println(v.getClassName() + ", Pass 3a, method " + i + " ['" + jc.getMethods()[i] + "']:\n" + vr);
                    }
                    if ((vr = v.doPass3b(i)) == VerificationResult.VR_OK) continue;
                    System.out.println(v.getClassName() + ", Pass 3b, method " + i + " ['" + jc.getMethods()[i] + "']:\n" + vr);
                }
            }
            catch (ClassNotFoundException e) {
                System.err.println("Could not find class " + v.getClassName() + " in Repository");
            }
        }
        --this.indent;
    }
}

