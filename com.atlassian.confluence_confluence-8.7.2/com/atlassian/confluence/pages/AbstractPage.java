/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.EnsuresNonNullIf
 */
package com.atlassian.confluence.pages;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.HasLinkWikiMarkup;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;

public abstract class AbstractPage
extends SpaceContentEntityObject
implements HasLinkWikiMarkup {
    private static final int MAX_PAGE_TITLE_LENGTH = 255;

    @EnsuresNonNullIf(expression={"title"}, result=true)
    public static boolean isValidPageTitle(String title) {
        return AbstractPage.isValidTitleLength(title);
    }

    public static boolean isValidTitleLength(String title) {
        return title != null && title.length() <= 255;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(StringUtils.left((String)title, (int)255));
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.setAttachments(new ArrayList<Attachment>());
    }

    public AbstractPage getOriginalVersionPage() {
        return this.isLatestVersion() ? null : this.getLatestVersion();
    }

    public void setOriginalVersionPage(AbstractPage originalVersionPage) {
        this.setOriginalVersion(originalVersionPage);
    }

    public int getPreviousVersion() {
        return this.getVersion() - 1;
    }

    public void remove(PageManager pageManager) {
        pageManager.removeContentEntity(this);
    }

    @Override
    public Collection<Searchable> getSearchableDependants() {
        return ImmutableList.copyOf((Iterable)Iterables.concat(this.getComments(), this.getAttachmentManager().getLatestVersionsOfAttachmentsWithAnyStatus(this)));
    }

    @Override
    public String getUrlPath() {
        if (this.isLatestVersion() && this.getSpace() == null) {
            return "";
        }
        boolean useReadableUrl = this.getDarkFeatureManager().map(darkFeatureManager -> darkFeatureManager.isEnabledForAllUsers("confluence.readable.url").orElse(Boolean.FALSE)).orElse(false);
        return useReadableUrl ? this.toUnicodePageUrl() : this.toLegacyPageUrl();
    }

    private Optional<DarkFeatureManager> getDarkFeatureManager() {
        if (!ContainerManager.isContainerSetup()) {
            return Optional.empty();
        }
        return Optional.ofNullable((DarkFeatureManager)ContainerManager.getComponent((String)"salDarkFeatureManager", DarkFeatureManager.class));
    }

    private String toLegacyPageUrl() {
        boolean showDisplayUrl;
        boolean bl = showDisplayUrl = this.isLatestVersion() && !"blogpost".equalsIgnoreCase(this.getType()) && UrlUtils.isSafeTitleForUrl(this.getTitle());
        if (showDisplayUrl) {
            String displayUrl = "/display/" + HtmlUtil.urlEncode(this.getSpace().getKey()) + "/" + HtmlUtil.urlEncode(this.getTitle());
            return displayUrl;
        }
        return this.getIdBasedPageUrl();
    }

    public String getIdBasedPageUrl() {
        return "/pages/viewpage.action?pageId=" + this.getId();
    }

    private String toUnicodePageUrl() {
        if (!this.isLatestVersion()) {
            return this.getIdBasedPageUrl();
        }
        StringBuilder displayUrl = new StringBuilder("/spaces/");
        displayUrl.append(HtmlUtil.urlEncode(this.getSpace().getKey()));
        displayUrl.append("/");
        ContentTypeEnum contentType = this.getTypeEnum();
        if (contentType == ContentTypeEnum.BLOG) {
            displayUrl.append("blog/");
            if (this.getCreationDate() != null) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                displayUrl.append(df.format(this.getCreationDate()));
                displayUrl.append("/");
            }
        } else if (contentType == ContentTypeEnum.PAGE) {
            displayUrl.append("pages/");
        }
        displayUrl.append(this.getIdAsString());
        String titleSlug = this.generateTitleSlug(this.getTitle());
        if (!titleSlug.isEmpty()) {
            displayUrl.append("/");
            displayUrl.append(titleSlug);
        }
        return displayUrl.toString();
    }

    private String generateTitleSlug(String title) {
        if (title == null) {
            return "";
        }
        String slug = title.replaceAll("[\\p{Punct}&&[^.~_-]]+", " ").replaceAll("\\s+", " ").trim();
        if (".".equals(slug) || "..".equals(slug)) {
            return "";
        }
        return HtmlUtil.urlEncode(slug);
    }

    public String getEditUrlPath() {
        return GeneralUtil.getEditPageUrl(this);
    }

    public List<Comment> getPageLevelComments() {
        ArrayList<Comment> pageLevelComments = new ArrayList<Comment>();
        for (Comment comment : this.getComments()) {
            if (comment.isInlineComment()) continue;
            pageLevelComments.add(comment);
        }
        return pageLevelComments;
    }

    public List<Comment> getTopLevelComments() {
        ArrayList<Comment> topLevelComments = new ArrayList<Comment>();
        for (Comment comment : this.getComments()) {
            if (comment.getParent() != null || comment.isInlineComment()) continue;
            topLevelComments.add(comment);
        }
        return topLevelComments;
    }

    @Override
    public String getAttachmentsUrlPath() {
        return "/pages/viewpageattachments.action?pageId=" + this.getId();
    }

    @Override
    public String getAttachmentUrlPath(Attachment attachment) {
        return GeneralUtil.getAttachmentUrl(attachment);
    }

    @Override
    public AbstractPage getLatestVersion() {
        return (AbstractPage)super.getLatestVersion();
    }

    @Deprecated
    public void setContentPropertiesFromDraft(Draft draft) {
        super.setContentPropertiesFrom(draft);
    }

    @Override
    public String getConfluenceRevision() {
        Object uuidPart = "";
        if (!StringUtils.isBlank((CharSequence)super.getCollaborativeEditingUuid())) {
            uuidPart = "$" + super.getCollaborativeEditingUuid();
        }
        return "confluence$content$" + this.getId() + (String)uuidPart + "." + super.getConfluenceRevision();
    }
}

