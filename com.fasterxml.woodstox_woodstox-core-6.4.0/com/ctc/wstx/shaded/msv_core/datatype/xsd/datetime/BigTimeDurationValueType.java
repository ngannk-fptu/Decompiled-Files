/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.TimeZone;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BigTimeDurationValueType
implements ITimeDurationValueType {
    protected int signum;
    protected BigInteger year;
    protected BigInteger month;
    protected BigInteger day;
    protected BigInteger hour;
    protected BigInteger minute;
    protected BigDecimal second;
    private static final BigDateTimeValueType[] testInstance = new BigDateTimeValueType[]{new BigDateTimeValueType(new BigInteger("1696"), 8, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO), new BigDateTimeValueType(new BigInteger("1697"), 1, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO), new BigDateTimeValueType(new BigInteger("1903"), 2, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO), new BigDateTimeValueType(new BigInteger("1903"), 6, 0, 0, 0, new BigDecimal(0), TimeZone.ZERO)};
    private static final long serialVersionUID = 1L;

    public boolean equals(Object o) {
        return this.equals((ITimeDurationValueType)o);
    }

    public boolean equals(ITimeDurationValueType o) {
        return this.compare(o) == 0;
    }

    public String toString() {
        return (this.signum < 0 ? "-" : "") + "P" + this.nullAsZero(this.year).abs() + "Y" + this.nullAsZero(this.month) + "M" + this.nullAsZero(this.day) + "DT" + this.nullAsZero(this.hour) + "H" + this.nullAsZero(this.minute) + "M" + (this.second == null ? "" : this.second.toString()) + "S";
    }

    private BigInteger nullAsZero(BigInteger o) {
        if (o == null) {
            return BigInteger.ZERO;
        }
        return o;
    }

    public int hashCode() {
        return this.nullAsZero(this.day).multiply(Util.the24).add(this.nullAsZero(this.hour)).multiply(Util.the60).add(this.nullAsZero(this.minute)).mod(Util.the210379680).hashCode();
    }

    public int compare(ITimeDurationValueType o) {
        if (!(o instanceof BigTimeDurationValueType)) {
            o = o.getBigValue();
        }
        return BigTimeDurationValueType.compare(this, (BigTimeDurationValueType)o);
    }

    private static int compare(BigTimeDurationValueType lhs, BigTimeDurationValueType rhs) {
        boolean less = false;
        boolean greater = false;
        boolean noDeterminate = false;
        for (int i = 0; i < testInstance.length; ++i) {
            BigDateTimeValueType r;
            BigDateTimeValueType l = (BigDateTimeValueType)testInstance[i].add(lhs);
            int v = BigDateTimeValueType.compare(l, r = (BigDateTimeValueType)testInstance[i].add(rhs));
            if (v < 0) {
                less = true;
            }
            if (v > 0) {
                greater = true;
            }
            if (v != 0 || l.equals(r)) continue;
            noDeterminate = true;
        }
        if (noDeterminate) {
            return 999;
        }
        if (less && greater) {
            return 999;
        }
        if (less) {
            return -1;
        }
        if (greater) {
            return 1;
        }
        return 0;
    }

    public BigTimeDurationValueType getBigValue() {
        return this;
    }

    public BigTimeDurationValueType(int signum, BigInteger year, BigInteger month, BigInteger day, BigInteger hour, BigInteger minute, BigDecimal second) {
        this.signum = signum;
        this.year = year != null ? year : BigInteger.ZERO;
        this.month = month != null ? month : BigInteger.ZERO;
        this.day = day != null ? day : BigInteger.ZERO;
        this.hour = hour != null ? hour : BigInteger.ZERO;
        this.minute = minute != null ? minute : BigInteger.ZERO;
        this.second = second != null ? second : Util.decimal0;
    }

    public static BigTimeDurationValueType fromMinutes(int minutes) {
        return BigTimeDurationValueType.fromMinutes(Util.int2bi(minutes));
    }

    public static BigTimeDurationValueType fromMinutes(BigInteger minutes) {
        return new BigTimeDurationValueType(minutes.signum(), null, null, null, null, minutes.abs(), null);
    }

    public BigInteger getDay() {
        return this.day;
    }

    public BigInteger getHour() {
        return this.hour;
    }

    public BigInteger getMinute() {
        return this.minute;
    }

    public BigInteger getMonth() {
        return this.month;
    }

    public BigDecimal getSecond() {
        return this.second;
    }

    public BigInteger getYear() {
        return this.year;
    }

    public BigTimeDurationValueType(String lexicalRepresentation) throws IllegalArgumentException {
        boolean positive;
        String s = lexicalRepresentation;
        int[] idx = new int[1];
        if (s.charAt(idx[0]) == '-') {
            idx[0] = idx[0] + 1;
            positive = false;
        } else {
            positive = true;
        }
        int n = idx[0];
        idx[0] = n + 1;
        if (s.charAt(n) != 'P') {
            throw new IllegalArgumentException(s);
        }
        int dateLen = 0;
        String[] dateParts = new String[3];
        int[] datePartsIndex = new int[3];
        while (s.length() != idx[0] && BigTimeDurationValueType.isDigit(s.charAt(idx[0])) && dateLen < 3) {
            datePartsIndex[dateLen] = idx[0];
            dateParts[dateLen++] = BigTimeDurationValueType.parsePiece(s, idx);
        }
        if (s.length() != idx[0]) {
            int n2 = idx[0];
            idx[0] = n2 + 1;
            if (s.charAt(n2) != 'T') {
                throw new IllegalArgumentException(s);
            }
        }
        int timeLen = 0;
        String[] timeParts = new String[3];
        int[] timePartsIndex = new int[3];
        while (s.length() != idx[0] && BigTimeDurationValueType.isDigitOrPeriod(s.charAt(idx[0])) && timeLen < 3) {
            timePartsIndex[timeLen] = idx[0];
            timeParts[timeLen++] = BigTimeDurationValueType.parsePiece(s, idx);
        }
        if (s.length() != idx[0]) {
            throw new IllegalArgumentException(s);
        }
        if (dateLen == 0 && timeLen == 0) {
            throw new IllegalArgumentException(s);
        }
        BigTimeDurationValueType.organizeParts(s, dateParts, datePartsIndex, dateLen, "YMD");
        BigTimeDurationValueType.organizeParts(s, timeParts, timePartsIndex, timeLen, "HMS");
        this.year = BigTimeDurationValueType.parseBigInteger(s, dateParts[0], datePartsIndex[0]);
        this.month = BigTimeDurationValueType.parseBigInteger(s, dateParts[1], datePartsIndex[1]);
        this.day = BigTimeDurationValueType.parseBigInteger(s, dateParts[2], datePartsIndex[2]);
        this.hour = BigTimeDurationValueType.parseBigInteger(s, timeParts[0], timePartsIndex[0]);
        this.minute = BigTimeDurationValueType.parseBigInteger(s, timeParts[1], timePartsIndex[1]);
        this.second = BigTimeDurationValueType.parseBigDecimal(s, timeParts[2], timePartsIndex[2]);
        this.year = this.year != null ? this.year : BigInteger.ZERO;
        this.month = this.month != null ? this.month : BigInteger.ZERO;
        this.day = this.day != null ? this.day : BigInteger.ZERO;
        this.hour = this.hour != null ? this.hour : BigInteger.ZERO;
        this.minute = this.minute != null ? this.minute : BigInteger.ZERO;
        BigDecimal bigDecimal = this.second = this.second != null ? this.second : Util.decimal0;
        this.signum = this.getSignum(this.year) == 0 && this.getSignum(this.month) == 0 && this.getSignum(this.day) == 0 && this.getSignum(this.hour) == 0 && this.getSignum(this.minute) == 0 && this.getSignum(this.second) == 0 ? 0 : (positive ? 1 : -1);
    }

    private int getSignum(BigInteger i) {
        if (i == null) {
            return 0;
        }
        return i.signum();
    }

    private int getSignum(BigDecimal i) {
        if (i == null) {
            return 0;
        }
        return i.signum();
    }

    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    private static boolean isDigitOrPeriod(char ch) {
        return BigTimeDurationValueType.isDigit(ch) || ch == '.';
    }

    private static String parsePiece(String whole, int[] idx) throws IllegalArgumentException {
        int start = idx[0];
        while (idx[0] < whole.length() && BigTimeDurationValueType.isDigitOrPeriod(whole.charAt(idx[0]))) {
            idx[0] = idx[0] + 1;
        }
        if (idx[0] == whole.length()) {
            throw new IllegalArgumentException(whole);
        }
        idx[0] = idx[0] + 1;
        return whole.substring(start, idx[0]);
    }

    private static void organizeParts(String whole, String[] parts, int[] partsIndex, int len, String tokens) throws IllegalArgumentException {
        int idx = tokens.length();
        for (int i = len - 1; i >= 0; --i) {
            int nidx = tokens.lastIndexOf(parts[i].charAt(parts[i].length() - 1), idx - 1);
            if (nidx == -1) {
                throw new IllegalArgumentException(whole);
            }
            for (int j = nidx + 1; j < idx; ++j) {
                parts[j] = null;
            }
            idx = nidx;
            parts[idx] = parts[i];
            partsIndex[idx] = partsIndex[i];
        }
        --idx;
        while (idx >= 0) {
            parts[idx] = null;
            --idx;
        }
    }

    private static BigInteger parseBigInteger(String whole, String part, int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        return new BigInteger(part);
    }

    private static BigDecimal parseBigDecimal(String whole, String part, int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        return new BigDecimal(part);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        switch (this.year.signum()) {
            case -1: {
                this.signum = -1;
                this.year = this.year.negate();
                return;
            }
            case 1: {
                this.signum = 1;
                return;
            }
        }
        switch (this.month.signum()) {
            case -1: {
                this.signum = -1;
                this.month = this.month.negate();
                return;
            }
            case 1: {
                this.signum = 1;
                return;
            }
        }
        switch (this.day.signum()) {
            case -1: {
                this.signum = -1;
                this.day = this.day.negate();
                return;
            }
            case 1: {
                this.signum = 1;
                return;
            }
        }
        switch (this.hour.signum()) {
            case -1: {
                this.signum = -1;
                this.hour = this.hour.negate();
                return;
            }
            case 1: {
                this.signum = 1;
                return;
            }
        }
        switch (this.minute.signum()) {
            case -1: {
                this.signum = -1;
                this.minute = this.minute.negate();
                return;
            }
            case 1: {
                this.signum = 1;
                return;
            }
        }
        switch (this.second.signum()) {
            case -1: {
                this.signum = -1;
                this.second = this.second.negate();
                return;
            }
            case 1: {
                this.signum = 1;
                return;
            }
        }
        this.signum = 0;
    }
}

