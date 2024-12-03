/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.CasIdentifier
 */
package com.atlassian.vcache.internal.core.cas;

import com.atlassian.vcache.CasIdentifier;
import java.io.Serializable;

public class IdentifiedData
implements Serializable,
CasIdentifier {
    private final long casId = System.nanoTime();

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentifiedData)) {
            return false;
        }
        IdentifiedData that = (IdentifiedData)o;
        return this.casId == that.casId;
    }
}

