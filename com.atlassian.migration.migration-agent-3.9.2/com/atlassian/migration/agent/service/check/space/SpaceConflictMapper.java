/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.SpaceConflict;
import com.atlassian.migration.agent.service.check.space.SpaceConflictChecker;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.util.UriComponentsBuilder;

public class SpaceConflictMapper
extends AbstractMapper {
    private final SpaceManager spaceManager;
    private final String serverBaseUrl;

    public SpaceConflictMapper(SpaceManager spaceManager, SystemInformationService sysInfoService) {
        this.spaceManager = spaceManager;
        this.serverBaseUrl = sysInfoService.getConfluenceInfo().getBaseUrl();
    }

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case SUCCESS: {
                dto.setDescription("There are no conflicting space keys");
                break;
            }
            case ERROR: 
            case WARNING: {
                dto.setDescription("Spaces already exist in your cloud site");
                CheckDetailsDto details = new CheckDetailsDto();
                details.setListOfOccurrences(this.transform(SpaceConflictChecker.retrieveConflictingSpaces(checkResult.details)));
                dto.setDetails(details);
                break;
            }
            case RUNNING: {
                dto.setDescription("Checking for spaces in your cloud site");
                break;
            }
            case EXECUTION_ERROR: {
                dto.setDescription("We couldn\u2019t check for space key conflicts");
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
    }

    private List<SpaceConflictDetail> transform(List<SpaceConflict> conflicts) {
        return conflicts.stream().map(this::buildConflictDetail).collect(Collectors.toList());
    }

    private SpaceConflictDetail buildConflictDetail(SpaceConflict spaceConflict) {
        Space space = this.spaceManager.getSpace(spaceConflict.key);
        return new SpaceConflictDetail(spaceConflict.key, space == null ? "unknown" : space.getName(), space == null ? "" : UriComponentsBuilder.fromHttpUrl((String)this.serverBaseUrl).path(space.getUrlPath()).toUriString(), spaceConflict.name, spaceConflict.url);
    }

    static class SpaceConflictDetail {
        public final String key;
        public final String serverName;
        public final String serverUrl;
        public final String cloudName;
        public final String cloudUrl;

        public SpaceConflictDetail(String key, String serverName, String serverUrl, String cloudName, String cloudUrl) {
            this.key = key;
            this.serverName = serverName;
            this.serverUrl = serverUrl;
            this.cloudName = cloudName;
            this.cloudUrl = cloudUrl;
        }
    }
}

