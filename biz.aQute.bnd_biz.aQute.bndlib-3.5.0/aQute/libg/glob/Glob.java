/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.glob;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Glob {
    public static final Glob ALL = new Glob("*");
    private final String glob;
    private final Pattern pattern;

    public Glob(String globString) {
        this(globString, 0);
    }

    public Glob(String globString, int flags) {
        this.glob = globString;
        this.pattern = Pattern.compile(Glob.convertGlobToRegEx(globString), flags);
    }

    public Matcher matcher(CharSequence input) {
        return this.pattern.matcher(input);
    }

    public String toString() {
        return this.glob;
    }

    private static String convertGlobToRegEx(String line) {
        line = line.trim();
        int strLen = line.length();
        StringBuilder sb = new StringBuilder(strLen);
        boolean escaping = false;
        int inCurlies = 0;
        block9: for (char currentChar : line.toCharArray()) {
            switch (currentChar) {
                case '*': {
                    if (escaping) {
                        sb.append("\\*");
                    } else {
                        sb.append(".*");
                    }
                    escaping = false;
                    continue block9;
                }
                case '?': {
                    if (escaping) {
                        sb.append("\\?");
                    } else {
                        sb.append('.');
                    }
                    escaping = false;
                    continue block9;
                }
                case '$': 
                case '%': 
                case '(': 
                case ')': 
                case '+': 
                case '.': 
                case '@': 
                case '^': 
                case '|': {
                    sb.append('\\');
                    sb.append(currentChar);
                    escaping = false;
                    continue block9;
                }
                case '\\': {
                    if (escaping) {
                        sb.append("\\\\");
                        escaping = false;
                        continue block9;
                    }
                    escaping = true;
                    continue block9;
                }
                case '{': {
                    if (escaping) {
                        sb.append("\\{");
                    } else {
                        sb.append("(?:");
                        ++inCurlies;
                    }
                    escaping = false;
                    continue block9;
                }
                case '}': {
                    if (inCurlies > 0 && !escaping) {
                        sb.append(')');
                        --inCurlies;
                    } else if (escaping) {
                        sb.append("\\}");
                    } else {
                        sb.append("}");
                    }
                    escaping = false;
                    continue block9;
                }
                case ',': {
                    if (inCurlies > 0 && !escaping) {
                        sb.append('|');
                        continue block9;
                    }
                    if (escaping) {
                        sb.append("\\,");
                        continue block9;
                    }
                    sb.append(",");
                    continue block9;
                }
                default: {
                    escaping = false;
                    sb.append(currentChar);
                }
            }
        }
        return sb.toString();
    }

    public void select(List<?> objects) {
        Iterator<?> i = objects.iterator();
        while (i.hasNext()) {
            String s = i.next().toString();
            if (this.matcher(s).matches()) continue;
            i.remove();
        }
    }

    public static Pattern toPattern(String s) {
        return Glob.toPattern(s, 0);
    }

    public static Pattern toPattern(String s, int flags) {
        try {
            return Pattern.compile(Glob.convertGlobToRegEx(s), flags);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public List<File> getFiles(File root, boolean recursive, boolean usePath) {
        ArrayList<File> result = new ArrayList<File>();
        this.getFiles(root, result, recursive, usePath);
        return result;
    }

    public void getFiles(File root, List<File> result, boolean recursive, boolean usePath) {
        if (root == null || !root.isDirectory()) {
            return;
        }
        for (File sub : root.listFiles()) {
            if (sub.isFile()) {
                String s;
                String string = s = usePath ? sub.getAbsolutePath() : sub.getName();
                if (!this.matcher(s).matches()) continue;
                result.add(sub);
                continue;
            }
            if (!recursive || !sub.isDirectory()) continue;
            this.getFiles(sub, result, recursive, usePath);
        }
    }
}

