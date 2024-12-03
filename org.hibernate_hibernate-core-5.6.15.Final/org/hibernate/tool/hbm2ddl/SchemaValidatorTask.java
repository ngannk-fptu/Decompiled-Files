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
import java.util.ArrayList;
import java.util.Iterator;
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
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.tool.hbm2ddl.SchemaValidator;

public class SchemaValidatorTask
extends MatchingTask {
    private List<FileSet> fileSets = new LinkedList<FileSet>();
    private File propertiesFile;
    private File configurationFile;
    private String implicitNamingStrategy = null;
    private String physicalNamingStrategy = null;

    public void addFileset(FileSet fileSet) {
        this.fileSets.add(fileSet);
    }

    public void setProperties(File propertiesFile) {
        if (!propertiesFile.exists()) {
            throw new BuildException("Properties file [" + propertiesFile + "] does not exist.");
        }
        this.log("Using properties file " + propertiesFile, 4);
        this.propertiesFile = propertiesFile;
    }

    public void setConfig(File configurationFile) {
        if (!configurationFile.exists()) {
            throw new BuildException("Configuration file [" + configurationFile + "] does not exist.");
        }
        this.log("Using configuration file " + this.propertiesFile, 4);
        this.configurationFile = configurationFile;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() throws BuildException {
        try {
            StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder();
            this.configure(ssrBuilder);
            StandardServiceRegistry ssr = ssrBuilder.build();
            try {
                MetadataSources metadataSources = new MetadataSources(ssrBuilder.build());
                this.configure(metadataSources);
                MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
                this.configure(metadataBuilder, ssr);
                MetadataImplementor metadata = (MetadataImplementor)metadataBuilder.build();
                new SchemaValidator().validate(metadata, ssr);
            }
            finally {
                StandardServiceRegistryBuilder.destroy(ssr);
            }
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
            try (FileInputStream fis = new FileInputStream(this.propertiesFile);){
                properties.load(fis);
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
        ArrayList<String> files = new ArrayList<String>();
        Iterator<FileSet> iterator = this.fileSets.iterator();
        while (iterator.hasNext()) {
            FileSet fileSet;
            FileSet fs = fileSet = iterator.next();
            DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            for (String dsFile : ds.getIncludedFiles()) {
                File f = new File(dsFile);
                if (!f.isFile()) {
                    f = new File(ds.getBasedir(), dsFile);
                }
                files.add(f.getAbsolutePath());
            }
        }
        return ArrayHelper.toStringArray(files);
    }

    private void configure(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
        StrategySelector strategySelector = serviceRegistry.getService(StrategySelector.class);
        if (this.implicitNamingStrategy != null) {
            metadataBuilder.applyImplicitNamingStrategy(strategySelector.resolveStrategy(ImplicitNamingStrategy.class, this.implicitNamingStrategy));
        }
        if (this.physicalNamingStrategy != null) {
            metadataBuilder.applyPhysicalNamingStrategy(strategySelector.resolveStrategy(PhysicalNamingStrategy.class, this.physicalNamingStrategy));
        }
    }
}

