/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOCase
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.WildcardFileFilter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.healthcheck.LogFileHelper;
import com.atlassian.troubleshooting.stp.mxbean.MXBeanProvider;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultLogFileHelper
implements LogFileHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLogFileHelper.class);
    private static final String CATALINA_OUT_RELATIVE_PATH = "/logs";
    private final MXBeanProvider mxBeanProvider;
    private final List<String> catalinaOutLocationCandidatePaths = Arrays.asList(System.getenv("CATALINA_OUT"), System.getProperty("user.dir") + "/logs", System.getProperty("catalina.base") + "/logs", System.getProperty("catalina.home") + "/logs", System.getProperty("working.dir") + "/logs", System.getenv("CATALINA_BASE") + "/logs", System.getenv("CATALINA_HOME") + "/logs");
    private final WildcardFileFilter catalinaOutFileFilter = new WildcardFileFilter(new String[]{"catalina*.out", "catalina*.log"}, IOCase.INSENSITIVE);

    @Autowired
    public DefaultLogFileHelper(MXBeanProvider mxBeanProvider) {
        this.mxBeanProvider = mxBeanProvider;
    }

    @Override
    public File getCurrentGCLog(File logDir) {
        return this.getLatestFile(logDir, (FileFilter)new WildcardFileFilter("*gc*.log.*.current"));
    }

    @Override
    public File getGCLogDir() {
        for (String arg : this.mxBeanProvider.getRuntimeMXBean().getInputArguments()) {
            if (!arg.contains("Xloggc")) continue;
            return new File(arg.substring(arg.indexOf(58) + 1)).getParentFile();
        }
        return null;
    }

    @Override
    public File getCurrentCompilationLog() {
        List<String> args = this.mxBeanProvider.getRuntimeMXBean().getInputArguments();
        Optional<String> unlockDiagnosticVMOptionsIsOn = args.stream().filter(a -> a.equals("-XX:+UnlockDiagnosticVMOptions")).findAny();
        Optional<String> logCompilationIsOn = args.stream().filter(a -> a.equals("-XX:+LogCompilation")).findAny();
        Optional<String> logFileSet = args.stream().filter(a -> a.startsWith("-XX:LogFile=")).findAny();
        if (!unlockDiagnosticVMOptionsIsOn.isPresent()) {
            return null;
        }
        if (!logCompilationIsOn.isPresent()) {
            return null;
        }
        if (logFileSet.isPresent()) {
            String arg = logFileSet.get();
            String pathString = this.getSubPart(arg, "=", 1);
            if (pathString != null) {
                Path path = Paths.get(pathString, new String[0]);
                return path.toFile();
            }
            LOG.debug("Unable to determine log path from {}", (Object)arg);
            return null;
        }
        String pid = this.getPid(this.mxBeanProvider.getRuntimeMXBean().getName());
        WildcardFileFilter hotspotLogFileFilter = new WildcardFileFilter(new String[]{"hotspot.log", "hotspot_pid" + pid + ".log"}, IOCase.INSENSITIVE);
        String path = System.getProperty("user.dir");
        File file = this.getLatestFile(new File(path), (FileFilter)hotspotLogFileFilter);
        if (file != null) {
            LOG.debug("Found hotspot log at {}", (Object)file.getName());
            return file;
        }
        LOG.debug("Unable to find hotspot log");
        return null;
    }

    private String getPid(String name) {
        return this.getSubPart(name, "@", 0);
    }

    private String getSubPart(String name, String divider, int index) {
        String[] parts = name.split(divider);
        if (parts.length < index - 1) {
            return null;
        }
        return parts[index];
    }

    @Override
    public File getCurrentCatalinaOut() {
        File catalinaOutFile = this.getCatalinaOutFile();
        if (catalinaOutFile != null && catalinaOutFile.exists()) {
            return catalinaOutFile;
        }
        return null;
    }

    private File getCatalinaOutFile() {
        for (String s : this.catalinaOutLocationCandidatePaths) {
            Path dirPath;
            File parentDir;
            if (s == null || !(parentDir = (dirPath = Paths.get(s, new String[0])).toFile()).exists()) continue;
            LOG.debug("Search {} for catalina log", (Object)parentDir.getName());
            File file = this.getLatestFile(parentDir, (FileFilter)this.catalinaOutFileFilter);
            if (file == null) continue;
            LOG.debug("Found catalina log at {}", (Object)file.getName());
            return file;
        }
        LOG.debug("Unable to find catalina log");
        return null;
    }

    private File getLatestFile(File parentDir, FileFilter fileFilter) {
        File currentLog = null;
        File[] files = parentDir.listFiles(fileFilter);
        if (files != null && files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            currentLog = files[0];
        }
        return currentLog;
    }
}

