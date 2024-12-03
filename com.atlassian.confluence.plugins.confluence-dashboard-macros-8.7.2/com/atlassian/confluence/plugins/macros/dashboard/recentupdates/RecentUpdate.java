/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.util.RendererUtil
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.serialisers.SearchResultSerialiser;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.util.RendererUtil;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonAutoDetect
public class RecentUpdate {
    private static final int MAX_SUMMARY_LENGTH = 120;
    private SearchResult searchResult;
    private long id;
    private String contentType;
    private String spaceName;
    private String title;
    private String urlPath;
    private String summary;
    private Date lastModificationDate;
    private String friendlyUpdateTime;
    private String updateDescription;
    private String viewChangesLink;
    private String thumbnailUrl;
    private String thumbnailWidth;
    private String thumbnailHeight;
    private String imageUrl;
    private String imageWidth;
    private String imageHeight;
    private String iconCss;
    private String friendlyContentType;
    private String html;

    public RecentUpdate(SearchResult result, FriendlyDateFormatter dateFormatter, String contextPath, ContentUiSupport contentUiSupport, I18NBeanFactory i18NBeanFactory) {
        this.searchResult = result;
        this.id = RecentUpdate.idFromHandle(result.getHandle());
        this.contentType = result.getType();
        this.spaceName = result.getSpaceName();
        this.title = this.searchResult.getDisplayTitle();
        this.urlPath = contextPath + result.getUrlPath();
        this.summary = this.getSummaryFromSearchResult();
        this.lastModificationDate = result.getLastModificationDate();
        this.friendlyUpdateTime = this.getFriendlyTimeFromSearchResult(dateFormatter);
        this.updateDescription = this.getUpdateDescriptionFromSearchResult();
        this.viewChangesLink = this.getViewChangesLinkFromSearchResult(contextPath);
        if (StringUtils.isNotBlank((CharSequence)result.getField("imgUrl"))) {
            this.imageUrl = contextPath + result.getField("imgUrl");
            this.imageWidth = result.getField("imgWidth");
            this.imageHeight = result.getField("imgHeight");
            this.thumbnailUrl = result.getField("thumbUrl");
            this.thumbnailHeight = result.getField("thumbHeight");
            this.thumbnailWidth = result.getField("thumbWidth");
        }
        I18NBean i18nBean = i18NBeanFactory.getI18NBean();
        this.iconCss = contentUiSupport.getIconCssClass(result);
        this.friendlyContentType = i18nBean.getText(contentUiSupport.getContentTypeI18NKey(result));
        this.html = this.renderSearchResultToHtml();
    }

    private static long idFromHandle(Handle handle) {
        if (handle instanceof HibernateHandle) {
            return ((HibernateHandle)handle).getId();
        }
        throw new IllegalArgumentException("Expected HibernateHandle but got " + handle.getClass().getSimpleName());
    }

    private static boolean isAbstractPageType(String contentType) {
        return "page".equals(contentType) || "blogpost".equals(contentType);
    }

    private static boolean isSpaceDescriptionType(String contentType) {
        return "spacedesc".equals(contentType) || "personalspacedesc".equals(contentType);
    }

    private String getSummaryFromSearchResult() {
        String summary = "comment".equals(this.contentType) ? this.searchResult.getField("excerpt") : RendererUtil.stripBasicMarkup((String)this.searchResult.getLastUpdateDescription());
        if (StringUtils.isBlank((CharSequence)summary)) {
            return null;
        }
        return GeneralUtil.shortenString((String)summary, (int)120);
    }

    private String getFriendlyTimeFromSearchResult(FriendlyDateFormatter dateFormatter) {
        return GeneralUtil.getI18n().getText(dateFormatter.getFormatMessage(this.searchResult.getLastModificationDate()));
    }

    private String getUpdateDescriptionFromSearchResult() {
        String updateDescriptionI18nKey = "update.item.desc.generic";
        if (RecentUpdate.isAbstractPageType(this.contentType) || RecentUpdate.isSpaceDescriptionType(this.contentType)) {
            updateDescriptionI18nKey = this.searchResult.getContentVersion() > 1 ? "update.item.desc.updated" : "update.item.desc.created";
        } else if ("userinfo".equals(this.contentType)) {
            updateDescriptionI18nKey = "update.item.desc.profile";
        } else if ("attachment".equals(this.contentType)) {
            updateDescriptionI18nKey = "update.item.desc.attachment";
        } else if ("comment".equals(this.contentType)) {
            updateDescriptionI18nKey = "update.item.desc.comment";
        }
        return GeneralUtil.getI18n().getText(updateDescriptionI18nKey, new Object[]{this.friendlyUpdateTime});
    }

    private String getViewChangesLinkFromSearchResult(String contextPath) {
        if (!RecentUpdate.isAbstractPageType(this.contentType) || this.searchResult.getContentVersion() <= 1) {
            return null;
        }
        return contextPath + "/pages/diffpagesbyversion.action?pageId=" + this.id + "&selectedPageVersions=" + this.searchResult.getContentVersion() + "&selectedPageVersions=" + (this.searchResult.getContentVersion() - 1);
    }

    private String renderSearchResultToHtml() {
        if (this.urlPath != null) {
            return "<a href=\"" + GeneralUtil.htmlEncode((String)this.urlPath) + "\">" + GeneralUtil.htmlEncode((String)this.title) + "</a>";
        }
        return HtmlUtil.htmlEncode((String)this.title);
    }

    public long getId() {
        return this.id;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public String getUrlPath() {
        return this.urlPath;
    }

    public String getTitle() {
        return this.title;
    }

    public String getFriendlyUpdateTime() {
        return this.friendlyUpdateTime;
    }

    public String getUpdateDescription() {
        return this.updateDescription;
    }

    public String getViewChangesLink() {
        return this.viewChangesLink;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public boolean isThumbnailable() {
        return StringUtils.isNotBlank((CharSequence)this.imageUrl);
    }

    @JsonSerialize(using=SearchResultSerialiser.class)
    public SearchResult getEntity() {
        return this.searchResult;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailWidth() {
        return this.thumbnailWidth;
    }

    public String getThumbnailHeight() {
        return this.thumbnailHeight;
    }

    public void setThumbnailHeight(String thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getImageWidth() {
        return this.imageWidth;
    }

    public String getImageHeight() {
        return this.imageHeight;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getHtml() {
        return this.html;
    }

    public String getIconCss() {
        return this.iconCss;
    }

    public String getFriendlyContentType() {
        return this.friendlyContentType;
    }
}

