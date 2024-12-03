/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractJSR310DateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public class JSR310ZonedDateTimeType
extends AbstractJSR310DateTimeType<ZonedDateTime> {
    public JSR310ZonedDateTimeType() {
        super(2014);
    }

    public JSR310ZonedDateTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(ZonedDateTime value) {
        return dateTimeFormatter.format(value);
    }

    @Override
    public Class<ZonedDateTime> getReturnedClass() {
        return ZonedDateTime.class;
    }

    @Override
    @Nullable
    public ZonedDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(startIndex, JSR310ZonedDateTimeType.utc());
        return ts != null ? ZonedDateTime.ofInstant(ts.toInstant(), ZoneOffset.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, ZonedDateTime value) throws SQLException {
        st.setTimestamp(startIndex, new Timestamp(value.toInstant().toEpochMilli()), JSR310ZonedDateTimeType.utc());
    }
}

