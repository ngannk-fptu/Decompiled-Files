/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.felix.bundlerepository.R4Attribute;
import org.apache.felix.bundlerepository.R4Directive;
import org.apache.felix.bundlerepository.R4Package;
import org.apache.felix.bundlerepository.Util;

public class R4Export
extends R4Package {
    private String[] m_uses = null;
    private String[][] m_includeFilter = null;
    private String[][] m_excludeFilter = null;

    public R4Export(R4Package pkg) {
        this(pkg.getName(), pkg.getDirectives(), pkg.getAttributes());
    }

    public R4Export(String name, R4Directive[] directives, R4Attribute[] attrs) {
        super(name, directives, attrs);
        String mandatory = "";
        String uses = "";
        for (int i = 0; i < this.m_directives.length; ++i) {
            int filterIdx;
            String[] ss;
            if (this.m_directives[i].getName().equals("uses")) {
                uses = this.m_directives[i].getValue();
                continue;
            }
            if (this.m_directives[i].getName().equals("mandatory")) {
                mandatory = this.m_directives[i].getValue();
                continue;
            }
            if (this.m_directives[i].getName().equals("include")) {
                ss = Util.parseDelimitedString(this.m_directives[i].getValue(), ",");
                this.m_includeFilter = new String[ss.length][];
                for (filterIdx = 0; filterIdx < ss.length; ++filterIdx) {
                    this.m_includeFilter[filterIdx] = R4Export.parseSubstring(ss[filterIdx]);
                }
                continue;
            }
            if (!this.m_directives[i].getName().equals("exclude")) continue;
            ss = Util.parseDelimitedString(this.m_directives[i].getValue(), ",");
            this.m_excludeFilter = new String[ss.length][];
            for (filterIdx = 0; filterIdx < ss.length; ++filterIdx) {
                this.m_excludeFilter[filterIdx] = R4Export.parseSubstring(ss[filterIdx]);
            }
        }
        StringTokenizer tok = new StringTokenizer(uses, ",");
        this.m_uses = new String[tok.countTokens()];
        for (int i = 0; i < this.m_uses.length; ++i) {
            this.m_uses[i] = tok.nextToken().trim();
        }
        tok = new StringTokenizer(mandatory, ",");
        while (tok.hasMoreTokens()) {
            String attrName = tok.nextToken().trim();
            boolean found = false;
            for (int i = 0; !found && i < this.m_attrs.length; ++i) {
                if (!this.m_attrs[i].getName().equals(attrName)) continue;
                this.m_attrs[i] = new R4Attribute(this.m_attrs[i].getName(), this.m_attrs[i].getValue(), true);
                found = true;
            }
            if (found) continue;
            throw new IllegalArgumentException("Mandatory attribute '" + attrName + "' does not exist.");
        }
    }

    public String[] getUses() {
        return this.m_uses;
    }

    public boolean isIncluded(String name) {
        if (this.m_includeFilter == null && this.m_excludeFilter == null) {
            return true;
        }
        String className = Util.getClassName(name);
        boolean included = this.m_includeFilter == null;
        for (int i = 0; !included && this.m_includeFilter != null && i < this.m_includeFilter.length; ++i) {
            included = R4Export.checkSubstring(this.m_includeFilter[i], className);
        }
        boolean excluded = false;
        for (int i = 0; !excluded && this.m_excludeFilter != null && i < this.m_excludeFilter.length; ++i) {
            excluded = R4Export.checkSubstring(this.m_excludeFilter[i], className);
        }
        return included && !excluded;
    }

    private static String[] parseSubstring(String target) {
        ArrayList<String> pieces = new ArrayList<String>();
        StringBuffer ss = new StringBuffer();
        boolean wasStar = false;
        boolean leftstar = false;
        boolean rightstar = false;
        int idx = 0;
        while (true) {
            char c;
            if (idx >= target.length()) {
                if (wasStar) {
                    rightstar = true;
                    break;
                }
                pieces.add(ss.toString());
                break;
            }
            if ((c = target.charAt(idx++)) == '*') {
                if (wasStar) {
                    throw new IllegalArgumentException("Invalid filter string: " + target);
                }
                if (ss.length() > 0) {
                    pieces.add(ss.toString());
                }
                ss.setLength(0);
                if (pieces.size() == 0) {
                    leftstar = true;
                }
                ss.setLength(0);
                wasStar = true;
                continue;
            }
            wasStar = false;
            ss.append(c);
        }
        ss.setLength(0);
        if (leftstar || rightstar || pieces.size() > 1) {
            if (rightstar) {
                pieces.add("");
            }
            if (leftstar) {
                pieces.add(0, "");
            }
        }
        return pieces.toArray(new String[pieces.size()]);
    }

    private static boolean checkSubstring(String[] pieces, String s) {
        boolean result = false;
        int len = pieces.length;
        for (int i = 0; i < len; ++i) {
            String piece = pieces[i];
            int index = 0;
            if (i == len - 1) {
                if (s.endsWith(piece)) {
                    result = true;
                    break;
                }
                result = false;
                break;
            }
            if (i == 0) {
                if (!s.startsWith(piece)) {
                    result = false;
                    break;
                }
            } else if ((index = s.indexOf(piece, index)) < 0) {
                result = false;
                break;
            }
            index += piece.length();
        }
        return result;
    }
}

