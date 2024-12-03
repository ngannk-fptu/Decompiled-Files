/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.admin.actions.lookandfeel.AbstractDecoratorAction;
import com.atlassian.confluence.admin.actions.lookandfeel.DefaultDecorator;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.themes.CustomLayoutManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import java.util.List;

public class EditDecoratorAction
extends AbstractDecoratorAction
implements LookAndFeel {
    private CustomLayoutManager customLayoutManager;

    @Deprecated
    public List getDecorators() {
        return DefaultDecorator.getDecorators();
    }

    public Collection<DefaultDecorator> getAllDefaultDecorators() {
        return this.customLayoutManager.getAllDefaultDecorators();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doList() {
        return "input";
    }

    public boolean hasTemplate(String decoratorName) {
        return this.customLayoutManager.hasCustomSpaceDecorator(this.getSpaceKey(), decoratorName);
    }

    public DefaultDecorator getDefaultDecorator() {
        return this.customLayoutManager.getDefaultDecorator(this.decoratorName);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doRead() {
        if (this.getDefaultDecorator() == null) {
            this.addActionError("template.notfound", this.decoratorName);
            return "error";
        }
        PersistentDecorator decorator = this.getPersistentDecorator();
        if (decorator == null) {
            this.content = this.readDefaultTemplate();
            if (this.content == null) {
                this.addActionError("template.notfound", this.decoratorName);
            }
        } else {
            this.content = decorator.getBody();
        }
        return "input";
    }

    private PersistentDecorator getPersistentDecorator() {
        return this.customLayoutManager.getPersistentDecorator(this.getSpaceKey(), this.getDecoratorName());
    }

    public String doWrite() throws Exception {
        if (this.getDefaultDecorator() == null) {
            this.addActionError("error.decorator.not.found");
            return "error";
        }
        this.customLayoutManager.saveOrUpdate(this.getSpaceKey(), this.getDecoratorName(), this.getContent());
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doReset() throws Exception {
        PersistentDecorator decorator = this.getPersistentDecorator();
        if (decorator == null) {
            this.addActionError("template.notfound", this.decoratorName);
            return "input";
        }
        this.customLayoutManager.remove(decorator);
        return "success";
    }

    public void setCustomLayoutManager(CustomLayoutManager customLayoutManager) {
        this.customLayoutManager = customLayoutManager;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}

