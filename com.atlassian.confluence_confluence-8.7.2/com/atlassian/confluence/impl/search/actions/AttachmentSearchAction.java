/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class AttachmentSearchAction
extends AbstractPageAwareAction
implements Beanable {
    private Map<String, Object> result = new HashMap<String, Object>();
    private AttachmentManager attachmentManager;
    private String[] filetypes;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.isPermitted()) {
            AbstractPage content = this.getPage();
            if (content != null) {
                List<Attachment> attachments = AttachmentSearchAction.filterAttachments(this.attachmentManager.getLatestVersionsOfAttachments(content), this.filetypes);
                this.result.put("attachments", attachments);
            } else {
                this.result.put("error", "No page/blogpost found");
            }
        } else {
            this.result.put("error", "No permission to view page/blogpost");
        }
        return super.execute();
    }

    static List<Attachment> filterAttachments(Collection<Attachment> attachments, String[] filetypes) {
        ArrayList<Attachment> filteredAttachments = new ArrayList<Attachment>(attachments.size());
        boolean noTypeFilter = ArrayUtils.isEmpty((Object[])filetypes);
        for (Attachment attachment : attachments) {
            String fileName = attachment.getFileName();
            String fileType = fileName.substring(fileName.lastIndexOf(46) + 1);
            if (!noTypeFilter && !ArrayUtils.contains((Object[])filetypes, (Object)fileType.toLowerCase())) continue;
            filteredAttachments.add(attachment);
        }
        return filteredAttachments;
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
}

