/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.mobile.model.card.Card;
import com.atlassian.confluence.plugins.mobile.search.CardSearchBuilder;
import com.atlassian.confluence.plugins.mobile.service.CardService;
import com.atlassian.confluence.plugins.mobile.service.factory.card.CardFactory;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardServiceImpl
implements CardService {
    private final CardFactory cardFactory;
    private final SearchManager searchManager;
    private final LabelManager labelManager;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private static final String ALL_SPACES_EXPANSION = "allSpaces";
    private static final String FAVOURITE_SPACES_EXPANSION = "favouriteSpaces";

    @Autowired
    public CardServiceImpl(CardFactory cardFactory, @ComponentImport SearchManager searchManager, @ComponentImport LabelManager labelManager, @ComponentImport SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.cardFactory = cardFactory;
        this.searchManager = searchManager;
        this.labelManager = labelManager;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    @Override
    public CardService.CardFinder find() {
        return new CardFinderImpl();
    }

    public static class CardPageResponse
    implements PageResponse<Card> {
        private List<Card> cards;
        private boolean hasMore;
        private PageRequest pageRequest;

        private CardPageResponse(Builder builder) {
            this.cards = builder.cards;
            this.hasMore = builder.hasMore;
            this.pageRequest = builder.pageRequest;
        }

        public static Builder builder() {
            return new Builder();
        }

        public List<Card> getResults() {
            return this.cards;
        }

        public int size() {
            return this.cards.size();
        }

        public boolean hasMore() {
            return this.hasMore;
        }

        public PageRequest getPageRequest() {
            return this.pageRequest;
        }

        public Iterator<Card> iterator() {
            return this.cards.iterator();
        }

        private static final class Builder {
            private List<Card> cards;
            private boolean hasMore;
            private PageRequest pageRequest;

            private Builder() {
            }

            public Builder cards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public Builder hasMore(boolean hasMore) {
                this.hasMore = hasMore;
                return this;
            }

            public Builder pageRequest(PageRequest pageRequest) {
                this.pageRequest = pageRequest;
                return this;
            }

            public PageResponse<Card> build() {
                return new CardPageResponse(this);
            }
        }
    }

    private class CardFinderImpl
    implements CardService.CardFinder {
        private String spaceKey;
        private Expansions expansions;

        private CardFinderImpl() {
        }

        @Override
        public CardService.CardFinder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        @Override
        public CardService.CardFinder expand(Expansions expansions) {
            this.expansions = expansions;
            return this;
        }

        public PageResponse<Card> fetchMany(PageRequest pageRequest) {
            Set<String> filterSpaces = this.getFilterSpaceKeys();
            CardPageResponse.Builder builder = CardPageResponse.builder().pageRequest(pageRequest).hasMore(false);
            if (!AuthenticatedUserThreadLocal.isAnonymousUser() && (this.expansions.canExpand(CardServiceImpl.ALL_SPACES_EXPANSION) || this.expansions.canExpand(CardServiceImpl.FAVOURITE_SPACES_EXPANSION) || StringUtils.isNotBlank((CharSequence)this.spaceKey))) {
                builder.cards(CardServiceImpl.this.cardFactory.build(this.getPageUpdateResults(pageRequest, filterSpaces)));
            }
            return builder.build();
        }

        private List<SearchResult> getPageUpdateResults(PageRequest pageRequest, Set<String> filterSpaces) {
            try {
                ISearch search = new CardSearchBuilder().withSpaceKeys(filterSpaces).withFilterQuery(CardServiceImpl.this.siteSearchPermissionsQueryFactory.create()).buildSearch(pageRequest);
                return CardServiceImpl.this.searchManager.search(search).getAll();
            }
            catch (InvalidSearchException e) {
                throw new ServiceException();
            }
        }

        private Set<String> getFilterSpaceKeys() {
            if (this.expansions.canExpand(CardServiceImpl.FAVOURITE_SPACES_EXPANSION)) {
                List spaces = CardServiceImpl.this.labelManager.getFavouriteSpaces(AuthenticatedUserThreadLocal.get().getName());
                Set<String> spaceKeys = spaces.stream().map(Space::getKey).collect(Collectors.toSet());
                return spaceKeys.isEmpty() && this.expansions.canExpand(CardServiceImpl.ALL_SPACES_EXPANSION) ? null : spaceKeys;
            }
            if (StringUtils.isNotBlank((CharSequence)this.spaceKey)) {
                return Arrays.stream(this.spaceKey.split(",")).collect(Collectors.toSet());
            }
            return null;
        }
    }
}

