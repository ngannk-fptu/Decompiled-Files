/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import com.atlassian.confluence.util.GeneralUtil;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class BlogPathConverter
implements PathConverter {
    private static final String DISPLAY_BLOG_POST_PATH = "/pages/viewpage.action";
    private static final String DISPLAY_BLOG_DATEVIEW_PATH = "/pages/viewblogposts.action";

    @Override
    public boolean handles(String simplePath) {
        StringTokenizer st = new StringTokenizer(simplePath, "/");
        if (st.countTokens() < 3 || st.countTokens() > 5) {
            return false;
        }
        BlogPathBean bpb = this.getBlogPath(st);
        return this.validatePath(bpb);
    }

    @Override
    public ConvertedPath getPath(String path) {
        BlogPathBean bpb = this.getBlogPath(new StringTokenizer(path, "/"));
        String postingDate = bpb.getYear() + "/" + bpb.getMonth() + "/" + (bpb.getDay() > 0 ? bpb.getDay() : 1);
        String spaceKey = bpb.getSpaceKey();
        String blogTitle = bpb.getTitle();
        if (StringUtils.isNotBlank((CharSequence)blogTitle)) {
            ConvertedPath convertedPath = new ConvertedPath(DISPLAY_BLOG_POST_PATH);
            convertedPath.addParameter("spaceKey", spaceKey);
            convertedPath.addParameter("title", blogTitle);
            convertedPath.addParameter("postingDay", postingDate);
            return convertedPath;
        }
        String period = Integer.toString(bpb.getDay() < 0 ? 2 : 5);
        ConvertedPath convertedPath = new ConvertedPath(DISPLAY_BLOG_DATEVIEW_PATH);
        convertedPath.addParameter("key", spaceKey);
        convertedPath.addParameter("postingDate", postingDate);
        convertedPath.addParameter("period", period);
        return convertedPath;
    }

    private BlogPathBean getBlogPath(StringTokenizer st) {
        String spaceKey = st.nextToken();
        int year = this.getInt(st);
        int month = this.getInt(st);
        int day = -1;
        if (st.hasMoreTokens()) {
            day = this.getInt(st);
        }
        String blogTitle = "";
        if (st.hasMoreTokens()) {
            blogTitle = st.nextToken();
        }
        return new BlogPathBean(spaceKey, year, month, day, blogTitle);
    }

    private boolean validatePath(BlogPathBean bpb) {
        if (bpb.getMonth() < 1 || bpb.getMonth() > 12) {
            return false;
        }
        if (bpb.getDay() != -1 && (bpb.getDay() < 0 || bpb.getDay() > 31)) {
            return false;
        }
        return !StringUtils.isNotEmpty((CharSequence)bpb.getTitle()) || GeneralUtil.isAllAscii(bpb.getTitle());
    }

    private int getInt(StringTokenizer st) {
        try {
            return Integer.parseInt(st.nextToken());
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    private static class BlogPathBean {
        private String spaceKey;
        private int year;
        private int month;
        private int day;
        private String title;

        public BlogPathBean(String spaceKey, int year, int month, int day, String title) {
            this.spaceKey = spaceKey;
            this.year = year;
            this.month = month;
            this.day = day;
            this.title = title;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public int getYear() {
            return this.year;
        }

        public int getMonth() {
            return this.month;
        }

        public int getDay() {
            return this.day;
        }

        public String getTitle() {
            return this.title;
        }
    }
}

