/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.languages.InvalidLanguageException;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageParser;
import com.atlassian.confluence.ext.code.languages.impl.BuiltinLanguage;
import com.atlassian.confluence.ext.code.languages.impl.RegisteredLanguage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.stereotype.Component;

@Component
public class RhinoLanguageParser
implements LanguageParser {
    private static final String PARSER_VALIDATION_FUNCTION = "SyntaxHighlighter.readBrushes();";
    private final String parserScript;

    public RhinoLanguageParser() {
        InputStream parserStream = null;
        try {
            parserStream = this.getClass().getClassLoader().getResourceAsStream("languageParser.js");
            this.parserScript = IOUtils.toString((InputStream)parserStream, (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to extract language parser script: " + e.getMessage(), e);
        }
        finally {
            if (parserStream != null) {
                IOUtils.closeQuietly((InputStream)parserStream);
            }
        }
    }

    private Language parseLanguage(Reader languageScript, boolean isBuiltin, String friendlyName) throws InvalidLanguageException {
        ArrayList<String> aliases;
        String name;
        StringWriter scriptWriter = new StringWriter();
        try {
            IOUtils.copy((Reader)languageScript, (Writer)scriptWriter);
        }
        catch (IOException e) {
            throw new InvalidLanguageException("newcode.language.parse.read.failed", e);
        }
        String scriptString = scriptWriter.toString();
        Context cx = Context.enter();
        try {
            ScriptableObject scope = cx.initStandardObjects();
            String script = this.parserScript + "\n" + scriptString + "\nSyntaxHighlighter.readBrushes();";
            cx.evaluateString(scope, script, "ParserScript", 0, null);
            Object nameObj = scope.get("brushName", (Scriptable)scope);
            if (!(nameObj instanceof ConsString)) {
                throw new InvalidLanguageException("newcode.language.parse.no.brush.name", new Object[0]);
            }
            name = nameObj.toString();
            Object brushAliasesObj = scope.get("brushAliases", (Scriptable)scope);
            if (!(brushAliasesObj instanceof NativeArray)) {
                throw new InvalidLanguageException("newcode.language.parse.no.brush.name", new Object[0]);
            }
            NativeArray array = (NativeArray)brushAliasesObj;
            aliases = new ArrayList<String>((int)array.getLength());
            int i = 0;
            while ((long)i < array.getLength()) {
                Object aliasObj = array.get(i, (Scriptable)scope);
                if (!(aliasObj instanceof String)) {
                    throw new InvalidLanguageException("newcode.language.parse.invalid.alias.type", new Object[0]);
                }
                aliases.add((String)aliasObj);
                ++i;
            }
        }
        catch (RhinoException re) {
            throw new InvalidLanguageException(re.getMessage(), re);
        }
        finally {
            Context.exit();
        }
        return isBuiltin ? new BuiltinLanguage(name, aliases) : new RegisteredLanguage(name, aliases, friendlyName);
    }

    @Override
    public BuiltinLanguage parseBuiltInLanguage(Reader reader) throws InvalidLanguageException {
        return (BuiltinLanguage)this.parseLanguage(reader, true, "");
    }

    @Override
    public RegisteredLanguage parseRegisteredLanguage(Reader reader, String friendlyName) throws InvalidLanguageException {
        return (RegisteredLanguage)this.parseLanguage(reader, false, friendlyName);
    }
}

