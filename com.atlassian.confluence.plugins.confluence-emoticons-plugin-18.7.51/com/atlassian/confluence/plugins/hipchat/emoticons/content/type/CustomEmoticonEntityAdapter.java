/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapterParent
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.BodyType
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.content.type;

import com.atlassian.confluence.content.ContentEntityAdapterParent;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyType;
import org.springframework.stereotype.Component;

@Component
public class CustomEmoticonEntityAdapter
extends ContentEntityAdapterParent {
    public BodyType getDefaultBodyType(CustomContentEntityObject pluginContentEntityObject) {
        return BodyType.RAW;
    }
}

