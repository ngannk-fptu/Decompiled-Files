/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 */
package com.benryan.conversion;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;

public interface ConverterHelper {
    public static final String NAME_KEY = "name";
    public static final String FULLNAME_KEY = "fullname";
    public static final String PAGE_KEY = "page";
    public static final String SPACE_KEY = "space";
    public static final String TYPE_KEY = "type";
    public static final String DATE_KEY = "date";
    public static final String ATTACHMENT_KEY = "attachment";
    public static final String CONTEXT_KEY = "context";
    public static final String ATTACHMENTOBJ_KEY = "attachmentObj";
    public static final String EDITURL_KEY = "editUrl";
    public static final String BASEURL_KEY = "baseUrl";
    public static final String SERVLET_BASE_URL = "servletBaseUrl";
    public static final String PAGE_ID_KEY = "pageID";
    public static final String ALLOW_EDIT_KEY = "isNews";
    public static final String USE_JAVASCRIPT = "useJavascript";
    public static final String RESOURCE_KEY = "resourcePath";
    public static final String USE_PATHAUTH = "usePathAuth";
    public static final String EDITGRID_FILE_KEY = "filename";

    public Map<String, Object> validateArguments(Map var1, ConversionContext var2) throws MacroExecutionException;
}

