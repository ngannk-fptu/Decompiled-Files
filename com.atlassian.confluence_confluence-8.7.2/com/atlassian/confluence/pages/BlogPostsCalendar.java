/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.util.DateUtils
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.util.DateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogPostsCalendar {
    private final Map<Integer, List<BlogPost>> postsPerDay = new HashMap<Integer, List<BlogPost>>();
    private Calendar calendar;
    private String spaceKey;
    private BlogPost lastPostInPreviousMonth;
    private BlogPost firstPostInNextMonth;
    private DateFormatter formatter;

    public BlogPostsCalendar(Date coversDate, List<BlogPost> blogPosts, String spaceKey, DateFormatter formatter) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(coversDate);
        this.calendar = DateUtils.toStartOfPeriod((Calendar)cal, (int)2);
        this.spaceKey = spaceKey;
        this.formatter = formatter;
        for (BlogPost blogPost : blogPosts) {
            int i = this.getDayOfMonth(blogPost);
            if (i < 1) continue;
            this.postsPerDay.computeIfAbsent(i, k -> new ArrayList());
            this.postsPerDay.get(i).add(blogPost);
        }
        for (Map.Entry entry : this.postsPerDay.entrySet()) {
            ((List)entry.getValue()).sort(null);
        }
    }

    private int getDayOfMonth(BlogPost post) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(post.getCreationDate());
        if (cal.get(2) == this.calendar.get(2)) {
            return cal.get(5);
        }
        return -1;
    }

    public int getDaysInMonth() {
        return this.calendar.getActualMaximum(5);
    }

    public int getStartingDayOfMonth() {
        return this.calendar.get(7) - 1;
    }

    public List<BlogPost> getPostsForDay(int dayOfMonth) {
        return this.postsPerDay.getOrDefault(dayOfMonth, Collections.emptyList());
    }

    public List<BlogPost> getPostsForMonth() {
        ArrayList<BlogPost> monthPosts = new ArrayList<BlogPost>();
        for (int i = 1; i <= this.getDaysInMonth(); ++i) {
            monthPosts.addAll(this.getPostsForDay(i));
        }
        monthPosts.sort(Comparator.comparing(EntityObject::getCreationDate));
        return monthPosts;
    }

    public String formatMonthYear() {
        Calendar localCalendar = Calendar.getInstance(this.formatter.getTimeZone().getWrappedTimeZone());
        GeneralUtil.copyDate(this.calendar, localCalendar);
        return this.formatter.formatGivenString("MMMM yyyy", localCalendar.getTime());
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getPreviousMonth() {
        return this.getRelativeYearMonth(-1);
    }

    public String getNextMonth() {
        return this.getRelativeYearMonth(1);
    }

    public String getCurrentMonth() {
        return this.getRelativeYearMonth(0);
    }

    private String getRelativeYearMonth(int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(this.calendar.getTime());
        c.add(2, offset);
        return this.formatter.formatGivenString("yyyy/MM", c.getTime());
    }

    public void setLastPostInPreviousMonth(BlogPost lastPostInPreviousMonth) {
        this.lastPostInPreviousMonth = lastPostInPreviousMonth;
    }

    public void setFirstPostInNextMonth(BlogPost firstPostInNextMonth) {
        this.firstPostInNextMonth = firstPostInNextMonth;
    }

    public BlogPost getLastPostInPreviousMonth() {
        return this.lastPostInPreviousMonth;
    }

    public BlogPost getFirstPostInNextMonth() {
        return this.firstPostInNextMonth;
    }
}

