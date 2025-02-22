/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.text;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

public class BidiAttributedCharacterIterator
implements AttributedCharacterIterator {
    private AttributedCharacterIterator reorderedACI;
    private FontRenderContext frc;
    private int chunkStart;
    private int[] newCharOrder;
    private static final Float FLOAT_NAN = Float.valueOf(Float.NaN);

    protected BidiAttributedCharacterIterator(AttributedCharacterIterator reorderedACI, FontRenderContext frc, int chunkStart, int[] newCharOrder) {
        this.reorderedACI = reorderedACI;
        this.frc = frc;
        this.chunkStart = chunkStart;
        this.newCharOrder = newCharOrder;
    }

    public BidiAttributedCharacterIterator(AttributedCharacterIterator aci, FontRenderContext frc, int chunkStart) {
        Float dy;
        Float dx;
        Float y;
        int i;
        this.frc = frc;
        this.chunkStart = chunkStart;
        aci.first();
        int numChars = aci.getEndIndex() - aci.getBeginIndex();
        StringBuffer strB = new StringBuffer(numChars);
        char c = aci.first();
        for (int i2 = 0; i2 < numChars; ++i2) {
            strB.append(c);
            c = aci.next();
        }
        AttributedString as = new AttributedString(strB.toString());
        int start = aci.getBeginIndex();
        int end = aci.getEndIndex();
        int index = start;
        while (index < end) {
            aci.setIndex(index);
            Map<AttributedCharacterIterator.Attribute, Object> attrMap = aci.getAttributes();
            int extent = aci.getRunLimit();
            HashMap<AttributedCharacterIterator.Attribute, Object> destMap = new HashMap<AttributedCharacterIterator.Attribute, Object>(attrMap.size());
            for (Map.Entry<AttributedCharacterIterator.Attribute, Object> o : attrMap.entrySet()) {
                Object value;
                Map.Entry<AttributedCharacterIterator.Attribute, Object> e = o;
                AttributedCharacterIterator.Attribute key = e.getKey();
                if (key == null || (value = e.getValue()) == null) continue;
                destMap.put(key, value);
            }
            as.addAttributes(destMap, index - start, extent - start);
            index = extent;
        }
        TextLayout tl = new TextLayout(as.getIterator(), frc);
        int[] charIndices = new int[numChars];
        int[] charLevels = new int[numChars];
        int runStart = 0;
        byte currBiDi = tl.getCharacterLevel(0);
        charIndices[0] = 0;
        charLevels[0] = currBiDi;
        byte maxBiDi = currBiDi;
        for (i = 1; i < numChars; ++i) {
            byte newBiDi = tl.getCharacterLevel(i);
            charIndices[i] = i;
            charLevels[i] = newBiDi;
            if (newBiDi == currBiDi) continue;
            as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL, currBiDi, runStart, i);
            runStart = i;
            currBiDi = newBiDi;
            if (newBiDi <= maxBiDi) continue;
            maxBiDi = newBiDi;
        }
        as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL, currBiDi, runStart, numChars);
        aci = as.getIterator();
        if (runStart == 0 && currBiDi == 0) {
            this.reorderedACI = aci;
            this.newCharOrder = new int[numChars];
            for (i = 0; i < numChars; ++i) {
                this.newCharOrder[i] = chunkStart + i;
            }
            return;
        }
        this.newCharOrder = this.doBidiReorder(charIndices, charLevels, numChars, maxBiDi);
        StringBuffer reorderedString = new StringBuffer(numChars);
        int reorderedFirstChar = 0;
        for (int i3 = 0; i3 < numChars; ++i3) {
            byte bidiLevel;
            int srcIdx = this.newCharOrder[i3];
            char c2 = aci.setIndex(srcIdx);
            if (srcIdx == 0) {
                reorderedFirstChar = i3;
            }
            if (((bidiLevel = tl.getCharacterLevel(srcIdx)) & 1) != 0) {
                c2 = (char)BidiAttributedCharacterIterator.mirrorChar(c2);
            }
            reorderedString.append(c2);
        }
        AttributedString reorderedAS = new AttributedString(reorderedString.toString());
        Map[] attrs = new Map[numChars];
        int start2 = aci.getBeginIndex();
        int end2 = aci.getEndIndex();
        int index2 = start2;
        while (index2 < end2) {
            aci.setIndex(index2);
            Map<AttributedCharacterIterator.Attribute, Object> attrMap = aci.getAttributes();
            int extent = aci.getRunLimit();
            for (int i4 = index2; i4 < extent; ++i4) {
                attrs[i4 - start2] = attrMap;
            }
            index2 = extent;
        }
        runStart = 0;
        Map prevAttrMap = attrs[this.newCharOrder[0]];
        for (int i5 = 1; i5 < numChars; ++i5) {
            Map attrMap = attrs[this.newCharOrder[i5]];
            if (attrMap == prevAttrMap) continue;
            reorderedAS.addAttributes(prevAttrMap, runStart, i5);
            prevAttrMap = attrMap;
            runStart = i5;
        }
        reorderedAS.addAttributes(prevAttrMap, runStart, numChars);
        aci.first();
        Float x = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.X);
        if (x != null && !x.isNaN()) {
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, FLOAT_NAN, reorderedFirstChar, reorderedFirstChar + 1);
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, x, 0, 1);
        }
        if ((y = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.Y)) != null && !y.isNaN()) {
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, FLOAT_NAN, reorderedFirstChar, reorderedFirstChar + 1);
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, y, 0, 1);
        }
        if ((dx = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.DX)) != null && !dx.isNaN()) {
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.DX, FLOAT_NAN, reorderedFirstChar, reorderedFirstChar + 1);
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.DX, dx, 0, 1);
        }
        if ((dy = (Float)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.DY)) != null && !dy.isNaN()) {
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.DY, FLOAT_NAN, reorderedFirstChar, reorderedFirstChar + 1);
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.DY, dy, 0, 1);
        }
        reorderedAS = ArabicTextHandler.assignArabicForms(reorderedAS);
        int i6 = 0;
        while (i6 < this.newCharOrder.length) {
            int n = i6++;
            this.newCharOrder[n] = this.newCharOrder[n] + chunkStart;
        }
        this.reorderedACI = reorderedAS.getIterator();
    }

    public int[] getCharMap() {
        return this.newCharOrder;
    }

    private int[] doBidiReorder(int[] charIndices, int[] charLevels, int numChars, int highestLevel) {
        if (highestLevel == 0) {
            return charIndices;
        }
        int currentIndex = 0;
        while (currentIndex < numChars) {
            while (currentIndex < numChars && charLevels[currentIndex] < highestLevel) {
                ++currentIndex;
            }
            if (currentIndex == numChars) break;
            int startIndex = currentIndex++;
            while (currentIndex < numChars && charLevels[currentIndex] == highestLevel) {
                ++currentIndex;
            }
            int endIndex = currentIndex - 1;
            int middle = (endIndex - startIndex >> 1) + 1;
            for (int i = 0; i < middle; ++i) {
                int tmp = charIndices[startIndex + i];
                charIndices[startIndex + i] = charIndices[endIndex - i];
                charIndices[endIndex - i] = tmp;
                charLevels[startIndex + i] = highestLevel - 1;
                charLevels[endIndex - i] = highestLevel - 1;
            }
        }
        return this.doBidiReorder(charIndices, charLevels, numChars, highestLevel - 1);
    }

    public Set getAllAttributeKeys() {
        return this.reorderedACI.getAllAttributeKeys();
    }

    @Override
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute) {
        return this.reorderedACI.getAttribute(attribute);
    }

    public Map getAttributes() {
        return this.reorderedACI.getAttributes();
    }

    @Override
    public int getRunLimit() {
        return this.reorderedACI.getRunLimit();
    }

    @Override
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
        return this.reorderedACI.getRunLimit(attribute);
    }

    public int getRunLimit(Set attributes) {
        return this.reorderedACI.getRunLimit(attributes);
    }

    @Override
    public int getRunStart() {
        return this.reorderedACI.getRunStart();
    }

    @Override
    public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
        return this.reorderedACI.getRunStart(attribute);
    }

    public int getRunStart(Set attributes) {
        return this.reorderedACI.getRunStart(attributes);
    }

    @Override
    public Object clone() {
        return new BidiAttributedCharacterIterator((AttributedCharacterIterator)this.reorderedACI.clone(), this.frc, this.chunkStart, (int[])this.newCharOrder.clone());
    }

    @Override
    public char current() {
        return this.reorderedACI.current();
    }

    @Override
    public char first() {
        return this.reorderedACI.first();
    }

    @Override
    public int getBeginIndex() {
        return this.reorderedACI.getBeginIndex();
    }

    @Override
    public int getEndIndex() {
        return this.reorderedACI.getEndIndex();
    }

    @Override
    public int getIndex() {
        return this.reorderedACI.getIndex();
    }

    @Override
    public char last() {
        return this.reorderedACI.last();
    }

    @Override
    public char next() {
        return this.reorderedACI.next();
    }

    @Override
    public char previous() {
        return this.reorderedACI.previous();
    }

    @Override
    public char setIndex(int position) {
        return this.reorderedACI.setIndex(position);
    }

    public static int mirrorChar(int c) {
        switch (c) {
            case 40: {
                return 41;
            }
            case 41: {
                return 40;
            }
            case 60: {
                return 62;
            }
            case 62: {
                return 60;
            }
            case 91: {
                return 93;
            }
            case 93: {
                return 91;
            }
            case 123: {
                return 125;
            }
            case 125: {
                return 123;
            }
            case 171: {
                return 187;
            }
            case 187: {
                return 171;
            }
            case 8249: {
                return 8250;
            }
            case 8250: {
                return 8249;
            }
            case 8261: {
                return 8262;
            }
            case 8262: {
                return 8261;
            }
            case 8317: {
                return 8318;
            }
            case 8318: {
                return 8317;
            }
            case 8333: {
                return 8334;
            }
            case 8334: {
                return 8333;
            }
            case 8712: {
                return 8715;
            }
            case 8713: {
                return 8716;
            }
            case 8714: {
                return 8717;
            }
            case 8715: {
                return 8712;
            }
            case 8716: {
                return 8713;
            }
            case 8717: {
                return 8714;
            }
            case 8764: {
                return 8765;
            }
            case 8765: {
                return 8764;
            }
            case 8771: {
                return 8909;
            }
            case 8786: {
                return 8787;
            }
            case 8787: {
                return 8786;
            }
            case 8788: {
                return 8789;
            }
            case 8789: {
                return 8788;
            }
            case 8804: {
                return 8805;
            }
            case 8805: {
                return 8804;
            }
            case 8806: {
                return 8807;
            }
            case 8807: {
                return 8806;
            }
            case 8808: {
                return 8809;
            }
            case 8809: {
                return 8808;
            }
            case 8810: {
                return 8811;
            }
            case 8811: {
                return 8810;
            }
            case 8814: {
                return 8815;
            }
            case 8815: {
                return 8814;
            }
            case 8816: {
                return 8817;
            }
            case 8817: {
                return 8816;
            }
            case 8818: {
                return 8819;
            }
            case 8819: {
                return 8818;
            }
            case 8820: {
                return 8821;
            }
            case 8821: {
                return 8820;
            }
            case 8822: {
                return 8823;
            }
            case 8823: {
                return 8822;
            }
            case 8824: {
                return 8825;
            }
            case 8825: {
                return 8824;
            }
            case 8826: {
                return 8827;
            }
            case 8827: {
                return 8826;
            }
            case 8828: {
                return 8829;
            }
            case 8829: {
                return 8828;
            }
            case 8830: {
                return 8831;
            }
            case 8831: {
                return 8830;
            }
            case 8832: {
                return 8833;
            }
            case 8833: {
                return 8832;
            }
            case 8834: {
                return 8835;
            }
            case 8835: {
                return 8834;
            }
            case 8836: {
                return 8837;
            }
            case 8837: {
                return 8836;
            }
            case 8838: {
                return 8839;
            }
            case 8839: {
                return 8838;
            }
            case 8840: {
                return 8841;
            }
            case 8841: {
                return 8840;
            }
            case 8842: {
                return 8843;
            }
            case 8843: {
                return 8842;
            }
            case 8847: {
                return 8848;
            }
            case 8848: {
                return 8847;
            }
            case 8849: {
                return 8850;
            }
            case 8850: {
                return 8849;
            }
            case 8866: {
                return 8867;
            }
            case 8867: {
                return 8866;
            }
            case 8880: {
                return 8881;
            }
            case 8881: {
                return 8880;
            }
            case 8882: {
                return 8883;
            }
            case 8883: {
                return 8882;
            }
            case 8884: {
                return 8885;
            }
            case 8885: {
                return 8884;
            }
            case 8886: {
                return 8887;
            }
            case 8887: {
                return 8886;
            }
            case 8905: {
                return 8906;
            }
            case 8906: {
                return 8905;
            }
            case 8907: {
                return 8908;
            }
            case 8908: {
                return 8907;
            }
            case 8909: {
                return 8771;
            }
            case 8912: {
                return 8913;
            }
            case 8913: {
                return 8912;
            }
            case 8918: {
                return 8919;
            }
            case 8919: {
                return 8918;
            }
            case 8920: {
                return 8921;
            }
            case 8921: {
                return 8920;
            }
            case 8922: {
                return 8923;
            }
            case 8923: {
                return 8922;
            }
            case 8924: {
                return 8925;
            }
            case 8925: {
                return 8924;
            }
            case 8926: {
                return 8927;
            }
            case 8927: {
                return 8926;
            }
            case 8928: {
                return 8929;
            }
            case 8929: {
                return 8928;
            }
            case 8930: {
                return 8931;
            }
            case 8931: {
                return 8930;
            }
            case 8932: {
                return 8933;
            }
            case 8933: {
                return 8932;
            }
            case 8934: {
                return 8935;
            }
            case 8935: {
                return 8934;
            }
            case 8936: {
                return 8937;
            }
            case 8937: {
                return 8936;
            }
            case 8938: {
                return 8939;
            }
            case 8939: {
                return 8938;
            }
            case 8940: {
                return 8941;
            }
            case 8941: {
                return 8940;
            }
            case 8944: {
                return 8945;
            }
            case 8945: {
                return 8944;
            }
            case 8968: {
                return 8969;
            }
            case 8969: {
                return 8968;
            }
            case 8970: {
                return 8971;
            }
            case 8971: {
                return 8970;
            }
            case 9001: {
                return 9002;
            }
            case 9002: {
                return 9001;
            }
            case 12296: {
                return 12297;
            }
            case 12297: {
                return 12296;
            }
            case 12298: {
                return 12299;
            }
            case 12299: {
                return 12298;
            }
            case 12300: {
                return 12301;
            }
            case 12301: {
                return 12300;
            }
            case 12302: {
                return 12303;
            }
            case 12303: {
                return 12302;
            }
            case 12304: {
                return 12305;
            }
            case 12305: {
                return 12304;
            }
            case 12308: {
                return 12309;
            }
            case 12309: {
                return 12308;
            }
            case 12310: {
                return 12311;
            }
            case 12311: {
                return 12310;
            }
            case 12312: {
                return 12313;
            }
            case 12313: {
                return 12312;
            }
            case 12314: {
                return 12315;
            }
            case 12315: {
                return 12314;
            }
        }
        return c;
    }
}

