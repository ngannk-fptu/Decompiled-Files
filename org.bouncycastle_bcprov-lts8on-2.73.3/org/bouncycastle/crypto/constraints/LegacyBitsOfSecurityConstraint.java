/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ServicesConstraint;

public class LegacyBitsOfSecurityConstraint
extends ServicesConstraint {
    private final int requiredBitsOfSecurity;
    private final int legacyRequiredBitsOfSecurity;

    public LegacyBitsOfSecurityConstraint(int requiredBitsOfSecurity) {
        this(requiredBitsOfSecurity, 0);
    }

    public LegacyBitsOfSecurityConstraint(int requiredBitsOfSecurity, int legacyRequiredBitsOfSecurity) {
        super(Collections.EMPTY_SET);
        this.requiredBitsOfSecurity = requiredBitsOfSecurity;
        this.legacyRequiredBitsOfSecurity = legacyRequiredBitsOfSecurity;
    }

    public LegacyBitsOfSecurityConstraint(int requiredBitsOfSecurity, Set<String> exceptions) {
        this(requiredBitsOfSecurity, 0, exceptions);
    }

    public LegacyBitsOfSecurityConstraint(int requiredBitsOfSecurity, int legacyRequiredBitsOfSecurity, Set<String> exceptions) {
        super(exceptions);
        this.requiredBitsOfSecurity = requiredBitsOfSecurity;
        this.legacyRequiredBitsOfSecurity = legacyRequiredBitsOfSecurity;
    }

    @Override
    public void check(CryptoServiceProperties service) {
        if (this.isException(service.getServiceName())) {
            return;
        }
        CryptoServicePurpose purpose = service.getPurpose();
        switch (purpose) {
            case ANY: 
            case VERIFYING: 
            case DECRYPTION: 
            case VERIFICATION: {
                if (service.bitsOfSecurity() < this.legacyRequiredBitsOfSecurity) {
                    throw new CryptoServiceConstraintsException("service does not provide " + this.legacyRequiredBitsOfSecurity + " bits of security only " + service.bitsOfSecurity());
                }
                if (purpose != CryptoServicePurpose.ANY && LOG.isLoggable(Level.FINE)) {
                    LOG.fine("usage of legacy cryptography service for algorithm " + service.getServiceName());
                }
                return;
            }
        }
        if (service.bitsOfSecurity() < this.requiredBitsOfSecurity) {
            throw new CryptoServiceConstraintsException("service does not provide " + this.requiredBitsOfSecurity + " bits of security only " + service.bitsOfSecurity());
        }
    }
}

