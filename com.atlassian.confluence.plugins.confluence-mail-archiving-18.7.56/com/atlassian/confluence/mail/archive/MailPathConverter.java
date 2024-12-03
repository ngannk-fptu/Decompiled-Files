/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.servlet.simpledisplay.ConvertedPath
 *  com.atlassian.confluence.servlet.simpledisplay.PathConverter
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailPathConverter
implements PathConverter {
    private Pattern pattern = Pattern.compile("^/?(\\p{Alnum}*)/mail/([\\d]*)/?");
    public static final String VIEW_MAIL = "/mail/archive/viewmail.action";

    public boolean handles(String simplePath) {
        return this.pattern.matcher(simplePath).matches();
    }

    public ConvertedPath getPath(String path) {
        Matcher m = this.pattern.matcher(path);
        String spaceKey = "";
        String mailId = "";
        if (m.matches()) {
            spaceKey = m.group(1);
            mailId = m.group(2);
        }
        ConvertedPath convertedPath = new ConvertedPath(VIEW_MAIL);
        convertedPath.addParameter("key", spaceKey);
        convertedPath.addParameter("id", mailId);
        return convertedPath;
    }
}

