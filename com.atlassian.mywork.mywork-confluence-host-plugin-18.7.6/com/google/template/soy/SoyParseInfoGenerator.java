/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.io.FileWriteMode
 *  com.google.common.io.Files
 *  org.kohsuke.args4j.Argument
 *  org.kohsuke.args4j.CmdLineParser
 *  org.kohsuke.args4j.Option
 */
package com.google.template.soy;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.template.soy.MainClassUtils;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class SoyParseInfoGenerator {
    private static final String USAGE_PREFIX = "Usage:\njava com.google.template.soy.SoyParseInfoGenerator  \\\n     [<flag1> <flag2> ...] --outputDirectory <path>  \\\n     --javaPackage <package> --javaClassNameSource <source>  \\\n     --srcs <soyFilePath>,... [--deps <soyFilePath>,...]\n";
    @Option(name="--inputPrefix", usage="If provided, this path prefix will be prepended to each input file path listed on the command line. This is a literal string prefix, so you'll need to include a trailing slash if necessary.")
    private String inputPrefix = "";
    @Option(name="--srcs", usage="[Required] The list of source Soy files.", handler=MainClassUtils.StringListOptionHandler.class)
    private List<String> srcs = Lists.newArrayList();
    @Option(name="--deps", usage="The list of dependency Soy files (if applicable). The compiler needs deps for analysis/checking, but will not generate code for dep files.", handler=MainClassUtils.StringListOptionHandler.class)
    private List<String> deps = Lists.newArrayList();
    @Option(name="--indirectDeps", usage="Soy files required by deps, but which may not be used by srcs.", handler=MainClassUtils.StringListOptionHandler.class)
    private List<String> indirectDeps = Lists.newArrayList();
    @Option(name="--allowExternalCalls", usage="Whether to allow external calls. New projects should set this to false, and existing projects should remove existing external calls and then set this to false. It will save you a lot of headaches. Currently defaults to true for backward compatibility.", handler=MainClassUtils.BooleanOptionHandler.class)
    private boolean allowExternalCalls = true;
    @Option(name="--outputDirectory", required=true, usage="[Required] The path to the output directory. If files with the same names already exist at this location, they will be overwritten.")
    private String outputDirectory = "";
    @Option(name="--javaPackage", required=true, usage="[Required] The Java package name to use for the generated classes.")
    private String javaPackage = "";
    @Option(name="--javaClassNameSource", required=true, usage="[Required] The source for the generated class names. Valid values are \"filename\", \"namespace\", and \"generic\". Option \"filename\" turns a Soy file name AaaBbb.soy or aaa_bbb.soy into AaaBbbSoyInfo. Option \"namespace\" turns a namespace aaa.bbb.cccDdd into CccDddSoyInfo (note it only uses the last part of the namespace). Option \"generic\" generates class names such as File1SoyInfo, File2SoyInfo.")
    private String javaClassNameSource = "";
    @Argument
    private List<String> arguments = Lists.newArrayList();

    public static void main(String[] args) throws IOException {
        new SoyParseInfoGenerator().execMain(args);
    }

    private SoyParseInfoGenerator() {
    }

    private void execMain(String[] args) throws IOException, SoySyntaxException {
        final CmdLineParser cmdLineParser = MainClassUtils.parseFlags(this, args, USAGE_PREFIX);
        Function<String, Void> exitWithErrorFn = new Function<String, Void>(){

            public Void apply(String errorMsg) {
                MainClassUtils.exitWithError(errorMsg, cmdLineParser, SoyParseInfoGenerator.USAGE_PREFIX);
                return null;
            }
        };
        if (this.outputDirectory.length() == 0) {
            MainClassUtils.exitWithError("Must provide output directory.", cmdLineParser, USAGE_PREFIX);
        }
        if (this.javaPackage.length() == 0) {
            MainClassUtils.exitWithError("Must provide Java package.", cmdLineParser, USAGE_PREFIX);
        }
        if (this.javaClassNameSource.length() == 0) {
            MainClassUtils.exitWithError("Must provide Java class name source.", cmdLineParser, USAGE_PREFIX);
        }
        Injector injector = Guice.createInjector(new SoyModule());
        SoyFileSet.Builder sfsBuilder = injector.getInstance(SoyFileSet.Builder.class);
        MainClassUtils.addSoyFilesToBuilder(sfsBuilder, this.inputPrefix, (Collection<String>)ImmutableSet.copyOf(this.srcs), (Collection<String>)ImmutableSet.copyOf(this.arguments), (Collection<String>)ImmutableSet.copyOf(this.deps), (Collection<String>)ImmutableSet.copyOf(this.indirectDeps), exitWithErrorFn);
        sfsBuilder.setAllowExternalCalls(this.allowExternalCalls);
        SoyFileSet sfs = sfsBuilder.build();
        ImmutableMap<String, String> generatedFiles = sfs.generateParseInfo(this.javaPackage, this.javaClassNameSource);
        for (Map.Entry entry : generatedFiles.entrySet()) {
            File outputFile = new File(this.outputDirectory, (String)entry.getKey());
            BaseUtils.ensureDirsExistInPath(outputFile.getPath());
            Files.asCharSink((File)outputFile, (Charset)Charsets.UTF_8, (FileWriteMode[])new FileWriteMode[0]).write((CharSequence)entry.getValue());
        }
    }
}

