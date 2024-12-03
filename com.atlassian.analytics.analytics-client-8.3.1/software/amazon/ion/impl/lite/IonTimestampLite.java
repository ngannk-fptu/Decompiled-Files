/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.NullValueException;
import software.amazon.ion.Timestamp;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonTimestampLite
extends IonValueLite
implements IonTimestamp {
    public static final Integer UTC_OFFSET = Timestamp.UTC_OFFSET;
    private static final int BIT_FLAG_YEAR = 1;
    private static final int BIT_FLAG_MONTH = 2;
    private static final int BIT_FLAG_DAY = 4;
    private static final int BIT_FLAG_MINUTE = 8;
    private static final int BIT_FLAG_SECOND = 16;
    private static final int BIT_FLAG_FRACTION = 32;
    private static final int HASH_SIGNATURE = IonType.TIMESTAMP.toString().hashCode();
    private Timestamp _timestamp_value;

    IonTimestampLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonTimestampLite(IonTimestampLite existing, IonContext context) {
        super(existing, context);
        this._timestamp_value = existing._timestamp_value;
    }

    IonTimestampLite clone(IonContext context) {
        return new IonTimestampLite(this, context);
    }

    public IonTimestampLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            result ^= this.timestampValue().hashCode();
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.TIMESTAMP;
    }

    public Timestamp timestampValue() {
        if (this.isNullValue()) {
            return null;
        }
        return this._timestamp_value;
    }

    public Date dateValue() {
        if (this._isNullValue()) {
            return null;
        }
        return this._timestamp_value.dateValue();
    }

    public Integer getLocalOffset() throws NullValueException {
        if (this._isNullValue()) {
            throw new NullValueException();
        }
        return this._timestamp_value.getLocalOffset();
    }

    private Integer getInternalLocalOffset() {
        if (this._isNullValue()) {
            return null;
        }
        return this._timestamp_value.getLocalOffset();
    }

    public void setValue(Timestamp timestamp) {
        this.checkForLock();
        this._timestamp_value = timestamp;
        this._isNullValue(timestamp == null);
    }

    public void setValue(BigDecimal millis, Integer localOffset) {
        this.setValue(Timestamp.forMillis(millis, localOffset));
    }

    public void setValue(long millis, Integer localOffset) {
        this.setValue(Timestamp.forMillis(millis, localOffset));
    }

    public void setTime(Date value) {
        if (value == null) {
            this.makeNull();
        } else {
            this.setMillis(value.getTime());
        }
    }

    public BigDecimal getDecimalMillis() {
        if (this._isNullValue()) {
            return null;
        }
        return this._timestamp_value.getDecimalMillis();
    }

    public void setDecimalMillis(BigDecimal millis) {
        Integer offset = this.getInternalLocalOffset();
        this.setValue(millis, offset);
    }

    public long getMillis() {
        if (this._isNullValue()) {
            throw new NullValueException();
        }
        return this._timestamp_value.getMillis();
    }

    public void setMillis(long millis) {
        Integer offset = this.getInternalLocalOffset();
        this.setValue(millis, offset);
    }

    public void setMillisUtc(long millis) {
        this.setValue(millis, UTC_OFFSET);
    }

    public void setCurrentTime() {
        long millis = System.currentTimeMillis();
        this.setMillis(millis);
    }

    public void setCurrentTimeUtc() {
        long millis = System.currentTimeMillis();
        this.setMillisUtc(millis);
    }

    public void setLocalOffset(int minutes) throws NullValueException {
        this.setLocalOffset(new Integer(minutes));
    }

    public void setLocalOffset(Integer minutes) throws NullValueException {
        this.validateThisNotNull();
        assert (this._timestamp_value != null);
        this.setValue(this._timestamp_value.getDecimalMillis(), minutes);
    }

    public void makeNull() {
        this.checkForLock();
        this._timestamp_value = null;
        this._isNullValue(true);
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        writer.writeTimestamp(this._timestamp_value);
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

