/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IModule;

public interface IBinaryModule
extends IModule {
    public IBinaryAnnotation[] getAnnotations();

    public long getTagBits();
}

