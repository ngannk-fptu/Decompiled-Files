/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class Jdk14RegexpMatcher
implements RegexpMatcher {
    private String pattern;

    @Override
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    protected Pattern getCompiledPattern(int options) throws BuildException {
        try {
            return Pattern.compile(this.pattern, this.getCompilerOptions(options));
        }
        catch (PatternSyntaxException e) {
            throw new BuildException(e);
        }
    }

    @Override
    public boolean matches(String argument) throws BuildException {
        return this.matches(argument, 0);
    }

    @Override
    public boolean matches(String input, int options) throws BuildException {
        try {
            return this.getCompiledPattern(options).matcher(input).find();
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    @Override
    public Vector<String> getGroups(String argument) throws BuildException {
        return this.getGroups(argument, 0);
    }

    @Override
    public Vector<String> getGroups(String input, int options) throws BuildException {
        Pattern p = this.getCompiledPattern(options);
        Matcher matcher = p.matcher(input);
        if (!matcher.find()) {
            return null;
        }
        Vector<String> v = new Vector<String>();
        int cnt = matcher.groupCount();
        for (int i = 0; i <= cnt; ++i) {
            String match = matcher.group(i);
            if (match == null) {
                match = "";
            }
            v.add(match);
        }
        return v;
    }

    protected int getCompilerOptions(int options) {
        int cOptions = 1;
        if (RegexpUtil.hasFlag(options, 256)) {
            cOptions |= 2;
        }
        if (RegexpUtil.hasFlag(options, 4096)) {
            cOptions |= 8;
        }
        if (RegexpUtil.hasFlag(options, 65536)) {
            cOptions |= 0x20;
        }
        return cOptions;
    }
}

