/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.service.ServiceRegistry;

public interface TypeContributor {
    public void contribute(TypeContributions var1, ServiceRegistry var2);
}

