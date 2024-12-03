/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.CFFFont;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CFFFontSubset
extends CFFFont {
    static final String[] SubrsFunctions = new String[]{"RESERVED_0", "hstem", "RESERVED_2", "vstem", "vmoveto", "rlineto", "hlineto", "vlineto", "rrcurveto", "RESERVED_9", "callsubr", "return", "escape", "RESERVED_13", "endchar", "RESERVED_15", "RESERVED_16", "RESERVED_17", "hstemhm", "hintmask", "cntrmask", "rmoveto", "hmoveto", "vstemhm", "rcurveline", "rlinecurve", "vvcurveto", "hhcurveto", "shortint", "callgsubr", "vhcurveto", "hvcurveto"};
    static final String[] SubrsEscapeFuncs = new String[]{"RESERVED_0", "RESERVED_1", "RESERVED_2", "and", "or", "not", "RESERVED_6", "RESERVED_7", "RESERVED_8", "abs", "add", "sub", "div", "RESERVED_13", "neg", "eq", "RESERVED_16", "RESERVED_17", "drop", "RESERVED_19", "put", "get", "ifelse", "random", "mul", "RESERVED_25", "sqrt", "dup", "exch", "index", "roll", "RESERVED_31", "RESERVED_32", "RESERVED_33", "hflex", "flex", "hflex1", "flex1", "RESERVED_REST"};
    static final byte ENDCHAR_OP = 14;
    static final byte RETURN_OP = 11;
    HashMap<Integer, int[]> GlyphsUsed;
    ArrayList<Integer> glyphsInList;
    HashMap<Integer, Object> FDArrayUsed = new HashMap();
    HashMap<Integer, int[]>[] hSubrsUsed;
    ArrayList<Integer>[] lSubrsUsed;
    HashMap<Integer, int[]> hGSubrsUsed = new HashMap();
    ArrayList<Integer> lGSubrsUsed = new ArrayList();
    HashMap<Integer, int[]> hSubrsUsedNonCID = new HashMap();
    ArrayList<Integer> lSubrsUsedNonCID = new ArrayList();
    byte[][] NewLSubrsIndex;
    byte[] NewSubrsIndexNonCID;
    byte[] NewGSubrsIndex;
    byte[] NewCharStringsIndex;
    int GBias = 0;
    LinkedList<CFFFont.Item> OutputList;
    int NumOfHints = 0;

    public CFFFontSubset(RandomAccessFileOrArray rf, HashMap<Integer, int[]> GlyphsUsed) {
        super(rf);
        this.GlyphsUsed = GlyphsUsed;
        this.glyphsInList = new ArrayList<Integer>(GlyphsUsed.keySet());
        for (int i = 0; i < this.fonts.length; ++i) {
            this.seek(this.fonts[i].charstringsOffset);
            this.fonts[i].nglyphs = this.getCard16();
            this.seek(this.stringIndexOffset);
            this.fonts[i].nstrings = this.getCard16() + standardStrings.length;
            this.fonts[i].charstringsOffsets = this.getIndex(this.fonts[i].charstringsOffset);
            if (this.fonts[i].fdselectOffset >= 0) {
                this.readFDSelect(i);
                this.BuildFDArrayUsed(i);
            }
            if (this.fonts[i].isCID) {
                this.ReadFDArray(i);
            }
            this.fonts[i].CharsetLength = this.CountCharset(this.fonts[i].charsetOffset, this.fonts[i].nglyphs);
        }
    }

    int CountCharset(int Offset, int NumofGlyphs) {
        int Length2 = 0;
        this.seek(Offset);
        char format = this.getCard8();
        switch (format) {
            case '\u0000': {
                Length2 = 1 + 2 * NumofGlyphs;
                break;
            }
            case '\u0001': {
                Length2 = 1 + 3 * this.CountRange(NumofGlyphs, 1);
                break;
            }
            case '\u0002': {
                Length2 = 1 + 4 * this.CountRange(NumofGlyphs, 2);
                break;
            }
        }
        return Length2;
    }

    int CountRange(int NumofGlyphs, int Type2) {
        char nLeft;
        int num = 0;
        for (int i = 1; i < NumofGlyphs; i += nLeft + '\u0001') {
            ++num;
            char Sid = this.getCard16();
            nLeft = Type2 == 1 ? this.getCard8() : this.getCard16();
        }
        return num;
    }

    protected void readFDSelect(int Font2) {
        int NumOfGlyphs = this.fonts[Font2].nglyphs;
        int[] FDSelect = new int[NumOfGlyphs];
        this.seek(this.fonts[Font2].fdselectOffset);
        this.fonts[Font2].FDSelectFormat = this.getCard8();
        switch (this.fonts[Font2].FDSelectFormat) {
            case 0: {
                for (int i = 0; i < NumOfGlyphs; ++i) {
                    FDSelect[i] = this.getCard8();
                }
                this.fonts[Font2].FDSelectLength = this.fonts[Font2].nglyphs + 1;
                break;
            }
            case 3: {
                int nRanges = this.getCard16();
                int l = 0;
                char first = this.getCard16();
                for (int i = 0; i < nRanges; ++i) {
                    char fd = this.getCard8();
                    char last = this.getCard16();
                    int steps = last - first;
                    for (int k = 0; k < steps; ++k) {
                        FDSelect[l] = fd;
                        ++l;
                    }
                    first = last;
                }
                this.fonts[Font2].FDSelectLength = 3 + nRanges * 3 + 2;
                break;
            }
        }
        this.fonts[Font2].FDSelect = FDSelect;
    }

    protected void BuildFDArrayUsed(int Font2) {
        int[] FDSelect = this.fonts[Font2].FDSelect;
        for (Integer o : this.glyphsInList) {
            int glyph = o;
            int FD = FDSelect[glyph];
            this.FDArrayUsed.put(FD, null);
        }
    }

    protected void ReadFDArray(int Font2) {
        this.seek(this.fonts[Font2].fdarrayOffset);
        this.fonts[Font2].FDArrayCount = this.getCard16();
        this.fonts[Font2].FDArrayOffsize = this.getCard8();
        if (this.fonts[Font2].FDArrayOffsize < 4) {
            ++this.fonts[Font2].FDArrayOffsize;
        }
        this.fonts[Font2].FDArrayOffsets = this.getIndex(this.fonts[Font2].fdarrayOffset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] Process(String fontName) throws IOException {
        try {
            byte[] Ret;
            int j;
            this.buf.reOpen();
            for (j = 0; j < this.fonts.length && !fontName.equals(this.fonts[j].name); ++j) {
            }
            if (j == this.fonts.length) {
                byte[] byArray = null;
                return byArray;
            }
            if (this.gsubrIndexOffset >= 0) {
                this.GBias = this.CalcBias(this.gsubrIndexOffset, j);
            }
            this.BuildNewCharString(j);
            this.BuildNewLGSubrs(j);
            byte[] byArray = Ret = this.BuildNewFile(j);
            return byArray;
        }
        finally {
            try {
                this.buf.close();
            }
            catch (Exception exception) {}
        }
    }

    protected int CalcBias(int Offset, int Font2) {
        this.seek(Offset);
        char nSubrs = this.getCard16();
        if (this.fonts[Font2].CharstringType == 1) {
            return 0;
        }
        if (nSubrs < '\u04d8') {
            return 107;
        }
        if (nSubrs < '\u846c') {
            return 1131;
        }
        return 32768;
    }

    protected void BuildNewCharString(int FontIndex) throws IOException {
        this.NewCharStringsIndex = this.BuildNewIndex(this.fonts[FontIndex].charstringsOffsets, this.GlyphsUsed, (byte)14);
    }

    protected void BuildNewLGSubrs(int Font2) throws IOException {
        if (this.fonts[Font2].isCID) {
            HashMap mapClazz = new HashMap();
            this.hSubrsUsed = (HashMap[])Array.newInstance(mapClazz.getClass(), this.fonts[Font2].fdprivateOffsets.length);
            ArrayList listClass = new ArrayList();
            this.lSubrsUsed = (ArrayList[])Array.newInstance(listClass.getClass(), this.fonts[Font2].fdprivateOffsets.length);
            this.NewLSubrsIndex = new byte[this.fonts[Font2].fdprivateOffsets.length][];
            this.fonts[Font2].PrivateSubrsOffset = new int[this.fonts[Font2].fdprivateOffsets.length];
            this.fonts[Font2].PrivateSubrsOffsetsArray = new int[this.fonts[Font2].fdprivateOffsets.length][];
            ArrayList<Integer> FDInList = new ArrayList<Integer>(this.FDArrayUsed.keySet());
            Iterator iterator = FDInList.iterator();
            while (iterator.hasNext()) {
                int FD = (Integer)iterator.next();
                this.hSubrsUsed[FD] = new HashMap();
                this.lSubrsUsed[FD] = new ArrayList();
                this.BuildFDSubrsOffsets(Font2, FD);
                if (this.fonts[Font2].PrivateSubrsOffset[FD] < 0) continue;
                this.BuildSubrUsed(Font2, FD, this.fonts[Font2].PrivateSubrsOffset[FD], this.fonts[Font2].PrivateSubrsOffsetsArray[FD], this.hSubrsUsed[FD], this.lSubrsUsed[FD]);
                this.NewLSubrsIndex[FD] = this.BuildNewIndex(this.fonts[Font2].PrivateSubrsOffsetsArray[FD], this.hSubrsUsed[FD], (byte)11);
            }
        } else if (this.fonts[Font2].privateSubrs >= 0) {
            this.fonts[Font2].SubrsOffsets = this.getIndex(this.fonts[Font2].privateSubrs);
            this.BuildSubrUsed(Font2, -1, this.fonts[Font2].privateSubrs, this.fonts[Font2].SubrsOffsets, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID);
        }
        this.BuildGSubrsUsed(Font2);
        if (this.fonts[Font2].privateSubrs >= 0) {
            this.NewSubrsIndexNonCID = this.BuildNewIndex(this.fonts[Font2].SubrsOffsets, this.hSubrsUsedNonCID, (byte)11);
        }
        this.NewGSubrsIndex = this.BuildNewIndex(this.gsubrOffsets, this.hGSubrsUsed, (byte)11);
    }

    protected void BuildFDSubrsOffsets(int Font2, int FD) {
        this.fonts[Font2].PrivateSubrsOffset[FD] = -1;
        this.seek(this.fonts[Font2].fdprivateOffsets[FD]);
        while (this.getPosition() < this.fonts[Font2].fdprivateOffsets[FD] + this.fonts[Font2].fdprivateLengths[FD]) {
            this.getDictItem();
            if (!Objects.equals(this.key, "Subrs")) continue;
            this.fonts[Font2].PrivateSubrsOffset[FD] = (Integer)this.args[0] + this.fonts[Font2].fdprivateOffsets[FD];
        }
        if (this.fonts[Font2].PrivateSubrsOffset[FD] >= 0) {
            this.fonts[Font2].PrivateSubrsOffsetsArray[FD] = this.getIndex(this.fonts[Font2].PrivateSubrsOffset[FD]);
        }
    }

    protected void BuildSubrUsed(int Font2, int FD, int SubrOffset, int[] SubrsOffsets, Map<Integer, int[]> hSubr, List<Integer> lSubr) {
        int LBias = this.CalcBias(SubrOffset, Font2);
        for (Integer o : this.glyphsInList) {
            int glyph = o;
            int Start2 = this.fonts[Font2].charstringsOffsets[glyph];
            int End2 = this.fonts[Font2].charstringsOffsets[glyph + 1];
            if (FD >= 0) {
                this.EmptyStack();
                this.NumOfHints = 0;
                int GlyphFD = this.fonts[Font2].FDSelect[glyph];
                if (GlyphFD != FD) continue;
                this.ReadASubr(Start2, End2, this.GBias, LBias, hSubr, lSubr, SubrsOffsets);
                continue;
            }
            this.ReadASubr(Start2, End2, this.GBias, LBias, hSubr, lSubr, SubrsOffsets);
        }
        for (int i = 0; i < lSubr.size(); ++i) {
            int Subr = lSubr.get(i);
            if (Subr >= SubrsOffsets.length - 1 || Subr < 0) continue;
            int Start3 = SubrsOffsets[Subr];
            int End3 = SubrsOffsets[Subr + 1];
            this.ReadASubr(Start3, End3, this.GBias, LBias, hSubr, lSubr, SubrsOffsets);
        }
    }

    protected void BuildGSubrsUsed(int Font2) {
        int LBias = 0;
        int SizeOfNonCIDSubrsUsed = 0;
        if (this.fonts[Font2].privateSubrs >= 0) {
            LBias = this.CalcBias(this.fonts[Font2].privateSubrs, Font2);
            SizeOfNonCIDSubrsUsed = this.lSubrsUsedNonCID.size();
        }
        for (int i = 0; i < this.lGSubrsUsed.size(); ++i) {
            int Subr = this.lGSubrsUsed.get(i);
            if (Subr >= this.gsubrOffsets.length - 1 || Subr < 0) continue;
            int Start2 = this.gsubrOffsets[Subr];
            int End2 = this.gsubrOffsets[Subr + 1];
            if (this.fonts[Font2].isCID) {
                this.ReadASubr(Start2, End2, this.GBias, 0, this.hGSubrsUsed, this.lGSubrsUsed, null);
                continue;
            }
            this.ReadASubr(Start2, End2, this.GBias, LBias, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[Font2].SubrsOffsets);
            if (SizeOfNonCIDSubrsUsed >= this.lSubrsUsedNonCID.size()) continue;
            for (int j = SizeOfNonCIDSubrsUsed; j < this.lSubrsUsedNonCID.size(); ++j) {
                int LSubr = this.lSubrsUsedNonCID.get(j);
                if (LSubr >= this.fonts[Font2].SubrsOffsets.length - 1 || LSubr < 0) continue;
                int LStart = this.fonts[Font2].SubrsOffsets[LSubr];
                int LEnd = this.fonts[Font2].SubrsOffsets[LSubr + 1];
                this.ReadASubr(LStart, LEnd, this.GBias, LBias, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[Font2].SubrsOffsets);
            }
            SizeOfNonCIDSubrsUsed = this.lSubrsUsedNonCID.size();
        }
    }

    protected void ReadASubr(int begin, int end, int GBias, int LBias, Map<Integer, int[]> hSubr, List<Integer> lSubr, int[] LSubrsOffsets) {
        this.EmptyStack();
        this.NumOfHints = 0;
        this.seek(begin);
        while (this.getPosition() < end) {
            int Subr;
            this.ReadCommand();
            int pos = this.getPosition();
            Object TopElement = null;
            if (this.arg_count > 0) {
                TopElement = this.args[this.arg_count - 1];
            }
            int NumOfArgs = this.arg_count;
            this.HandelStack();
            if (Objects.equals(this.key, "callsubr")) {
                if (NumOfArgs <= 0) continue;
                Subr = (Integer)TopElement + LBias;
                if (!hSubr.containsKey(Subr)) {
                    hSubr.put(Subr, null);
                    lSubr.add(Subr);
                }
                this.CalcHints(LSubrsOffsets[Subr], LSubrsOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
                continue;
            }
            if (Objects.equals(this.key, "callgsubr")) {
                if (NumOfArgs <= 0) continue;
                Subr = (Integer)TopElement + GBias;
                if (!this.hGSubrsUsed.containsKey(Subr)) {
                    this.hGSubrsUsed.put(Subr, null);
                    this.lGSubrsUsed.add(Subr);
                }
                this.CalcHints(this.gsubrOffsets[Subr], this.gsubrOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
                continue;
            }
            if (Objects.equals(this.key, "hstem") || Objects.equals(this.key, "vstem") || Objects.equals(this.key, "hstemhm") || Objects.equals(this.key, "vstemhm")) {
                this.NumOfHints += NumOfArgs / 2;
                continue;
            }
            if (!Objects.equals(this.key, "hintmask") && !Objects.equals(this.key, "cntrmask")) continue;
            int SizeOfMask = this.NumOfHints / 8;
            if (this.NumOfHints % 8 != 0 || SizeOfMask == 0) {
                ++SizeOfMask;
            }
            for (int i = 0; i < SizeOfMask; ++i) {
                this.getCard8();
            }
        }
    }

    protected void HandelStack() {
        int StackHandel = this.StackOpp();
        if (StackHandel < 2) {
            if (StackHandel == 1) {
                this.PushStack();
            } else {
                StackHandel *= -1;
                for (int i = 0; i < StackHandel; ++i) {
                    this.PopStack();
                }
            }
        } else {
            this.EmptyStack();
        }
    }

    protected int StackOpp() {
        if (Objects.equals(this.key, "ifelse")) {
            return -3;
        }
        if (Objects.equals(this.key, "roll") || Objects.equals(this.key, "put")) {
            return -2;
        }
        if (Objects.equals(this.key, "callsubr") || Objects.equals(this.key, "callgsubr") || Objects.equals(this.key, "add") || Objects.equals(this.key, "sub") || Objects.equals(this.key, "div") || Objects.equals(this.key, "mul") || Objects.equals(this.key, "drop") || Objects.equals(this.key, "and") || Objects.equals(this.key, "or") || Objects.equals(this.key, "eq")) {
            return -1;
        }
        if (Objects.equals(this.key, "abs") || Objects.equals(this.key, "neg") || Objects.equals(this.key, "sqrt") || Objects.equals(this.key, "exch") || Objects.equals(this.key, "index") || Objects.equals(this.key, "get") || Objects.equals(this.key, "not") || Objects.equals(this.key, "return")) {
            return 0;
        }
        if (Objects.equals(this.key, "random") || Objects.equals(this.key, "dup")) {
            return 1;
        }
        return 2;
    }

    protected void EmptyStack() {
        for (int i = 0; i < this.arg_count; ++i) {
            this.args[i] = null;
        }
        this.arg_count = 0;
    }

    protected void PopStack() {
        if (this.arg_count > 0) {
            this.args[this.arg_count - 1] = null;
            --this.arg_count;
        }
    }

    protected void PushStack() {
        ++this.arg_count;
    }

    protected void ReadCommand() {
        this.key = null;
        boolean gotKey = false;
        while (!gotKey) {
            char w;
            char second;
            char first;
            char b0 = this.getCard8();
            if (b0 == '\u001c') {
                first = this.getCard8();
                second = this.getCard8();
                this.args[this.arg_count] = first << 8 | second;
                ++this.arg_count;
                continue;
            }
            if (b0 >= ' ' && b0 <= '\u00f6') {
                this.args[this.arg_count] = b0 - 139;
                ++this.arg_count;
                continue;
            }
            if (b0 >= '\u00f7' && b0 <= '\u00fa') {
                w = this.getCard8();
                this.args[this.arg_count] = (b0 - 247) * 256 + w + 108;
                ++this.arg_count;
                continue;
            }
            if (b0 >= '\u00fb' && b0 <= '\u00fe') {
                w = this.getCard8();
                this.args[this.arg_count] = -(b0 - 251) * 256 - w - 108;
                ++this.arg_count;
                continue;
            }
            if (b0 == '\u00ff') {
                first = this.getCard8();
                second = this.getCard8();
                char third = this.getCard8();
                char fourth = this.getCard8();
                this.args[this.arg_count] = first << 24 | second << 16 | third << 8 | fourth;
                ++this.arg_count;
                continue;
            }
            if (b0 > '\u001f') continue;
            gotKey = true;
            if (b0 == '\f') {
                int b1 = this.getCard8();
                if (b1 > SubrsEscapeFuncs.length - 1) {
                    b1 = SubrsEscapeFuncs.length - 1;
                }
                this.key = SubrsEscapeFuncs[b1];
                continue;
            }
            this.key = SubrsFunctions[b0];
        }
    }

    protected int CalcHints(int begin, int end, int LBias, int GBias, int[] LSubrsOffsets) {
        this.seek(begin);
        while (this.getPosition() < end) {
            int Subr;
            this.ReadCommand();
            int pos = this.getPosition();
            Object TopElement = null;
            if (this.arg_count > 0) {
                TopElement = this.args[this.arg_count - 1];
            }
            int NumOfArgs = this.arg_count;
            this.HandelStack();
            if (Objects.equals(this.key, "callsubr")) {
                if (NumOfArgs <= 0) continue;
                Subr = (Integer)TopElement + LBias;
                this.CalcHints(LSubrsOffsets[Subr], LSubrsOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
                continue;
            }
            if (Objects.equals(this.key, "callgsubr")) {
                if (NumOfArgs <= 0) continue;
                Subr = (Integer)TopElement + GBias;
                this.CalcHints(this.gsubrOffsets[Subr], this.gsubrOffsets[Subr + 1], LBias, GBias, LSubrsOffsets);
                this.seek(pos);
                continue;
            }
            if (Objects.equals(this.key, "hstem") || Objects.equals(this.key, "vstem") || Objects.equals(this.key, "hstemhm") || Objects.equals(this.key, "vstemhm")) {
                this.NumOfHints += NumOfArgs / 2;
                continue;
            }
            if (!Objects.equals(this.key, "hintmask") && !Objects.equals(this.key, "cntrmask")) continue;
            int SizeOfMask = this.NumOfHints / 8;
            if (this.NumOfHints % 8 != 0 || SizeOfMask == 0) {
                ++SizeOfMask;
            }
            for (int i = 0; i < SizeOfMask; ++i) {
                this.getCard8();
            }
        }
        return this.NumOfHints;
    }

    protected byte[] BuildNewIndex(int[] Offsets, Map<Integer, int[]> Used, byte OperatorForUnusedEntries) throws IOException {
        int unusedCount = 0;
        int Offset = 0;
        int[] NewOffsets = new int[Offsets.length];
        for (int i = 0; i < Offsets.length; ++i) {
            NewOffsets[i] = Offset;
            if (Used.containsKey(i)) {
                if (Offsets.length <= i + 1) continue;
                Offset += Offsets[i + 1] - Offsets[i];
                continue;
            }
            ++unusedCount;
        }
        byte[] NewObjects = new byte[Offset + unusedCount];
        int unusedOffset = 0;
        for (int i = 0; i < Offsets.length - 1; ++i) {
            int start = NewOffsets[i];
            int end = NewOffsets[i + 1];
            NewOffsets[i] = start + unusedOffset;
            if (start != end) {
                this.buf.seek(Offsets[i]);
                this.buf.readFully(NewObjects, start + unusedOffset, end - start);
                continue;
            }
            NewObjects[start + unusedOffset] = OperatorForUnusedEntries;
            ++unusedOffset;
        }
        int n = Offsets.length - 1;
        NewOffsets[n] = NewOffsets[n] + unusedOffset;
        return this.AssembleIndex(NewOffsets, NewObjects);
    }

    protected byte[] AssembleIndex(int[] NewOffsets, byte[] NewObjects) {
        char Count = (char)(NewOffsets.length - 1);
        int Size = NewOffsets[NewOffsets.length - 1];
        int Offsize = Size <= 255 ? 1 : (Size <= 65535 ? 2 : (Size <= 0xFFFFFF ? 3 : 4));
        byte[] NewIndex = new byte[3 + Offsize * (Count + '\u0001') + NewObjects.length];
        int Place = 0;
        NewIndex[Place++] = (byte)(Count >>> 8 & 0xFF);
        NewIndex[Place++] = (byte)(Count >>> 0 & 0xFF);
        NewIndex[Place++] = Offsize;
        for (int newOffset : NewOffsets) {
            int Num = newOffset - NewOffsets[0] + 1;
            switch (Offsize) {
                case 4: {
                    NewIndex[Place++] = (byte)(Num >>> 24 & 0xFF);
                }
                case 3: {
                    NewIndex[Place++] = (byte)(Num >>> 16 & 0xFF);
                }
                case 2: {
                    NewIndex[Place++] = (byte)(Num >>> 8 & 0xFF);
                }
                case 1: {
                    NewIndex[Place++] = (byte)(Num >>> 0 & 0xFF);
                }
            }
        }
        for (byte newObject : NewObjects) {
            NewIndex[Place++] = newObject;
        }
        return NewIndex;
    }

    protected byte[] BuildNewFile(int Font2) {
        this.OutputList = new LinkedList();
        this.CopyHeader();
        this.BuildIndexHeader(1, 1, 1);
        this.OutputList.addLast(new CFFFont.UInt8Item((char)(1 + this.fonts[Font2].name.length())));
        this.OutputList.addLast(new CFFFont.StringItem(this.fonts[Font2].name));
        this.BuildIndexHeader(1, 2, 1);
        CFFFont.IndexOffsetItem topdictIndex1Ref = new CFFFont.IndexOffsetItem(2);
        this.OutputList.addLast(topdictIndex1Ref);
        CFFFont.IndexBaseItem topdictBase = new CFFFont.IndexBaseItem();
        this.OutputList.addLast(topdictBase);
        CFFFont.DictOffsetItem charsetRef = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem charstringsRef = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem fdarrayRef = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem fdselectRef = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem privateRef = new CFFFont.DictOffsetItem();
        if (!this.fonts[Font2].isCID) {
            this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[Font2].nstrings));
            this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[Font2].nstrings + 1));
            this.OutputList.addLast(new CFFFont.DictNumberItem(0));
            this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
            this.OutputList.addLast(new CFFFont.UInt8Item('\u001e'));
            this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[Font2].nglyphs));
            this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
            this.OutputList.addLast(new CFFFont.UInt8Item('\"'));
        }
        this.seek(this.topdictOffsets[Font2]);
        while (this.getPosition() < this.topdictOffsets[Font2 + 1]) {
            int p1 = this.getPosition();
            this.getDictItem();
            int p2 = this.getPosition();
            if (Objects.equals(this.key, "Encoding") || Objects.equals(this.key, "Private") || Objects.equals(this.key, "FDSelect") || Objects.equals(this.key, "FDArray") || Objects.equals(this.key, "charset") || Objects.equals(this.key, "CharStrings")) continue;
            this.OutputList.add(new CFFFont.RangeItem(this.buf, p1, p2 - p1));
        }
        this.CreateKeys(fdarrayRef, fdselectRef, charsetRef, charstringsRef);
        this.OutputList.addLast(new CFFFont.IndexMarkerItem(topdictIndex1Ref, topdictBase));
        if (this.fonts[Font2].isCID) {
            this.OutputList.addLast(this.getEntireIndexRange(this.stringIndexOffset));
        } else {
            this.CreateNewStringIndex(Font2);
        }
        this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewGSubrsIndex), 0, this.NewGSubrsIndex.length));
        if (this.fonts[Font2].isCID) {
            this.OutputList.addLast(new CFFFont.MarkerItem(fdselectRef));
            if (this.fonts[Font2].fdselectOffset >= 0) {
                this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.fonts[Font2].fdselectOffset, this.fonts[Font2].FDSelectLength));
            } else {
                this.CreateFDSelect(fdselectRef, this.fonts[Font2].nglyphs);
            }
            this.OutputList.addLast(new CFFFont.MarkerItem(charsetRef));
            this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.fonts[Font2].charsetOffset, this.fonts[Font2].CharsetLength));
            if (this.fonts[Font2].fdarrayOffset >= 0) {
                this.OutputList.addLast(new CFFFont.MarkerItem(fdarrayRef));
                this.Reconstruct(Font2);
            } else {
                this.CreateFDArray(fdarrayRef, privateRef, Font2);
            }
        } else {
            this.CreateFDSelect(fdselectRef, this.fonts[Font2].nglyphs);
            this.CreateCharset(charsetRef, this.fonts[Font2].nglyphs);
            this.CreateFDArray(fdarrayRef, privateRef, Font2);
        }
        if (this.fonts[Font2].privateOffset >= 0) {
            CFFFont.IndexBaseItem PrivateBase = new CFFFont.IndexBaseItem();
            this.OutputList.addLast(PrivateBase);
            this.OutputList.addLast(new CFFFont.MarkerItem(privateRef));
            CFFFont.DictOffsetItem Subr = new CFFFont.DictOffsetItem();
            this.CreateNonCIDPrivate(Font2, Subr);
            this.CreateNonCIDSubrs(Font2, PrivateBase, Subr);
        }
        this.OutputList.addLast(new CFFFont.MarkerItem(charstringsRef));
        this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewCharStringsIndex), 0, this.NewCharStringsIndex.length));
        int[] currentOffset = new int[]{0};
        for (CFFFont.Item item : this.OutputList) {
            item.increment(currentOffset);
        }
        for (CFFFont.Item item : this.OutputList) {
            item.xref();
        }
        int size = currentOffset[0];
        byte[] b = new byte[size];
        for (CFFFont.Item item : this.OutputList) {
            item.emit(b);
        }
        return b;
    }

    protected void CopyHeader() {
        this.seek(0);
        char major = this.getCard8();
        char minor = this.getCard8();
        char hdrSize = this.getCard8();
        char offSize = this.getCard8();
        this.nextIndexOffset = hdrSize;
        this.OutputList.addLast(new CFFFont.RangeItem(this.buf, 0, hdrSize));
    }

    protected void BuildIndexHeader(int Count, int Offsize, int First2) {
        this.OutputList.addLast(new CFFFont.UInt16Item((char)Count));
        this.OutputList.addLast(new CFFFont.UInt8Item((char)Offsize));
        switch (Offsize) {
            case 1: {
                this.OutputList.addLast(new CFFFont.UInt8Item((char)First2));
                break;
            }
            case 2: {
                this.OutputList.addLast(new CFFFont.UInt16Item((char)First2));
                break;
            }
            case 3: {
                this.OutputList.addLast(new CFFFont.UInt24Item((char)First2));
                break;
            }
            case 4: {
                this.OutputList.addLast(new CFFFont.UInt32Item((char)First2));
                break;
            }
        }
    }

    protected void CreateKeys(CFFFont.OffsetItem fdarrayRef, CFFFont.OffsetItem fdselectRef, CFFFont.OffsetItem charsetRef, CFFFont.OffsetItem charstringsRef) {
        this.OutputList.addLast(fdarrayRef);
        this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
        this.OutputList.addLast(new CFFFont.UInt8Item('$'));
        this.OutputList.addLast(fdselectRef);
        this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
        this.OutputList.addLast(new CFFFont.UInt8Item('%'));
        this.OutputList.addLast(charsetRef);
        this.OutputList.addLast(new CFFFont.UInt8Item('\u000f'));
        this.OutputList.addLast(charstringsRef);
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0011'));
    }

    protected void CreateNewStringIndex(int Font2) {
        String fdFontName = this.fonts[Font2].name + "-OneRange";
        if (fdFontName.length() > 127) {
            fdFontName = fdFontName.substring(0, 127);
        }
        String extraStrings = "AdobeIdentity" + fdFontName;
        int origStringsLen = this.stringOffsets[this.stringOffsets.length - 1] - this.stringOffsets[0];
        int stringsBaseOffset = this.stringOffsets[0] - 1;
        int stringsIndexOffSize = origStringsLen + extraStrings.length() <= 255 ? 1 : (origStringsLen + extraStrings.length() <= 65535 ? 2 : (origStringsLen + extraStrings.length() <= 0xFFFFFF ? 3 : 4));
        this.OutputList.addLast(new CFFFont.UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
        this.OutputList.addLast(new CFFFont.UInt8Item((char)stringsIndexOffSize));
        for (int stringOffset : this.stringOffsets) {
            this.OutputList.addLast(new CFFFont.IndexOffsetItem(stringsIndexOffSize, stringOffset - stringsBaseOffset));
        }
        int currentStringsOffset = this.stringOffsets[this.stringOffsets.length - 1] - stringsBaseOffset;
        this.OutputList.addLast(new CFFFont.IndexOffsetItem(stringsIndexOffSize, currentStringsOffset += "Adobe".length()));
        this.OutputList.addLast(new CFFFont.IndexOffsetItem(stringsIndexOffSize, currentStringsOffset += "Identity".length()));
        this.OutputList.addLast(new CFFFont.IndexOffsetItem(stringsIndexOffSize, currentStringsOffset += fdFontName.length()));
        this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.stringOffsets[0], origStringsLen));
        this.OutputList.addLast(new CFFFont.StringItem(extraStrings));
    }

    protected void CreateFDSelect(CFFFont.OffsetItem fdselectRef, int nglyphs) {
        this.OutputList.addLast(new CFFFont.MarkerItem(fdselectRef));
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0003'));
        this.OutputList.addLast(new CFFFont.UInt16Item('\u0001'));
        this.OutputList.addLast(new CFFFont.UInt16Item('\u0000'));
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0000'));
        this.OutputList.addLast(new CFFFont.UInt16Item((char)nglyphs));
    }

    protected void CreateCharset(CFFFont.OffsetItem charsetRef, int nglyphs) {
        this.OutputList.addLast(new CFFFont.MarkerItem(charsetRef));
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0002'));
        this.OutputList.addLast(new CFFFont.UInt16Item('\u0001'));
        this.OutputList.addLast(new CFFFont.UInt16Item((char)(nglyphs - 1)));
    }

    protected void CreateFDArray(CFFFont.OffsetItem fdarrayRef, CFFFont.OffsetItem privateRef, int Font2) {
        this.OutputList.addLast(new CFFFont.MarkerItem(fdarrayRef));
        this.BuildIndexHeader(1, 1, 1);
        CFFFont.IndexOffsetItem privateIndex1Ref = new CFFFont.IndexOffsetItem(1);
        this.OutputList.addLast(privateIndex1Ref);
        CFFFont.IndexBaseItem privateBase = new CFFFont.IndexBaseItem();
        this.OutputList.addLast(privateBase);
        int NewSize = this.fonts[Font2].privateLength;
        int OrgSubrsOffsetSize = this.CalcSubrOffsetSize(this.fonts[Font2].privateOffset, this.fonts[Font2].privateLength);
        if (OrgSubrsOffsetSize != 0) {
            NewSize += 5 - OrgSubrsOffsetSize;
        }
        this.OutputList.addLast(new CFFFont.DictNumberItem(NewSize));
        this.OutputList.addLast(privateRef);
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0012'));
        this.OutputList.addLast(new CFFFont.IndexMarkerItem(privateIndex1Ref, privateBase));
    }

    void Reconstruct(int Font2) {
        CFFFont.OffsetItem[] fdPrivate = new CFFFont.DictOffsetItem[this.fonts[Font2].FDArrayOffsets.length - 1];
        CFFFont.IndexBaseItem[] fdPrivateBase = new CFFFont.IndexBaseItem[this.fonts[Font2].fdprivateOffsets.length];
        CFFFont.OffsetItem[] fdSubrs = new CFFFont.DictOffsetItem[this.fonts[Font2].fdprivateOffsets.length];
        this.ReconstructFDArray(Font2, fdPrivate);
        this.ReconstructPrivateDict(Font2, fdPrivate, fdPrivateBase, fdSubrs);
        this.ReconstructPrivateSubrs(Font2, fdPrivateBase, fdSubrs);
    }

    void ReconstructFDArray(int Font2, CFFFont.OffsetItem[] fdPrivate) {
        this.BuildIndexHeader(this.fonts[Font2].FDArrayCount, this.fonts[Font2].FDArrayOffsize, 1);
        CFFFont.IndexOffsetItem[] fdOffsets = new CFFFont.IndexOffsetItem[this.fonts[Font2].FDArrayOffsets.length - 1];
        for (int i = 0; i < this.fonts[Font2].FDArrayOffsets.length - 1; ++i) {
            fdOffsets[i] = new CFFFont.IndexOffsetItem(this.fonts[Font2].FDArrayOffsize);
            this.OutputList.addLast(fdOffsets[i]);
        }
        CFFFont.IndexBaseItem fdArrayBase = new CFFFont.IndexBaseItem();
        this.OutputList.addLast(fdArrayBase);
        for (int k = 0; k < this.fonts[Font2].FDArrayOffsets.length - 1; ++k) {
            if (this.FDArrayUsed.containsKey(k)) {
                this.seek(this.fonts[Font2].FDArrayOffsets[k]);
                while (this.getPosition() < this.fonts[Font2].FDArrayOffsets[k + 1]) {
                    int p1 = this.getPosition();
                    this.getDictItem();
                    int p2 = this.getPosition();
                    if (Objects.equals(this.key, "Private")) {
                        int NewSize = (Integer)this.args[0];
                        int OrgSubrsOffsetSize = this.CalcSubrOffsetSize(this.fonts[Font2].fdprivateOffsets[k], this.fonts[Font2].fdprivateLengths[k]);
                        if (OrgSubrsOffsetSize != 0) {
                            NewSize += 5 - OrgSubrsOffsetSize;
                        }
                        this.OutputList.addLast(new CFFFont.DictNumberItem(NewSize));
                        fdPrivate[k] = new CFFFont.DictOffsetItem();
                        this.OutputList.addLast(fdPrivate[k]);
                        this.OutputList.addLast(new CFFFont.UInt8Item('\u0012'));
                        this.seek(p2);
                        continue;
                    }
                    this.OutputList.addLast(new CFFFont.RangeItem(this.buf, p1, p2 - p1));
                }
            }
            this.OutputList.addLast(new CFFFont.IndexMarkerItem(fdOffsets[k], fdArrayBase));
        }
    }

    void ReconstructPrivateDict(int Font2, CFFFont.OffsetItem[] fdPrivate, CFFFont.IndexBaseItem[] fdPrivateBase, CFFFont.OffsetItem[] fdSubrs) {
        for (int i = 0; i < this.fonts[Font2].fdprivateOffsets.length; ++i) {
            if (!this.FDArrayUsed.containsKey(i)) continue;
            this.OutputList.addLast(new CFFFont.MarkerItem(fdPrivate[i]));
            fdPrivateBase[i] = new CFFFont.IndexBaseItem();
            this.OutputList.addLast(fdPrivateBase[i]);
            this.seek(this.fonts[Font2].fdprivateOffsets[i]);
            while (this.getPosition() < this.fonts[Font2].fdprivateOffsets[i] + this.fonts[Font2].fdprivateLengths[i]) {
                int p1 = this.getPosition();
                this.getDictItem();
                int p2 = this.getPosition();
                if (Objects.equals(this.key, "Subrs")) {
                    fdSubrs[i] = new CFFFont.DictOffsetItem();
                    this.OutputList.addLast(fdSubrs[i]);
                    this.OutputList.addLast(new CFFFont.UInt8Item('\u0013'));
                    continue;
                }
                this.OutputList.addLast(new CFFFont.RangeItem(this.buf, p1, p2 - p1));
            }
        }
    }

    void ReconstructPrivateSubrs(int Font2, CFFFont.IndexBaseItem[] fdPrivateBase, CFFFont.OffsetItem[] fdSubrs) {
        for (int i = 0; i < this.fonts[Font2].fdprivateLengths.length; ++i) {
            if (fdSubrs[i] == null || this.fonts[Font2].PrivateSubrsOffset[i] < 0) continue;
            this.OutputList.addLast(new CFFFont.SubrMarkerItem(fdSubrs[i], fdPrivateBase[i]));
            this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewLSubrsIndex[i]), 0, this.NewLSubrsIndex[i].length));
        }
    }

    int CalcSubrOffsetSize(int Offset, int Size) {
        int OffsetSize = 0;
        this.seek(Offset);
        while (this.getPosition() < Offset + Size) {
            int p1 = this.getPosition();
            this.getDictItem();
            int p2 = this.getPosition();
            if (!Objects.equals(this.key, "Subrs")) continue;
            OffsetSize = p2 - p1 - 1;
        }
        return OffsetSize;
    }

    protected int countEntireIndexRange(int indexOffset) {
        this.seek(indexOffset);
        char count = this.getCard16();
        if (count == '\u0000') {
            return 2;
        }
        char indexOffSize = this.getCard8();
        this.seek(indexOffset + 2 + 1 + count * indexOffSize);
        int size = this.getOffset(indexOffSize) - 1;
        return 3 + (count + '\u0001') * indexOffSize + size;
    }

    void CreateNonCIDPrivate(int Font2, CFFFont.OffsetItem Subr) {
        this.seek(this.fonts[Font2].privateOffset);
        while (this.getPosition() < this.fonts[Font2].privateOffset + this.fonts[Font2].privateLength) {
            int p1 = this.getPosition();
            this.getDictItem();
            int p2 = this.getPosition();
            if (Objects.equals(this.key, "Subrs")) {
                this.OutputList.addLast(Subr);
                this.OutputList.addLast(new CFFFont.UInt8Item('\u0013'));
                continue;
            }
            this.OutputList.addLast(new CFFFont.RangeItem(this.buf, p1, p2 - p1));
        }
    }

    void CreateNonCIDSubrs(int Font2, CFFFont.IndexBaseItem PrivateBase, CFFFont.OffsetItem Subrs) {
        this.OutputList.addLast(new CFFFont.SubrMarkerItem(Subrs, PrivateBase));
        if (this.NewSubrsIndexNonCID != null) {
            this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewSubrsIndexNonCID), 0, this.NewSubrsIndexNonCID.length));
        }
    }
}

