/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hwpf.model.SavedByEntry;
import org.apache.poi.hwpf.model.SttbUtils;
import org.apache.poi.util.Internal;

@Internal
public final class SavedByTable {
    private SavedByEntry[] entries;

    public SavedByTable(byte[] tableStream, int offset, int size) {
        String[] strings = SttbUtils.readSttbSavedBy(tableStream, offset);
        int numEntries = strings.length / 2;
        this.entries = new SavedByEntry[numEntries];
        for (int i = 0; i < numEntries; ++i) {
            this.entries[i] = new SavedByEntry(strings[i * 2], strings[i * 2 + 1]);
        }
    }

    public List<SavedByEntry> getEntries() {
        return Collections.unmodifiableList(Arrays.asList(this.entries));
    }

    public void writeTo(ByteArrayOutputStream tableStream) throws IOException {
        String[] toSave = new String[this.entries.length * 2];
        int counter = 0;
        for (SavedByEntry entry : this.entries) {
            toSave[counter++] = entry.getUserName();
            toSave[counter++] = entry.getSaveLocation();
        }
        SttbUtils.writeSttbSavedBy(toSave, tableStream);
    }
}

