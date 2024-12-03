/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.modules;

import org.jfree.base.modules.ModuleInfo;

public class DefaultModuleInfo
implements ModuleInfo {
    private String moduleClass;
    private String majorVersion;
    private String minorVersion;
    private String patchLevel;

    public DefaultModuleInfo() {
    }

    public DefaultModuleInfo(String moduleClass, String majorVersion, String minorVersion, String patchLevel) {
        if (moduleClass == null) {
            throw new NullPointerException("Module class must not be null.");
        }
        this.moduleClass = moduleClass;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchLevel = patchLevel;
    }

    public String getModuleClass() {
        return this.moduleClass;
    }

    public void setModuleClass(String moduleClass) {
        if (moduleClass == null) {
            throw new NullPointerException();
        }
        this.moduleClass = moduleClass;
    }

    public String getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
    }

    public String getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(String minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getPatchLevel() {
        return this.patchLevel;
    }

    public void setPatchLevel(String patchLevel) {
        this.patchLevel = patchLevel;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultModuleInfo)) {
            return false;
        }
        ModuleInfo defaultModuleInfo = (ModuleInfo)o;
        return this.moduleClass.equals(defaultModuleInfo.getModuleClass());
    }

    public int hashCode() {
        int result = this.moduleClass.hashCode();
        return result;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getName());
        buffer.append("={ModuleClass=");
        buffer.append(this.getModuleClass());
        if (this.getMajorVersion() != null) {
            buffer.append("; Version=");
            buffer.append(this.getMajorVersion());
            if (this.getMinorVersion() != null) {
                buffer.append("-");
                buffer.append(this.getMinorVersion());
                if (this.getPatchLevel() != null) {
                    buffer.append("_");
                    buffer.append(this.getPatchLevel());
                }
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
}

