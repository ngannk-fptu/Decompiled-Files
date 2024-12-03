/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.dto.CheckDetailsDto
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.cmpt.check.mapper.AbstractMapper
 */
package com.atlassian.migration.agent.service.check.version;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.dto.CheckDetailsDto;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.cmpt.check.mapper.AbstractMapper;
import java.util.HashMap;

public class ConfluenceSupportedVersionCheckMapper
extends AbstractMapper {
    public static final String SUCCESS_DESCRIPTION = "Your Confluence version is up to date";
    public static final String ERROR_DESCRIPTION = "Upgrade to Confluence 6.15 or higher";
    public static final String RUNNING_DESCRIPTION = "Checking your Confluence version";
    public static final String EXECUTION_ERROR_DESCRIPTION = "We couldn't check your Confluence version";
    public static final String UPGRADE_CONFLUENCE_LINK = "https://confluence.atlassian.com/doc/upgrading-confluence-4578.html";
    public static final String LEARN_MORE_LINK = "https://community.atlassian.com/t5/Feedback-Forum-articles/Upgrade-to-enhance-your-Server-Data-Center-to-Cloud-migration/ba-p/2138337";
    public static final String SUPPORT_LINK = "https://support.atlassian.com/contact/#/?inquiry_category=migration_support";

    protected void inject(CheckResultDto dto, CheckResult checkResult) {
        switch (dto.getStatus()) {
            case RUNNING: {
                dto.setDescription(RUNNING_DESCRIPTION);
                return;
            }
            case SUCCESS: {
                dto.setDescription(SUCCESS_DESCRIPTION);
                return;
            }
            case ERROR: 
            case WARNING: {
                dto.setDescription(ERROR_DESCRIPTION);
                dto.setStatus(Status.ERROR);
                break;
            }
            case EXECUTION_ERROR: {
                dto.setDescription(EXECUTION_ERROR_DESCRIPTION);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported status.");
            }
        }
        dto.setDetails(this.getDtoDetails());
    }

    private CheckDetailsDto getDtoDetails() {
        CheckDetailsDto details = new CheckDetailsDto();
        HashMap info = new HashMap();
        HashMap<String, String> links = new HashMap<String, String>();
        links.put("upgradeConfluence", UPGRADE_CONFLUENCE_LINK);
        links.put("learnMore", LEARN_MORE_LINK);
        links.put("contactSupport", SUPPORT_LINK);
        info.put("links", links);
        details.setInfo(info);
        return details;
    }
}

