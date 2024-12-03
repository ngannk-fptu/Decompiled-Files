/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.factories.RomanAlphabetFactory;
import com.lowagie.text.factories.RomanNumberFactory;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfNumberTree;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class PdfPageLabels {
    public static final int DECIMAL_ARABIC_NUMERALS = 0;
    public static final int UPPERCASE_ROMAN_NUMERALS = 1;
    public static final int LOWERCASE_ROMAN_NUMERALS = 2;
    public static final int UPPERCASE_LETTERS = 3;
    public static final int LOWERCASE_LETTERS = 4;
    public static final int EMPTY = 5;
    static PdfName[] numberingStyle = new PdfName[]{PdfName.D, PdfName.R, new PdfName("r"), PdfName.A, new PdfName("a")};
    private HashMap<Integer, PdfDictionary> map = new HashMap();

    public PdfPageLabels() {
        this.addPageLabel(1, 0, null, 1);
    }

    public void addPageLabel(int page, int numberStyle, String text, int firstPage) {
        if (page < 1 || firstPage < 1) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("in.a.page.label.the.page.numbers.must.be.greater.or.equal.to.1"));
        }
        PdfDictionary dic = new PdfDictionary();
        if (numberStyle >= 0 && numberStyle < numberingStyle.length) {
            dic.put(PdfName.S, numberingStyle[numberStyle]);
        }
        if (text != null) {
            dic.put(PdfName.P, new PdfString(text, "UnicodeBig"));
        }
        if (firstPage != 1) {
            dic.put(PdfName.ST, new PdfNumber(firstPage));
        }
        this.map.put(page - 1, dic);
    }

    public void addPageLabel(int page, int numberStyle, String text) {
        this.addPageLabel(page, numberStyle, text, 1);
    }

    public void addPageLabel(int page, int numberStyle) {
        this.addPageLabel(page, numberStyle, null, 1);
    }

    public void addPageLabel(PdfPageLabelFormat format) {
        this.addPageLabel(format.physicalPage, format.numberStyle, format.prefix, format.logicalPage);
    }

    public void removePageLabel(int page) {
        if (page <= 1) {
            return;
        }
        this.map.remove(page - 1);
    }

    PdfDictionary getDictionary(PdfWriter writer) {
        try {
            return PdfNumberTree.writeTree(this.map, writer);
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static String[] getPageLabels(PdfReader reader) {
        int n = reader.getNumberOfPages();
        PdfDictionary dict = reader.getCatalog();
        PdfDictionary labels = (PdfDictionary)PdfReader.getPdfObjectRelease(dict.get(PdfName.PAGELABELS));
        if (labels == null) {
            return null;
        }
        String[] labelstrings = new String[n];
        HashMap<PdfObject, PdfObject> numberTree = PdfNumberTree.readTree(labels);
        int pagecount = 1;
        int type = 68;
        String prefix = "";
        for (int i = 0; i < n; ++i) {
            Integer current = i;
            if (numberTree.containsKey(current)) {
                PdfDictionary d = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)numberTree.get(current));
                pagecount = d.contains(PdfName.ST) ? ((PdfNumber)d.get(PdfName.ST)).intValue() : 1;
                prefix = d.contains(PdfName.P) ? ((PdfString)d.get(PdfName.P)).toUnicodeString() : "";
                if (d.contains(PdfName.S)) {
                    type = d.get(PdfName.S).toString().charAt(1);
                }
            }
            switch (type) {
                default: {
                    labelstrings[i] = prefix + pagecount;
                    break;
                }
                case 82: {
                    labelstrings[i] = prefix + RomanNumberFactory.getUpperCaseString(pagecount);
                    break;
                }
                case 114: {
                    labelstrings[i] = prefix + RomanNumberFactory.getLowerCaseString(pagecount);
                    break;
                }
                case 65: {
                    labelstrings[i] = prefix + RomanAlphabetFactory.getUpperCaseString(pagecount);
                    break;
                }
                case 97: {
                    labelstrings[i] = prefix + RomanAlphabetFactory.getLowerCaseString(pagecount);
                }
            }
            ++pagecount;
        }
        return labelstrings;
    }

    public static PdfPageLabelFormat[] getPageLabelFormats(PdfReader reader) {
        PdfDictionary dict = reader.getCatalog();
        PdfDictionary labels = (PdfDictionary)PdfReader.getPdfObjectRelease(dict.get(PdfName.PAGELABELS));
        if (labels == null) {
            return null;
        }
        HashMap<PdfObject, PdfObject> numberTree = PdfNumberTree.readTree(labels);
        Object[] numbers = new Integer[numberTree.size()];
        numbers = numberTree.keySet().toArray(numbers);
        Arrays.sort(numbers);
        PdfPageLabelFormat[] formats = new PdfPageLabelFormat[numberTree.size()];
        for (int k = 0; k < numbers.length; ++k) {
            int numberStyle;
            Object key = numbers[k];
            PdfDictionary d = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)numberTree.get(key));
            int pagecount = d.contains(PdfName.ST) ? ((PdfNumber)d.get(PdfName.ST)).intValue() : 1;
            String prefix = d.contains(PdfName.P) ? ((PdfString)d.get(PdfName.P)).toUnicodeString() : "";
            if (d.contains(PdfName.S)) {
                char type = d.get(PdfName.S).toString().charAt(1);
                switch (type) {
                    case 'R': {
                        numberStyle = 1;
                        break;
                    }
                    case 'r': {
                        numberStyle = 2;
                        break;
                    }
                    case 'A': {
                        numberStyle = 3;
                        break;
                    }
                    case 'a': {
                        numberStyle = 4;
                        break;
                    }
                    default: {
                        numberStyle = 0;
                        break;
                    }
                }
            } else {
                numberStyle = 5;
            }
            formats[k] = new PdfPageLabelFormat((Integer)key + 1, numberStyle, prefix, pagecount);
        }
        return formats;
    }

    public static class PdfPageLabelFormat {
        public int physicalPage;
        public int numberStyle;
        public String prefix;
        public int logicalPage;

        public PdfPageLabelFormat(int physicalPage, int numberStyle, String prefix, int logicalPage) {
            this.physicalPage = physicalPage;
            this.numberStyle = numberStyle;
            this.prefix = prefix;
            this.logicalPage = logicalPage;
        }
    }
}

