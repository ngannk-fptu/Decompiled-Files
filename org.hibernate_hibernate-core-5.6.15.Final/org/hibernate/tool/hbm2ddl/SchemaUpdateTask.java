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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.hibernate.HibernateException;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.TargetTypeHelper;

public class SchemaUpdateTask
extends MatchingTask {
    private List<FileSet> fileSets = new LinkedList<FileSet>();
    private File propertiesFile;
    private File configurationFile;
    private File outputFile;
    private boolean quiet;
    private boolean text = true;
    private boolean haltOnError;
    private String delimiter;
    private String implicitNamingStrategy = null;
    private String physicalNamingStrategy = null;

    public void addFileset(FileSet fileSet) {
        this.fileSets.add(fileSet);
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

    public void setText(boolean text) {
        this.text = text;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void setNamingStrategy(String namingStrategy) {
        DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedNamingStrategyAntArgument();
    }

    public void setImplicitNamingStrategy(String implicitNamingStrategy) {
        this.implicitNamingStrategy = implicitNamingStrategy;
    }

    public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isHaltOnError() {
        return this.haltOnError;
    }

    public void setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void execute() throws BuildException {
        this.log("Running Hibernate Core SchemaUpdate.");
        this.log("This is an Ant task supporting only mapping files, if you want to use annotations see http://tools.hibernate.org.");
        try {
            StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder();
            this.configure(ssrBuilder);
            StandardServiceRegistry ssr = ssrBuilder.build();
            MetadataSources metadataSources = new MetadataSources(ssr);
            this.configure(metadataSources);
            MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
            this.configure(metadataBuilder, ssr);
            MetadataImplementor metadata = (MetadataImplementor)metadataBuilder.build();
            new SchemaUpdate().setOutputFile(this.outputFile.getPath()).setDelimiter(this.delimiter).setHaltOnError(this.haltOnError).execute(TargetTypeHelper.parseLegacyCommandLineOptions(!this.quiet, !this.text, this.outputFile.getPath()), metadata);
        }
        catch (HibernateException e) {
            throw new BuildException("Schema text failed: " + e.getMessage(), (Throwable)((Object)e));
        }
        catch (FileNotFoundException e) {
            throw new BuildException("File not found: " + e.getMessage(), (Throwable)e);
        }
        catch (IOException e) {
            throw new BuildException("IOException : " + e.getMessage(), (Throwable)e);
        }
        catch (BuildException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BuildException((Throwable)e);
        }
    }

    private void configure(StandardServiceRegistryBuilder registryBuilder) throws IOException {
        if (this.configurationFile != null) {
            registryBuilder.configure(this.configurationFile);
        }
        Properties properties = new Properties();
        if (this.propertiesFile == null) {
            properties.putAll((Map<?, ?>)this.getProject().getProperties());
        } else {
            try (FileInputStream fip = new FileInputStream(this.propertiesFile);){
                properties.load(fip);
            }
        }
        registryBuilder.applySettings(properties);
    }

    private void configure(MetadataSources metadataSources) {
        for (String filename : this.collectFiles()) {
            if (filename.endsWith(".jar")) {
                metadataSources.addJar(new File(filename));
                continue;
            }
            metadataSources.addFile(filename);
        }
    }

    private String[] collectFiles() {
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

    private void configure(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        if (this.implicitNamingStrategy != null) {
            try {
                metadataBuilder.applyImplicitNamingStrategy((ImplicitNamingStrategy)classLoaderService.classForName(this.implicitNamingStrategy).newInstance());
            }
            catch (Exception e) {
                throw new BuildException("Unable to instantiate specified ImplicitNamingStrategy [" + this.implicitNamingStrategy + "]", (Throwable)e);
            }
        }
        if (this.physicalNamingStrategy != null) {
            try {
                metadataBuilder.applyPhysicalNamingStrategy((PhysicalNamingStrategy)classLoaderService.classForName(this.physicalNamingStrategy).newInstance());
            }
            catch (Exception e) {
                throw new BuildException("Unable to instantiate specified PhysicalNamingStrategy [" + this.physicalNamingStrategy + "]", (Throwable)e);
            }
        }
    }
}

