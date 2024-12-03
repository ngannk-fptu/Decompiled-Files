/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.namedparam;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.lang.Nullable;

public class NamedParameterJdbcDaoSupport
extends JdbcDaoSupport {
    @Nullable
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    protected void initTemplateConfig() {
        JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
        if (jdbcTemplate != null) {
            this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        }
    }

    @Nullable
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return this.namedParameterJdbcTemplate;
    }
}

