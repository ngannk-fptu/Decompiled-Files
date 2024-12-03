/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.ArgumentProcessor;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.LoaderUtils;

public class ArgumentProcessorRegistry {
    private static final String DEBUG_ARGUMENT_PROCESSOR_REPOSITORY = "ant.argument-processor-repo.debug";
    private static final boolean DEBUG = "true".equals(System.getProperty("ant.argument-processor-repo.debug"));
    private static final String SERVICE_ID = "META-INF/services/org.apache.tools.ant.ArgumentProcessor";
    private static ArgumentProcessorRegistry instance = new ArgumentProcessorRegistry();
    private List<ArgumentProcessor> processors = new ArrayList<ArgumentProcessor>();

    public static ArgumentProcessorRegistry getInstance() {
        return instance;
    }

    private ArgumentProcessorRegistry() {
        this.collectArgumentProcessors();
    }

    public List<ArgumentProcessor> getProcessors() {
        return this.processors;
    }

    private void collectArgumentProcessors() {
        block5: {
            try {
                InputStream systemResource;
                ClassLoader classLoader = LoaderUtils.getContextClassLoader();
                if (classLoader != null) {
                    for (URL resource : Collections.list(classLoader.getResources(SERVICE_ID))) {
                        URLConnection conn = resource.openConnection();
                        conn.setUseCaches(false);
                        ArgumentProcessor processor = this.getProcessorByService(conn.getInputStream());
                        this.registerArgumentProcessor(processor);
                    }
                }
                if ((systemResource = ClassLoader.getSystemResourceAsStream(SERVICE_ID)) != null) {
                    ArgumentProcessor processor = this.getProcessorByService(systemResource);
                    this.registerArgumentProcessor(processor);
                }
            }
            catch (Exception e) {
                System.err.println("Unable to load ArgumentProcessor from service META-INF/services/org.apache.tools.ant.ArgumentProcessor (" + e.getClass().getName() + ": " + e.getMessage() + ")");
                if (!DEBUG) break block5;
                e.printStackTrace(System.err);
            }
        }
    }

    public void registerArgumentProcessor(String helperClassName) throws BuildException {
        this.registerArgumentProcessor(this.getProcessor(helperClassName));
    }

    public void registerArgumentProcessor(Class<? extends ArgumentProcessor> helperClass) throws BuildException {
        this.registerArgumentProcessor(this.getProcessor(helperClass));
    }

    private ArgumentProcessor getProcessor(String helperClassName) {
        try {
            Class<?> cl = Class.forName(helperClassName);
            return this.getProcessor(cl);
        }
        catch (ClassNotFoundException e) {
            throw new BuildException("Argument processor class " + helperClassName + " was not found", e);
        }
    }

    private ArgumentProcessor getProcessor(Class<? extends ArgumentProcessor> processorClass) {
        ArgumentProcessor processor;
        try {
            processor = processorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new BuildException("The argument processor class" + processorClass.getName() + " could not be instantiated with a default constructor", e);
        }
        return processor;
    }

    public void registerArgumentProcessor(ArgumentProcessor processor) {
        if (processor == null) {
            return;
        }
        this.processors.add(processor);
        if (DEBUG) {
            System.out.println("Argument processor " + processor.getClass().getName() + " registered.");
        }
    }

    private ArgumentProcessor getProcessorByService(InputStream is) throws IOException {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));){
            String processorClassName = rd.readLine();
            if (processorClassName != null && !processorClassName.isEmpty()) {
                ArgumentProcessor argumentProcessor = this.getProcessor(processorClassName);
                return argumentProcessor;
            }
        }
        return null;
    }
}

