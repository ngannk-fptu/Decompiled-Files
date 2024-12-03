/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.buf;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;

public class StringCache {
    private static final Log log = LogFactory.getLog(StringCache.class);
    protected static boolean byteEnabled = Boolean.getBoolean("tomcat.util.buf.StringCache.byte.enabled");
    protected static boolean charEnabled = Boolean.getBoolean("tomcat.util.buf.StringCache.char.enabled");
    protected static int trainThreshold = Integer.getInteger("tomcat.util.buf.StringCache.trainThreshold", 20000);
    protected static int cacheSize = Integer.getInteger("tomcat.util.buf.StringCache.cacheSize", 200);
    protected static final int maxStringSize = Integer.getInteger("tomcat.util.buf.StringCache.maxStringSize", 128);
    protected static final HashMap<ByteEntry, int[]> bcStats = new HashMap(cacheSize);
    protected static int bcCount = 0;
    protected static volatile ByteEntry[] bcCache = null;
    protected static final HashMap<CharEntry, int[]> ccStats = new HashMap(cacheSize);
    protected static int ccCount = 0;
    protected static volatile CharEntry[] ccCache = null;
    protected static int accessCount = 0;
    protected static int hitCount = 0;

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        StringCache.cacheSize = cacheSize;
    }

    public boolean getByteEnabled() {
        return byteEnabled;
    }

    public void setByteEnabled(boolean byteEnabled) {
        StringCache.byteEnabled = byteEnabled;
    }

    public boolean getCharEnabled() {
        return charEnabled;
    }

    public void setCharEnabled(boolean charEnabled) {
        StringCache.charEnabled = charEnabled;
    }

    public int getTrainThreshold() {
        return trainThreshold;
    }

    public void setTrainThreshold(int trainThreshold) {
        StringCache.trainThreshold = trainThreshold;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public int getHitCount() {
        return hitCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        hitCount = 0;
        accessCount = 0;
        HashMap<Object, int[]> hashMap = bcStats;
        synchronized (hashMap) {
            bcCache = null;
            bcCount = 0;
        }
        hashMap = ccStats;
        synchronized (hashMap) {
            ccCache = null;
            ccCount = 0;
        }
    }

    public static String toString(ByteChunk bc) {
        try {
            return StringCache.toString(bc, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
        }
        catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String toString(ByteChunk bc, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) throws CharacterCodingException {
        if (bcCache == null) {
            String value = bc.toStringInternal(malformedInputAction, unmappableCharacterAction);
            if (byteEnabled && value.length() < maxStringSize) {
                HashMap<ByteEntry, int[]> hashMap = bcStats;
                synchronized (hashMap) {
                    if (bcCache != null) {
                        return value;
                    }
                    if (bcCount > trainThreshold) {
                        long t1 = System.currentTimeMillis();
                        TreeMap<Integer, ArrayList> tempMap = new TreeMap<Integer, ArrayList>();
                        for (Map.Entry<ByteEntry, int[]> item : bcStats.entrySet()) {
                            ByteEntry entry = item.getKey();
                            int[] countA = item.getValue();
                            Integer count = countA[0];
                            tempMap.computeIfAbsent(count, k -> new ArrayList()).add(entry);
                        }
                        int size = bcStats.size();
                        if (size > cacheSize) {
                            size = cacheSize;
                        }
                        ByteEntry[] tempbcCache = new ByteEntry[size];
                        ByteChunk tempChunk = new ByteChunk();
                        int n = 0;
                        while (n < size) {
                            Object key = tempMap.lastKey();
                            ArrayList list = (ArrayList)tempMap.get(key);
                            for (int i = 0; i < list.size() && n < size; ++n, ++i) {
                                ByteEntry entry = (ByteEntry)list.get(i);
                                tempChunk.setBytes(entry.name, 0, entry.name.length);
                                int insertPos = StringCache.findClosest(tempChunk, tempbcCache, n);
                                if (insertPos == n) {
                                    tempbcCache[n + 1] = entry;
                                    continue;
                                }
                                System.arraycopy(tempbcCache, insertPos + 1, tempbcCache, insertPos + 2, n - insertPos - 1);
                                tempbcCache[insertPos + 1] = entry;
                            }
                            tempMap.remove(key);
                        }
                        bcCount = 0;
                        bcStats.clear();
                        bcCache = tempbcCache;
                        if (log.isDebugEnabled()) {
                            long t2 = System.currentTimeMillis();
                            log.debug((Object)("ByteCache generation time: " + (t2 - t1) + "ms"));
                        }
                    } else {
                        ++bcCount;
                        ByteEntry entry = new ByteEntry();
                        entry.value = value;
                        int[] count = bcStats.get(entry);
                        if (count == null) {
                            int end = bc.getEnd();
                            int start = bc.getStart();
                            ByteEntry.access$002(entry, new byte[bc.getLength()]);
                            System.arraycopy(bc.getBuffer(), start, entry.name, 0, end - start);
                            entry.charset = bc.getCharset();
                            entry.malformedInputAction = malformedInputAction;
                            entry.unmappableCharacterAction = unmappableCharacterAction;
                            count = new int[]{1};
                            bcStats.put(entry, count);
                        } else {
                            count[0] = count[0] + 1;
                        }
                    }
                }
            }
            return value;
        }
        ++accessCount;
        String result = StringCache.find(bc, malformedInputAction, unmappableCharacterAction);
        if (result == null) {
            return bc.toStringInternal(malformedInputAction, unmappableCharacterAction);
        }
        ++hitCount;
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String toString(CharChunk cc) {
        if (ccCache == null) {
            String value = cc.toStringInternal();
            if (charEnabled && value.length() < maxStringSize) {
                HashMap<CharEntry, int[]> hashMap = ccStats;
                synchronized (hashMap) {
                    if (ccCache != null) {
                        return value;
                    }
                    if (ccCount > trainThreshold) {
                        ArrayList list;
                        long t1 = System.currentTimeMillis();
                        TreeMap<Integer, ArrayList> tempMap = new TreeMap<Integer, ArrayList>();
                        for (Map.Entry<CharEntry, int[]> item : ccStats.entrySet()) {
                            CharEntry entry = item.getKey();
                            int[] countA = item.getValue();
                            Integer count = countA[0];
                            list = tempMap.computeIfAbsent(count, k -> new ArrayList());
                            list.add(entry);
                        }
                        int size = ccStats.size();
                        if (size > cacheSize) {
                            size = cacheSize;
                        }
                        CharEntry[] tempccCache = new CharEntry[size];
                        CharChunk tempChunk = new CharChunk();
                        int n = 0;
                        while (n < size) {
                            Object key = tempMap.lastKey();
                            list = (ArrayList)tempMap.get(key);
                            for (int i = 0; i < list.size() && n < size; ++n, ++i) {
                                CharEntry entry = (CharEntry)list.get(i);
                                tempChunk.setChars(entry.name, 0, entry.name.length);
                                int insertPos = StringCache.findClosest(tempChunk, tempccCache, n);
                                if (insertPos == n) {
                                    tempccCache[n + 1] = entry;
                                    continue;
                                }
                                System.arraycopy(tempccCache, insertPos + 1, tempccCache, insertPos + 2, n - insertPos - 1);
                                tempccCache[insertPos + 1] = entry;
                            }
                            tempMap.remove(key);
                        }
                        ccCount = 0;
                        ccStats.clear();
                        ccCache = tempccCache;
                        if (log.isDebugEnabled()) {
                            long t2 = System.currentTimeMillis();
                            log.debug((Object)("CharCache generation time: " + (t2 - t1) + "ms"));
                        }
                    } else {
                        ++ccCount;
                        CharEntry entry = new CharEntry();
                        entry.value = value;
                        int[] count = ccStats.get(entry);
                        if (count == null) {
                            int end = cc.getEnd();
                            int start = cc.getStart();
                            CharEntry.access$602(entry, new char[cc.getLength()]);
                            System.arraycopy(cc.getBuffer(), start, entry.name, 0, end - start);
                            count = new int[]{1};
                            ccStats.put(entry, count);
                        } else {
                            count[0] = count[0] + 1;
                        }
                    }
                }
            }
            return value;
        }
        ++accessCount;
        String result = StringCache.find(cc);
        if (result == null) {
            return cc.toStringInternal();
        }
        ++hitCount;
        return result;
    }

    protected static final int compare(ByteChunk name, byte[] compareTo) {
        int len;
        int result = 0;
        byte[] b = name.getBuffer();
        int start = name.getStart();
        int end = name.getEnd();
        if (end - start < (len = compareTo.length)) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; ++i) {
            if (b[i + start] > compareTo[i]) {
                result = 1;
                continue;
            }
            if (b[i + start] >= compareTo[i]) continue;
            result = -1;
        }
        if (result == 0) {
            if (compareTo.length > end - start) {
                result = -1;
            } else if (compareTo.length < end - start) {
                result = 1;
            }
        }
        return result;
    }

    @Deprecated
    protected static final String find(ByteChunk name) {
        return StringCache.find(name, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
    }

    protected static final String find(ByteChunk name, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
        int pos = StringCache.findClosest(name, bcCache, bcCache.length);
        if (!(pos >= 0 && StringCache.compare(name, bcCache[pos].name) == 0 && name.getCharset().equals(bcCache[pos].charset) && malformedInputAction.equals(bcCache[pos].malformedInputAction) && unmappableCharacterAction.equals(bcCache[pos].unmappableCharacterAction))) {
            return null;
        }
        return bcCache[pos].value;
    }

    protected static final int findClosest(ByteChunk name, ByteEntry[] array, int len) {
        int a = 0;
        int b = len - 1;
        if (b == -1) {
            return -1;
        }
        if (StringCache.compare(name, array[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        int i = 0;
        do {
            int result;
            if ((result = StringCache.compare(name, array[i = b + a >>> 1].name)) == 1) {
                a = i;
                continue;
            }
            if (result == 0) {
                return i;
            }
            b = i;
        } while (b - a != 1);
        int result2 = StringCache.compare(name, array[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    protected static final int compare(CharChunk name, char[] compareTo) {
        int len;
        int result = 0;
        char[] c = name.getBuffer();
        int start = name.getStart();
        int end = name.getEnd();
        if (end - start < (len = compareTo.length)) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; ++i) {
            if (c[i + start] > compareTo[i]) {
                result = 1;
                continue;
            }
            if (c[i + start] >= compareTo[i]) continue;
            result = -1;
        }
        if (result == 0) {
            if (compareTo.length > end - start) {
                result = -1;
            } else if (compareTo.length < end - start) {
                result = 1;
            }
        }
        return result;
    }

    protected static final String find(CharChunk name) {
        int pos = StringCache.findClosest(name, ccCache, ccCache.length);
        if (pos < 0 || StringCache.compare(name, ccCache[pos].name) != 0) {
            return null;
        }
        return ccCache[pos].value;
    }

    protected static final int findClosest(CharChunk name, CharEntry[] array, int len) {
        int a = 0;
        int b = len - 1;
        if (b == -1) {
            return -1;
        }
        if (StringCache.compare(name, array[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        int i = 0;
        do {
            int result;
            if ((result = StringCache.compare(name, array[i = b + a >>> 1].name)) == 1) {
                a = i;
                continue;
            }
            if (result == 0) {
                return i;
            }
            b = i;
        } while (b - a != 1);
        int result2 = StringCache.compare(name, array[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static class ByteEntry {
        private byte[] name = null;
        private Charset charset = null;
        private CodingErrorAction malformedInputAction = null;
        private CodingErrorAction unmappableCharacterAction = null;
        private String value = null;

        private ByteEntry() {
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            return Objects.hash(this.malformedInputAction, this.unmappableCharacterAction, this.value);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ByteEntry other = (ByteEntry)obj;
            return Objects.equals(this.malformedInputAction, other.malformedInputAction) && Objects.equals(this.unmappableCharacterAction, other.unmappableCharacterAction) && Objects.equals(this.value, other.value);
        }

        static /* synthetic */ byte[] access$002(ByteEntry x0, byte[] x1) {
            x0.name = x1;
            return x1;
        }
    }

    private static class CharEntry {
        private char[] name = null;
        private String value = null;

        private CharEntry() {
        }

        public String toString() {
            return this.value;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof CharEntry) {
                return this.value.equals(((CharEntry)obj).value);
            }
            return false;
        }

        static /* synthetic */ char[] access$602(CharEntry x0, char[] x1) {
            x0.name = x1;
            return x1;
        }
    }
}

