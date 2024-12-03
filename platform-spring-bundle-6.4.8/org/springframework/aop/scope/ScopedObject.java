/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

public interface ScopedObject
extends RawTargetAccess {
    public Object getTargetObject();

    public void removeFromScope();
}

