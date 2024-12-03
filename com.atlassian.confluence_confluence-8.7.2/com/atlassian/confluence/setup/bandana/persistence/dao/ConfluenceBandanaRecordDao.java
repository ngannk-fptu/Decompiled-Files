/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.bandana.persistence.dao;

import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import java.util.Collection;

public interface ConfluenceBandanaRecordDao {
    public void saveOrUpdate(ConfluenceBandanaRecord var1);

    public ConfluenceBandanaRecord getRecord(String var1, String var2);

    public void remove(ConfluenceBandanaRecord var1);

    public void removeAllInContext(String var1);

    public Collection findForContext(String var1);

    public Iterable<String> findKeysForContext(String var1);

    public long countWithKey(String var1);

    public Iterable<ConfluenceBandanaRecord> findAllWithKey(String var1);
}

