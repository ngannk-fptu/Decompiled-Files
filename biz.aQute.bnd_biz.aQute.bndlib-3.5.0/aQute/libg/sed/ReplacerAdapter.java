/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.sed;

import aQute.lib.collections.ExtList;
import aQute.lib.collections.SortedList;
import aQute.lib.io.IO;
import aQute.libg.glob.Glob;
import aQute.libg.reporter.ReporterAdapter;
import aQute.libg.sed.Domain;
import aQute.libg.sed.Replacer;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacerAdapter
extends ReporterAdapter
implements Replacer {
    static final Random random = new Random();
    static Pattern WILDCARD = Pattern.compile("[*?|[\\\\]\\(\\)]");
    Domain domain;
    List<Object> targets = new ArrayList<Object>();
    boolean flattening;
    File base = new File(System.getProperty("user.dir"));
    Reporter reporter = this;
    static Pattern commands = Pattern.compile("(?<!\\\\);");
    static String _uniqHelp = "${uniq;<list> ...}";
    static String _filterHelp = "${%s;<list>;<regex>}";
    static String _sortHelp = "${sort;<list>...}";
    static String _nsortHelp = "${nsort;<list>...}";
    static String _joinHelp = "${join;<list>...}";
    static String _ifHelp = "${if;<condition>;<iftrue> [;<iffalse>] }";
    public static final String _fmodifiedHelp = "${fmodified;<list of filenames>...}, return latest modification date";
    static String _toclassnameHelp = "${classname;<list of class names>}, convert class paths to FQN class names ";
    static String _toclasspathHelp = "${toclasspath;<list>[;boolean]}, convert a list of class names to paths";
    public static final String _fileHelp = "${file;<base>;<paths>...}, create correct OS dependent path";

    public ReplacerAdapter(Domain domain) {
        this.domain = domain;
    }

    public ReplacerAdapter(final Map<String, String> domain) {
        this(new Domain(){

            @Override
            public Map<String, String> getMap() {
                return domain;
            }

            @Override
            public Domain getParent() {
                return null;
            }
        });
    }

    public ReplacerAdapter target(Object target) {
        assert (target != null);
        this.targets.add(target);
        return this;
    }

    public ReplacerAdapter target(File base) {
        this.base = base;
        return this;
    }

    public String process(String line, Domain source) {
        return this.process(line, new Link(source, null, line));
    }

    String process(String line, Link link) {
        StringBuilder sb = new StringBuilder();
        this.process(line, 0, '\u0000', '\u0000', sb, link);
        return sb.toString();
    }

    int process(CharSequence org, int index, char begin, char end, StringBuilder result, Link link) {
        StringBuilder line = new StringBuilder(org);
        int nesting = 1;
        StringBuilder variable = new StringBuilder();
        while (index < line.length()) {
            char c1;
            if ((c1 = line.charAt(index++)) == end) {
                if (--nesting == 0) {
                    result.append(this.replace(variable.toString(), link));
                    return index;
                }
            } else if (c1 == begin) {
                ++nesting;
            } else {
                if (c1 == '\\' && index < line.length() - 1 && line.charAt(index) == '$') {
                    ++index;
                    variable.append('$');
                    continue;
                }
                if (c1 == '$' && index < line.length() - 2) {
                    char c2 = line.charAt(index);
                    char terminator = ReplacerAdapter.getTerminator(c2);
                    if (terminator != '\u0000') {
                        index = this.process(line, index + 1, c2, terminator, variable, link);
                        continue;
                    }
                } else if (c1 == '.' && index < line.length() && line.charAt(index) == '/' && (index == 1 || Character.isWhitespace(line.charAt(index - 2)))) {
                    ++index;
                    variable.append(this.base.getAbsolutePath());
                    variable.append('/');
                    continue;
                }
            }
            variable.append(c1);
        }
        result.append((CharSequence)variable);
        return index;
    }

    /*
     * Enabled aggressive block sorting
     */
    public int findMacro(CharSequence line, int index) {
        if (index >= line.length() || line.charAt(index) != '$') {
            return -1;
        }
        int nesting = 1;
        int n = ++index;
        ++index;
        char begin = line.charAt(n);
        char end = ReplacerAdapter.getTerminator(begin);
        if (end == '\u0000') {
            return -1;
        }
        block4: while (index < line.length()) {
            char c1;
            if ((c1 = line.charAt(index++)) == end) {
                if (--nesting != 0) continue;
                return index;
            }
            if (c1 == begin) {
                ++nesting;
                continue;
            }
            if (c1 == '\\' && index < line.length() - 1) {
                ++index;
                continue;
            }
            if (c1 != '\'' && c1 != '\"') continue;
            while (true) {
                if (index >= line.length()) continue block4;
                char c2 = line.charAt(index++);
                switch (c2) {
                    case '\"': 
                    case '\'': {
                        if (c2 != c1) break;
                        continue block4;
                    }
                    case '\\': {
                        ++index;
                    }
                }
            }
        }
        return index;
    }

    public static char getTerminator(char c) {
        switch (c) {
            case '(': {
                return ')';
            }
            case '[': {
                return ']';
            }
            case '{': {
                return '}';
            }
            case '<': {
                return '>';
            }
            case '\u00ab': {
                return '\u00bb';
            }
            case '\u2039': {
                return '\u203a';
            }
        }
        return '\u0000';
    }

    public String getProcessed(String key) {
        return this.replace(key, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String replace(String key, Link link) {
        if (link != null && link.contains(key)) {
            return "${infinite:" + link + "}";
        }
        if (key != null) {
            if ((key = key.trim()).length() > 0) {
                String[] parts;
                Domain source;
                String value = null;
                if (key.indexOf(59) < 0) {
                    if (WILDCARD.matcher(key).find()) {
                        Glob ins = new Glob(key);
                        StringBuilder sb = new StringBuilder();
                        String del = "";
                        for (String k : this.getAllKeys()) {
                            String v;
                            if (!ins.matcher(k).find() || (v = this.replace(k, new Link(source, link, key))) == null) continue;
                            sb.append(del);
                            del = ",";
                            sb.append(v);
                        }
                        return sb.toString();
                    }
                    for (source = this.domain; value == null && source != null; source = source.getParent()) {
                        value = source.getMap().get(key);
                        if (value == null) continue;
                        return this.process(value, new Link(source, link, key));
                    }
                }
                if ((value = this.doCommands(key, link)) != null) {
                    return this.process(value, new Link(source, link, key));
                }
                if (key != null && key.trim().length() > 0 && (value = System.getProperty(key)) != null) {
                    return value;
                }
                if (key.indexOf(59) >= 0 && (parts = key.split(";")).length > 1) {
                    String template;
                    if (parts.length >= 16) {
                        this.error("too many arguments for template: %s, max is 16", key);
                    }
                    if ((template = this.domain.getMap().get(parts[0])) != null) {
                        final Domain old = this.domain;
                        try {
                            final HashMap<String, String> args = new HashMap<String, String>();
                            for (int i = 0; i < 16; ++i) {
                                args.put("" + i, i < parts.length ? parts[i] : "null");
                            }
                            this.domain = new Domain(){

                                @Override
                                public Map<String, String> getMap() {
                                    return args;
                                }

                                @Override
                                public Domain getParent() {
                                    return old;
                                }
                            };
                            ExtList<String> args0 = new ExtList<String>(parts);
                            args0.remove(0);
                            args.put("#", args0.join());
                            value = this.process(template, new Link(this.domain, link, key));
                            if (value != null) {
                                String string = value;
                                return string;
                            }
                        }
                        finally {
                            this.domain = old;
                        }
                    }
                }
                if (!this.flattening && !key.equals("@")) {
                    this.reporter.warning("No translation found for macro: %s", key);
                }
            } else {
                this.reporter.warning("Found empty macro key", new Object[0]);
            }
        } else {
            this.reporter.warning("Found null macro key", new Object[0]);
        }
        return "${" + key + "}";
    }

    private List<String> getAllKeys() {
        ArrayList<String> l = new ArrayList<String>();
        Domain source = this.domain;
        do {
            l.addAll(source.getMap().keySet());
        } while ((source = source.getParent()) != null);
        Collections.sort(l);
        return l;
    }

    private String doCommands(String key, Link source) {
        String[] args = commands.split(key);
        if (args == null || args.length == 0) {
            return null;
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i].indexOf(92) < 0) continue;
            args[i] = args[i].replaceAll("\\\\;", ";");
        }
        if (args[0].startsWith("^")) {
            String varname = args[0].substring(1).trim();
            Domain parent = source.start.getParent();
            if (parent != null) {
                return parent.getMap().get(varname);
            }
            return null;
        }
        for (Domain rover = this.domain; rover != null; rover = rover.getParent()) {
            String result = this.doCommand(rover, args[0], args);
            if (result == null) continue;
            return result;
        }
        for (Object target : this.targets) {
            String result = this.doCommand(target, args[0], args);
            if (result == null) continue;
            return result;
        }
        return this.doCommand(this, args[0], args);
    }

    private String doCommand(Object target, String method, String[] args) {
        if (target != null) {
            String cname = "_" + method.replaceAll("-", "_");
            try {
                Method m = target.getClass().getMethod(cname, String[].class);
                return "" + m.invoke(target, new Object[]{args});
            }
            catch (NoSuchMethodException e) {
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    this.reporter.error("%s, for cmd: %s, arguments; %s", e.getCause(), method, Arrays.toString(args));
                } else {
                    this.reporter.warning("Exception in replace: %s", e.getCause());
                    e.getCause().printStackTrace();
                }
            }
            catch (Exception e) {
                this.reporter.warning("Exception in replace: %s method=%s", e, method);
                e.printStackTrace();
            }
        }
        return null;
    }

    public String _uniq(String[] args) {
        ReplacerAdapter.verifyCommand(args, _uniqHelp, null, 1, Integer.MAX_VALUE);
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 1; i < args.length; ++i) {
            set.addAll(ExtList.from(args[i].trim()));
        }
        ExtList<String> rsult = new ExtList<String>(new String[0]);
        rsult.addAll(set);
        return rsult.join(",");
    }

    public String _pathseparator(String[] args) {
        return File.pathSeparator;
    }

    public String _separator(String[] args) {
        return File.separator;
    }

    public String _filter(String[] args) {
        return this.filter(args, false);
    }

    public String _filterout(String[] args) {
        return this.filter(args, true);
    }

    String filter(String[] args, boolean include) {
        ReplacerAdapter.verifyCommand(args, String.format(_filterHelp, args[0]), null, 3, 3);
        ExtList<String> list = ExtList.from(args[1]);
        Pattern pattern = Pattern.compile(args[2]);
        Iterator i = list.iterator();
        while (i.hasNext()) {
            if (pattern.matcher((CharSequence)i.next()).matches() != include) continue;
            i.remove();
        }
        return list.join();
    }

    public String _sort(String[] args) {
        ReplacerAdapter.verifyCommand(args, _sortHelp, null, 2, Integer.MAX_VALUE);
        ExtList<String> result = new ExtList<String>(new String[0]);
        for (int i = 1; i < args.length; ++i) {
            result.addAll(ExtList.from(args[i]));
        }
        Collections.sort(result);
        return result.join();
    }

    public String _nsort(String[] args) {
        ReplacerAdapter.verifyCommand(args, _nsortHelp, null, 2, Integer.MAX_VALUE);
        ExtList<String> result = new ExtList<String>(new String[0]);
        for (int i = 1; i < args.length; ++i) {
            result.addAll(ExtList.from(args[i]));
        }
        Collections.sort(result, new Comparator<String>(){

            @Override
            public int compare(String a, String b) {
                while (a.startsWith("0")) {
                    a = a.substring(1);
                }
                while (b.startsWith("0")) {
                    b = b.substring(1);
                }
                if (a.length() == b.length()) {
                    return a.compareTo(b);
                }
                if (a.length() > b.length()) {
                    return 1;
                }
                return -1;
            }
        });
        return result.join();
    }

    public String _join(String[] args) {
        ReplacerAdapter.verifyCommand(args, _joinHelp, null, 1, Integer.MAX_VALUE);
        ExtList<String> result = new ExtList<String>(new String[0]);
        for (int i = 1; i < args.length; ++i) {
            result.addAll(ExtList.from(args[i]));
        }
        return result.join();
    }

    public String _if(String[] args) {
        ReplacerAdapter.verifyCommand(args, _ifHelp, null, 3, 4);
        String condition = args[1].trim();
        if (!condition.equalsIgnoreCase("false") && condition.length() != 0) {
            return args[2];
        }
        if (args.length > 3) {
            return args[3];
        }
        return "";
    }

    public String _now(String[] args) {
        return new Date().toString();
    }

    public String _fmodified(String[] args) throws Exception {
        ReplacerAdapter.verifyCommand(args, _fmodifiedHelp, null, 2, Integer.MAX_VALUE);
        long time = 0L;
        ExtList<String> names = new ExtList<String>(new String[0]);
        for (int i = 1; i < args.length; ++i) {
            names.addAll(ExtList.from(args[i]));
        }
        for (String name : names) {
            File f = new File(name);
            if (!f.exists() || f.lastModified() <= time) continue;
            time = f.lastModified();
        }
        return "" + time;
    }

    public String _long2date(String[] args) {
        try {
            return new Date(Long.parseLong(args[1])).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "not a valid long";
        }
    }

    public String _literal(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("Need a value for the ${literal;<value>} macro");
        }
        return "${" + args[1] + "}";
    }

    public String _def(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("Need a value for the ${def;<value>} macro");
        }
        String value = this.domain.getMap().get(args[1]);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String _replace(String[] args) {
        if (args.length != 4) {
            this.reporter.warning("Invalid nr of arguments to replace %s", Arrays.asList(args));
            return null;
        }
        String[] list = args[1].split("\\s*,\\s*");
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < list.length; ++i) {
            String element = list[i].trim();
            if (element.equals("")) continue;
            sb.append(del);
            sb.append(element.replaceAll(args[2], args[3]));
            del = ", ";
        }
        return sb.toString();
    }

    public String _warning(String[] args) {
        for (int i = 1; i < args.length; ++i) {
            this.reporter.warning("%s", this.process(args[i]));
        }
        return "";
    }

    public String _error(String[] args) {
        for (int i = 1; i < args.length; ++i) {
            this.reporter.error("%s", this.process(args[i]));
        }
        return "";
    }

    public String _toclassname(String[] args) {
        ReplacerAdapter.verifyCommand(args, _toclassnameHelp, null, 2, 2);
        ExtList<String> paths = ExtList.from(args[1]);
        ExtList names = new ExtList(paths.size());
        for (String path : paths) {
            String name;
            if (path.endsWith(".class")) {
                name = path.substring(0, path.length() - 6).replace('/', '.');
                names.add(name);
                continue;
            }
            if (path.endsWith(".java")) {
                name = path.substring(0, path.length() - 5).replace('/', '.');
                names.add(name);
                continue;
            }
            this.reporter.warning("in toclassname, %s, is not a class path because it does not end in .class", args[1]);
        }
        return names.join(",");
    }

    public String _toclasspath(String[] args) {
        ReplacerAdapter.verifyCommand(args, _toclasspathHelp, null, 2, 3);
        boolean cl = true;
        if (args.length > 2) {
            cl = Boolean.valueOf(args[2]);
        }
        ExtList<String> names = ExtList.from(args[1]);
        ExtList paths = new ExtList(names.size());
        for (String name : names) {
            String path = name.replace('.', '/') + (cl ? ".class" : "");
            paths.add(path);
        }
        return paths.join(",");
    }

    public String _dir(String[] args) {
        if (args.length < 2) {
            this.reporter.warning("Need at least one file name for ${dir;...}", new Object[0]);
            return null;
        }
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            File f = IO.getFile(this.base, args[i]);
            if (!f.exists() || !f.getParentFile().exists()) continue;
            sb.append(del);
            sb.append(f.getParentFile().getAbsolutePath());
            del = ",";
        }
        return sb.toString();
    }

    public String _basename(String[] args) {
        if (args.length < 2) {
            this.reporter.warning("Need at least one file name for ${basename;...}", new Object[0]);
            return null;
        }
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            File f = IO.getFile(this.base, args[i]);
            if (!f.exists() || !f.getParentFile().exists()) continue;
            sb.append(del);
            sb.append(f.getName());
            del = ",";
        }
        return sb.toString();
    }

    public String _isfile(String[] args) {
        if (args.length < 2) {
            this.reporter.warning("Need at least one file name for ${isfile;...}", new Object[0]);
            return null;
        }
        boolean isfile = true;
        for (int i = 1; i < args.length; ++i) {
            File f = new File(args[i]).getAbsoluteFile();
            isfile &= f.isFile();
        }
        return isfile ? "true" : "false";
    }

    public String _isdir(String[] args) {
        if (args.length < 2) {
            this.reporter.warning("Need at least one file name for ${isdir;...}", new Object[0]);
            return null;
        }
        boolean isdir = true;
        for (int i = 1; i < args.length; ++i) {
            File f = new File(args[i]).getAbsoluteFile();
            isdir &= f.isDirectory();
        }
        return isdir ? "true" : "false";
    }

    public String _tstamp(String[] args) {
        String format = "yyyyMMddHHmm";
        long now = System.currentTimeMillis();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        if (args.length > 1) {
            format = args[1];
        }
        if (args.length > 2) {
            tz = TimeZone.getTimeZone(args[2]);
        }
        if (args.length > 3) {
            now = Long.parseLong(args[3]);
        }
        if (args.length > 4) {
            this.reporter.warning("Too many arguments for tstamp: %s", Arrays.toString(args));
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(tz);
        return sdf.format(new Date(now));
    }

    public String _lsr(String[] args) {
        return this.ls(args, true);
    }

    public String _lsa(String[] args) {
        return this.ls(args, false);
    }

    String ls(String[] args, boolean relative) {
        if (args.length < 2) {
            throw new IllegalArgumentException("the ${ls} macro must at least have a directory as parameter");
        }
        File dir = IO.getFile(this.base, args[1]);
        if (!dir.isAbsolute()) {
            throw new IllegalArgumentException("the ${ls} macro directory parameter is not absolute: " + dir);
        }
        if (!dir.exists()) {
            throw new IllegalArgumentException("the ${ls} macro directory parameter does not exist: " + dir);
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("the ${ls} macro directory parameter points to a file instead of a directory: " + dir);
        }
        ArrayList files = new ArrayList(new SortedList(dir.listFiles()));
        for (int i = 2; i < args.length; ++i) {
            Glob filters = new Glob(args[i]);
            filters.select(files);
        }
        ExtList<String> result = new ExtList<String>(new String[0]);
        for (File file : files) {
            result.add(relative ? file.getName() : file.getAbsolutePath().replace(File.separatorChar, '/'));
        }
        return result.join(",");
    }

    public String _currenttime(String[] args) {
        return Long.toString(System.currentTimeMillis());
    }

    public String system_internal(boolean allowFail, String[] args) throws Exception {
        ReplacerAdapter.verifyCommand(args, "${" + (allowFail ? "system-allow-fail" : "system") + ";<command>[;<in>]}, execute a system command", null, 2, 3);
        String command = args[1];
        String input = null;
        if (args.length > 2) {
            input = args[2];
        }
        Process process = Runtime.getRuntime().exec(command, null, this.base);
        if (input != null) {
            process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        }
        process.getOutputStream().close();
        String s = IO.collect(process.getInputStream(), StandardCharsets.UTF_8);
        s = s + IO.collect(process.getErrorStream(), StandardCharsets.UTF_8);
        int exitValue = process.waitFor();
        if (exitValue != 0) {
            return exitValue + "";
        }
        if (exitValue != 0) {
            if (!allowFail) {
                this.reporter.error("System command %s failed with exit code %d", command, exitValue);
            } else {
                this.reporter.warning("System command %s failed with exit code %d (allowed)", command, exitValue);
            }
        }
        return s.trim();
    }

    public String _system(String[] args) throws Exception {
        return this.system_internal(false, args);
    }

    public String _system_allow_fail(String[] args) throws Exception {
        String result = "";
        try {
            result = this.system_internal(true, args);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return result;
    }

    public String _env(String[] args) {
        ReplacerAdapter.verifyCommand(args, "${env;<name>}, get the environmet variable", null, 2, 2);
        try {
            return System.getenv(args[1]);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public String _cat(String[] args) throws IOException {
        ReplacerAdapter.verifyCommand(args, "${cat;<in>}, get the content of a file", null, 2, 2);
        File f = IO.getFile(this.base, args[1]);
        if (f.isFile()) {
            return IO.collect(f);
        }
        if (f.isDirectory()) {
            return Arrays.toString(f.list());
        }
        try {
            URL url = new URL(args[1]);
            return IO.collect(url, StandardCharsets.UTF_8);
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    public static void verifyCommand(String[] args, String help, Pattern[] patterns, int low, int high) {
        String message = "";
        if (args.length > high) {
            message = "too many arguments";
        } else if (args.length < low) {
            message = "too few arguments";
        } else {
            for (int i = 0; patterns != null && i < patterns.length && i < args.length; ++i) {
                Matcher m;
                if (patterns[i] == null || (m = patterns[i].matcher(args[i])).matches()) continue;
                message = message + String.format("Argument %s (%s) does not match %s%n", i, args[i], patterns[i].pattern());
            }
        }
        if (message.length() != 0) {
            StringBuilder sb = new StringBuilder();
            String del = "${";
            for (String arg : args) {
                sb.append(del);
                sb.append(arg);
                del = ";";
            }
            sb.append("}, is not understood. ");
            sb.append(message);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, String> getFlattenedProperties() {
        this.flattening = true;
        try {
            HashMap<String, String> flattened = new HashMap<String, String>();
            Map<String, String> source = this.domain.getMap();
            for (String key : source.keySet()) {
                if (key.startsWith("_")) continue;
                if (key.startsWith("-")) {
                    flattened.put(key, source.get(key));
                    continue;
                }
                flattened.put(key, this.process(source.get(key)));
            }
            HashMap<String, String> hashMap = flattened;
            return hashMap;
        }
        finally {
            this.flattening = false;
        }
    }

    public String _osfile(String[] args) {
        ReplacerAdapter.verifyCommand(args, _fileHelp, null, 3, 3);
        File base = new File(args[1]);
        File f = IO.getFile(base, args[2]);
        return f.getAbsolutePath();
    }

    public String _path(String[] args) {
        ExtList<String> list = new ExtList<String>(new String[0]);
        for (int i = 1; i < args.length; ++i) {
            list.addAll(ExtList.from(args[i]));
        }
        return list.join(File.pathSeparator);
    }

    public static Properties getParent(Properties p) {
        try {
            Field f = Properties.class.getDeclaredField("defaults");
            f.setAccessible(true);
            return (Properties)f.get(p);
        }
        catch (Exception e) {
            Object[] fields = Properties.class.getFields();
            System.err.println(Arrays.toString(fields));
            return null;
        }
    }

    @Override
    public String process(String line) {
        return this.process(line, this.domain);
    }

    public String _random(String[] args) {
        int numchars = 8;
        if (args.length > 1) {
            try {
                numchars = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid character count parameter in ${random} macro.");
            }
        }
        char[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] alphanums = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] array = new char[numchars];
        for (int i = 0; i < numchars; ++i) {
            char c = i == 0 ? letters[random.nextInt(letters.length)] : alphanums[random.nextInt(alphanums.length)];
            array[i] = c;
        }
        return new String(array);
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public int _processors(String[] args) {
        float multiplier = 1.0f;
        if (args.length > 1) {
            multiplier = Float.parseFloat(args[1]);
        }
        return (int)((float)Runtime.getRuntime().availableProcessors() * multiplier);
    }

    public long _maxMemory(String[] args) {
        return Runtime.getRuntime().maxMemory();
    }

    public long _freeMemory(String[] args) {
        return Runtime.getRuntime().freeMemory();
    }

    public long _nanoTime(String[] args) {
        return System.nanoTime();
    }

    public void addTarget(Object target) {
        this.targets.remove(target);
        this.targets.add(target);
    }

    public void removeTarget(Object target) {
        this.targets.remove(target);
    }

    public String _unescape(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            sb.append(args[i]);
        }
        block8: for (int j = 0; j < sb.length() - 1; ++j) {
            if (sb.charAt(j) != '\\') continue;
            switch (sb.charAt(j + 1)) {
                case 'n': {
                    sb.replace(j, j + 2, "\n");
                    continue block8;
                }
                case 'r': {
                    sb.replace(j, j + 2, "\r");
                    continue block8;
                }
                case 'b': {
                    sb.replace(j, j + 2, "\b");
                    continue block8;
                }
                case 'f': {
                    sb.replace(j, j + 2, "\f");
                    continue block8;
                }
                case 't': {
                    sb.replace(j, j + 2, "\t");
                    continue block8;
                }
            }
        }
        return sb.toString();
    }

    public String _bytes(String[] args) {
        Formatter sb = new Formatter();
        for (int i = 0; i < args.length; ++i) {
            long l = Long.parseLong(args[1]);
            this.bytes(sb, l, 0, new String[]{"b", "Kb", "Mb", "Gb", "Tb", "Pb", "Eb", "Zb", "Yb", "Bb", "Geopbyte"});
        }
        return sb.toString();
    }

    private void bytes(Formatter sb, double l, int i, String[] strings) {
        if (l > 1024.0 && i < strings.length - 1) {
            this.bytes(sb, l / 1024.0, i + 1, strings);
            return;
        }
        l = Math.round(l * 10.0) / 10L;
        sb.format("%s %s", l, strings[i]);
    }

    static class Link {
        Link previous;
        String key;
        Domain start;

        public Link(Domain start, Link previous, String key) {
            this.previous = previous;
            this.key = key;
            this.start = start;
        }

        public boolean contains(String key) {
            if (this.key.equals(key)) {
                return true;
            }
            if (this.previous == null) {
                return false;
            }
            return this.previous.contains(key);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            this.append(sb);
            sb.append("]");
            return sb.toString();
        }

        private void append(StringBuilder sb) {
            if (this.previous != null) {
                this.previous.append(sb);
                sb.append(",");
            }
            sb.append(this.key);
        }
    }
}

