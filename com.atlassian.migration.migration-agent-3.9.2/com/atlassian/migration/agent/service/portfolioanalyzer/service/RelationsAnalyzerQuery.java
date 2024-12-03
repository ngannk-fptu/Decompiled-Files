/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.service;

public class RelationsAnalyzerQuery {
    public static final String LOCAL_SPACE_RELATIONS_QUERY = "SELECT NEW com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceRelations(       CONCAT(:baseUrl, ':', s1.key),       CONCAT(:baseUrl, ':', s2.key),       COUNT(*))FROM Link l JOIN SpaceContent sc ON (CASE WHEN l.content.spaceId IS NULL THEN (SELECT c2.id FROM Content c2 WHERE c2.id = l.content.container.id) ELSE l.content.id END) = sc.id JOIN sc.space s1 JOIN Space s2 ON l.destSpaceKey = s2.key WHERE l.destSpaceKey NOT LIKE 'http%'  AND l.content.status = 'current'  AND s1.key != s2.key GROUP BY s1.key, s2.key";
    public static final String CONTENT_WITH_JIRA_MACROS_CQL_QUERY = "type IN (page,blogpost,comment) AND macro IN (jira)";
    public static final String LINKS_BY_DESTINATION_BASE_URL_QUERY = "SELECT NEW com.atlassian.migration.agent.service.portfolioanalyzer.model.LinkWithSourceSpaceKey(l.lowerDestPageTitle, l.lowerDestSpaceKey, s1.key) FROM Link l JOIN SpaceContent sc ON (CASE WHEN l.content.spaceId IS NULL THEN (SELECT c2.id FROM Content c2 WHERE c2.id = l.content.container.id) ELSE l.content.id END) = sc.id JOIN sc.space s1 WHERE l.destSpaceKey like 'http%' AND l.lowerDestPageTitle LIKE '//' || :baseUrlWithoutScheme || '%' AND l.content.status = 'current'";
    public static final String LAST_MODIFIED_CONTENT_WITHIN_SPACE_TIMESTAMP_QUERY = "SELECT MAX(c.lastModDate) FROM Content c LEFT JOIN Content cc ON cc.id = c.container.id WHERE COALESCE(c.spaceId, cc.spaceId) = :spaceId";

    private RelationsAnalyzerQuery() {
    }
}

