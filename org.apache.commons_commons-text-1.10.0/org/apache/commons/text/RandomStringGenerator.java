/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.Validate
 */
package org.apache.commons.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.TextRandomProvider;

public final class RandomStringGenerator {
    private final int minimumCodePoint;
    private final int maximumCodePoint;
    private final Set<CharacterPredicate> inclusivePredicates;
    private final TextRandomProvider random;
    private final List<Character> characterList;

    private RandomStringGenerator(int minimumCodePoint, int maximumCodePoint, Set<CharacterPredicate> inclusivePredicates, TextRandomProvider random, List<Character> characterList) {
        this.minimumCodePoint = minimumCodePoint;
        this.maximumCodePoint = maximumCodePoint;
        this.inclusivePredicates = inclusivePredicates;
        this.random = random;
        this.characterList = characterList;
    }

    public String generate(int length) {
        if (length == 0) {
            return "";
        }
        Validate.isTrue((length > 0 ? 1 : 0) != 0, (String)"Length %d is smaller than zero.", (long)length);
        StringBuilder builder = new StringBuilder(length);
        long remaining = length;
        block3: do {
            int codePoint = this.characterList != null && !this.characterList.isEmpty() ? this.generateRandomNumber(this.characterList) : this.generateRandomNumber(this.minimumCodePoint, this.maximumCodePoint);
            switch (Character.getType(codePoint)) {
                case 0: 
                case 18: 
                case 19: {
                    break;
                }
                default: {
                    if (this.inclusivePredicates != null) {
                        boolean matchedFilter = false;
                        for (CharacterPredicate predicate : this.inclusivePredicates) {
                            if (!predicate.test(codePoint)) continue;
                            matchedFilter = true;
                            break;
                        }
                        if (!matchedFilter) continue block3;
                    }
                    builder.appendCodePoint(codePoint);
                    --remaining;
                }
            }
        } while (remaining != 0L);
        return builder.toString();
    }

    public String generate(int minLengthInclusive, int maxLengthInclusive) {
        Validate.isTrue((minLengthInclusive >= 0 ? 1 : 0) != 0, (String)"Minimum length %d is smaller than zero.", (long)minLengthInclusive);
        Validate.isTrue((minLengthInclusive <= maxLengthInclusive ? 1 : 0) != 0, (String)"Maximum length %d is smaller than minimum length %d.", (Object[])new Object[]{maxLengthInclusive, minLengthInclusive});
        return this.generate(this.generateRandomNumber(minLengthInclusive, maxLengthInclusive));
    }

    private int generateRandomNumber(int minInclusive, int maxInclusive) {
        if (this.random != null) {
            return this.random.nextInt(maxInclusive - minInclusive + 1) + minInclusive;
        }
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    private int generateRandomNumber(List<Character> characterList) {
        int listSize = characterList.size();
        if (this.random != null) {
            return String.valueOf(characterList.get(this.random.nextInt(listSize))).codePointAt(0);
        }
        return String.valueOf(characterList.get(ThreadLocalRandom.current().nextInt(0, listSize))).codePointAt(0);
    }

    public static class Builder
    implements org.apache.commons.text.Builder<RandomStringGenerator> {
        public static final int DEFAULT_MAXIMUM_CODE_POINT = 0x10FFFF;
        public static final int DEFAULT_LENGTH = 0;
        public static final int DEFAULT_MINIMUM_CODE_POINT = 0;
        private int minimumCodePoint = 0;
        private int maximumCodePoint = 0x10FFFF;
        private Set<CharacterPredicate> inclusivePredicates;
        private TextRandomProvider random;
        private List<Character> characterList;

        @Override
        public RandomStringGenerator build() {
            return new RandomStringGenerator(this.minimumCodePoint, this.maximumCodePoint, this.inclusivePredicates, this.random, this.characterList);
        }

        public Builder filteredBy(CharacterPredicate ... predicates) {
            if (ArrayUtils.isEmpty((Object[])predicates)) {
                this.inclusivePredicates = null;
                return this;
            }
            if (this.inclusivePredicates == null) {
                this.inclusivePredicates = new HashSet<CharacterPredicate>();
            } else {
                this.inclusivePredicates.clear();
            }
            Collections.addAll(this.inclusivePredicates, predicates);
            return this;
        }

        public Builder selectFrom(char ... chars) {
            this.characterList = new ArrayList<Character>();
            for (char c : chars) {
                this.characterList.add(Character.valueOf(c));
            }
            return this;
        }

        public Builder usingRandom(TextRandomProvider random) {
            this.random = random;
            return this;
        }

        public Builder withinRange(char[] ... pairs) {
            this.characterList = new ArrayList<Character>();
            for (char[] pair : pairs) {
                Validate.isTrue((pair.length == 2 ? 1 : 0) != 0, (String)"Each pair must contain minimum and maximum code point", (Object[])new Object[0]);
                int minimumCodePoint = pair[0];
                char maximumCodePoint = pair[1];
                Validate.isTrue((minimumCodePoint <= maximumCodePoint ? 1 : 0) != 0, (String)"Minimum code point %d is larger than maximum code point %d", (Object[])new Object[]{minimumCodePoint, (int)maximumCodePoint});
                for (int index = minimumCodePoint; index <= maximumCodePoint; ++index) {
                    this.characterList.add(Character.valueOf((char)index));
                }
            }
            return this;
        }

        public Builder withinRange(int minimumCodePoint, int maximumCodePoint) {
            Validate.isTrue((minimumCodePoint <= maximumCodePoint ? 1 : 0) != 0, (String)"Minimum code point %d is larger than maximum code point %d", (Object[])new Object[]{minimumCodePoint, maximumCodePoint});
            Validate.isTrue((minimumCodePoint >= 0 ? 1 : 0) != 0, (String)"Minimum code point %d is negative", (long)minimumCodePoint);
            Validate.isTrue((maximumCodePoint <= 0x10FFFF ? 1 : 0) != 0, (String)"Value %d is larger than Character.MAX_CODE_POINT.", (long)maximumCodePoint);
            this.minimumCodePoint = minimumCodePoint;
            this.maximumCodePoint = maximumCodePoint;
            return this;
        }
    }
}

