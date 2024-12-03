/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.units;

import com.ibm.icu.impl.units.ConversionRates;
import com.ibm.icu.impl.units.SingleUnitImpl;
import com.ibm.icu.impl.units.UnitsData;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.CharsTrieBuilder;
import com.ibm.icu.util.ICUCloneNotSupportedException;
import com.ibm.icu.util.MeasureUnit;
import com.ibm.icu.util.StringTrieBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MeasureUnitImpl {
    private String identifier = null;
    private MeasureUnit.Complexity complexity = MeasureUnit.Complexity.SINGLE;
    private final ArrayList<SingleUnitImpl> singleUnits = new ArrayList();

    public MeasureUnitImpl() {
    }

    public MeasureUnitImpl(SingleUnitImpl singleUnit) {
        this();
        this.appendSingleUnit(singleUnit);
    }

    public static MeasureUnitImpl forIdentifier(String identifier) {
        return UnitsParser.parseForIdentifier(identifier);
    }

    public static MeasureUnitImpl forCurrencyCode(String currencyCode) {
        MeasureUnitImpl result = new MeasureUnitImpl();
        result.identifier = currencyCode;
        return result;
    }

    public MeasureUnitImpl copy() {
        MeasureUnitImpl result = new MeasureUnitImpl();
        result.complexity = this.complexity;
        result.identifier = this.identifier;
        for (SingleUnitImpl singleUnit : this.singleUnits) {
            result.singleUnits.add(singleUnit.copy());
        }
        return result;
    }

    public MeasureUnitImpl copyAndSimplify() {
        MeasureUnitImpl result = new MeasureUnitImpl();
        for (SingleUnitImpl singleUnit : this.getSingleUnits()) {
            boolean unitExist = false;
            for (SingleUnitImpl resultSingleUnit : result.getSingleUnits()) {
                if (resultSingleUnit.getSimpleUnitID().compareTo(singleUnit.getSimpleUnitID()) != 0 || resultSingleUnit.getPrefix().getIdentifier().compareTo(singleUnit.getPrefix().getIdentifier()) != 0) continue;
                unitExist = true;
                resultSingleUnit.setDimensionality(resultSingleUnit.getDimensionality() + singleUnit.getDimensionality());
                break;
            }
            if (unitExist) continue;
            result.appendSingleUnit(singleUnit);
        }
        return result;
    }

    public ArrayList<SingleUnitImpl> getSingleUnits() {
        return this.singleUnits;
    }

    public void takeReciprocal() {
        this.identifier = null;
        for (SingleUnitImpl singleUnit : this.singleUnits) {
            singleUnit.setDimensionality(singleUnit.getDimensionality() * -1);
        }
    }

    public ArrayList<MeasureUnitImplWithIndex> extractIndividualUnitsWithIndices() {
        ArrayList<MeasureUnitImplWithIndex> result = new ArrayList<MeasureUnitImplWithIndex>();
        if (this.getComplexity() == MeasureUnit.Complexity.MIXED) {
            int i = 0;
            for (SingleUnitImpl singleUnit : this.getSingleUnits()) {
                result.add(new MeasureUnitImplWithIndex(i++, new MeasureUnitImpl(singleUnit)));
            }
            return result;
        }
        result.add(new MeasureUnitImplWithIndex(0, this.copy()));
        return result;
    }

    public void applyDimensionality(int dimensionality) {
        for (SingleUnitImpl singleUnit : this.singleUnits) {
            singleUnit.setDimensionality(singleUnit.getDimensionality() * dimensionality);
        }
    }

    public boolean appendSingleUnit(SingleUnitImpl singleUnit) {
        this.identifier = null;
        if (singleUnit == null) {
            return false;
        }
        SingleUnitImpl oldUnit = null;
        for (SingleUnitImpl candidate : this.singleUnits) {
            if (!candidate.isCompatibleWith(singleUnit)) continue;
            oldUnit = candidate;
            break;
        }
        if (oldUnit != null) {
            oldUnit.setDimensionality(oldUnit.getDimensionality() + singleUnit.getDimensionality());
            return false;
        }
        this.singleUnits.add(singleUnit.copy());
        if (this.singleUnits.size() > 1 && this.complexity == MeasureUnit.Complexity.SINGLE) {
            this.setComplexity(MeasureUnit.Complexity.COMPOUND);
        }
        return true;
    }

    public MeasureUnit build() {
        return MeasureUnit.fromMeasureUnitImpl(this);
    }

    public SingleUnitImpl getSingleUnitImpl() {
        if (this.singleUnits.size() == 0) {
            return new SingleUnitImpl();
        }
        if (this.singleUnits.size() == 1) {
            return this.singleUnits.get(0).copy();
        }
        throw new UnsupportedOperationException();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public MeasureUnit.Complexity getComplexity() {
        return this.complexity;
    }

    public void setComplexity(MeasureUnit.Complexity complexity) {
        this.complexity = complexity;
    }

    public void serialize() {
        if (this.getSingleUnits().size() == 0) {
            return;
        }
        if (this.complexity == MeasureUnit.Complexity.COMPOUND) {
            Collections.sort(this.getSingleUnits(), new SingleUnitComparator());
        }
        StringBuilder result = new StringBuilder();
        boolean beforePer = true;
        boolean firstTimeNegativeDimension = false;
        for (SingleUnitImpl singleUnit : this.getSingleUnits()) {
            if (beforePer && singleUnit.getDimensionality() < 0) {
                beforePer = false;
                firstTimeNegativeDimension = true;
            } else if (singleUnit.getDimensionality() < 0) {
                firstTimeNegativeDimension = false;
            }
            if (this.getComplexity() == MeasureUnit.Complexity.MIXED) {
                if (result.length() != 0) {
                    result.append("-and-");
                }
            } else if (firstTimeNegativeDimension) {
                if (result.length() == 0) {
                    result.append("per-");
                } else {
                    result.append("-per-");
                }
            } else if (result.length() != 0) {
                result.append("-");
            }
            result.append(singleUnit.getNeutralIdentifier());
        }
        this.identifier = result.toString();
    }

    public String toString() {
        return "MeasureUnitImpl [" + this.build().getIdentifier() + "]";
    }

    static class SingleUnitComparator
    implements Comparator<SingleUnitImpl> {
        SingleUnitComparator() {
        }

        @Override
        public int compare(SingleUnitImpl o1, SingleUnitImpl o2) {
            return o1.compareTo(o2);
        }
    }

    static class MeasureUnitImplWithIndexComparator
    implements Comparator<MeasureUnitImplWithIndex> {
        private MeasureUnitImplComparator measureUnitImplComparator;

        public MeasureUnitImplWithIndexComparator(ConversionRates conversionRates) {
            this.measureUnitImplComparator = new MeasureUnitImplComparator(conversionRates);
        }

        @Override
        public int compare(MeasureUnitImplWithIndex o1, MeasureUnitImplWithIndex o2) {
            return this.measureUnitImplComparator.compare(o1.unitImpl, o2.unitImpl);
        }
    }

    static class MeasureUnitImplComparator
    implements Comparator<MeasureUnitImpl> {
        private final ConversionRates conversionRates;

        public MeasureUnitImplComparator(ConversionRates conversionRates) {
            this.conversionRates = conversionRates;
        }

        @Override
        public int compare(MeasureUnitImpl o1, MeasureUnitImpl o2) {
            BigDecimal factor1 = this.conversionRates.getFactorToBase(o1).getConversionRate();
            BigDecimal factor2 = this.conversionRates.getFactorToBase(o2).getConversionRate();
            return factor1.compareTo(factor2);
        }
    }

    public static class UnitsParser {
        private static volatile CharsTrie savedTrie = null;
        private final CharsTrie trie;
        private final String fSource;
        private int fIndex = 0;
        private boolean fAfterPer = false;
        private boolean fSawAnd = false;
        private static MeasureUnit.MeasurePrefix[] measurePrefixValues = MeasureUnit.MeasurePrefix.values();

        private UnitsParser(String identifier) {
            this.fSource = identifier;
            try {
                this.trie = savedTrie.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new ICUCloneNotSupportedException();
            }
        }

        public static MeasureUnitImpl parseForIdentifier(String identifier) {
            if (identifier == null || identifier.isEmpty()) {
                return null;
            }
            UnitsParser parser = new UnitsParser(identifier);
            return parser.parse();
        }

        private static MeasureUnit.MeasurePrefix getPrefixFromTrieIndex(int trieIndex) {
            return measurePrefixValues[trieIndex - 64];
        }

        private static int getTrieIndexForPrefix(MeasureUnit.MeasurePrefix prefix) {
            return prefix.ordinal() + 64;
        }

        private MeasureUnitImpl parse() {
            MeasureUnitImpl result = new MeasureUnitImpl();
            if (this.fSource.isEmpty()) {
                return null;
            }
            while (this.hasNext()) {
                MeasureUnit.Complexity complexity;
                this.fSawAnd = false;
                SingleUnitImpl singleUnit = this.nextSingleUnit();
                boolean added = result.appendSingleUnit(singleUnit);
                if (this.fSawAnd && !added) {
                    throw new IllegalArgumentException("Two similar units are not allowed in a mixed unit.");
                }
                if (result.singleUnits.size() < 2) continue;
                MeasureUnit.Complexity complexity2 = complexity = this.fSawAnd ? MeasureUnit.Complexity.MIXED : MeasureUnit.Complexity.COMPOUND;
                if (result.getSingleUnits().size() == 2) {
                    assert (result.getComplexity() == MeasureUnit.Complexity.COMPOUND);
                    result.setComplexity(complexity);
                    continue;
                }
                if (result.getComplexity() == complexity) continue;
                throw new IllegalArgumentException("Can't have mixed compound units");
            }
            return result;
        }

        private SingleUnitImpl nextSingleUnit() {
            SingleUnitImpl result = new SingleUnitImpl();
            int state = 0;
            boolean atStart = this.fIndex == 0;
            Token token = this.nextToken();
            if (atStart) {
                if (token.getType() == Token.Type.TYPE_INITIAL_COMPOUND_PART) {
                    assert (token.getInitialCompoundPart() == InitialCompoundPart.INITIAL_COMPOUND_PART_PER);
                    this.fAfterPer = true;
                    result.setDimensionality(-1);
                    token = this.nextToken();
                }
            } else {
                if (token.getType() != Token.Type.TYPE_COMPOUND_PART) {
                    throw new IllegalArgumentException("token type must be TYPE_COMPOUND_PART");
                }
                CompoundPart compoundPart = CompoundPart.getCompoundPartFromTrieIndex(token.getMatch());
                switch (compoundPart) {
                    case PER: {
                        if (this.fSawAnd) {
                            throw new IllegalArgumentException("Mixed compound units not yet supported");
                        }
                        this.fAfterPer = true;
                        result.setDimensionality(-1);
                        break;
                    }
                    case TIMES: {
                        if (!this.fAfterPer) break;
                        result.setDimensionality(-1);
                        break;
                    }
                    case AND: {
                        if (this.fAfterPer) {
                            throw new IllegalArgumentException("Can't start with \"-and-\", and mixed compound units");
                        }
                        this.fSawAnd = true;
                    }
                }
                token = this.nextToken();
            }
            while (true) {
                switch (token.getType()) {
                    case TYPE_POWER_PART: {
                        if (state > 0) {
                            throw new IllegalArgumentException();
                        }
                        result.setDimensionality(result.getDimensionality() * token.getPower());
                        state = 1;
                        break;
                    }
                    case TYPE_PREFIX: {
                        if (state > 1) {
                            throw new IllegalArgumentException();
                        }
                        result.setPrefix(token.getPrefix());
                        state = 2;
                        break;
                    }
                    case TYPE_SIMPLE_UNIT: {
                        result.setSimpleUnit(token.getSimpleUnitIndex(), UnitsData.getSimpleUnits());
                        return result;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
                if (!this.hasNext()) {
                    throw new IllegalArgumentException("We ran out of tokens before finding a complete single unit.");
                }
                token = this.nextToken();
            }
        }

        private boolean hasNext() {
            return this.fIndex < this.fSource.length();
        }

        private Token nextToken() {
            BytesTrie.Result result;
            this.trie.reset();
            int match = -1;
            int previ = -1;
            while (this.fIndex < this.fSource.length() && (result = this.trie.next(this.fSource.charAt(this.fIndex++))) != BytesTrie.Result.NO_MATCH) {
                if (result == BytesTrie.Result.NO_VALUE) continue;
                match = this.trie.getValue();
                previ = this.fIndex;
                if (result == BytesTrie.Result.FINAL_VALUE) break;
                if (result == BytesTrie.Result.INTERMEDIATE_VALUE) continue;
                throw new IllegalArgumentException("result must has an intermediate value");
            }
            if (match < 0) {
                throw new IllegalArgumentException("Encountered unknown token starting at index " + previ);
            }
            this.fIndex = previ;
            return new Token(match);
        }

        static {
            CharsTrieBuilder trieBuilder = new CharsTrieBuilder();
            for (MeasureUnit.MeasurePrefix unitPrefix : measurePrefixValues) {
                trieBuilder.add(unitPrefix.getIdentifier(), UnitsParser.getTrieIndexForPrefix(unitPrefix));
            }
            trieBuilder.add("-per-", CompoundPart.PER.getTrieIndex());
            trieBuilder.add("-", CompoundPart.TIMES.getTrieIndex());
            trieBuilder.add("-and-", CompoundPart.AND.getTrieIndex());
            trieBuilder.add("per-", InitialCompoundPart.INITIAL_COMPOUND_PART_PER.getTrieIndex());
            trieBuilder.add("square-", PowerPart.P2.getTrieIndex());
            trieBuilder.add("cubic-", PowerPart.P3.getTrieIndex());
            trieBuilder.add("pow2-", PowerPart.P2.getTrieIndex());
            trieBuilder.add("pow3-", PowerPart.P3.getTrieIndex());
            trieBuilder.add("pow4-", PowerPart.P4.getTrieIndex());
            trieBuilder.add("pow5-", PowerPart.P5.getTrieIndex());
            trieBuilder.add("pow6-", PowerPart.P6.getTrieIndex());
            trieBuilder.add("pow7-", PowerPart.P7.getTrieIndex());
            trieBuilder.add("pow8-", PowerPart.P8.getTrieIndex());
            trieBuilder.add("pow9-", PowerPart.P9.getTrieIndex());
            trieBuilder.add("pow10-", PowerPart.P10.getTrieIndex());
            trieBuilder.add("pow11-", PowerPart.P11.getTrieIndex());
            trieBuilder.add("pow12-", PowerPart.P12.getTrieIndex());
            trieBuilder.add("pow13-", PowerPart.P13.getTrieIndex());
            trieBuilder.add("pow14-", PowerPart.P14.getTrieIndex());
            trieBuilder.add("pow15-", PowerPart.P15.getTrieIndex());
            String[] simpleUnits = UnitsData.getSimpleUnits();
            for (int i = 0; i < simpleUnits.length; ++i) {
                trieBuilder.add(simpleUnits[i], i + 512);
            }
            savedTrie = trieBuilder.build(StringTrieBuilder.Option.FAST);
        }

        static class Token {
            private final int fMatch;
            private final Type type;

            public Token(int fMatch) {
                this.fMatch = fMatch;
                this.type = this.calculateType(fMatch);
            }

            public Type getType() {
                return this.type;
            }

            public MeasureUnit.MeasurePrefix getPrefix() {
                assert (this.type == Type.TYPE_PREFIX);
                return UnitsParser.getPrefixFromTrieIndex(this.fMatch);
            }

            public int getMatch() {
                assert (this.getType() == Type.TYPE_COMPOUND_PART);
                return this.fMatch;
            }

            public InitialCompoundPart getInitialCompoundPart() {
                assert (this.type == Type.TYPE_INITIAL_COMPOUND_PART && this.fMatch == InitialCompoundPart.INITIAL_COMPOUND_PART_PER.getTrieIndex());
                return InitialCompoundPart.getInitialCompoundPartFromTrieIndex(this.fMatch);
            }

            public int getPower() {
                assert (this.type == Type.TYPE_POWER_PART);
                return PowerPart.getPowerFromTrieIndex(this.fMatch);
            }

            public int getSimpleUnitIndex() {
                assert (this.type == Type.TYPE_SIMPLE_UNIT);
                return this.fMatch - 512;
            }

            private Type calculateType(int fMatch) {
                if (fMatch <= 0) {
                    throw new AssertionError((Object)"fMatch must have a positive value");
                }
                if (fMatch < 128) {
                    return Type.TYPE_PREFIX;
                }
                if (fMatch < 192) {
                    return Type.TYPE_COMPOUND_PART;
                }
                if (fMatch < 256) {
                    return Type.TYPE_INITIAL_COMPOUND_PART;
                }
                if (fMatch < 512) {
                    return Type.TYPE_POWER_PART;
                }
                return Type.TYPE_SIMPLE_UNIT;
            }

            static enum Type {
                TYPE_UNDEFINED,
                TYPE_PREFIX,
                TYPE_COMPOUND_PART,
                TYPE_INITIAL_COMPOUND_PART,
                TYPE_POWER_PART,
                TYPE_SIMPLE_UNIT;

            }
        }
    }

    public static class MeasureUnitImplWithIndex {
        int index;
        MeasureUnitImpl unitImpl;

        MeasureUnitImplWithIndex(int index, MeasureUnitImpl unitImpl) {
            this.index = index;
            this.unitImpl = unitImpl;
        }
    }

    public static enum InitialCompoundPart {
        INITIAL_COMPOUND_PART_PER(0);

        private final int index;

        private InitialCompoundPart(int powerIndex) {
            this.index = powerIndex;
        }

        public static InitialCompoundPart getInitialCompoundPartFromTrieIndex(int trieIndex) {
            int index = trieIndex - 192;
            if (index == 0) {
                return INITIAL_COMPOUND_PART_PER;
            }
            throw new IllegalArgumentException("Incorrect trieIndex");
        }

        public int getTrieIndex() {
            return this.index + 192;
        }

        public int getValue() {
            return this.index;
        }
    }

    public static enum PowerPart {
        P2(2),
        P3(3),
        P4(4),
        P5(5),
        P6(6),
        P7(7),
        P8(8),
        P9(9),
        P10(10),
        P11(11),
        P12(12),
        P13(13),
        P14(14),
        P15(15);

        private final int power;

        private PowerPart(int power) {
            this.power = power;
        }

        public static int getPowerFromTrieIndex(int trieIndex) {
            return trieIndex - 256;
        }

        public int getTrieIndex() {
            return this.power + 256;
        }

        public int getValue() {
            return this.power;
        }
    }

    public static enum CompoundPart {
        PER(0),
        TIMES(1),
        AND(2);

        private final int index;

        private CompoundPart(int index) {
            this.index = index;
        }

        public static CompoundPart getCompoundPartFromTrieIndex(int trieIndex) {
            int index = trieIndex - 128;
            switch (index) {
                case 0: {
                    return PER;
                }
                case 1: {
                    return TIMES;
                }
                case 2: {
                    return AND;
                }
            }
            throw new AssertionError((Object)"CompoundPart index must be 0, 1 or 2");
        }

        public int getTrieIndex() {
            return this.index + 128;
        }

        public int getValue() {
            return this.index;
        }
    }
}

