/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import java.util.Collections;
import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.Message;
import org.aspectj.util.LangUtil;

public class Options {
    private static final String OPTION_15 = "-1.5";
    private static final String OPTION_lazyTjp = "-XlazyTjp";
    private static final String OPTION_noWarn = "-nowarn";
    private static final String OPTION_noWarnNone = "-warn:none";
    private static final String OPTION_proceedOnError = "-proceedOnError";
    private static final String OPTION_verbose = "-verbose";
    private static final String OPTION_debug = "-debug";
    private static final String OPTION_reweavable = "-Xreweavable";
    private static final String OPTION_noinline = "-Xnoinline";
    private static final String OPTION_addSerialVersionUID = "-XaddSerialVersionUID";
    private static final String OPTION_hasMember = "-XhasMember";
    private static final String OPTION_pinpoint = "-Xdev:pinpoint";
    private static final String OPTION_showWeaveInfo = "-showWeaveInfo";
    private static final String OPTIONVALUED_messageHandler = "-XmessageHandlerClass:";
    private static final String OPTIONVALUED_Xlintfile = "-Xlintfile:";
    private static final String OPTIONVALUED_Xlint = "-Xlint:";
    private static final String OPTIONVALUED_joinpoints = "-Xjoinpoints:";
    private static final String OPTIONVALUED_Xset = "-Xset:";
    private static final String OPTION_timers = "-timers";
    private static final String OPTIONVALUED_loadersToSkip = "-loadersToSkip:";

    public static WeaverOption parse(String options, ClassLoader laoder, IMessageHandler imh) {
        WeaverOption weaverOption = new WeaverOption(imh);
        if (LangUtil.isEmpty(options)) {
            return weaverOption;
        }
        List<String> flags = LangUtil.anySplit(options, " ");
        Collections.reverse(flags);
        for (String arg : flags) {
            if (!arg.startsWith(OPTIONVALUED_messageHandler) || arg.length() <= OPTIONVALUED_messageHandler.length()) continue;
            String handlerClass = arg.substring(OPTIONVALUED_messageHandler.length()).trim();
            try {
                Class<?> handler = Class.forName(handlerClass, false, laoder);
                weaverOption.messageHandler = (IMessageHandler)handler.newInstance();
            }
            catch (Throwable t) {
                weaverOption.messageHandler.handleMessage(new Message("Cannot instantiate message handler " + handlerClass, IMessage.ERROR, t, null));
            }
        }
        for (String arg : flags) {
            if (arg.equals(OPTION_15)) {
                weaverOption.java5 = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_lazyTjp)) {
                weaverOption.lazyTjp = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_noinline)) {
                weaverOption.noInline = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_addSerialVersionUID)) {
                weaverOption.addSerialVersionUID = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_noWarn) || arg.equalsIgnoreCase(OPTION_noWarnNone)) {
                weaverOption.noWarn = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_proceedOnError)) {
                weaverOption.proceedOnError = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_reweavable)) {
                weaverOption.notReWeavable = false;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_showWeaveInfo)) {
                weaverOption.showWeaveInfo = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_hasMember)) {
                weaverOption.hasMember = true;
                continue;
            }
            if (arg.startsWith(OPTIONVALUED_joinpoints)) {
                if (arg.length() <= OPTIONVALUED_joinpoints.length()) continue;
                weaverOption.optionalJoinpoints = arg.substring(OPTIONVALUED_joinpoints.length()).trim();
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_verbose)) {
                weaverOption.verbose = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_debug)) {
                weaverOption.debug = true;
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_pinpoint)) {
                weaverOption.pinpoint = true;
                continue;
            }
            if (arg.startsWith(OPTIONVALUED_messageHandler)) continue;
            if (arg.startsWith(OPTIONVALUED_Xlintfile)) {
                if (arg.length() <= OPTIONVALUED_Xlintfile.length()) continue;
                weaverOption.lintFile = arg.substring(OPTIONVALUED_Xlintfile.length()).trim();
                continue;
            }
            if (arg.startsWith(OPTIONVALUED_Xlint)) {
                if (arg.length() <= OPTIONVALUED_Xlint.length()) continue;
                weaverOption.lint = arg.substring(OPTIONVALUED_Xlint.length()).trim();
                continue;
            }
            if (arg.startsWith(OPTIONVALUED_Xset)) {
                if (arg.length() <= OPTIONVALUED_Xlint.length()) continue;
                weaverOption.xSet = arg.substring(OPTIONVALUED_Xset.length()).trim();
                continue;
            }
            if (arg.equalsIgnoreCase(OPTION_timers)) {
                weaverOption.timers = true;
                continue;
            }
            if (arg.startsWith(OPTIONVALUED_loadersToSkip)) {
                String value;
                if (arg.length() <= OPTIONVALUED_loadersToSkip.length()) continue;
                weaverOption.loadersToSkip = value = arg.substring(OPTIONVALUED_loadersToSkip.length()).trim();
                continue;
            }
            weaverOption.messageHandler.handleMessage(new Message("Cannot configure weaver with option '" + arg + "': unknown option", IMessage.WARNING, null, null));
        }
        if (weaverOption.noWarn) {
            weaverOption.messageHandler.ignore(IMessage.WARNING);
        }
        if (weaverOption.verbose) {
            weaverOption.messageHandler.dontIgnore(IMessage.INFO);
        }
        if (weaverOption.debug) {
            weaverOption.messageHandler.dontIgnore(IMessage.DEBUG);
        }
        if (weaverOption.showWeaveInfo) {
            weaverOption.messageHandler.dontIgnore(IMessage.WEAVEINFO);
        }
        return weaverOption;
    }

    public static class WeaverOption {
        boolean java5;
        boolean lazyTjp;
        boolean hasMember;
        boolean timers = false;
        String optionalJoinpoints;
        boolean noWarn;
        boolean proceedOnError;
        boolean verbose;
        boolean debug;
        boolean notReWeavable = true;
        boolean noInline;
        boolean addSerialVersionUID;
        boolean showWeaveInfo;
        boolean pinpoint;
        IMessageHandler messageHandler;
        String lint;
        String lintFile;
        String xSet;
        String loadersToSkip;

        public WeaverOption(IMessageHandler imh) {
            this.messageHandler = imh;
        }
    }
}

