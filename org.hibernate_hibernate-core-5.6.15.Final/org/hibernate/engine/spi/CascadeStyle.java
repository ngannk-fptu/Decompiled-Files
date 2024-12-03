/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import org.hibernate.engine.spi.CascadingAction;

public interface CascadeStyle
extends Serializable {
    public boolean doCascade(CascadingAction var1);

    public boolean reallyDoCascade(CascadingAction var1);

    public boolean hasOrphanDelete();
}

