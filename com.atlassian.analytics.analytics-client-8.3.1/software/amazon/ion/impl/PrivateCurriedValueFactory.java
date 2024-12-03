/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

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
import software.amazon.ion.ValueFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public abstract class PrivateCurriedValueFactory
implements ValueFactory {
    private final ValueFactory myFactory;

    protected PrivateCurriedValueFactory(ValueFactory factory) {
        this.myFactory = factory;
    }

    protected abstract void handle(IonValue var1);

    @Override
    public IonBlob newNullBlob() {
        IonBlob v = this.myFactory.newNullBlob();
        this.handle(v);
        return v;
    }

    @Override
    public IonBlob newBlob(byte[] value) {
        IonBlob v = this.myFactory.newBlob(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonBlob newBlob(byte[] value, int offset, int length) {
        IonBlob v = this.myFactory.newBlob(value, offset, length);
        this.handle(v);
        return v;
    }

    @Override
    public IonBool newNullBool() {
        IonBool v = this.myFactory.newNullBool();
        this.handle(v);
        return v;
    }

    @Override
    public IonBool newBool(boolean value) {
        IonBool v = this.myFactory.newBool(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonBool newBool(Boolean value) {
        IonBool v = this.myFactory.newBool(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonClob newNullClob() {
        IonClob v = this.myFactory.newNullClob();
        this.handle(v);
        return v;
    }

    @Override
    public IonClob newClob(byte[] value) {
        IonClob v = this.myFactory.newClob(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonClob newClob(byte[] value, int offset, int length) {
        IonClob v = this.myFactory.newClob(value, offset, length);
        this.handle(v);
        return v;
    }

    @Override
    public IonDecimal newNullDecimal() {
        IonDecimal v = this.myFactory.newNullDecimal();
        this.handle(v);
        return v;
    }

    @Override
    public IonDecimal newDecimal(long value) {
        IonDecimal v = this.myFactory.newDecimal(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonDecimal newDecimal(double value) {
        IonDecimal v = this.myFactory.newDecimal(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonDecimal newDecimal(BigInteger value) {
        IonDecimal v = this.myFactory.newDecimal(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonDecimal newDecimal(BigDecimal value) {
        IonDecimal v = this.myFactory.newDecimal(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonFloat newNullFloat() {
        IonFloat v = this.myFactory.newNullFloat();
        this.handle(v);
        return v;
    }

    @Override
    public IonFloat newFloat(long value) {
        IonFloat v = this.myFactory.newFloat(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonFloat newFloat(double value) {
        IonFloat v = this.myFactory.newFloat(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonInt newNullInt() {
        IonInt v = this.myFactory.newNullInt();
        this.handle(v);
        return v;
    }

    @Override
    public IonInt newInt(int value) {
        IonInt v = this.myFactory.newInt(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonInt newInt(long value) {
        IonInt v = this.myFactory.newInt(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonInt newInt(Number value) {
        IonInt v = this.myFactory.newInt(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonList newNullList() {
        IonList v = this.myFactory.newNullList();
        this.handle(v);
        return v;
    }

    @Override
    public IonList newEmptyList() {
        IonList v = this.myFactory.newEmptyList();
        this.handle(v);
        return v;
    }

    @Override
    public IonList newList(IonSequence firstChild) throws ContainedValueException, NullPointerException {
        IonList v = this.myFactory.newList(firstChild);
        this.handle(v);
        return v;
    }

    @Override
    public IonList newList(IonValue ... values) throws ContainedValueException, NullPointerException {
        IonList v = this.myFactory.newList(values);
        this.handle(v);
        return v;
    }

    @Override
    public IonList newList(int[] values) {
        IonList v = this.myFactory.newList(values);
        this.handle(v);
        return v;
    }

    @Override
    public IonList newList(long[] values) {
        IonList v = this.myFactory.newList(values);
        this.handle(v);
        return v;
    }

    @Override
    public IonNull newNull() {
        IonNull v = this.myFactory.newNull();
        this.handle(v);
        return v;
    }

    @Override
    public IonValue newNull(IonType type) {
        IonValue v = this.myFactory.newNull(type);
        this.handle(v);
        return v;
    }

    @Override
    public IonSexp newNullSexp() {
        IonSexp v = this.myFactory.newNullSexp();
        this.handle(v);
        return v;
    }

    @Override
    public IonSexp newEmptySexp() {
        IonSexp v = this.myFactory.newEmptySexp();
        this.handle(v);
        return v;
    }

    @Override
    public IonSexp newSexp(IonSequence firstChild) throws ContainedValueException, NullPointerException {
        IonSexp v = this.myFactory.newSexp(firstChild);
        this.handle(v);
        return v;
    }

    @Override
    public IonSexp newSexp(IonValue ... values) throws ContainedValueException, NullPointerException {
        IonSexp v = this.myFactory.newSexp(values);
        this.handle(v);
        return v;
    }

    @Override
    public IonSexp newSexp(int[] values) {
        IonSexp v = this.myFactory.newSexp(values);
        this.handle(v);
        return v;
    }

    @Override
    public IonSexp newSexp(long[] values) {
        IonSexp v = this.myFactory.newSexp(values);
        this.handle(v);
        return v;
    }

    @Override
    public IonString newNullString() {
        IonString v = this.myFactory.newNullString();
        this.handle(v);
        return v;
    }

    @Override
    public IonString newString(String value) {
        IonString v = this.myFactory.newString(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonStruct newNullStruct() {
        IonStruct v = this.myFactory.newNullStruct();
        this.handle(v);
        return v;
    }

    @Override
    public IonStruct newEmptyStruct() {
        IonStruct v = this.myFactory.newEmptyStruct();
        this.handle(v);
        return v;
    }

    @Override
    public IonSymbol newNullSymbol() {
        IonSymbol v = this.myFactory.newNullSymbol();
        this.handle(v);
        return v;
    }

    @Override
    public IonSymbol newSymbol(String value) {
        IonSymbol v = this.myFactory.newSymbol(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonSymbol newSymbol(SymbolToken value) {
        IonSymbol v = this.myFactory.newSymbol(value);
        this.handle(v);
        return v;
    }

    @Override
    public IonTimestamp newNullTimestamp() {
        IonTimestamp v = this.myFactory.newNullTimestamp();
        this.handle(v);
        return v;
    }

    @Override
    public IonTimestamp newTimestamp(Timestamp value) {
        IonTimestamp v = this.myFactory.newTimestamp(value);
        this.handle(v);
        return v;
    }

    @Override
    public <T extends IonValue> T clone(T value) throws IonException {
        T v = this.myFactory.clone(value);
        this.handle(v);
        return v;
    }
}

