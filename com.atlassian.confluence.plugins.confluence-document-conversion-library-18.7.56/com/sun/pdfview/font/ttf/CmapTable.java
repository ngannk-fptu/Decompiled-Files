/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.CMap;
import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class CmapTable
extends TrueTypeTable {
    private short version;
    private SortedMap<CmapSubtable, CMap> subtables;

    protected CmapTable() {
        super(1668112752);
        this.setVersion((short)0);
        this.subtables = Collections.synchronizedSortedMap(new TreeMap());
    }

    public void addCMap(short platformID, short platformSpecificID, CMap cMap) {
        CmapSubtable key = new CmapSubtable(platformID, platformSpecificID);
        this.subtables.put(key, cMap);
    }

    public CMap getCMap(short platformID, short platformSpecificID) {
        CmapSubtable key = new CmapSubtable(platformID, platformSpecificID);
        return (CMap)this.subtables.get(key);
    }

    public CMap[] getCMaps() {
        Collection<CMap> c = this.subtables.values();
        CMap[] maps = new CMap[c.size()];
        c.toArray(maps);
        return maps;
    }

    public void removeCMap(short platformID, short platformSpecificID) {
        CmapSubtable key = new CmapSubtable(platformID, platformSpecificID);
        this.subtables.remove(key);
    }

    @Override
    public void setData(ByteBuffer data) {
        this.setVersion(data.getShort());
        int numberSubtables = data.getShort();
        for (int i = 0; i < numberSubtables; ++i) {
            short platformID = data.getShort();
            short platformSpecificID = data.getShort();
            int offset = data.getInt();
            data.mark();
            data.position(offset);
            ByteBuffer mapData = data.slice();
            data.reset();
            try {
                CMap cMap = CMap.getMap(mapData);
                if (cMap == null) continue;
                this.addCMap(platformID, platformSpecificID, cMap);
                continue;
            }
            catch (Exception ex) {
                System.out.println("Error reading map.  PlatformID=" + platformID + ", PlatformSpecificID=" + platformSpecificID);
                System.out.println("Reason: " + ex);
            }
        }
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putShort(this.getVersion());
        buf.putShort((short)this.subtables.size());
        int curOffset = 4 + this.subtables.size() * 8;
        for (CmapSubtable cms : this.subtables.keySet()) {
            CMap map = (CMap)this.subtables.get(cms);
            buf.putShort(cms.platformID);
            buf.putShort(cms.platformSpecificID);
            buf.putInt(curOffset);
            curOffset += map.getLength();
        }
        for (CMap map : this.subtables.values()) {
            buf.put(map.getData());
        }
        buf.flip();
        return buf;
    }

    @Override
    public int getLength() {
        int length = 4;
        length += this.subtables.size() * 8;
        for (CMap map : this.subtables.values()) {
            length += map.getLength();
        }
        return length;
    }

    public short getVersion() {
        return this.version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getNumberSubtables() {
        return (short)this.subtables.size();
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        buf.append(indent + "Version: " + this.getVersion() + "\n");
        buf.append(indent + "NumMaps: " + this.getNumberSubtables() + "\n");
        for (CmapSubtable key : this.subtables.keySet()) {
            buf.append(indent + "Map: platformID: " + key.platformID + " PlatformSpecificID: " + key.platformSpecificID + "\n");
            CMap map = (CMap)this.subtables.get(key);
            buf.append(map.toString());
        }
        return buf.toString();
    }

    class CmapSubtable
    implements Comparable {
        short platformID;
        short platformSpecificID;

        protected CmapSubtable(short platformID, short platformSpecificID) {
            this.platformID = platformID;
            this.platformSpecificID = platformSpecificID;
        }

        public boolean equals(Object obj) {
            return this.compareTo(obj) == 0;
        }

        public int compareTo(Object obj) {
            if (!(obj instanceof CmapSubtable)) {
                return -1;
            }
            CmapSubtable cms = (CmapSubtable)obj;
            if (this.platformID < cms.platformID) {
                return -1;
            }
            if (this.platformID > cms.platformID) {
                return 1;
            }
            if (this.platformSpecificID < cms.platformSpecificID) {
                return -1;
            }
            if (this.platformSpecificID > cms.platformSpecificID) {
                return 1;
            }
            return 0;
        }
    }
}

