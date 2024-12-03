/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.io.IOUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.languages.DuplicateLanguageException;
import com.atlassian.confluence.ext.code.languages.InvalidLanguageException;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageParser;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.atlassian.confluence.ext.code.languages.RegisteredLanguageInstaller;
import com.atlassian.confluence.ext.code.languages.impl.PluginGenerator;
import com.atlassian.confluence.ext.code.languages.impl.RegisteredLanguage;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisteredLanguageInstallerImpl
implements RegisteredLanguageInstaller {
    private final LanguageRegistry languageRegistry;
    private final LanguageParser languageParser;
    private final PluginController pluginController;
    private final PluginGenerator pluginGenerator;

    @Autowired
    public RegisteredLanguageInstallerImpl(LanguageRegistry languageRegistry, LanguageParser languageParser, @ComponentImport PluginController pluginController, PluginGenerator pluginGenerator) {
        this.languageRegistry = languageRegistry;
        this.languageParser = languageParser;
        this.pluginController = pluginController;
        this.pluginGenerator = pluginGenerator;
    }

    @Override
    public void installLanguage(Reader reader, String friendlyName) throws InvalidLanguageException, DuplicateLanguageException {
        PluginArtifact pluginForLanguage;
        String script;
        try {
            script = IOUtils.toString((Reader)reader);
        }
        catch (IOException e) {
            throw new InvalidLanguageException("The language input could not be read", e);
        }
        RegisteredLanguage language = this.languageParser.parseRegisteredLanguage(new StringReader(script), friendlyName);
        this.checkForDuplicates(language);
        try {
            pluginForLanguage = this.pluginGenerator.createPluginForLanguage(language, new StringReader(script));
        }
        catch (IOException e) {
            throw new InvalidLanguageException("The language could not be converted to a plugin artifact", e);
        }
        this.pluginController.installPlugins(new PluginArtifact[]{pluginForLanguage});
    }

    private void checkForDuplicates(Language language) throws DuplicateLanguageException {
        if (this.languageRegistry.isLanguageRegistered(language.getName())) {
            throw new DuplicateLanguageException("The language " + language.getName() + " is already registered.", language.getName());
        }
        for (String alias : language.getAliases()) {
            if (!this.languageRegistry.isLanguageRegistered(alias)) continue;
            throw new DuplicateLanguageException("The language " + alias + " is already registered.", alias);
        }
    }
}

