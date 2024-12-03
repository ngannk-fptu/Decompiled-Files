/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.cid;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.PDFCMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToUnicodeMap
extends PDFCMap {
    private Map<Character, Character> singleCharMappings = new HashMap<Character, Character>();
    private List<CharRangeMapping> charRangeMappings = new ArrayList<CharRangeMapping>();

    public ToUnicodeMap(PDFObject map) throws IOException {
        this.parseMappings(map);
    }

    private void parseMappings(PDFObject map) throws IOException {
        try {
            StringReader reader = new StringReader(new String(map.getStream(), "ASCII"));
            BufferedReader bf = new BufferedReader(reader);
            String line = bf.readLine();
            while (line != null) {
                if (line.contains("beginbfchar")) {
                    this.parseSingleCharMappingSection(bf);
                }
                if (line.contains("beginbfrange")) {
                    this.parseCharRangeMappingSection(bf);
                }
                line = bf.readLine();
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }

    private void parseCharRangeMappingSection(BufferedReader bf) throws IOException {
        String line = bf.readLine();
        while (line != null && !line.contains("endbfrange")) {
            this.parseRangeLine(line);
            line = bf.readLine();
        }
    }

    private void parseRangeLine(String line) {
        String[] mapping = line.split(" ");
        if (mapping.length == 3) {
            Character srcStart = this.parseChar(mapping[0]);
            Character srcEnd = this.parseChar(mapping[1]);
            Character destStart = this.parseChar(mapping[2]);
            this.charRangeMappings.add(new CharRangeMapping(srcStart.charValue(), srcEnd.charValue(), destStart.charValue()));
        }
    }

    private void parseSingleCharMappingSection(BufferedReader bf) throws IOException {
        String line = bf.readLine();
        while (line != null && !line.contains("endbfchar")) {
            this.parseSingleCharMappingLine(line);
            line = bf.readLine();
        }
    }

    private void parseSingleCharMappingLine(String line) {
        String[] mapping = line.split(" ");
        if (mapping.length == 2 && mapping[0].startsWith("<") && mapping[1].startsWith("<")) {
            this.singleCharMappings.put(this.parseChar(mapping[0]), this.parseChar(mapping[1]));
        }
    }

    private Character parseChar(String charDef) {
        if (charDef.startsWith("<")) {
            charDef = charDef.substring(1);
        }
        if (charDef.endsWith(">")) {
            charDef = charDef.substring(0, charDef.length() - 1);
        }
        long result = Long.decode("0x" + charDef);
        return Character.valueOf((char)result);
    }

    @Override
    public char map(char src) {
        Character mappedChar = this.singleCharMappings.get(Character.valueOf(src));
        if (mappedChar == null) {
            mappedChar = this.lookupInRanges(src);
        }
        if (mappedChar == null) {
            mappedChar = Character.valueOf(src);
        }
        return mappedChar.charValue();
    }

    private Character lookupInRanges(char src) {
        Character mappedChar = null;
        for (CharRangeMapping rangeMapping : this.charRangeMappings) {
            if (!rangeMapping.contains(src)) continue;
            mappedChar = Character.valueOf(rangeMapping.map(src));
            break;
        }
        return mappedChar;
    }

    private static class CharRangeMapping {
        char srcStart;
        char srcEnd;
        char destStart;

        CharRangeMapping(char srcStart, char srcEnd, char destStart) {
            this.srcStart = srcStart;
            this.srcEnd = srcEnd;
            this.destStart = destStart;
        }

        boolean contains(char c) {
            return this.srcStart <= c && c <= this.srcEnd;
        }

        char map(char src) {
            return (char)(this.destStart + (src - this.srcStart));
        }
    }
}

