/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.gen;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.DefaultAuthenticator;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Parser;

public class WSDL2 {
    protected static final int DEBUG_OPT = 68;
    protected static final int HELP_OPT = 104;
    protected static final int NETWORK_TIMEOUT_OPT = 79;
    protected static final int NOIMPORTS_OPT = 110;
    protected static final int VERBOSE_OPT = 118;
    protected static final int NOWRAP_OPT = 87;
    protected static final int QUIET_OPT = 113;
    protected CLOptionDescriptor[] options = new CLOptionDescriptor[]{new CLOptionDescriptor("help", 8, 104, Messages.getMessage("optionHelp00")), new CLOptionDescriptor("verbose", 8, 118, Messages.getMessage("optionVerbose00")), new CLOptionDescriptor("noImports", 8, 110, Messages.getMessage("optionImport00")), new CLOptionDescriptor("timeout", 2, 79, Messages.getMessage("optionTimeout00")), new CLOptionDescriptor("Debug", 8, 68, Messages.getMessage("optionDebug00")), new CLOptionDescriptor("noWrapped", 8, 87, Messages.getMessage("optionNoWrap00")), new CLOptionDescriptor("quiet", 8, 113, Messages.getMessage("optionQuiet"))};
    protected String wsdlURI = null;
    protected Parser parser = this.createParser();

    protected WSDL2() {
    }

    protected Parser createParser() {
        return new Parser();
    }

    protected Parser getParser() {
        return this.parser;
    }

    protected void addOptions(CLOptionDescriptor[] newOptions) {
        if (newOptions != null && newOptions.length > 0) {
            CLOptionDescriptor[] allOptions = new CLOptionDescriptor[this.options.length + newOptions.length];
            System.arraycopy(this.options, 0, allOptions, 0, this.options.length);
            System.arraycopy(newOptions, 0, allOptions, this.options.length, newOptions.length);
            this.options = allOptions;
        }
    }

    protected void removeOption(String name) {
        int foundOptionIndex = -1;
        for (int i = 0; i < this.options.length; ++i) {
            if (!this.options[i].getName().equals(name)) continue;
            foundOptionIndex = i;
            break;
        }
        if (foundOptionIndex != -1) {
            CLOptionDescriptor[] newOptions = new CLOptionDescriptor[this.options.length - 1];
            System.arraycopy(this.options, 0, newOptions, 0, foundOptionIndex);
            if (foundOptionIndex < newOptions.length) {
                System.arraycopy(this.options, foundOptionIndex + 1, newOptions, foundOptionIndex, newOptions.length - foundOptionIndex);
            }
            this.options = newOptions;
        }
    }

    protected void parseOption(CLOption option) {
        switch (option.getId()) {
            case 0: {
                if (this.wsdlURI != null) {
                    System.out.println(Messages.getMessage("w2jDuplicateWSDLURI00", this.wsdlURI, option.getArgument()));
                    this.printUsage();
                }
                this.wsdlURI = option.getArgument();
                break;
            }
            case 104: {
                this.printUsage();
                break;
            }
            case 110: {
                this.parser.setImports(false);
                break;
            }
            case 79: {
                String timeoutValue = option.getArgument();
                long timeout = Long.parseLong(timeoutValue);
                if (timeout > 0L) {
                    timeout *= 1000L;
                }
                this.parser.setTimeout(timeout);
                break;
            }
            case 118: {
                this.parser.setVerbose(true);
                break;
            }
            case 68: {
                this.parser.setDebug(true);
                break;
            }
            case 113: {
                this.parser.setQuiet(true);
                break;
            }
            case 87: {
                this.parser.setNowrap(true);
            }
        }
    }

    protected void validateOptions() {
        if (this.wsdlURI == null) {
            System.out.println(Messages.getMessage("w2jMissingWSDLURI00"));
            this.printUsage();
        }
        if (this.parser.isQuiet()) {
            if (this.parser.isVerbose()) {
                System.out.println(Messages.getMessage("exclusiveQuietVerbose"));
                this.printUsage();
            }
            if (this.parser.isDebug()) {
                System.out.println(Messages.getMessage("exclusiveQuietDebug"));
                this.printUsage();
            }
        }
        this.checkForAuthInfo(this.wsdlURI);
        Authenticator.setDefault(new DefaultAuthenticator(this.parser.getUsername(), this.parser.getPassword()));
    }

    private void checkForAuthInfo(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        }
        catch (MalformedURLException e) {
            return;
        }
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            int i = userInfo.indexOf(58);
            if (i >= 0) {
                this.parser.setUsername(userInfo.substring(0, i));
                this.parser.setPassword(userInfo.substring(i + 1));
            } else {
                this.parser.setUsername(userInfo);
            }
        }
    }

    protected void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append(Messages.getMessage("usage00", "java " + this.getClass().getName() + " [options] WSDL-URI")).append(lSep);
        msg.append(Messages.getMessage("options00")).append(lSep);
        msg.append(CLUtil.describeOptions(this.options).toString());
        System.out.println(msg.toString());
        System.exit(1);
    }

    protected void run(String[] args) {
        CLArgsParser argsParser = new CLArgsParser(args, this.options);
        if (null != argsParser.getErrorString()) {
            System.err.println(Messages.getMessage("error01", argsParser.getErrorString()));
            this.printUsage();
        }
        Vector clOptions = argsParser.getArguments();
        int size = clOptions.size();
        try {
            for (int i = 0; i < size; ++i) {
                this.parseOption((CLOption)clOptions.get(i));
            }
            this.validateOptions();
            this.parser.run(this.wsdlURI);
            System.exit(0);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        WSDL2 wsdl2 = new WSDL2();
        wsdl2.run(args);
    }
}

