/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.XPathMatcher;

public interface FieldActivator {
    public void startValueScopeFor(IdentityConstraint var1, int var2);

    public XPathMatcher activateField(Field var1, int var2);

    public void endValueScopeFor(IdentityConstraint var1, int var2);
}

