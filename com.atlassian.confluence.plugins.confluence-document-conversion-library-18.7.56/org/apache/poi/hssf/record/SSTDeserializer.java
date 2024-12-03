/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.util.IntMapper;

class SSTDeserializer {
    private static final Logger LOG = LogManager.getLogger(SSTDeserializer.class);
    private IntMapper<UnicodeString> strings;

    public SSTDeserializer(IntMapper<UnicodeString> strings) {
        this.strings = strings;
    }

    public void manufactureStrings(int stringCount, RecordInputStream in) {
        for (int i = 0; i < stringCount; ++i) {
            UnicodeString str;
            if (in.available() == 0 && !in.hasNextRecord()) {
                LOG.atError().log("Ran out of data before creating all the strings! String at index {}", (Object)Unbox.box(i));
                str = new UnicodeString("");
            } else {
                str = new UnicodeString(in);
            }
            SSTDeserializer.addToStringTable(this.strings, str);
        }
    }

    public static void addToStringTable(IntMapper<UnicodeString> strings, UnicodeString string) {
        strings.add(string);
    }
}

