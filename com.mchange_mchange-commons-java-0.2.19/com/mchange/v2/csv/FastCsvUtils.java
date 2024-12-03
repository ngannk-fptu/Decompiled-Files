/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.csv;

import com.mchange.v2.csv.MalformedCsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FastCsvUtils {
    private static final int ESCAPE_BIT = 0x1000000;
    private static final int SHIFT_BIT = 0x2000000;
    private static final int SHIFT_OFFSET = 8;
    private static final int CR = 13;
    private static final int LF = 10;
    private static final int EOF = -1;
    private static final int CRLF_TOKEN = 999;
    private static final String CRLF = "\r\n";
    private static final int GUESSED_LINE_LEN = 512;

    public static String csvReadLine(BufferedReader bufferedReader) throws IOException, MalformedCsvException {
        String string;
        int[] nArray = new int[1];
        String string2 = FastCsvUtils.readLine(bufferedReader, nArray);
        if (string2 != null) {
            int n = FastCsvUtils.countQuotes(string2);
            if (n % 2 != 0) {
                StringBuilder stringBuilder = new StringBuilder(string2);
                do {
                    FastCsvUtils.appendForToken(nArray[0], stringBuilder);
                    string2 = FastCsvUtils.readLine(bufferedReader, nArray);
                    if (string2 == null) {
                        throw new MalformedCsvException("Unterminated quote at EOF: '" + stringBuilder.toString() + "'");
                    }
                    stringBuilder.append(string2);
                } while ((n += FastCsvUtils.countQuotes(string2)) % 2 != 0);
                string = stringBuilder.toString();
            } else {
                string = string2;
            }
        } else {
            string = null;
        }
        return string;
    }

    private static void appendForToken(int n, StringBuilder stringBuilder) {
        switch (n) {
            case 10: 
            case 13: {
                stringBuilder.append((char)n);
                break;
            }
            case 999: {
                stringBuilder.append(CRLF);
                break;
            }
            case -1: {
                break;
            }
            default: {
                throw new InternalError("Unexpected token (should never happen): " + n);
            }
        }
    }

    private static String readLine(BufferedReader bufferedReader, int[] nArray) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(512);
        int n = bufferedReader.read();
        if (n < 0) {
            nArray[0] = -1;
            return null;
        }
        while (FastCsvUtils.notSepOrEOF(n)) {
            stringBuilder.append((char)n);
            n = bufferedReader.read();
        }
        if (n == 13) {
            bufferedReader.mark(1);
            int n2 = bufferedReader.read();
            if (n2 == 10) {
                nArray[0] = 999;
            } else {
                bufferedReader.reset();
                nArray[0] = 13;
            }
        } else {
            nArray[0] = n;
        }
        return stringBuilder.toString();
    }

    private static boolean notSepOrEOF(int n) {
        return n >= 0 && n != 10 && n != 13;
    }

    private static int countQuotes(String string) {
        char[] cArray = string.toCharArray();
        int n = 0;
        int n2 = cArray.length;
        for (int i = 0; i < n2; ++i) {
            if (cArray[i] != '\"') continue;
            ++n;
        }
        return n;
    }

    public static String[] splitRecord(String string) throws MalformedCsvException {
        int[] nArray = FastCsvUtils.upshiftQuoteString(string);
        List list = FastCsvUtils.splitShifted(nArray);
        int n = list.size();
        String[] stringArray = new String[n];
        for (int i = 0; i < n; ++i) {
            stringArray[i] = FastCsvUtils.downshift((int[])list.get(i));
        }
        return stringArray;
    }

    private static void debugPrint(int[] nArray) {
        int n = nArray.length;
        char[] cArray = new char[n];
        for (int i = 0; i < n; ++i) {
            cArray[i] = (char)(FastCsvUtils.isShifted(nArray[i]) ? 95 : (char)nArray[i]);
        }
        System.err.println(new String(cArray));
    }

    private static List splitShifted(int[] nArray) {
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        int n = 0;
        int n2 = nArray.length;
        for (int i = 0; i <= n2; ++i) {
            int n3;
            if (i != n2 && nArray[i] != 44) continue;
            int n4 = i - n;
            int n5 = -1;
            for (n3 = n; n3 <= i; ++n3) {
                if (n3 == i) {
                    n5 = 0;
                    break;
                }
                if (nArray[n3] != 32 && nArray[n3] != 9) break;
            }
            if (n5 < 0) {
                if (n3 == i - 1) {
                    n5 = 1;
                } else {
                    int n6;
                    for (n5 = i - n3; n5 > 0 && (nArray[n6 = n3 + n5 - 1] == 32 || nArray[n6] == 9); --n5) {
                    }
                }
            }
            int[] nArray2 = new int[n5];
            if (n5 > 0) {
                System.arraycopy(nArray, n3, nArray2, 0, n5);
            }
            arrayList.add(nArray2);
            n = i + 1;
        }
        return arrayList;
    }

    private static String downshift(int[] nArray) {
        int n = nArray.length;
        char[] cArray = new char[n];
        for (int i = 0; i < n; ++i) {
            int n2 = nArray[i];
            cArray[i] = (char)(FastCsvUtils.isShifted(n2) ? n2 >>> 8 : n2);
        }
        return new String(cArray);
    }

    private static boolean isShifted(int n) {
        return (n & 0x2000000) != 0;
    }

    private static int[] upshiftQuoteString(String string) throws MalformedCsvException {
        char[] cArray = string.toCharArray();
        int[] nArray = new int[cArray.length];
        EscapedCharReader escapedCharReader = new EscapedCharReader(cArray);
        int n = 0;
        boolean bl = false;
        int n2 = escapedCharReader.read(bl);
        while (n2 >= 0) {
            if (n2 == 34) {
                bl = !bl;
            } else {
                nArray[n++] = FastCsvUtils.findShiftyChar(n2, bl);
            }
            n2 = escapedCharReader.read(bl);
        }
        int[] nArray2 = new int[n];
        System.arraycopy(nArray, 0, nArray2, 0, n);
        return nArray2;
    }

    private static int findShiftyChar(int n, boolean bl) {
        return bl ? n << 8 | 0x2000000 : n;
    }

    private static int escape(int n) {
        return n | 0x1000000;
    }

    private static boolean isEscaped(int n) {
        return (n & 0x1000000) != 0;
    }

    private FastCsvUtils() {
    }

    private static class EscapedCharReader {
        char[] chars;
        int finger;

        EscapedCharReader(char[] cArray) {
            this.chars = cArray;
            this.finger = 0;
        }

        int read(boolean bl) throws MalformedCsvException {
            if (this.finger < this.chars.length) {
                char c;
                if ((c = this.chars[this.finger++]) == '\"' && bl) {
                    if (this.finger < this.chars.length) {
                        char c2 = this.chars[this.finger];
                        if (c2 == '\"') {
                            ++this.finger;
                            return FastCsvUtils.escape(c2);
                        }
                        return c;
                    }
                    return c;
                }
                return c;
            }
            return -1;
        }
    }
}

