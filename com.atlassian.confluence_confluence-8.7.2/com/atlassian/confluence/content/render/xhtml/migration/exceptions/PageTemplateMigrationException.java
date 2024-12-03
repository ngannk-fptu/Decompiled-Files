/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.pages.templates.PageTemplate;

public class PageTemplateMigrationException
extends MigrationException {
    private final long id;
    private final String spaceKey;
    private final String spaceName;
    private final String name;
    private final String description;
    private final int version;

    public PageTemplateMigrationException(PageTemplate template, Throwable cause) {
        super("PageTemplate migration failure", cause);
        if (template.getSpace() != null) {
            this.spaceKey = template.getSpace().getKey();
            this.spaceName = template.getSpace().getName();
        } else {
            this.spaceKey = null;
            this.spaceName = null;
        }
        this.name = template.getName();
        this.description = template.getDescription();
        this.version = template.getVersion();
        this.id = template.getId();
    }

    public long getId() {
        return this.id;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getVersion() {
        return this.version;
    }
}

