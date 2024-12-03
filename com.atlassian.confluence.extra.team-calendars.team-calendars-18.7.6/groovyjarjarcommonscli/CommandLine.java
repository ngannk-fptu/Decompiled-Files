/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.Option;
import groovyjarjarcommonscli.ParseException;
import groovyjarjarcommonscli.TypeHandler;
import groovyjarjarcommonscli.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class CommandLine
implements Serializable {
    private static final long serialVersionUID = 1L;
    private List args = new LinkedList();
    private List options = new ArrayList();

    CommandLine() {
    }

    public boolean hasOption(String opt) {
        return this.options.contains(this.resolveOption(opt));
    }

    public boolean hasOption(char opt) {
        return this.hasOption(String.valueOf(opt));
    }

    public Object getOptionObject(String opt) {
        try {
            return this.getParsedOptionValue(opt);
        }
        catch (ParseException pe) {
            System.err.println("Exception found converting " + opt + " to desired type: " + pe.getMessage());
            return null;
        }
    }

    public Object getParsedOptionValue(String opt) throws ParseException {
        String res = this.getOptionValue(opt);
        Option option = this.resolveOption(opt);
        if (option == null) {
            return null;
        }
        Object type = option.getType();
        return res == null ? null : TypeHandler.createValue(res, type);
    }

    public Object getOptionObject(char opt) {
        return this.getOptionObject(String.valueOf(opt));
    }

    public String getOptionValue(String opt) {
        String[] values = this.getOptionValues(opt);
        return values == null ? null : values[0];
    }

    public String getOptionValue(char opt) {
        return this.getOptionValue(String.valueOf(opt));
    }

    public String[] getOptionValues(String opt) {
        ArrayList values = new ArrayList();
        Iterator it = this.options.iterator();
        while (it.hasNext()) {
            Option option = (Option)it.next();
            if (!opt.equals(option.getOpt()) && !opt.equals(option.getLongOpt())) continue;
            values.addAll(option.getValuesList());
        }
        return values.isEmpty() ? null : values.toArray(new String[values.size()]);
    }

    private Option resolveOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);
        Iterator it = this.options.iterator();
        while (it.hasNext()) {
            Option option = (Option)it.next();
            if (opt.equals(option.getOpt())) {
                return option;
            }
            if (!opt.equals(option.getLongOpt())) continue;
            return option;
        }
        return null;
    }

    public String[] getOptionValues(char opt) {
        return this.getOptionValues(String.valueOf(opt));
    }

    public String getOptionValue(String opt, String defaultValue) {
        String answer = this.getOptionValue(opt);
        return answer != null ? answer : defaultValue;
    }

    public String getOptionValue(char opt, String defaultValue) {
        return this.getOptionValue(String.valueOf(opt), defaultValue);
    }

    public Properties getOptionProperties(String opt) {
        Properties props = new Properties();
        Iterator it = this.options.iterator();
        while (it.hasNext()) {
            Option option = (Option)it.next();
            if (!opt.equals(option.getOpt()) && !opt.equals(option.getLongOpt())) continue;
            List values = option.getValuesList();
            if (values.size() >= 2) {
                props.put(values.get(0), values.get(1));
                continue;
            }
            if (values.size() != 1) continue;
            props.put(values.get(0), "true");
        }
        return props;
    }

    public String[] getArgs() {
        String[] answer = new String[this.args.size()];
        this.args.toArray(answer);
        return answer;
    }

    public List getArgList() {
        return this.args;
    }

    void addArg(String arg) {
        this.args.add(arg);
    }

    void addOption(Option opt) {
        this.options.add(opt);
    }

    public Iterator iterator() {
        return this.options.iterator();
    }

    public Option[] getOptions() {
        List processed = this.options;
        Option[] optionsArray = new Option[processed.size()];
        return processed.toArray(optionsArray);
    }
}

