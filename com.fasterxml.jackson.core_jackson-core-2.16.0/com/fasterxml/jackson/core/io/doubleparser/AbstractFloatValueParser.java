/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.AbstractNumberParser;

abstract class AbstractFloatValueParser
extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 0x7FFFFFFB;
    static final long MINIMAL_NINETEEN_DIGIT_INTEGER = 1000000000000000000L;
    static final int MAX_EXPONENT_NUMBER = 1024;

    AbstractFloatValueParser() {
    }
}

