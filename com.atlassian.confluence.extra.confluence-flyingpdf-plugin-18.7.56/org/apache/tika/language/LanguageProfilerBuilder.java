/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.tika.exception.TikaException;

@Deprecated
public class LanguageProfilerBuilder {
    static final int ABSOLUTE_MIN_NGRAM_LENGTH = 3;
    static final int ABSOLUTE_MAX_NGRAM_LENGTH = 3;
    static final int DEFAULT_MIN_NGRAM_LENGTH = 3;
    static final int DEFAULT_MAX_NGRAM_LENGTH = 3;
    static final String FILE_EXTENSION = "ngp";
    static final int MAX_SIZE = 1000;
    static final char SEPARATOR = '_';
    private static final String SEP_CHARSEQ = new String(new char[]{'_'});
    private String name = null;
    private List<NGramEntry> sorted = null;
    private int minLength = 3;
    private int maxLength = 3;
    private int[] ngramcounts = null;
    private Map<CharSequence, NGramEntry> ngrams = null;
    private QuickStringBuffer word = new QuickStringBuffer();

    public LanguageProfilerBuilder(String name, int minlen, int maxlen) {
        this.ngrams = new HashMap<CharSequence, NGramEntry>(4000);
        this.minLength = minlen;
        this.maxLength = maxlen;
        this.name = name;
    }

