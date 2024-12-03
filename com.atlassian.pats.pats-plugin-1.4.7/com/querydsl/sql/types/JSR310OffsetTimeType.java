/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractJSR310DateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public class JSR310OffsetTimeType
extends AbstractJSR310DateTimeType<OffsetTime> {
    public JSR310OffsetTimeType() {
        super(92);
    }

    public JSR310OffsetTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(OffsetTime value) {
        return timeFormatter.format(value);
    }

    @Override
    public Class<OffsetTime> getReturnedClass() {
        return OffsetTime.class;
    }

    @Override
    @Nullable
    public OffsetTime getValue(ResultSet rs, int startIndex) throws SQLException {
        Time time = rs.getTime(startIndex, JSR310OffsetTimeType.utc());
        return time != null ? OffsetTime.ofInstant(Instant.ofEpochMilli(time.getTime()), ZoneOffset.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, OffsetTime value) throws SQLException {
        OffsetTime normalized = value.withOffsetSameInstant(ZoneOffset.UTC);
        st.setTime(startIndex, new Time(normalized.get(ChronoField.MILLI_OF_DAY)), JSR310OffsetTimeType.utc());
    }
}

