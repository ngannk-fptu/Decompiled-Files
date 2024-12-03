/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

import com.atlassian.confluence.ext.code.languages.InvalidLanguageException;
import com.atlassian.confluence.ext.code.languages.impl.BuiltinLanguage;
import com.atlassian.confluence.ext.code.languages.impl.RegisteredLanguage;
import java.io.Reader;

public interface LanguageParser {
    public BuiltinLanguage parseBuiltInLanguage(Reader var1) throws InvalidLanguageException;

    public RegisteredLanguage parseRegisteredLanguage(Reader var1, String var2) throws InvalidLanguageException;
}

