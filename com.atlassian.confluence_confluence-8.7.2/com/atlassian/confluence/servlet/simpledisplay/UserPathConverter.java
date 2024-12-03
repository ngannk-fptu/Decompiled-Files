/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;

public class UserPathConverter
implements PathConverter {
    private static final String DISPLAY_USER_PATH = "/users/viewuserprofile.action";

    @Override
    public boolean handles(String simplePath) {
        return simplePath.startsWith("~") && simplePath.indexOf("/") == -1;
    }

    @Override
    public ConvertedPath getPath(String path) {
        String userName = path.substring(1);
        ConvertedPath convertedPath = new ConvertedPath(DISPLAY_USER_PATH);
        convertedPath.addParameter("username", userName);
        return convertedPath;
    }
}

