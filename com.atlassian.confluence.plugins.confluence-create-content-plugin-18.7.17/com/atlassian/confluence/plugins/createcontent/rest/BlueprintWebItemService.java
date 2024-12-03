/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintDescriptor;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.List;

public interface BlueprintWebItemService {
    public List<CreateDialogWebItemEntity> getCreateSpaceWebItems(I18NBean var1, DocumentationBean var2, ConfluenceUser var3);

    public List<CreateDialogWebItemEntity> getCreateContentWebItems(Space var1, I18NBean var2, DocumentationBean var3, ConfluenceUser var4);

    public List<WebItemModuleDescriptor> getCreateDialogWebItemModuleDescriptors(WebInterfaceContext var1);

    @Deprecated
    public BlueprintDescriptor getBlueprintDescriptorForWebItem(ModuleDescriptor var1);

    public List<CreateDialogWebItemEntity> getCreatePersonalSpaceWebItems(I18NBean var1, DocumentationBean var2, ConfluenceUser var3);
}

