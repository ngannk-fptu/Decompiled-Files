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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public class JSR310LocalDateTimeType
extends AbstractJSR310DateTimeType<LocalDateTime> {
    public JSR310LocalDateTimeType() {
        super(93);
    }

    public JSR310LocalDateTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(LocalDateTime value) {
        return dateTimeFormatter.format(value);
    }

    @Override
    public Class<LocalDateTime> getReturnedClass() {
        return LocalDateTime.class;
    }

    @Override
    @Nullable
    public LocalDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(startIndex, JSR310LocalDateTimeType.utc());
        return ts != null ? LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, LocalDateTime value) throws SQLException {
        Instant i = value.toInstant(ZoneOffset.UTC);
        st.setTimestamp(startIndex, new Timestamp(i.toEpochMilli()), JSR310LocalDateTimeType.utc());
    }
}

