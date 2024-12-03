/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.cmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cmap.CIDRange;
import org.apache.fontbox.cmap.CodespaceRange;

public class CMap {
    private static final Log LOG = LogFactory.getLog(CMap.class);
    private int wmode = 0;
    private String cmapName = null;
    private String cmapVersion = null;
    private int cmapType = -1;
    private String registry = null;
    private String ordering = null;
    private int supplement = 0;
    private int minCodeLength = 4;
    private int maxCodeLength;
    private final List<CodespaceRange> codespaceRanges = new ArrayList<CodespaceRange>();
    private final Map<Integer, String> charToUnicode = new HashMap<Integer, String>();
    private final Map<String, byte[]> unicodeToByteCodes = new HashMap<String, byte[]>();
    private final Map<Integer, Integer> codeToCid = new HashMap<Integer, Integer>();
    private final List<CIDRange> codeToCidRanges = new ArrayList<CIDRange>();
    private static final String SPACE = " ";
    private int spaceMapping = -1;

    CMap() {
    }

    public boolean hasCIDMappings() {
        return !this.codeToCid.isEmpty() || !this.codeToCidRanges.isEmpty();
    }

    public boolean hasUnicodeMappings() {
        return !this.charToUnicode.isEmpty();
    }

    public String toUnicode(int code) {
        return this.charToUnicode.get(code);
    }

    public int readCode(InputStream in) throws IOException {
        byte[] bytes = new byte[this.maxCodeLength];
        in.read(bytes, 0, this.minCodeLength);
        in.mark(this.maxCodeLength);
        for (int i = this.minCodeLength - 1; i < this.maxCodeLength; ++i) {
            int byteCount = i + 1;
            for (CodespaceRange range : this.codespaceRanges) {
                if (!range.isFullMatch(bytes, byteCount)) continue;
                return CMap.toInt(bytes, byteCount);
            }
            if (byteCount >= this.maxCodeLength) continue;
            bytes[byteCount] = (byte)in.read();
        }
        if (LOG.isWarnEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.maxCodeLength; ++i) {
                sb.append(String.format("0x%02X (%04o) ", bytes[i], bytes[i]));
            }
            LOG.warn((Object)("Invalid character code sequence " + sb + "in CMap " + this.cmapName));
        }
        if (in.markSupported()) {
            in.reset();
        } else {
            LOG.warn((Object)("mark() and reset() not supported, " + (this.maxCodeLength - 1) + " bytes have been skipped"));
        }
        return CMap.toInt(bytes, this.minCodeLength);
    }

    static int toInt(byte[] data, int dataLen) {
        int code = 0;
        for (int i = 0; i < dataLen; ++i) {
            code <<= 8;
            code |= data[i] & 0xFF;
        }
        return code;
    }

    public int toCID(int code) {
        Integer cid = this.codeToCid.get(code);
        if (cid != null) {
            return cid;
        }
        for (CIDRange range : this.codeToCidRanges) {
            int ch = range.map((char)code);
            if (ch == -1) continue;
            return ch;
        }
        return 0;
    }

    private int getCodeFromArray(byte[] data, int offset, int length) {
        int code = 0;
        for (int i = 0; i < length; ++i) {
            code <<= 8;
            code |= (data[offset + i] + 256) % 256;
        }
        return code;
    }

    void addCharMapping(byte[] codes, String unicode) {
        this.unicodeToByteCodes.put(unicode, (byte[])codes.clone());
        int code = this.getCodeFromArray(codes, 0, codes.length);
        this.charToUnicode.put(code, unicode);
        if (SPACE.equals(unicode)) {
            this.spaceMapping = code;
        }
    }

    public byte[] getCodesFromUnicode(String unicode) {
        return this.unicodeToByteCodes.get(unicode);
    }

    void addCIDMapping(int code, int cid) {
        this.codeToCid.put(cid, code);
    }

    void addCIDRange(char from, char to, int cid) {
        CIDRange lastRange = null;
        if (!this.codeToCidRanges.isEmpty()) {
            lastRange = this.codeToCidRanges.get(this.codeToCidRanges.size() - 1);
        }
        if (lastRange == null || !lastRange.extend(from, to, cid)) {
            this.codeToCidRanges.add(new CIDRange(from, to, cid));
        }
    }

    void addCodespaceRange(CodespaceRange range) {
        this.codespaceRanges.add(range);
        this.maxCodeLength = Math.max(this.maxCodeLength, range.getCodeLength());
        this.minCodeLength = Math.min(this.minCodeLength, range.getCodeLength());
    }

    void useCmap(CMap cmap) {
        for (CodespaceRange codespaceRange : cmap.codespaceRanges) {
            this.addCodespaceRange(codespaceRange);
        }
        this.charToUnicode.putAll(cmap.charToUnicode);
        this.codeToCid.putAll(cmap.codeToCid);
        this.codeToCidRanges.addAll(cmap.codeToCidRanges);
    }

    public int getWMode() {
        return this.wmode;
    }

    public void setWMode(int newWMode) {
        this.wmode = newWMode;
    }

    public String getName() {
        return this.cmapName;
    }

    public void setName(String name) {
        this.cmapName = name;
    }

    public String getVersion() {
        return this.cmapVersion;
    }

    public void setVersion(String version) {
        this.cmapVersion = version;
    }

    public int getType() {
        return this.cmapType;
    }

    public void setType(int type) {
        this.cmapType = type;
    }

    public String getRegistry() {
        return this.registry;
    }

    public void setRegistry(String newRegistry) {
        this.registry = newRegistry;
    }

    public String getOrdering() {
        return this.ordering;
    }

    public void setOrdering(String newOrdering) {
        this.ordering = newOrdering;
    }

    public int getSupplement() {
        return this.supplement;
    }

    public void setSupplement(int newSupplement) {
        this.supplement = newSupplement;
    }

    public int getSpaceMapping() {
        return this.spaceMapping;
    }

    public String toString() {
        return this.cmapName;
    }
}

