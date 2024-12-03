/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import org.bouncycastle.util.test.TestResult;

public class TestFailedException
extends RuntimeException {
    private TestResult _result;

    public TestFailedException(TestResult result) {
        this._result = result;
    }

    public TestResult getResult() {
        return this._result;
    }
}

