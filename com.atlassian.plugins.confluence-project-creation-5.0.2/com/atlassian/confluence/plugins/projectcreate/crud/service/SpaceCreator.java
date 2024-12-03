/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.projectcreate.crud.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.plugins.projectcreate.crud.exception.CreateSpaceFailureException;
import com.atlassian.confluence.user.ConfluenceUser;
import io.atlassian.fugue.Option;
import java.util.Map;

public interface SpaceCreator {
    public Space createSpace(ConfluenceUser var1, String var2, String var3, Map<String, String> var4) throws CreateSpaceFailureException;

    public boolean canHandle(ConfluenceUser var1, String var2, String var3, Map<String, String> var4);

    public Option<String> validateCreateSpace(ConfluenceUser var1, String var2, String var3, Map<String, String> var4);
}

