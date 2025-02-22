/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;

public enum FileSystem {
    GENERIC(4096, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE, new int[]{0}, new String[0], false, false, '/'),
    LINUX(8192, true, true, 255, 4096, new int[]{0, 47}, new String[0], false, false, '/'),
    MAC_OSX(4096, true, true, 255, 1024, new int[]{0, 47, 58}, new String[0], false, false, '/'),
    WINDOWS(4096, false, true, 255, 32000, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 34, 42, 47, 58, 60, 62, 63, 92, 124}, new String[]{"AUX", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "CON", "CONIN$", "CONOUT$", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", "NUL", "PRN"}, true, true, '\\');

    private static final boolean IS_OS_LINUX;
    private static final boolean IS_OS_MAC;
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    private static final boolean IS_OS_WINDOWS;
    private static final FileSystem CURRENT;
    private final int blockSize;
    private final boolean casePreserving;
    private final boolean caseSensitive;
    private final int[] illegalFileNameChars;
    private final int maxFileNameLength;
    private final int maxPathLength;
    private final String[] reservedFileNames;
    private final boolean reservedFileNamesExtensions;
    private final boolean supportsDriveLetter;
    private final char nameSeparator;
    private final char nameSeparatorOther;

    private static FileSystem current() {
        if (IS_OS_LINUX) {
            return LINUX;
        }
        if (IS_OS_MAC) {
            return MAC_OSX;
        }
        if (IS_OS_WINDOWS) {
            return WINDOWS;
        }
        return GENERIC;
    }

    public static FileSystem getCurrent() {
        return CURRENT;
    }

    private static boolean getOsMatchesName(String osNamePrefix) {
        return FileSystem.isOsNameMatch(FileSystem.getSystemProperty("os.name"), osNamePrefix);
    }

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException ex) {
            System.err.println("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }

    private static int indexOf(CharSequence cs, int searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar, start);
        }
        int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < 65536) {
            for (int i = start; i < sz; ++i) {
                if (cs.charAt(i) != searchChar) continue;
                return i;
            }
            return -1;
        }
        if (searchChar <= 0x10FFFF) {
            char[] chars = Character.toChars(searchChar);
            for (int i = start; i < sz - 1; ++i) {
                char high = cs.charAt(i);
                char low = cs.charAt(i + 1);
                if (high != chars[0] || low != chars[1]) continue;
                return i;
            }
        }
        return -1;
    }

    private static boolean isOsNameMatch(String osName, String osNamePrefix) {
        if (osName == null) {
            return false;
        }
        return osName.toUpperCase(Locale.ROOT).startsWith(osNamePrefix.toUpperCase(Locale.ROOT));
    }

    private static String replace(String path, char oldChar, char newChar) {
        return path == null ? null : path.replace(oldChar, newChar);
    }

    private FileSystem(int blockSize, boolean caseSensitive, boolean casePreserving, int maxFileLength, int maxPathLength, int[] illegalFileNameChars, String[] reservedFileNames, boolean reservedFileNamesExtensions, boolean supportsDriveLetter, char nameSeparator) {
        this.blockSize = blockSize;
        this.maxFileNameLength = maxFileLength;
        this.maxPathLength = maxPathLength;
        this.illegalFileNameChars = Objects.requireNonNull(illegalFileNameChars, "illegalFileNameChars");
        this.reservedFileNames = Objects.requireNonNull(reservedFileNames, "reservedFileNames");
        this.reservedFileNamesExtensions = reservedFileNamesExtensions;
        this.caseSensitive = caseSensitive;
        this.casePreserving = casePreserving;
        this.supportsDriveLetter = supportsDriveLetter;
        this.nameSeparator = nameSeparator;
        this.nameSeparatorOther = FilenameUtils.flipSeparator(nameSeparator);
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public char[] getIllegalFileNameChars() {
        char[] chars = new char[this.illegalFileNameChars.length];
        for (int i = 0; i < this.illegalFileNameChars.length; ++i) {
            chars[i] = (char)this.illegalFileNameChars[i];
        }
        return chars;
    }

    public int[] getIllegalFileNameCodePoints() {
        return (int[])this.illegalFileNameChars.clone();
    }

    public int getMaxFileNameLength() {
        return this.maxFileNameLength;
    }

    public int getMaxPathLength() {
        return this.maxPathLength;
    }

    public char getNameSeparator() {
        return this.nameSeparator;
    }

    public String[] getReservedFileNames() {
        return (String[])this.reservedFileNames.clone();
    }

    public boolean isCasePreserving() {
        return this.casePreserving;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    private boolean isIllegalFileNameChar(int c) {
        return Arrays.binarySearch(this.illegalFileNameChars, c) >= 0;
    }

    public boolean isLegalFileName(CharSequence candidate) {
        if (candidate == null || candidate.length() == 0 || candidate.length() > this.maxFileNameLength) {
            return false;
        }
        if (this.isReservedFileName(candidate)) {
            return false;
        }
        return candidate.chars().noneMatch(this::isIllegalFileNameChar);
    }

    public boolean isReservedFileName(CharSequence candidate) {
        CharSequence test = this.reservedFileNamesExtensions ? this.trimExtension(candidate) : candidate;
        return Arrays.binarySearch(this.reservedFileNames, test) >= 0;
    }

    public String normalizeSeparators(String path) {
        return FileSystem.replace(path, this.nameSeparatorOther, this.nameSeparator);
    }

    public boolean supportsDriveLetter() {
        return this.supportsDriveLetter;
    }

    public String toLegalFileName(String candidate, char replacement) {
        if (this.isIllegalFileNameChar(replacement)) {
            throw new IllegalArgumentException(String.format("The replacement character '%s' cannot be one of the %s illegal characters: %s", replacement == '\u0000' ? "\\0" : Character.valueOf(replacement), this.name(), Arrays.toString(this.illegalFileNameChars)));
        }
        String truncated = candidate.length() > this.maxFileNameLength ? candidate.substring(0, this.maxFileNameLength) : candidate;
        int[] array = truncated.chars().map(i -> this.isIllegalFileNameChar(i) ? replacement : i).toArray();
        return new String(array, 0, array.length);
    }

    CharSequence trimExtension(CharSequence cs) {
        int index = FileSystem.indexOf(cs, 46, 0);
        return index < 0 ? cs : cs.subSequence(0, index);
    }

    static {
        IS_OS_LINUX = FileSystem.getOsMatchesName("Linux");
        IS_OS_MAC = FileSystem.getOsMatchesName("Mac");
        IS_OS_WINDOWS = FileSystem.getOsMatchesName(OS_NAME_WINDOWS_PREFIX);
        CURRENT = FileSystem.current();
    }
}

