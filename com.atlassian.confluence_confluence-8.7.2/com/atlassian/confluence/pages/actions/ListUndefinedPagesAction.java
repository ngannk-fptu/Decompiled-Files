/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.opensymphony.xwork2.ActionContext
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.content.page.PageListViewEvent;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPaginatedListAction;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedOrAnonymousConfluenceAccess;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

@RequiresLicensedOrAnonymousConfluenceAccess
public class ListUndefinedPagesAction
extends AbstractPaginatedListAction<UndefinedPage>
implements Evented<PageListViewEvent> {
    private static String cachingEnablingItemNumberVariable = "CachingEnablingItemNumber";
    private static String cachingEnablingTimeoutVariable = "CachingEnablingItemTimeout";
    protected int cachingEnablingItemNumber = Integer.getInteger(cachingEnablingItemNumberVariable, 1000);
    protected int cachingEnablingTimeout = Integer.getInteger(cachingEnablingTimeoutVariable, 300000);
    private int cacheTimeout;
    private boolean disableCaching;
    private PageManager pageManager;

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return super.execute();
    }

    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public void setCacheTimeout(int cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public List getUndefinedPages() {
        return this.paginationSupport.getItems();
    }

    @Override
    public PageListViewEvent getEventToPublish(String result) {
        return new PageListViewEvent(this, this.getSpace(), "undefined");
    }

    @Override
    public List getItems() {
        Supplier<List<UndefinedPage>> resultSupplier = () -> {
            try (Ticker ignored = Timers.start((String)"ListUndefinedPagesAction.getItems");){
                List<OutgoingLink> links = this.getPermittedEntitiesOf(this.pageManager.getUndefinedLinks(this.key));
                HashMap<String, UndefinedPage> condensedLinks = new HashMap<String, UndefinedPage>();
                for (OutgoingLink outgoingLink : links) {
                    String destinationPageTitle = outgoingLink.getDestinationPageTitle();
                    if (StringUtils.isEmpty((CharSequence)destinationPageTitle) || destinationPageTitle.contains("/")) continue;
                    String mapKey = (outgoingLink.getDestinationSpaceKey() + ":" + destinationPageTitle).toLowerCase();
                    UndefinedPage undefPage = condensedLinks.computeIfAbsent(mapKey, k -> new UndefinedPage(outgoingLink.getDestinationSpaceKey(), outgoingLink.getDestinationPageTitle()));
                    undefPage.addReferenceFrom(outgoingLink);
                }
                ArrayList undefinedLinks = Lists.newArrayList(condensedLinks.values());
                undefinedLinks.sort(null);
                ArrayList arrayList = undefinedLinks;
                return arrayList;
            }
        };
        if (this.paginationSupport.getStartIndex() == 0) {
            ActionContext.getContext().getSession().remove(ListUndefinedPagesAction.class.getName());
        }
        if (this.cacheTimeout == 0) {
            this.cacheTimeout = this.cachingEnablingTimeout;
        }
        if (this.disableCaching) {
            return resultSupplier.get();
        }
        return this.cacheResult(this.cacheTimeout, this.cachingEnablingItemNumber, new Date().getTime(), resultSupplier, ActionContext.getContext().getSession());
    }

    protected List<UndefinedPage> cacheResult(long cachingEnablingTimeout, int cachingEnablingItemNumber, long currentTime, Supplier<List<UndefinedPage>> resultSupplier, Map<String, Object> cacheStorage) {
        CacheableUndefinedLinksResult cacheableUndefinedLinksResult = (CacheableUndefinedLinksResult)cacheStorage.get(ListUndefinedPagesAction.class.getName());
        if (cacheableUndefinedLinksResult != null) {
            List<UndefinedPage> cachedResult = cacheableUndefinedLinksResult.undefinedLinks;
            long lastCachingTime = cacheableUndefinedLinksResult.cachingTime;
            if (lastCachingTime + cachingEnablingTimeout < currentTime) {
                cachedResult = this.prepareNewCache(cachingEnablingItemNumber, currentTime, resultSupplier, cacheStorage);
            }
            return cachedResult;
        }
        return this.prepareNewCache(cachingEnablingItemNumber, currentTime, resultSupplier, cacheStorage);
    }

    protected List<UndefinedPage> prepareNewCache(int cachingEnablingItemNumber, long currentTime, Supplier<List<UndefinedPage>> resultSupplier, Map<String, Object> cacheStorage) {
        cacheStorage.remove(ListUndefinedPagesAction.class.getName());
        CacheableUndefinedLinksResult cacheableUndefinedLinksResult = new CacheableUndefinedLinksResult();
        List<UndefinedPage> cachedResult = resultSupplier.get();
        if (cachedResult != null && cachedResult.size() >= cachingEnablingItemNumber) {
            cacheableUndefinedLinksResult.cachingTime = currentTime;
            cacheableUndefinedLinksResult.undefinedLinks = cachedResult;
            cacheStorage.put(ListUndefinedPagesAction.class.getName(), cacheableUndefinedLinksResult);
        }
        return cachedResult;
    }

    public int getMaxReferencesPerUndefinedPage() {
        return 5;
    }

    public static class CacheableUndefinedLinksResult {
        public List<UndefinedPage> undefinedLinks;
        public long cachingTime;
    }

    public static class UndefinedPage
    implements Comparable<UndefinedPage> {
        public static final int MAX_REFERENCES = 5;
        private final String destinationSpaceKey;
        private final String destinationPageTitle;
        private final LinkedHashSet<ContentEntityObject> referencedFrom = Sets.newLinkedHashSet();
        private int numReferences = 0;

        public UndefinedPage(String spaceKey, String pageTitle) {
            this.destinationSpaceKey = spaceKey;
            this.destinationPageTitle = pageTitle;
        }

        public String getDestinationSpaceKey() {
            return this.destinationSpaceKey;
        }

        public String getDestinationPageTitle() {
            return this.destinationPageTitle;
        }

        public void addReferenceFrom(OutgoingLink link) {
            boolean isNewReference = true;
            if (this.numReferences < 5) {
                isNewReference = this.referencedFrom.add(link.getSourceContent());
            }
            if (isNewReference) {
                ++this.numReferences;
            }
        }

        public List<ContentEntityObject> getReferencedFrom() {
            return ImmutableList.copyOf(this.referencedFrom);
        }

        public int getNumReferences() {
            return this.numReferences;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UndefinedPage that = (UndefinedPage)o;
            if (this.destinationPageTitle != null ? !this.destinationPageTitle.equals(that.destinationPageTitle) : that.destinationPageTitle != null) {
                return false;
            }
            return !(this.destinationSpaceKey != null ? !this.destinationSpaceKey.equals(that.destinationSpaceKey) : that.destinationSpaceKey != null);
        }

        public int hashCode() {
            int result = this.destinationSpaceKey != null ? this.destinationSpaceKey.hashCode() : 0;
            result = 29 * result + (this.destinationPageTitle != null ? this.destinationPageTitle.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(UndefinedPage other) {
            int i = this.destinationPageTitle.compareTo(other.destinationPageTitle);
            return i == 0 ? this.destinationSpaceKey.compareTo(other.destinationSpaceKey) : i;
        }
    }
}

