/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.pool.sizeof;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.pool.sizeof.JvmInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AgentLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentLoader.class);
    private static final String INSTRUMENTATION_INSTANCE_SYSTEM_PROPERTY_NAME = "net.sf.ehcache.sizeof.agent.instrumentation";
    private static final String SIZEOF_AGENT_CLASSNAME = "net.sf.ehcache.pool.sizeof.SizeOfAgent";
    private static final String VIRTUAL_MACHINE_CLASSNAME = "com.sun.tools.attach.VirtualMachine";
    private static final Method VIRTUAL_MACHINE_ATTACH;
    private static final Method VIRTUAL_MACHINE_DETACH;
    private static final Method VIRTUAL_MACHINE_LOAD_AGENT;
    private static volatile Instrumentation instrumentation;

    AgentLoader() {
    }

    private static Class<?> getVirtualMachineClass() throws ClassNotFoundException {
        try {
            return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>(){

                @Override
                public Class<?> run() throws Exception {
                    try {
                        return ClassLoader.getSystemClassLoader().loadClass(AgentLoader.VIRTUAL_MACHINE_CLASSNAME);
                    }
                    catch (ClassNotFoundException cnfe) {
                        for (File jar : AgentLoader.getPossibleToolsJars()) {
                            try {
                                Class<?> vmClass = new URLClassLoader(new URL[]{jar.toURL()}).loadClass(AgentLoader.VIRTUAL_MACHINE_CLASSNAME);
                                LOGGER.info("Located valid 'tools.jar' at '{}'", (Object)jar);
                                return vmClass;
                            }
                            catch (Throwable t) {
                                LOGGER.info("Exception while loading tools.jar from '{}': {}", (Object)jar, (Object)t);
                            }
                        }
                        throw new ClassNotFoundException(AgentLoader.VIRTUAL_MACHINE_CLASSNAME);
                    }
                }
            });
        }
        catch (PrivilegedActionException pae) {
            Throwable actual = pae.getCause();
            if (actual instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)actual;
            }
            throw new AssertionError((Object)("Unexpected checked exception : " + actual));
        }
    }

    private static List<File> getPossibleToolsJars() {
        File jdkHome;
        File jdkSourced;
        ArrayList<File> jars = new ArrayList<File>();
        File javaHome = new File(System.getProperty("java.home"));
        File jreSourced = new File(javaHome, "lib/tools.jar");
        if (jreSourced.exists()) {
            jars.add(jreSourced);
        }
        if ("jre".equals(javaHome.getName()) && (jdkSourced = new File(jdkHome = new File(javaHome, "../"), "lib/tools.jar")).exists()) {
            jars.add(jdkSourced);
        }
        return jars;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static boolean loadAgent() {
        String string = AgentLoader.class.getName().intern();
        synchronized (string) {
            if (!AgentLoader.agentIsAvailable() && VIRTUAL_MACHINE_LOAD_AGENT != null) {
                try {
                    AgentLoader.warnIfOSX();
                    String name = ManagementFactory.getRuntimeMXBean().getName();
                    Object vm = VIRTUAL_MACHINE_ATTACH.invoke(null, name.substring(0, name.indexOf(64)));
                    try {
                        File agent = AgentLoader.getAgentFile();
                        LOGGER.info("Trying to load agent @ {}", (Object)agent);
                        if (agent != null) {
                            VIRTUAL_MACHINE_LOAD_AGENT.invoke(vm, agent.getAbsolutePath());
                        }
                    }
                    finally {
                        VIRTUAL_MACHINE_DETACH.invoke(vm, new Object[0]);
                    }
                }
                catch (InvocationTargetException ite) {
                    Throwable cause = ite.getCause();
                    LOGGER.info("Failed to attach to VM and load the agent: {}: {}", cause.getClass(), (Object)cause.getMessage());
                }
                catch (Throwable t) {
                    LOGGER.info("Failed to attach to VM and load the agent: {}: {}", t.getClass(), (Object)t.getMessage());
                }
            }
            return AgentLoader.agentIsAvailable();
        }
    }

    private static void warnIfOSX() {
        if (JvmInformation.isOSX() && System.getProperty("java.io.tmpdir") != null) {
            LOGGER.warn("Loading the SizeOfAgent will probably fail, as you are running on Apple OS X and have a value set for java.io.tmpdir\nThey both result in a bug, not yet fixed by Apple, that won't let us attach to the VM and load the agent.\nMost probably, you'll also get a full thread-dump after this because of the failure... Nothing to worry about!\nYou can bypass trying to load the Agent entirely by setting the System property 'net.sf.ehcache.pool.sizeof.AgentSizeOf.bypass'  to true");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static File getAgentFile() throws IOException, URISyntaxException {
        URL agent = AgentLoader.class.getResource("sizeof-agent.jar");
        if (agent == null) {
            return null;
        }
        if (agent.getProtocol().equals("file")) {
            return new File(agent.toURI());
        }
        File temp = File.createTempFile("ehcache-sizeof-agent", ".jar");
        try (FileOutputStream fout = new FileOutputStream(temp);
             InputStream in = agent.openStream();){
            int read;
            byte[] buffer = new byte[(int)MemoryUnit.KILOBYTES.toBytes(1L)];
            while ((read = in.read(buffer)) >= 0) {
                fout.write(buffer, 0, read);
            }
        }
        finally {
            temp.deleteOnExit();
        }
        LOGGER.info("Extracted agent jar to temporary file {}", (Object)temp);
        return temp;
    }

    static boolean agentIsAvailable() {
        try {
            if (instrumentation == null) {
                instrumentation = (Instrumentation)System.getProperties().get(INSTRUMENTATION_INSTANCE_SYSTEM_PROPERTY_NAME);
            }
            if (instrumentation == null) {
                Class<?> sizeOfAgentClass = ClassLoader.getSystemClassLoader().loadClass(SIZEOF_AGENT_CLASSNAME);
                Method getInstrumentationMethod = sizeOfAgentClass.getMethod("getInstrumentation", new Class[0]);
                instrumentation = (Instrumentation)getInstrumentationMethod.invoke(sizeOfAgentClass, new Object[0]);
            }
            return instrumentation != null;
        }
        catch (SecurityException e) {
            LOGGER.warn("Couldn't access the system classloader because of the security policies applied by the security manager. You either want to loosen these, so ClassLoader.getSystemClassLoader() and reflection API calls are permitted or the sizing will be done using some other mechanism.\nAlternatively, set the system property net.sf.ehcache.sizeof.agent.instrumentationSystemProperty to true to have the agent put the required instances in the System Properties for the loader to access.");
        }
        finally {
            return false;
        }
    }

    static long agentSizeOf(Object obj) {
        if (instrumentation == null) {
            throw new UnsupportedOperationException("Sizeof agent is not available");
        }
        return instrumentation.getObjectSize(obj);
    }

    static {
        Method attach = null;
        Method detach = null;
        Method loadAgent = null;
        try {
            Class<?> virtualMachineClass = AgentLoader.getVirtualMachineClass();
            attach = virtualMachineClass.getMethod("attach", String.class);
            detach = virtualMachineClass.getMethod("detach", new Class[0]);
            loadAgent = virtualMachineClass.getMethod("loadAgent", String.class);
        }
        catch (Throwable e) {
            LOGGER.info("Unavailable or unrecognised attach API : {}", (Object)e.toString());
        }
        VIRTUAL_MACHINE_ATTACH = attach;
        VIRTUAL_MACHINE_DETACH = detach;
        VIRTUAL_MACHINE_LOAD_AGENT = loadAgent;
    }
}

