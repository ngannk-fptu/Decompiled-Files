/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import org.apache.felix.bundlerepository.R4Attribute;
import org.apache.felix.bundlerepository.R4Directive;
import org.apache.felix.bundlerepository.R4Export;
import org.apache.felix.bundlerepository.R4Package;
import org.apache.felix.bundlerepository.VersionRange;
import org.osgi.framework.Version;

public class R4Import
extends R4Package {
    private VersionRange m_versionRange = null;
    private boolean m_isOptional = false;

    public R4Import(R4Package pkg) {
        this(pkg.getName(), pkg.getDirectives(), pkg.getAttributes());
    }

    public R4Import(String name, R4Directive[] directives, R4Attribute[] attrs) {
        super(name, directives, attrs);
        for (int i = 0; i < this.m_directives.length; ++i) {
            if (!this.m_directives[i].getName().equals("resolution")) continue;
            this.m_isOptional = this.m_directives[i].getValue().equals("optional");
        }
        String rangeStr = "0.0.0";
        for (int i = 0; i < this.m_attrs.length; ++i) {
            if (!this.m_attrs[i].getName().equals("version") && !this.m_attrs[i].getName().equals("specification-version")) continue;
            this.m_attrs[i] = new R4Attribute("version", this.m_attrs[i].getValue(), this.m_attrs[i].isMandatory());
            rangeStr = this.m_attrs[i].getValue();
            break;
        }
        this.m_versionRange = VersionRange.parse(rangeStr);
        this.m_version = this.m_versionRange.getLow();
    }

    public Version getVersionHigh() {
        return this.m_versionRange.getHigh();
    }

    public boolean isLowInclusive() {
        return this.m_versionRange.isLowInclusive();
    }

    public boolean isHighInclusive() {
        return this.m_versionRange.isHighInclusive();
    }

    public boolean isOptional() {
        return this.m_isOptional;
    }

    public boolean isSatisfied(R4Export export) {
        if (!this.getName().equals(export.getName())) {
            return false;
        }
        return this.m_versionRange.isInRange(export.getVersion()) && this.doAttributesMatch(export);
    }

    private boolean doAttributesMatch(R4Export export) {
        boolean found;
        for (int impAttrIdx = 0; impAttrIdx < this.getAttributes().length; ++impAttrIdx) {
            R4Attribute impAttr = this.getAttributes()[impAttrIdx];
            if (impAttr.getName().equals("version")) continue;
            found = false;
            for (int expAttrIdx = 0; !found && expAttrIdx < export.getAttributes().length; ++expAttrIdx) {
                R4Attribute expAttr = export.getAttributes()[expAttrIdx];
                if (!impAttr.getName().equals(expAttr.getName())) continue;
                if (!impAttr.getValue().equals(expAttr.getValue())) {
                    return false;
                }
                found = true;
            }
            if (found) continue;
            return false;
        }
        for (int expAttrIdx = 0; expAttrIdx < export.getAttributes().length; ++expAttrIdx) {
            R4Attribute expAttr = export.getAttributes()[expAttrIdx];
            if (!expAttr.isMandatory()) continue;
            found = false;
            for (int impAttrIdx = 0; !found && impAttrIdx < this.getAttributes().length; ++impAttrIdx) {
                R4Attribute impAttr = this.getAttributes()[impAttrIdx];
                if (!expAttr.getName().equals(impAttr.getName())) continue;
                found = true;
            }
            if (found) continue;
            return false;
        }
        return true;
    }
}

