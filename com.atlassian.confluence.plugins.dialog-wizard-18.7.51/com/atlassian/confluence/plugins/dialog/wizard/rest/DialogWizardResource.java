/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.dialog.wizard.rest;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogManager;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardEntity;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.StringUtils;

@Path(value="/wizard")
@Produces(value={"application/json"})
public class DialogWizardResource {
    private static final String PARAM_DIALOG_KEY = "key";
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final DialogManager dialogManager;

    public DialogWizardResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, DialogManager dialogManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.dialogManager = dialogManager;
    }

    @GET
    @Path(value="/{key}")
    @AnonymousAllowed
    public DialogWizardEntity getDialogWizardByKey(@PathParam(value="key") String dialogKey) {
        if (StringUtils.isBlank((CharSequence)dialogKey)) {
            return null;
        }
        DialogWizard dialogWizard = this.dialogManager.getDialogWizardByKey(dialogKey);
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
        return new DialogWizardEntity(i18NBean, dialogWizard);
    }
}

