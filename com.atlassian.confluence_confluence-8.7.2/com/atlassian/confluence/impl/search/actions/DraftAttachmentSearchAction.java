/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.search.actions.AttachmentSearchAction;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class DraftAttachmentSearchAction
extends ConfluenceActionSupport
implements Beanable {
    private Map<String, Object> result = new HashMap<String, Object>();
    private AttachmentManager attachmentManager;
    private long draftId;
    private DraftManager draftManager;
    private String[] filetypes;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        Draft content = this.getDraft();
        if (content != null) {
            List<Attachment> attachments = AttachmentSearchAction.filterAttachments(this.attachmentManager.getLatestVersionsOfAttachments(content), this.filetypes);
            this.result.put("attachments", attachments);
        } else {
            this.result.put("error", "No draft found");
        }
        return "success";
    }

    private Draft getDraft() {
        return this.draftManager.getDraft(this.draftId);
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getDraft());
    }

    @Override
    public Object getBean() {
        return this.result;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setFileTypes(String[] filetypes) {
        this.filetypes = (String[])ArrayUtils.clone((Object[])filetypes);
    }

    public void setDraftId(long draftId) {
        this.draftId = draftId;
    }

    public void setDraftManager(DraftManager draftManager) {
        this.draftManager = draftManager;
    }
}

