/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class SpacePathConverter
implements PathConverter {
    public static final String DISPLAY_SPACE_HOME_PAGE_PATH = "/spaces/viewspace.action";

    @Override
    public boolean handles(String simplePath) {
        Pattern pattern = Pattern.compile("^/?\\p{Alnum}*/?$");
        return pattern.matcher(simplePath).matches();
    }

    @Override
    public ConvertedPath getPath(String path) {
        String spaceKey = path;
        if (StringUtils.isNotBlank((CharSequence)path) && path.endsWith("/")) {
            spaceKey = path.substring(0, path.length() - 1);
        }
        ConvertedPath convertedPath = new ConvertedPath(DISPLAY_SPACE_HOME_PAGE_PATH);
        convertedPath.addParameter("key", spaceKey);
        return convertedPath;
    }
}

