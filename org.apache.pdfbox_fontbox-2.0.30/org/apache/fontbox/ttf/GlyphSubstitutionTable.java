/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class GlyphSubstitutionTable
extends TTFTable {
    private static final Log LOG = LogFactory.getLog(GlyphSubstitutionTable.class);
    public static final String TAG = "GSUB";
    private LinkedHashMap<String, ScriptTable> scriptList;
    private FeatureRecord[] featureList;
    private LookupTable[] lookupList;
    private final Map<Integer, Integer> lookupCache = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> reverseLookup = new HashMap<Integer, Integer>();
    private String lastUsedSupportedScript;

    GlyphSubstitutionTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        long start = data.getCurrentPosition();
        int majorVersion = data.readUnsignedShort();
        int minorVersion = data.readUnsignedShort();
        int scriptListOffset = data.readUnsignedShort();
        int featureListOffset = data.readUnsignedShort();
        int lookupListOffset = data.readUnsignedShort();
        long featureVariationsOffset = -1L;
        if ((long)minorVersion == 1L) {
            featureVariationsOffset = data.readUnsignedInt();
        }
        this.scriptList = this.readScriptList(data, start + (long)scriptListOffset);
        this.featureList = this.readFeatureList(data, start + (long)featureListOffset);
        this.lookupList = this.readLookupList(data, start + (long)lookupListOffset);
        this.initialized = true;
    }

    LinkedHashMap<String, ScriptTable> readScriptList(TTFDataStream data, long offset) throws IOException {
        int i;
        data.seek(offset);
        int scriptCount = data.readUnsignedShort();
        ScriptRecord[] scriptRecords = new ScriptRecord[scriptCount];
        int[] scriptOffsets = new int[scriptCount];
        for (i = 0; i < scriptCount; ++i) {
            ScriptRecord scriptRecord = new ScriptRecord();
            scriptRecord.scriptTag = data.readString(4);
            scriptOffsets[i] = data.readUnsignedShort();
            scriptRecords[i] = scriptRecord;
        }
        for (i = 0; i < scriptCount; ++i) {
            scriptRecords[i].scriptTable = this.readScriptTable(data, offset + (long)scriptOffsets[i]);
        }
        LinkedHashMap<String, ScriptTable> resultScriptList = new LinkedHashMap<String, ScriptTable>(scriptCount);
        for (ScriptRecord scriptRecord : scriptRecords) {
            resultScriptList.put(scriptRecord.scriptTag, scriptRecord.scriptTable);
        }
        return resultScriptList;
    }

    ScriptTable readScriptTable(TTFDataStream data, long offset) throws IOException {
        int i;
        data.seek(offset);
        ScriptTable scriptTable = new ScriptTable();
        int defaultLangSys = data.readUnsignedShort();
        int langSysCount = data.readUnsignedShort();
        LangSysRecord[] langSysRecords = new LangSysRecord[langSysCount];
        int[] langSysOffsets = new int[langSysCount];
        String prevLangSysTag = "";
        for (i = 0; i < langSysCount; ++i) {
            LangSysRecord langSysRecord = new LangSysRecord();
            langSysRecord.langSysTag = data.readString(4);
            if (i > 0 && langSysRecord.langSysTag.compareTo(prevLangSysTag) <= 0) {
                throw new IOException("LangSysRecords not alphabetically sorted by LangSys tag: " + langSysRecord.langSysTag + " <= " + prevLangSysTag);
            }
            langSysOffsets[i] = data.readUnsignedShort();
            langSysRecords[i] = langSysRecord;
            prevLangSysTag = langSysRecord.langSysTag;
        }
        if (defaultLangSys != 0) {
            scriptTable.defaultLangSysTable = this.readLangSysTable(data, offset + (long)defaultLangSys);
        }
        for (i = 0; i < langSysCount; ++i) {
            langSysRecords[i].langSysTable = this.readLangSysTable(data, offset + (long)langSysOffsets[i]);
        }
        scriptTable.langSysTables = new LinkedHashMap(langSysCount);
        for (LangSysRecord langSysRecord : langSysRecords) {
            scriptTable.langSysTables.put(langSysRecord.langSysTag, langSysRecord.langSysTable);
        }
        return scriptTable;
    }

    LangSysTable readLangSysTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        LangSysTable langSysTable = new LangSysTable();
        int lookupOrder = data.readUnsignedShort();
        langSysTable.requiredFeatureIndex = data.readUnsignedShort();
        int featureIndexCount = data.readUnsignedShort();
        langSysTable.featureIndices = new int[featureIndexCount];
        for (int i = 0; i < featureIndexCount; ++i) {
            langSysTable.featureIndices[i] = data.readUnsignedShort();
        }
        return langSysTable;
    }

    FeatureRecord[] readFeatureList(TTFDataStream data, long offset) throws IOException {
        int i;
        data.seek(offset);
        int featureCount = data.readUnsignedShort();
        FeatureRecord[] featureRecords = new FeatureRecord[featureCount];
        int[] featureOffsets = new int[featureCount];
        String prevFeatureTag = "";
        for (i = 0; i < featureCount; ++i) {
            FeatureRecord featureRecord = new FeatureRecord();
            featureRecord.featureTag = data.readString(4);
            if (i > 0 && featureRecord.featureTag.compareTo(prevFeatureTag) < 0) {
                if (featureRecord.featureTag.matches("\\w{4}") && prevFeatureTag.matches("\\w{4}")) {
                    LOG.debug((Object)("FeatureRecord array not alphabetically sorted by FeatureTag: " + featureRecord.featureTag + " < " + prevFeatureTag));
                } else {
                    LOG.warn((Object)("FeatureRecord array not alphabetically sorted by FeatureTag: " + featureRecord.featureTag + " < " + prevFeatureTag));
                    return new FeatureRecord[0];
                }
            }
            featureOffsets[i] = data.readUnsignedShort();
            featureRecords[i] = featureRecord;
            prevFeatureTag = featureRecord.featureTag;
        }
        for (i = 0; i < featureCount; ++i) {
            featureRecords[i].featureTable = this.readFeatureTable(data, offset + (long)featureOffsets[i]);
        }
        return featureRecords;
    }

    FeatureTable readFeatureTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        FeatureTable featureTable = new FeatureTable();
        int featureParams = data.readUnsignedShort();
        int lookupIndexCount = data.readUnsignedShort();
        featureTable.lookupListIndices = new int[lookupIndexCount];
        for (int i = 0; i < lookupIndexCount; ++i) {
            featureTable.lookupListIndices[i] = data.readUnsignedShort();
        }
        return featureTable;
    }

    LookupTable[] readLookupList(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int lookupCount = data.readUnsignedShort();
        int[] lookups = new int[lookupCount];
        for (int i = 0; i < lookupCount; ++i) {
            lookups[i] = data.readUnsignedShort();
        }
        LookupTable[] lookupTables = new LookupTable[lookupCount];
        for (int i = 0; i < lookupCount; ++i) {
            lookupTables[i] = this.readLookupTable(data, offset + (long)lookups[i]);
        }
        return lookupTables;
    }

    LookupTable readLookupTable(TTFDataStream data, long offset) throws IOException {
        int i;
        data.seek(offset);
        LookupTable lookupTable = new LookupTable();
        lookupTable.lookupType = data.readUnsignedShort();
        lookupTable.lookupFlag = data.readUnsignedShort();
        int subTableCount = data.readUnsignedShort();
        int[] subTableOffsets = new int[subTableCount];
        for (i = 0; i < subTableCount; ++i) {
            subTableOffsets[i] = data.readUnsignedShort();
        }
        if ((lookupTable.lookupFlag & 0x10) != 0) {
            lookupTable.markFilteringSet = data.readUnsignedShort();
        }
        lookupTable.subTables = new LookupSubTable[subTableCount];
        switch (lookupTable.lookupType) {
            case 1: {
                for (i = 0; i < subTableCount; ++i) {
                    lookupTable.subTables[i] = this.readLookupSubTable(data, offset + (long)subTableOffsets[i]);
                }
                break;
            }
            default: {
                LOG.debug((Object)("Type " + lookupTable.lookupType + " GSUB lookup table is not supported and will be ignored"));
            }
        }
        return lookupTable;
    }

    LookupSubTable readLookupSubTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int substFormat = data.readUnsignedShort();
        switch (substFormat) {
            case 1: {
                LookupTypeSingleSubstFormat1 lookupSubTable = new LookupTypeSingleSubstFormat1();
                lookupSubTable.substFormat = substFormat;
                int coverageOffset = data.readUnsignedShort();
                lookupSubTable.deltaGlyphID = data.readSignedShort();
                lookupSubTable.coverageTable = this.readCoverageTable(data, offset + (long)coverageOffset);
                return lookupSubTable;
            }
            case 2: {
                LookupTypeSingleSubstFormat2 lookupSubTable = new LookupTypeSingleSubstFormat2();
                lookupSubTable.substFormat = substFormat;
                int coverageOffset = data.readUnsignedShort();
                int glyphCount = data.readUnsignedShort();
                lookupSubTable.substituteGlyphIDs = new int[glyphCount];
                for (int i = 0; i < glyphCount; ++i) {
                    lookupSubTable.substituteGlyphIDs[i] = data.readUnsignedShort();
                }
                lookupSubTable.coverageTable = this.readCoverageTable(data, offset + (long)coverageOffset);
                return lookupSubTable;
            }
        }
        LOG.warn((Object)("Unknown substFormat: " + substFormat));
        return null;
    }

    CoverageTable readCoverageTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int coverageFormat = data.readUnsignedShort();
        switch (coverageFormat) {
            case 1: {
                CoverageTableFormat1 coverageTable = new CoverageTableFormat1();
                coverageTable.coverageFormat = coverageFormat;
                int glyphCount = data.readUnsignedShort();
                coverageTable.glyphArray = new int[glyphCount];
                for (int i = 0; i < glyphCount; ++i) {
                    coverageTable.glyphArray[i] = data.readUnsignedShort();
                }
                return coverageTable;
            }
            case 2: {
                CoverageTableFormat2 coverageTable = new CoverageTableFormat2();
                coverageTable.coverageFormat = coverageFormat;
                int rangeCount = data.readUnsignedShort();
                coverageTable.rangeRecords = new RangeRecord[rangeCount];
                for (int i = 0; i < rangeCount; ++i) {
                    coverageTable.rangeRecords[i] = this.readRangeRecord(data);
                }
                return coverageTable;
            }
        }
        throw new IOException("Unknown coverage format: " + coverageFormat);
    }

    private String selectScriptTag(String[] tags) {
        String tag;
        if (tags.length == 1 && ("Inherited".equals(tag = tags[0]) || "DFLT".equals(tag) && !this.scriptList.containsKey(tag))) {
            if (this.lastUsedSupportedScript == null) {
                this.lastUsedSupportedScript = this.scriptList.keySet().iterator().next();
            }
            return this.lastUsedSupportedScript;
        }
        for (String tag2 : tags) {
            if (!this.scriptList.containsKey(tag2)) continue;
            this.lastUsedSupportedScript = tag2;
            return this.lastUsedSupportedScript;
        }
        return tags[0];
    }

    private Collection<LangSysTable> getLangSysTables(String scriptTag) {
        Collection<LangSysTable> result = Collections.emptyList();
        ScriptTable scriptTable = this.scriptList.get(scriptTag);
        if (scriptTable != null) {
            if (scriptTable.defaultLangSysTable == null) {
                result = scriptTable.langSysTables.values();
            } else {
                result = new ArrayList<LangSysTable>(scriptTable.langSysTables.values());
                result.add(scriptTable.defaultLangSysTable);
            }
        }
        return result;
    }

    private List<FeatureRecord> getFeatureRecords(Collection<LangSysTable> langSysTables, final List<String> enabledFeatures) {
        if (langSysTables.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<FeatureRecord> result = new ArrayList<FeatureRecord>();
        for (LangSysTable langSysTable : langSysTables) {
            int required = langSysTable.requiredFeatureIndex;
            if (required != 65535 && required < this.featureList.length) {
                result.add(this.featureList[required]);
            }
            for (int featureIndex : langSysTable.featureIndices) {
                if (featureIndex >= this.featureList.length || enabledFeatures != null && !enabledFeatures.contains(this.featureList[featureIndex].featureTag)) continue;
                result.add(this.featureList[featureIndex]);
            }
        }
        if (this.containsFeature(result, "vrt2")) {
            this.removeFeature(result, "vert");
        }
        if (enabledFeatures != null && result.size() > 1) {
            Collections.sort(result, new Comparator<FeatureRecord>(){

                @Override
                public int compare(FeatureRecord o1, FeatureRecord o2) {
                    int i2;
                    int i1 = enabledFeatures.indexOf(o1.featureTag);
                    return i1 < (i2 = enabledFeatures.indexOf(o2.featureTag)) ? -1 : (i1 == i2 ? 0 : 1);
                }
            });
        }
        return result;
    }

    private boolean containsFeature(List<FeatureRecord> featureRecords, String featureTag) {
        for (FeatureRecord featureRecord : featureRecords) {
            if (!featureRecord.featureTag.equals(featureTag)) continue;
            return true;
        }
        return false;
    }

    private void removeFeature(List<FeatureRecord> featureRecords, String featureTag) {
        Iterator<FeatureRecord> iter = featureRecords.iterator();
        while (iter.hasNext()) {
            if (!iter.next().featureTag.equals(featureTag)) continue;
            iter.remove();
        }
    }

    private int applyFeature(FeatureRecord featureRecord, int gid) {
        for (int lookupListIndex : featureRecord.featureTable.lookupListIndices) {
            LookupTable lookupTable = this.lookupList[lookupListIndex];
            if (lookupTable.lookupType != 1) {
                LOG.debug((Object)("Skipping GSUB feature '" + featureRecord.featureTag + "' because it requires unsupported lookup table type " + lookupTable.lookupType));
                continue;
            }
            gid = this.doLookup(lookupTable, gid);
        }
        return gid;
    }

    private int doLookup(LookupTable lookupTable, int gid) {
        for (LookupSubTable lookupSubtable : lookupTable.subTables) {
            int coverageIndex = lookupSubtable.coverageTable.getCoverageIndex(gid);
            if (coverageIndex < 0) continue;
            return lookupSubtable.doSubstitution(gid, coverageIndex);
        }
        return gid;
    }

    public int getSubstitution(int gid, String[] scriptTags, List<String> enabledFeatures) {
        if (gid == -1) {
            return -1;
        }
        Integer cached = this.lookupCache.get(gid);
        if (cached != null) {
            return cached;
        }
        String scriptTag = this.selectScriptTag(scriptTags);
        Collection<LangSysTable> langSysTables = this.getLangSysTables(scriptTag);
        List<FeatureRecord> featureRecords = this.getFeatureRecords(langSysTables, enabledFeatures);
        int sgid = gid;
        for (FeatureRecord featureRecord : featureRecords) {
            sgid = this.applyFeature(featureRecord, sgid);
        }
        this.lookupCache.put(gid, sgid);
        this.reverseLookup.put(sgid, gid);
        return sgid;
    }

    public int getUnsubstitution(int sgid) {
        Integer gid = this.reverseLookup.get(sgid);
        if (gid == null) {
            LOG.warn((Object)("Trying to un-substitute a never-before-seen gid: " + sgid));
            return sgid;
        }
        return gid;
    }

    RangeRecord readRangeRecord(TTFDataStream data) throws IOException {
        RangeRecord rangeRecord = new RangeRecord();
        rangeRecord.startGlyphID = data.readUnsignedShort();
        rangeRecord.endGlyphID = data.readUnsignedShort();
        rangeRecord.startCoverageIndex = data.readUnsignedShort();
        return rangeRecord;
    }

    static class RangeRecord {
        int startGlyphID;
        int endGlyphID;
        int startCoverageIndex;

        RangeRecord() {
        }

        public String toString() {
            return String.format("RangeRecord[startGlyphID=%d,endGlyphID=%d,startCoverageIndex=%d]", this.startGlyphID, this.endGlyphID, this.startCoverageIndex);
        }
    }

    static class CoverageTableFormat2
    extends CoverageTable {
        RangeRecord[] rangeRecords;

        CoverageTableFormat2() {
        }

        @Override
        int getCoverageIndex(int gid) {
            for (RangeRecord rangeRecord : this.rangeRecords) {
                if (rangeRecord.startGlyphID > gid || gid > rangeRecord.endGlyphID) continue;
                return rangeRecord.startCoverageIndex + gid - rangeRecord.startGlyphID;
            }
            return -1;
        }

        public String toString() {
            return String.format("CoverageTableFormat2[coverageFormat=%d]", this.coverageFormat);
        }
    }

    static class CoverageTableFormat1
    extends CoverageTable {
        int[] glyphArray;

        CoverageTableFormat1() {
        }

        @Override
        int getCoverageIndex(int gid) {
            return Arrays.binarySearch(this.glyphArray, gid);
        }

        public String toString() {
            return String.format("CoverageTableFormat1[coverageFormat=%d,glyphArray=%s]", this.coverageFormat, Arrays.toString(this.glyphArray));
        }
    }

    static abstract class CoverageTable {
        int coverageFormat;

        CoverageTable() {
        }

        abstract int getCoverageIndex(int var1);
    }

    static class LookupTypeSingleSubstFormat2
    extends LookupSubTable {
        int[] substituteGlyphIDs;

        LookupTypeSingleSubstFormat2() {
        }

        @Override
        int doSubstitution(int gid, int coverageIndex) {
            return coverageIndex < 0 ? gid : this.substituteGlyphIDs[coverageIndex];
        }

        public String toString() {
            return String.format("LookupTypeSingleSubstFormat2[substFormat=%d,substituteGlyphIDs=%s]", this.substFormat, Arrays.toString(this.substituteGlyphIDs));
        }
    }

    static class LookupTypeSingleSubstFormat1
    extends LookupSubTable {
        short deltaGlyphID;

        LookupTypeSingleSubstFormat1() {
        }

        @Override
        int doSubstitution(int gid, int coverageIndex) {
            return coverageIndex < 0 ? gid : gid + this.deltaGlyphID;
        }

        public String toString() {
            return String.format("LookupTypeSingleSubstFormat1[substFormat=%d,deltaGlyphID=%d]", this.substFormat, this.deltaGlyphID);
        }
    }

    static abstract class LookupSubTable {
        int substFormat;
        CoverageTable coverageTable;

        LookupSubTable() {
        }

        abstract int doSubstitution(int var1, int var2);
    }

    static class LookupTable {
        int lookupType;
        int lookupFlag;
        int markFilteringSet;
        LookupSubTable[] subTables;

        LookupTable() {
        }

        public String toString() {
            return String.format("LookupTable[lookupType=%d,lookupFlag=%d,markFilteringSet=%d]", this.lookupType, this.lookupFlag, this.markFilteringSet);
        }
    }

    static class FeatureTable {
        int[] lookupListIndices;

        FeatureTable() {
        }

        public String toString() {
            return String.format("FeatureTable[lookupListIndicesCount=%d]", this.lookupListIndices.length);
        }
    }

    static class FeatureRecord {
        String featureTag;
        FeatureTable featureTable;

        FeatureRecord() {
        }

        public String toString() {
            return String.format("FeatureRecord[featureTag=%s]", this.featureTag);
        }
    }

    static class LangSysTable {
        int requiredFeatureIndex;
        int[] featureIndices;

        LangSysTable() {
        }

        public String toString() {
            return String.format("LangSysTable[requiredFeatureIndex=%d]", this.requiredFeatureIndex);
        }
    }

    static class LangSysRecord {
        String langSysTag;
        LangSysTable langSysTable;

        LangSysRecord() {
        }

        public String toString() {
            return String.format("LangSysRecord[langSysTag=%s]", this.langSysTag);
        }
    }

    static class ScriptTable {
        LangSysTable defaultLangSysTable;
        LinkedHashMap<String, LangSysTable> langSysTables;

        ScriptTable() {
        }

        public String toString() {
            return String.format("ScriptTable[hasDefault=%s,langSysRecordsCount=%d]", this.defaultLangSysTable != null, this.langSysTables.size());
        }
    }

    static class ScriptRecord {
        String scriptTag;
        ScriptTable scriptTable;

        ScriptRecord() {
        }

        public String toString() {
            return String.format("ScriptRecord[scriptTag=%s]", this.scriptTag);
        }
    }
}

