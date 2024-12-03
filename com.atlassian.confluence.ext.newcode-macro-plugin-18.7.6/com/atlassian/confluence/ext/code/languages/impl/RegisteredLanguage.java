/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.languages.Language;
import java.util.Collection;

public final class RegisteredLanguage
implements Language {
    private String name;
    private String friendlyName;
    private Collection<String> aliases;
    private String webResource;

    public RegisteredLanguage(String name, Collection<String> aliases, String friendlyName) {
        this.name = name;
        this.aliases = aliases;
        this.friendlyName = friendlyName;
    }

    @Override
    public Collection<String> getAliases() {
        return this.aliases;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isBuiltIn() {
        return false;
    }

    @Override
    public String getWebResource() {
        return this.webResource;
    }

    public void setWebResource(String webResource) {
        this.webResource = webResource;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }
}

