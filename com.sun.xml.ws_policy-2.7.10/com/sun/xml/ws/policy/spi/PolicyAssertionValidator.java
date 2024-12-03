/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.spi;

import com.sun.xml.ws.policy.PolicyAssertion;

public interface PolicyAssertionValidator {
    public Fitness validateClientSide(PolicyAssertion var1);

    public Fitness validateServerSide(PolicyAssertion var1);

    public String[] declareSupportedDomains();

    public static enum Fitness {
        UNKNOWN,
        INVALID,
        UNSUPPORTED,
        SUPPORTED;


        public Fitness combine(Fitness other) {
            if (this.compareTo(other) < 0) {
                return other;
            }
            return this;
        }
    }
}

