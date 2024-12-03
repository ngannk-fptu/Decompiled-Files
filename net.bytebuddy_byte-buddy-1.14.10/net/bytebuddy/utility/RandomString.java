/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import net.bytebuddy.utility.nullability.MaybeNull;

public class RandomString {
    public static final int DEFAULT_LENGTH = 8;
    private static final char[] SYMBOL;
    private static final int KEY_BITS;
    private final Random random;
    private final int length;

    public RandomString() {
        this(8);
    }

    public RandomString(int length) {
        this(length, new Random());
    }

    public RandomString(int length, Random random) {
        if (length <= 0) {
            throw new IllegalArgumentException("A random string's length cannot be zero or negative");
        }
        this.length = length;
        this.random = random;
    }

    public static String make() {
        return RandomString.make(8);
    }

    public static String make(int length) {
        return new RandomString(length).nextString();
    }

    public static String hashOf(@MaybeNull Object value) {
        return RandomString.hashOf(value == null ? 0 : value.getClass().hashCode() ^ System.identityHashCode(value));
    }

    public static String hashOf(int value) {
        char[] buffer = new char[32 / KEY_BITS + (32 % KEY_BITS == 0 ? 0 : 1)];
        for (int index = 0; index < buffer.length; ++index) {
            buffer[index] = SYMBOL[value >>> index * KEY_BITS & -1 >>> 32 - KEY_BITS];
        }
        return new String(buffer);
    }

    @SuppressFBWarnings(value={"DMI_RANDOM_USED_ONLY_ONCE"}, justification="Random value is used on each invocation.")
    public String nextString() {
        char[] buffer = new char[this.length];
        for (int index = 0; index < this.length; ++index) {
            buffer[index] = SYMBOL[this.random.nextInt(SYMBOL.length)];
        }
        return new String(buffer);
    }

    static {
        char character;
        StringBuilder symbol = new StringBuilder();
        for (character = '0'; character <= '9'; character = (char)(character + '\u0001')) {
            symbol.append(character);
        }
        for (character = 'a'; character <= 'z'; character = (char)(character + '\u0001')) {
            symbol.append(character);
        }
        for (character = 'A'; character <= 'Z'; character = (char)(character + '\u0001')) {
            symbol.append(character);
        }
        SYMBOL = symbol.toString().toCharArray();
        int bits = 32 - Integer.numberOfLeadingZeros(SYMBOL.length);
        KEY_BITS = bits - (Integer.bitCount(SYMBOL.length) == bits ? 0 : 1);
    }
}

