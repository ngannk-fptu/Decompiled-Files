/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.Calendar;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public final class DateAndTime
implements Duplicatable {
    public static final int SIZE = 4;
    private static final BitField _minutes = BitFieldFactory.getInstance(63);
    private static final BitField _hours = BitFieldFactory.getInstance(1984);
    private static final BitField _dom = BitFieldFactory.getInstance(63488);
    private static final BitField _months = BitFieldFactory.getInstance(15);
    private static final BitField _years = BitFieldFactory.getInstance(8176);
    private short _info;
    private short _info2;

    public DateAndTime() {
    }

    public DateAndTime(DateAndTime other) {
        this._info = other._info;
        this._info2 = other._info2;
    }

    public DateAndTime(byte[] buf, int offset) {
        this._info = LittleEndian.getShort(buf, offset);
        this._info2 = LittleEndian.getShort(buf, offset + 2);
    }

    public Calendar getDate() {
        return LocaleUtil.getLocaleCalendar(_years.getValue(this._info2) + 1900, _months.getValue(this._info2) - 1, _dom.getValue(this._info), _hours.getValue(this._info), _minutes.getValue(this._info), 0);
    }

    public void serialize(byte[] buf, int offset) {
        LittleEndian.putShort(buf, offset, this._info);
        LittleEndian.putShort(buf, offset + 2, this._info2);
    }

    public boolean equals(Object o) {
        if (!(o instanceof DateAndTime)) {
            return false;
        }
        DateAndTime dttm = (DateAndTime)o;
        return this._info == dttm._info && this._info2 == dttm._info2;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Override
    public DateAndTime copy() {
        return new DateAndTime(this);
    }

    public boolean isEmpty() {
        return this._info == 0 && this._info2 == 0;
    }

    public String toString() {
        if (this.isEmpty()) {
            return "[DTTM] EMPTY";
        }
        return "[DTTM] " + this.getDate();
    }
}

