/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.inject.Guice
 *  com.google.inject.Injector
 *  com.google.inject.Module
 *  javax.annotation.Nullable
 *  org.kohsuke.args4j.CmdLineException
 *  org.kohsuke.args4j.CmdLineParser
 *  org.kohsuke.args4j.OptionDef
 *  org.kohsuke.args4j.spi.OptionHandler
 *  org.kohsuke.args4j.spi.Parameters
 *  org.kohsuke.args4j.spi.Setter
 */
package com.google.template.soy;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.base.internal.SoyFileKind;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

class MainClassUtils {
    private MainClassUtils() {
    }

    public static CmdLineParser parseFlags(Object objWithFlags, String[] args, String usagePrefix) {
        CmdLineParser cmdLineParser = new CmdLineParser(objWithFlags);
        cmdLineParser.setUsageWidth(100);
        try {
            cmdLineParser.parseArgument(args);
        }
        catch (CmdLineException cle) {
            MainClassUtils.exitWithError(cle.getMessage(), cmdLineParser, usagePrefix);
        }
        return cmdLineParser;
    }

    public static void exitWithError(String errorMsg, CmdLineParser cmdLineParser, String usagePrefix) {
        System.err.println("\nError: " + errorMsg + "\n\n");
        System.err.println(usagePrefix);
        cmdLineParser.printUsage((OutputStream)System.err);
        System.exit(1);
    }

    public static Injector createInjector(String msgPluginModuleName, @Nullable String pluginModuleNames) {
        ArrayList guiceModules = Lists.newArrayListWithCapacity((int)2);
        guiceModules.add(new SoyModule());
        Preconditions.checkArgument((msgPluginModuleName != null && msgPluginModuleName.length() > 0 ? 1 : 0) != 0);
        guiceModules.add(MainClassUtils.instantiatePluginModule(msgPluginModuleName));
        if (pluginModuleNames != null && !pluginModuleNames.isEmpty()) {
            for (String pluginModuleName : Splitter.on((char)',').split((CharSequence)pluginModuleNames)) {
                guiceModules.add(MainClassUtils.instantiatePluginModule(pluginModuleName));
            }
        }
        return Guice.createInjector((Iterable)guiceModules);
    }

    public static Injector createInjector(@Nullable String pluginModuleNames) {
        ArrayList guiceModules = Lists.newArrayListWithCapacity((int)2);
        guiceModules.add(new SoyModule());
        if (pluginModuleNames != null && !pluginModuleNames.isEmpty()) {
            for (String pluginModuleName : Splitter.on((char)',').split((CharSequence)pluginModuleNames)) {
                guiceModules.add(MainClassUtils.instantiatePluginModule(pluginModuleName));
            }
        }
        return Guice.createInjector((Iterable)guiceModules);
    }

    private static Module instantiatePluginModule(String moduleName) {
        try {
            return (Module)Class.forName(moduleName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find plugin module \"" + moduleName + "\".", e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access plugin module \"" + moduleName + "\".", e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate plugin module \"" + moduleName + "\".", e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Failed instantiating plugin module \"" + moduleName + "\".", e.getTargetException());
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Plugin module \"" + moduleName + "\" has no nullary constructor.", e);
        }
    }

    public static void addSoyFilesToBuilder(SoyFileSet.Builder sfsBuilder, String inputPrefix, Collection<String> srcs, Collection<String> args, Collection<String> deps, Collection<String> indirectDeps, Function<String, Void> exitWithErrorFn) {
        if (srcs.size() == 0 && args.size() == 0) {
            exitWithErrorFn.apply((Object)"Must provide list of source Soy files (--srcs).");
        }
        if (srcs.size() != 0 && args.size() != 0) {
            exitWithErrorFn.apply((Object)"Found source Soy files from --srcs and from args (please use --srcs only).");
        }
        ImmutableSet srcsSet = ImmutableSet.builder().addAll(srcs).addAll(args).build();
        Sets.SetView depsSet = Sets.difference((Set)ImmutableSet.copyOf(deps), (Set)srcsSet);
        Sets.SetView indirectDepsSet = Sets.difference((Set)ImmutableSet.copyOf(indirectDeps), (Set)Sets.union((Set)srcsSet, (Set)depsSet));
        for (String src : srcsSet) {
            sfsBuilder.addWithKind(new File(inputPrefix + src), SoyFileKind.SRC);
        }
        for (String dep : depsSet) {
            sfsBuilder.addWithKind(new File(inputPrefix + dep), SoyFileKind.DEP);
        }
        for (String dep : indirectDepsSet) {
            sfsBuilder.addWithKind(new File(inputPrefix + dep), SoyFileKind.INDIRECT_DEP);
        }
    }

    public static class StringListOptionHandler
    extends ListOptionHandler<String> {
        public StringListOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super String> setter) {
            super(parser, option, setter);
        }

        @Override
        public String parseItem(String item) {
            return item;
        }
    }

    public static abstract class ListOptionHandler<T>
    extends OptionHandler<T> {
        public ListOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super T> setter) {
            super(parser, option, setter);
        }

        public abstract T parseItem(String var1);

        public int parseArguments(Parameters params) throws CmdLineException {
            for (String item : params.getParameter(0).split(",")) {
                this.setter.addValue(this.parseItem(item));
            }
            return 1;
        }

        public String getDefaultMetaVariable() {
            return "ITEM,ITEM,...";
        }
    }

    public static class BooleanOptionHandler
    extends OptionHandler<Boolean> {
        public BooleanOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Boolean> setter) {
            super(parser, option, setter);
        }

        public int parseArguments(Parameters params) throws CmdLineException {
            boolean hasParam;
            boolean value;
            try {
                String nextArg = params.getParameter(0);
                if (nextArg.equals("true") || nextArg.equals("1")) {
                    value = true;
                    hasParam = true;
                } else if (nextArg.equals("false") || nextArg.equals("0")) {
                    value = false;
                    hasParam = true;
                } else {
                    value = true;
                    hasParam = false;
                }
            }
            catch (CmdLineException e) {
                value = true;
                hasParam = false;
            }
            this.setter.addValue((Object)value);
            return hasParam ? 1 : 0;
        }

        public String getDefaultMetaVariable() {
            return null;
        }
    }
}

