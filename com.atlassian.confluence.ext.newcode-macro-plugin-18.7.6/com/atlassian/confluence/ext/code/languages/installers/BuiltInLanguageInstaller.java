/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.languages.installers;

import com.atlassian.confluence.ext.code.descriptor.DescriptorFacade;
import com.atlassian.confluence.ext.code.languages.LanguageParser;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuiltInLanguageInstaller {
    private static final Logger log = LoggerFactory.getLogger(BuiltInLanguageInstaller.class);
    private static final Map<String, String> FRIENDLY_NAME_MAP = ImmutableMap.builder().put((Object)"AppleScript", (Object)"AppleScript").put((Object)"AS3", (Object)"ActionScript").put((Object)"Bash", (Object)"Bash").put((Object)"ColdFusion", (Object)"ColdFusion").put((Object)"Cpp", (Object)"C++").put((Object)"CSharp", (Object)"C#").put((Object)"CSS", (Object)"CSS").put((Object)"Delphi", (Object)"Delphi").put((Object)"Diff", (Object)"Diff").put((Object)"Erland", (Object)"Erlang").put((Object)"Groovy", (Object)"Groovy").put((Object)"Java", (Object)"Java").put((Object)"JavaFX", (Object)"Java FX").put((Object)"JScript", (Object)"JavaScript").put((Object)"Perl", (Object)"Perl").put((Object)"Php", (Object)"PHP").put((Object)"Plain", (Object)"Plain Text").put((Object)"PowerShell", (Object)"PowerShell").put((Object)"Python", (Object)"Python").put((Object)"Ruby", (Object)"Ruby").put((Object)"Sass", (Object)"Sass").put((Object)"Scala", (Object)"Scala").put((Object)"Sql", (Object)"SQL").put((Object)"Vb", (Object)"Visual Basic").put((Object)"Xml", (Object)"HTML and XML").put((Object)"Yaml", (Object)"YAML").build();
    private final DescriptorFacade descriptorFacade;
    private final LanguageParser languageParser;
    private final LanguageRegistry languageRegistry;

    @Autowired
    public BuiltInLanguageInstaller(DescriptorFacade descriptorFacade, LanguageParser languageParser, LanguageRegistry languageRegistry) {
        this.descriptorFacade = descriptorFacade;
        this.languageParser = languageParser;
        this.languageRegistry = languageRegistry;
    }

    /*
     * Exception decompiling
     */
    @PostConstruct
    public void onStart() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

