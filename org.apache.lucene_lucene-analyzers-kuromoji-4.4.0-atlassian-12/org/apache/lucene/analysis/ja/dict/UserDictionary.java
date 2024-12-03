/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.IntsRef
 *  org.apache.lucene.util.fst.Builder
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 *  org.apache.lucene.util.fst.FST$INPUT_TYPE
 *  org.apache.lucene.util.fst.Outputs
 *  org.apache.lucene.util.fst.PositiveIntOutputs
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.analysis.ja.dict.Dictionary;
import org.apache.lucene.analysis.ja.dict.TokenInfoFST;
import org.apache.lucene.analysis.ja.util.CSVUtil;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.PositiveIntOutputs;

public final class UserDictionary
implements Dictionary {
    private final TokenInfoFST fst;
    private final int[][] segmentations;
    private final String[] data;
    private static final int CUSTOM_DICTIONARY_WORD_ID_OFFSET = 100000000;
    public static final int WORD_COST = -100000;
    public static final int LEFT_ID = 5;
    public static final int RIGHT_ID = 5;
    private static final int[][] EMPTY_RESULT = new int[0][];

    public UserDictionary(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        int wordId = 100000000;
        ArrayList<String[]> featureEntries = new ArrayList<String[]>();
        while ((line = br.readLine()) != null) {
            if ((line = line.replaceAll("#.*$", "")).trim().length() == 0) continue;
            String[] values = CSVUtil.parse(line);
            featureEntries.add(values);
        }
        Collections.sort(featureEntries, new Comparator<String[]>(){

            @Override
            public int compare(String[] left, String[] right) {
                return left[0].compareTo(right[0]);
            }
        });
        ArrayList<String> data = new ArrayList<String>(featureEntries.size());
        ArrayList<int[]> segmentations = new ArrayList<int[]>(featureEntries.size());
        PositiveIntOutputs fstOutput = PositiveIntOutputs.getSingleton();
        Builder fstBuilder = new Builder(FST.INPUT_TYPE.BYTE2, (Outputs)fstOutput);
        IntsRef scratch = new IntsRef();
        long ord = 0L;
        for (String[] values : featureEntries) {
            String[] segmentation = values[1].replaceAll("  *", " ").split(" ");
            String[] readings = values[2].replaceAll("  *", " ").split(" ");
            String pos = values[3];
            if (segmentation.length != readings.length) {
                throw new RuntimeException("Illegal user dictionary entry " + values[0] + " - the number of segmentations (" + segmentation.length + ") does not the match number of readings (" + readings.length + ")");
            }
            int[] wordIdAndLength = new int[segmentation.length + 1];
            wordIdAndLength[0] = wordId;
            for (int i = 0; i < segmentation.length; ++i) {
                wordIdAndLength[i + 1] = segmentation[i].length();
                data.add(readings[i] + "\u0000" + pos);
                ++wordId;
            }
            String token = values[0];
            scratch.grow(token.length());
            scratch.length = token.length();
            for (int i = 0; i < token.length(); ++i) {
                scratch.ints[i] = token.charAt(i);
            }
            fstBuilder.add(scratch, (Object)ord);
            segmentations.add(wordIdAndLength);
            ++ord;
        }
        this.fst = new TokenInfoFST((FST<Long>)fstBuilder.finish(), false);
        this.data = data.toArray(new String[data.size()]);
        this.segmentations = (int[][])segmentations.toArray((T[])new int[segmentations.size()][]);
    }

    public int[][] lookup(char[] chars, int off, int len) throws IOException {
        TreeMap<Integer, int[]> result = new TreeMap<Integer, int[]>();
        boolean found = false;
        FST.BytesReader fstReader = this.fst.getBytesReader();
        FST.Arc<Long> arc = new FST.Arc<Long>();
        int end = off + len;
        for (int startOffset = off; startOffset < end; ++startOffset) {
            char ch;
            arc = this.fst.getFirstArc(arc);
            int output = 0;
            int remaining = end - startOffset;
            for (int i = 0; i < remaining && this.fst.findTargetArc(ch = chars[startOffset + i], arc, arc, i == 0, fstReader) != null; ++i) {
                output += ((Long)arc.output).intValue();
                if (!arc.isFinal()) continue;
                int finalOutput = output + ((Long)arc.nextFinalOutput).intValue();
                result.put(startOffset - off, this.segmentations[finalOutput]);
                found = true;
            }
        }
        return found ? this.toIndexArray(result) : EMPTY_RESULT;
    }

    public TokenInfoFST getFST() {
        return this.fst;
    }

    private int[][] toIndexArray(Map<Integer, int[]> input) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        for (int i : input.keySet()) {
            int[] wordIdAndLength = input.get(i);
            int wordId = wordIdAndLength[0];
            int current = i;
            for (int j = 1; j < wordIdAndLength.length; ++j) {
                int[] token = new int[]{wordId + j - 1, current, wordIdAndLength[j]};
                result.add(token);
                current += wordIdAndLength[j];
            }
        }
        return (int[][])result.toArray((T[])new int[result.size()][]);
    }

    public int[] lookupSegmentation(int phraseID) {
        return this.segmentations[phraseID];
    }

    @Override
    public int getLeftId(int wordId) {
        return 5;
    }

    @Override
    public int getRightId(int wordId) {
        return 5;
    }

    @Override
    public int getWordCost(int wordId) {
        return -100000;
    }

    @Override
    public String getReading(int wordId, char[] surface, int off, int len) {
        return this.getFeature(wordId, 0);
    }

    @Override
    public String getPartOfSpeech(int wordId) {
        return this.getFeature(wordId, 1);
    }

    @Override
    public String getBaseForm(int wordId, char[] surface, int off, int len) {
        return null;
    }

    @Override
    public String getPronunciation(int wordId, char[] surface, int off, int len) {
        return null;
    }

    @Override
    public String getInflectionType(int wordId) {
        return null;
    }

    @Override
    public String getInflectionForm(int wordId) {
        return null;
    }

    private String[] getAllFeaturesArray(int wordId) {
        String allFeatures = this.data[wordId - 100000000];
        if (allFeatures == null) {
            return null;
        }
        return allFeatures.split("\u0000");
    }

    private String getFeature(int wordId, int ... fields) {
        String[] allFeatures = this.getAllFeaturesArray(wordId);
        if (allFeatures == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (fields.length == 0) {
            for (String feature : allFeatures) {
                sb.append(CSVUtil.quoteEscape(feature)).append(",");
            }
        } else if (fields.length == 1) {
            sb.append(allFeatures[fields[0]]).append(",");
        } else {
            for (int field : fields) {
                sb.append(CSVUtil.quoteEscape(allFeatures[field])).append(",");
            }
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}

