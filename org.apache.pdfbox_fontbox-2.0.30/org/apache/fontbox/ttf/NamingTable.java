/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.Charsets;

public class NamingTable
extends TTFTable {
    public static final String TAG = "name";
    private List<NameRecord> nameRecords;
    private Map<Integer, Map<Integer, Map<Integer, Map<Integer, String>>>> lookupTable;
    private String fontFamily = null;
    private String fontSubFamily = null;
    private String psName = null;

    NamingTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        int formatSelector = data.readUnsignedShort();
        int numberOfNameRecords = data.readUnsignedShort();
        int offsetToStartOfStringStorage = data.readUnsignedShort();
        this.nameRecords = new ArrayList<NameRecord>(numberOfNameRecords);
        for (int i = 0; i < numberOfNameRecords; ++i) {
            NameRecord nr = new NameRecord();
            nr.initData(ttf, data);
            this.nameRecords.add(nr);
        }
        for (NameRecord nr : this.nameRecords) {
            if ((long)nr.getStringOffset() > this.getLength()) {
                nr.setString(null);
                continue;
            }
            data.seek(this.getOffset() + 6L + (long)(numberOfNameRecords * 2 * 6) + (long)nr.getStringOffset());
            int platform = nr.getPlatformId();
            int encoding = nr.getPlatformEncodingId();
            Charset charset = Charsets.ISO_8859_1;
            if (platform == 3 && (encoding == 0 || encoding == 1)) {
                charset = Charsets.UTF_16;
            } else if (platform == 0) {
                charset = Charsets.UTF_16;
            } else if (platform == 2) {
                switch (encoding) {
                    case 0: {
                        charset = Charsets.US_ASCII;
                        break;
                    }
                    case 1: {
                        charset = Charsets.ISO_10646;
                        break;
                    }
                    case 2: {
                        charset = Charsets.ISO_8859_1;
                        break;
                    }
                }
            }
            String string = data.readString(nr.getStringLength(), charset);
            nr.setString(string);
        }
        this.lookupTable = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, String>>>>(this.nameRecords.size());
        for (NameRecord nr : this.nameRecords) {
            Map<Integer, String> languageLookup;
            Map<Integer, Map<Integer, String>> encodingLookup;
            Map<Integer, Map<Integer, Map<Integer, String>>> platformLookup = this.lookupTable.get(nr.getNameId());
            if (platformLookup == null) {
                platformLookup = new HashMap<Integer, Map<Integer, Map<Integer, String>>>();
                this.lookupTable.put(nr.getNameId(), platformLookup);
            }
            if ((encodingLookup = platformLookup.get(nr.getPlatformId())) == null) {
                encodingLookup = new HashMap<Integer, Map<Integer, String>>();
                platformLookup.put(nr.getPlatformId(), encodingLookup);
            }
            if ((languageLookup = encodingLookup.get(nr.getPlatformEncodingId())) == null) {
                languageLookup = new HashMap<Integer, String>(1);
                encodingLookup.put(nr.getPlatformEncodingId(), languageLookup);
            }
            languageLookup.put(nr.getLanguageId(), nr.getString());
        }
        this.fontFamily = this.getEnglishName(1);
        this.fontSubFamily = this.getEnglishName(2);
        this.psName = this.getName(6, 1, 0, 0);
        if (this.psName == null) {
            this.psName = this.getName(6, 3, 1, 1033);
        }
        if (this.psName != null) {
            this.psName = this.psName.trim();
        }
        this.initialized = true;
    }

    private String getEnglishName(int nameId) {
        for (int i = 4; i >= 0; --i) {
            String nameUni = this.getName(nameId, 0, i, 0);
            if (nameUni == null) continue;
            return nameUni;
        }
        String nameWin = this.getName(nameId, 3, 1, 1033);
        if (nameWin != null) {
            return nameWin;
        }
        return this.getName(nameId, 1, 0, 0);
    }

    public String getName(int nameId, int platformId, int encodingId, int languageId) {
        Map<Integer, Map<Integer, Map<Integer, String>>> platforms = this.lookupTable.get(nameId);
        if (platforms == null) {
            return null;
        }
        Map<Integer, Map<Integer, String>> encodings = platforms.get(platformId);
        if (encodings == null) {
            return null;
        }
        Map<Integer, String> languages = encodings.get(encodingId);
        if (languages == null) {
            return null;
        }
        return languages.get(languageId);
    }

    public List<NameRecord> getNameRecords() {
        return this.nameRecords;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public String getFontSubFamily() {
        return this.fontSubFamily;
    }

    public String getPostScriptName() {
        return this.psName;
    }
}