    public LanguageProfilerBuilder(String name) {
        this.ngrams = new HashMap<CharSequence, NGramEntry>(4000);
        this.minLength = 3;
        this.maxLength = 3;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void add(StringBuffer word) {
        for (int i = this.minLength; i <= this.maxLength && i < word.length(); ++i) {
            this.add(word, i);
        }
    }

    private void add(QuickStringBuffer word) {
        int wlen = word.length();
        if (wlen >= this.minLength) {
            int max = Math.min(this.maxLength, wlen);
            for (int i = this.minLength; i <= max; ++i) {
                this.add(word.subSequence(wlen - i, wlen));
            }
        }
    }

    private void add(CharSequence cs) {
        if (cs.equals(SEP_CHARSEQ)) {
            return;
        }
        NGramEntry nge = this.ngrams.get(cs);
        if (nge == null) {
            nge = new NGramEntry(cs);
            this.ngrams.put(cs, nge);
        }
        nge.inc();
    }

    public void analyze(StringBuilder text) {
        if (this.ngrams != null) {
            this.ngrams.clear();
            this.sorted = null;
            this.ngramcounts = null;
        }
        this.word.clear().append('_');
        for (int i = 0; i < text.length(); ++i) {
            char c = Character.toLowerCase(text.charAt(i));
            if (Character.isLetter(c)) {
                this.add(this.word.append(c));
                continue;
            }
            if (this.word.length() <= 1) continue;
            this.add(this.word.append('_'));
            this.word.clear().append('_');
        }
        if (this.word.length() > 1) {
            this.add(this.word.append('_'));
        }
        this.normalize();
    }

    private void add(StringBuffer word, int n) {
        for (int i = 0; i <= word.length() - n; ++i) {
            this.add(word.subSequence(i, i + n));
        }
    }

    protected void normalize() {
        NGramEntry e2 = null;
        Iterator<NGramEntry> i = this.ngrams.values().iterator();
        if (this.ngramcounts == null) {
            this.ngramcounts = new int[this.maxLength + 1];
            while (i.hasNext()) {
                e2 = i.next();
                int n = e2.size();
                this.ngramcounts[n] = this.ngramcounts[n] + e2.count;
            }
        }
        for (NGramEntry e2 : this.ngrams.values()) {
            e2.frequency = (float)e2.count / (float)this.ngramcounts[e2.size()];
        }
    }

    public List<NGramEntry> getSorted() {
        if (this.sorted == null) {
            this.sorted = new ArrayList<NGramEntry>(this.ngrams.values());
            Collections.sort(this.sorted);
            if (this.sorted.size() > 1000) {
                this.sorted = this.sorted.subList(0, 1000);
            }
        }
        return this.sorted;
    }

    public String toString() {
        StringBuffer s = new StringBuffer().append("NGramProfile: ").append(this.name).append("\n");
        for (NGramEntry entry : this.getSorted()) {
            s.append("[").append(entry.seq).append("/").append(entry.count).append("/").append(entry.frequency).append("]\n");
        }
        return s.toString();
    }

    public float getSimilarity(LanguageProfilerBuilder another) throws TikaException {
        float sum = 0.0f;
        try {
            for (NGramEntry other : another.getSorted()) {
                if (this.ngrams.containsKey(other.seq)) {
                    sum += Math.abs(other.frequency - this.ngrams.get(other.seq).frequency) / 2.0f;
                    continue;
                }
                sum += other.frequency;
            }
            for (NGramEntry other : this.getSorted()) {
                if (another.ngrams.containsKey(other.seq)) {
                    sum += Math.abs(other.frequency - another.ngrams.get(other.seq).frequency) / 2.0f;
                    continue;
                }
                sum += other.frequency;
            }
        }
        catch (Exception e) {
            throw new TikaException("Could not calculate a score how well NGramProfiles match each other");
        }
        return sum;
    }

    public void load(InputStream is) throws IOException {
        this.ngrams.clear();
        this.ngramcounts = new int[this.maxLength + 1];
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line = null;
        while ((line = reader.readLine()) != null) {
            int spacepos;
            String ngramsequence;
            int len;
            if (line.charAt(0) == '#' || (len = (ngramsequence = line.substring(0, spacepos = line.indexOf(32)).trim()).length()) < this.minLength || len > this.maxLength) continue;
            int ngramcount = Integer.parseInt(line.substring(spacepos + 1));
            NGramEntry en = new NGramEntry(ngramsequence, ngramcount);
            this.ngrams.put(en.getSeq(), en);
            int n = len;
            this.ngramcounts[n] = this.ngramcounts[n] + ngramcount;
        }
        this.normalize();
    }

    public static LanguageProfilerBuilder create(String name, InputStream is, String encoding) throws TikaException {
        LanguageProfilerBuilder newProfile = new LanguageProfilerBuilder(name, 3, 3);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[4096];
        StringBuilder text = new StringBuilder();
        try {
            int len;
            while ((len = bis.read(buffer)) != -1) {
                text.append(new String(buffer, 0, len, encoding));
            }
        }
        catch (IOException e) {
            throw new TikaException("Could not create profile, " + e.getMessage());
        }
        newProfile.analyze(text);
        return newProfile;
    }

    public void save(OutputStream os) throws IOException {
        int i;
        os.write(("# NgramProfile generated at " + new Date() + " for Apache Tika Language Identification\n").getBytes(StandardCharsets.UTF_8));
        ArrayList list = new ArrayList();
        List sublist = new ArrayList<NGramEntry>();
        NGramEntry[] entries = this.ngrams.values().toArray(new NGramEntry[this.ngrams.size()]);
        for (i = this.minLength; i <= this.maxLength; ++i) {
            for (int j = 0; j < entries.length; ++j) {
                if (entries[j].getSeq().length() != i) continue;
                sublist.add(entries[j]);
            }
            Collections.sort(sublist);
            if (sublist.size() > 1000) {
                sublist = sublist.subList(0, 1000);
            }
            list.addAll(sublist);
            sublist.clear();
        }
        for (i = 0; i < list.size(); ++i) {
            NGramEntry e = (NGramEntry)list.get(i);
            String line = e.toString() + " " + e.getCount() + "\n";
            os.write(line.getBytes(StandardCharsets.UTF_8));
        }
        os.flush();
    }

    public static void main(String[] args) {
        String usage = "Usage: NGramProfile [-create profilename filename encoding] [-similarity file1 file2] [-score profile-name filename encoding]";
        int command = 0;
        boolean CREATE = true;
        int SIMILARITY = 2;
        int SCORE = 3;
        String profilename = "";
        String filename = "";
        String filename2 = "";
        String encoding = "";
        if (args.length == 0) {
            System.err.println(usage);
            System.exit(-1);
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-create")) {
                command = 1;
                profilename = args[++i];
                filename = args[++i];
                encoding = args[++i];
            }
            if (args[i].equals("-similarity")) {
                command = 2;
                filename = args[++i];
                filename2 = args[++i];
                encoding = args[++i];
            }
            if (!args[i].equals("-score")) continue;
            command = 3;
            profilename = args[++i];
            filename = args[++i];
            encoding = args[++i];
        }
        try {
            switch (command) {
                case 1: {
                    File f = new File(filename);
                    FileInputStream fis = new FileInputStream(f);
                    LanguageProfilerBuilder newProfile = LanguageProfilerBuilder.create(profilename, fis, encoding);
                    fis.close();
                    f = new File(profilename + "." + FILE_EXTENSION);
                    FileOutputStream fos = new FileOutputStream(f);
                    newProfile.save(fos);
                    System.out.println("new profile " + profilename + "." + FILE_EXTENSION + " was created.");
                    break;
                }
                case 2: {
                    File f = new File(filename);
                    FileInputStream fis = new FileInputStream(f);
                    LanguageProfilerBuilder newProfile = LanguageProfilerBuilder.create(filename, fis, encoding);
                    newProfile.normalize();
                    f = new File(filename2);
                    fis = new FileInputStream(f);
                    LanguageProfilerBuilder newProfile2 = LanguageProfilerBuilder.create(filename2, fis, encoding);
                    newProfile2.normalize();
                    System.out.println("Similarity is " + newProfile.getSimilarity(newProfile2));
                    break;
                }
                case 3: {
                    File f = new File(filename);
                    FileInputStream fis = new FileInputStream(f);
                    LanguageProfilerBuilder newProfile = LanguageProfilerBuilder.create(filename, fis, encoding);
                    f = new File(profilename + "." + FILE_EXTENSION);
                    fis = new FileInputStream(f);
                    LanguageProfilerBuilder compare = new LanguageProfilerBuilder(profilename, 3, 3);
                    compare.load(fis);
                    System.out.println("Score is " + compare.getSimilarity(newProfile));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class QuickStringBuffer
    implements CharSequence {
        private char[] value;
        private int count;

        QuickStringBuffer() {
            this(16);
        }

        QuickStringBuffer(char[] value) {
            this.value = value;
            this.count = value.length;
        }

        QuickStringBuffer(int length) {
            this.value = new char[length];
        }

        QuickStringBuffer(String str) {
            this(str.length() + 16);
            this.append(str);
        }

        @Override
        public int length() {
            return this.count;
        }

        private void expandCapacity(int minimumCapacity) {
            int newCapacity = (this.value.length + 1) * 2;
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            } else if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            char[] newValue = new char[newCapacity];
            System.arraycopy(this.value, 0, newValue, 0, this.count);
            this.value = newValue;
        }

        QuickStringBuffer clear() {
            this.count = 0;
            return this;
        }

        @Override
        public char charAt(int index) {
            return this.value[index];
        }

        QuickStringBuffer append(String str) {
            int len;
            int newcount;
            if (str == null) {
                str = String.valueOf(str);
            }
            if ((newcount = this.count + (len = str.length())) > this.value.length) {
                this.expandCapacity(newcount);
            }
            str.getChars(0, len, this.value, this.count);
            this.count = newcount;
            return this;
        }

        QuickStringBuffer append(char c) {
            int newcount = this.count + 1;
            if (newcount > this.value.length) {
                this.expandCapacity(newcount);
            }
            this.value[this.count++] = c;
            return this;
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new String(this.value, start, end - start);
        }

        @Override
        public String toString() {
            return new String(this.value);
        }
    }

    static class NGramEntry
    implements Comparable<NGramEntry> {
        private LanguageProfilerBuilder profile = null;
        CharSequence seq = null;
        private int count = 0;
        private float frequency = 0.0f;

        public NGramEntry(CharSequence seq) {
            this.seq = seq;
        }

        public NGramEntry(String seq, int count) {
            this.seq = new StringBuffer(seq).subSequence(0, seq.length());
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }

        public float getFrequency() {
            return this.frequency;
        }

        public CharSequence getSeq() {
            return this.seq;
        }

        public int size() {
            return this.seq.length();
        }

        @Override
        public int compareTo(NGramEntry ngram) {
            int diff = Float.compare(ngram.getFrequency(), this.frequency);
            if (diff != 0) {
                return diff;
            }
            return this.toString().compareTo(ngram.toString());
        }

        public void inc() {
            ++this.count;
        }

        public void setProfile(LanguageProfilerBuilder profile) {
            this.profile = profile;
        }

        public LanguageProfilerBuilder getProfile() {
            return this.profile;
        }

        public String toString() {
            return this.seq.toString();
        }

        public int hashCode() {
            return this.seq.hashCode();
        }

        public boolean equals(Object obj) {
            NGramEntry ngram = null;
            try {
                ngram = (NGramEntry)obj;
                return ngram.seq.equals(this.seq);
            }
            catch (Exception e) {
                return false;
            }
        }
    }
}

