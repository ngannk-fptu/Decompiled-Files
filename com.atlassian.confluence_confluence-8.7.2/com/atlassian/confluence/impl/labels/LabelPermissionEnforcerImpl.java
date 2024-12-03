/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.core.util.filter.Filter
 *  com.atlassian.core.util.filter.FilterChain
 *  com.atlassian.core.util.filter.ListFilter
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.labels;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.NamespaceLabelFilter;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.PermittedLabelView;
import com.atlassian.confluence.labels.SpecialLabelFilter;
import com.atlassian.confluence.labels.VisibleLabelFilter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.filter.Filter;
import com.atlassian.core.util.filter.FilterChain;
import com.atlassian.core.util.filter.ListFilter;
import com.atlassian.user.User;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LabelPermissionEnforcerImpl
implements LabelPermissionEnforcer {
    private final PermissionManager permissionManager;
    private final AccessModeService accessModeService;

    public LabelPermissionEnforcerImpl(PermissionManager permissionManager, AccessModeService accessModeService) {
        this.permissionManager = permissionManager;
        this.accessModeService = accessModeService;
    }

    @Override
    public boolean isLabelableByUser(Labelable object) {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.EDIT, object) || user != null && this.permissionManager.hasPermission((User)user, Permission.VIEW, object);
    }

    @Override
    public boolean userCanEditLabel(ParsedLabelName ref, Labelable object) {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        if (object instanceof PermittedLabelView) {
            return this.userCanEditLabel(ref, ((PermittedLabelView)object).getDelegate());
        }
        if (!this.userCanViewObject(object)) {
            return false;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return !(ref.getOwner() == null ? !this.permissionManager.hasPermission((User)user, Permission.EDIT, object) : user == null || !this.permissionManager.hasPermission((User)user, Permission.VIEW, object));
    }

    @Override
    public boolean userCanEditLabel(Label label, Labelable object) {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        if (object instanceof PermittedLabelView) {
            return this.userCanEditLabel(label, ((PermittedLabelView)object).getDelegate());
        }
        if (!this.userCanViewObject(object)) {
            return false;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (Namespace.isPersonal(label)) {
            return null != user && this.permissionManager.hasPermission((User)user, Permission.VIEW, object);
        }
        return this.permissionManager.hasPermission((User)user, Permission.EDIT, object);
    }

    @Override
    public boolean userCanEditLabelOrIsSpaceAdmin(Label label, SpaceContentEntityObject object) {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return this.userEquals(label.getOwnerUser(), currentUser) && this.userCanEditLabel(label, (Labelable)object) || this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, object.getSpace());
    }

    @Override
    public boolean userCanViewObject(Labelable object) {
        if (object == null) {
            return false;
        }
        if (object instanceof PermittedLabelView) {
            return this.userCanViewObject(((PermittedLabelView)object).getDelegate());
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, object);
    }

    private boolean userEquals(@Nullable User x, @Nullable User y) {
        return x == y || x != null && x.equals(y);
    }

    @Override
    public List filterVisibleLabels(List labelList, @Nullable User user, boolean hideSpecialLabels) {
        FilterChain filters = new FilterChain();
        VisibleLabelFilter visibleLabelFilter = user != null ? new VisibleLabelFilter(user.getName()) : new VisibleLabelFilter();
        filters.addFilter((Filter)visibleLabelFilter);
        if (hideSpecialLabels) {
            filters.addFilter((Filter)new SpecialLabelFilter());
        }
        ListFilter listFilter = new ListFilter((Filter)filters);
        return listFilter.filterList(labelList);
    }

    @Override
    public List filterLabelsByNamespace(List labelList, @Nullable User user, Namespace namespace) {
        FilterChain filters = new FilterChain();
        NamespaceLabelFilter namespaceLabelFilter = user != null ? new NamespaceLabelFilter(namespace, user.getName()) : new NamespaceLabelFilter(namespace);
        filters.addFilter((Filter)namespaceLabelFilter);
        ListFilter listFilter = new ListFilter((Filter)filters);
        return listFilter.filterList(labelList);
    }
}

