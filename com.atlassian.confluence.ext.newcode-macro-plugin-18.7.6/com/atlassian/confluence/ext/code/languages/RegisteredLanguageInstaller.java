/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

import com.atlassian.confluence.ext.code.languages.DuplicateLanguageException;
import com.atlassian.confluence.ext.code.languages.InvalidLanguageException;
import java.io.Reader;

public interface RegisteredLanguageInstaller {
    public void installLanguage(Reader var1, String var2) throws InvalidLanguageException, DuplicateLanguageException;
}

