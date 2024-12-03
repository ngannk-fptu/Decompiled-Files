/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.service;

import com.atlassian.mywork.model.Notification;
import com.atlassian.sal.api.user.UserKey;
import java.util.concurrent.Future;
import org.codehaus.jackson.node.ObjectNode;

public interface NotificationService {
    public Future<Notification> createOrUpdate(String var1, Notification var2);

    public int getCount(String var1);

    public void updateMetadata(String var1, String var2, ObjectNode var3, ObjectNode var4);

    public void setRead(UserKey var1, String var2, String var3, ObjectNode var4);
}

