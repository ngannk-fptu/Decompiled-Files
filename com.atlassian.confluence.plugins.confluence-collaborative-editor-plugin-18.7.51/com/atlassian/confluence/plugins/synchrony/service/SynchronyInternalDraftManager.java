/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.ContentEntityObjectDao
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-internal-draft-manager")
@Internal
public class SynchronyInternalDraftManager {
    private final ContentEntityObjectDao contentEntityObjectDao;

    @Autowired
    public SynchronyInternalDraftManager(@ComponentImport ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public <T extends ContentEntityObject> T findDraftFor(@Nonnull T ceo) {
        return (T)(ceo.isDraft() ? ceo : this.contentEntityObjectDao.findDraftFor(ceo.getId()));
    }

    public <T extends ContentEntityObject> T findDraftFor(long contentId) {
        return (T)this.contentEntityObjectDao.findDraftFor(contentId);
    }
}

