/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.tika.language.ProfilingWriter;

@Deprecated
public class LanguageProfile {
    public static final int DEFAULT_NGRAM_LENGTH = 3;
    private final int length;
    private final Map<String, Counter> ngrams = new HashMap<String, Counter>();
    private Interleaved interleaved = new Interleaved();
    public static boolean useInterleaved = true;
    private long count = 0L;

    public LanguageProfile(int length) {
        this.length = length;
    }

    public LanguageProfile() {
        this(3);
    }

    public LanguageProfile(String content, int length) {
        this(length);
        ProfilingWriter writer = new ProfilingWriter(this);
        char[] ch = content.toCharArray();
        writer.write(ch, 0, ch.length);
    }

    public LanguageProfile(String content) {
        this(content, 3);
    }

    public long getCount() {
        return this.count;
    }

    public long getCount(String ngram) {
        Counter counter = this.ngrams.get(ngram);
        if (counter != null) {
            return counter.count;
        }
        return 0L;
    }

    public void add(String ngram) {
        this.add(ngram, 1L);
    }

    public void add(String ngram, long count) {
        if (this.length != ngram.length()) {
            throw new IllegalArgumentException("Unable to add an ngram of incorrect length: " + ngram.length() + " != " + this.length);
        }
        Counter counter = this.ngrams.get(ngram);
        if (counter == null) {
            counter = new Counter();
            this.ngrams.put(ngram, counter);
        }
        Counter counter2 = counter;
        counter2.count = counter2.count + count;
        this.count += count;
    }

    public double distance(LanguageProfile that) {
        return useInterleaved ? this.distanceInterleaved(that) : this.distanceStandard(that);
    }

    private double distanceStandard(LanguageProfile that) {
        if (this.length != that.length) {
            throw new IllegalArgumentException("Unable to calculage distance of language profiles with different ngram lengths: " + that.length + " != " + this.length);
        }
        double sumOfSquares = 0.0;
        double thisCount = Math.max((double)this.count, 1.0);
        double thatCount = Math.max((double)that.count, 1.0);
        HashSet<String> ngrams = new HashSet<String>();
        ngrams.addAll(this.ngrams.keySet());
        ngrams.addAll(that.ngrams.keySet());
        for (String ngram : ngrams) {
            double thisFrequency = (double)this.getCount(ngram) / thisCount;
            double thatFrequency = (double)that.getCount(ngram) / thatCount;
            double difference = thisFrequency - thatFrequency;
            sumOfSquares += difference * difference;
        }
        return Math.sqrt(sumOfSquares);
    }

    public String toString() {
        return this.ngrams.toString();
    }

    private double distanceInterleaved(LanguageProfile that) {
        if (this.length != that.length) {
            throw new IllegalArgumentException("Unable to calculage distance of language profiles with different ngram lengths: " + that.length + " != " + this.length);
        }
        double sumOfSquares = 0.0;
        double thisCount = Math.max((double)this.count, 1.0);
        double thatCount = Math.max((double)that.count, 1.0);
        Interleaved.Entry thisEntry = this.updateInterleaved().firstEntry();
        Interleaved.Entry thatEntry = that.updateInterleaved().firstEntry();
        while (thisEntry.hasNgram() || thatEntry.hasNgram()) {
            if (!thisEntry.hasNgram()) {
                sumOfSquares += this.square((double)thatEntry.count / thatCount);
                thatEntry.next();
                continue;
            }
            if (!thatEntry.hasNgram()) {
                sumOfSquares += this.square((double)thisEntry.count / thisCount);
                thisEntry.next();
                continue;
            }
            int compare = thisEntry.compareTo(thatEntry);
            if (compare == 0) {
                double difference = (double)thisEntry.count / thisCount - (double)thatEntry.count / thatCount;
                sumOfSquares += this.square(difference);
                thisEntry.next();
                thatEntry.next();
                continue;
            }
            if (compare < 0) {
                sumOfSquares += this.square((double)thisEntry.count / thisCount);
                thisEntry.next();
                continue;
            }
            sumOfSquares += this.square((double)thatEntry.count / thatCount);
            thatEntry.next();
        }
        return Math.sqrt(sumOfSquares);
    }

    private double square(double count) {
        return count * count;
    }

    private Interleaved updateInterleaved() {
        this.interleaved.update();
        return this.interleaved;
    }

    private class Interleaved {
        private char[] entries = null;
        private int size = 0;
        private long entriesGeneratedAtCount = -1L;

        private Interleaved() {
        }

        public void update() {
            if (LanguageProfile.this.count == this.entriesGeneratedAtCount) {
                return;
            }
            this.size = LanguageProfile.this.ngrams.size();
            int numChars = (LanguageProfile.this.length + 2) * this.size;
            if (this.entries == null || this.entries.length < numChars) {
                this.entries = new char[numChars];
            }
            int pos = 0;
            for (Map.Entry<String, Counter> entry : this.getSortedNgrams()) {
                for (int l = 0; l < LanguageProfile.this.length; ++l) {
                    this.entries[pos + l] = entry.getKey().charAt(l);
                }
                this.entries[pos + ((LanguageProfile)LanguageProfile.this).length] = (char)(entry.getValue().count / 65536L);
                this.entries[pos + ((LanguageProfile)LanguageProfile.this).length + 1] = (char)(entry.getValue().count % 65536L);
                pos += LanguageProfile.this.length + 2;
            }
            this.entriesGeneratedAtCount = LanguageProfile.this.count;
        }

        public Entry firstEntry() {
            Entry entry = new Entry();
            if (this.size > 0) {
                entry.update(0);
            }
            return entry;
        }

        private List<Map.Entry<String, Counter>> getSortedNgrams() {
            ArrayList<Map.Entry<String, Counter>> entries = new ArrayList<Map.Entry<String, Counter>>(LanguageProfile.this.ngrams.size());
            entries.addAll(LanguageProfile.this.ngrams.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<String, Counter>>(){

                @Override
                public int compare(Map.Entry<String, Counter> o1, Map.Entry<String, Counter> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            return entries;
        }

        private class Entry
        implements Comparable<Entry> {
            char[] ngram;
            int count;
            int pos;

            private Entry() {
                this.ngram = new char[LanguageProfile.this.length];
                this.count = 0;
                this.pos = 0;
            }

            private void update(int pos) {
                this.pos = pos;
                if (pos >= Interleaved.this.size) {
                    return;
                }
                int origo = pos * (LanguageProfile.this.length + 2);
                System.arraycopy(Interleaved.this.entries, origo, this.ngram, 0, LanguageProfile.this.length);
                this.count = Interleaved.this.entries[origo + LanguageProfile.this.length] * 65536 + Interleaved.this.entries[origo + LanguageProfile.this.length + 1];
            }

            @Override
            public int compareTo(Entry other) {
                for (int i = 0; i < this.ngram.length; ++i) {
                    if (this.ngram[i] == other.ngram[i]) continue;
                    return this.ngram[i] - other.ngram[i];
                }
                return 0;
            }

            public boolean hasNext() {
                return this.pos < Interleaved.this.size - 1;
            }

            public boolean hasNgram() {
                return this.pos < Interleaved.this.size;
            }

            public void next() {
                this.update(this.pos + 1);
            }

            public String toString() {
                return new String(this.ngram) + "(" + this.count + ")";
            }
        }
    }

    private static class Counter {
        private long count = 0L;

        private Counter() {
        }

        public String toString() {
            return Long.toString(this.count);
        }
    }
}

