/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.spaces.Space;
import java.util.Map;

public interface SpaceMetadataFactory {
    public Map<String, Object> makeMetadata(Space var1, Fauxpansions var2);
}

