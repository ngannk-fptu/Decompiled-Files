/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.spi;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;

public final class AssertionCreationException
extends PolicyException {
    private final AssertionData assertionData;

    public AssertionCreationException(AssertionData assertionData, String message) {
        super(message);
        this.assertionData = assertionData;
    }

    public AssertionCreationException(AssertionData assertionData, String message, Throwable cause) {
        super(message, cause);
        this.assertionData = assertionData;
    }

    public AssertionCreationException(AssertionData assertionData, Throwable cause) {
        super(cause);
        this.assertionData = assertionData;
    }

    public AssertionData getAssertionData() {
        return this.assertionData;
    }
}

