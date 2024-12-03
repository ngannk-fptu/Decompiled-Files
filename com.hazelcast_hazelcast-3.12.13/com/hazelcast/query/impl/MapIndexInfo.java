/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.IndexInfo;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MapIndexInfo
implements IdentifiedDataSerializable {
    private String mapName;
    private List<IndexInfo> indexInfos = new LinkedList<IndexInfo>();

    public MapIndexInfo(String mapName) {
        this.mapName = mapName;
    }

    public MapIndexInfo() {
    }

    public void addIndexInfo(String attributeName, boolean ordered) {
        this.indexInfos.add(new IndexInfo(attributeName, ordered));
    }

    public void addIndexInfos(Collection<IndexInfo> indexInfos) {
        this.indexInfos.addAll(indexInfos);
    }

    public String getMapName() {
        return this.mapName;
    }

    public List<IndexInfo> getIndexInfos() {
        return this.indexInfos;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.mapName);
        out.writeInt(this.indexInfos.size());
        for (IndexInfo indexInfo : this.indexInfos) {
            indexInfo.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mapName = in.readUTF();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            IndexInfo indexInfo = new IndexInfo();
            indexInfo.readData(in);
            this.indexInfos.add(indexInfo);
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 99;
    }
}

