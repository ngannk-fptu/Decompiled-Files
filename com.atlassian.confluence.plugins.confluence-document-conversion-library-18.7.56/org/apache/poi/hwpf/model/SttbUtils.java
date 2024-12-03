/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.hwpf.model.Sttb;
import org.apache.poi.util.Internal;

@Internal
class SttbUtils {
    private static final int CDATA_SIZE_STTB_SAVED_BY = 2;
    private static final int CDATA_SIZE_STTBF_BKMK = 2;
    private static final int CDATA_SIZE_STTBF_R_MARK = 2;

    SttbUtils() {
    }

    static String[] readSttbfBkmk(byte[] buffer, int startOffset) {
        return new Sttb(2, buffer, startOffset).getData();
    }

    static String[] readSttbfRMark(byte[] buffer, int startOffset) {
        return new Sttb(2, buffer, startOffset).getData();
    }

    static String[] readSttbSavedBy(byte[] buffer, int startOffset) {
        return new Sttb(2, buffer, startOffset).getData();
    }

    static void writeSttbfBkmk(String[] data, OutputStream tableStream) throws IOException {
        tableStream.write(new Sttb(2, data).serialize());
    }

    static void writeSttbfRMark(String[] data, OutputStream tableStream) throws IOException {
        tableStream.write(new Sttb(2, data).serialize());
    }

    static void writeSttbSavedBy(String[] data, OutputStream tableStream) throws IOException {
        tableStream.write(new Sttb(2, data).serialize());
    }
}

