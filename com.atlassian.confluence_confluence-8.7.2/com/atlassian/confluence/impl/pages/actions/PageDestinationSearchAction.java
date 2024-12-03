/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.event.events.search.SearchPerformedEvent;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.event.Event;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageDestinationSearchAction
extends ConfluenceActionSupport
implements Beanable {
    private static final Logger log = LoggerFactory.getLogger(PageDestinationSearchAction.class);
    private static final int MAX_RESULTS_PER_PAGE = 50;
    private static final Set<ContentTypeEnum> RESULT_TYPES = new HashSet<ContentTypeEnum>(2);
    private String query;
    private String where;
    private int startIndex;
    private LuceneSearchMapper searchMapper;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private SearchManager searchManager;
    private DestinationSearchResults destinationResults;

    @Override
    public void validate() {
        super.validate();
        if (StringUtils.isBlank((CharSequence)this.query)) {
            this.addFieldError("query", this.getText("error.missing.search.term"));
            return;
        }
        if (this.query.startsWith("*")) {
            this.addFieldError("query", this.getText("error.star.cannot.lead"));
            return;
        }
        try {
            this.searchMapper.convertToLuceneQuery(new TextFieldQuery(SearchFieldNames.CONTENT, this.query, BooleanOperator.AND));
        }
        catch (IllegalArgumentException ex) {
            log.debug("Error parsing the query.", (Throwable)ex);
            this.addFieldError("query", this.getText("error.invalid.search.term"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        SearchResults searchResults;
        ISearch search = this.createSearch();
        try {
            searchResults = this.searchManager.search(search);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid search specified", ex);
        }
        catch (InvalidSearchException e) {
            log.warn("Failure executing search for term {}", (Object)this.query);
            return "error";
        }
        this.eventManager.publishEvent((Event)new SearchPerformedEvent(this, search.getQuery(), AuthenticatedUserThreadLocal.get(), searchResults.size()));
        this.destinationResults = this.createDestinationSearchResult(searchResults);
        return "success";
    }

    private ISearch createSearch() {
        SearchQueryParameters params = new SearchQueryParameters(this.query);
        SpaceCategoryEnum spaceCategory = SpaceCategoryEnum.get(this.where);
        if (spaceCategory != null) {
            params.setCategory(spaceCategory);
        } else if (!StringUtils.isBlank((CharSequence)this.where)) {
            params.setSpaceKey(this.where);
        }
        params.setContentTypes(RESULT_TYPES);
        return this.predefinedSearchBuilder.buildSiteSearch(params, this.startIndex, 50);
    }

    private DestinationSearchResults createDestinationSearchResult(SearchResults results) {
        ArrayList<IndividualDestinationSearchResult> destinationResultList = new ArrayList<IndividualDestinationSearchResult>(results.getAll().size());
        for (SearchResult result : results) {
            try {
                destinationResultList.add(new IndividualDestinationSearchResult(result));
            }
            catch (SearchResultConversionException ex) {
                log.warn("Not all of the results for the search '{}' could be processed. Exception: {} ", (Object)this.query, (Object)ex.getMessage());
            }
        }
        DestinationSearchResults destinationSearchResult = new DestinationSearchResults(destinationResultList, this.query);
        return destinationSearchResult;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String queryString) {
        this.query = queryString;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setSearchMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
    }

    public void setPredefinedSearchBuilder(PredefinedSearchBuilder predefinedSearchBuilder) {
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    private String getFriendlyDate(Date date) {
        FriendlyDateFormatter dateFormatter = new FriendlyDateFormatter(this.getDateFormatter());
        Message formatMessage = dateFormatter.getFormatMessage(date);
        return this.getText(formatMessage.getKey(), formatMessage.getArguments());
    }

    @Override
    public Object getBean() {
        return this.destinationResults;
    }

    static {
        RESULT_TYPES.add(ContentTypeEnum.PAGE);
        RESULT_TYPES.add(ContentTypeEnum.SPACE_DESCRIPTION);
        RESULT_TYPES.add(ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION);
    }

    private static class SearchResultConversionException
    extends Exception {
        public SearchResultConversionException(String message) {
            super(message);
        }
    }

    public class IndividualDestinationSearchResult {
        private String type;
        private String id;
        private String name;
        private String spaceName;
        private String spaceKey;
        private String date;

        private IndividualDestinationSearchResult(SearchResult result) throws SearchResultConversionException {
            if (!(result.getHandle() instanceof HibernateHandle)) {
                throw new SearchResultConversionException("The search result did not contain a handle which an id can be extracted from.");
            }
            HibernateHandle handle = (HibernateHandle)result.getHandle();
            this.id = String.valueOf(handle.getId());
            this.type = result.getType();
            this.name = result.getDisplayTitle();
            this.spaceName = result.getSpaceName();
            this.spaceKey = result.getSpaceKey();
            this.date = PageDestinationSearchAction.this.getFriendlyDate(result.getLastModificationDate());
        }

        public String getType() {
            return this.type;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getSpaceName() {
            return this.spaceName;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public String getDate() {
            return this.date;
        }
    }

    public static class DestinationSearchResults {
        private List<IndividualDestinationSearchResult> matches;
        private String query;

        public DestinationSearchResults(List<IndividualDestinationSearchResult> matches, String query) {
            this.matches = matches;
            this.query = query;
        }

        public List<IndividualDestinationSearchResult> getMatches() {
            return this.matches;
        }

        public String getQuery() {
            return this.query;
        }
    }
}

