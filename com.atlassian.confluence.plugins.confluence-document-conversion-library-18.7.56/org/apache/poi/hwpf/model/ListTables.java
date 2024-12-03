/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.LFO;
import org.apache.poi.hwpf.model.LFOData;
import org.apache.poi.hwpf.model.ListData;
import org.apache.poi.hwpf.model.ListLevel;
import org.apache.poi.hwpf.model.PlfLfo;
import org.apache.poi.hwpf.model.types.LSTFAbstractType;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class ListTables {
    private static final Logger LOGGER = LogManager.getLogger(ListTables.class);
    private final LinkedHashMap<Integer, ListData> _listMap = new LinkedHashMap();
    private PlfLfo _plfLfo;

    public ListTables() {
    }

    public ListTables(byte[] tableStream, int lstOffset, int fcPlfLfo, int lcbPlfLfo) {
        int offset = lstOffset;
        int cLst = LittleEndian.getShort(tableStream, offset);
        int levelOffset = (offset += 2) + cLst * LSTFAbstractType.getSize();
        for (int x = 0; x < cLst; ++x) {
            ListData lst = new ListData(tableStream, offset);
            this._listMap.put(lst.getLsid(), lst);
            offset += LSTFAbstractType.getSize();
            int num = lst.numLevels();
            for (int y = 0; y < num; ++y) {
                ListLevel lvl = new ListLevel();
                levelOffset += lvl.read(tableStream, levelOffset);
                lst.setLevel(y, lvl);
            }
        }
        this._plfLfo = new PlfLfo(tableStream, fcPlfLfo, lcbPlfLfo);
    }

    public void writeListDataTo(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        int startOffset = tableStream.size();
        fib.setFcPlfLst(startOffset);
        int listSize = this._listMap.size();
        ByteArrayOutputStream levelBuf = new ByteArrayOutputStream();
        byte[] shortHolder = new byte[2];
        LittleEndian.putShort(shortHolder, 0, (short)listSize);
        tableStream.write(shortHolder);
        for (ListData lst : this._listMap.values()) {
            ListLevel[] lvls;
            tableStream.write(lst.toByteArray());
            for (ListLevel lvl : lvls = lst.getLevels()) {
                levelBuf.write(lvl.toByteArray());
            }
        }
        fib.setLcbPlfLst(tableStream.size() - startOffset);
        tableStream.write(levelBuf.toByteArray());
    }

    public void writeListOverridesTo(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        this._plfLfo.writeTo(fib, tableStream);
    }

    public LFO getLfo(int ilfo) throws NoSuchElementException {
        return this._plfLfo.getLfo(ilfo);
    }

    public LFOData getLfoData(int ilfo) throws NoSuchElementException {
        return this._plfLfo.getLfoData(ilfo);
    }

    public int getOverrideIndexFromListID(int lsid) throws NoSuchElementException {
        return this._plfLfo.getIlfoByLsid(lsid);
    }

    public ListLevel getLevel(int lsid, int level) {
        ListData lst = this._listMap.get(lsid);
        if (lst == null) {
            LOGGER.atWarn().log("ListData for {} was null.", (Object)Unbox.box(lsid));
            return null;
        }
        if (level < lst.numLevels()) {
            return lst.getLevels()[level];
        }
        LOGGER.atWarn().log("Requested level {} which was greater than the maximum defined ({})", (Object)Unbox.box(level), (Object)Unbox.box(lst.numLevels()));
        return null;
    }

    public ListData getListData(int lsid) {
        return this._listMap.get(lsid);
    }

    public int hashCode() {
        return Objects.hash(this._listMap, this._plfLfo);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ListTables other = (ListTables)obj;
        if (!this._listMap.equals(other._listMap)) {
            return false;
        }
        return Objects.equals(this._plfLfo, other._plfLfo);
    }

    public int addList(ListData lst, LFO lfo, LFOData lfoData) {
        int lsid = lst.getLsid();
        while (this._listMap.containsKey(lsid)) {
            lsid = lst.resetListID();
            lfo.setLsid(lsid);
        }
        this._listMap.put(lsid, lst);
        if (lfo == null && lfoData != null) {
            throw new IllegalArgumentException("LFO and LFOData should be specified both or noone");
        }
        if (lfo != null) {
            this._plfLfo.add(lfo, lfoData);
        }
        return lsid;
    }
}

