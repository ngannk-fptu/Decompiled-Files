/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.Set;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.constraints.ServicesConstraint;

public class BitsOfSecurityConstraint
extends ServicesConstraint {
    private final int requiredBitsOfSecurity;

    public BitsOfSecurityConstraint(int requiredBitsOfSecurity) {
        super(Collections.EMPTY_SET);
        this.requiredBitsOfSecurity = requiredBitsOfSecurity;
    }

    public BitsOfSecurityConstraint(int requiredBitsOfSecurity, Set<String> exceptions) {
        super(exceptions);
        this.requiredBitsOfSecurity = requiredBitsOfSecurity;
    }

    @Override
    public void check(CryptoServiceProperties service) {
        if (this.isException(service.getServiceName())) {
            return;
        }
        if (service.bitsOfSecurity() < this.requiredBitsOfSecurity) {
            throw new CryptoServiceConstraintsException("service does not provide " + this.requiredBitsOfSecurity + " bits of security only " + service.bitsOfSecurity());
        }
    }
}

