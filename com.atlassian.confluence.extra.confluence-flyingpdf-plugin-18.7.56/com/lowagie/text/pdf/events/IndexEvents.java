/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndexEvents
extends PdfPageEventHelper {
    private Map<String, Integer> indextag = new TreeMap<String, Integer>();
    private long indexcounter = 0L;
    private List<Entry> indexentry = new ArrayList<Entry>();
    private Comparator<Entry> comparator = (en1, en2) -> {
        int rt = 0;
        if (en1.getIn1() != null && en2.getIn1() != null && (rt = en1.getIn1().compareToIgnoreCase(en2.getIn1())) == 0 && en1.getIn2() != null && en2.getIn2() != null && (rt = en1.getIn2().compareToIgnoreCase(en2.getIn2())) == 0 && en1.getIn3() != null && en2.getIn3() != null) {
            rt = en1.getIn3().compareToIgnoreCase(en2.getIn3());
        }
        return rt;
    };

    @Override
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
        this.indextag.put(text, writer.getPageNumber());
    }

    public Chunk create(String text, String in1, String in2, String in3) {
        Chunk chunk = new Chunk(text);
        String tag = "idx_" + this.indexcounter++;
        chunk.setGenericTag(tag);
        chunk.setLocalDestination(tag);
        Entry entry = new Entry(in1, in2, in3, tag);
        this.indexentry.add(entry);
        return chunk;
    }

    public Chunk create(String text, String in1) {
        return this.create(text, in1, "", "");
    }

    public Chunk create(String text, String in1, String in2) {
        return this.create(text, in1, in2, "");
    }

    public void create(Chunk text, String in1, String in2, String in3) {
        String tag = "idx_" + this.indexcounter++;
        text.setGenericTag(tag);
        text.setLocalDestination(tag);
        Entry entry = new Entry(in1, in2, in3, tag);
        this.indexentry.add(entry);
    }

    public void create(Chunk text, String in1) {
        this.create(text, in1, "", "");
    }

    public void create(Chunk text, String in1, String in2) {
        this.create(text, in1, in2, "");
    }

    public void setComparator(Comparator<Entry> aComparator) {
        this.comparator = aComparator;
    }

    public List<Entry> getSortedEntries() {
        HashMap<String, Entry> grouped = new HashMap<String, Entry>();
        for (Entry e : this.indexentry) {
            String key = e.getKey();
            Entry master = (Entry)grouped.get(key);
            if (master != null) {
                master.addPageNumberAndTag(e.getPageNumber(), e.getTag());
                continue;
            }
            e.addPageNumberAndTag(e.getPageNumber(), e.getTag());
            grouped.put(key, e);
        }
        ArrayList<Entry> sorted = new ArrayList<Entry>(grouped.values());
        sorted.sort(this.comparator);
        return sorted;
    }

    public class Entry {
        private String in1;
        private String in2;
        private String in3;
        private String tag;
        private List<Integer> pagenumbers = new ArrayList<Integer>();
        private List<String> tags = new ArrayList<String>();

        public Entry(String aIn1, String aIn2, String aIn3, String aTag) {
            this.in1 = aIn1;
            this.in2 = aIn2;
            this.in3 = aIn3;
            this.tag = aTag;
        }

        public String getIn1() {
            return this.in1;
        }

        public String getIn2() {
            return this.in2;
        }

        public String getIn3() {
            return this.in3;
        }

        public String getTag() {
            return this.tag;
        }

        public int getPageNumber() {
            int rt = -1;
            Integer i = (Integer)IndexEvents.this.indextag.get(this.tag);
            if (i != null) {
                rt = i;
            }
            return rt;
        }

        public void addPageNumberAndTag(int number, String tag) {
            this.pagenumbers.add(number);
            this.tags.add(tag);
        }

        public String getKey() {
            return this.in1 + "!" + this.in2 + "!" + this.in3;
        }

        public List getPagenumbers() {
            return this.pagenumbers;
        }

        public List getTags() {
            return this.tags;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(this.in1).append(' ');
            buf.append(this.in2).append(' ');
            buf.append(this.in3).append(' ');
            for (Integer pagenumber : this.pagenumbers) {
                buf.append(pagenumber).append(' ');
            }
            return buf.toString();
        }
    }
}

