/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.osgi.impl.OSGiScriptEngine;
import com.hazelcast.osgi.impl.OSGiScriptEngineFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class OSGiScriptEngineManager
extends ScriptEngineManager {
    private static final String RHINO_SCRIPT_ENGINE_FACTORY = "com.sun.script.javascript.RhinoScriptEngineFactory";
    private static final String NASHORN_SCRIPT_ENGINE_FACTORY = "jdk.nashorn.api.scripting.NashornScriptEngineFactory";
    private final ILogger logger = Logger.getLogger(this.getClass());
    private Bindings bindings;
    private List<ScriptEngineManagerInfo> scriptEngineManagerInfoList;
    private BundleContext context;

    public OSGiScriptEngineManager(BundleContext context) {
        this.context = context;
        this.bindings = new SimpleBindings();
        this.scriptEngineManagerInfoList = this.findManagers(context);
    }

    public void reloadManagers() {
        this.scriptEngineManagerInfoList = this.findManagers(this.context);
    }

    @Override
    public Object get(String key) {
        return this.bindings.get(key);
    }

    @Override
    public Bindings getBindings() {
        return this.bindings;
    }

    @Override
    public void setBindings(Bindings bindings) {
        this.bindings = bindings;
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            info.scriptEngineManager.setBindings(bindings);
        }
    }

    @Override
    public ScriptEngine getEngineByExtension(String extension) {
        ScriptEngine engine = null;
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            Thread currentThread = Thread.currentThread();
            ClassLoader old = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(info.classloader);
            engine = info.scriptEngineManager.getEngineByExtension(extension);
            currentThread.setContextClassLoader(old);
            if (engine == null) continue;
            break;
        }
        return engine;
    }

    @Override
    public ScriptEngine getEngineByMimeType(String mimeType) {
        ScriptEngine engine = null;
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            Thread currentThread = Thread.currentThread();
            ClassLoader old = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(info.classloader);
            engine = info.scriptEngineManager.getEngineByMimeType(mimeType);
            currentThread.setContextClassLoader(old);
            if (engine == null) continue;
            break;
        }
        return engine;
    }

    @Override
    public ScriptEngine getEngineByName(String shortName) {
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            Thread currentThread = Thread.currentThread();
            ClassLoader old = currentThread.getContextClassLoader();
            ClassLoader contextClassLoader = info.classloader;
            currentThread.setContextClassLoader(contextClassLoader);
            ScriptEngine engine = info.scriptEngineManager.getEngineByName(shortName);
            currentThread.setContextClassLoader(old);
            if (engine == null) continue;
            OSGiScriptEngineFactory factory = new OSGiScriptEngineFactory(engine.getFactory(), contextClassLoader);
            return new OSGiScriptEngine(engine, factory);
        }
        return null;
    }

    @Override
    public List<ScriptEngineFactory> getEngineFactories() {
        ArrayList<ScriptEngineFactory> osgiFactories = new ArrayList<ScriptEngineFactory>();
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            for (ScriptEngineFactory factory : info.scriptEngineManager.getEngineFactories()) {
                OSGiScriptEngineFactory scriptEngineFactory = new OSGiScriptEngineFactory(factory, info.classloader);
                osgiFactories.add(scriptEngineFactory);
            }
        }
        return osgiFactories;
    }

    @Override
    public void put(String key, Object value) {
        this.bindings.put(key, value);
    }

    @Override
    public void registerEngineExtension(String extension, ScriptEngineFactory factory) {
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            info.scriptEngineManager.registerEngineExtension(extension, factory);
        }
    }

    @Override
    public void registerEngineMimeType(String type, ScriptEngineFactory factory) {
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            info.scriptEngineManager.registerEngineMimeType(type, factory);
        }
    }

    @Override
    public void registerEngineName(String name, ScriptEngineFactory factory) {
        for (ScriptEngineManagerInfo info : this.scriptEngineManagerInfoList) {
            info.scriptEngineManager.registerEngineName(name, factory);
        }
    }

    private List<ScriptEngineManagerInfo> findManagers(BundleContext context) {
        ArrayList<ScriptEngineManagerInfo> scriptEngineManagerInfos = new ArrayList<ScriptEngineManagerInfo>();
        try {
            for (String factoryName : this.findFactoryCandidates(context)) {
                ScriptEngineManagerInfo scriptEngineManagerInfo;
                ClassLoader factoryClassLoader = this.loadScriptEngineFactoryClassLoader(factoryName);
                if (factoryClassLoader == null || (scriptEngineManagerInfo = this.createScriptEngineManagerInfo(factoryName, factoryClassLoader)) == null) continue;
                scriptEngineManagerInfos.add(scriptEngineManagerInfo);
            }
            return scriptEngineManagerInfos;
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private ClassLoader loadScriptEngineFactoryClassLoader(String factoryName) {
        try {
            return ClassLoaderUtil.tryLoadClass(factoryName).getClassLoader();
        }
        catch (ClassNotFoundException cnfe) {
            this.logger.warning("Found ScriptEngineFactory candidate for " + factoryName + ", but cannot load class! -> " + cnfe);
            if (this.logger.isFinestEnabled()) {
                this.logger.finest(cnfe);
            }
            return null;
        }
    }

    private ScriptEngineManagerInfo createScriptEngineManagerInfo(String factoryName, ClassLoader factoryLoader) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager(factoryLoader);
            manager.setBindings(this.bindings);
            return new ScriptEngineManagerInfo(manager, factoryLoader);
        }
        catch (Exception e) {
            this.logger.warning("Found ScriptEngineFactory candidate for " + factoryName + ", but could not load ScripEngineManager! -> " + e);
            if (this.logger.isFinestEnabled()) {
                this.logger.finest(e);
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<String> findFactoryCandidates(BundleContext context) throws IOException {
        Bundle[] bundles = context.getBundles();
        ArrayList<String> factoryCandidates = new ArrayList<String>();
        for (Bundle bundle : bundles) {
            Enumeration urls;
            if (bundle == null || "system.bundle".equals(bundle.getSymbolicName()) || (urls = bundle.findEntries("META-INF/services", "javax.script.ScriptEngineFactory", false)) == null) continue;
            while (urls.hasMoreElements()) {
                URL u = (URL)urls.nextElement();
                BufferedReader reader = null;
                try {
                    String line;
                    reader = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        if ((line = line.trim()).startsWith("#") || line.length() <= 0) continue;
                        factoryCandidates.add(line);
                    }
                }
                catch (Throwable throwable) {
                    IOUtil.closeResource(reader);
                    throw throwable;
                }
                IOUtil.closeResource(reader);
            }
        }
        this.addJavaScriptEngine(factoryCandidates);
        return factoryCandidates;
    }

    private void addJavaScriptEngine(List<String> factoryCandidates) {
        factoryCandidates.add(OSGiScriptEngineFactory.class.getName());
        if (ClassLoaderUtil.isClassDefined(RHINO_SCRIPT_ENGINE_FACTORY)) {
            factoryCandidates.add(RHINO_SCRIPT_ENGINE_FACTORY);
        } else if (ClassLoaderUtil.isClassDefined(NASHORN_SCRIPT_ENGINE_FACTORY)) {
            factoryCandidates.add(NASHORN_SCRIPT_ENGINE_FACTORY);
        } else {
            this.logger.warning("No built-in JavaScript ScriptEngineFactory found.");
        }
    }

    public String printScriptEngines() {
        StringBuilder msg = new StringBuilder("Available script engines are:\n");
        for (ScriptEngineFactory scriptEngineFactory : this.getEngineFactories()) {
            msg.append("\t- ").append(scriptEngineFactory.getEngineName()).append('\n');
        }
        return msg.toString();
    }

    private static final class ScriptEngineManagerInfo {
        private final ScriptEngineManager scriptEngineManager;
        private final ClassLoader classloader;

        private ScriptEngineManagerInfo(ScriptEngineManager scriptEngineManager, ClassLoader classloader) {
            this.scriptEngineManager = scriptEngineManager;
            this.classloader = classloader;
        }
    }
}

