/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.Category
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.Directory;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;

final class DirectoryPredicates {
    private DirectoryPredicates() {
    }

    static Predicate<Directory.Entry> inCategory(Category categoryToMatch) {
        return new InCategoryPredicate((Category)Preconditions.checkNotNull((Object)categoryToMatch));
    }

    private static final class InCategoryPredicate
    implements Predicate<Directory.Entry> {
        private final Category categoryToMatch;

        public InCategoryPredicate(Category categoryToMatch) {
            this.categoryToMatch = categoryToMatch;
        }

        public boolean apply(@Nullable Directory.Entry entry) {
            return entry != null && entry.getCategories().contains(this.categoryToMatch);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof InCategoryPredicate)) {
                return false;
            }
            InCategoryPredicate predicateObj = (InCategoryPredicate)obj;
            if (this.categoryToMatch != null) {
                return this.categoryToMatch.equals((Object)predicateObj.categoryToMatch);
            }
            return predicateObj.categoryToMatch == null;
        }

        public int hashCode() {
            return this.categoryToMatch.hashCode();
        }

        public String toString() {
            return "inCategory(" + this.categoryToMatch + ")";
        }
    }
}

