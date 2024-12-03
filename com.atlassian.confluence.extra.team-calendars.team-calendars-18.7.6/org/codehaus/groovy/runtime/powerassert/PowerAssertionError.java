/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.powerassert;

public class PowerAssertionError
extends AssertionError {
    public PowerAssertionError(String msg) {
        super((Object)msg);
    }

    public String toString() {
        return String.format("Assertion failed: \n\n%s\n", this.getMessage());
    }
}

