/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.fonts.cmaps;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.fonts.cmaps.CodespaceRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMap {
    private List<CodespaceRange> codeSpaceRanges = new ArrayList<CodespaceRange>();
    private Map<Integer, String> singleByteMappings = new HashMap<Integer, String>();
    private Map<Integer, String> doubleByteMappings = new HashMap<Integer, String>();

    public boolean hasOneByteMappings() {
        return !this.singleByteMappings.isEmpty();
    }

    public boolean hasTwoByteMappings() {
        return !this.doubleByteMappings.isEmpty();
    }

    public String lookup(char code) {
        String result = null;
        if (this.hasTwoByteMappings()) {
            result = this.doubleByteMappings.get(code);
        }
        if (result == null && code <= '\u00ff' && this.hasOneByteMappings()) {
            result = this.singleByteMappings.get(code & 0xFF);
        }
        return result;
    }

    public String lookup(byte[] code, int offset, int length) {
        String result = null;
        Integer key = null;
        if (length == 1) {
            key = code[offset] & 0xFF;
            result = this.singleByteMappings.get(key);
        } else if (length == 2) {
            int intKey = code[offset] & 0xFF;
            intKey <<= 8;
            key = intKey += code[offset + 1] & 0xFF;
            result = this.doubleByteMappings.get(key);
        }
        return result;
    }

    public void addMapping(byte[] src, String dest) throws IOException {
        if (src.length == 1) {
            this.singleByteMappings.put(src[0] & 0xFF, dest);
        } else if (src.length == 2) {
            int intSrc = src[0] & 0xFF;
            intSrc <<= 8;
            this.doubleByteMappings.put(intSrc |= src[1] & 0xFF, dest);
        } else {
            throw new IOException(MessageLocalization.getComposedMessage("mapping.code.should.be.1.or.two.bytes.and.not.1", src.length));
        }
    }

    public void addCodespaceRange(CodespaceRange range) {
        this.codeSpaceRanges.add(range);
    }

    public List getCodeSpaceRanges() {
        return this.codeSpaceRanges;
    }
}

