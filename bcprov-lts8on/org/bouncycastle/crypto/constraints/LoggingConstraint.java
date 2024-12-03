/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.constraints.ServicesConstraint;

public class LoggingConstraint
extends ServicesConstraint {
    protected LoggingConstraint(Set<String> exceptions) {
        super(exceptions);
    }

    @Override
    public void check(CryptoServiceProperties service) {
        if (this.isException(service.getServiceName())) {
            return;
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("service " + service.getServiceName() + " referenced [" + service.getServiceName() + ", " + service.bitsOfSecurity() + ", " + (Object)((Object)service.getPurpose()));
        }
    }
}

