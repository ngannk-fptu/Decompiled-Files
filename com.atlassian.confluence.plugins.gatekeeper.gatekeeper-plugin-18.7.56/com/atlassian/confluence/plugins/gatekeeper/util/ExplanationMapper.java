/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Explanation;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationDetailType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationType;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.RefinedExplanation;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Source;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplanationMapper {
    public static final Logger logger = LoggerFactory.getLogger(ExplanationMapper.class);
    public static final String GATEKEEPER_V2_DARK_FEATURE = "gatekeeper-ui-v2";
    private static final String I18N_PREFIX = "com.atlassian.confluence.plugins.gatekeeper.";
    private static final String I18N_NO_PERMISSION_GRANTED_TO_USER = "com.atlassian.confluence.plugins.gatekeeper.no.perm.granted.to.user";
    private static final String I18N_GROUP_PERMISSION_GRANTED_TO_AUTHENTICATED_USERS_VIA_ANONYMOUS = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.authenticated.users.via.anon";
    private static final String I18N_PERMISSION_GRANTED_TO_EVERYONE_VIA_ANONYMOUS = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.everyone.via.anon";
    private static final String I18N_NO_PERMISSION_GRANTED_TO_GROUP = "com.atlassian.confluence.plugins.gatekeeper.no.perm.granted.to.group";
    private static final String I18N_USER_DISABLED = "com.atlassian.confluence.plugins.gatekeeper.user.disabled";
    private static final String I18N_USER_CAN_USE_DISABLED = "com.atlassian.confluence.plugins.gatekeeper.user.can.use.disabled";
    private static final String I18N_GROUP_CAN_USE_DISABLED = "com.atlassian.confluence.plugins.gatekeeper.group.can.use.disabled";
    private static final String I18N_PERMISSION_GRANTED_AS_INDIVIDUAL = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.user";
    private static final String I18N_PERMISSION_GRANTED_AS_INDIVIDUAL_AND_GROUP_MEMBER = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.user.and.group";
    private static final String I18N_PERMISSION_GRANTED_AS_GROUP_MEMBER = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.group";
    private static final String I18N_PERMISSION_GRANTED_ALL_MEMBERS = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.all.members";
    private static final String I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.anon";
    private static final String I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS_GLOBAL_ANON_CAN_USE_DISABLED = "com.atlassian.confluence.plugins.gatekeeper.perm.granted.to.anon.no.global.anon.can.use";
    private static final String I18N_NO_PERMISSION_GRANTED_TO_ANONYMOUS_USERS = "com.atlassian.confluence.plugins.gatekeeper.no.perm.granted.to.anon";
    private static final String I18N_NO_ADMIN_OR_RESTRICTION_PERMISSION_GRANTED_TO_ANONYMOUS_USERS = "com.atlassian.confluence.plugins.gatekeeper.no.admin.or.restriction.perm.granted.to.anon";
    private static final String I18N_SPACE_ADMIN_CAN_OVERRIDE = "com.atlassian.confluence.plugins.gatekeeper.space.admin.can.override";
    private static final String I18N_USER_RESTRICTIONS_GRANTED_TO_USER_AS_SPACE_ADMIN = "com.atlassian.confluence.plugins.gatekeeper.space.admin.can.edit.restrictions";
    private static final String I18N_USER_RESTRICTIONS_GRANTED_TO_GROUP_AS_SPACE_ADMIN = "com.atlassian.confluence.plugins.gatekeeper.space.admin.group.can.edit.restrictions";
    private static final String I18N_ANONYMOUS_ACCESS_ENABLED_GLOBALLY = "com.atlassian.confluence.plugins.gatekeeper.global.anon.access.enabled";
    private static final String I18N_GROUP_IS_SPACE_ADMIN = "com.atlassian.confluence.plugins.gatekeeper.group.is.space.admin";
    private static final String I18N_DELETE_OWN_AVAILABLE_TO_GROUP_MEMBERS = "com.atlassian.confluence.plugins.gatekeeper.delete.own.available.to.group.members";
    private static final String I18N_DELETE_OWN_GRANTED_TO_USER = "com.atlassian.confluence.plugins.gatekeeper.delete.own.granted.to.user";
    private static final String I18N_DELETE_OWN_GRANTED_TO_GROUP = "com.atlassian.confluence.plugins.gatekeeper.delete.own.granted.to.group";
    private static final String I18N_DELETE_OWN_GRANTED_TO_ANONYMOUS_USERS = "com.atlassian.confluence.plugins.gatekeeper.delete.own.granted.to.anon";
    private static final String I18N_SUPER_USER = "com.atlassian.confluence.plugins.gatekeeper.super.user";
    private static final String I18N_SUPER_GROUP = "com.atlassian.confluence.plugins.gatekeeper.super.group";
    private static final String I18N_RESTRICTIONS_DEPENDS_EDIT_PERMISSION_USER = "com.atlassian.confluence.plugins.gatekeeper.restrictions.depend.on.edit.perm.user";
    private static final String I18N_RESTRICTIONS_DEPENDS_EDIT_PERMISSION_GROUP = "com.atlassian.confluence.plugins.gatekeeper.restrictions.depend.on.edit.perm.group";
    public static final String I18N_GOOD_TO_KNOW_GROUP_HAS_PARENT = "com.atlassian.confluence.plugins.gatekeeper.good-to-know.group-has-parent";
    private final I18NBean i18NBean;

    public ExplanationMapper(I18NBean i18NBean) {
        this.i18NBean = i18NBean;
    }

    private boolean isEditAccessAllowed(Set<ExplanationType> types, List<Explanation> explanations) {
        if (types.contains((Object)ExplanationType.EXPLAIN_EDIT_RESTRICTION_NOT_FOUND)) {
            return true;
        }
        Optional<Explanation> editRestrictionExplanationOpt = explanations.stream().filter(editRestrictionExplanation -> editRestrictionExplanation.getType() == ExplanationType.EXPLAIN_EDIT_RESTRICTION).findFirst();
        return editRestrictionExplanationOpt.map(Explanation::isPermitted).orElse(false);
    }

    private String explainRestrictions(OwnerType ownerType, Set<ExplanationType> types, List<Explanation> explanations, TinyPage currentPage, Permission permission) {
        String i18nKey;
        boolean isViewRestrictionOnlyPermission = Permissions.VIEW_RESTRICTION_ONLY_DEPENDENT_PERMISSIONS.contains(permission);
        TinyPage restrictingPage = currentPage;
        Optional<Explanation> viewRestrictionExplanationOpt = explanations.stream().filter(explanation -> explanation.getType() == ExplanationType.EXPLAIN_EXPLICIT_VIEW_RESTRICTION).findFirst();
        if (types.contains((Object)ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION)) {
            Optional<Explanation> inheritedForbidViewRestrictionExplanationOpt = explanations.stream().filter(explanation -> explanation.getType() == ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION && !explanation.isPermitted()).min(Comparator.comparing(explanation -> explanation.getRestrictingPage().getLevel()));
            if (inheritedForbidViewRestrictionExplanationOpt.isPresent()) {
                Explanation inheritedViewRestrictionExplanation = inheritedForbidViewRestrictionExplanationOpt.get();
                restrictingPage = inheritedViewRestrictionExplanation.getRestrictingPage();
                i18nKey = "com.atlassian.confluence.plugins.gatekeeper.restrictions.view.forbid." + ownerType.getKey();
            } else if (isViewRestrictionOnlyPermission) {
                Explanation inheritedAllowViewRestrictionExplanation = explanations.stream().filter(explanation -> explanation.getType() == ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION && explanation.isPermitted()).max(Comparator.comparing(explanation -> explanation.getRestrictingPage().getLevel())).get();
                restrictingPage = inheritedAllowViewRestrictionExplanation.getRestrictingPage();
                i18nKey = "com.atlassian.confluence.plugins.gatekeeper.restrictions.view.allow." + ownerType.getKey();
            } else if (this.isEditAccessAllowed(types, explanations)) {
                Optional<Explanation> inheritedAllowViewRestrictionExplanationOpt;
                if (types.contains((Object)ExplanationType.EXPLAIN_EDIT_RESTRICTION_NOT_FOUND) && (inheritedAllowViewRestrictionExplanationOpt = explanations.stream().filter(explanation -> explanation.getType() == ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION).max(Comparator.comparing(explanation -> explanation.getRestrictingPage().getLevel()))).isPresent()) {
                    restrictingPage = inheritedAllowViewRestrictionExplanationOpt.get().getRestrictingPage();
                }
                i18nKey = "com.atlassian.confluence.plugins.gatekeeper.restrictions.all.allow." + ownerType.getKey();
            } else {
                i18nKey = "com.atlassian.confluence.plugins.gatekeeper.restrictions.edit.forbid." + ownerType.getKey();
            }
        } else {
            boolean isDirectViewAccessAllowed;
            i18nKey = viewRestrictionExplanationOpt.isPresent() ? ((isDirectViewAccessAllowed = viewRestrictionExplanationOpt.map(Explanation::isPermitted).orElse(false).booleanValue()) ? (isViewRestrictionOnlyPermission ? "com.atlassian.confluence.plugins.gatekeeper.restrictions.view.allow." + ownerType.getKey() : (this.isEditAccessAllowed(types, explanations) ? "com.atlassian.confluence.plugins.gatekeeper.restrictions.all.allow." + ownerType.getKey() : "com.atlassian.confluence.plugins.gatekeeper.restrictions.edit.forbid." + ownerType.getKey())) : "com.atlassian.confluence.plugins.gatekeeper.restrictions.view.forbid." + ownerType.getKey()) : (isViewRestrictionOnlyPermission ? "com.atlassian.confluence.plugins.gatekeeper.restrictions.view.allow." + ownerType.getKey() : (this.isEditAccessAllowed(types, explanations) ? "com.atlassian.confluence.plugins.gatekeeper.restrictions.all.allow." + ownerType.getKey() : "com.atlassian.confluence.plugins.gatekeeper.restrictions.edit.forbid." + ownerType.getKey()));
        }
        return this.paragraph(i18nKey, this.getPageLink(restrictingPage));
    }

    public RefinedExplanation refine(OwnerType ownerType, List<Explanation> explanations, boolean isGlobalAnonymousAccessEnabled, Permission permission, TinyPage page) {
        return this.refine(ownerType, explanations, isGlobalAnonymousAccessEnabled, permission, page, false);
    }

    public RefinedExplanation refine(OwnerType ownerType, List<Explanation> explanations, boolean isGlobalAnonymousAccessEnabled, Permission permission, TinyPage page, boolean groupHasParents) {
        Object goodToKnow;
        String why;
        block61: {
            boolean restrictionsExplained;
            boolean spacePermissionGrantedToUserOrGroup;
            Set<ExplanationType> types;
            block62: {
                block60: {
                    why = "";
                    goodToKnow = "";
                    types = explanations.stream().map(Explanation::getType).collect(Collectors.toSet());
                    spacePermissionGrantedToUserOrGroup = types.stream().anyMatch(Sets.newHashSet((Object[])new ExplanationType[]{ExplanationType.EXPLAIN_SPACE_USER_PERMISSION, ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION})::contains);
                    restrictionsExplained = types.stream().anyMatch(Sets.newHashSet((Object[])new ExplanationType[]{ExplanationType.EXPLAIN_EXPLICIT_VIEW_RESTRICTION, ExplanationType.EXPLAIN_EDIT_RESTRICTION, ExplanationType.EXPLAIN_INHERITED_VIEW_RESTRICTION})::contains);
                    if (!types.contains((Object)ExplanationType.EXPLAIN_LOGIN)) break block60;
                    Explanation canUseExplanation = explanations.stream().filter(explanation -> explanation.getType() == ExplanationType.EXPLAIN_LOGIN).findFirst().get();
                    List<ExplanationDetailType> explanationDetailTypeList = canUseExplanation.getDetailTypeList();
                    switch (ownerType) {
                        case TYPE_USER: {
                            if (explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_USER_DISABLED)) {
                                if (explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_ENABLED)) {
                                    why = this.paragraph(I18N_PERMISSION_GRANTED_TO_EVERYONE_VIA_ANONYMOUS, new String[0]);
                                    goodToKnow = (String)goodToKnow + this.paragraph(I18N_USER_DISABLED, new String[0]);
                                } else {
                                    why = this.paragraph(I18N_USER_DISABLED, new String[0]);
                                }
                            } else if (explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_NO_CAN_USE)) {
                                why = this.paragraph(I18N_USER_CAN_USE_DISABLED, new String[0]);
                            }
                            if (spacePermissionGrantedToUserOrGroup) {
                                goodToKnow = (String)goodToKnow + this.explainWhy(ownerType, explanations, types, isGlobalAnonymousAccessEnabled);
                                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION) && permission != Permission.REMOVE_OWN_CONTENT_PERMISSION) {
                                    goodToKnow = (String)goodToKnow + this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS_GLOBAL_ANON_CAN_USE_DISABLED, new String[0]);
                                }
                                if (explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_PERMITTED_GLOBALLY_DISABLED)) {
                                    goodToKnow = (String)goodToKnow + this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS_GLOBAL_ANON_CAN_USE_DISABLED, new String[0]);
                                    break;
                                }
                            } else if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND) || explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_NOT_PERMITTED_GLOBALLY_DISABLED)) {
                                goodToKnow = (String)goodToKnow + this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_USER, new String[0]);
                                break;
                            }
                            break block61;
                        }
                        case TYPE_GROUP: {
                            if (explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_NO_CAN_USE)) {
                                why = this.paragraph(I18N_GROUP_CAN_USE_DISABLED, new String[0]);
                                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION)) {
                                    goodToKnow = this.paragraph(I18N_PERMISSION_GRANTED_ALL_MEMBERS, new String[0]);
                                    break;
                                }
                                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND) || explanationDetailTypeList.contains((Object)ExplanationDetailType.DETAIL_LOGIN_ANONYMOUS_NOT_PERMITTED_GLOBALLY_DISABLED)) {
                                    goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_GROUP, new String[0]);
                                    break;
                                }
                            }
                            break block61;
                        }
                        default: {
                            throw new UnsupportedOperationException();
                        }
                    }
                    break block61;
                }
                if (!types.contains((Object)ExplanationType.EXPLAIN_SUPER_USER)) break block62;
                switch (ownerType) {
                    case TYPE_USER: {
                        why = this.paragraph(I18N_SUPER_USER, new String[0]);
                        if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND)) {
                            goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_USER, new String[0]);
                        }
                        break block61;
                    }
                    case TYPE_GROUP: {
                        why = this.paragraph(I18N_SUPER_GROUP, new String[0]);
                        if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND)) {
                            goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_GROUP, new String[0]);
                        }
                        break block61;
                    }
                    default: {
                        throw new UnsupportedOperationException();
                    }
                }
            }
            if (ownerType != OwnerType.TYPE_GROUP && restrictionsExplained) {
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND)) {
                    why = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_USER, new String[0]);
                    goodToKnow = this.explainRestrictions(ownerType, types, explanations, page, permission);
                } else {
                    why = this.explainRestrictions(ownerType, types, explanations, page, permission);
                    goodToKnow = this.explainWhy(ownerType, explanations, types, isGlobalAnonymousAccessEnabled);
                }
                if (permission == Permission.SET_PAGE_PERMISSIONS_PERMISSION && types.contains((Object)ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN)) {
                    goodToKnow = (String)goodToKnow + this.paragraph(I18N_USER_RESTRICTIONS_GRANTED_TO_USER_AS_SPACE_ADMIN, new String[0]);
                }
            } else if (spacePermissionGrantedToUserOrGroup) {
                if (permission == Permission.SET_PAGE_PERMISSIONS_PERMISSION && types.contains((Object)ExplanationType.EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_NOT_PERMITTED)) {
                    switch (ownerType) {
                        case TYPE_USER: {
                            why = this.paragraph(I18N_RESTRICTIONS_DEPENDS_EDIT_PERMISSION_USER, new String[0]);
                            break;
                        }
                        case TYPE_GROUP: {
                            why = this.paragraph(I18N_RESTRICTIONS_DEPENDS_EDIT_PERMISSION_GROUP, new String[0]);
                        }
                    }
                    goodToKnow = this.explainWhy(ownerType, explanations, types, isGlobalAnonymousAccessEnabled);
                } else {
                    why = this.explainWhy(ownerType, explanations, types, isGlobalAnonymousAccessEnabled);
                }
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION)) {
                    switch (ownerType) {
                        case TYPE_USER: 
                        case TYPE_GROUP: {
                            goodToKnow = (String)goodToKnow + (isGlobalAnonymousAccessEnabled ? this.paragraph(I18N_PERMISSION_GRANTED_TO_EVERYONE_VIA_ANONYMOUS, new String[0]) : this.paragraph(I18N_GROUP_PERMISSION_GRANTED_TO_AUTHENTICATED_USERS_VIA_ANONYMOUS, new String[0]));
                            break;
                        }
                        case TYPE_ANONYMOUS: {
                            break;
                        }
                        default: {
                            throw new UnsupportedOperationException();
                        }
                    }
                }
                if (types.contains((Object)ExplanationType.EXPLAIN_PARTIAL_REMOVE)) {
                    goodToKnow = (String)goodToKnow + this.paragraph(I18N_DELETE_OWN_AVAILABLE_TO_GROUP_MEMBERS, new String[0]);
                }
            } else if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION)) {
                Pair<String, String> explanationDetails = this.explainSpaceAnonymousPermission(ownerType, isGlobalAnonymousAccessEnabled, permission);
                why = (String)explanationDetails.getLeft();
                goodToKnow = (String)explanationDetails.getRight();
            } else if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND)) {
                goodToKnow = "";
                switch (ownerType) {
                    case TYPE_USER: {
                        if (permission == Permission.SET_PAGE_PERMISSIONS_PERMISSION && types.contains((Object)ExplanationType.EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_NOT_PERMITTED)) {
                            why = this.paragraph(I18N_RESTRICTIONS_DEPENDS_EDIT_PERMISSION_USER, new String[0]);
                            goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_USER, new String[0]);
                        } else {
                            why = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_USER, new String[0]);
                        }
                        if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ADMIN_CAN_OVERRIDE)) {
                            goodToKnow = this.paragraph(I18N_SPACE_ADMIN_CAN_OVERRIDE, new String[0]);
                        }
                        if (types.contains((Object)ExplanationType.EXPLAIN_PARTIAL_REMOVE)) {
                            goodToKnow = (String)goodToKnow + this.paragraph(I18N_DELETE_OWN_GRANTED_TO_USER, new String[0]);
                        }
                        if (permission != Permission.SET_PAGE_PERMISSIONS_PERMISSION || !types.contains((Object)ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN)) break;
                        goodToKnow = (String)goodToKnow + this.paragraph(I18N_USER_RESTRICTIONS_GRANTED_TO_USER_AS_SPACE_ADMIN, new String[0]);
                        break;
                    }
                    case TYPE_GROUP: {
                        if (permission == Permission.SET_PAGE_PERMISSIONS_PERMISSION && types.contains((Object)ExplanationType.EXPLAIN_SET_PERMISSIONS_DEPENDS_EDIT_NOT_PERMITTED)) {
                            why = this.paragraph(I18N_RESTRICTIONS_DEPENDS_EDIT_PERMISSION_GROUP, new String[0]);
                            goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_GROUP, new String[0]);
                        } else {
                            why = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_GROUP, new String[0]);
                        }
                        if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ADMIN_CAN_OVERRIDE)) {
                            goodToKnow = this.paragraph(I18N_GROUP_IS_SPACE_ADMIN, new String[0]);
                        }
                        if (types.contains((Object)ExplanationType.EXPLAIN_PARTIAL_REMOVE)) {
                            goodToKnow = (String)goodToKnow + this.paragraph(I18N_DELETE_OWN_GRANTED_TO_GROUP, new String[0]);
                        }
                        if (permission != Permission.SET_PAGE_PERMISSIONS_PERMISSION || !types.contains((Object)ExplanationType.EXPLAIN_IMPLICIT_SPACE_ADMIN)) break;
                        goodToKnow = (String)goodToKnow + this.paragraph(I18N_USER_RESTRICTIONS_GRANTED_TO_GROUP_AS_SPACE_ADMIN, new String[0]);
                        break;
                    }
                    case TYPE_ANONYMOUS: {
                        why = permission == Permission.ADMINISTER_SPACE_PERMISSION || permission == Permission.SET_PAGE_PERMISSIONS_PERMISSION ? this.paragraph(I18N_NO_ADMIN_OR_RESTRICTION_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]) : this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
                        if (types.contains((Object)ExplanationType.EXPLAIN_PARTIAL_REMOVE)) {
                            goodToKnow = this.paragraph(I18N_DELETE_OWN_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
                        }
                        if (!isGlobalAnonymousAccessEnabled) break;
                        goodToKnow = (String)goodToKnow + this.paragraph(I18N_ANONYMOUS_ACCESS_ENABLED_GLOBALLY, new String[0]);
                        break;
                    }
                    default: {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
        if (OwnerType.TYPE_GROUP.equals((Object)ownerType) && groupHasParents) {
            goodToKnow = (String)goodToKnow + this.paragraph(I18N_GOOD_TO_KNOW_GROUP_HAS_PARENT, new String[0]);
        }
        return new RefinedExplanation(why, (String)goodToKnow);
    }

    private Pair<String, String> explainSpaceAnonymousPermission(OwnerType ownerType, boolean isGlobalAnonymousAccessEnabled, Permission permission) {
        String why = "";
        String goodToKnow = "";
        switch (ownerType) {
            case TYPE_USER: {
                why = isGlobalAnonymousAccessEnabled ? this.paragraph(I18N_PERMISSION_GRANTED_TO_EVERYONE_VIA_ANONYMOUS, new String[0]) : this.paragraph(I18N_GROUP_PERMISSION_GRANTED_TO_AUTHENTICATED_USERS_VIA_ANONYMOUS, new String[0]);
                goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_USER, new String[0]);
                break;
            }
            case TYPE_GROUP: {
                why = isGlobalAnonymousAccessEnabled ? this.paragraph(I18N_PERMISSION_GRANTED_TO_EVERYONE_VIA_ANONYMOUS, new String[0]) : this.paragraph(I18N_GROUP_PERMISSION_GRANTED_TO_AUTHENTICATED_USERS_VIA_ANONYMOUS, new String[0]);
                goodToKnow = this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_GROUP, new String[0]);
                break;
            }
            case TYPE_ANONYMOUS: {
                if (permission == Permission.REMOVE_OWN_CONTENT_PERMISSION) {
                    why = this.paragraph(I18N_DELETE_OWN_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
                    goodToKnow = this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
                    break;
                }
                if (isGlobalAnonymousAccessEnabled) {
                    why = this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
                    goodToKnow = this.paragraph(I18N_ANONYMOUS_ACCESS_ENABLED_GLOBALLY, new String[0]);
                    break;
                }
                why = this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS_GLOBAL_ANON_CAN_USE_DISABLED, new String[0]);
                goodToKnow = this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
            }
        }
        return Pair.of((Object)why, (Object)goodToKnow);
    }

    private String explainWhy(OwnerType ownerType, List<Explanation> explanations, Set<ExplanationType> types, boolean isGlobalAnonymousAccessEnabled) {
        switch (ownerType) {
            case TYPE_USER: {
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION)) {
                    ArrayList groupNames = new ArrayList();
                    for (Explanation explanation : explanations) {
                        if (explanation.getType() != ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION) continue;
                        List<Source> sourceList = explanation.getSourceList();
                        List names = sourceList.stream().filter(Source::isGroup).map(Source::getName).collect(Collectors.toList());
                        groupNames.addAll(names);
                    }
                    if (groupNames.isEmpty()) {
                        return "";
                    }
                    StringBuilder groupListHtml = new StringBuilder("<ul>");
                    for (String groupName : groupNames) {
                        groupListHtml.append("<li>").append(groupName).append("</li>");
                    }
                    groupListHtml.append("</ul>");
                    if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_USER_PERMISSION)) {
                        return this.paragraph(I18N_PERMISSION_GRANTED_AS_INDIVIDUAL_AND_GROUP_MEMBER, groupListHtml.toString());
                    }
                    return this.paragraph(I18N_PERMISSION_GRANTED_AS_GROUP_MEMBER, groupListHtml.toString());
                }
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_USER_PERMISSION)) {
                    return this.paragraph(I18N_PERMISSION_GRANTED_AS_INDIVIDUAL, new String[0]);
                }
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION)) {
                    return isGlobalAnonymousAccessEnabled ? this.paragraph(I18N_PERMISSION_GRANTED_TO_EVERYONE_VIA_ANONYMOUS, new String[0]) : this.paragraph(I18N_GROUP_PERMISSION_GRANTED_TO_AUTHENTICATED_USERS_VIA_ANONYMOUS, new String[0]);
                }
                return "";
            }
            case TYPE_GROUP: {
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_GROUP_PERMISSION)) {
                    return this.paragraph(I18N_PERMISSION_GRANTED_ALL_MEMBERS, new String[0]);
                }
            }
            case TYPE_ANONYMOUS: {
                if (types.contains((Object)ExplanationType.EXPLAIN_SPACE_ANONYMOUS_PERMISSION)) {
                    return isGlobalAnonymousAccessEnabled ? this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]) : this.paragraph(I18N_PERMISSION_GRANTED_TO_ANONYMOUS_USERS_GLOBAL_ANON_CAN_USE_DISABLED, new String[0]);
                }
                if (!types.contains((Object)ExplanationType.EXPLAIN_SPACE_PERMISSION_NOT_FOUND)) break;
                return this.paragraph(I18N_NO_PERMISSION_GRANTED_TO_ANONYMOUS_USERS, new String[0]);
            }
        }
        return "";
    }

    private String paragraph(String content, String ... params) {
        return "<p>" + this.i18NBean.getText(content, (List)Lists.newArrayList((Object[])params)) + "</p>";
    }

    private String getPageLink(TinyPage page) {
        HttpServletRequest request = ServletActionContext.getRequest();
        return "<a href=\"" + request.getContextPath() + "/pages/viewpage.action?pageId=" + page.getId() + "\" target=\"_blank\">" + HtmlUtil.htmlEncode((String)page.getTitle()) + "</a>";
    }
}

