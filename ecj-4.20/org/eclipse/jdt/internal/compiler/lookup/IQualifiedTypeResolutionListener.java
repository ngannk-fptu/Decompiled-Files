/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public interface IQualifiedTypeResolutionListener {
    public void recordResolution(QualifiedTypeReference var1, TypeBinding var2);
}

