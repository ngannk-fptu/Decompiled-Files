/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import java.util.ArrayList;
import org.apache.felix.bundlerepository.R4Attribute;
import org.apache.felix.bundlerepository.R4Directive;
import org.apache.felix.bundlerepository.Util;
import org.apache.felix.bundlerepository.VersionRange;
import org.osgi.framework.Version;

public class R4Package {
    private String m_name = "";
    protected R4Directive[] m_directives = null;
    protected R4Attribute[] m_attrs = null;
    protected Version m_version = null;

    public R4Package(String name, R4Directive[] directives, R4Attribute[] attrs) {
        this.m_name = name;
        this.m_directives = directives == null ? new R4Directive[]{} : directives;
        this.m_attrs = attrs == null ? new R4Attribute[]{} : attrs;
        String rangeStr = "0.0.0";
        for (int i = 0; i < this.m_attrs.length; ++i) {
            if (!this.m_attrs[i].getName().equals("version") && !this.m_attrs[i].getName().equals("specification-version")) continue;
            this.m_attrs[i] = new R4Attribute("version", this.m_attrs[i].getValue(), this.m_attrs[i].isMandatory());
            rangeStr = this.m_attrs[i].getValue();
            break;
        }
        VersionRange range = VersionRange.parse(rangeStr);
        this.m_version = range.getLow();
    }

    public String getName() {
        return this.m_name;
    }

    public R4Directive[] getDirectives() {
        return this.m_directives;
    }

    public R4Attribute[] getAttributes() {
        return this.m_attrs;
    }

    public Version getVersion() {
        return this.m_version;
    }

    public String toString() {
        int i;
        String msg = this.getName();
        for (i = 0; this.m_directives != null && i < this.m_directives.length; ++i) {
            msg = msg + " [" + this.m_directives[i].getName() + ":=" + this.m_directives[i].getValue() + "]";
        }
        for (i = 0; this.m_attrs != null && i < this.m_attrs.length; ++i) {
            msg = msg + " [" + this.m_attrs[i].getName() + "=" + this.m_attrs[i].getValue() + "]";
        }
        return msg;
    }

    public static R4Package[] parseImportOrExportHeader(String s) {
        R4Package[] pkgs = null;
        if (s != null) {
            if (s.length() == 0) {
                throw new IllegalArgumentException("The import and export headers cannot be an empty string.");
            }
            String[] ss = Util.parseDelimitedString(s, ",");
            pkgs = R4Package.parsePackageStrings(ss);
        }
        return pkgs == null ? new R4Package[]{} : pkgs;
    }

    public static R4Package[] parsePackageStrings(String[] ss) throws IllegalArgumentException {
        if (ss == null) {
            return null;
        }
        ArrayList<R4Package> completeList = new ArrayList<R4Package>();
        for (int ssIdx = 0; ssIdx < ss.length; ++ssIdx) {
            String[] pieces = Util.parseDelimitedString(ss[ssIdx], ";");
            int pkgCount = 0;
            for (int pieceIdx = 0; pieceIdx < pieces.length && pieces[pieceIdx].indexOf(61) < 0; ++pieceIdx) {
                ++pkgCount;
            }
            if (pkgCount == 0) {
                throw new IllegalArgumentException("No packages specified on import: " + ss[ssIdx]);
            }
            R4Directive[] dirs = new R4Directive[pieces.length - pkgCount];
            R4Attribute[] attrs = new R4Attribute[pieces.length - pkgCount];
            int dirCount = 0;
            int attrCount = 0;
            int idx = -1;
            String sep = null;
            for (int pieceIdx = pkgCount; pieceIdx < pieces.length; ++pieceIdx) {
                idx = pieces[pieceIdx].indexOf(":=");
                if (idx >= 0) {
                    sep = ":=";
                } else {
                    idx = pieces[pieceIdx].indexOf("=");
                    if (idx >= 0) {
                        sep = "=";
                    } else {
                        throw new IllegalArgumentException("Not a directive/attribute: " + ss[ssIdx]);
                    }
                }
                String key = pieces[pieceIdx].substring(0, idx).trim();
                String value = pieces[pieceIdx].substring(idx + sep.length()).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                if (sep.equals(":=")) {
                    dirs[dirCount++] = new R4Directive(key, value);
                    continue;
                }
                attrs[attrCount++] = new R4Attribute(key, value, false);
            }
            R4Directive[] dirsFinal = new R4Directive[dirCount];
            System.arraycopy(dirs, 0, dirsFinal, 0, dirCount);
            R4Attribute[] attrsFinal = new R4Attribute[attrCount];
            System.arraycopy(attrs, 0, attrsFinal, 0, attrCount);
            R4Package[] pkgs = new R4Package[pkgCount];
            for (int pkgIdx = 0; pkgIdx < pkgCount; ++pkgIdx) {
                pkgs[pkgIdx] = new R4Package(pieces[pkgIdx], dirsFinal, attrsFinal);
                completeList.add(pkgs[pkgIdx]);
            }
        }
        R4Package[] pkgs = completeList.toArray(new R4Package[completeList.size()]);
        return pkgs;
    }
}

