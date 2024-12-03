/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.local;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;

class PreparationContextImpl
implements AbstractMultiTableBulkIdStrategyImpl.PreparationContext {
    List<String> dropStatements = new ArrayList<String>();

    PreparationContextImpl() {
    }
}

