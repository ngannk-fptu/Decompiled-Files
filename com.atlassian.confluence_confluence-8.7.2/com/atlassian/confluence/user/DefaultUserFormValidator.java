/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.user.UserFormValidator;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.validation.MessageHolder;
import com.opensymphony.util.TextUtils;
import org.apache.commons.lang3.StringUtils;

public class DefaultUserFormValidator
implements UserFormValidator {
    private static final String ENGLISH_ANONYMOUS_USER = "anonymous";
    private final ConfluenceUserResolver confluenceUserResolver;
    private final I18NBeanFactory i18NBeanFactory;

    public DefaultUserFormValidator(ConfluenceUserResolver confluenceUserResolver, I18NBeanFactory i18NBeanFactory) {
        this.confluenceUserResolver = confluenceUserResolver;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public MessageHolder validateNewUserBySignup(UserForm form, MessageHolder result) {
        this.safePut("username", this.validateUsername(form.getUsername()), result);
        this.safePut("fullName", this.validateFullName(form.getFullName()), result);
        this.safePut("email", this.validateEmail(form.getEmail()), result);
        this.validatePassword(form.getPassword(), form.getConfirm(), result);
        return result;
    }

    @Override
    public MessageHolder validateEditUser(UserForm form, MessageHolder result) {
        if (this.confluenceUserResolver.getUserByName(form.getUsername()) == null) {
            result.addActionError("user.doesnt.exist");
        }
        this.safePut("fullName", this.validateFullName(form.getFullName()), result);
        this.safePut("email", this.validateEmail(form.getEmail()), result);
        return result;
    }

    @Override
    public MessageHolder validateEditUserAllowRename(UserForm form, MessageHolder result) {
        ConfluenceUser user = this.confluenceUserResolver.getExistingUserByKey(form.getUserKey());
        if (user == null) {
            result.addActionError("user.doesnt.exist");
        } else if (!user.getName().equals(form.getUsername())) {
            this.safePut("username", this.validateNewUsername(form.getUsername()), result);
        }
        this.safePut("fullName", this.validateFullName(form.getFullName()), result);
        this.safePut("email", this.validateEmail(form.getEmail()), result);
        return result;
    }

    @Override
    public MessageHolder validateNewUser(UserForm form, MessageHolder result) {
        this.safePut("username", this.validateNewUsername(form.getUsername()), result);
        this.safePut("fullName", this.validateFullName(form.getFullName()), result);
        this.safePut("email", this.validateEmail(form.getEmail()), result);
        this.validatePassword(form.getPassword(), form.getConfirm(), result);
        return result;
    }

    private String validateNewUsername(String username) {
        if (this.confluenceUserResolver.getUserByName(username) != null) {
            return "user.exists";
        }
        return this.validateUsername(username);
    }

    private void safePut(String field, String error, MessageHolder result) {
        if (StringUtils.isNotBlank((CharSequence)error)) {
            result.addFieldError(field, error);
        }
    }

    private String validateUsername(String username) {
        if (StringUtils.isBlank((CharSequence)username)) {
            return "username.empty";
        }
        if (StringUtils.containsAny((CharSequence)username, (CharSequence)"\\,+<>'\"")) {
            return "username.invalid";
        }
        if (!username.matches("[^\\s]+")) {
            return "username.no.whitespace";
        }
        if (this.getI18NBean().getText("anonymous.name").equalsIgnoreCase(username) || ENGLISH_ANONYMOUS_USER.equalsIgnoreCase(username)) {
            return "reserved.username";
        }
        if (!username.equals(username.toLowerCase())) {
            return "username.uppercase";
        }
        return null;
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean();
    }

    private void validatePassword(String password, String confirm, MessageHolder result) {
        if (StringUtils.isBlank((CharSequence)password)) {
            result.addFieldError("password", "password.empty");
        } else if (!password.equals(confirm)) {
            result.addFieldError("confirm", "passwords.dontmatch");
        }
    }

    private String validateEmail(String email) {
        if (StringUtils.isBlank((CharSequence)email) || !TextUtils.verifyEmail((String)email)) {
            return "email.invalid";
        }
        return null;
    }

    private String validateFullName(String fullName) {
        if (StringUtils.isBlank((CharSequence)fullName)) {
            return "fullname.empty";
        }
        if (StringUtils.containsAny((CharSequence)fullName, (CharSequence)"<>")) {
            return "signup.fullname.contains.angle.bracket";
        }
        if (this.getI18NBean().getText("anonymous.name").equalsIgnoreCase(fullName) || ENGLISH_ANONYMOUS_USER.equalsIgnoreCase(fullName)) {
            return "reserved.fullname";
        }
        return null;
    }
}

