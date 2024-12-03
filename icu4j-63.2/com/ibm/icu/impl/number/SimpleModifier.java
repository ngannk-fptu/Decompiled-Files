/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number;

import com.ibm.icu.impl.SimpleFormatterImpl;
import com.ibm.icu.impl.number.Modifier;
import com.ibm.icu.impl.number.NumberStringBuilder;
import com.ibm.icu.impl.number.range.PrefixInfixSuffixLengthHelper;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ICUException;

public class SimpleModifier
implements Modifier {
    private final String compiledPattern;
    private final NumberFormat.Field field;
    private final boolean strong;
    private final int prefixLength;
    private final int suffixOffset;
    private final int suffixLength;
    private final Modifier.Parameters parameters;
    private static final int ARG_NUM_LIMIT = 256;

    public SimpleModifier(String compiledPattern, NumberFormat.Field field, boolean strong) {
        this(compiledPattern, field, strong, null);
    }

    public SimpleModifier(String compiledPattern, NumberFormat.Field field, boolean strong, Modifier.Parameters parameters) {
        assert (compiledPattern != null);
        this.compiledPattern = compiledPattern;
        this.field = field;
        this.strong = strong;
        this.parameters = parameters;
        int argLimit = SimpleFormatterImpl.getArgumentLimit(compiledPattern);
        if (argLimit == 0) {
            this.prefixLength = compiledPattern.charAt(1) - 256;
            assert (2 + this.prefixLength == compiledPattern.length());
            this.suffixOffset = -1;
            this.suffixLength = 0;
        } else {
            assert (argLimit == 1);
            if (compiledPattern.charAt(1) != '\u0000') {
                this.prefixLength = compiledPattern.charAt(1) - 256;
                this.suffixOffset = 3 + this.prefixLength;
            } else {
                this.prefixLength = 0;
                this.suffixOffset = 2;
            }
            this.suffixLength = 3 + this.prefixLength < compiledPattern.length() ? compiledPattern.charAt(this.suffixOffset) - 256 : 0;
        }
    }

    @Override
    public int apply(NumberStringBuilder output, int leftIndex, int rightIndex) {
        return this.formatAsPrefixSuffix(output, leftIndex, rightIndex, this.field);
    }

    @Override
    public int getPrefixLength() {
        return this.prefixLength;
    }

    @Override
    public int getCodePointCount() {
        int count = 0;
        if (this.prefixLength > 0) {
            count += Character.codePointCount(this.compiledPattern, 2, 2 + this.prefixLength);
        }
        if (this.suffixLength > 0) {
            count += Character.codePointCount(this.compiledPattern, 1 + this.suffixOffset, 1 + this.suffixOffset + this.suffixLength);
        }
        return count;
    }

    @Override
    public boolean isStrong() {
        return this.strong;
    }

    @Override
    public boolean containsField(NumberFormat.Field field) {
        assert (false);
        return false;
    }

    @Override
    public Modifier.Parameters getParameters() {
        return this.parameters;
    }

    @Override
    public boolean semanticallyEquivalent(Modifier other) {
        if (!(other instanceof SimpleModifier)) {
            return false;
        }
        SimpleModifier _other = (SimpleModifier)other;
        if (this.parameters != null && _other.parameters != null && this.parameters.obj == _other.parameters.obj) {
            return true;
        }
        return this.compiledPattern.equals(_other.compiledPattern) && this.field == _other.field && this.strong == _other.strong;
    }

    public int formatAsPrefixSuffix(NumberStringBuilder result, int startIndex, int endIndex, NumberFormat.Field field) {
        if (this.suffixOffset == -1) {
            return result.splice(startIndex, endIndex, this.compiledPattern, 2, 2 + this.prefixLength, field);
        }
        if (this.prefixLength > 0) {
            result.insert(startIndex, this.compiledPattern, 2, 2 + this.prefixLength, field);
        }
        if (this.suffixLength > 0) {
            result.insert(endIndex + this.prefixLength, this.compiledPattern, 1 + this.suffixOffset, 1 + this.suffixOffset + this.suffixLength, field);
        }
        return this.prefixLength + this.suffixLength;
    }

    public static void formatTwoArgPattern(String compiledPattern, NumberStringBuilder result, int index, PrefixInfixSuffixLengthHelper h, NumberFormat.Field field) {
        int suffixLength;
        int argLimit = SimpleFormatterImpl.getArgumentLimit(compiledPattern);
        if (argLimit != 2) {
            throw new ICUException();
        }
        int offset = 1;
        int length = 0;
        int prefixLength = compiledPattern.charAt(offset);
        ++offset;
        if (prefixLength < 256) {
            prefixLength = 0;
        } else {
            result.insert(index + length, compiledPattern, offset, offset + (prefixLength -= 256), field);
            offset += prefixLength;
            length += prefixLength;
            ++offset;
        }
        int infixLength = compiledPattern.charAt(offset);
        ++offset;
        if (infixLength < 256) {
            infixLength = 0;
        } else {
            result.insert(index + length, compiledPattern, offset, offset + (infixLength -= 256), field);
            offset += infixLength;
            length += infixLength;
            ++offset;
        }
        if (offset == compiledPattern.length()) {
            suffixLength = 0;
        } else {
            suffixLength = compiledPattern.charAt(offset) - 256;
            result.insert(index + length, compiledPattern, ++offset, offset + suffixLength, field);
            length += suffixLength;
        }
        h.lengthPrefix = prefixLength;
        h.lengthInfix = infixLength;
        h.lengthSuffix = suffixLength;
    }
}

