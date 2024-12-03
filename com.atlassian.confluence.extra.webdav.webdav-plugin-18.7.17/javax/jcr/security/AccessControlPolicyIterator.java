/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

import javax.jcr.RangeIterator;
import javax.jcr.security.AccessControlPolicy;

public interface AccessControlPolicyIterator
extends RangeIterator {
    public AccessControlPolicy nextAccessControlPolicy();
}

