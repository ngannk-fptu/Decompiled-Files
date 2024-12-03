/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.util.GeneralUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BlogPost
extends AbstractPage
implements ContentConvertible {
    public static final String CONTENT_TYPE = "blogpost";
    public static final String POSTING_DAY_FORMAT = "yyyy/MM/dd";
    public static final String POSTING_DATE = "PostingDate";

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    public static Calendar toCalendar(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getCalendarFromDatePath(String datePath) {
        if (datePath.charAt(0) != '/') {
            return null;
        }
        try {
            Date date = new SimpleDateFormat(POSTING_DAY_FORMAT).parse(datePath.substring(1));
            return BlogPost.toCalendar(date);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getTitleFromDatePath(String datePath) {
        return datePath.substring(datePath.lastIndexOf(47) + 1);
    }

    public String getLinkPart() {
        return "/" + this.getDatePath() + "/" + this.getTitle();
    }

    public String getDatePath() {
        return BlogPost.toDatePath(this.getCreationDate());
    }

    public static String toDatePath(Date date) {
        return new SimpleDateFormat(POSTING_DAY_FORMAT).format(date);
    }

    @Override
    public String getLinkWikiMarkup() {
        return String.format("[%s:/%s/%s]", this.getSpaceKey(), this.getDatePath(), this.getTitle());
    }

    public String getPostingYear() {
        return this.formatPostingDate("yyyy");
    }

    public String getPostingMonth() {
        return this.formatPostingDate("MMMM");
    }

    public String getPostingMonth(DateFormatter formatter) {
        if (formatter == null) {
            return this.getPostingMonth();
        }
        return this.formatInternationalisedPostingDate("MMMM", formatter);
    }

    public String getPostingMonthNumeric() {
        return this.formatPostingDate("MM");
    }

    public String getPostingDayOfMonth() {
        return this.formatPostingDate("dd");
    }

    private String formatPostingDate(String format) {
        if (this.getCreationDate() == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(this.getCreationDate());
    }

    private String formatInternationalisedPostingDate(String format, DateFormatter formatter) {
        if (this.getCreationDate() == null) {
            return null;
        }
        Calendar serverCalendar = Calendar.getInstance();
        serverCalendar.setTime(this.getCreationDate());
        Calendar localCalendar = Calendar.getInstance(formatter.getTimeZone().getWrappedTimeZone());
        GeneralUtil.copyDate(serverCalendar, localCalendar);
        return formatter.formatGivenString(format, localCalendar.getTime());
    }

    public Date getPostingDate() {
        return this.getCreationDate();
    }

    public Calendar getPostingCalendarDate() {
        Calendar result = Calendar.getInstance();
        result.setTime(this.getCreationDate());
        return result;
    }

    @Override
    public ContentType getContentTypeObject() {
        return ContentType.BLOG_POST;
    }

    @Override
    public ContentId getContentId() {
        return ContentId.of((ContentType)ContentType.BLOG_POST, (long)this.getId());
    }

    @Override
    public boolean shouldConvertToContent() {
        return true;
    }
}

