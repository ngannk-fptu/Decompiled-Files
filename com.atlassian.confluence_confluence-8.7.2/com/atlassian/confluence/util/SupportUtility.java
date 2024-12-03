/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionSupport
 *  com.opensymphony.xwork2.Validateable
 *  org.apache.commons.io.filefilter.RegexFileFilter
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.jmx.CurrentTimeFacade;
import com.atlassian.confluence.util.FileSanitizer;
import com.atlassian.confluence.util.ZipUtility;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Validateable;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportUtility
implements Validateable {
    private static final String SANITIZER_MESSAGE = "Sanitized by Support Utility";
    private static final Logger log = LoggerFactory.getLogger(SupportUtility.class);
    private static final Map<String, List<Pattern>> FILE_PATTERNS = new HashMap<String, List<Pattern>>();
    static final Pattern TOMCAT_USERS_SANITIZER_PATTERN = Pattern.compile(".*?<user\\b(?>username=(?:\"|')([^'\"]*)(?:\"|')|password=(?:\"|')([^'\"]*)(?:\"|')|[^\\s>]+|\\s+)+/?>.*?", 2);
    private final ActionSupport validationAware;
    private String localHomeDirectory;
    private String webInfClassesDir;
    private boolean includeLogs;
    private boolean includeServerLogs;
    private String serverLogsDirectory;
    private boolean includeTomcatConf;
    private boolean includeCfgFile;
    private boolean includeUserFiles;

    public SupportUtility(ActionSupport validationAware, String localHomeDirectory, String webInfClassesDir) {
        this.validationAware = validationAware;
        this.localHomeDirectory = localHomeDirectory;
        this.webInfClassesDir = webInfClassesDir;
        this.includeLogs = false;
        this.includeServerLogs = false;
        this.serverLogsDirectory = null;
        this.includeTomcatConf = false;
        this.includeCfgFile = false;
        this.includeUserFiles = false;
    }

    public void setDefaults() {
        this.includeLogs = true;
        this.includeServerLogs = true;
        this.includeTomcatConf = true;
        this.includeCfgFile = true;
        this.includeUserFiles = true;
    }

    public void validate() {
        if (!this.isTomcatServer() && this.includeServerLogs) {
            if (StringUtils.isBlank((CharSequence)this.serverLogsDirectory)) {
                this.validationAware.addFieldError("serverLogsDirectory", this.validationAware.getText("create.support.zip.error.no.log"));
            } else {
                File file = new File(this.serverLogsDirectory);
                if (!file.exists()) {
                    this.validationAware.addFieldError("serverLogsDirectory", this.validationAware.getText("create.support.zip.error.invalid.path"));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createSupportZip(File supportZip) throws IOException {
        try {
            supportZip.createNewFile();
        }
        catch (IOException e) {
            throw new IOException(e.getMessage() + " - " + supportZip.getAbsolutePath());
        }
        FileSanitizer sanitizer = new FileSanitizer(FILE_PATTERNS, SANITIZER_MESSAGE);
        ZipUtility.FileHandler sanitizeFileHandler = f -> {
            try {
                return sanitizer.sanitize(f);
            }
            catch (IOException ioe) {
                log.error("Could not sanitize file: " + f.getAbsolutePath(), (Throwable)ioe);
                return f;
            }
        };
        ZipUtility zipper = new ZipUtility(sanitizeFileHandler);
        this.addFilesToZip(zipper);
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(supportZip));){
            zipper.zip(out);
        }
        finally {
            sanitizer.cleanUpTempFiles();
        }
        log.info("Saved Support Zip to: " + supportZip.getAbsolutePath());
    }

    private void addFilesToZip(ZipUtility zipUtility) {
        File[] serverLogFiles;
        File serverLogsDir;
        File confluenceLogsDir;
        File[] confluenceLogFiles;
        if (this.includeLogs && (confluenceLogFiles = (confluenceLogsDir = new File(this.localHomeDirectory, "logs")).listFiles((FilenameFilter)new RegexFileFilter("^.*\\.log.*"))) != null) {
            for (File confluenceLogFile : confluenceLogFiles) {
                zipUtility.add("Confluence_Home/logs/" + confluenceLogFile.getName(), new File(confluenceLogsDir, confluenceLogFile.getName()));
            }
        }
        if (this.includeCfgFile) {
            File file = new File(this.localHomeDirectory, "confluence.cfg.xml");
            if (file.exists()) {
                log.debug("adding confluence.cfg.xml to zip");
                zipUtility.add("Confluence_Home/confluence.cfg.xml", file);
            } else {
                log.debug("Unable to find confluence.cfg.xml at: " + file.getAbsolutePath());
            }
        }
        if (this.includeServerLogs && (serverLogsDir = this.isTomcatServer() ? this.findTomcatFileOrDirectory("logs") : new File(this.serverLogsDirectory)) != null && serverLogsDir.exists() && (serverLogFiles = serverLogsDir.listFiles((FilenameFilter)new RegexFileFilter("^.*\\.log|^.*\\.out"))) != null) {
            for (File serverLogFile : serverLogFiles) {
                if (!serverLogFile.isFile()) continue;
                zipUtility.add("Server/logs/" + serverLogFile.getName(), new File(serverLogsDir, serverLogFile.getName()));
            }
        }
        if (this.isTomcatServer() && this.includeTomcatConf) {
            zipUtility.add("Tomcat/conf", this.findTomcatFileOrDirectory("conf"));
        }
        if (this.includeUserFiles) {
            zipUtility.add("WEB-INF/classes/atlassian-user.xml", new File(this.webInfClassesDir, "atlassian-user.xml"));
            zipUtility.add("WEB-INF/classes/osuser.xml", new File(this.webInfClassesDir, "osuser.xml"));
            zipUtility.add("WEB-INF/classes/crowd.properties", new File(this.webInfClassesDir, "crowd.properties"));
        }
        zipUtility.add("WEB-INF/classes/confluence-init.properties", new File(this.webInfClassesDir, "confluence-init.properties"));
        zipUtility.add("WEB-INF/classes/log4j.properties", new File(this.webInfClassesDir, "log4j.properties"));
        zipUtility.add("WEB-INF/classes/seraph-config.xml", new File(this.webInfClassesDir, "seraph-config.xml"));
        zipUtility.add("Confluence_Home/config", new File(this.localHomeDirectory, "config"));
    }

    public File createSupportZip() throws IOException {
        File supportDir = new File(this.localHomeDirectory, "logs/support");
        if (!supportDir.exists() && !supportDir.mkdirs()) {
            throw new IOException("Couldn't create export directory " + supportDir.getAbsolutePath());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String filename = "confluence_support_" + format.format(CurrentTimeFacade.getCurrentTime()) + ".zip";
        File supportZipFile = new File(supportDir, filename);
        this.createSupportZip(supportZipFile);
        return supportZipFile;
    }

    private File findTomcatFileOrDirectory(String fileOrDirectoryName) {
        String catalinaBase = System.getProperty("catalina.base");
        File file = new File(catalinaBase, fileOrDirectoryName);
        if (file.exists()) {
            return file;
        }
        String catalinaHome = System.getProperty("catalina.home");
        file = new File(catalinaHome, fileOrDirectoryName);
        if (file.exists()) {
            return file;
        }
        String workingDirecotry = System.getProperty("working.dir");
        file = new File(workingDirecotry + "../", fileOrDirectoryName);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public boolean isTomcatServer() {
        return ServletActionContext.getServletContext().getServerInfo().contains("Tomcat");
    }

    public boolean isIncludeLogs() {
        return this.includeLogs;
    }

    public void setIncludeLogs(boolean includeLogs) {
        this.includeLogs = includeLogs;
    }

    public boolean isIncludeServerLogs() {
        return this.includeServerLogs;
    }

    public void setIncludeServerLogs(boolean includeServerLogs) {
        this.includeServerLogs = includeServerLogs;
    }

    public String getServerLogsDirectory() {
        return this.serverLogsDirectory;
    }

    public void setServerLogsDirectory(String serverLogsDirectory) {
        this.serverLogsDirectory = serverLogsDirectory;
    }

    public boolean isIncludeTomcatConf() {
        return this.includeTomcatConf;
    }

    public void setIncludeTomcatConf(boolean includeTomcatConf) {
        this.includeTomcatConf = includeTomcatConf;
    }

    public boolean isIncludeCfgFile() {
        return this.includeCfgFile;
    }

    public void setIncludeCfgFile(boolean includeCfgFile) {
        log.debug("Setting include config file to: " + includeCfgFile);
        this.includeCfgFile = includeCfgFile;
    }

    public boolean isIncludeUserFiles() {
        return this.includeUserFiles;
    }

    public void setIncludeUserFiles(boolean includeUserFiles) {
        this.includeUserFiles = includeUserFiles;
    }

    static {
        FILE_PATTERNS.put("confluence.cfg.xml", Arrays.asList(Pattern.compile("(?:.*<property name=\"confluence\\.license\\.message\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"hibernate\\.connection\\.password\">)(.*)(?:</property>.*)"), Pattern.compile("(?:.*<property name=\"hibernate\\.connection\\.username\">)(.*)(?:</property>.*)")));
        FILE_PATTERNS.put("atlassian-user.xml", Arrays.asList(Pattern.compile("(?:.*<securityPrincipal>)(.*)(?:</securityPrincipal>.*)"), Pattern.compile("(?:.*<securityCredential>)(.*)(?:</securityCredential>.*)")));
        FILE_PATTERNS.put("tomcat-users.xml", Arrays.asList(TOMCAT_USERS_SANITIZER_PATTERN));
    }
}

