/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages.templates.persistence.dao;

import com.atlassian.confluence.core.persistence.VersionedObjectDao;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.spaces.Space;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface PageTemplateDao
extends VersionedObjectDao<PageTemplate> {
    public PageTemplate getById(long var1);

    public List<PageTemplate> findAllGlobalPageTemplates();

    public List<PageTemplate> findLatestVersions();

    public PageTemplate findPageTemplateByName(String var1);

    public PageTemplate findPageTemplateByNameAndSpace(String var1, Space var2);

    public PageTemplate findCustomisedPluginTemplate(PluginTemplateReference var1);

    public List<PageTemplate> findPreviousVersions(long var1);

    public List<PageTemplate> findBySpace(Space var1);
}

