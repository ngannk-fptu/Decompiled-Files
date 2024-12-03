/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.core.util.PairType
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.opensymphony.xwork2.Preparable
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.dashboard.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.internal.search.SpacePickerHelper;
import com.atlassian.confluence.rss.FeedCustomContentType;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedOrAnonymousConfluenceAccess;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.CompleteURLEncoder;
import com.atlassian.core.util.PairType;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.opensymphony.xwork2.Preparable;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

@RequiresLicensedOrAnonymousConfluenceAccess
public class ConfigureRssFeedAction
extends ConfluenceActionSupport
implements Preparable,
FormAware {
    private static final String CREATE_RSSFEED_URL = "/createrssfeed.action?";
    private ContentEntityManager contentEntityManager;
    private SpaceManager spaceManager;
    private String title;
    private List spaces;
    private List excludedSpaceKeys;
    private boolean created;
    private boolean modified;
    private int maxResults = 10;
    private int timeSpan = 5;
    private String rssType = "atom";
    private String sort = "modified";
    private boolean publicFeed;
    private boolean showContent;
    private String rssLink;
    private static final String ALL_SPACES = "";
    private SpacePickerHelper spacePickerHelper;
    private PluginAccessor pluginAccessor;
    private List<String> types;
    private List<String> blogSubTypes;
    private List<String> pageSubTypes;

    public void prepare() {
        this.spacePickerHelper = new SpacePickerHelper(this.spaceManager, this.labelManager);
    }

    @Override
    public void validate() {
        super.validate();
        if (CollectionUtils.isEmpty((Collection)this.spaces)) {
            this.addFieldError("spaces", this.getText("no.spaces.selected"));
        }
        if (CollectionUtils.isEmpty(this.types) && CollectionUtils.isEmpty(this.pageSubTypes) && CollectionUtils.isEmpty(this.blogSubTypes)) {
            this.addFieldError("types", this.getText("no.content.type.selected"));
        }
        if (this.maxResults <= 0 || this.maxResults > this.getGlobalSettings().getMaxRssItems()) {
            this.addFieldError("maxResults", "rss.feed.items.outofrange", new String[]{String.valueOf(this.getGlobalSettings().getMaxRssItems())});
        }
    }

    @Override
    public String doDefault() throws Exception {
        this.setTitle(this.getText("rss.feed", new Object[]{this.getGlobalSettings().getSiteTitle()}));
        return super.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.setRssLink(this.createRssLink());
        return "success";
    }

    private String createRssLink() {
        StringBuilder rssString = new StringBuilder();
        rssString.append(this.settingsManager.getGlobalSettings().getBaseUrl());
        rssString.append(CREATE_RSSFEED_URL);
        rssString.append(ServletActionContext.getRequest().getQueryString());
        String encoding = ServletActionContext.getRequest().getCharacterEncoding();
        if (!this.showContent) {
            rssString.append("&showContent=false");
        }
        if (!this.publicFeed) {
            String authType = "&" + SecurityConfigFactory.getInstance().getAuthType() + "=basic";
            rssString.append(authType);
        }
        try {
            String unencodedString = URLDecoder.decode(rssString.toString(), encoding);
            return CompleteURLEncoder.encode(unencodedString, "UTF-8");
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to convert [ " + rssString + " ] to a URL", e);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported encoding", e);
        }
    }

    public ContentEntityManager getContentEntityManager() {
        return this.contentEntityManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public List<PairType> getSubContentEntityTypes() {
        ArrayList<PairType> result = new ArrayList<PairType>();
        result.add(new PairType((Serializable)((Object)"comment"), (Serializable)((Object)this.getText("list.element.comment"))));
        if (!this.isWebdavEnabled()) {
            result.add(new PairType((Serializable)((Object)"attachment"), (Serializable)((Object)this.getText("list.element.attachment"))));
        }
        return result;
    }

    public List<PairType> getContentStatusList() {
        ArrayList<PairType> result = new ArrayList<PairType>();
        result.add(new PairType((Serializable)((Object)"created"), (Serializable)((Object)this.getText("list.element.created"))));
        result.add(new PairType((Serializable)((Object)"modified"), (Serializable)((Object)this.getText("list.element.modified"))));
        return result;
    }

    public List<PairType> getRssTypes() {
        ArrayList<PairType> result = new ArrayList<PairType>();
        result.add(new PairType((Serializable)((Object)"rss1"), (Serializable)((Object)this.getText("rss.type.rss1"))));
        result.add(new PairType((Serializable)((Object)"rss2"), (Serializable)((Object)this.getText("rss.type.rss2"))));
        result.add(new PairType((Serializable)((Object)"atom"), (Serializable)((Object)this.getText("rss.type.atom"))));
        return result;
    }

    public List<PairType> getRssSorts() {
        ArrayList<PairType> result = new ArrayList<PairType>();
        result.add(new PairType((Serializable)((Object)"created"), (Serializable)((Object)this.getText("list.element.created"))));
        result.add(new PairType((Serializable)((Object)"modified"), (Serializable)((Object)this.getText("list.element.modified"))));
        return result;
    }

    public List<PairType> getAccessTypes() {
        ArrayList<PairType> result = new ArrayList<PairType>();
        result.add(new PairType((Serializable)((Object)String.valueOf(false)), (Serializable)((Object)this.getText("access.private"))));
        result.add(new PairType((Serializable)((Object)String.valueOf(true)), (Serializable)((Object)this.getText("access.public"))));
        return result;
    }

    public String getNiceRssType() {
        String type = this.getRssType();
        for (PairType pairType : this.getRssTypes()) {
            if (!pairType.getKey().equals(type)) continue;
            return (String)((Object)pairType.getValue());
        }
        return this.getText("rss.type.rss1");
    }

    public String getSpacesAsNiceList(List spaceList) {
        String spaceListString = spaceList == null ? this.getText("description.feed.nospaces") : (spaceList.contains(ALL_SPACES) ? this.getText("description.feed.allspaces") : StringUtils.join(spaceList.iterator(), (String)", "));
        return spaceListString;
    }

    public List<SpacePickerHelper.SpaceDTO> getAvailableGlobalSpaces() {
        return this.spacePickerHelper.getAvailableGlobalSpaces(this.getAuthenticatedUser());
    }

    public List<PairType> getAggregateOptions() {
        return this.spacePickerHelper.getAggregateOptions(this);
    }

    public List<SpacePickerHelper.SpaceDTO> getFavouriteSpaces() {
        return this.spacePickerHelper.getFavouriteSpaces(this.getAuthenticatedUser());
    }

    public String getContentListAsString() {
        if (CollectionUtils.isEmpty(this.types)) {
            return ALL_SPACES;
        }
        ArrayList<String> textList = new ArrayList<String>();
        for (String type : this.types) {
            FeedCustomContentType customContentType = this.getCustomContentType(type);
            textList.add(customContentType == null ? this.getText("list.element." + type) : this.getText(customContentType.getI18nKey()));
        }
        return StringUtils.join(textList.iterator(), (String)", ");
    }

    public FeedCustomContentType getCustomContentType(String identifier) {
        for (FeedCustomContentType customContentType : this.pluginAccessor.getEnabledModulesByClass(FeedCustomContentType.class)) {
            if (!customContentType.getIdentifier().equals(identifier)) continue;
            return customContentType;
        }
        return null;
    }

    public List<FeedCustomContentType> getCustomContentTypes() {
        ArrayList<FeedCustomContentType> types = new ArrayList<FeedCustomContentType>(this.pluginAccessor.getEnabledModulesByClass(FeedCustomContentType.class));
        Collections.sort(types, (type1, type2) -> type1.getIdentifier().compareTo(type2.getIdentifier()));
        return types;
    }

    public boolean isWebdavEnabled() {
        return false;
    }

    public boolean hasAnonymousAccess() {
        return this.permissionManager.hasPermission(null, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }

    public List getSpaces() {
        return this.spaces;
    }

    public void setSpaces(List spaces) {
        this.spaces = spaces;
    }

    public List getExcludedSpaceKeys() {
        return this.excludedSpaceKeys;
    }

    public void setExcludedSpaceKeys(List excludedSpaceKeys) {
        this.excludedSpaceKeys = excludedSpaceKeys;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public boolean isModified() {
        return this.modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTimeSpan() {
        return this.timeSpan;
    }

    public void setTimeSpan(int timeSpan) {
        this.timeSpan = timeSpan;
    }

    public boolean isCreated() {
        return this.created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public void setBlogpostSubTypes(List<String> blogSubTypes) {
        this.blogSubTypes = blogSubTypes;
    }

    public void setPageSubTypes(List<String> pageSubTypes) {
        this.pageSubTypes = pageSubTypes;
    }

    public String getRssLink() {
        return this.rssLink;
    }

    public void setRssLink(String rssLink) {
        this.rssLink = rssLink;
    }

    public String getRssType() {
        return this.rssType;
    }

    public void setRssType(String rssType) {
        this.rssType = rssType;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean isPublicFeed() {
        return this.publicFeed;
    }

    public void setPublicFeed(boolean publicFeed) {
        this.publicFeed = publicFeed;
    }

    public String getSelectedPublicFeed() {
        return String.valueOf(this.publicFeed);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public boolean isShowContent() {
        return this.showContent;
    }

    public void setShowContent(boolean showContent) {
        this.showContent = showContent;
    }

    @Override
    public boolean isEditMode() {
        return true;
    }
}

