/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.math.BigDecimal;
import java.math.BigInteger;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonBlob;
import software.amazon.ion.IonBool;
import software.amazon.ion.IonClob;
import software.amazon.ion.IonDecimal;
import software.amazon.ion.IonException;
import software.amazon.ion.IonFloat;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonList;
import software.amazon.ion.IonNull;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonSexp;
import software.amazon.ion.IonString;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSymbol;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ValueFactory {
    public IonBlob newNullBlob();

    public IonBlob newBlob(byte[] var1);

    public IonBlob newBlob(byte[] var1, int var2, int var3);

    public IonBool newNullBool();

    public IonBool newBool(boolean var1);

    public IonBool newBool(Boolean var1);

    public IonClob newNullClob();

    public IonClob newClob(byte[] var1);

    public IonClob newClob(byte[] var1, int var2, int var3);

    public IonDecimal newNullDecimal();

    public IonDecimal newDecimal(long var1);

    public IonDecimal newDecimal(double var1);

    public IonDecimal newDecimal(BigInteger var1);

    public IonDecimal newDecimal(BigDecimal var1);

    public IonFloat newNullFloat();

    public IonFloat newFloat(long var1);

    public IonFloat newFloat(double var1);

    public IonInt newNullInt();

    public IonInt newInt(int var1);

    public IonInt newInt(long var1);

    public IonInt newInt(Number var1);

    public IonList newNullList();

    public IonList newEmptyList();

    public IonList newList(IonSequence var1) throws ContainedValueException, NullPointerException;

    public IonList newList(IonValue ... var1) throws ContainedValueException, NullPointerException;

    public IonList newList(int[] var1);

    public IonList newList(long[] var1);

    public IonNull newNull();

    public IonValue newNull(IonType var1);

    public IonSexp newNullSexp();

    public IonSexp newEmptySexp();

    public IonSexp newSexp(IonSequence var1) throws ContainedValueException, NullPointerException;

    public IonSexp newSexp(IonValue ... var1) throws ContainedValueException, NullPointerException;

    public IonSexp newSexp(int[] var1);

    public IonSexp newSexp(long[] var1);

    public IonString newNullString();

    public IonString newString(String var1);

    public IonStruct newNullStruct();

    public IonStruct newEmptyStruct();

    public IonSymbol newNullSymbol();

    public IonSymbol newSymbol(String var1);

    public IonSymbol newSymbol(SymbolToken var1);

    public IonTimestamp newNullTimestamp();

    public IonTimestamp newTimestamp(Timestamp var1);

    public <T extends IonValue> T clone(T var1) throws IonException;
}

