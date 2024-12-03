/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.core.compiler;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.core.compiler.SubwordMatcher;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

public final class CharOperation {
    public static final char[] NO_CHAR = new char[0];
    public static final char[][] NO_CHAR_CHAR = new char[0][];
    public static final String[] NO_STRINGS = new String[0];
    public static final char[] ALL_PREFIX = new char[]{'*'};
    public static final char[] COMMA_SEPARATOR = new char[]{','};
    private static final int[] EMPTY_REGIONS = new int[0];

    public static final char[] append(char[] array, char suffix) {
        if (array == null) {
            return new char[]{suffix};
        }
        int length = array.length;
        char[] cArray = array;
        array = new char[length + 1];
        System.arraycopy(cArray, 0, array, 0, length);
        array[length] = suffix;
        return array;
    }

    public static final char[] append(char[] target, char[] suffix) {
        if (suffix == null || suffix.length == 0) {
            return target;
        }
        int targetLength = target.length;
        int subLength = suffix.length;
        int newTargetLength = targetLength + subLength;
        if (newTargetLength > targetLength) {
            char[] cArray = target;
            target = new char[newTargetLength];
            System.arraycopy(cArray, 0, target, 0, targetLength);
        }
        System.arraycopy(suffix, 0, target, targetLength, subLength);
        return target;
    }

    public static final char[] append(char[] target, int index, char[] array, int start, int end) {
        int subLength = end - start;
        int newTargetLength = subLength + index;
        int targetLength = target.length;
        if (newTargetLength > targetLength) {
            char[] cArray = target;
            target = new char[newTargetLength * 2];
            System.arraycopy(cArray, 0, target, 0, index);
        }
        System.arraycopy(array, start, target, index, subLength);
        return target;
    }

    public static final char[] prepend(char prefix, char[] array) {
        if (array == null) {
            return new char[]{prefix};
        }
        int length = array.length;
        char[] cArray = array;
        array = new char[length + 1];
        System.arraycopy(cArray, 0, array, 1, length);
        array[0] = prefix;
        return array;
    }

    public static final char[][] arrayConcat(char[][] first, char[][] second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        int length1 = first.length;
        int length2 = second.length;
        char[][] result = new char[length1 + length2][];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        return result;
    }

