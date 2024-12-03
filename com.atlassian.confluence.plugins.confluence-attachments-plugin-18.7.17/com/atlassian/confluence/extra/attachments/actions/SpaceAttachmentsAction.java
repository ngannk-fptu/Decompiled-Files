/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.LabelUtil
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.attachments.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.attachments.SpaceAttachments;
import com.atlassian.confluence.extra.attachments.SpaceAttachmentsUtils;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SpaceAttachmentsAction
extends ConfluenceActionSupport
implements SpaceAware {
    private SpaceAttachmentsUtils spaceAttachmentsUtils;
    private String sortBy;
    private String fileExtension;
    private String labelFilter;
    private String messageKey;
    private String[] messageParameter;
    private int pageNumber;
    private int totalAttachments;
    private int totalPage;
    private int pageSize;
    private List<Attachment> latestVersionsOfAttachments;
    private boolean allowFilterByFileExtension;
    private boolean showFilter;
    private boolean showAttachmentsNotFound;
    private Space space;
    private PaginationSupport<Attachment> paginationSupport = new PaginationSupport(20);

    public void validate() {
        super.validate();
        if (!LabelUtil.isValidLabelNames((String)this.getLabelFilter())) {
            this.setShowAttachmentsNotFound(false);
            this.addFieldError("attachmentLabelsString", this.getText(this.getText("attachments.labels.invalid.characters", new String[]{LabelParser.getInvalidCharactersAsString()})));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws InvalidSearchException {
        HashSet<String> labels = new HashSet<String>(SpaceAttachmentsAction.getLabelsForFilter(this.getLabelFilter(), this.labelManager));
        SpaceAttachments spaceAttachments = null;
        if (labels.size() != 0 || StringUtils.isBlank((CharSequence)this.getLabelFilter())) {
            spaceAttachments = this.spaceAttachmentsUtils.getAttachmentList(this.getSpace().getKey(), this.getPageNumber(), this.getTotalAttachments(), this.getPageSize(), this.getSortBy(), this.formatFileExtension(this.getFileExtension()), labels);
            this.setLatestVersionsOfAttachments(spaceAttachments.getAttachmentList());
            this.setTotalAttachments(spaceAttachments.getTotalAttachments());
            this.setTotalPage(spaceAttachments.getTotalPage());
        }
        this.setShowAttachmentsNotFound(true);
        if (spaceAttachments == null || spaceAttachments.getAttachmentList().isEmpty()) {
            this.setMessages();
        }
        return "success";
    }

    public void setSpaceAttachmentsUtils(SpaceAttachmentsUtils spaceAttachmentsUtils) {
        this.spaceAttachmentsUtils = spaceAttachmentsUtils;
    }

    public String getSpaceKey() {
        return this.getSpace().getKey();
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setTotalAttachments(int totalAttachments) {
        this.totalAttachments = totalAttachments;
    }

    public int getTotalAttachments() {
        return this.totalAttachments;
    }

    public void setLatestVersionsOfAttachments(List<Attachment> latestVersionsOfAttachments) {
        this.latestVersionsOfAttachments = latestVersionsOfAttachments;
    }

    public List<Attachment> getLatestVersionsOfAttachments() {
        return this.latestVersionsOfAttachments;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public void setAllowFilterByFileExtension(boolean allowFilterByFileExtension) {
        this.allowFilterByFileExtension = allowFilterByFileExtension;
    }

    public boolean isAllowFilterByFileExtension() {
        return this.allowFilterByFileExtension;
    }

    public boolean isSpaceRequired() {
        return true;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setShowFilter(boolean showFilter) {
        this.showFilter = showFilter;
    }

    public boolean isShowFilter() {
        return this.showFilter;
    }

    private String formatFileExtension(String fileExtension) {
        if (StringUtils.isNotBlank((CharSequence)fileExtension) && fileExtension.startsWith(".")) {
            fileExtension = fileExtension.substring(fileExtension.lastIndexOf(".") + 1);
        }
        return fileExtension;
    }

    public PaginationSupport<Attachment> getPaginationSupport() {
        return this.paginationSupport;
    }

    public String getLabelFilter() {
        return this.labelFilter;
    }

    public void setLabelFilter(String labelFilter) {
        this.labelFilter = labelFilter;
    }

    public boolean isShowAttachmentsNotFound() {
        return this.showAttachmentsNotFound;
    }

    public void setShowAttachmentsNotFound(boolean showAttachmentsNotFound) {
        this.showAttachmentsNotFound = showAttachmentsNotFound;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public String[] getMessageParameter() {
        return this.messageParameter;
    }

    private void setMessages() {
        if (StringUtils.isNotBlank((CharSequence)this.getFileExtension()) && StringUtils.isNotBlank((CharSequence)this.getLabelFilter())) {
            this.messageKey = "attachments.no.attachments.with.label.and.ending.with.ext";
            this.messageParameter = new String[]{GeneralUtil.htmlEncode((String)this.getFileExtension()), GeneralUtil.htmlEncode((String)this.getLabelFilter())};
        } else if (StringUtils.isNotBlank((CharSequence)this.getFileExtension())) {
            this.messageKey = "attachments.no.attachments.ending.with.ext";
            this.messageParameter = new String[]{GeneralUtil.htmlEncode((String)this.getFileExtension())};
        } else if (StringUtils.isNotBlank((CharSequence)this.getLabelFilter())) {
            this.messageKey = "attachments.no.attachments.with.label";
            this.messageParameter = new String[]{GeneralUtil.htmlEncode((String)this.getLabelFilter())};
        } else {
            this.messageKey = "attachments.no.attachments.to.space";
            this.messageParameter = new String[]{GeneralUtil.htmlEncode((String)this.space.getKey())};
        }
    }

    public static List<String> getLabelsForFilter(String labels, LabelManager labelManager) {
        ArrayList<String> labelsList = new ArrayList<String>();
        if (labels != null) {
            for (String labelString : LabelUtil.split((String)labels)) {
                if (StringUtils.isBlank((CharSequence)labelString)) continue;
                ParsedLabelName labelName = LabelParser.parse((String)labelString, (User)AuthenticatedUserThreadLocal.get());
                Label label = labelManager.getLabel(labelName);
                if (label != null) {
                    labelsList.add(label.getName());
                    continue;
                }
                return Collections.emptyList();
            }
        }
        return labelsList;
    }
}

