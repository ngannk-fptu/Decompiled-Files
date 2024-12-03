/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.regexp.Jdk14RegexpMatcher;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class Jdk14RegexpRegexp
extends Jdk14RegexpMatcher
implements Regexp {
    private static final int DECIMAL = 10;

    protected int getSubsOptions(int options) {
        int subsOptions = 1;
        if (RegexpUtil.hasFlag(options, 16)) {
            subsOptions = 16;
        }
        return subsOptions;
    }

    @Override
    public String substitute(String input, String argument, int options) throws BuildException {
        StringBuilder subst = new StringBuilder();
        for (int i = 0; i < argument.length(); ++i) {
            char c = argument.charAt(i);
            if (c == '$') {
                subst.append('\\');
                subst.append('$');
                continue;
            }
            if (c == '\\') {
                if (++i < argument.length()) {
                    c = argument.charAt(i);
                    int value = Character.digit(c, 10);
                    if (value > -1) {
                        subst.append('$').append(value);
                        continue;
                    }
                    subst.append(c);
                    continue;
                }
                subst.append('\\');
                continue;
            }
            subst.append(c);
        }
        int sOptions = this.getSubsOptions(options);
        Pattern p = this.getCompiledPattern(options);
        StringBuffer sb = new StringBuffer();
        Matcher m = p.matcher(input);
        if (RegexpUtil.hasFlag(sOptions, 16)) {
            sb.append(m.replaceAll(subst.toString()));
        } else if (m.find()) {
            m.appendReplacement(sb, subst.toString());
            m.appendTail(sb);
        } else {
            sb.append(input);
        }
        return sb.toString();
    }
}

