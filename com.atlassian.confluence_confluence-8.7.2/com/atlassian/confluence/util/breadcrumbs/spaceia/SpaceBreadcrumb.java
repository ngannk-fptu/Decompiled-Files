/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.util.breadcrumbs.spaceia;

import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractSpaceBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.CompositeBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PeopleBreadcrumb;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.util.List;

public class SpaceBreadcrumb
extends AbstractSpaceBreadcrumb {
    public SpaceBreadcrumb(Space space) {
        super(space);
        if (space != null) {
            this.title = space.getName();
            this.displayTitle = space.getName();
            this.target = this.getSpaceUrl(space);
        } else {
            this.title = "space-undefined";
        }
    }

    private String getSpaceUrl(Space space) {
        if (space.isPersonal()) {
            return UserProfileLink.getLinkPath(space.getCreatorName());
        }
        return space.getUrlPath();
    }

    @Override
    protected Breadcrumb getParent() {
        if (this.space != null && this.space.isPersonal()) {
            return PeopleBreadcrumb.getInstance();
        }
        return DashboardBreadcrumb.getInstance();
    }

    public Breadcrumb concatWith(Breadcrumb original) {
        if (original == null) {
            return this;
        }
        List<Breadcrumb> originalTrail = original.getBreadcrumbsTrail();
        if (this.contains(originalTrail, Predicates.or((Predicate)Predicates.instanceOf(com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb.class), (Predicate)Predicates.instanceOf(SpaceBreadcrumb.class)))) {
            return original;
        }
        return new CompositeBreadcrumb(originalTrail, this);
    }

    private <T> boolean contains(Iterable<T> iterable, Predicate<? super T> matcher) {
        return !Iterables.isEmpty((Iterable)Iterables.filter(iterable, matcher));
    }
}

