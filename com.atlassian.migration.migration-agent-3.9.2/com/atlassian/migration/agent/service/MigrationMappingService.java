/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationMappingService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationMappingService.class);
    private static final String CONFLUENCE_NAMESPACE = "confluence:";
    private static final String EMPTY_STRING = "";
    public static final ImmutableList<String> CONFLUENCE_MAPPINGS = ImmutableList.of((Object)"space", (Object)"page", (Object)"comment", (Object)"attachment", (Object)"blogPost", (Object)"outgoingLink", (Object)"referralLink", (Object)"customContentEntityObject");
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;

    public MigrationMappingService(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
    }

    public Map<String, String> getMappings(String cloudId, String migrationScopeId, String namespace) {
        return this.enterpriseGatekeeperClient.getMappings(cloudId, migrationScopeId, CONFLUENCE_NAMESPACE + namespace, EMPTY_STRING);
    }
}

