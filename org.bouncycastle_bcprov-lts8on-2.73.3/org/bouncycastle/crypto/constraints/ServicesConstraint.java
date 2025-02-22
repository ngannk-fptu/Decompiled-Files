/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.bouncycastle.crypto.CryptoServicesConstraints;
import org.bouncycastle.crypto.constraints.Utils;
import org.bouncycastle.util.Strings;

public abstract class ServicesConstraint
implements CryptoServicesConstraints {
    protected static final Logger LOG = Logger.getLogger(ServicesConstraint.class.getName());
    private final Set<String> exceptions;

    protected ServicesConstraint(Set<String> exceptions) {
        if (exceptions.isEmpty()) {
            this.exceptions = Collections.EMPTY_SET;
        } else {
            this.exceptions = new HashSet<String>(exceptions.size());
            Iterator<String> it = exceptions.iterator();
            while (it.hasNext()) {
                this.exceptions.add(Strings.toUpperCase(it.next().toString()));
            }
            Utils.addAliases(this.exceptions);
        }
    }

    protected boolean isException(String algorithm) {
        if (this.exceptions.isEmpty()) {
            return false;
        }
        return this.exceptions.contains(Strings.toUpperCase(algorithm));
    }
}

