/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.math.BigDecimal;
import java.util.Date;
import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.Timestamp;
import software.amazon.ion.UnknownSymbolException;

public interface IonTimestamp
extends IonValue {
    public Timestamp timestampValue();

    public Date dateValue();

    public long getMillis() throws NullValueException;

    public BigDecimal getDecimalMillis();

    public void setValue(Timestamp var1);

    public void setValue(BigDecimal var1, Integer var2);

    public void setValue(long var1, Integer var3);

    public void setMillis(long var1);

    public void setDecimalMillis(BigDecimal var1);

    public void setMillisUtc(long var1);

    public Integer getLocalOffset() throws NullValueException;

    public void setTime(Date var1);

    public void setCurrentTime();

    public void setCurrentTimeUtc();

    public void setLocalOffset(int var1) throws NullValueException;

    public void setLocalOffset(Integer var1) throws NullValueException;

    public void makeNull();

    public IonTimestamp clone() throws UnknownSymbolException;
}

