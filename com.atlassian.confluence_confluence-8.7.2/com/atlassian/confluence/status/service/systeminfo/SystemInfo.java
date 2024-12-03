/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.util.Container
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.opensymphony.module.sitemesh.util.Container;

public class SystemInfo {
    private String date;
    private String time;
    private String javaVersion;
    private String javaVendor;
    private String javaSpecificationVersion;
    private String jvmVersion;
    private String jvmVendor;
    private String jvmImplementationVersion;
    private String javaRuntime;
    private String javaVm;
    private String userName;
    private String systemLanguage;
    private String systemTimezone;
    private String operatingSystem;
    private String operatingSystemArchitecture;
    private String fileSystemEncoding;
    private String jvmInputArguments;
    private String jvmInputArgumentsFiltered;
    private String workingDirectory;
    private String tempDirectory;
    private String operatingSystemName;
    private String operatingSystemVersion;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJavaVersion() {
        return this.javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getJavaVendor() {
        return this.javaVendor;
    }

    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    public String getJavaSpecificationVersion() {
        return this.javaSpecificationVersion;
    }

    public void setJavaSpecificationVersion(String javaSpecificationVersion) {
        this.javaSpecificationVersion = javaSpecificationVersion;
    }

    public String getJvmVersion() {
        return this.jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public String getJvmVendor() {
        return this.jvmVendor;
    }

    public void setJvmVendor(String jvmVendor) {
        this.jvmVendor = jvmVendor;
    }

    public String getJvmImplementationVersion() {
        return this.jvmImplementationVersion;
    }

    public void setJvmImplementationVersion(String jvmImplementationVersion) {
        this.jvmImplementationVersion = jvmImplementationVersion;
    }

    public String getJavaRuntime() {
        return this.javaRuntime;
    }

    public void setJavaRuntime(String javaRuntime) {
        this.javaRuntime = javaRuntime;
    }

    public String getJavaVm() {
        return this.javaVm;
    }

    public void setJavaVm(String javaVm) {
        this.javaVm = javaVm;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSystemLanguage() {
        return this.systemLanguage;
    }

    public void setSystemLanguage(String systemLanguage) {
        this.systemLanguage = systemLanguage;
    }

    public String getSystemTimezone() {
        return this.systemTimezone;
    }

    public void setSystemTimezone(String systemTimezone) {
        this.systemTimezone = systemTimezone;
    }

    public String getOperatingSystem() {
        return this.operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getOperatingSystemName() {
        return this.operatingSystemName;
    }

    public void setOperatingSystemName(String operatingSystemName) {
        this.operatingSystemName = operatingSystemName;
    }

    public String getOperatingSystemVersion() {
        return this.operatingSystemVersion;
    }

    public void setOperatingSystemVersion(String operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public String getOperatingSystemArchitecture() {
        return this.operatingSystemArchitecture;
    }

    public void setOperatingSystemArchitecture(String operatingSystemArchitecture) {
        this.operatingSystemArchitecture = operatingSystemArchitecture;
    }

    public String getFileSystemEncoding() {
        return this.fileSystemEncoding;
    }

    public void setFileSystemEncoding(String fileSystemEncoding) {
        this.fileSystemEncoding = fileSystemEncoding;
    }

    public String getJvmInputArguments() {
        return this.jvmInputArguments;
    }

    public void setJvmInputArguments(String jvmInputArguments) {
        this.jvmInputArguments = jvmInputArguments;
    }

    public String getJvmInputArgumentsFiltered() {
        return this.jvmInputArgumentsFiltered;
    }

    public void setJvmInputArgumentsFiltered(String jvmInputArgumentsFiltered) {
        this.jvmInputArgumentsFiltered = jvmInputArgumentsFiltered;
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getTempDirectory() {
        return this.tempDirectory;
    }

    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    public String getAppServer() {
        switch (Container.get()) {
            case 1: {
                return "Apache Tomcat";
            }
        }
        return "Unknown";
    }
}

