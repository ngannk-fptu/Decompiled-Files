/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.inject.Injector
 *  org.kohsuke.args4j.Argument
 *  org.kohsuke.args4j.CmdLineParser
 *  org.kohsuke.args4j.Option
 */
package com.google.template.soy;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.template.soy.MainClassUtils;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.shared.internal.MainEntryPointUtils;
import com.google.template.soy.xliffmsgplugin.XliffMsgPluginModule;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class SoyMsgExtractor {
    private static final String USAGE_PREFIX = "Usage:\njava com.google.template.soy.SoyMsgExtractor  \\\n     [<flag1> <flag2> ...] --outputFile <path>  \\\n     --srcs <soyFilePath>,...\n";
    @Option(name="--inputPrefix", usage="If provided, this path prefix will be prepended to each input file path listed on the command line. This is a literal string prefix, so you'll need to include a trailing slash if necessary.")
    private String inputPrefix = "";
    @Option(name="--srcs", usage="[Required] The list of source Soy files.", handler=MainClassUtils.StringListOptionHandler.class)
    private List<String> srcs = Lists.newArrayList();
    @Option(name="--allowExternalCalls", usage="Whether to allow external calls. New projects should set this to false, and existing projects should remove existing external calls and then set this to false. It will save you a lot of headaches. Currently defaults to true for backward compatibility.", handler=MainClassUtils.BooleanOptionHandler.class)
    private boolean allowExternalCalls = true;
    @Option(name="--outputFile", usage="The path to the output file to write. If a file already exists at this location, it will be overwritten. The file extension must match the output format requested.")
    private String outputFile = "";
    @Option(name="--outputPathFormat", usage="A format string that specifies how to build the path to each output file. The format string can include literal characters as well as the placeholders {INPUT_PREFIX}, {INPUT_DIRECTORY}, {INPUT_FILE_NAME}, {INPUT_FILE_NAME_NO_EXT}")
    private String outputPathFormat = "";
    @Option(name="--sourceLocaleString", usage="The locale string of the source language (default 'en').")
    private String sourceLocaleString = "en";
    @Option(name="--targetLocaleString", usage="The locale string of the target language (default empty). If empty, then the output messages file will not specify a target locale string. Note that this option may not be applicable for certain message plugins (in which case this value will be ignored by the message plugin).")
    private String targetLocaleString = "";
    @Option(name="--messagePluginModule", usage="Specifies the full class name of a Guice module that binds a SoyMsgPlugin. If not specified, the default is com.google.template.soy.xliffmsgplugin.XliffMsgPluginModule, which binds the XliffMsgPlugin.")
    private String messagePluginModule = XliffMsgPluginModule.class.getName();
    @Argument
    private List<String> arguments = Lists.newArrayList();

    public static void main(String ... args) throws IOException {
        new SoyMsgExtractor().execMain(args);
    }

    private SoyMsgExtractor() {
    }

    private void execMain(String[] args) throws IOException, SoySyntaxException {
        File outputFile0;
        final CmdLineParser cmdLineParser = MainClassUtils.parseFlags(this, args, USAGE_PREFIX);
        Function<String, Void> exitWithErrorFn = new Function<String, Void>(){

            public Void apply(String errorMsg) {
                MainClassUtils.exitWithError(errorMsg, cmdLineParser, SoyMsgExtractor.USAGE_PREFIX);
                return null;
            }
        };
        Injector injector = MainClassUtils.createInjector(this.messagePluginModule, null);
        SoyFileSet.Builder sfsBuilder = (SoyFileSet.Builder)injector.getInstance(SoyFileSet.Builder.class);
        MainClassUtils.addSoyFilesToBuilder(sfsBuilder, this.inputPrefix, this.srcs, this.arguments, (Collection<String>)ImmutableList.of(), (Collection<String>)ImmutableList.of(), exitWithErrorFn);
        sfsBuilder.setAllowExternalCalls(this.allowExternalCalls);
        if (this.outputPathFormat.length() != 0) {
            if (this.outputFile.length() != 0) {
                exitWithErrorFn.apply((Object)"Must provide one of output file path or output path format.");
            }
            String inputFilePath = this.inputPrefix + (this.srcs.size() != 0 ? this.srcs.get(0) : this.arguments.get(0));
            String outputFilePath = MainEntryPointUtils.buildFilePath(this.outputPathFormat, null, inputFilePath, this.inputPrefix);
            outputFile0 = new File(outputFilePath);
        } else if (this.outputFile.length() != 0) {
            outputFile0 = new File(this.outputFile);
        } else {
            exitWithErrorFn.apply((Object)"Must provide output file path or output path format.");
            return;
        }
        SoyFileSet sfs = sfsBuilder.build();
        SoyMsgBundle msgBundle = sfs.extractMsgs();
        SoyMsgBundleHandler msgBundleHandler = (SoyMsgBundleHandler)injector.getInstance(SoyMsgBundleHandler.class);
        SoyMsgBundleHandler.OutputFileOptions options = new SoyMsgBundleHandler.OutputFileOptions();
        options.setSourceLocaleString(this.sourceLocaleString);
        if (this.targetLocaleString.length() > 0) {
            options.setTargetLocaleString(this.targetLocaleString);
        }
        msgBundleHandler.writeToExtractedMsgsFile(msgBundle, options, outputFile0);
    }
}

