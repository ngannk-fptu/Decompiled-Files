/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.verifier.statics;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.verifier.PassVerifier;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.exc.LoadingException;
import org.apache.commons.lang3.ArrayUtils;

public final class Pass1Verifier
extends PassVerifier {
    private JavaClass javaClass;
    private final Verifier verifier;

    public Pass1Verifier(Verifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public VerificationResult do_verify() {
        JavaClass jc;
        try {
            jc = this.getJavaClass();
            if (jc != null && !this.verifier.getClassName().equals(jc.getClassName()) && !jc.getClassName().endsWith(this.verifier.getClassName())) {
                throw new LoadingException("Wrong name: the internal name of the .class file '" + jc.getClassName() + "' does not match the file's name '" + this.verifier.getClassName() + "'.");
            }
        }
        catch (ClassFormatException | LoadingException e) {
            return new VerificationResult(2, e.getMessage());
        }
        catch (RuntimeException e) {
            return new VerificationResult(2, "Parsing via BCEL did not succeed.  exception occurred:\n" + e.toString());
        }
        if (jc != null) {
            return VerificationResult.VR_OK;
        }
        return new VerificationResult(2, "Repository.lookup() failed. FILE NOT FOUND?");
    }

    private JavaClass getJavaClass() {
        if (this.javaClass == null) {
            try {
                this.javaClass = Repository.lookupClass(this.verifier.getClassName());
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return this.javaClass;
    }

    @Override
    public String[] getMessages() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
}

