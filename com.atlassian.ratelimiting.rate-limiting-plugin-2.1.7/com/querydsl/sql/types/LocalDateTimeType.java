/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDateTime
 *  org.joda.time.ReadablePartial
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadablePartial;

public class LocalDateTimeType
extends AbstractDateTimeType<LocalDateTime> {
    public LocalDateTimeType() {
        super(93);
    }

    public LocalDateTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(LocalDateTime value) {
        return dateTimeFormatter.print((ReadablePartial)value);
    }

    @Override
    public Class<LocalDateTime> getReturnedClass() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime getValue(ResultSet rs, int index) throws SQLException {
        Timestamp ts = rs.getTimestamp(index, LocalDateTimeType.utc());
        return ts != null ? new LocalDateTime(ts.getTime(), DateTimeZone.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int index, LocalDateTime value) throws SQLException {
        DateTime dt = value.toDateTime(DateTimeZone.UTC);
        st.setTimestamp(index, new Timestamp(dt.getMillis()), LocalDateTimeType.utc());
    }
}

