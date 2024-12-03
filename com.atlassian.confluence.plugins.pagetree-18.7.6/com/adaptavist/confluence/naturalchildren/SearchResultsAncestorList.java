/*
 * Decompiled with CFR 0.152.
 */
package com.adaptavist.confluence.naturalchildren;

import com.adaptavist.confluence.naturalchildren.AncestorList;

class SearchResultsAncestorList
extends AncestorList {
    SearchResultsAncestorList(String[] ancestors) {
        for (int i = 1; i < ancestors.length; ++i) {
            this.addAncestor(Long.parseLong(ancestors[i]));
        }
        if (ancestors.length > 0) {
            this.addAncestor(Long.parseLong(ancestors[0]));
        }
    }
}

