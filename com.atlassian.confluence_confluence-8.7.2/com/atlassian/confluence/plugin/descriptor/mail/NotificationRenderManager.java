/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 */
package com.atlassian.confluence.plugin.descriptor.mail;

import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.List;

public interface NotificationRenderManager {
    public List<WebItemModuleDescriptor> getDisplayableItems(String var1, NotificationContext var2);

    public void attachActionIconImages(String var1, NotificationContext var2);
}

