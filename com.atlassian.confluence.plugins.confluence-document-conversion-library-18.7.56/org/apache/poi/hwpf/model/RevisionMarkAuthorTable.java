/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hwpf.model.SttbUtils;
import org.apache.poi.util.Internal;

@Internal
public final class RevisionMarkAuthorTable {
    private String[] entries;

    public RevisionMarkAuthorTable(byte[] tableStream, int offset, int size) throws IOException {
        this.entries = SttbUtils.readSttbfRMark(tableStream, offset);
    }

    public List<String> getEntries() {
        return Collections.unmodifiableList(Arrays.asList(this.entries));
    }

    public String getAuthor(int index) {
        String auth = null;
        if (index >= 0 && index < this.entries.length) {
            auth = this.entries[index];
        }
        return auth;
    }

    public int getSize() {
        return this.entries.length;
    }

    public void writeTo(ByteArrayOutputStream tableStream) throws IOException {
        SttbUtils.writeSttbfRMark(this.entries, tableStream);
    }
}

