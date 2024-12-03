/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.content.encoding;

import java.util.Optional;
import java.util.stream.IntStream;
import org.checkerframework.checker.nullness.qual.NonNull;

final class SupplementaryCharacterUtils {
    private static final int SUPPLEMENTARY_CHARACTER_LENGTH = 2;

    SupplementaryCharacterUtils() {
    }

    static Optional<String> getFirstSupplementaryCharacter(@NonNull String content) {
        return IntStream.range(0, content.length() - 2 + 1).filter(loc -> Character.isSurrogatePair(content.charAt(loc), content.charAt(loc + 1))).mapToObj(loc -> content.substring(loc, loc + 2)).findFirst();
    }
}

