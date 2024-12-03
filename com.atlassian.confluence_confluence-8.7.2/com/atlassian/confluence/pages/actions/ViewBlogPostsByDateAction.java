/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.DateUtils$DateRange
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Sets
 *  org.apache.commons.collections.map.ListOrderedMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostsCalendar;
import com.atlassian.confluence.pages.actions.AbstractBlogPostsAction;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogPostDateBreadcrumb;
import com.atlassian.core.util.DateUtils;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Sets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

@RequiresAnyConfluenceAccess
public class ViewBlogPostsByDateAction
extends AbstractBlogPostsAction
implements BreadcrumbAware {
    private String postingDate;
    private Calendar postingDay;
    private int period = 5;
    private static final int POSTS_PER_PAGE = 15;
    private int currentPage = 1;
    private boolean isOldestPage = false;
    private List blogPosts;
    private Map aggregatedBlogPosts;
    private BlogPost nextPost;
    private BlogPost previousPost;
    private BlogPost nextDatePost;
    private BlogPost previousDatePost;
    private BlogPostsCalendar calendar;
    private BreadcrumbGenerator breadcrumbGenerator;
    private SearchManager searchManager;

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        try {
            this.calculatePostingDay();
            this.blogPosts = this.calculateViewingBlogs();
            return super.execute();
        }
        catch (ParseException e) {
            this.addActionError("date.notvalid", this.postingDate);
            return "error";
        }
    }

    private void calculatePostingDay() throws ParseException {
        TimeZone userTimeZone = this.getTimeZone().getWrappedTimeZone();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy/MM/dd");
        parser.setTimeZone(userTimeZone);
        this.postingDay = Calendar.getInstance(userTimeZone);
        this.postingDay.setTime(parser.parse(this.postingDate));
        DateUtils.toStartOfPeriod((Calendar)this.postingDay, (int)this.period);
    }

    @Override
    public void validate() {
        super.validate();
        if (this.getSpace() == null) {
            this.addActionError(this.getText("space.doesnt.exist"));
        }
    }

    public BlogPostsCalendar getCalendarForThisMonth() {
        if (this.postingDay == null) {
            return null;
        }
        if (this.calendar == null) {
            this.calendar = new BlogPostsCalendar(this.postingDay.getTime(), this.pageManager.getBlogPosts(this.getKey(), this.postingDay, 2), this.getKey(), this.getDateFormatter());
            this.calendar.setFirstPostInNextMonth(this.getFirstPostInNextMonth(this.postingDay));
            this.calendar.setLastPostInPreviousMonth(this.getLastPostInPreviousMonth(this.postingDay));
        }
        return this.calendar;
    }

    public List getBlogPosts() {
        if (this.blogPosts == null) {
            this.blogPosts = Collections.emptyList();
        }
        return this.blogPosts;
    }

    public Map getAggregatedBlogPosts() {
        if (this.aggregatedBlogPosts == null) {
            this.aggregatedBlogPosts = new ListOrderedMap();
            ArrayList reversedBlogs = new ArrayList();
            Collections.copy(this.getBlogPosts(), reversedBlogs);
            for (BlogPost blogPost : reversedBlogs) {
                List<BlogPost> temp;
                String dateString = this.getDateString(blogPost.getCreationDate());
                if (this.aggregatedBlogPosts.get(dateString) == null) {
                    temp = new ArrayList<BlogPost>();
                    temp.add(blogPost);
                    this.aggregatedBlogPosts.put(dateString, temp);
                    continue;
                }
                temp = (List)this.aggregatedBlogPosts.get(dateString);
                temp.add(blogPost);
            }
        }
        return this.aggregatedBlogPosts;
    }

    private String getDateString(Date date) {
        return DateFormat.getDateInstance(0).format(date);
    }

    public String getPostingDate() {
        return this.postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public Calendar getPostingDay() {
        return this.postingDay;
    }

    public String getPostingDayOfMonth() {
        return this.postingDay != null ? new SimpleDateFormat("dd").format(this.postingDay.getTime()) : "";
    }

    public String getPostingYear() {
        return this.postingDay != null ? new SimpleDateFormat("yyyy").format(this.postingDay.getTime()) : "";
    }

    public String getPostingMonthNumeric() {
        return this.postingDay != null ? new SimpleDateFormat("MM").format(this.postingDay.getTime()) : "";
    }

    public String getPostingMonth() {
        return this.getPostingMonth(null);
    }

    public String getPostingMonth(DateFormatter dateFormatter) {
        if (this.postingDay == null) {
            return "";
        }
        if (dateFormatter != null) {
            return dateFormatter.formatGivenString("MMMM", this.postingDay.getTime());
        }
        return new SimpleDateFormat("MMMM").format(this.postingDay.getTime());
    }

    public BlogPost getNextPost() {
        if (this.nextPost == null) {
            int blogPostsSize = this.getBlogPosts().size();
            if (blogPostsSize > 0) {
                BlogPost lastPost = (BlogPost)this.getBlogPosts().get(blogPostsSize - 1);
                this.nextPost = this.pageManager.findNextBlogPost(lastPost);
            } else {
                this.nextPost = this.pageManager.findNextBlogPost(this.getKey(), this.postingDay.getTime());
            }
        }
        return this.nextPost;
    }

    public BlogPost getNextDatePost() {
        if (this.nextDatePost == null && this.getNextPost() != null) {
            this.nextDatePost = this.getNextPost();
            while (this.compareToPostingDay(this.nextDatePost.getCreationDate())) {
                BlogPost temp = this.pageManager.findNextBlogPost(this.nextDatePost);
                if (temp != null && temp != this.nextDatePost) {
                    this.nextDatePost = temp;
                    continue;
                }
                this.nextDatePost = null;
                break;
            }
        }
        return this.nextDatePost;
    }

    public BlogPost getPreviousPost() {
        if (this.previousPost == null) {
            if (this.getBlogPosts().size() > 0) {
                BlogPost firstPost = (BlogPost)this.getBlogPosts().get(0);
                this.previousPost = this.pageManager.findPreviousBlogPost(firstPost);
            } else {
                this.previousPost = this.pageManager.findPreviousBlogPost(this.getKey(), this.postingDay.getTime());
            }
        }
        return this.previousPost;
    }

    public BlogPost getPreviousDatePost() {
        if (this.previousDatePost == null && this.getPreviousPost() != null) {
            this.previousDatePost = this.getPreviousPost();
            while (this.compareToPostingDay(this.previousDatePost.getCreationDate())) {
                BlogPost temp = this.pageManager.findPreviousBlogPost(this.previousDatePost);
                if (temp != null && temp != this.previousDatePost) {
                    this.previousDatePost = temp;
                    continue;
                }
                this.previousDatePost = null;
                break;
            }
        }
        return this.previousDatePost;
    }

    private boolean compareToPostingDay(Date date) {
        Calendar cal = BlogPost.toCalendar(date);
        return this.postingDay.get(1) == cal.get(1) && this.postingDay.get(2) == cal.get(2) && this.postingDay.get(5) == cal.get(5);
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getFormattedDateRangeDescription() {
        if (this.postingDay == null) {
            return "";
        }
        if (this.period == 5) {
            return this.getDateFormatter().formatServerDate(this.postingDay.getTime());
        }
        Calendar localCalendar = Calendar.getInstance(this.getDateFormatter().getTimeZone().getWrappedTimeZone());
        GeneralUtil.copyDate(this.postingDay, localCalendar);
        return this.getDateFormatter().formatGivenString("MMMMMMMMM, yyyy", localCalendar.getTime());
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        Breadcrumb parent = this.breadcrumbGenerator.getBlogCollectorBreadcrumb(this.space);
        DateFormatter dateFormatter = this.getDateFormatter();
        return new BlogPostDateBreadcrumb(this.getSpace(), this.getPostingYear(), this.getPostingMonthNumeric(), this.getPostingMonth(dateFormatter), this.getPostingDayOfMonth(), parent);
    }

    public boolean isBlogPostsByDateAction() {
        return true;
    }

    private SearchQuery makeSearchQuery(Calendar postingDay, int period) {
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)postingDay, (int)period);
        DateRangeQuery.DateRange dateRange = new DateRangeQuery.DateRange(range.startDate, range.endDate, true, false);
        HashSet searchTerms = Sets.newHashSet();
        searchTerms.add(new ContentTypeQuery(ContentTypeEnum.BLOG));
        searchTerms.add(new DateRangeQuery(dateRange, DateRangeQuery.DateRangeQueryType.CREATED));
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        if (!this.userAccessor.isSuperUser(remoteUser)) {
            ContentPermissionsQuery contentPermissionsQuery = ContentPermissionsQuery.builder().user(remoteUser).groupNames(this.userAccessor.getGroupNames(remoteUser)).build();
            searchTerms.add(contentPermissionsQuery);
        }
        queryBuilder.addMust(searchTerms);
        String spaceKey = this.getSpaceKey();
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            queryBuilder.addFilter(new InSpaceQuery(Collections.singleton(spaceKey)));
        }
        return queryBuilder.build();
    }

    private List calculateViewingBlogs() throws InvalidSearchException {
        this.currentPage = Math.max(this.currentPage, 1);
        int startIndex = (this.currentPage - 1) * 15;
        ContentSearch contentSearch = new ContentSearch(this.makeSearchQuery(this.postingDay, this.period), CreatedSort.DESCENDING, startIndex, 16);
        SearchResults searchResults = this.searchManager.search(contentSearch);
        List<Searchable> resultObjects = this.searchManager.convertToEntities(searchResults, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        if (!resultObjects.isEmpty()) {
            this.blogPosts = resultObjects.subList(0, Math.min(resultObjects.size(), 15));
        }
        if (resultObjects.size() <= 15) {
            this.isOldestPage = true;
        }
        return this.blogPosts;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isOldestPage() {
        return this.isOldestPage;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
}

