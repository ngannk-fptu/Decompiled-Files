/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.result;

import org.hibernate.result.Output;

public interface Outputs {
    public Output getCurrent();

    public boolean goToNext();

    public void release();
}

