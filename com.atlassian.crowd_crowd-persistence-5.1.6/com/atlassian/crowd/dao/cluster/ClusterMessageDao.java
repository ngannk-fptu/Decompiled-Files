/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.cluster;

import com.atlassian.crowd.model.cluster.InternalClusterMessage;
import java.util.List;

public interface ClusterMessageDao {
    public void addMessage(InternalClusterMessage var1);

    public List<InternalClusterMessage> getMessagesAfter(String var1, long var2);

    public int deleteClusterMessagesBeforeAndOn(long var1);

    public Long getHighestId();
}

