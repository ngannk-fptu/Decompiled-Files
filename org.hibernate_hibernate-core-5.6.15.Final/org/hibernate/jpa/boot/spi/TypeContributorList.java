/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.boot.spi;

import java.util.List;
import org.hibernate.boot.model.TypeContributor;

public interface TypeContributorList {
    public List<TypeContributor> getTypeContributors();
}

