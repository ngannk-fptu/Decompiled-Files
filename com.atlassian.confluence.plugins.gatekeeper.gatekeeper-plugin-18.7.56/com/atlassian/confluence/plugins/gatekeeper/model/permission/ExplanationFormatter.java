/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExplainingEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.util.ResourceBundle;

public class ExplanationFormatter {
    private static final String FOR_ANONYMOUS_SUFFIX = ".for-anonymous";
    private static final String FOR_USERS_SUFFIX = ".for-users";
    private static final String FOR_GROUPS_SUFFIX = ".for-groups";
    private static final String PERMISSION_PREFIX = "com.atlassian.confluence.plugins.gatekeeper.permission.";
    private static final String EXPLANATION_PREFIX = "com.atlassian.confluence.plugins.gatekeeper.explanation.";
    private static final String EXPLANATION_DETAIL_PREFIX = "com.atlassian.confluence.plugins.gatekeeper.explanation-detail.";
    private I18NBean i18n;
    private OwnerType evaluationOwnerType;
    private String contextPath;
    private ResourceBundle resourceBundle;

    public ExplanationFormatter(ExplainingEvaluation evaluation) {
        this.evaluationOwnerType = evaluation.getEvaluationOwnerType();
        this.i18n = evaluation.getI18nBean();
        this.contextPath = evaluation.getContextPath();
        this.resourceBundle = this.i18n.getResourceBundle();
    }

    public String getDescription(String key) {
        return this.getText(EXPLANATION_PREFIX + key, new Object[0]);
    }

    public String getPermissionName(String key) {
        return this.getText(PERMISSION_PREFIX + key, new Object[0]);
    }

    public String getDetail(String key, TinyPage restrictedPage) {
        Object[] params = new Object[]{this.contextPath, Long.toString(restrictedPage.getId()), restrictedPage.getTitle()};
        return this.getText(EXPLANATION_DETAIL_PREFIX + key, params);
    }

    public String getDetail(String key, Object ... params) {
        return this.getText(EXPLANATION_DETAIL_PREFIX + key, params);
    }

    private String getText(String key, Object ... params) {
        switch (this.evaluationOwnerType) {
            case TYPE_GROUP: {
                String keyForGroups = key + FOR_GROUPS_SUFFIX;
                if (this.resourceBundle.containsKey(keyForGroups)) {
                    return this.i18n.getText(keyForGroups, params);
                }
            }
            case TYPE_USER: {
                String keyForUsers = key + FOR_USERS_SUFFIX;
                if (this.resourceBundle.containsKey(keyForUsers)) {
                    return this.i18n.getText(keyForUsers, params);
                }
            }
            case TYPE_ANONYMOUS: {
                String keyForAnonymous = key + FOR_ANONYMOUS_SUFFIX;
                if (!this.resourceBundle.containsKey(keyForAnonymous)) break;
                return this.i18n.getText(keyForAnonymous, params);
            }
        }
        return this.i18n.getText(key, params);
    }
}

