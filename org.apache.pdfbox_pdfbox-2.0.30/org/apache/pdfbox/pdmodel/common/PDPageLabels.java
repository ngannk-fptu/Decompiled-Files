/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

public class PDPageLabels
implements COSObjectable {
    private Map<Integer, PDPageLabelRange> labels = new TreeMap<Integer, PDPageLabelRange>();
    private PDDocument doc;

    public PDPageLabels(PDDocument document) {
        this.doc = document;
        PDPageLabelRange defaultRange = new PDPageLabelRange();
        defaultRange.setStyle("D");
        this.labels.put(0, defaultRange);
    }

    public PDPageLabels(PDDocument document, COSDictionary dict) throws IOException {
        this(document);
        if (dict == null) {
            return;
        }
        PDNumberTreeNode root = new PDNumberTreeNode(dict, PDPageLabelRange.class);
        this.findLabels(root);
    }

    private void findLabels(PDNumberTreeNode node) throws IOException {
        block3: {
            block2: {
                List<PDNumberTreeNode> kids = node.getKids();
                if (node.getKids() == null) break block2;
                for (PDNumberTreeNode kid : kids) {
                    this.findLabels(kid);
                }
                break block3;
            }
            Map<Integer, COSObjectable> numbers = node.getNumbers();
            if (numbers == null) break block3;
            for (Map.Entry<Integer, COSObjectable> i : numbers.entrySet()) {
                if (i.getKey() < 0) continue;
                this.labels.put(i.getKey(), (PDPageLabelRange)i.getValue());
            }
        }
    }

    public int getPageRangeCount() {
        return this.labels.size();
    }

    public PDPageLabelRange getPageLabelRange(int startPage) {
        return this.labels.get(startPage);
    }

    public void setLabelItem(int startPage, PDPageLabelRange item) {
        if (startPage < 0) {
            throw new IllegalArgumentException("startPage parameter of setLabelItem may not be < 0");
        }
        this.labels.put(startPage, item);
    }

    @Override
    public COSBase getCOSObject() {
        COSDictionary dict = new COSDictionary();
        COSArray arr = new COSArray();
        for (Map.Entry<Integer, PDPageLabelRange> i : this.labels.entrySet()) {
            arr.add(COSInteger.get(i.getKey().intValue()));
            arr.add(i.getValue());
        }
        dict.setItem(COSName.NUMS, (COSBase)arr);
        return dict;
    }

    public Map<String, Integer> getPageIndicesByLabels() {
        int numberOfPages = this.doc.getNumberOfPages();
        final HashMap<String, Integer> labelMap = new HashMap<String, Integer>(numberOfPages);
        this.computeLabels(new LabelHandler(){

            @Override
            public void newLabel(int pageIndex, String label) {
                labelMap.put(label, pageIndex);
            }
        }, numberOfPages);
        return labelMap;
    }

    public String[] getLabelsByPageIndices() {
        final int numberOfPages = this.doc.getNumberOfPages();
        final String[] map = new String[numberOfPages];
        this.computeLabels(new LabelHandler(){

            @Override
            public void newLabel(int pageIndex, String label) {
                if (pageIndex < numberOfPages) {
                    map[pageIndex] = label;
                }
            }
        }, numberOfPages);
        return map;
    }

    public NavigableSet<Integer> getPageIndices() {
        return new TreeSet<Integer>(this.labels.keySet());
    }

    private void computeLabels(LabelHandler handler, int numberOfPages) {
        Iterator<Map.Entry<Integer, PDPageLabelRange>> iterator = this.labels.entrySet().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        int pageIndex = 0;
        Map.Entry<Integer, PDPageLabelRange> lastEntry = iterator.next();
        while (iterator.hasNext()) {
            Map.Entry<Integer, PDPageLabelRange> entry = iterator.next();
            int numPages = entry.getKey() - lastEntry.getKey();
            LabelGenerator gen = new LabelGenerator(lastEntry.getValue(), numPages);
            while (gen.hasNext()) {
                handler.newLabel(pageIndex, gen.next());
                ++pageIndex;
            }
            lastEntry = entry;
        }
        LabelGenerator gen = new LabelGenerator(lastEntry.getValue(), numberOfPages - lastEntry.getKey());
        while (gen.hasNext()) {
            handler.newLabel(pageIndex, gen.next());
            ++pageIndex;
        }
    }

    private static class LabelGenerator
    implements Iterator<String> {
        private final PDPageLabelRange labelInfo;
        private final int numPages;
        private int currentPage;
        private static final String[][] ROMANS = new String[][]{{"", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix"}, {"", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc"}, {"", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm"}};

        LabelGenerator(PDPageLabelRange label, int pages) {
            this.labelInfo = label;
            this.numPages = pages;
            this.currentPage = 0;
        }

        @Override
        public boolean hasNext() {
            return this.currentPage < this.numPages;
        }

        @Override
        public String next() {
            String style;
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            StringBuilder buf = new StringBuilder();
            String label = this.labelInfo.getPrefix();
            if (label != null) {
                int index = label.indexOf(0);
                if (index > -1) {
                    label = label.substring(0, index);
                }
                buf.append(label);
            }
            if ((style = this.labelInfo.getStyle()) != null) {
                buf.append(this.getNumber(this.labelInfo.getStart() + this.currentPage, style));
            }
            ++this.currentPage;
            return buf.toString();
        }

        private String getNumber(int pageIndex, String style) {
            if ("D".equals(style)) {
                return Integer.toString(pageIndex);
            }
            if ("a".equals(style)) {
                return LabelGenerator.makeLetterLabel(pageIndex);
            }
            if ("A".equals(style)) {
                return LabelGenerator.makeLetterLabel(pageIndex).toUpperCase();
            }
            if ("r".equals(style)) {
                return LabelGenerator.makeRomanLabel(pageIndex);
            }
            if ("R".equals(style)) {
                return LabelGenerator.makeRomanLabel(pageIndex).toUpperCase();
            }
            return Integer.toString(pageIndex);
        }

        private static String makeRomanLabel(int pageIndex) {
            StringBuilder buf = new StringBuilder();
            for (int power = 0; power < 3 && pageIndex > 0; pageIndex /= 10, ++power) {
                buf.insert(0, ROMANS[power][pageIndex % 10]);
            }
            for (int i = 0; i < pageIndex; ++i) {
                buf.insert(0, 'm');
            }
            return buf.toString();
        }

        private static String makeLetterLabel(int num) {
            StringBuilder buf = new StringBuilder();
            int numLetters = num / 26 + Integer.signum(num % 26);
            int letter = num % 26 + 26 * (1 - Integer.signum(num % 26)) + 97 - 1;
            for (int i = 0; i < numLetters; ++i) {
                buf.appendCodePoint(letter);
            }
            return buf.toString();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static interface LabelHandler {
        public void newLabel(int var1, String var2);
    }
}

