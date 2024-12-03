/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

public class MemorySizeParser {
    private static final long BYTE = 1L;
    private static final long KILOBYTE = 1024L;
    private static final long MEGABYTE = 0x100000L;
    private static final long GIGABYTE = 0x40000000L;
    private static final long TERABYTE = 0x10000000000L;

    public static long parse(String configuredMemorySize) throws IllegalArgumentException {
        MemorySize size = MemorySizeParser.parseIncludingUnit(configuredMemorySize);
        return size.calculateMemorySizeInBytes();
    }

    private static MemorySize parseIncludingUnit(String configuredMemorySize) throws IllegalArgumentException {
        MemorySize memorySize;
        if (configuredMemorySize == null || "".equals(configuredMemorySize)) {
            return new MemorySize("0", 1L);
        }
        char unit = configuredMemorySize.charAt(configuredMemorySize.length() - 1);
        switch (unit) {
            case 'K': 
            case 'k': {
                memorySize = MemorySizeParser.toMemorySize(configuredMemorySize, 1024L);
                break;
            }
            case 'M': 
            case 'm': {
                memorySize = MemorySizeParser.toMemorySize(configuredMemorySize, 0x100000L);
                break;
            }
            case 'G': 
            case 'g': {
                memorySize = MemorySizeParser.toMemorySize(configuredMemorySize, 0x40000000L);
                break;
            }
            case 'T': 
            case 't': {
                memorySize = MemorySizeParser.toMemorySize(configuredMemorySize, 0x10000000000L);
                break;
            }
            default: {
                try {
                    Integer.parseInt("" + unit);
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException("invalid format for memory size [" + configuredMemorySize + "]");
                }
                memorySize = new MemorySize(configuredMemorySize, 1L);
            }
        }
        return memorySize;
    }

    private static MemorySize toMemorySize(String configuredMemorySize, long unitMultiplier) {
        if (configuredMemorySize.length() < 2) {
            throw new IllegalArgumentException("invalid format for memory size [" + configuredMemorySize + "]");
        }
        return new MemorySize(configuredMemorySize.substring(0, configuredMemorySize.length() - 1), unitMultiplier);
    }

    private static final class MemorySize {
        private String configuredMemorySizeWithoutUnit;
        private long multiplicationFactor;

        private MemorySize(String configuredMemorySizeWithoutUnit, long multiplicationFactor) {
            this.configuredMemorySizeWithoutUnit = configuredMemorySizeWithoutUnit;
            this.multiplicationFactor = multiplicationFactor;
        }

        public long calculateMemorySizeInBytes() throws IllegalArgumentException {
            try {
                long memorySizeLong = Long.parseLong(this.configuredMemorySizeWithoutUnit);
                long result = memorySizeLong * this.multiplicationFactor;
                if (result < 0L) {
                    throw new IllegalArgumentException("memory size cannot be negative");
                }
                return result;
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid format for memory size");
            }
        }
    }
}

