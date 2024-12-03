/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.usertype;

import java.util.Comparator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public interface UserVersionType
extends UserType,
Comparator {
    public Object seed(SharedSessionContractImplementor var1);

    public Object next(Object var1, SharedSessionContractImplementor var2);
}

