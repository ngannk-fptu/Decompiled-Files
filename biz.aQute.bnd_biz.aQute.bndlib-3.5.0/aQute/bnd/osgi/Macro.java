/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.version.MavenVersion;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.base64.Base64;
import aQute.lib.collections.ExtList;
import aQute.lib.collections.SortedList;
import aQute.lib.filter.ExtendedFilter;
import aQute.lib.filter.Get;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.glob.Glob;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Macro {
    static final String NULLVALUE = "c29e43048791e250dfd5723e7b8aa048df802c9262cfa8fbc4475b2e392a8ad2";
    static final String LITERALVALUE = "017a3ddbfc0fcd27bcdb2590cdb713a379ae59ef";
    static final Pattern NUMERIC_P = Pattern.compile("[-+]?(\\d*\\.?\\d+|\\d+\\.)(e[-+]?[0-9]+)?");
    static final Pattern PRINTF_P = Pattern.compile("%(?:(\\d+)\\$)?(-|\\+|0|\\(|,|\\^|#| )*(\\d*)?(?:\\.(\\d+))?(a|A|b|B|h|H|d|f|c|s|x|X|u|o|z|Z|e|E|g|G|p|n|b|B|%)");
    Processor domain;
    Object[] targets;
    boolean flattening;
    String profile;
    private boolean nosystem;
    ScriptEngine engine = null;
    ScriptContext context = null;
    Bindings bindings = null;
    StringWriter stdout = new StringWriter();
    StringWriter stderr = new StringWriter();
    static Pattern commands = Pattern.compile("(?<!\\\\);");
    static String _uniqHelp = "${uniq;<list> ...}";
    static String _filterHelp = "${%s;<list>;<regex>}";
    static String _sortHelp = "${sort;<list>...}";
    static String _nsortHelp = "${nsort;<list>...}";
    static String _joinHelp = "${join;<list>...}";
    static String _sjoinHelp = "${sjoin;<separator>;<list>...}";
    static String _ifHelp = "${if;<condition>;<iftrue> [;<iffalse>] } condition is either a filter expression or truthy";
    public static final String _nowHelp = "${now;pattern|'long'}, returns current time";
    public static final String _fmodifiedHelp = "${fmodified;<list of filenames>...}, return latest modification date";
    static String _toclassnameHelp = "${classname;<list of class names>}, convert class paths to FQN class names ";
    static String _toclasspathHelp = "${toclasspath;<list>[;boolean]}, convert a list of class names to paths";
    static final String MASK_STRING = "[\\-+=~0123456789]{0,3}[=~]?";
    static final Pattern MASK = Pattern.compile("[\\-+=~0123456789]{0,3}[=~]?");
    static final String _versionHelp = "${version;<mask>;<version>}, modify a version\n<mask> ::= [ M [ M [ M [ MQ ]]]\nM ::= '+' | '-' | MQ\nMQ ::= '~' | '='";
    static final Pattern[] _versionPattern = new Pattern[]{null, null, MASK, Verifier.VERSION};
    static Pattern RANGE_MASK = Pattern.compile("(\\[|\\()([\\-+=~0123456789]{0,3}[=~]?),([\\-+=~0123456789]{0,3}[=~]?)(\\]|\\))");
    static String _rangeHelp = "${range;<mask>[;<version>]}, range for version, if version not specified lookup ${@}\n<mask> ::= [ M [ M [ M [ MQ ]]]\nM ::= '+' | '-' | MQ\nMQ ::= '~' | '='";
    static Pattern[] _rangePattern = new Pattern[]{null, RANGE_MASK};
    public static final String _fileHelp = "${file;<base>;<paths>...}, create correct OS dependent path";
    public static final String _sizeHelp = "${size;<collection>;...}, count the number of elements (of all collections combined)";
    static String _startswith = "${startswith;<string>;<prefix>}";
    static String _endswith = "${endswith;<string>;<suffix>}";
    static String _extension = "${extension;<string>}";
    static String _stem = "${stem;<string>}";
    static String _substring = "${substring;<string>;<start>[;<end>]}";
    static String _rand = "${rand;[<min>[;<end>]]}";
    static Random random = new Random();
    static String _length = "${length;<string>}";
    static String _get = "${get;<index>;<list>}";
    static String _sublist = "${sublist;<start>;<end>[;<list>...]}";
    static String _first = "${first;<list>[;<list>...]}";
    static String _last = "${last;<list>[;<list>...]}";
    static String _max = "${max;<list>[;<list>...]}";
    static String _min = "${min;<list>[;<list>...]}";
    static String _nmax = "${nmax;<list>[;<list>...]}";
    static String _nmin = "${nmin;<list>[;<list>...]}";
    static String _sum = "${sum;<list>[;<list>...]}";
    static String _average = "${average;<list>[;<list>...]}";
    static String _reverse = "${reverse;<list>[;<list>...]}";
    static String _indexof = "${indexof;<value>;<list>[;<list>...]}";
    static String _lastindexof = "${lastindexof;<value>;<list>[;<list>...]}";
    static String _find = "${find;<target>;<searched>}";
    static String _findlast = "${findlast;<find>;<target>}";
    static String _split = "${split;<regex>[;<target>...]}";
    static String _js = "${js [;<js expr>...]}";
    static String _toupper = "${toupper;<target>}";
    static String _tolower = "${tolower;<target>}";
    static String _compare = "${compare;<astring>;<bstring>}";
    static String _ncompare = "${ncompare;<anumber>;<bnumber>}";
    static String _matches = "${matches;<target>;<regex>}";
    static String _subst = "${subst;<target>;<regex>[;<replace>[;count]]}";
    static String _trim = "${trim;<target>}";
    static String _format = "${format;<format>[;args...]}";
    static String _isempty = "${isempty;[<target>...]}";
    static String _isnumber = "${isnumber[;<target>...]}";
    static String _is = "${is;<a>;<b>}";
    static String _map = "${map;<macro>[;<list>...]}";
    static String _foreach = "${foreach;<macro>[;<list>...]}";
    static String _apply = "${apply;<macro>[;<list>...]}";
    static String _globHelp = "${glob;<globexp>} (turn it into a regular expression)";

    public Macro(Processor domain, Object ... targets) {
        this.domain = domain;
        this.targets = targets;
        if (targets != null) {
            for (Object o : targets) {
                assert (o != null);
            }
        }
    }

    public String process(String line, Processor source) {
        return this.process(line, new Link(source, null, line));
    }

    String process(String line, Link link) {
        StringBuilder sb = new StringBuilder();
        this.process(line, 0, '\u0000', '\u0000', sb, link);
        return sb.toString();
    }

    int process(CharSequence org, int index, char begin, char end, StringBuilder result, Link link) {
        if (org == null) {
            return index;
        }
        StringBuilder line = new StringBuilder(org);
        int nesting = 1;
        StringBuilder variable = new StringBuilder();
        while (index < line.length()) {
            char c1;
            if ((c1 = line.charAt(index++)) == end) {
                if (--nesting == 0) {
                    result.append(this.replace(variable.toString(), link, begin, end));
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
                    char terminator = Macro.getTerminator(c2);
                    if (terminator != '\u0000') {
                        index = this.process(line, index + 1, c2, terminator, variable, link);
                        continue;
                    }
                } else if (c1 == '.' && index < line.length() && line.charAt(index) == '/' && (index == 1 || Character.isWhitespace(line.charAt(index - 2)))) {
                    ++index;
                    variable.append(this.domain.getBase().getAbsolutePath());
                    variable.append('/');
                    continue;
                }
            }
            variable.append(c1);
        }
        result.append((CharSequence)variable);
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

    protected String getMacro(String key, Link link) {
        return this.getMacro(key, link, '{', '}');
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getMacro(String key, Link link, char begin, char end) {
        if (link != null && link.contains(key)) {
            return "${infinite:" + link.toString() + "}";
        }
        if (key != null) {
            if ((key = key.trim()).length() > 0) {
                String[] parts;
                Processor source;
                Instruction ins;
                String value = null;
                if (key.indexOf(59) < 0 && !(ins = new Instruction(key)).isLiteral()) {
                    SortedList<String> sortedList = SortedList.fromIterator(this.domain.iterator());
                    StringBuilder sb = new StringBuilder();
                    String del = "";
                    for (String k : sortedList) {
                        String v;
                        if (!ins.matches(k) || (v = this.replace(k, new Link(source, link, key), begin, end)) == null) continue;
                        sb.append(del);
                        del = ",";
                        sb.append(v);
                    }
                    return sb.toString();
                }
                for (source = this.domain; value == null && source != null; source = source.getParent()) {
                    value = source.getProperties().getProperty(key);
                }
                if (value != null) {
                    return this.process(value, new Link(source, link, key));
                }
                value = this.doCommands(key, link);
                if (value != null) {
                    if (value == NULLVALUE) {
                        return null;
                    }
                    if (value == LITERALVALUE) {
                        return LITERALVALUE;
                    }
                    return this.process(value, new Link(source, link, key));
                }
                if (key != null && key.trim().length() > 0 && (value = System.getProperty(key)) != null) {
                    return value;
                }
                if (key != null && key.indexOf(59) >= 0 && (parts = key.split(";")).length > 1) {
                    String template;
                    if (parts.length >= 16) {
                        this.domain.error("too many arguments for template: %s, max is 16", key);
                    }
                    if ((template = this.domain.getProperties().getProperty(parts[0])) != null) {
                        this.domain = new Processor(this.domain);
                        for (int i = 0; i < 16; ++i) {
                            this.domain.setProperty("" + i, i < parts.length ? parts[i] : "null");
                        }
                        ExtList<String> args = new ExtList<String>(parts);
                        args.remove(0);
                        this.domain.setProperty("#", args.join());
                        try {
                            value = this.process(template, new Link(this.domain, link, key));
                            if (value != null) {
                                String string = value;
                                return string;
                            }
                        }
                        finally {
                            this.domain = this.domain.getParent();
                        }
                    }
                }
            } else {
                this.domain.warning("Found empty macro key", new Object[0]);
            }
        } else {
            this.domain.warning("Found null macro key", new Object[0]);
        }
        if (key != null && !key.startsWith("[") && !key.equals("-profile")) {
            String replace;
            if (this.profile == null) {
                this.profile = this.domain.get("-profile");
            }
            if (this.profile != null && (replace = this.getMacro("[" + this.profile + "]" + key, link, begin, end)) != null) {
                return replace;
            }
        }
        return null;
    }

    public String replace(String key, Link link) {
        return this.replace(key, link, '{', '}');
    }

    private String replace(String key, Link link, char begin, char end) {
        String value = this.getMacro(key, link, begin, end);
        if (value != LITERALVALUE) {
            if (value != null) {
                return value;
            }
            if (!this.flattening && !key.startsWith("@")) {
                this.domain.warning("No translation found for macro: %s", key);
            }
        }
        return "$" + begin + key + end;
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
            Processor parent = source.start.getParent();
            if (parent != null) {
                return parent.getProperty(varname);
            }
            return null;
        }
        for (Processor rover = this.domain; rover != null; rover = rover.getParent()) {
            String result = this.doCommand(rover, args[0], args);
            if (result == null) continue;
            return result;
        }
        for (int i = 0; this.targets != null && i < this.targets.length; ++i) {
            String result = this.doCommand(this.targets[i], args[0], args);
            if (result == null) continue;
            return result;
        }
        return this.doCommand(this, args[0], args);
    }

    private String doCommand(Object target, String method, String[] args) {
        if (target != null) {
            if (method.startsWith("-")) {
                return null;
            }
            String part = method.replaceAll("-", "_");
            for (int i = 0; i < part.length(); ++i) {
                if (Character.isJavaIdentifierPart(part.charAt(i))) continue;
                return null;
            }
            String cname = "_" + part;
            try {
                Method m = target.getClass().getMethod(cname, String[].class);
                Object result = m.invoke(target, new Object[]{args});
                return result == null ? NULLVALUE : result.toString();
            }
            catch (NoSuchMethodException e) {
                return null;
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    this.domain.error("%s, for cmd: %s, arguments; %s", e.getCause().getMessage(), method, Arrays.toString(args));
                } else {
                    this.domain.warning("Exception in replace: %s", e.getCause());
                }
                return NULLVALUE;
            }
            catch (Exception e) {
                this.domain.warning("Exception in replace: %s method=%s", e, method);
                return NULLVALUE;
            }
        }
        return null;
    }

    public String _uniq(String[] args) {
        Macro.verifyCommand(args, _uniqHelp, null, 1, Integer.MAX_VALUE);
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (int i = 1; i < args.length; ++i) {
            Processor.split(args[i], set);
        }
        return Processor.join(set, ",");
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

    public String _select(String[] args) {
        return this.filter(args, false);
    }

    public String _filterout(String[] args) {
        return this.filter(args, true);
    }

    public String _reject(String[] args) {
        return this.filter(args, true);
    }

    String filter(String[] args, boolean include) {
        Macro.verifyCommand(args, String.format(_filterHelp, args[0]), null, 3, 3);
        ArrayList<String> list = this.toCollection(args[1]);
        Pattern pattern = Pattern.compile(args[2]);
        Iterator i = list.iterator();
        while (i.hasNext()) {
            if (pattern.matcher((CharSequence)i.next()).matches() != include) continue;
            i.remove();
        }
        return Processor.join(list);
    }

    ArrayList<String> toCollection(String arg) {
        return new ArrayList<String>(Processor.split(arg));
    }

    public String _sort(String[] args) {
        Macro.verifyCommand(args, _sortHelp, null, 2, Integer.MAX_VALUE);
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            Processor.split(args[i], result);
        }
        Collections.sort(result);
        return Processor.join(result);
    }

    public String _nsort(String[] args) {
        Macro.verifyCommand(args, _nsortHelp, null, 2, Integer.MAX_VALUE);
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
        Macro.verifyCommand(args, _joinHelp, null, 1, Integer.MAX_VALUE);
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            Processor.split(args[i], result);
        }
        return Processor.join(result);
    }

    public String _sjoin(String[] args) throws Exception {
        Macro.verifyCommand(args, _sjoinHelp, null, 2, Integer.MAX_VALUE);
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 2; i < args.length; ++i) {
            Processor.split(args[i], result);
        }
        return Processor.join(args[1], result);
    }

    public String _if(String[] args) throws Exception {
        Macro.verifyCommand(args, _ifHelp, null, 2, 4);
        String condition = args[1];
        if (this.isTruthy(condition)) {
            return args.length > 2 ? args[2] : "true";
        }
        if (args.length > 3) {
            return args[3];
        }
        return "";
    }

    public boolean isTruthy(String condition) throws Exception {
        if (condition == null) {
            return false;
        }
        if ((condition = condition.trim()).startsWith("(") && condition.endsWith(")")) {
            return this.doCondition(condition);
        }
        return !condition.equalsIgnoreCase("false") && !condition.equals("0") && !condition.equals("0.0") && condition.length() != 0;
    }

    public Object _now(String[] args) {
        Macro.verifyCommand(args, _nowHelp, null, 1, 2);
        Date now = new Date();
        if (args.length == 2) {
            if ("long".equals(args[1])) {
                return now.getTime();
            }
            SimpleDateFormat df = new SimpleDateFormat(args[1], Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format(now);
        }
        return new Date();
    }

    public String _fmodified(String[] args) throws Exception {
        Macro.verifyCommand(args, _fmodifiedHelp, null, 2, Integer.MAX_VALUE);
        long time = 0L;
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            Processor.split(args[i], names);
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
        Macro.verifyCommand(args, "${def;<name>[;<value>]}, get the property or a default value if unset", null, 2, 3);
        return this.domain.getProperty(args[1], args.length == 3 ? args[2] : "");
    }

    public String _replace(String[] args) {
        if (args.length < 4 || args.length > 5) {
            this.domain.warning("Invalid nr of arguments to replace %s", Arrays.asList(args));
            return null;
        }
        String middle = ", ";
        if (args.length > 4) {
            middle = args[4];
        }
        String[] list = args[1].split("\\s*,\\s*");
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < list.length; ++i) {
            String element = list[i].trim();
            if (element.equals("")) continue;
            sb.append(del);
            sb.append(element.replaceAll(args[2], args[3]));
            del = middle;
        }
        return sb.toString();
    }

    public String _warning(String[] args) throws Exception {
        for (int i = 1; i < args.length; ++i) {
            Reporter.SetLocation warning = this.domain.warning("%s", this.process(args[i]));
            Processor.FileLine header = this.domain.getHeader(Pattern.compile(".*"), Pattern.compile("\\$\\{warning;"));
            if (header == null) continue;
            header.set(warning);
        }
        return "";
    }

    public String _error(String[] args) throws Exception {
        for (int i = 1; i < args.length; ++i) {
            Reporter.SetLocation error = this.domain.error("%s", this.process(args[i]));
            Processor.FileLine header = this.domain.getHeader(Pattern.compile(".*"), Pattern.compile("\\$\\{error;"));
            if (header == null) continue;
            header.set(error);
        }
        return "";
    }

    public String _toclassname(String[] args) {
        Macro.verifyCommand(args, _toclassnameHelp, null, 2, 2);
        Collection<String> paths = Processor.split(args[1]);
        ArrayList<String> names = new ArrayList<String>(paths.size());
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
            this.domain.warning("in toclassname, %s is not a class path because it does not end in .class", args[1]);
        }
        return Processor.join(names, ",");
    }

    public String _toclasspath(String[] args) {
        Macro.verifyCommand(args, _toclasspathHelp, null, 2, 3);
        boolean cl = true;
        if (args.length > 2) {
            cl = Boolean.valueOf(args[2]);
        }
        Collection<String> names = Processor.split(args[1]);
        ArrayList<String> paths = new ArrayList<String>(names.size());
        for (String name : names) {
            String path = name.replace('.', '/') + (cl ? ".class" : "");
            paths.add(path);
        }
        return Processor.join(paths, ",");
    }

    public String _dir(String[] args) {
        if (args.length < 2) {
            this.domain.warning("Need at least one file name for ${dir;...}", new Object[0]);
            return null;
        }
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            File f = this.domain.getFile(args[i]);
            if (!f.exists() || !f.getParentFile().exists()) continue;
            sb.append(del);
            sb.append(f.getParentFile().getAbsolutePath());
            del = ",";
        }
        return sb.toString();
    }

    public String _basename(String[] args) {
        if (args.length < 2) {
            this.domain.warning("Need at least one file name for ${basename;...}", new Object[0]);
            return null;
        }
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            File f = this.domain.getFile(args[i]);
            if (!f.exists() || !f.getParentFile().exists()) continue;
            sb.append(del);
            sb.append(f.getName());
            del = ",";
        }
        return sb.toString();
    }

    public String _isfile(String[] args) {
        if (args.length < 2) {
            this.domain.warning("Need at least one file name for ${isfile;...}", new Object[0]);
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
        boolean isdir = true;
        if (args.length < 2) {
            isdir = false;
        }
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
            this.domain.warning("Too many arguments for tstamp: %s", Arrays.toString(args));
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        sdf.setTimeZone(tz);
        String tstamp = this.domain.getProperty("_@tstamp");
        if (tstamp != null) {
            try {
                now = Long.parseLong(tstamp);
            }
            catch (NumberFormatException e) {
                // empty catch block
            }
        }
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
        File dir = this.domain.getFile(args[1]);
        if (!dir.isAbsolute()) {
            throw new IllegalArgumentException("the ${ls} macro directory parameter is not absolute: " + dir);
        }
        if (!dir.exists()) {
            throw new IllegalArgumentException("the ${ls} macro directory parameter does not exist: " + dir);
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("the ${ls} macro directory parameter points to a file instead of a directory: " + dir);
        }
        Collection files = new ArrayList(new SortedList(dir.listFiles()));
        for (int i = 2; i < args.length; ++i) {
            Instructions filters = new Instructions(args[i]);
            files = filters.select(files, true);
        }
        ArrayList<String> result = new ArrayList<String>();
        for (File file : files) {
            result.add(relative ? file.getName() : file.getAbsolutePath().replace(File.separatorChar, '/'));
        }
        return Processor.join(result, ",");
    }

    public String _currenttime(String[] args) {
        return Long.toString(System.currentTimeMillis());
    }

    public String _versionmask(String[] args) {
        return this._version(args);
    }

    public String _version(String[] args) {
        Macro.verifyCommand(args, _versionHelp, null, 2, 3);
        String mask = args[1];
        Version version = null;
        if (args.length >= 3) {
            if (this.isLocalTarget(args[2])) {
                return LITERALVALUE;
            }
            version = Version.parseVersion(args[2]);
        }
        return this.version(version, mask);
    }

    String version(Version version, String mask) {
        if (version == null) {
            String v = this.domain.getProperty("@");
            if (v == null) {
                return LITERALVALUE;
            }
            version = new Version(v);
        }
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < mask.length(); ++i) {
            char c = mask.charAt(i);
            String result = null;
            if (c == '~') continue;
            if (i == 3) {
                result = version.getQualifier();
                MavenVersion mv = new MavenVersion(version);
                if (c == 'S') {
                    if (mv.isSnapshot()) {
                        return sb.append("-SNAPSHOT").toString();
                    }
                } else if (c == 's') {
                    if (mv.isSnapshot()) {
                        return sb.append("-SNAPSHOT").toString();
                    }
                    return sb.toString();
                }
            } else if (Character.isDigit(c)) {
                result = String.valueOf(c);
            } else {
                int x = version.get(i);
                switch (c) {
                    case '+': {
                        ++x;
                        break;
                    }
                    case '-': {
                        --x;
                        break;
                    }
                }
                result = Integer.toString(x);
            }
            if (result == null) continue;
            sb.append(del);
            del = ".";
            sb.append(result);
        }
        return sb.toString();
    }

    public String _range(String[] args) {
        Macro.verifyCommand(args, _rangeHelp, _rangePattern, 2, 3);
        Version version = null;
        if (args.length >= 3) {
            String string = args[2];
            if (this.isLocalTarget(string)) {
                return LITERALVALUE;
            }
            version = new Version(string);
        } else {
            String v = this.domain.getProperty("@");
            if (v == null) {
                return LITERALVALUE;
            }
            version = new Version(v);
        }
        String spec = args[1];
        Matcher m = RANGE_MASK.matcher(spec);
        m.matches();
        String floor = m.group(1);
        String floorMask = m.group(2);
        String ceilingMask = m.group(3);
        String ceiling = m.group(4);
        String left = this.version(version, floorMask);
        String right = this.version(version, ceilingMask);
        StringBuilder sb = new StringBuilder();
        sb.append(floor);
        sb.append(left);
        sb.append(",");
        sb.append(right);
        sb.append(ceiling);
        String s = sb.toString();
        VersionRange vr = new VersionRange(s);
        if (!vr.includes(vr.getHigh()) && !vr.includes(vr.getLow())) {
            this.domain.error("${range} macro created an invalid range %s from %s and mask %s", s, version, spec);
        }
        return sb.toString();
    }

    boolean isLocalTarget(String string) {
        return string.matches("\\$(\\{@\\}|\\[@\\]|\\(@\\)|<@>|\u00ab@\u00bb|\u2039@\u203a)");
    }

    public String system_internal(boolean allowFail, String[] args) throws Exception {
        if (this.nosystem) {
            throw new RuntimeException("Macros in this mode cannot excute system commands");
        }
        Macro.verifyCommand(args, "${" + (allowFail ? "system-allow-fail" : "system") + ";<command>[;<in>]}, execute a system command", null, 2, 3);
        String command = args[1];
        String input = null;
        if (args.length > 2) {
            input = args[2];
        }
        if (File.separatorChar == '\\') {
            command = "cmd /c \"" + command + "\"";
        }
        Process process = Runtime.getRuntime().exec(command, null, this.domain.getBase());
        if (input != null) {
            process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        }
        process.getOutputStream().close();
        String s = IO.collect(process.getInputStream(), StandardCharsets.UTF_8);
        int exitValue = process.waitFor();
        if (exitValue != 0) {
            if (!allowFail) {
                this.domain.error("System command %s failed with exit code %d", command, exitValue);
            } else {
                this.domain.warning("System command %s failed with exit code %d (allowed)", command, exitValue);
            }
            return null;
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
            return result == null ? "" : result;
        }
        catch (Throwable t) {
            return "";
        }
    }

    public String _env(String[] args) {
        Macro.verifyCommand(args, "${env;<name>[;alternative]}, get the environment variable", null, 2, 3);
        try {
            String ret = System.getenv(args[1]);
            if (ret != null) {
                return ret;
            }
            if (args.length > 2) {
                return args[2];
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return "";
    }

    public String _cat(String[] args) throws IOException {
        Macro.verifyCommand(args, "${cat;<in>}, get the content of a file", null, 2, 2);
        File f = this.domain.getFile(args[1]);
        if (f.isFile()) {
            return IO.collect(f).replaceAll("\\\\", "\\\\\\\\");
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

    public String _base64(String ... args) throws IOException {
        Macro.verifyCommand(args, "${base64;<file>[;fileSizeLimit]}, get the Base64 encoding of a file", null, 2, 3);
        File file = this.domain.getFile(args[1]);
        long maxLength = 100000L;
        if (args.length > 2) {
            maxLength = Long.parseLong(args[2]);
        }
        if (file.length() > maxLength) {
            throw new IllegalArgumentException("Maximum file size (" + maxLength + ") for base64 macro exceeded for file " + file);
        }
        return Base64.encodeBase64(file);
    }

    public String _digest(String ... args) throws NoSuchAlgorithmException, IOException {
        Macro.verifyCommand(args, "${digest;<algo>;<in>}, get a digest (e.g. MD5, SHA-256) of a file", null, 3, 3);
        MessageDigest digester = MessageDigest.getInstance(args[1]);
        File f = this.domain.getFile(args[2]);
        IO.copy(f, digester);
        byte[] digest = digester.digest();
        return Hex.toHexString(digest);
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

    public Properties getFlattenedProperties() {
        return this.getFlattenedProperties(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Properties getFlattenedProperties(boolean ignoreInstructions) {
        this.flattening = true;
        try {
            UTF8Properties flattened = new UTF8Properties();
            Properties source = this.domain.getProperties();
            Enumeration<?> e = source.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                if (key.startsWith("_")) continue;
                String value = source.getProperty(key);
                if (value == null) {
                    Object raw = source.get(key);
                    this.domain.warning("Key '%s' has a non-String value: %s:%s", key, raw == null ? "" : raw.getClass().getName(), raw);
                    continue;
                }
                if (ignoreInstructions && key.startsWith("-")) {
                    flattened.put(key, value);
                    continue;
                }
                flattened.put(key, this.process(value));
            }
            UTF8Properties uTF8Properties = flattened;
            return uTF8Properties;
        }
        finally {
            this.flattening = false;
        }
    }

    public String _osfile(String[] args) {
        Macro.verifyCommand(args, _fileHelp, null, 3, 3);
        File base = new File(args[1]);
        File f = Processor.getFile(base, args[2]);
        return f.getAbsolutePath();
    }

    public String _path(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            list.addAll(Processor.split(args[i]));
        }
        return Processor.join(list, File.pathSeparator);
    }

    public int _size(String[] args) {
        Macro.verifyCommand(args, _sizeHelp, null, 1, 16);
        int size = 0;
        for (int i = 1; i < args.length; ++i) {
            ExtList<String> l = ExtList.from(args[i]);
            size += l.size();
        }
        return size;
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

    public String process(String line) {
        return this.process(line, this.domain);
    }

    public boolean isNosystem() {
        return this.nosystem;
    }

    public boolean setNosystem(boolean nosystem) {
        boolean tmp = this.nosystem;
        this.nosystem = nosystem;
        return tmp;
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

    public String _startswith(String[] args) throws Exception {
        Macro.verifyCommand(args, _startswith, null, 3, 3);
        if (args[1].startsWith(args[2])) {
            return args[1];
        }
        return "";
    }

    public String _endswith(String[] args) throws Exception {
        Macro.verifyCommand(args, _endswith, null, 3, 3);
        if (args[1].endsWith(args[2])) {
            return args[1];
        }
        return "";
    }

    public String _extension(String[] args) throws Exception {
        Macro.verifyCommand(args, _extension, null, 2, 2);
        String name = args[1];
        int n = name.indexOf(46);
        if (n < 0) {
            return "";
        }
        return name.substring(n + 1);
    }

    public String _stem(String[] args) throws Exception {
        Macro.verifyCommand(args, _stem, null, 2, 2);
        String name = args[1];
        int n = name.indexOf(46);
        if (n < 0) {
            return name;
        }
        return name.substring(0, n);
    }

    public String _substring(String[] args) throws Exception {
        Macro.verifyCommand(args, _substring, null, 3, 4);
        String string = args[1];
        int start = Integer.parseInt(args[2].equals("") ? "0" : args[2]);
        int end = string.length();
        if (args.length > 3 && (end = Integer.parseInt(args[3])) < 0) {
            end = string.length() + end;
        }
        if (start < 0) {
            start = string.length() + start;
        }
        if (start > end) {
            int t = start;
            start = end;
            end = t;
        }
        return string.substring(start, end);
    }

    public long _rand(String[] args) throws Exception {
        Macro.verifyCommand(args, _rand, null, 2, 3);
        int min = 0;
        int max = 100;
        if (args.length > 1) {
            max = Integer.parseInt(args[1]);
            if (args.length > 2) {
                min = Integer.parseInt(args[2]);
            }
        }
        int diff = max - min;
        double d = random.nextDouble() * (double)diff + (double)min;
        return Math.round(d);
    }

    public int _length(String[] args) throws Exception {
        Macro.verifyCommand(args, _length, null, 1, 2);
        if (args.length == 1) {
            return 0;
        }
        return args[1].length();
    }

    public String _get(String[] args) throws Exception {
        Macro.verifyCommand(args, _get, null, 3, 3);
        int index = Integer.parseInt(args[1]);
        ExtList<String> list = this.toList(args, 2, 3);
        if (index < 0) {
            index = list.size() + index;
        }
        return (String)list.get(index);
    }

    public String _sublist(String[] args) throws Exception {
        Macro.verifyCommand(args, _sublist, null, 4, Integer.MAX_VALUE);
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        ExtList<String> list = this.toList(args, 3, args.length);
        if (start < 0) {
            start = list.size() + start + 1;
        }
        if (end < 0) {
            end = list.size() + end + 1;
        }
        if (start > end) {
            int t = start;
            start = end;
            end = t;
        }
        return Processor.join(list.subList(start, end));
    }

    private ExtList<String> toList(String[] args, int i, int j) {
        ExtList<String> list = new ExtList<String>(new String[0]);
        while (i < j) {
            Processor.split(args[i], list);
            ++i;
        }
        return list;
    }

    public String _first(String[] args) throws Exception {
        Macro.verifyCommand(args, _first, null, 1, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        if (list.isEmpty()) {
            return "";
        }
        return (String)list.get(0);
    }

    public String _last(String[] args) throws Exception {
        Macro.verifyCommand(args, _last, null, 1, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        if (list.isEmpty()) {
            return "";
        }
        return (String)list.get(list.size() - 1);
    }

    public String _max(String[] args) throws Exception {
        Macro.verifyCommand(args, _max, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        String a = null;
        for (String s : list) {
            if (a != null && a.compareTo(s) >= 0) continue;
            a = s;
        }
        if (a == null) {
            return "";
        }
        return a;
    }

    public String _min(String[] args) throws Exception {
        Macro.verifyCommand(args, _min, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        String a = null;
        for (String s : list) {
            if (a != null && a.compareTo(s) <= 0) continue;
            a = s;
        }
        if (a == null) {
            return "";
        }
        return a;
    }

    public String _nmax(String[] args) throws Exception {
        Macro.verifyCommand(args, _nmax, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        double d = Double.NaN;
        for (String s : list) {
            double v = Double.parseDouble(s);
            if (!Double.isNaN(d) && !(v > d)) continue;
            d = v;
        }
        return this.toString(d);
    }

    public String _nmin(String[] args) throws Exception {
        Macro.verifyCommand(args, _nmin, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        double d = Double.NaN;
        for (String s : list) {
            double v = Double.parseDouble(s);
            if (!Double.isNaN(d) && !(v < d)) continue;
            d = v;
        }
        return this.toString(d);
    }

    public String _sum(String[] args) throws Exception {
        Macro.verifyCommand(args, _sum, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        double d = 0.0;
        for (String s : list) {
            double v = Double.parseDouble(s);
            d += v;
        }
        return this.toString(d);
    }

    public String _average(String[] args) throws Exception {
        Macro.verifyCommand(args, _sum, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No members in list to calculate average");
        }
        double d = 0.0;
        for (String s : list) {
            double v = Double.parseDouble(s);
            d += v;
        }
        return this.toString(d / (double)list.size());
    }

    public String _reverse(String[] args) throws Exception {
        Macro.verifyCommand(args, _reverse, null, 2, Integer.MAX_VALUE);
        ExtList<String> list = this.toList(args, 1, args.length);
        Collections.reverse(list);
        return Processor.join(list);
    }

    public int _indexof(String[] args) throws Exception {
        Macro.verifyCommand(args, _indexof, null, 3, Integer.MAX_VALUE);
        String value = args[1];
        ExtList<String> list = this.toList(args, 2, args.length);
        return list.indexOf(value);
    }

    public int _lastindexof(String[] args) throws Exception {
        Macro.verifyCommand(args, _indexof, null, 3, Integer.MAX_VALUE);
        String value = args[1];
        ExtList<String> list = this.toList(args, 1, args.length);
        return list.lastIndexOf(value);
    }

    public int _find(String[] args) throws Exception {
        Macro.verifyCommand(args, _find, null, 3, 3);
        return args[1].indexOf(args[2]);
    }

    public int _findlast(String[] args) throws Exception {
        Macro.verifyCommand(args, _findlast, null, 3, 3);
        return args[2].lastIndexOf(args[1]);
    }

    public String _split(String[] args) throws Exception {
        Macro.verifyCommand(args, _split, null, 2, Integer.MAX_VALUE);
        ArrayList<String> collected = new ArrayList<String>();
        for (int n = 2; n < args.length; ++n) {
            String[] split;
            String value = args[n];
            for (String s : split = value.split(args[1])) {
                if (s.isEmpty()) continue;
                collected.add(s);
            }
        }
        return Processor.join(collected);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object _js(String[] args) throws Exception {
        Macro.verifyCommand(args, _js, null, 2, Integer.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            sb.append(args[i]).append(';');
        }
        if (this.context == null) {
            Macro i = this;
            synchronized (i) {
                if (this.engine == null) {
                    this.engine = new ScriptEngineManager().getEngineByName("javascript");
                }
            }
            this.context = this.engine.getContext();
            this.bindings = this.context.getBindings(100);
            this.bindings.put("domain", (Object)this.domain);
            String javascript = this.domain.mergeProperties("javascript", ";");
            if (javascript != null && javascript.length() > 0) {
                this.engine.eval(javascript, this.context);
            }
            this.context.setErrorWriter(this.stderr);
            this.context.setWriter(this.stdout);
        }
        Object eval = this.engine.eval(sb.toString(), this.context);
        StringBuffer buffer = this.stdout.getBuffer();
        if (buffer.length() > 0) {
            this.domain.error("Executing js: %s: %s", sb, buffer);
            buffer.setLength(0);
        }
        if (eval != null) {
            return this.toString(eval);
        }
        String out = this.stdout.toString();
        this.stdout.getBuffer().setLength(0);
        return out;
    }

    private String toString(Object eval) {
        String v;
        if (eval == null) {
            return "null";
        }
        if ((eval instanceof Double || eval instanceof Float) && (v = eval.toString()).endsWith(".0")) {
            return v.substring(0, v.length() - 2);
        }
        return eval.toString();
    }

    public String _toupper(String[] args) throws Exception {
        Macro.verifyCommand(args, _tolower, null, 2, 2);
        return args[1].toUpperCase();
    }

    public String _tolower(String[] args) throws Exception {
        Macro.verifyCommand(args, _tolower, null, 2, 2);
        return args[1].toLowerCase();
    }

    public int _compare(String[] args) throws Exception {
        Macro.verifyCommand(args, _compare, null, 3, 3);
        int n = args[1].compareTo(args[2]);
        if (n == 0) {
            return 0;
        }
        return n > 0 ? 1 : -1;
    }

    public int _ncompare(String[] args) throws Exception {
        Macro.verifyCommand(args, _ncompare, null, 3, 3);
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        if (a > b) {
            return 1;
        }
        if (a < b) {
            return -1;
        }
        return 0;
    }

    public boolean _matches(String[] args) throws Exception {
        Macro.verifyCommand(args, _matches, null, 3, 3);
        return args[1].matches(args[2]);
    }

    public StringBuffer _subst(String[] args) throws Exception {
        Macro.verifyCommand(args, _subst, null, 4, 5);
        Pattern p = Pattern.compile(args[2]);
        Matcher matcher = p.matcher(args[1]);
        String replace = "";
        int count = Integer.MAX_VALUE;
        if (args.length > 3) {
            replace = args[3];
        }
        if (args.length > 4) {
            count = Integer.parseInt(args[4]);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count && matcher.find(); ++i) {
            matcher.appendReplacement(sb, replace);
        }
        matcher.appendTail(sb);
        return sb;
    }

    public String _trim(String[] args) throws Exception {
        Macro.verifyCommand(args, _trim, null, 2, 2);
        return args[1].trim();
    }

    public String _format(String[] args) throws Exception {
        Macro.verifyCommand(args, _format, null, 2, Integer.MAX_VALUE);
        Object[] args2 = new Object[args.length + 10];
        Matcher m = PRINTF_P.matcher(args[1]);
        int n = 2;
        while (n < args.length && m.find()) {
            char conversion = m.group(5).charAt(0);
            switch (conversion) {
                case 'X': 
                case 'Z': 
                case 'd': 
                case 'o': 
                case 'u': 
                case 'x': 
                case 'z': {
                    args2[n - 2] = Long.parseLong(args[n]);
                    ++n;
                    break;
                }
                case 'A': 
                case 'E': 
                case 'G': 
                case 'a': 
                case 'e': 
                case 'f': 
                case 'g': {
                    args2[n - 2] = Double.parseDouble(args[n]);
                    ++n;
                    break;
                }
                case 'c': {
                    if (args[n].length() != 1) {
                        throw new IllegalArgumentException("Character expected but found '" + args[n] + "'");
                    }
                    args2[n - 2] = Character.valueOf(args[n].charAt(0));
                    ++n;
                    break;
                }
                case 'b': {
                    String v = args[n].toLowerCase();
                    args2[n - 2] = v == null || v.equals("false") || v.isEmpty() || NUMERIC_P.matcher(v).matches() && Double.parseDouble(v) == 0.0 ? Boolean.valueOf(false) : Boolean.valueOf(false);
                    ++n;
                    break;
                }
                case 'H': 
                case 'h': 
                case 'p': 
                case 's': {
                    args2[n - 2] = args[n];
                    ++n;
                    break;
                }
                case 'T': 
                case 't': {
                    SimpleDateFormat df;
                    String dt = args[n];
                    if (NUMERIC_P.matcher(dt).matches()) {
                        args2[n - 2] = Long.parseLong(dt);
                        break;
                    }
                    switch (args[n].length()) {
                        case 6: {
                            df = new SimpleDateFormat("yyMMdd", Locale.US);
                            break;
                        }
                        case 8: {
                            df = new SimpleDateFormat("yyyyMMdd", Locale.US);
                            break;
                        }
                        case 12: {
                            df = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
                            break;
                        }
                        case 14: {
                            df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                            break;
                        }
                        case 19: {
                            df = new SimpleDateFormat("yyyyMMddHHmmss.SSSZ", Locale.US);
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Unknown dateformat " + args[n]);
                        }
                    }
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    args2[n - 2] = df.parse(args[n]);
                    break;
                }
            }
        }
        try (Formatter f = new Formatter();){
            f.format(args[1], args2);
            String string = f.toString();
            return string;
        }
    }

    public boolean _isempty(String[] args) throws Exception {
        Macro.verifyCommand(args, _isempty, null, 1, Integer.MAX_VALUE);
        for (int i = 1; i < args.length; ++i) {
            if (args[i].trim().isEmpty()) continue;
            return false;
        }
        return true;
    }

    public boolean _isnumber(String[] args) throws Exception {
        Macro.verifyCommand(args, _isnumber, null, 2, Integer.MAX_VALUE);
        for (int i = 1; i < args.length; ++i) {
            if (NUMERIC_P.matcher(args[i]).matches()) continue;
            return false;
        }
        return true;
    }

    public boolean _is(String[] args) throws Exception {
        Macro.verifyCommand(args, _is, null, 3, Integer.MAX_VALUE);
        String a = args[1];
        for (int i = 2; i < args.length; ++i) {
            if (a.equals(args[i])) continue;
            return false;
        }
        return true;
    }

    public String _map(String[] args) throws Exception {
        Macro.verifyCommand(args, _map, null, 2, Integer.MAX_VALUE);
        String macro = args[1];
        ExtList<String> list = this.toList(args, 2, args.length);
        ArrayList<String> result = new ArrayList<String>();
        for (String s : list) {
            String invoc = this.process("${" + macro + ";" + s + "}");
            result.add(invoc);
        }
        return Processor.join(result);
    }

    public String _foreach(String[] args) throws Exception {
        Macro.verifyCommand(args, _foreach, null, 2, Integer.MAX_VALUE);
        String macro = args[1];
        ExtList<String> list = this.toList(args, 2, args.length);
        ArrayList<String> result = new ArrayList<String>();
        int n = 0;
        for (String s : list) {
            String invoc = this.process("${" + macro + ";" + s + ";" + n++ + "}");
            result.add(invoc);
        }
        return Processor.join(result);
    }

    public String _apply(String[] args) throws Exception {
        Macro.verifyCommand(args, _apply, null, 2, Integer.MAX_VALUE);
        String macro = args[1];
        ExtList<String> list = this.toList(args, 2, args.length);
        ArrayList result = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("${").append(macro);
        for (String s : list) {
            sb.append(";").append(s);
        }
        sb.append("}");
        return this.process(sb.toString());
    }

    public String _bytes(String[] args) {
        try (Formatter sb = new Formatter();){
            for (int i = 0; i < args.length; ++i) {
                long l = Long.parseLong(args[1]);
                this.bytes(sb, l, 0, new String[]{"b", "Kb", "Mb", "Gb", "Tb", "Pb", "Eb", "Zb", "Yb", "Bb", "Geopbyte"});
            }
            String string = sb.toString();
            return string;
        }
    }

    private void bytes(Formatter sb, double l, int i, String[] strings) {
        if (l > 1024.0 && i < strings.length - 1) {
            this.bytes(sb, l / 1024.0, i + 1, strings);
            return;
        }
        l = Math.round(l * 10.0) / 10L;
        sb.format("%s %s", l, strings[i]);
    }

    public String _glob(String[] args) {
        Macro.verifyCommand(args, _globHelp, null, 2, 2);
        String glob = args[1];
        boolean negate = false;
        if (glob.startsWith("!")) {
            glob = glob.substring(1);
            negate = true;
        }
        Pattern pattern = Glob.toPattern(glob);
        if (negate) {
            return "(?!" + pattern.pattern() + ")";
        }
        return pattern.pattern();
    }

    public boolean doCondition(String arg) throws Exception {
        ExtendedFilter f = new ExtendedFilter(arg);
        return f.match(new Get(){

            @Override
            public Object get(String key) throws Exception {
                if (key.endsWith("[]")) {
                    key = key.substring(0, key.length() - 2);
                    return Macro.this.toCollection(Macro.this.domain.getProperty(key));
                }
                return Macro.this.domain.getProperty(key);
            }
        });
    }

    static class Link {
        Link previous;
        String key;
        Processor start;

        public Link(Processor start, Link previous, String key) {
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
            StringBuilder sb = new StringBuilder();
            String del = "[";
            Link r = this;
            while (r != null) {
                sb.append(del);
                sb.append(r.key);
                del = ",";
                r = r.previous;
            }
            sb.append("]");
            return sb.toString();
        }
    }
}

