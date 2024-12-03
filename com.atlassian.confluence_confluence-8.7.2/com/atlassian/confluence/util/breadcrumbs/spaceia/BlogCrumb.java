/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs.spaceia;

import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;

public class BlogCrumb
extends SimpleBreadcrumb {
    public BlogCrumb(String i18nKey, String path) {
        super(i18nKey, path);
    }

    public BlogCrumb(String i18nKey, String path, Breadcrumb parent) {
        super(i18nKey, path, parent);
    }
}

