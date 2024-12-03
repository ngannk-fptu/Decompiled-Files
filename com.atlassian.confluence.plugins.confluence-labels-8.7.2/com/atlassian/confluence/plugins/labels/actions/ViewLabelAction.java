/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatterHelper
 *  com.atlassian.confluence.labels.CombinedLabel
 *  com.atlassian.confluence.labels.DisplayableLabel
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.confluence.search.v2.sort.CreatedSort
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.LabelUtil
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.MoreObjects
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.plugins.labels.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatterHelper;
import com.atlassian.confluence.labels.CombinedLabel;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugins.labels.actions.AbstractLabelDisplayingAction;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class ViewLabelAction
extends AbstractLabelDisplayingAction {
    public static final int ITEMS_PER_PAGE = 10;
    private static final int MAX_RELATED_LABELS_PER_LABEL = 100;
    private static final Comparator<DisplayableLabel> LABEL_TITLE_COMPARATOR = (label1, label2) -> label1.getRealTitle().compareTo(label2.getRealTitle());
    private long[] ids;
    private List<Label> labels;
    protected PaginationSupport paginationSupport = new PaginationSupport(10);
    private List related = new LinkedList();
    private List content = new LinkedList();
    private SearchManager searchManager;
    private FriendlyDateFormatterHelper friendlyDateFormatterHelper;
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private DisplayableLabel combinedLabel;
    private String friendlyTitle;
    private String description;
    private int startIndex;

    public void setIds(long[] ids) {
        this.ids = ids;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        this.getPaginationSupport().setStartIndex(startIndex);
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public List getLabels() {
        return this.labels;
    }

    public List getContent() {
        return this.content;
    }

    public List getRelated() {
        return this.related;
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public void validate() {
        if (ArrayUtils.isEmpty((long[])this.ids)) {
            this.getPaginationSupport().setItems(Collections.EMPTY_LIST);
            this.addActionError(this.getText("error.no.label"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.hasErrors()) {
            return "error";
        }
        this.friendlyDateFormatterHelper = new FriendlyDateFormatterHelper(this.getFriendlyDateFormatter(), this.i18NBeanFactory, this.getLocaleManager());
        this.labels = this.retrieveLabels();
        if (this.labels.size() == 0) {
            this.getPaginationSupport().setItems(Collections.EMPTY_LIST);
            ServletActionContext.getResponse().sendError(404);
            return "error";
        }
        SearchResults searchResults = this.getSearchResults();
        this.getPaginationSupport().setTotal(searchResults.getUnfilteredResultsCount());
        this.content = this.searchManager.convertToEntities(searchResults, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        this.related = this.getLabelManager().getRelatedLabels(this.labels, (String)MoreObjects.firstNonNull((Object)this.getSpaceKey(), (Object)""), 100);
        return "success";
    }

    private SearchResults getSearchResults() {
        HashSet<Object> searchQueries = new HashSet<Object>();
        if (this.getSpace() != null) {
            searchQueries.add(new InSpaceQuery(this.getSpace().getKey()));
        }
        for (DisplayableLabel displayableLabel : this.labels) {
            searchQueries.add(new LabelQuery(displayableLabel.getRealTitle()));
        }
        searchQueries.add(new ContentTypeQuery(this.toContentTypeEnums(LabelManager.CONTENT_TYPES)));
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        searchQueryBuilder.addMust(searchQueries);
        searchQueryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        ContentSearch contentSearch = new ContentSearch(searchQueryBuilder.build(), (SearchSort)CreatedSort.DESCENDING, this.startIndex, 10);
        try {
            return this.searchManager.search((ISearch)contentSearch);
        }
        catch (InvalidSearchException e) {
            throw new IllegalStateException("Unable to perform label search: " + contentSearch, e);
        }
    }

    private Set<ContentTypeEnum> toContentTypeEnums(List<String> contentTypes) {
        HashSet<ContentTypeEnum> enums = new HashSet<ContentTypeEnum>();
        for (String contentType : contentTypes) {
            enums.add(ContentTypeEnum.getByRepresentation((String)contentType));
        }
        return enums;
    }

    private List<Label> retrieveLabels() {
        ArrayList<Label> labels = new ArrayList<Label>();
        if (!ArrayUtils.isEmpty((long[])this.ids)) {
            for (long id : this.ids) {
                Label label = this.getLabelManager().getLabel(id);
                if (label == null) continue;
                labels.add(label);
            }
        }
        return labels;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }

    public List<DisplayableLabel> getOtherLabels(Labelable content) {
        ArrayList<DisplayableLabel> otherLabels = new ArrayList<DisplayableLabel>();
        for (Label label : content.getLabels()) {
            if (this.labels.contains(label) || !Namespace.GLOBAL.equals((Object)label.getNamespace())) continue;
            otherLabels.add((DisplayableLabel)label);
            if (otherLabels.size() < 8) continue;
            break;
        }
        return otherLabels;
    }

    public DisplayableLabel getLabel() {
        if (this.combinedLabel == null && this.labels != null && this.labels.size() > 0) {
            this.combinedLabel = new CombinedLabel(this.labels);
        }
        return this.combinedLabel;
    }

    public DisplayableLabel getAddLabel(Label label) {
        ArrayList<Label> labelList = new ArrayList<Label>(this.labels);
        labelList.add(label);
        Collections.sort(labelList, LABEL_TITLE_COMPARATOR);
        return new CombinedLabel(labelList);
    }

    public DisplayableLabel getRemoveLabel(Label label) {
        ArrayList<Label> labelList = new ArrayList<Label>(this.labels);
        labelList.remove(label);
        Collections.sort(labelList, LABEL_TITLE_COMPARATOR);
        return new CombinedLabel(labelList);
    }

    @HtmlSafe
    public String getPaginationUrl() {
        String idParams = "ids=" + LabelUtil.joinIds(this.labels, (String)"&ids=");
        Object spaceParam = "";
        String spaceKey = this.getSpaceKey();
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            spaceParam = "&key=" + HtmlUtil.urlEncode((String)spaceKey);
        }
        return "?" + idParams + (String)spaceParam + "&";
    }

    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext context = DefaultWebInterfaceContext.copyOf((WebInterfaceContext)super.getWebInterfaceContext());
        context.setLabel(this.getLabel());
        return context;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FriendlyDateFormatterHelper getFriendlyDateFormatterHelper() {
        return this.friendlyDateFormatterHelper;
    }

    public SiteSearchPermissionsQueryFactory getSiteSearchPermissionsQueryFactory() {
        return this.siteSearchPermissionsQueryFactory;
    }

    public void setSiteSearchPermissionsQueryFactory(SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }
}

