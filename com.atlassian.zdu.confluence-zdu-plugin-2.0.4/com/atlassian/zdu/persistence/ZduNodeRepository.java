/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.persistence;

import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import java.util.List;

public interface ZduNodeRepository {
    public void put(List<NodeInfoDTO> var1);

    public List<NodeInfoDTO> get();

    public void cleanAll();
}

