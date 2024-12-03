/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.CommandLineParser;
import groovyjarjarcommonscli.MissingArgumentException;
import groovyjarjarcommonscli.MissingOptionException;
import groovyjarjarcommonscli.Option;
import groovyjarjarcommonscli.OptionGroup;
import groovyjarjarcommonscli.Options;
import groovyjarjarcommonscli.ParseException;
import groovyjarjarcommonscli.UnrecognizedOptionException;
import groovyjarjarcommonscli.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

public abstract class Parser
implements CommandLineParser {
    protected CommandLine cmd;
    private Options options;
    private List requiredOptions;

    protected void setOptions(Options options) {
        this.options = options;
        this.requiredOptions = new ArrayList(options.getRequiredOptions());
    }

    protected Options getOptions() {
        return this.options;
    }

    protected List getRequiredOptions() {
        return this.requiredOptions;
    }

    protected abstract String[] flatten(Options var1, String[] var2, boolean var3);

    public CommandLine parse(Options options, String[] arguments) throws ParseException {
        return this.parse(options, arguments, null, false);
    }

    public CommandLine parse(Options options, String[] arguments, Properties properties) throws ParseException {
        return this.parse(options, arguments, properties, false);
    }

    public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
        return this.parse(options, arguments, null, stopAtNonOption);
    }

    public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) throws ParseException {
        Iterator it = options.helpOptions().iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            opt.clearValues();
        }
        this.setOptions(options);
        this.cmd = new CommandLine();
        boolean eatTheRest = false;
        if (arguments == null) {
            arguments = new String[]{};
        }
        List<String> tokenList = Arrays.asList(this.flatten(this.getOptions(), arguments, stopAtNonOption));
        ListIterator<String> iterator = tokenList.listIterator();
        while (iterator.hasNext()) {
            String t = iterator.next();
            if ("--".equals(t)) {
                eatTheRest = true;
            } else if ("-".equals(t)) {
                if (stopAtNonOption) {
                    eatTheRest = true;
                } else {
                    this.cmd.addArg(t);
                }
            } else if (t.startsWith("-")) {
                if (stopAtNonOption && !this.getOptions().hasOption(t)) {
                    eatTheRest = true;
                    this.cmd.addArg(t);
                } else {
                    this.processOption(t, iterator);
                }
            } else {
                this.cmd.addArg(t);
                if (stopAtNonOption) {
                    eatTheRest = true;
                }
            }
            if (!eatTheRest) continue;
            while (iterator.hasNext()) {
                String str = iterator.next();
                if ("--".equals(str)) continue;
                this.cmd.addArg(str);
            }
        }
        this.processProperties(properties);
        this.checkRequiredOptions();
        return this.cmd;
    }

    protected void processProperties(Properties properties) {
        if (properties == null) {
            return;
        }
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String option = e.nextElement().toString();
            if (this.cmd.hasOption(option)) continue;
            Option opt = this.getOptions().getOption(option);
            String value = properties.getProperty(option);
            if (opt.hasArg()) {
                if (opt.getValues() == null || opt.getValues().length == 0) {
                    try {
                        opt.addValueForProcessing(value);
                    }
                    catch (RuntimeException exp) {}
                }
            } else if (!"yes".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"1".equalsIgnoreCase(value)) break;
            this.cmd.addOption(opt);
        }
    }

    protected void checkRequiredOptions() throws MissingOptionException {
        if (!this.getRequiredOptions().isEmpty()) {
            throw new MissingOptionException(this.getRequiredOptions());
        }
    }

    public void processArgs(Option opt, ListIterator iter) throws ParseException {
        while (iter.hasNext()) {
            String str = (String)iter.next();
            if (this.getOptions().hasOption(str) && str.startsWith("-")) {
                iter.previous();
                break;
            }
            try {
                opt.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(str));
            }
            catch (RuntimeException exp) {
                iter.previous();
                break;
            }
        }
        if (opt.getValues() == null && !opt.hasOptionalArg()) {
            throw new MissingArgumentException(opt);
        }
    }

    protected void processOption(String arg, ListIterator iter) throws ParseException {
        boolean hasOption = this.getOptions().hasOption(arg);
        if (!hasOption) {
            throw new UnrecognizedOptionException("Unrecognized option: " + arg, arg);
        }
        Option opt = (Option)this.getOptions().getOption(arg).clone();
        if (opt.isRequired()) {
            this.getRequiredOptions().remove(opt.getKey());
        }
        if (this.getOptions().getOptionGroup(opt) != null) {
            OptionGroup group = this.getOptions().getOptionGroup(opt);
            if (group.isRequired()) {
                this.getRequiredOptions().remove(group);
            }
            group.setSelected(opt);
        }
        if (opt.hasArg()) {
            this.processArgs(opt, iter);
        }
        this.cmd.addOption(opt);
    }
}

