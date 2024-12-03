/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.taskdefs.MatchingTask
 *  org.apache.tools.ant.types.FileSet
 */
package org.hibernate.tool.hbm2ddl;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.tool.hbm2ddl.Target;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.DelayedDropRegistryNotAvailableImpl;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;

public class SchemaExportTask
extends MatchingTask {
    private List<FileSet> fileSets = new LinkedList<FileSet>();
    private File propertiesFile;
    private File configurationFile;
    private File outputFile;
    private boolean quiet;
    private boolean text;
    private boolean drop;
    private boolean create;
    private boolean haltOnError;
    private String delimiter;
    private String implicitNamingStrategy;
    private String physicalNamingStrategy;

    public void addFileset(FileSet set) {
        this.fileSets.add(set);
    }

    public void setProperties(File propertiesFile) {
        if (!propertiesFile.exists()) {
            throw new BuildException("Properties file: " + propertiesFile + " does not exist.");
        }
        this.log("Using properties file " + propertiesFile, 4);
        this.propertiesFile = propertiesFile;
    }

    public void setConfig(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setOutput(File outputFile) {
        this.outputFile = outputFile;
    }

    @Deprecated
    public void setNamingStrategy(String namingStrategy) {
        DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyAntArgument();
    }

    public void setImplicitNamingStrategy(String implicitNamingStrategy) {
        this.implicitNamingStrategy = implicitNamingStrategy;
    }

    public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
    }

    public void setHaltonerror(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }

    public void execute() throws BuildException {
        try {
            this.doExecution();
        }
        catch (BuildException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BuildException("Error performing export : " + e.getMessage(), (Throwable)e);
        }
    }

    @AllowSysOut
    private void doExecution() throws Exception {
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder(bsr);
        MetadataSources metadataSources = new MetadataSources(bsr);
        if (this.configurationFile != null) {
            ssrBuilder.configure(this.configurationFile);
        }
        if (this.propertiesFile != null) {
            ssrBuilder.loadProperties(this.propertiesFile);
        }
        ssrBuilder.applySettings(this.getProject().getProperties());
        for (String fileName : this.getFiles()) {
            if (fileName.endsWith(".jar")) {
                metadataSources.addJar(new File(fileName));
                continue;
            }
            metadataSources.addFile(fileName);
        }
        ssrBuilder.applySetting("hibernate.hbm2ddl.delimiter", this.delimiter);
        ExportType exportType = ExportType.interpret(this.drop, this.create);
        Target output = Target.interpret(!this.quiet, !this.text);
        if (output.doScript()) {
            ssrBuilder.applySetting("javax.persistence.schema-generation.scripts.action", (Object)exportType.getAction());
            Object scriptTarget = this.outputFile == null ? new OutputStreamWriter(System.out) : this.outputFile;
            if (exportType.doCreate()) {
                ssrBuilder.applySetting("javax.persistence.schema-generation.scripts.create-target", scriptTarget);
            }
            if (exportType.doDrop()) {
                ssrBuilder.applySetting("javax.persistence.schema-generation.scripts.drop-target", scriptTarget);
            }
        }
        if (output.doExport()) {
            ssrBuilder.applySetting("javax.persistence.schema-generation.database.action", (Object)exportType.getAction());
        }
        StandardServiceRegistryImpl ssr = (StandardServiceRegistryImpl)ssrBuilder.build();
        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder(ssr);
        ClassLoaderService classLoaderService = bsr.getService(ClassLoaderService.class);
        if (this.implicitNamingStrategy != null) {
            metadataBuilder.applyImplicitNamingStrategy((ImplicitNamingStrategy)classLoaderService.classForName(this.implicitNamingStrategy).newInstance());
        }
        if (this.physicalNamingStrategy != null) {
            metadataBuilder.applyPhysicalNamingStrategy((PhysicalNamingStrategy)classLoaderService.classForName(this.physicalNamingStrategy).newInstance());
        }
        MetadataImplementor metadata = (MetadataImplementor)metadataBuilder.build();
        metadata.validate();
        SchemaManagementToolCoordinator.process(metadata, ssr, ssr.getService(ConfigurationService.class).getSettings(), DelayedDropRegistryNotAvailableImpl.INSTANCE);
    }

    private String[] getFiles() {
        LinkedList<String> files = new LinkedList<String>();
        for (FileSet fileSet : this.fileSets) {
            String[] dsFiles;
            DirectoryScanner ds = fileSet.getDirectoryScanner(this.getProject());
            for (String dsFileName : dsFiles = ds.getIncludedFiles()) {
                File f = new File(dsFileName);
                if (!f.isFile()) {
                    f = new File(ds.getBasedir(), dsFileName);
                }
                files.add(f.getAbsolutePath());
            }
        }
        return ArrayHelper.toStringArray(files);
    }

    public static enum ExportType {
        CREATE(Action.CREATE_ONLY),
        DROP(Action.DROP),
        NONE(Action.NONE),
        BOTH(Action.CREATE);

        private final Action action;

        private ExportType(Action action) {
            this.action = action;
        }

        public boolean doCreate() {
            return this == BOTH || this == CREATE;
        }

        public boolean doDrop() {
            return this == BOTH || this == DROP;
        }

        public Action getAction() {
            return this.action;
        }

        public static ExportType interpret(boolean justDrop, boolean justCreate) {
            if (justDrop) {
                return DROP;
            }
            if (justCreate) {
                return CREATE;
            }
            return BOTH;
        }
    }
}

