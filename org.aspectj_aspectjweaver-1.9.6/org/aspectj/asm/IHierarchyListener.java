/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.util.EventListener;
import org.aspectj.asm.IHierarchy;

public interface IHierarchyListener
extends EventListener {
    public void elementsUpdated(IHierarchy var1);
}

