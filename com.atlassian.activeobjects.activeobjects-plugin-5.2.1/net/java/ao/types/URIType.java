/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.java.ao.EntityManager;
import net.java.ao.types.AbstractLogicalType;
import net.java.ao.util.StringUtils;

final class URIType
extends AbstractLogicalType<URI> {
    public URIType() {
        super("URI", new Class[]{URI.class}, 12, new Integer[0]);
    }

    @Override
    public boolean isAllowedAsPrimaryKey() {
        return true;
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, URI value, int jdbcType) throws SQLException {
        stmt.setString(index, value.toString());
    }

    @Override
    public URI pullFromDatabase(EntityManager manager, ResultSet res, Class<URI> type, String columnName) throws SQLException {
        try {
            String uri = res.getString(columnName);
            return uri == null ? null : new URI(uri);
        }
        catch (URISyntaxException e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public URI parse(String input) {
        try {
            return StringUtils.isBlank(input) ? null : new URI(input);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("'" + input + "' is not a valid URI");
        }
    }
}

