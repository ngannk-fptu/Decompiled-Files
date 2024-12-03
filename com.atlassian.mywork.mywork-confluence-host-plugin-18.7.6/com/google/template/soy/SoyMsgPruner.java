/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.kohsuke.args4j.Argument
 *  org.kohsuke.args4j.CmdLineParser
 *  org.kohsuke.args4j.Option
 */
package com.google.template.soy;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.template.soy.MainClassUtils;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.shared.internal.MainEntryPointUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class SoyMsgPruner {
    private static final String USAGE_PREFIX = "Usage:\njava com.google.template.soy.SoyMsgPruner  \\\n             [<flag_1> <flag_2> ...]  \\\n             --inputMsgFilePathFormat <formatString>  \\\n             --outputMsgFilePathFormat <formatString>  \\\n             --locales <locale>,... --srcs <soyFilePath>,...\n";
    @Option(name="--inputPrefix", usage="If provided, this path prefix will be prepended to each input file path. This is a literal string prefix, so you'll need to include a trailing slash if necessary.")
    private String inputPrefix = "";
    @Option(name="--srcs", usage="[Required] The list of source Soy files.", handler=MainClassUtils.StringListOptionHandler.class)
    private List<String> srcs = Lists.newArrayList();
    @Option(name="--allowExternalCalls", usage="Whether to allow external calls. New projects should set this to false, and existing projects should remove existing external calls and then set this to false. It will save you a lot of headaches. Currently defaults to true for backward compatibility.", handler=MainClassUtils.BooleanOptionHandler.class)
    private boolean allowExternalCalls = true;
    @Option(name="--locales", usage="[Required] Comma-delimited list of locales.", handler=MainClassUtils.StringListOptionHandler.class)
    private List<String> locales = Lists.newArrayList();
    @Option(name="--inputMsgFilePathFormat", usage="[Required] A format string that specifies how to build the path to each translated messages file. The format string can include literal characters as well as the placeholders {INPUT_PREFIX}, {LOCALE}, and {LOCALE_LOWER_CASE}. Note {LOCALE_LOWER_CASE} also turns dash into underscore, e.g. pt-BR becomes pt_br. The format string must end with an extension matching the message file format (case-insensitive).")
    private String inputMsgFilePathFormat = "";
    @Option(name="--outputMsgFilePathFormat", usage="[Required] A format string that specifies how to build the path to each pruned output translated messages file. The format string can include literal characters as well as the placeholders {INPUT_PREFIX}, {LOCALE}, and {LOCALE_LOWER_CASE}. Note {LOCALE_LOWER_CASE} also turns dash into underscore, e.g. pt-BR becomes pt_br. The format string must end with an extension matching the message file format (case-insensitive).")
    private String outputMsgFilePathFormat = "";
    @Option(name="--msgPluginModule", usage="Specifies the full class name of a Guice module that binds a BidirectionalSoyMsgPlugin.")
    private String msgPluginModule = "";
    @Argument
    private List<String> arguments = Lists.newArrayList();

    public static void main(String[] args) throws IOException, SoySyntaxException {
        new SoyMsgPruner().execMain(args);
    }

    private SoyMsgPruner() {
    }

    private void execMain(String[] args) throws IOException, SoySyntaxException {
        final CmdLineParser cmdLineParser = MainClassUtils.parseFlags(this, args, USAGE_PREFIX);
        Function<String, Void> exitWithErrorFn = new Function<String, Void>(){

            public Void apply(String errorMsg) {
                MainClassUtils.exitWithError(errorMsg, cmdLineParser, SoyMsgPruner.USAGE_PREFIX);
                return null;
            }
        };
        if (this.arguments.size() > 1) {
            MainClassUtils.exitWithError("Unrecognized args left on command line: \"" + this.arguments + "\".", cmdLineParser, USAGE_PREFIX);
        }
        Injector injector = MainClassUtils.createInjector(this.msgPluginModule, null);
        SoyFileSet.Builder sfsBuilder = injector.getInstance(SoyFileSet.Builder.class);
        MainClassUtils.addSoyFilesToBuilder(sfsBuilder, this.inputPrefix, this.srcs, (Collection<String>)ImmutableSet.of(), (Collection<String>)ImmutableSet.of(), (Collection<String>)ImmutableSet.of(), exitWithErrorFn);
        sfsBuilder.setAllowExternalCalls(this.allowExternalCalls);
        SoyFileSet sfs = sfsBuilder.build();
        SoyMsgBundleHandler msgBundleHandler = injector.getInstance(SoyMsgBundleHandler.class);
        for (String locale : this.locales) {
            String inputMsgFilePath = MainEntryPointUtils.buildFilePath(this.inputMsgFilePathFormat, locale, null, this.inputPrefix);
            SoyMsgBundle origTransMsgBundle = msgBundleHandler.createFromFile(new File(inputMsgFilePath));
            if (origTransMsgBundle.getLocaleString() == null) {
                throw new IOException("Error opening or parsing message file " + inputMsgFilePath);
            }
            SoyMsgBundle prunedTransSoyMsgBundle = sfs.pruneTranslatedMsgs(origTransMsgBundle);
            String outputMsgFilePath = MainEntryPointUtils.buildFilePath(this.outputMsgFilePathFormat, locale, inputMsgFilePath, this.inputPrefix);
            msgBundleHandler.writeToTranslatedMsgsFile(prunedTransSoyMsgBundle, new SoyMsgBundleHandler.OutputFileOptions(), new File(outputMsgFilePath));
        }
    }
}

