/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import java.util.StringTokenizer;

public class PagePathConverter
implements PathConverter {
    private static final String DISPLAY_PAGE_PATH = "/pages/viewpage.action";

    @Override
    public boolean handles(String simplePath) {
        return new StringTokenizer(simplePath, "/").countTokens() == 2;
    }

    @Override
    public ConvertedPath getPath(String path) {
        StringTokenizer st = new StringTokenizer(path, "/");
        String spaceKey = st.nextToken();
        String pageTitle = st.nextToken();
        ConvertedPath convertedPath = new ConvertedPath(DISPLAY_PAGE_PATH);
        convertedPath.addParameter("spaceKey", spaceKey);
        convertedPath.addParameter("title", pageTitle);
        return convertedPath;
    }
}

