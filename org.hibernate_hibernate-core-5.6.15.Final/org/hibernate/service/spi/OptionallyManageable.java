/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

import java.util.List;
import org.hibernate.service.spi.Manageable;

public interface OptionallyManageable
extends Manageable {
    public List<Manageable> getRealManageables();

    @Override
    default public Object getManagementBean() {
        return null;
    }
}

