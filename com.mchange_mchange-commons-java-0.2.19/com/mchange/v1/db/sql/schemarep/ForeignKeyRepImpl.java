/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.ForeignKeyRep;
import com.mchange.v1.util.ListUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForeignKeyRepImpl
implements ForeignKeyRep {
    List locColNames;
    String refTableName;
    List refColNames;

    public ForeignKeyRepImpl(List list, String string, List list2) {
        this.locColNames = Collections.unmodifiableList(new ArrayList(list));
        this.refTableName = string;
        this.refColNames = Collections.unmodifiableList(new ArrayList(list2));
    }

    @Override
    public List getLocalColumnNames() {
        return this.locColNames;
    }

    @Override
    public String getReferencedTableName() {
        return this.refTableName;
    }

    @Override
    public List getReferencedColumnNames() {
        return this.refColNames;
    }

    public boolean equals(Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ForeignKeyRepImpl foreignKeyRepImpl = (ForeignKeyRepImpl)object;
        return ListUtils.equivalent(this.locColNames, foreignKeyRepImpl.locColNames) && this.refTableName.equals(foreignKeyRepImpl.refTableName) && ListUtils.equivalent(this.refColNames, foreignKeyRepImpl.refColNames);
    }

    public int hashCode() {
        return ListUtils.hashContents(this.locColNames) ^ this.refTableName.hashCode() ^ ListUtils.hashContents(this.refColNames);
    }
}

