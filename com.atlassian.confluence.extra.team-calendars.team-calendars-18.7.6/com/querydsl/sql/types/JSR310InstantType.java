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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public class JSR310InstantType
extends AbstractJSR310DateTimeType<Instant> {
    public JSR310InstantType() {
        super(93);
    }

    public JSR310InstantType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(Instant value) {
        return dateTimeFormatter.format(LocalDateTime.ofInstant(value, ZoneId.of("Z")));
    }

    @Override
    public Class<Instant> getReturnedClass() {
        return Instant.class;
    }

    @Override
    @Nullable
    public Instant getValue(ResultSet rs, int startIndex) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(startIndex, JSR310InstantType.utc());
        return timestamp != null ? timestamp.toInstant() : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Instant value) throws SQLException {
        st.setTimestamp(startIndex, Timestamp.from(value), JSR310InstantType.utc());
    }
}

