/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base;

import java.util.ArrayList;
import org.jfree.base.BasicProjectInfo;
import org.jfree.base.Library;

public class BootableProjectInfo
extends BasicProjectInfo {
    private String bootClass;
    private boolean autoBoot = true;

    public BootableProjectInfo() {
    }

    public BootableProjectInfo(String name, String version, String licence, String info) {
        this();
        this.setName(name);
        this.setVersion(version);
        this.setLicenceName(licence);
        this.setInfo(info);
    }

    public BootableProjectInfo(String name, String version, String info, String copyright, String licenceName) {
        this();
        this.setName(name);
        this.setVersion(version);
        this.setLicenceName(licenceName);
        this.setInfo(info);
        this.setCopyright(copyright);
    }

    public BootableProjectInfo[] getDependencies() {
        ArrayList<Library> dependencies = new ArrayList<Library>();
        Library[] libraries = this.getLibraries();
        for (int i = 0; i < libraries.length; ++i) {
            Library lib = libraries[i];
            if (!(lib instanceof BootableProjectInfo)) continue;
            dependencies.add(lib);
        }
        Library[] optionalLibraries = this.getOptionalLibraries();
        for (int i = 0; i < optionalLibraries.length; ++i) {
            Library lib = optionalLibraries[i];
            if (!(lib instanceof BootableProjectInfo)) continue;
            dependencies.add(lib);
        }
        return dependencies.toArray(new BootableProjectInfo[dependencies.size()]);
    }

    public void addDependency(BootableProjectInfo projectInfo) {
        if (projectInfo == null) {
            throw new NullPointerException();
        }
        this.addLibrary(projectInfo);
    }

    public String getBootClass() {
        return this.bootClass;
    }

    public void setBootClass(String bootClass) {
        this.bootClass = bootClass;
    }

    public boolean isAutoBoot() {
        return this.autoBoot;
    }

    public void setAutoBoot(boolean autoBoot) {
        this.autoBoot = autoBoot;
    }
}

