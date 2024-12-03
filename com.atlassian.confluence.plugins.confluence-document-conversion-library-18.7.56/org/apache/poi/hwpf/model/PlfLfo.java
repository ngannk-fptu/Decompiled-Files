/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.LFO;
import org.apache.poi.hwpf.model.LFOData;
import org.apache.poi.hwpf.model.types.LFOAbstractType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class PlfLfo {
    private static final Logger LOGGER = LogManager.getLogger(PlfLfo.class);
    private static final int MAX_NUMBER_OF_LFO = 100000;
    private int _lfoMac;
    private LFO[] _rgLfo;
    private LFOData[] _rgLfoData;

    PlfLfo(byte[] tableStream, int fcPlfLfo, int lcbPlfLfo) {
        int x;
        int offset = fcPlfLfo;
        long lfoMacLong = LittleEndian.getUInt(tableStream, offset);
        offset += 4;
        if (lfoMacLong > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Apache POI doesn't support rgLfo/rgLfoData size large than 2147483647 elements");
        }
        IOUtils.safelyAllocateCheck(lfoMacLong, 100000);
        this._lfoMac = (int)lfoMacLong;
        this._rgLfo = new LFO[this._lfoMac];
        this._rgLfoData = new LFOData[this._lfoMac];
        for (x = 0; x < this._lfoMac; ++x) {
            LFO lfo = new LFO(tableStream, offset);
            offset += LFOAbstractType.getSize();
            this._rgLfo[x] = lfo;
        }
        for (x = 0; x < this._lfoMac; ++x) {
            LFOData lfoData = new LFOData(tableStream, offset, this._rgLfo[x].getClfolvl());
            offset += lfoData.getSizeInBytes();
            this._rgLfoData[x] = lfoData;
        }
        if (offset - fcPlfLfo != lcbPlfLfo) {
            LOGGER.atWarn().log("Actual size of PlfLfo is {} bytes, but expected {}", (Object)Unbox.box(offset - fcPlfLfo), (Object)Unbox.box(lcbPlfLfo));
        }
    }

    void add(LFO lfo, LFOData lfoData) {
        this._rgLfo = Arrays.copyOf(this._rgLfo, this._lfoMac + 1);
        this._rgLfo[this._lfoMac] = lfo;
        this._rgLfoData = Arrays.copyOf(this._rgLfoData, this._lfoMac + 1);
        this._rgLfoData[this._lfoMac] = lfoData;
        ++this._lfoMac;
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
        PlfLfo other = (PlfLfo)obj;
        return this._lfoMac == other._lfoMac && Arrays.equals(this._rgLfo, other._rgLfo) && Arrays.equals(this._rgLfoData, other._rgLfoData);
    }

    public int getLfoMac() {
        return this._lfoMac;
    }

    public int getIlfoByLsid(int lsid) {
        for (int i = 0; i < this._lfoMac; ++i) {
            if (this._rgLfo[i].getLsid() != lsid) continue;
            return i + 1;
        }
        throw new NoSuchElementException("LFO with lsid " + lsid + " not found");
    }

    public LFO getLfo(int ilfo) throws NoSuchElementException {
        if (ilfo <= 0 || ilfo > this._lfoMac) {
            throw new NoSuchElementException("LFO with ilfo " + ilfo + " not found. lfoMac is " + this._lfoMac);
        }
        return this._rgLfo[ilfo - 1];
    }

    public LFOData getLfoData(int ilfo) throws NoSuchElementException {
        if (ilfo <= 0 || ilfo > this._lfoMac) {
            throw new NoSuchElementException("LFOData with ilfo " + ilfo + " not found. lfoMac is " + this._lfoMac);
        }
        return this._rgLfoData[ilfo - 1];
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this._lfoMac, this._rgLfo, this._rgLfoData});
    }

    void writeTo(FileInformationBlock fib, ByteArrayOutputStream outputStream) throws IOException {
        int i;
        int offset = outputStream.size();
        fib.setFcPlfLfo(offset);
        LittleEndian.putUInt(this._lfoMac, outputStream);
        byte[] bs = new byte[LFOAbstractType.getSize() * this._lfoMac];
        for (i = 0; i < this._lfoMac; ++i) {
            this._rgLfo[i].serialize(bs, i * LFOAbstractType.getSize());
        }
        outputStream.write(bs, 0, LFOAbstractType.getSize() * this._lfoMac);
        for (i = 0; i < this._lfoMac; ++i) {
            this._rgLfoData[i].writeTo(outputStream);
        }
        fib.setLcbPlfLfo(outputStream.size() - offset);
    }
}

