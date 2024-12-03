/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.ColumnRep;
import com.mchange.v1.db.sql.schemarep.TableRep;
import com.mchange.v1.util.ListUtils;
import com.mchange.v1.util.MapUtils;
import com.mchange.v1.util.SetUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableRepImpl
implements TableRep {
    String tableName;
    List colNameList;
    Map namesToColReps;
    Set primaryKeyColNames;
    Set foreignKeyReps;
    Set uniqConstrReps;

    public TableRepImpl(String string, List list, Collection collection, Collection collection2, Collection collection3) {
        this.tableName = string;
        ArrayList<String> arrayList = new ArrayList<String>();
        HashMap<String, ColumnRep> hashMap = new HashMap<String, ColumnRep>();
        int n = list.size();
        for (int i = 0; i < n; ++i) {
            ColumnRep columnRep = (ColumnRep)list.get(i);
            String string2 = columnRep.getColumnName();
            arrayList.add(string2);
            hashMap.put(string2, columnRep);
        }
        this.colNameList = Collections.unmodifiableList(arrayList);
        this.namesToColReps = Collections.unmodifiableMap(hashMap);
        this.primaryKeyColNames = collection == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(new HashSet(collection));
        this.foreignKeyReps = collection2 == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(new HashSet(collection2));
        this.uniqConstrReps = collection3 == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(new HashSet(collection3));
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public Iterator getColumnNames() {
        return this.colNameList.iterator();
    }

    @Override
    public ColumnRep columnRepForName(String string) {
        return (ColumnRep)this.namesToColReps.get(string);
    }

    @Override
    public Set getPrimaryKeyColumnNames() {
        return this.primaryKeyColNames;
    }

    @Override
    public Set getForeignKeyReps() {
        return this.foreignKeyReps;
    }

    @Override
    public Set getUniquenessConstraintReps() {
        return this.uniqConstrReps;
    }

    public boolean equals(Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TableRepImpl tableRepImpl = (TableRepImpl)object;
        return this.tableName.equals(tableRepImpl.tableName) && ListUtils.equivalent(this.colNameList, tableRepImpl.colNameList) && MapUtils.equivalentDisregardingSort(this.namesToColReps, tableRepImpl.namesToColReps) && SetUtils.equivalentDisregardingSort(this.primaryKeyColNames, tableRepImpl.primaryKeyColNames) && SetUtils.equivalentDisregardingSort(this.foreignKeyReps, tableRepImpl.foreignKeyReps) && SetUtils.equivalentDisregardingSort(this.uniqConstrReps, tableRepImpl.uniqConstrReps);
    }

    public int hashCode() {
        return this.tableName.hashCode() ^ ListUtils.hashContents(this.colNameList) ^ MapUtils.hashContentsDisregardingSort(this.namesToColReps) ^ SetUtils.hashContentsDisregardingSort(this.primaryKeyColNames) ^ SetUtils.hashContentsDisregardingSort(this.foreignKeyReps) ^ SetUtils.hashContentsDisregardingSort(this.uniqConstrReps);
    }
}

