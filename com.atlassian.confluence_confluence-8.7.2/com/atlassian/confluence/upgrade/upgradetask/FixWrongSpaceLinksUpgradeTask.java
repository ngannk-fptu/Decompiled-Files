/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.collect.ImmutableMap
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class FixWrongSpaceLinksUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(FixWrongSpaceLinksUpgradeTask.class);
    private final SessionFactory sessionFactory;
    private static final int MAX_UPDATE_BATCH_SIZE = 100;
    private static final String GET_BROKEN_PAGES_QUERY = "SELECT CONTENT.CONTENTID as contentId, parent.SPACEID as spaceId\nFROM CONTENT\nJOIN CONTENT parent ON CONTENT.PARENTID = parent.CONTENTID\nWHERE CONTENT.SPACEID != parent.SPACEID";
    private static final String FIX_BROKEN_PAGES_IN_THE_SPACE_QUERY = "UPDATE CONTENT SET SPACEID = :spaceId where CONTENTID in (:contentIdList)";

    public FixWrongSpaceLinksUpgradeTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public String getBuildNumber() {
        return "7701";
    }

    public String getShortDescription() {
        return "Fixes wrong spaces for drafts";
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        log.info("Started to check database inconsistency");
        try {
            HashMap pagesWithBrokenSpaceId = new HashMap();
            DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).query(GET_BROKEN_PAGES_QUERY, rs -> {
                Long contentId = rs.getLong("contentId");
                Long spaceId = rs.getLong("spaceId");
                pagesWithBrokenSpaceId.put(contentId, spaceId);
            });
            if (pagesWithBrokenSpaceId.size() > 0) {
                log.info("Upgrade found {} object(s) with broken space id and going to fix it", (Object)pagesWithBrokenSpaceId.size());
            }
            Map pagesGroupedBySpaces = pagesWithBrokenSpaceId.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
            int updated = 0;
            for (Map.Entry entry : pagesGroupedBySpaces.entrySet()) {
                updated += this.fixSpaceId(entry.getKey(), entry.getValue());
            }
            if (updated > 0) {
                this.sessionFactory.getCache().evict(Page.class);
            }
            if (updated != pagesWithBrokenSpaceId.size()) {
                log.warn("Upgrade: we found {} page(s), but upgraded only {}", (Object)pagesGroupedBySpaces.size(), (Object)updated);
            }
            log.info("Upgrade finished. Found {} broken page(s), fixed {} page(s)", (Object)pagesWithBrokenSpaceId.size(), (Object)updated);
        }
        catch (Exception e) {
            log.error("doUpgrade failed with the message " + e.toString(), (Throwable)e);
        }
    }

    private int fixSpaceId(Long spaceId, List<Long> contentIdList) {
        int updated = 0;
        for (int i = 0; i < contentIdList.size(); i += 100) {
            List<Long> contentIdListSubset = contentIdList.subList(i, Math.min(i + 100, contentIdList.size()));
            ImmutableMap paramMap = ImmutableMap.of((Object)"spaceId", (Object)spaceId, (Object)"contentIdList", contentIdListSubset);
            DataSource dataSource = Objects.requireNonNull(DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).getDataSource());
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
            updated += template.update(FIX_BROKEN_PAGES_IN_THE_SPACE_QUERY, (Map)paramMap);
        }
        return updated;
    }
}

