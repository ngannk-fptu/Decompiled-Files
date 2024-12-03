/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.io.Serializable;

public interface Instantiator
extends Serializable {
    public Object instantiate(Serializable var1);

    public Object instantiate();

    public boolean isInstance(Object var1);
}

