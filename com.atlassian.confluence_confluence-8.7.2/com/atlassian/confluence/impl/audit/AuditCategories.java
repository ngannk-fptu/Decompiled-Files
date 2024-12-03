/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.impl.audit.AuditHelper;

public final class AuditCategories {
    private static final String PREFIX = AuditHelper.buildTextKey("category.");
    public static final String SPACES = PREFIX + "spaces";
    public static final String IMPORT_EXPORT = PREFIX + "import.export";
    public static final String USER_MANAGEMENT = PREFIX + "user.management";
    public static final String PERMISSIONS = PREFIX + "permissions";
    public static final String SECURITY = PREFIX + "security";
    public static final String PLUGINS = PREFIX + "plugins";
    public static final String ADMIN = PREFIX + "admin";
    public static final String PAGE_TEMPLATES = PREFIX + "page.templates";
    public static final String AUTH = PREFIX + "auth";
    public static final String PAGES = PREFIX + "pages";
    public static final String SYSTEM = PREFIX + "system";
    public static final String REINDEX = PREFIX + "reindex";

    private AuditCategories() {
    }
}

