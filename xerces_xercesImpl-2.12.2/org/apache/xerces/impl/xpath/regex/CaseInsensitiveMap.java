/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

final class CaseInsensitiveMap {
    private static int CHUNK_SHIFT = 10;
    private static int CHUNK_SIZE = 1 << CHUNK_SHIFT;
    private static int CHUNK_MASK = CHUNK_SIZE - 1;
    private static int INITIAL_CHUNK_COUNT = 64;
    private static int[][][] caseInsensitiveMap;
    private static int LOWER_CASE_MATCH;
    private static int UPPER_CASE_MATCH;

    CaseInsensitiveMap() {
    }

    public static int[] get(int n) {
        return n < 65536 ? CaseInsensitiveMap.getMapping(n) : null;
    }

    private static int[] getMapping(int n) {
        int n2 = n >>> CHUNK_SHIFT;
        int n3 = n & CHUNK_MASK;
        return caseInsensitiveMap[n2][n3];
    }

    private static void buildCaseInsensitiveMap() {
        caseInsensitiveMap = new int[INITIAL_CHUNK_COUNT][CHUNK_SIZE][];
        for (int i = 0; i < 65536; ++i) {
            int[] nArray;
            char c;
            int n = Character.toLowerCase((char)i);
            if (n == (c = Character.toUpperCase((char)i)) && n == i) continue;
            int[] nArray2 = new int[2];
            int n2 = 0;
            if (n != i) {
                nArray2[n2++] = n;
                nArray2[n2++] = LOWER_CASE_MATCH;
                nArray = CaseInsensitiveMap.getMapping(n);
                if (nArray != null) {
                    nArray2 = CaseInsensitiveMap.updateMap(i, nArray2, n, nArray, LOWER_CASE_MATCH);
                }
            }
            if (c != i) {
                if (n2 == nArray2.length) {
                    nArray2 = CaseInsensitiveMap.expandMap(nArray2, 2);
                }
                nArray2[n2++] = c;
                nArray2[n2++] = UPPER_CASE_MATCH;
                nArray = CaseInsensitiveMap.getMapping(c);
                if (nArray != null) {
                    nArray2 = CaseInsensitiveMap.updateMap(i, nArray2, c, nArray, UPPER_CASE_MATCH);
                }
            }
            CaseInsensitiveMap.set(i, nArray2);
        }
    }

    private static int[] expandMap(int[] nArray, int n) {
        int n2 = nArray.length;
        int[] nArray2 = new int[n2 + n];
        System.arraycopy(nArray, 0, nArray2, 0, n2);
        return nArray2;
    }

    private static void set(int n, int[] nArray) {
        int n2 = n >>> CHUNK_SHIFT;
        int n3 = n & CHUNK_MASK;
        CaseInsensitiveMap.caseInsensitiveMap[n2][n3] = nArray;
    }

    private static int[] updateMap(int n, int[] nArray, int n2, int[] nArray2, int n3) {
        for (int i = 0; i < nArray2.length; i += 2) {
            int n4 = nArray2[i];
            int[] nArray3 = CaseInsensitiveMap.getMapping(n4);
            if (nArray3 == null || !CaseInsensitiveMap.contains(nArray3, n2, n3)) continue;
            if (!CaseInsensitiveMap.contains(nArray3, n)) {
                nArray3 = CaseInsensitiveMap.expandAndAdd(nArray3, n, n3);
                CaseInsensitiveMap.set(n4, nArray3);
            }
            if (CaseInsensitiveMap.contains(nArray, n4)) continue;
            nArray = CaseInsensitiveMap.expandAndAdd(nArray, n4, n3);
        }
        if (!CaseInsensitiveMap.contains(nArray2, n)) {
            nArray2 = CaseInsensitiveMap.expandAndAdd(nArray2, n, n3);
            CaseInsensitiveMap.set(n2, nArray2);
        }
        return nArray;
    }

    private static boolean contains(int[] nArray, int n) {
        for (int i = 0; i < nArray.length; i += 2) {
            if (nArray[i] != n) continue;
            return true;
        }
        return false;
    }

    private static boolean contains(int[] nArray, int n, int n2) {
        for (int i = 0; i < nArray.length; i += 2) {
            if (nArray[i] != n || nArray[i + 1] != n2) continue;
            return true;
        }
        return false;
    }

    private static int[] expandAndAdd(int[] nArray, int n, int n2) {
        int n3 = nArray.length;
        int[] nArray2 = new int[n3 + 2];
        System.arraycopy(nArray, 0, nArray2, 0, n3);
        nArray2[n3] = n;
        nArray2[n3 + 1] = n2;
        return nArray2;
    }

    static {
        LOWER_CASE_MATCH = 1;
        UPPER_CASE_MATCH = 2;
        CaseInsensitiveMap.buildCaseInsensitiveMap();
    }
}

