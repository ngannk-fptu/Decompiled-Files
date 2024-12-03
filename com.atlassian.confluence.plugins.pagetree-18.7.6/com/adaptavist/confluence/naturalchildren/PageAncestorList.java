/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 */
package com.adaptavist.confluence.naturalchildren;

import com.adaptavist.confluence.naturalchildren.AncestorList;
import com.atlassian.confluence.pages.Page;
import java.util.List;

class PageAncestorList
extends AncestorList {
    PageAncestorList(List<Page> ancestors, Page currentPage) {
        for (Page ancestor : ancestors) {
            this.addAncestor(ancestor.getId());
        }
        this.addAncestor(currentPage.getId());
    }
}

