/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.verifier;

import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.commons.lang3.ArrayUtils;

public abstract class PassVerifier {
    private final List<String> messages = new ArrayList<String>();
    private VerificationResult verificationResult;

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public abstract VerificationResult do_verify();

    public String[] getMessages() {
        return this.getMessagesList().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public List<String> getMessagesList() {
        this.verify();
        return this.messages;
    }

    public VerificationResult verify() {
        if (this.verificationResult == null) {
            this.verificationResult = this.do_verify();
        }
        return this.verificationResult;
    }
}

