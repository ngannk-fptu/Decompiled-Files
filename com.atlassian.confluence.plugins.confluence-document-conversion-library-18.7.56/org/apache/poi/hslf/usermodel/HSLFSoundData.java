/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.ArrayList;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.Sound;

public final class HSLFSoundData {
    private Sound _container;

    public HSLFSoundData(Sound container) {
        this._container = container;
    }

    public String getSoundName() {
        return this._container.getSoundName();
    }

    public String getSoundType() {
        return this._container.getSoundType();
    }

    public byte[] getData() {
        return this._container.getSoundData();
    }

    public static HSLFSoundData[] find(Document document) {
        ArrayList<HSLFSoundData> lst = new ArrayList<HSLFSoundData>();
        for (Record value : document.getChildRecords()) {
            Record[] sr;
            if (value.getRecordType() != (long)RecordTypes.SoundCollection.typeID) continue;
            RecordContainer col = (RecordContainer)value;
            for (Record record : sr = col.getChildRecords()) {
                if (!(record instanceof Sound)) continue;
                lst.add(new HSLFSoundData((Sound)record));
            }
        }
        return lst.toArray(new HSLFSoundData[0]);
    }
}