    public static final boolean camelCaseMatch(char[] pattern, char[] name) {
        if (pattern == null) {
            return true;
        }
        if (name == null) {
            return false;
        }
        return CharOperation.camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length, false);
    }

    public static final boolean camelCaseMatch(char[] pattern, char[] name, boolean samePartCount) {
        if (pattern == null) {
            return true;
        }
        if (name == null) {
            return false;
        }
        return CharOperation.camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length, samePartCount);
    }

    public static final boolean camelCaseMatch(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd) {
        return CharOperation.camelCaseMatch(pattern, patternStart, patternEnd, name, nameStart, nameEnd, false);
    }

    public static final boolean camelCaseMatch(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd, boolean samePartCount) {
        if (name == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        if (patternEnd < 0) {
            patternEnd = pattern.length;
        }
        if (nameEnd < 0) {
            nameEnd = name.length;
        }
        if (patternEnd <= patternStart) {
            return nameEnd <= nameStart;
        }
        if (nameEnd <= nameStart) {
            return false;
        }
        if (name[nameStart] != pattern[patternStart]) {
            return false;
        }
        int iPattern = patternStart;
        int iName = nameStart;
        block0: while (true) {
            char nameChar;
            ++iName;
            if (++iPattern == patternEnd) {
                if (!samePartCount || iName == nameEnd) {
                    return true;
                }
                while (true) {
                    if (iName == nameEnd) {
                        return true;
                    }
                    nameChar = name[iName];
                    if (nameChar < '\u0080' ? (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar] & 0x20) != 0 : !Character.isJavaIdentifierPart(nameChar) || Character.isUpperCase(nameChar)) {
                        return false;
                    }
                    ++iName;
                }
            }
            if (iName == nameEnd) {
                return false;
            }
            char patternChar = pattern[iPattern];
            if (patternChar == name[iName]) continue;
            if (patternChar < '\u0080' ? (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[patternChar] & 0x24) == 0 : Character.isJavaIdentifierPart(patternChar) && !Character.isUpperCase(patternChar) && !Character.isDigit(patternChar)) {
                return false;
            }
            while (true) {
                if (iName == nameEnd) {
                    return false;
                }
                nameChar = name[iName];
                if (nameChar < '\u0080') {
                    int charNature = ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar];
                    if ((charNature & 0x90) != 0) {
                        ++iName;
                        continue;
                    }
                    if ((charNature & 4) != 0) {
                        if (patternChar == nameChar) continue block0;
                        ++iName;
                        continue;
                    }
                    if (patternChar == nameChar) continue block0;
                    return false;
                }
                if (Character.isJavaIdentifierPart(nameChar) && !Character.isUpperCase(nameChar)) {
                    ++iName;
                    continue;
                }
                if (!Character.isDigit(nameChar)) break;
                if (patternChar == nameChar) continue block0;
                ++iName;
            }
            if (patternChar != nameChar) break;
        }
        return false;
    }

    public static final boolean subWordMatch(char[] pattern, char[] name) {
        if (name == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        int[] matchingRegions = CharOperation.getSubWordMatchingRegions(new String(pattern), new String(name));
        return matchingRegions != null;
    }

    public static final int[] getSubWordMatchingRegions(String pattern, String name) {
        if (name == null) {
            return null;
        }
        if (pattern == null) {
            return EMPTY_REGIONS;
        }
        return new SubwordMatcher(name).getMatchingRegions(pattern);
    }

    public static final boolean substringMatch(String pattern, String name) {
        if (pattern == null || pattern.length() == 0) {
            return true;
        }
        if (name == null) {
            return false;
        }
        return CharOperation.checkSubstringMatch(pattern.toCharArray(), name.toCharArray());
    }

    public static final boolean substringMatch(char[] pattern, char[] name) {
        if (pattern == null || pattern.length == 0) {
            return true;
        }
        if (name == null) {
            return false;
        }
        return CharOperation.checkSubstringMatch(pattern, name);
    }

    private static final boolean checkSubstringMatch(char[] pattern, char[] name) {
        int nidx = 0;
        while (nidx < name.length - pattern.length + 1) {
            int pidx = 0;
            while (pidx < pattern.length) {
                if (Character.toLowerCase(name[nidx + pidx]) != Character.toLowerCase(pattern[pidx])) {
                    if (name[nidx + pidx] != '(' && name[nidx + pidx] != ':') break;
                    return false;
                }
                if (pidx == pattern.length - 1) {
                    return true;
                }
                ++pidx;
            }
            ++nidx;
        }
        return false;
    }

    public static String[] charArrayToStringArray(char[][] charArrays) {
        if (charArrays == null) {
            return null;
        }
        int length = charArrays.length;
        if (length == 0) {
            return NO_STRINGS;
        }
        String[] strings = new String[length];
        int i = 0;
        while (i < length) {
            strings[i] = new String(charArrays[i]);
            ++i;
        }
        return strings;
    }

    public static String charToString(char[] charArray) {
        if (charArray == null) {
            return null;
        }
        return new String(charArray);
    }

    public static char[][] toCharArrays(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        char[][] result = new char[stringList.size()][];
        int i = 0;
        while (i < result.length) {
            result[i] = stringList.get(i).toCharArray();
            ++i;
        }
        return result;
    }

    public static final char[][] arrayConcat(char[][] first, char[] second) {
        if (second == null) {
            return first;
        }
        if (first == null) {
            return new char[][]{second};
        }
        int length = first.length;
        char[][] result = new char[length + 1][];
        System.arraycopy(first, 0, result, 0, length);
        result[length] = second;
        return result;
    }

    public static final int compareTo(char[] array1, char[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        int min = Math.min(length1, length2);
        int i = 0;
        while (i < min) {
            if (array1[i] != array2[i]) {
                return array1[i] - array2[i];
            }
            ++i;
        }
        return length1 - length2;
    }

    public static final int compareTo(char[] array1, char[] array2, int start, int end) {
        int length1 = array1.length;
        int length2 = array2.length;
        int min = Math.min(length1, length2);
        min = Math.min(min, end);
        int i = start;
        while (i < min) {
            if (array1[i] != array2[i]) {
                return array1[i] - array2[i];
            }
            ++i;
        }
        return length1 - length2;
    }

    public static final int compareWith(char[] array, char[] prefix) {
        int arrayLength = array.length;
        int prefixLength = prefix.length;
        int min = Math.min(arrayLength, prefixLength);
        int i = 0;
        while (min-- != 0) {
            char c2;
            char c1 = array[i];
            if (c1 == (c2 = prefix[i++])) continue;
            return c1 - c2;
        }
        if (prefixLength == i) {
            return 0;
        }
        return -1;
    }

    public static final char[] concat(char[] first, char[] second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        int length1 = first.length;
        int length2 = second.length;
        char[] result = new char[length1 + length2];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        return result;
    }

    public static final char[] concat(char[] first, char[] second, char[] third) {
        if (first == null) {
            return CharOperation.concat(second, third);
        }
        if (second == null) {
            return CharOperation.concat(first, third);
        }
        if (third == null) {
            return CharOperation.concat(first, second);
        }
        int length1 = first.length;
        int length2 = second.length;
        int length3 = third.length;
        char[] result = new char[length1 + length2 + length3];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        System.arraycopy(third, 0, result, length1 + length2, length3);
        return result;
    }

    public static final char[] concat(char[] first, char[] second, char separator) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        int length1 = first.length;
        if (length1 == 0) {
            return second;
        }
        int length2 = second.length;
        if (length2 == 0) {
            return first;
        }
        char[] result = new char[length1 + length2 + 1];
        System.arraycopy(first, 0, result, 0, length1);
        result[length1] = separator;
        System.arraycopy(second, 0, result, length1 + 1, length2);
        return result;
    }

    public static final char[] concatAll(char[] first, char[] second, char separator) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        int length1 = first.length;
        if (length1 == 0) {
            return second;
        }
        int length2 = second.length;
        char[] result = new char[length1 + length2 + 1];
        System.arraycopy(first, 0, result, 0, length1);
        result[length1] = separator;
        if (length2 > 0) {
            System.arraycopy(second, 0, result, length1 + 1, length2);
        }
        return result;
    }

    public static final char[] concat(char[] first, char sep1, char[] second, char sep2, char[] third) {
        if (first == null) {
            return CharOperation.concat(second, third, sep2);
        }
        if (second == null) {
            return CharOperation.concat(first, third, sep1);
        }
        if (third == null) {
            return CharOperation.concat(first, second, sep1);
        }
        int length1 = first.length;
        int length2 = second.length;
        int length3 = third.length;
        char[] result = new char[length1 + length2 + length3 + 2];
        System.arraycopy(first, 0, result, 0, length1);
        result[length1] = sep1;
        System.arraycopy(second, 0, result, length1 + 1, length2);
        result[length1 + length2 + 1] = sep2;
        System.arraycopy(third, 0, result, length1 + length2 + 2, length3);
        return result;
    }

    public static final char[] concatNonEmpty(char[] first, char[] second, char separator) {
        if (first == null || first.length == 0) {
            return second;
        }
        if (second == null || second.length == 0) {
            return first;
        }
        return CharOperation.concat(first, second, separator);
    }

    public static final char[] concatNonEmpty(char[] first, char sep1, char[] second, char sep2, char[] third) {
        if (first == null || first.length == 0) {
            return CharOperation.concatNonEmpty(second, third, sep2);
        }
        if (second == null || second.length == 0) {
            return CharOperation.concatNonEmpty(first, third, sep1);
        }
        if (third == null || third.length == 0) {
            return CharOperation.concatNonEmpty(first, second, sep1);
        }
        return CharOperation.concat(first, sep1, second, sep2, third);
    }

    public static final char[] concat(char prefix, char[] array, char suffix) {
        if (array == null) {
            return new char[]{prefix, suffix};
        }
        int length = array.length;
        char[] result = new char[length + 2];
        result[0] = prefix;
        System.arraycopy(array, 0, result, 1, length);
        result[length + 1] = suffix;
        return result;
    }

    public static final char[] concatWith(char[] name, char[][] array, char separator) {
        int length;
        int nameLength;
        int n = nameLength = name == null ? 0 : name.length;
        if (nameLength == 0) {
            return CharOperation.concatWith(array, separator);
        }
        int n2 = length = array == null ? 0 : array.length;
        if (length == 0) {
            return name;
        }
        int size = nameLength;
        int index = length;
        while (--index >= 0) {
            if (array[index].length <= 0) continue;
            size += array[index].length + 1;
        }
        char[] result = new char[size];
        index = size;
        int i = length - 1;
        while (i >= 0) {
            int subLength = array[i].length;
            if (subLength > 0) {
                System.arraycopy(array[i], 0, result, index -= subLength, subLength);
                result[--index] = separator;
            }
            --i;
        }
        System.arraycopy(name, 0, result, 0, nameLength);
        return result;
    }

    public static final char[] concatWith(char[][] array, char[] name, char separator) {
        int length;
        int nameLength;
        int n = nameLength = name == null ? 0 : name.length;
        if (nameLength == 0) {
            return CharOperation.concatWith(array, separator);
        }
        int n2 = length = array == null ? 0 : array.length;
        if (length == 0) {
            return name;
        }
        int size = nameLength;
        int index = length;
        while (--index >= 0) {
            if (array[index].length <= 0) continue;
            size += array[index].length + 1;
        }
        char[] result = new char[size];
        index = 0;
        int i = 0;
        while (i < length) {
            int subLength = array[i].length;
            if (subLength > 0) {
                System.arraycopy(array[i], 0, result, index, subLength);
                index += subLength;
                result[index++] = separator;
            }
            ++i;
        }
        System.arraycopy(name, 0, result, index, nameLength);
        return result;
    }

    public static final char[] concatWith(char[][] array, char separator) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0) {
            return NO_CHAR;
        }
        int size = length - 1;
        int index = length;
        while (--index >= 0) {
            if (array[index].length == 0) {
                --size;
                continue;
            }
            size += array[index].length;
        }
        if (size <= 0) {
            return NO_CHAR;
        }
        char[] result = new char[size];
        index = length;
        while (--index >= 0) {
            length = array[index].length;
            if (length <= 0) continue;
            System.arraycopy(array[index], 0, result, size -= length, length);
            if (--size < 0) continue;
            result[size] = separator;
        }
        return result;
    }

    public static final char[] concatWithAll(char[][] array, char separator) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0) {
            return NO_CHAR;
        }
        int size = length - 1;
        int index = length;
        while (--index >= 0) {
            size += array[index].length;
        }
        char[] result = new char[size];
        index = length;
        while (--index >= 0) {
            length = array[index].length;
            if (length > 0) {
                System.arraycopy(array[index], 0, result, size -= length, length);
            }
            if (--size < 0) continue;
            result[size] = separator;
        }
        return result;
    }

    public static final boolean contains(char character, char[][] array) {
        int i = array.length;
        while (--i >= 0) {
            char[] subarray = array[i];
            int j = subarray.length;
            while (--j >= 0) {
                if (subarray[j] != character) continue;
                return true;
            }
        }
        return false;
    }

    public static final boolean contains(char character, char[] array) {
        int i = array.length;
        while (--i >= 0) {
            if (array[i] != character) continue;
            return true;
        }
        return false;
    }

    public static final boolean contains(char[] characters, char[] array) {
        int i = array.length;
        while (--i >= 0) {
            int j = characters.length;
            while (--j >= 0) {
                if (array[i] != characters[j]) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean containsEqual(char[][] array, char[] sequence) {
        int i = 0;
        while (i < array.length) {
            if (CharOperation.equals(array[i], sequence)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public static final char[][] deepCopy(char[][] toCopy) {
        int toCopyLength = toCopy.length;
        char[][] result = new char[toCopyLength][];
        int i = 0;
        while (i < toCopyLength) {
            char[] toElement = toCopy[i];
            int toElementLength = toElement.length;
            char[] resultElement = new char[toElementLength];
            System.arraycopy(toElement, 0, resultElement, 0, toElementLength);
            result[i] = resultElement;
            ++i;
        }
        return result;
    }

    /*
     * Unable to fully structure code
     */
    public static final boolean endsWith(char[] array, char[] toBeFound) {
        i = toBeFound.length;
        j = array.length - i;
        if (j >= 0) ** GOTO lbl7
        return false;
lbl-1000:
        // 1 sources

        {
            if (toBeFound[i] == array[i + j]) continue;
            return false;
lbl7:
            // 2 sources

            ** while (--i >= 0)
        }
lbl8:
        // 1 sources

        return true;
    }

    public static final boolean equals(char[][] first, char[][] second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (CharOperation.equals(first[i], second[i])) continue;
            return false;
        }
        return true;
    }

    public static final boolean equals(char[][] first, char[][] second, boolean isCaseSensitive) {
        if (isCaseSensitive) {
            return CharOperation.equals(first, second);
        }
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (CharOperation.equals(first[i], second[i], false)) continue;
            return false;
        }
        return true;
    }

    public static final boolean equals(char[] first, char[] second) {
        return Arrays.equals(first, second);
    }

    public static final boolean equals(char[] first, char[] second, int secondStart, int secondEnd) {
        return CharOperation.equals(first, second, secondStart, secondEnd, true);
    }

    public static final boolean equals(char[] first, char[] second, int secondStart, int secondEnd, boolean isCaseSensitive) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != secondEnd - secondStart) {
            return false;
        }
        if (isCaseSensitive) {
            int i = first.length;
            while (--i >= 0) {
                if (first[i] == second[i + secondStart]) continue;
                return false;
            }
        } else {
            int i = first.length;
            while (--i >= 0) {
                if (ScannerHelper.toLowerCase(first[i]) == ScannerHelper.toLowerCase(second[i + secondStart])) continue;
                return false;
            }
        }
        return true;
    }

    public static final boolean equals(char[] first, char[] second, boolean isCaseSensitive) {
        if (isCaseSensitive) {
            return CharOperation.equals(first, second);
        }
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (ScannerHelper.toLowerCase(first[i]) == ScannerHelper.toLowerCase(second[i])) continue;
            return false;
        }
        return true;
    }

    public static final boolean fragmentEquals(char[] fragment, char[] name, int startIndex, boolean isCaseSensitive) {
        int max = fragment.length;
        if (name.length < max + startIndex) {
            return false;
        }
        if (isCaseSensitive) {
            int i = max;
            while (--i >= 0) {
                if (fragment[i] == name[i + startIndex]) continue;
                return false;
            }
            return true;
        }
        int i = max;
        while (--i >= 0) {
            if (ScannerHelper.toLowerCase(fragment[i]) == ScannerHelper.toLowerCase(name[i + startIndex])) continue;
            return false;
        }
        return true;
    }

    public static final int hashCode(char[] array) {
        int hash = Arrays.hashCode(array);
        return hash & Integer.MAX_VALUE;
    }

    public static boolean isWhitespace(char c) {
        return c < '\u0080' && (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x100) != 0;
    }

    public static final int indexOf(char toBeFound, char[] array) {
        return CharOperation.indexOf(toBeFound, array, 0);
    }

    public static final int indexOf(char[] toBeFound, char[] array, boolean isCaseSensitive) {
        return CharOperation.indexOf(toBeFound, array, isCaseSensitive, 0);
    }

    public static final int indexOf(char[] toBeFound, char[] array, boolean isCaseSensitive, int start) {
        return CharOperation.indexOf(toBeFound, array, isCaseSensitive, start, array.length);
    }

    public static final int indexOf(char[] toBeFound, char[] array, boolean isCaseSensitive, int start, int end) {
        int toBeFoundLength = toBeFound.length;
        int arrayLength = end;
        if (toBeFoundLength > arrayLength || start < 0) {
            return -1;
        }
        if (toBeFoundLength == 0) {
            return 0;
        }
        if (toBeFoundLength == arrayLength) {
            if (isCaseSensitive) {
                int i = start;
                while (i < arrayLength) {
                    if (array[i] != toBeFound[i]) {
                        return -1;
                    }
                    ++i;
                }
                return 0;
            }
            int i = start;
            while (i < arrayLength) {
                if (ScannerHelper.toLowerCase(array[i]) != ScannerHelper.toLowerCase(toBeFound[i])) {
                    return -1;
                }
                ++i;
            }
            return 0;
        }
        if (isCaseSensitive) {
            int i = start;
            int max = arrayLength - toBeFoundLength + 1;
            while (i < max) {
                block18: {
                    if (array[i] == toBeFound[0]) {
                        int j = 1;
                        while (j < toBeFoundLength) {
                            if (array[i + j] == toBeFound[j]) {
                                ++j;
                                continue;
                            }
                            break block18;
                        }
                        return i;
                    }
                }
                ++i;
            }
        } else {
            int i = start;
            int max = arrayLength - toBeFoundLength + 1;
            while (i < max) {
                block19: {
                    if (ScannerHelper.toLowerCase(array[i]) == ScannerHelper.toLowerCase(toBeFound[0])) {
                        int j = 1;
                        while (j < toBeFoundLength) {
                            if (ScannerHelper.toLowerCase(array[i + j]) == ScannerHelper.toLowerCase(toBeFound[j])) {
                                ++j;
                                continue;
                            }
                            break block19;
                        }
                        return i;
                    }
                }
                ++i;
            }
        }
        return -1;
    }

    public static final int indexOf(char toBeFound, char[] array, int start) {
        int i = start;
        while (i < array.length) {
            if (toBeFound == array[i]) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static final int indexOf(char toBeFound, char[] array, int start, int end) {
        int i = start;
        while (i < end) {
            if (toBeFound == array[i]) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static final int lastIndexOf(char toBeFound, char[] array) {
        int i = array.length;
        while (--i >= 0) {
            if (toBeFound != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static final int lastIndexOf(char toBeFound, char[] array, int startIndex) {
        int i = array.length;
        while (--i >= startIndex) {
            if (toBeFound != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static final int lastIndexOf(char toBeFound, char[] array, int startIndex, int endIndex) {
        int i = endIndex;
        while (--i >= startIndex) {
            if (toBeFound != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static final char[] lastSegment(char[] array, char separator) {
        int pos = CharOperation.lastIndexOf(separator, array);
        if (pos < 0) {
            return array;
        }
        return CharOperation.subarray(array, pos + 1, array.length);
    }

    public static final boolean match(char[] pattern, char[] name, boolean isCaseSensitive) {
        if (name == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        return CharOperation.match(pattern, 0, pattern.length, name, 0, name.length, isCaseSensitive);
    }

    public static final boolean match(char[] pattern, int patternStart, int patternEnd, char[] name, int nameStart, int nameEnd, boolean isCaseSensitive) {
        if (name == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        int iPattern = patternStart;
        int iName = nameStart;
        if (patternEnd < 0) {
            patternEnd = pattern.length;
        }
        if (nameEnd < 0) {
            nameEnd = name.length;
        }
        char patternChar = '\u0000';
        while (true) {
            if (iPattern == patternEnd) {
                return iName == nameEnd;
            }
            patternChar = pattern[iPattern];
            if (patternChar == '*') break;
            if (iName == nameEnd) {
                return false;
            }
            if (patternChar != (isCaseSensitive ? name[iName] : ScannerHelper.toLowerCase(name[iName])) && patternChar != '?') {
                return false;
            }
            ++iName;
            ++iPattern;
        }
        int segmentStart = patternChar == '*' ? ++iPattern : 0;
        int prefixStart = iName;
        while (iName < nameEnd) {
            if (iPattern == patternEnd) {
                iPattern = segmentStart;
                iName = ++prefixStart;
                continue;
            }
            patternChar = pattern[iPattern];
            if (patternChar == '*') {
                if ((segmentStart = ++iPattern) == patternEnd) {
                    return true;
                }
                prefixStart = iName;
                continue;
            }
            if ((isCaseSensitive ? name[iName] : ScannerHelper.toLowerCase(name[iName])) != patternChar && patternChar != '?') {
                iPattern = segmentStart;
                iName = ++prefixStart;
                continue;
            }
            ++iName;
            ++iPattern;
        }
        return segmentStart == patternEnd || iName == nameEnd && iPattern == patternEnd || iPattern == patternEnd - 1 && pattern[iPattern] == '*';
    }

    public static final boolean pathMatch(char[] pattern, char[] filepath, boolean isCaseSensitive, char pathSeparator) {
        int pSegmentRestart;
        if (filepath == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        int pSegmentStart = pattern[0] == pathSeparator ? 1 : 0;
        int pLength = pattern.length;
        int pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart + 1);
        if (pSegmentEnd < 0) {
            pSegmentEnd = pLength;
        }
        boolean freeTrailingDoubleStar = pattern[pLength - 1] == pathSeparator;
        int fLength = filepath.length;
        int fSegmentStart = filepath[0] != pathSeparator ? 0 : 1;
        if (fSegmentStart != pSegmentStart) {
            return false;
        }
        int fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath, fSegmentStart + 1);
        if (fSegmentEnd < 0) {
            fSegmentEnd = fLength;
        }
        while (!(pSegmentStart >= pLength || pSegmentEnd == pLength && freeTrailingDoubleStar || pSegmentEnd == pSegmentStart + 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*')) {
            if (fSegmentStart >= fLength) {
                return false;
            }
            if (!CharOperation.match(pattern, pSegmentStart, pSegmentEnd, filepath, fSegmentStart, fSegmentEnd, isCaseSensitive)) {
                return false;
            }
            pSegmentStart = pSegmentEnd + 1;
            if ((pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart)) < 0) {
                pSegmentEnd = pLength;
            }
            if ((fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath, fSegmentStart = fSegmentEnd + 1)) >= 0) continue;
            fSegmentEnd = fLength;
        }
        if (pSegmentStart >= pLength && freeTrailingDoubleStar || pSegmentEnd == pSegmentStart + 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*') {
            pSegmentStart = pSegmentEnd + 1;
            if ((pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart)) < 0) {
                pSegmentEnd = pLength;
            }
            pSegmentRestart = pSegmentStart;
        } else {
            if (pSegmentStart >= pLength) {
                return fSegmentStart >= fLength;
            }
            pSegmentRestart = 0;
        }
        int fSegmentRestart = fSegmentStart;
        while (fSegmentStart < fLength) {
            if (pSegmentStart >= pLength) {
                if (freeTrailingDoubleStar) {
                    return true;
                }
                pSegmentStart = pSegmentRestart;
                pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart);
                if (pSegmentEnd < 0) {
                    pSegmentEnd = pLength;
                }
                if ((fSegmentRestart = CharOperation.indexOf(pathSeparator, filepath, fSegmentRestart + 1)) < 0) {
                    fSegmentRestart = fLength;
                }
                fSegmentStart = ++fSegmentRestart;
                fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath, fSegmentStart);
                if (fSegmentEnd >= 0) continue;
                fSegmentEnd = fLength;
                continue;
            }
            if (pSegmentEnd == pSegmentStart + 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*') {
                pSegmentStart = pSegmentEnd + 1;
                if ((pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart)) < 0) {
                    pSegmentEnd = pLength;
                }
                pSegmentRestart = pSegmentStart;
                fSegmentRestart = fSegmentStart;
                if (pSegmentStart < pLength) continue;
                return true;
            }
            if (!CharOperation.match(pattern, pSegmentStart, pSegmentEnd, filepath, fSegmentStart, fSegmentEnd, isCaseSensitive)) {
                pSegmentStart = pSegmentRestart;
                pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart);
                if (pSegmentEnd < 0) {
                    pSegmentEnd = pLength;
                }
                if ((fSegmentRestart = CharOperation.indexOf(pathSeparator, filepath, fSegmentRestart + 1)) < 0) {
                    fSegmentRestart = fLength;
                }
                fSegmentStart = ++fSegmentRestart;
                fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath, fSegmentStart);
                if (fSegmentEnd >= 0) continue;
                fSegmentEnd = fLength;
                continue;
            }
            pSegmentStart = pSegmentEnd + 1;
            if ((pSegmentEnd = CharOperation.indexOf(pathSeparator, pattern, pSegmentStart)) < 0) {
                pSegmentEnd = pLength;
            }
            if ((fSegmentEnd = CharOperation.indexOf(pathSeparator, filepath, fSegmentStart = fSegmentEnd + 1)) >= 0) continue;
            fSegmentEnd = fLength;
        }
        return pSegmentRestart >= pSegmentEnd || fSegmentStart >= fLength && pSegmentStart >= pLength || pSegmentStart == pLength - 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*' || pSegmentStart == pLength && freeTrailingDoubleStar;
    }

    public static final int occurencesOf(char toBeFound, char[] array) {
        int count = 0;
        int i = 0;
        while (i < array.length) {
            if (toBeFound == array[i]) {
                ++count;
            }
            ++i;
        }
        return count;
    }

    public static final int occurencesOf(char toBeFound, char[] array, int start) {
        int count = 0;
        int i = start;
        while (i < array.length) {
            if (toBeFound == array[i]) {
                ++count;
            }
            ++i;
        }
        return count;
    }

    public static final int parseInt(char[] array, int start, int length) throws NumberFormatException {
        if (length == 1) {
            int result = array[start] - 48;
            if (result < 0 || result > 9) {
                throw new NumberFormatException("invalid digit");
            }
            return result;
        }
        return Integer.parseInt(new String(array, start, length));
    }

    public static final boolean prefixEquals(char[] prefix, char[] name) {
        int max = prefix.length;
        if (name.length < max) {
            return false;
        }
        int i = max;
        while (--i >= 0) {
            if (prefix[i] == name[i]) continue;
            return false;
        }
        return true;
    }

    public static final boolean prefixEquals(char[] prefix, char[] name, boolean isCaseSensitive) {
        return CharOperation.prefixEquals(prefix, name, isCaseSensitive, 0);
    }

    public static final boolean prefixEquals(char[] prefix, char[] name, boolean isCaseSensitive, int startIndex) {
        int max = prefix.length;
        if (name.length - startIndex < max) {
            return false;
        }
        if (isCaseSensitive) {
            int i = max;
            while (--i >= 0) {
                if (prefix[i] == name[startIndex + i]) continue;
                return false;
            }
            return true;
        }
        int i = max;
        while (--i >= 0) {
            if (ScannerHelper.toLowerCase(prefix[i]) == ScannerHelper.toLowerCase(name[startIndex + i])) continue;
            return false;
        }
        return true;
    }

    public static final char[] remove(char[] array, char toBeRemoved) {
        if (array == null) {
            return null;
        }
        int length = array.length;
        if (length == 0) {
            return array;
        }
        char[] result = null;
        int count = 0;
        int i = 0;
        while (i < length) {
            char c = array[i];
            if (c == toBeRemoved) {
                if (result == null) {
                    result = new char[length];
                    System.arraycopy(array, 0, result, 0, i);
                    count = i;
                }
            } else if (result != null) {
                result[count++] = c;
            }
            ++i;
        }
        if (result == null) {
            return array;
        }
        char[] cArray = result;
        result = new char[count];
        System.arraycopy(cArray, 0, result, 0, count);
        return result;
    }

    public static final void replace(char[] array, char toBeReplaced, char replacementChar) {
        if (toBeReplaced != replacementChar) {
            int i = 0;
            int max = array.length;
            while (i < max) {
                if (array[i] == toBeReplaced) {
                    array[i] = replacementChar;
                }
                ++i;
            }
        }
    }

    public static final void replace(char[] array, char[] toBeReplaced, char replacementChar) {
        CharOperation.replace(array, toBeReplaced, replacementChar, 0, array.length);
    }

    public static final void replace(char[] array, char[] toBeReplaced, char replacementChar, int start, int end) {
        int i = end;
        while (--i >= start) {
            int j = toBeReplaced.length;
            while (--j >= 0) {
                if (array[i] != toBeReplaced[j]) continue;
                array[i] = replacementChar;
            }
        }
    }

    public static final char[] replace(char[] array, char[] toBeReplaced, char[] replacementChars) {
        int max = array.length;
        int replacedLength = toBeReplaced.length;
        int replacementLength = replacementChars.length;
        int[] starts = new int[5];
        int occurrenceCount = 0;
        if (!CharOperation.equals(toBeReplaced, replacementChars)) {
            int i = 0;
            while (i < max) {
                int index = CharOperation.indexOf(toBeReplaced, array, true, i);
                if (index == -1) {
                    ++i;
                    continue;
                }
                if (occurrenceCount == starts.length) {
                    int[] nArray = starts;
                    starts = new int[occurrenceCount * 2];
                    System.arraycopy(nArray, 0, starts, 0, occurrenceCount);
                }
                starts[occurrenceCount++] = index;
                i = index + replacedLength;
            }
        }
        if (occurrenceCount == 0) {
            return array;
        }
        char[] result = new char[max + occurrenceCount * (replacementLength - replacedLength)];
        int inStart = 0;
        int outStart = 0;
        int i = 0;
        while (i < occurrenceCount) {
            int offset = starts[i] - inStart;
            System.arraycopy(array, inStart, result, outStart, offset);
            inStart += offset;
            System.arraycopy(replacementChars, 0, result, outStart += offset, replacementLength);
            inStart += replacedLength;
            outStart += replacementLength;
            ++i;
        }
        System.arraycopy(array, inStart, result, outStart, max - inStart);
        return result;
    }

    public static final char[] replaceOnCopy(char[] array, char toBeReplaced, char replacementChar) {
        char[] result = null;
        int i = 0;
        int length = array.length;
        while (i < length) {
            char c = array[i];
            if (c == toBeReplaced) {
                if (result == null) {
                    result = new char[length];
                    System.arraycopy(array, 0, result, 0, i);
                }
                result[i] = replacementChar;
            } else if (result != null) {
                result[i] = c;
            }
            ++i;
        }
        if (result == null) {
            return array;
        }
        return result;
    }

    public static final char[][] splitAndTrimOn(char divider, char[] array) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0) {
            return NO_CHAR_CHAR;
        }
        int wordCount = 1;
        int i = 0;
        while (i < length) {
            if (array[i] == divider) {
                ++wordCount;
            }
            ++i;
        }
        char[][] split = new char[wordCount][];
        int last = 0;
        int currentWord = 0;
        int i2 = 0;
        while (i2 < length) {
            if (array[i2] == divider) {
                int start = last;
                int end = i2 - 1;
                while (start < i2 && array[start] == ' ') {
                    ++start;
                }
                while (end > start && array[end] == ' ') {
                    --end;
                }
                split[currentWord] = new char[end - start + 1];
                System.arraycopy(array, start, split[currentWord++], 0, end - start + 1);
                last = i2 + 1;
            }
            ++i2;
        }
        int start = last;
        int end = length - 1;
        while (start < length && array[start] == ' ') {
            ++start;
        }
        while (end > start && array[end] == ' ') {
            --end;
        }
        split[currentWord] = new char[end - start + 1];
        System.arraycopy(array, start, split[currentWord++], 0, end - start + 1);
        return split;
    }

    public static final char[][] splitOn(char divider, char[] array) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0) {
            return NO_CHAR_CHAR;
        }
        int wordCount = 1;
        int i = 0;
        while (i < length) {
            if (array[i] == divider) {
                ++wordCount;
            }
            ++i;
        }
        char[][] split = new char[wordCount][];
        int last = 0;
        int currentWord = 0;
        int i2 = 0;
        while (i2 < length) {
            if (array[i2] == divider) {
                split[currentWord] = new char[i2 - last];
                System.arraycopy(array, last, split[currentWord++], 0, i2 - last);
                last = i2 + 1;
            }
            ++i2;
        }
        split[currentWord] = new char[length - last];
        System.arraycopy(array, last, split[currentWord], 0, length - last);
        return split;
    }

    public static final char[][] splitOn(char divider, char[] array, int start, int end) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0 || start > end) {
            return NO_CHAR_CHAR;
        }
        int wordCount = 1;
        int i = start;
        while (i < end) {
            if (array[i] == divider) {
                ++wordCount;
            }
            ++i;
        }
        char[][] split = new char[wordCount][];
        int last = start;
        int currentWord = 0;
        int i2 = start;
        while (i2 < end) {
            if (array[i2] == divider) {
                split[currentWord] = new char[i2 - last];
                System.arraycopy(array, last, split[currentWord++], 0, i2 - last);
                last = i2 + 1;
            }
            ++i2;
        }
        split[currentWord] = new char[end - last];
        System.arraycopy(array, last, split[currentWord], 0, end - last);
        return split;
    }

    public static final char[][] splitOnWithEnclosures(char divider, char openEncl, char closeEncl, char[] array, int start, int end) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0 || start > end) {
            return NO_CHAR_CHAR;
        }
        int wordCount = 1;
        int enclCount = 0;
        int i = start;
        while (i < end) {
            if (array[i] == openEncl) {
                ++enclCount;
            } else if (array[i] == divider) {
                ++wordCount;
            }
            ++i;
        }
        if (enclCount == 0) {
            return CharOperation.splitOn(divider, array, start, end);
        }
        int nesting = 0;
        if (openEncl == divider || closeEncl == divider) {
            return NO_CHAR_CHAR;
        }
        int[][] splitOffsets = new int[wordCount][2];
        int last = start;
        int currentWord = 0;
        int prevOffset = start;
        int i2 = start;
        while (i2 < end) {
            if (array[i2] == openEncl) {
                ++nesting;
            } else if (array[i2] == closeEncl) {
                if (nesting > 0) {
                    --nesting;
                }
            } else if (array[i2] == divider && nesting == 0) {
                splitOffsets[currentWord][0] = prevOffset;
                int n2 = currentWord++;
                int n3 = i2;
                splitOffsets[n2][1] = n3;
                last = n3;
                prevOffset = last + 1;
            }
            ++i2;
        }
        if (last < end - 1) {
            splitOffsets[currentWord][0] = prevOffset;
            splitOffsets[currentWord++][1] = end;
        }
        char[][] split = new char[currentWord][];
        int i3 = 0;
        while (i3 < currentWord) {
            int sStart = splitOffsets[i3][0];
            int sEnd = splitOffsets[i3][1];
            int size = sEnd - sStart;
            split[i3] = new char[size];
            System.arraycopy(array, sStart, split[i3], 0, size);
            ++i3;
        }
        return split;
    }

    public static final char[][] subarray(char[][] array, int start, int end) {
        if (end == -1) {
            end = array.length;
        }
        if (start > end) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (end > array.length) {
            return null;
        }
        char[][] result = new char[end - start][];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }

    public static final char[] subarray(char[] array, int start, int end) {
        if (end == -1) {
            end = array.length;
        }
        if (start > end) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (end > array.length) {
            return null;
        }
        char[] result = new char[end - start];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }

    public static final char[] toLowerCase(char[] chars) {
        if (chars == null) {
            return null;
        }
        int length = chars.length;
        char[] lowerChars = null;
        int i = 0;
        while (i < length) {
            char c = chars[i];
            char lc = ScannerHelper.toLowerCase(c);
            if (c != lc || lowerChars != null) {
                if (lowerChars == null) {
                    lowerChars = new char[length];
                    System.arraycopy(chars, 0, lowerChars, 0, i);
                }
                lowerChars[i] = lc;
            }
            ++i;
        }
        return lowerChars == null ? chars : lowerChars;
    }

    public static final char[] toUpperCase(char[] chars) {
        if (chars == null) {
            return null;
        }
        int length = chars.length;
        char[] upperChars = null;
        int i = 0;
        while (i < length) {
            char c = chars[i];
            char lc = ScannerHelper.toUpperCase(c);
            if (c != lc || upperChars != null) {
                if (upperChars == null) {
                    upperChars = new char[length];
                    System.arraycopy(chars, 0, upperChars, 0, i);
                }
                upperChars[i] = lc;
            }
            ++i;
        }
        return upperChars == null ? chars : upperChars;
    }

    public static final char[] trim(char[] chars) {
        if (chars == null) {
            return null;
        }
        int start = 0;
        int length = chars.length;
        int end = length - 1;
        while (start < length && chars[start] == ' ') {
            ++start;
        }
        while (end > start && chars[end] == ' ') {
            --end;
        }
        if (start != 0 || end != length - 1) {
            return CharOperation.subarray(chars, start, end + 1);
        }
        return chars;
    }

    public static final String toString(char[][] array) {
        char[] result = CharOperation.concatWith(array, '.');
        return new String(result);
    }

    public static final String[] toStrings(char[][] array) {
        if (array == null) {
            return NO_STRINGS;
        }
        int length = array.length;
        if (length == 0) {
            return NO_STRINGS;
        }
        String[] result = new String[length];
        int i = 0;
        while (i < length) {
            result[i] = new String(array[i]);
            ++i;
        }
        return result;
    }
}

