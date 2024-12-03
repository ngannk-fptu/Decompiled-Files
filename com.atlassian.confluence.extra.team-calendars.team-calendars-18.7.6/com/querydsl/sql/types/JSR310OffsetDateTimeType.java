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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public class JSR310OffsetDateTimeType
extends AbstractJSR310DateTimeType<OffsetDateTime> {
    public JSR310OffsetDateTimeType() {
        super(93);
    }

    public JSR310OffsetDateTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(OffsetDateTime value) {
        return dateTimeFormatter.format(value);
    }

    @Override
    public Class<OffsetDateTime> getReturnedClass() {
        return OffsetDateTime.class;
    }

    @Override
    @Nullable
    public OffsetDateTime getValue(ResultSet rs, int startIndex) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(startIndex, JSR310OffsetDateTimeType.utc());
        return timestamp != null ? OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, OffsetDateTime value) throws SQLException {
        st.setTimestamp(startIndex, new Timestamp(value.toInstant().toEpochMilli()), JSR310OffsetDateTimeType.utc());
    }
}

