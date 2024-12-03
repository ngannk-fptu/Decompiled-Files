/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.service.extract.GlobalEntityExtractionService;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class GlobalEntityExtractionServiceImpl
implements GlobalEntityExtractionService {
    @VisibleForTesting
    static final String GLOBAL_TEMPLATE_COUNT_QUERY = "SELECT COUNT(*) FROM PAGETEMPLATES \nwhere SPACEID is null AND\n PREVVER is null AND\n MODULEKEY is null";
    @VisibleForTesting
    static final String SYSTEM_TEMPLATE_COUNT_QUERY = "SELECT COUNT(*) FROM PAGETEMPLATES \nwhere SPACEID is null AND\n PREVVER is null AND\n MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message')";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GlobalEntityExtractionServiceImpl(ConfluenceWrapperDataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate((DataSource)((Object)dataSource));
    }

    @VisibleForTesting
    GlobalEntityExtractionServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long getGlobalTemplatesCount() {
        return (Long)this.jdbcTemplate.queryForObject(GLOBAL_TEMPLATE_COUNT_QUERY, new HashMap(), Long.class);
    }

    @Override
    public Long getSystemTemplatesCount() {
        return (Long)this.jdbcTemplate.queryForObject(SYSTEM_TEMPLATE_COUNT_QUERY, new HashMap(), Long.class);
    }
}

