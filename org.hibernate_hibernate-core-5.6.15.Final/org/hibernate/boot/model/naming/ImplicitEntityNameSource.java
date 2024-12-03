/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNameSource;

public interface ImplicitEntityNameSource
extends ImplicitNameSource {
    public EntityNaming getEntityNaming();
}

