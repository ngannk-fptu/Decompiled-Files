/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.getopt;

import aQute.configurable.Config;
import aQute.configurable.Configurable;
import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.CommandLineMessages;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.justif.Justif;
import aQute.lib.markdown.MarkdownFormatter;
import aQute.libg.generics.Create;
import aQute.libg.reporter.ReporterMessages;
import aQute.service.reporter.Reporter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLine {
    static int LINELENGTH = 60;
    static Pattern ASSIGNMENT = Pattern.compile("(\\w[\\w\\d]*+)\\s*=\\s*([^\\s]+)\\s*");
    Reporter reporter;
    Justif justif = new Justif(80, 30, 32, 70);
    CommandLineMessages msg;
    private Object result;
    static Pattern LAST_PART = Pattern.compile(".*[\\$\\.]([^\\$\\.]+)");

    public CommandLine(Reporter reporter) {
        this.reporter = reporter;
        this.msg = ReporterMessages.base(reporter, CommandLineMessages.class);
    }

    public String execute(Object target, String cmd, List<String> input) throws Exception {
        if (cmd.equals("help")) {
            StringBuilder sb = new StringBuilder();
            Formatter f = new Formatter(sb);
            if (input.isEmpty()) {
                this.help(f, target);
            } else {
                for (String s : input) {
                    this.help(f, target, s);
                }
            }
            f.flush();
            this.justif.wrap(sb);
            return sb.toString();
        }
        ArrayList<String> arguments = new ArrayList<String>(input);
        Map<String, Method> commands = this.getCommands(target);
        Method m = commands.get(cmd);
        if (m == null) {
            this.msg.NoSuchCommand_(cmd);
            return this.help(target, null, null);
        }
        Class<?> optionClass = m.getParameterTypes()[0];
        Object options = this.getOptions(optionClass, arguments);
        if (options == null) {
            return this.help(target, cmd, null);
        }
        Arguments argumentsAnnotation = optionClass.getAnnotation(Arguments.class);
        if (argumentsAnnotation != null) {
            int i;
            String[] patterns = argumentsAnnotation.arg();
            if (patterns.length == 0 && arguments.size() > 0) {
                this.msg.TooManyArguments_(arguments);
                return this.help(target, cmd, null);
            }
            for (i = 0; i < patterns.length; ++i) {
                String pattern = patterns[i];
                boolean optional = pattern.matches("\\[.*\\]");
                if (pattern.contains("...")) {
                    i = Integer.MAX_VALUE;
                    break;
                }
                if (i < arguments.size() || optional) continue;
                this.msg.MissingArgument_(patterns[i]);
                return this.help(target, cmd, optionClass);
            }
            if (i < arguments.size()) {
                this.msg.TooManyArguments_(arguments);
                return this.help(target, cmd, optionClass);
            }
        }
        if (this.reporter.getErrors().size() == 0) {
            m.setAccessible(true);
            this.result = m.invoke(target, options);
            return null;
        }
        return this.help(target, cmd, optionClass);
    }

    public void generateDocumentation(Object target, Appendable out) {
        MarkdownFormatter f = new MarkdownFormatter(out);
        f.h1("Available Commands:", new Object[0]);
        Map<String, Method> commands = this.getCommands(target);
        for (String command : commands.keySet()) {
            Class<?> specification = commands.get(command).getParameterTypes()[0];
            Map<String, Method> options = this.getOptions(specification);
            Arguments patterns = specification.getAnnotation(Arguments.class);
            f.h2(command, new Object[0]);
            Description descr = specification.getAnnotation(Description.class);
            if (descr != null) {
                f.format("%s%n%n", descr.value());
            }
            f.h3("Synopsis:", new Object[0]);
            f.code(this.getSynopsis(command, options, patterns), new Object[0]);
            if (options.isEmpty()) continue;
            f.h3("Options:", new Object[0]);
            for (Map.Entry<String, Method> entry : options.entrySet()) {
                Option option = this.getOption(entry.getKey(), entry.getValue());
                f.inlineCode("%s -%s --%s %s%s", option.required ? " " : "[", Character.valueOf(option.shortcut), option.name, option.paramType, option.required ? " " : "]");
                if (option.description == null) continue;
                f.format("%s", option.description);
                f.endP();
            }
            f.format("%n", new Object[0]);
        }
        f.flush();
    }

    private String help(Object target, String cmd, Class<? extends Options> type) throws Exception {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        if (cmd == null) {
            this.help(f, target);
        } else if (type == null) {
            this.help(f, target, cmd);
        } else {
            this.help(f, target, cmd, type);
        }
        f.flush();
        this.justif.wrap(sb);
        return sb.toString();
    }

    public <T extends Options> T getOptions(Class<T> specification, List<String> arguments) throws Exception {
        Method m;
        Map<String, String> properties = Create.map();
        HashMap<String, Object> values = new HashMap<String, Object>();
        Map<String, Method> options = this.getOptions(specification);
        while (arguments.size() > 0) {
            String option = arguments.get(0);
            if (option.startsWith("-")) {
                arguments.remove(0);
                if (option.startsWith("--")) {
                    if ("--".equals(option)) break;
                    String name = option.substring(2);
                    m = options.get(name);
                    if (m == null) {
                        m = options.get(Character.toLowerCase(name.charAt(0)) + name.substring(1));
                    }
                    if (m == null) {
                        this.msg.UnrecognizedOption_(name);
                        continue;
                    }
                    this.assignOptionValue(values, m, arguments, true);
                    continue;
                }
                block1: for (int j = 1; j < option.length(); ++j) {
                    char optionChar = option.charAt(j);
                    for (Map.Entry<String, Method> entry : options.entrySet()) {
                        if (entry.getKey().charAt(0) != optionChar) continue;
                        boolean last = j + 1 >= option.length();
                        this.assignOptionValue(values, entry.getValue(), arguments, last);
                        continue block1;
                    }
                    this.msg.UnrecognizedOption_(optionChar + "");
                }
                continue;
            }
            Matcher m2 = ASSIGNMENT.matcher(option);
            if (!m2.matches()) break;
            properties.put(m2.group(1), m2.group(2));
            break;
        }
        for (Map.Entry<String, Method> entry : options.entrySet()) {
            m = entry.getValue();
            String name = entry.getKey();
            if (values.containsKey(name) || !this.isMandatory(m)) continue;
            this.msg.OptionNotSet_(name);
        }
        values.put(".", arguments);
        values.put(".arguments", arguments);
        values.put(".command", this);
        values.put(".properties", properties);
        return (T)((Options)Configurable.createConfigurable(specification, values));
    }

    private Map<String, Method> getOptions(Class<? extends Options> interf) {
        TreeMap<String, Method> map = new TreeMap<String, Method>(String.CASE_INSENSITIVE_ORDER);
        for (Method m : interf.getMethods()) {
            if (m.getName().startsWith("_")) continue;
            Config cfg = m.getAnnotation(Config.class);
            String name = cfg == null || cfg.id() == null || cfg.id().equals("<<NULL>>") ? m.getName() : cfg.id();
            map.put(name, m);
        }
        char prevChar = '\u0000';
        boolean throwOnNextMatch = false;
        HashMap toModify = new HashMap();
        for (String name : map.keySet()) {
            if (Character.toLowerCase(name.charAt(0)) != name.charAt(0)) {
                throw new Error("Only commands with lower case first char are acceptable (" + name + ")");
            }
            if (Character.toLowerCase(name.charAt(0)) == prevChar) {
                if (throwOnNextMatch) {
                    throw new Error("3 options with same first letter (one is: " + name + ")");
                }
                toModify.put(name, map.get(name));
                throwOnNextMatch = true;
                continue;
            }
            throwOnNextMatch = false;
            prevChar = name.charAt(0);
        }
        for (String name : toModify.keySet()) {
            map.remove(name);
            String newName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            map.put(newName, (Method)toModify.get(name));
        }
        return map;
    }

    public void assignOptionValue(Map<String, Object> options, Method m, List<String> args, boolean last) {
        String name = m.getName();
        Type type = m.getGenericReturnType();
        if (this.isOption(m)) {
            options.put(name, true);
        } else {
            if (!last) {
                this.msg.Option__WithArgumentNotLastInAbbreviation_(name, name.charAt(0), this.getTypeDescriptor(type));
                return;
            }
            if (args.isEmpty()) {
                this.msg.MissingArgument__(name, name.charAt(0));
                return;
            }
            String parameter = args.remove(0);
            if (Collection.class.isAssignableFrom(m.getReturnType())) {
                ArrayList<String> optionValues = (ArrayList<String>)options.get(m.getName());
                if (optionValues == null) {
                    optionValues = new ArrayList<String>();
                    options.put(name, optionValues);
                }
                optionValues.add(parameter);
            } else {
                if (options.containsKey(name)) {
                    this.msg.OptionCanOnlyOccurOnce_(name);
                    return;
                }
                options.put(name, parameter);
            }
        }
    }

    public void help(Formatter f, Object target, String cmd, Class<? extends Options> specification) {
        Description descr = specification.getAnnotation(Description.class);
        Arguments patterns = specification.getAnnotation(Arguments.class);
        String description = descr == null ? "" : descr.value();
        f.format("%nNAME%n  %s \t0- \t1%s%n%n", cmd, description);
        Map<String, Method> options = this.getOptions(specification);
        f.format("SYNOPSIS%n", new Object[0]);
        f.format(this.getSynopsis(cmd, options, patterns), new Object[0]);
        this.help(f, specification, "OPTIONS");
    }

    private void help(Formatter f, Class<? extends Options> specification, String title) {
        Map<String, Method> options = this.getOptions(specification);
        if (!options.isEmpty()) {
            f.format("%n%s%n%n", title);
            for (Map.Entry<String, Method> entry : options.entrySet()) {
                Option option = this.getOption(entry.getKey(), entry.getValue());
                f.format("   %s -%s, --%s %s%s \t0- \t1%s%n", option.required ? " " : "[", Character.valueOf(option.shortcut), option.name, option.paramType, option.required ? " " : "]", option.description);
            }
            f.format("%n", new Object[0]);
        }
    }

    private Option getOption(String optionName, Method m) {
        Option option = new Option();
        Config cfg = m.getAnnotation(Config.class);
        Description d = m.getAnnotation(Description.class);
        option.shortcut = optionName.charAt(0);
        option.name = Character.toLowerCase(optionName.charAt(0)) + optionName.substring(1);
        option.description = cfg != null ? cfg.description() : (d == null ? "" : d.value());
        option.required = this.isMandatory(m);
        String pt = this.getTypeDescriptor(m.getGenericReturnType());
        if (pt.length() != 0) {
            pt = pt + " ";
        }
        option.paramType = pt;
        return option;
    }

    private String getSynopsis(String cmd, Map<String, Method> options, Arguments patterns) {
        StringBuilder sb = new StringBuilder();
        if (options.isEmpty()) {
            sb.append(String.format("   %s ", cmd));
        } else {
            sb.append(String.format("   %s [options] ", cmd));
        }
        if (patterns == null) {
            sb.append(String.format(" ...%n%n", new Object[0]));
        } else {
            String del = " ";
            for (String pattern : patterns.arg()) {
                if (pattern.equals("...")) {
                    sb.append(String.format("%s...", del));
                } else {
                    sb.append(String.format("%s<%s>", del, pattern));
                }
                del = " ";
            }
            sb.append(String.format("%n", new Object[0]));
        }
        return sb.toString();
    }

    private static String lastPart(String name) {
        Matcher m = LAST_PART.matcher(name);
        if (m.matches()) {
            return m.group(1);
        }
        return name;
    }

    public void help(Formatter f, Object target) throws Exception {
        f.format("%n", new Object[0]);
        Description descr = target.getClass().getAnnotation(Description.class);
        if (descr != null) {
            f.format("%s%n%n", descr.value());
        }
        for (Map.Entry<String, Method> e : this.getCommands(target).entrySet()) {
            Method m = e.getValue();
            if (!m.getName().startsWith("__")) continue;
            Class<?> options = m.getParameterTypes()[0];
            this.help(f, options, "MAIN OPTIONS");
        }
        f.format("Available sub-commands: %n%n", new Object[0]);
        for (Map.Entry<String, Method> e : this.getCommands(target).entrySet()) {
            if (e.getValue().getName().startsWith("__")) continue;
            Description d = e.getValue().getAnnotation(Description.class);
            String desc = " ";
            if (d != null) {
                desc = d.value();
            }
            f.format("  %s\t0-\t1%s %n", e.getKey(), desc);
        }
        f.format("%n", new Object[0]);
    }

    public void help(Formatter f, Object target, String cmd) {
        Method m = this.getCommands(target).get(cmd);
        if (m == null) {
            f.format("No such command: %s%n", cmd);
        } else {
            Class<?> options = m.getParameterTypes()[0];
            this.help(f, target, cmd, options);
        }
    }

    public Map<String, Method> getCommands(Object target) {
        TreeMap<String, Method> map = new TreeMap<String, Method>();
        for (Method m : target.getClass().getMethods()) {
            Class<?> clazz;
            if (m.getParameterTypes().length != 1 || !m.getName().startsWith("_") || !Options.class.isAssignableFrom(clazz = m.getParameterTypes()[0])) continue;
            String name = m.getName().substring(1);
            map.put(name, m);
        }
        return map;
    }

    private boolean isMandatory(Method m) {
        Config cfg = m.getAnnotation(Config.class);
        if (cfg == null) {
            return false;
        }
        return cfg.required();
    }

    private boolean isOption(Method m) {
        return m.getReturnType() == Boolean.TYPE || m.getReturnType() == Boolean.class;
    }

    private String getTypeDescriptor(Type type) {
        ParameterizedType pt;
        Type c;
        if (type instanceof ParameterizedType && (c = (pt = (ParameterizedType)type).getRawType()) instanceof Class && Collection.class.isAssignableFrom((Class)c)) {
            return this.getTypeDescriptor(pt.getActualTypeArguments()[0]) + "*";
        }
        if (!(type instanceof Class)) {
            return "<>";
        }
        Class clazz = (Class)type;
        if (clazz == Boolean.class || clazz == Boolean.TYPE) {
            return "";
        }
        return "<" + CommandLine.lastPart(clazz.getName().toLowerCase()) + ">";
    }

    public Object getResult() {
        return this.result;
    }

    public String subCmd(Options opts, Object target) throws Exception {
        List<String> arguments = opts._arguments();
        if (arguments.isEmpty()) {
            Justif j = new Justif();
            Formatter f = j.formatter();
            this.help(f, target);
            return j.wrap();
        }
        String cmd = arguments.remove(0);
        return this.execute(target, cmd, arguments);
    }

    class Option {
        public char shortcut;
        public String name;
        public String paramType;
        public String description;
        public boolean required;

        Option() {
        }
    }
}

