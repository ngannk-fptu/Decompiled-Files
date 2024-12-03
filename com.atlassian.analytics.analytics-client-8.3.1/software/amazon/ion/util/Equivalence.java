/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonBool;
import software.amazon.ion.IonDecimal;
import software.amazon.ion.IonException;
import software.amazon.ion.IonFloat;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonLob;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSymbol;
import software.amazon.ion.IonText;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolToken;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Equivalence {
    private static final boolean PUBLIC_COMPARISON_API = false;

    private Equivalence() {
    }

    private static int compareAnnotations(SymbolToken[] ann1, SymbolToken[] ann2) {
        int len = ann1.length;
        int result = len - ann2.length;
        if (result == 0) {
            for (int i = 0; result == 0 && i < len; ++i) {
                result = Equivalence.compareSymbolTokens(ann1[i], ann2[i]);
            }
        }
        return result;
    }

    private static int compareSymbolTokens(SymbolToken tok1, SymbolToken tok2) {
        String text1 = tok1.getText();
        String text2 = tok2.getText();
        if (text1 == null || text2 == null) {
            int sid2;
            if (text1 != null) {
                return 1;
            }
            if (text2 != null) {
                return -1;
            }
            int sid1 = tok1.getSid();
            if (sid1 < (sid2 = tok2.getSid())) {
                return -1;
            }
            if (sid1 > sid2) {
                return 1;
            }
            return 0;
        }
        return text1.compareTo(text2);
    }

    private static final Map<Field, Field> convertToMultiSet(IonStruct struct, boolean strict) {
        HashMap<Field, Field> structMultiSet = new HashMap<Field, Field>();
        for (IonValue val : struct) {
            Field item = new Field(val, strict);
            Field curr = structMultiSet.put(item, item);
            if (curr != null) {
                item.occurrences = curr.occurrences;
            }
            item.occurrences++;
        }
        return structMultiSet;
    }

    private static int compareStructs(IonStruct s1, IonStruct s2, boolean strict) {
        int result = s1.size() - s2.size();
        if (result == 0) {
            Map<Field, Field> s1MultiSet = Equivalence.convertToMultiSet(s1, strict);
            for (IonValue val : s2) {
                Field field = new Field(val, strict);
                Field mappedValue = s1MultiSet.get(field);
                if (mappedValue == null || mappedValue.occurrences == 0) {
                    return -1;
                }
                mappedValue.occurrences--;
            }
        }
        return result;
    }

    private static int compareSequences(IonSequence s1, IonSequence s2, boolean strict) {
        int result;
        block1: {
            result = s1.size() - s2.size();
            if (result != 0) break block1;
            Iterator<IonValue> iter1 = s1.iterator();
            Iterator<IonValue> iter2 = s2.iterator();
            while (iter1.hasNext() && (result = Equivalence.ionCompareToImpl(iter1.next(), iter2.next(), strict)) == 0) {
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int compareLobContents(IonLob lob1, IonLob lob2) {
        int in2;
        int in1 = lob1.byteSize();
        int result = in1 - (in2 = lob2.byteSize());
        if (result == 0) {
            InputStream stream1 = lob1.newInputStream();
            InputStream stream2 = lob2.newInputStream();
            try {
                try {
                    try {
                        while (result == 0) {
                            in1 = stream1.read();
                            in2 = stream2.read();
                            if (in1 == -1 || in2 == -1) {
                                if (in1 != -1) {
                                    result = 1;
                                }
                                if (in2 != -1) {
                                    result = -1;
                                }
                                break;
                            }
                            result = in1 - in2;
                        }
                    }
                    finally {
                        stream1.close();
                    }
                }
                finally {
                    stream2.close();
                }
            }
            catch (IOException e) {
                throw new IonException(e);
            }
        }
        return result;
    }

    private static boolean ionEqualsImpl(IonValue v1, IonValue v2, boolean strict) {
        return Equivalence.ionCompareToImpl(v1, v2, strict) == 0;
    }

    private static int ionCompareToImpl(IonValue v1, IonValue v2, boolean strict) {
        IonType ty2;
        int result = 0;
        if (v1 == null || v2 == null) {
            if (v1 != null) {
                result = 1;
            }
            if (v2 != null) {
                result = -1;
            }
            return result;
        }
        IonType ty1 = v1.getType();
        result = ty1.compareTo(ty2 = v2.getType());
        if (result == 0) {
            boolean bo1 = v1.isNullValue();
            boolean bo2 = v2.isNullValue();
            if (bo1 || bo2) {
                if (!bo1) {
                    result = 1;
                }
                if (!bo2) {
                    result = -1;
                }
            } else {
                switch (ty1) {
                    case NULL: {
                        break;
                    }
                    case BOOL: {
                        if (((IonBool)v1).booleanValue()) {
                            result = ((IonBool)v2).booleanValue() ? 0 : 1;
                            break;
                        }
                        result = ((IonBool)v2).booleanValue() ? -1 : 0;
                        break;
                    }
                    case INT: {
                        result = ((IonInt)v1).bigIntegerValue().compareTo(((IonInt)v2).bigIntegerValue());
                        break;
                    }
                    case FLOAT: {
                        result = Double.compare(((IonFloat)v1).doubleValue(), ((IonFloat)v2).doubleValue());
                        break;
                    }
                    case DECIMAL: {
                        result = Decimal.equals(((IonDecimal)v1).decimalValue(), ((IonDecimal)v2).decimalValue()) ? 0 : 1;
                        break;
                    }
                    case TIMESTAMP: {
                        if (strict) {
                            result = ((IonTimestamp)v1).timestampValue().equals(((IonTimestamp)v2).timestampValue()) ? 0 : 1;
                            break;
                        }
                        result = ((IonTimestamp)v1).timestampValue().compareTo(((IonTimestamp)v2).timestampValue());
                        break;
                    }
                    case STRING: {
                        result = ((IonText)v1).stringValue().compareTo(((IonText)v2).stringValue());
                        break;
                    }
                    case SYMBOL: {
                        result = Equivalence.compareSymbolTokens(((IonSymbol)v1).symbolValue(), ((IonSymbol)v2).symbolValue());
                        break;
                    }
                    case BLOB: 
                    case CLOB: {
                        result = Equivalence.compareLobContents((IonLob)v1, (IonLob)v2);
                        break;
                    }
                    case STRUCT: {
                        result = Equivalence.compareStructs((IonStruct)v1, (IonStruct)v2, strict);
                        break;
                    }
                    case LIST: 
                    case SEXP: 
                    case DATAGRAM: {
                        result = Equivalence.compareSequences((IonSequence)v1, (IonSequence)v2, strict);
                    }
                }
            }
        }
        if (result == 0 && strict) {
            result = Equivalence.compareAnnotations(v1.getTypeAnnotationSymbols(), v2.getTypeAnnotationSymbols());
        }
        return result;
    }

    public static boolean ionEquals(IonValue v1, IonValue v2) {
        return Equivalence.ionEqualsImpl(v1, v2, true);
    }

    public static boolean ionEqualsByContent(IonValue v1, IonValue v2) {
        return Equivalence.ionEqualsImpl(v1, v2, false);
    }

    static class Field {
        private final String name;
        private final IonValue value;
        private final boolean strict;
        private int occurrences;

        Field(IonValue value, boolean strict) {
            SymbolToken tok = value.getFieldNameSymbol();
            String name = tok.getText();
            if (name == null) {
                name = " -- UNKNOWN SYMBOL TEXT -- $" + tok.getSid();
            }
            this.name = name;
            this.value = value;
            this.strict = strict;
            this.occurrences = 0;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object other) {
            Field sOther = (Field)other;
            return this.name.equals(sOther.name) && Equivalence.ionEqualsImpl(this.value, ((Field)other).value, this.strict);
        }
    }
}

