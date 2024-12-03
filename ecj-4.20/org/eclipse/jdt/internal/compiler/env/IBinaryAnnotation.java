/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;

public interface IBinaryAnnotation {
    public char[] getTypeName();

    public IBinaryElementValuePair[] getElementValuePairs();

    default public boolean isExternalAnnotation() {
        return false;
    }

    default public boolean isDeprecatedAnnotation() {
        return false;
    }
}

