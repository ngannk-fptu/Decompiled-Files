/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.importexport.resource.DownloadResourceManager
 *  com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException
 *  com.atlassian.confluence.importexport.resource.DownloadResourcePrefixEnum
 *  com.atlassian.confluence.importexport.resource.DownloadResourceReader
 *  com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException
 *  com.atlassian.confluence.setup.BuildInformation
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  com.atlassian.confluence.util.profiling.TimedAnalytics
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.utils.process.ExternalProcess
 *  com.atlassian.utils.process.ExternalProcessBuilder
 *  com.atlassian.utils.process.ExternalProcessFactory
 *  com.atlassian.utils.process.OutputHandler
 *  com.atlassian.utils.process.ProcessMonitor
 *  com.google.common.base.Joiner
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Strings
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.SystemUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourcePrefixEnum;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import com.atlassian.confluence.plugins.synchrony.bootstrap.DefaultSynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.bootstrap.ExternalProcessBuilderFactory;
import com.atlassian.confluence.plugins.synchrony.bootstrap.LoggingOutputHandler;
import com.atlassian.confluence.plugins.synchrony.bootstrap.NonIdlingExternalProcessFactory;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyEnvironmentBuilder;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyProcessConfigurationUtils;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ExternalProcessBuilder;
import com.atlassian.utils.process.ExternalProcessFactory;
import com.atlassian.utils.process.OutputHandler;
import com.atlassian.utils.process.ProcessMonitor;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronyProcessBuilder {
    private static final Logger log = LoggerFactory.getLogger(SynchronyProcessBuilder.class);
    @VisibleForTesting
    static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    @VisibleForTesting
    static final String SYNCHRONY_ARGS_PROPERTIES = "synchrony-args.properties";
    private static final String JAVA_BINARY_NAME = "bin" + File.separator + (SystemUtils.IS_OS_WINDOWS ? "java.exe" : "java");
    private static final String SYNCHRONY_STANDALONE_JAR = "synchrony-standalone.jar";
    private static final String SYNCHRONY_ARGS_PROPERTY_PREFIX = "synchrony.jvm.arg.";
    private static final String PACKAGE_PATH_PREFIX = DownloadResourcePrefixEnum.PACKAGE_DOWNLOAD_RESOURCE_PREFIX.getPrefix();
    private static final String SAMPLE_SYNCHRONY_ARGS_PROPERTIES = "env/sample-synchrony-args.properties";
    private static final String[] SYNCHRONY_ARGS_BLACKLIST = new String[]{"synchrony.memory.max", "synchrony.stack.space", "synchrony.host", "reza.host", "synchrony.context.path", "reza.context.path", "synchrony.port", "reza.port", "synchrony.bind", "reza.bind", "synchrony.service.url", "reza.service.url", "synchrony.cluster.bind", "reza.cluster.bind"};
    private final ApplicationProperties applicationProperties;
    private final SystemInformationService systemInformationService;
    private final NonIdlingExternalProcessFactory nonIdlingExternalProcessFactory;
    private final ExternalProcessBuilderFactory externalProcessBuilderFactory;
    private final ClusterManager clusterManager;
    private final DownloadResourceManager downloadResourceManager;
    private final TomcatConfigHelper tomcatConfigHelper;

    @Autowired
    public SynchronyProcessBuilder(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport SystemInformationService systemInformationService, ExternalProcessBuilderFactory externalProcessBuilderFactory, NonIdlingExternalProcessFactory nonIdlingExternalProcessFactory, @ComponentImport(value="clusterManager") ClusterManager clusterManager, @ComponentImport(value="downloadResourceManager") DownloadResourceManager downloadResourceManager, @ComponentImport TomcatConfigHelper tomcatConfigHelper) {
        this.applicationProperties = applicationProperties;
        this.systemInformationService = systemInformationService;
        this.externalProcessBuilderFactory = externalProcessBuilderFactory;
        this.nonIdlingExternalProcessFactory = nonIdlingExternalProcessFactory;
        this.clusterManager = clusterManager;
        this.downloadResourceManager = downloadResourceManager;
        this.tomcatConfigHelper = tomcatConfigHelper;
    }

    public ExternalProcess build(ProcessMonitor synchronyProcessMonitor, SynchronyEnvironmentBuilder.SynchronyEnvironment synchronyEnvironment) {
        String extractDirectory = synchronyEnvironment.getSynchronyProperty(SynchronyEnv.ExtractDirectory);
        String synchronyJarPath = Strings.isNullOrEmpty((String)extractDirectory) ? TEMP_DIR + File.separator + BuildInformation.INSTANCE.getBundledSynchronyVersion() + ".jar" : extractDirectory + File.separator + SYNCHRONY_STANDALONE_JAR;
        File synchronyBinary = new File(synchronyJarPath);
        this.extractSynchronyBinaryTo(synchronyBinary);
        String workingDir = synchronyEnvironment.getSynchronyProperty(SynchronyEnv.WorkingDirectory);
        File synchronyWorkingDir = Strings.isNullOrEmpty((String)workingDir) ? ((Path)this.applicationProperties.getLocalHomeDirectory().orElseThrow(IllegalStateException::new)).toFile() : new File(workingDir);
        File javaBinary = new File(this.tomcatConfigHelper.getJavaRuntimeDirectory(), JAVA_BINARY_NAME);
        DatabaseInfo databaseInfo = this.systemInformationService.getDatabaseInfo();
        ImmutableList.Builder commandBuilder = ImmutableList.builder();
        commandBuilder.add((Object[])new String[]{javaBinary.getAbsolutePath(), "-classpath", this.formatClasspath(synchronyBinary, databaseInfo.getDriverFile()), "-Xss" + synchronyEnvironment.getSynchronyProperty(SynchronyEnv.StackSpace), "-Xmx" + synchronyEnvironment.getSynchronyProperty(SynchronyEnv.Memory)});
        HashSet<String> synchronySysProps = new HashSet<String>();
        this.populateSynchronyArguments(synchronySysProps);
        SynchronyProcessConfigurationUtils.addIpV6SupportIfNeeded(synchronyEnvironment.getAllSynchronyProperties(), synchronySysProps);
        if (!synchronySysProps.isEmpty()) {
            commandBuilder.addAll(synchronySysProps);
        }
        ImmutableList command = commandBuilder.add((Object[])new String[]{"synchrony.core", "sql"}).build();
        ExternalProcess newSynchronyProcess = this.buildExternalProcess(synchronyProcessMonitor, (List<String>)command, synchronyEnvironment, synchronyWorkingDir);
        this.debugPrintEnvironment(newSynchronyProcess, synchronyEnvironment, synchronyWorkingDir);
        return newSynchronyProcess;
    }

    private void setupSynchronyArgsFile(File synchronyArgsFile) {
        try (InputStream sampleProps = DefaultSynchronyProcessManager.class.getClassLoader().getResourceAsStream(SAMPLE_SYNCHRONY_ARGS_PROPERTIES);){
            FileUtils.copyToFile((InputStream)sampleProps, (File)synchronyArgsFile);
        }
        catch (IOException e) {
            log.warn("Problem setting up synchrony-args.properties file", (Throwable)e);
        }
    }

    @VisibleForTesting
    void populateSynchronyArguments(Collection<String> args) {
        if (this.clusterManager.isClustered()) {
            this.populateSynchronyArguments(new File(((Path)this.applicationProperties.getSharedHomeDirectory().orElseThrow(IllegalStateException::new)).toFile(), SYNCHRONY_ARGS_PROPERTIES), args);
        }
        this.populateSynchronyArguments(new File(((Path)this.applicationProperties.getLocalHomeDirectory().orElseThrow(IllegalStateException::new)).toFile(), SYNCHRONY_ARGS_PROPERTIES), args);
    }

    @VisibleForTesting
    void populateSynchronyArguments(File synchronyArgsFile, Collection<String> args) {
        block13: {
            try {
                if (synchronyArgsFile.exists()) {
                    Properties synchronyArgs = new Properties();
                    try (String[] synchronyArgsInputStream = new FileInputStream(synchronyArgsFile);){
                        synchronyArgs.load((InputStream)synchronyArgsInputStream);
                    }
                    for (String argumentName : SYNCHRONY_ARGS_BLACKLIST) {
                        Object removed = synchronyArgs.remove(argumentName);
                        if (removed == null) continue;
                        log.warn("Removed property [{}->{}] from synchrony sysprop argument list", (Object)argumentName, removed);
                    }
                    if (!synchronyArgs.isEmpty()) {
                        Enumeration<?> argEnum = synchronyArgs.propertyNames();
                        while (argEnum.hasMoreElements()) {
                            String argName = (String)argEnum.nextElement();
                            if (argName.startsWith(SYNCHRONY_ARGS_PROPERTY_PREFIX)) {
                                args.add(synchronyArgs.getProperty(argName));
                                continue;
                            }
                            args.add("-D" + argName + "=" + synchronyArgs.getProperty(argName));
                        }
                    }
                    break block13;
                }
                this.setupSynchronyArgsFile(synchronyArgsFile);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void debugPrintEnvironment(ExternalProcess process, SynchronyEnvironmentBuilder.SynchronyEnvironment synchronyEnvironment, File synchronyWorkingDir) {
        if (log.isInfoEnabled()) {
            log.info("Synchrony working dir: {}", (Object)synchronyWorkingDir.getAbsolutePath());
            log.info("Synchrony command line: {}", (Object)StringUtils.defaultIfEmpty((CharSequence)process.getCommandLine(), (CharSequence)"no commandLine"));
        }
        TreeMap<String, String> vars = new TreeMap<String, String>(synchronyEnvironment.getAllSynchronyProperties());
        int longest = vars.keySet().stream().map(String::length).reduce(0, Math::max);
        String format = "%-" + longest + "s -> %s";
        log.debug("Synchrony Env Variables:");
        vars.forEach((key, value) -> log.debug(String.format(format, key, StringUtils.abbreviate((String)(key.toLowerCase().contains("password") ? "*********" : value), (int)100))));
    }

    String formatClasspath(File ... jars) {
        return Joiner.on((char)File.pathSeparatorChar).skipNulls().join((Iterable)Collections2.transform(Arrays.asList(jars), file -> {
            if (file == null) {
                return null;
            }
            String fileAbsolutePath = file.getAbsolutePath();
            try {
                fileAbsolutePath = URLDecoder.decode(fileAbsolutePath, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                log.error("Cannot decode the jar's path: {}", (Object)file.getAbsolutePath());
            }
            return fileAbsolutePath;
        }));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ExternalProcess buildExternalProcess(ProcessMonitor synchronyProcessMonitor, List<String> command, SynchronyEnvironmentBuilder.SynchronyEnvironment synchronyEnvironment, File synchronyWorkingDir) {
        ExternalProcessFactory original = ExternalProcessBuilder.getExternalProcessFactory();
        try {
            ExternalProcessBuilder.setExternalProcessFactory((ExternalProcessFactory)this.nonIdlingExternalProcessFactory);
            ExternalProcessBuilder builder = this.externalProcessBuilderFactory.createBuilder();
            LoggingOutputHandler outputHandler = new LoggingOutputHandler();
            ExternalProcess externalProcess = builder.asynchronous().command(command, synchronyWorkingDir).env(synchronyEnvironment.getAllSynchronyProperties()).addMonitor(new ProcessMonitor[]{synchronyProcessMonitor}).executionTimeout(-1L).idleTimeout(9223371036854L).handlers((OutputHandler)outputHandler, (OutputHandler)outputHandler).build();
            return externalProcess;
        }
        finally {
            ExternalProcessBuilder.setExternalProcessFactory((ExternalProcessFactory)original);
        }
    }

    public void extractSynchronyBinaryTo(File dest) {
        try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.synchrony.extract");){
            log.debug("Start deleting old synchrony-standalone jar files under {}", (Object)dest.getParent());
            Stopwatch stopWatch = Stopwatch.createStarted();
            this.deleteOldSynchronyStandaloneJarFiles(dest.getParentFile());
            log.debug("Completed deleting old synchrony-standalone jar files under {} in {} ms", (Object)dest.getParent(), (Object)stopWatch.stop().elapsed(TimeUnit.MILLISECONDS));
            stopWatch.reset().start();
            log.debug("Start extracting Synchrony to {}", (Object)dest);
            try (FileOutputStream output = new FileOutputStream(dest);){
                DownloadResourceReader downloadResourceReader = this.downloadResourceManager.getResourceReader(null, PACKAGE_PATH_PREFIX + "/synchrony-standalone.jar", null);
                try (InputStream synchronyJar = downloadResourceReader.getStreamForReading();){
                    IOUtils.copyLarge((InputStream)synchronyJar, (OutputStream)output);
                }
                log.trace("Completed extracting Synchrony to {} in {} ms", (Object)dest, (Object)stopWatch.stop().elapsed(TimeUnit.MILLISECONDS));
            }
            catch (DownloadResourceNotFoundException | UnauthorizedDownloadResourceException | IOException e) {
                throw new RuntimeException(String.format("Failed to extract Synchrony to %s", dest), e);
            }
        }
    }

    private void deleteOldSynchronyStandaloneJarFiles(File directory) {
        File[] oldSynchronyStandaloneJarFiles = directory.listFiles((dir, name) -> name.matches("synchrony-standalone[0-9]*+\\.jar"));
        if (oldSynchronyStandaloneJarFiles != null) {
            for (File oldJarFile : oldSynchronyStandaloneJarFiles) {
                if (oldJarFile.delete()) continue;
                log.error("Can't remove {}", (Object)oldJarFile.getAbsolutePath());
            }
        }
    }
}

