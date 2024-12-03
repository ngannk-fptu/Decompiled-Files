/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.bind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Messages;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class ModuleUtil {
    private static Logger logger = Logger.getLogger("javax.xml.bind");
    static final String DEFAULT_FACTORY_CLASS = "com.sun.xml.bind.v2.ContextFactory".toString();

    ModuleUtil() {
    }

    static Class[] getClassesFromContextPath(String contextPath, ClassLoader classLoader) throws JAXBException {
        String[] tokens;
        ArrayList<Class> classes = new ArrayList<Class>();
        if (contextPath == null || contextPath.isEmpty()) {
            return classes.toArray(new Class[0]);
        }
        for (String pkg : tokens = contextPath.split(":")) {
            try {
                Class<?> o = classLoader.loadClass(pkg + ".ObjectFactory");
                classes.add(o);
            }
            catch (ClassNotFoundException classNotFoundException) {
                try {
                    Class firstByJaxbIndex = ModuleUtil.findFirstByJaxbIndex(pkg, classLoader);
                    if (firstByJaxbIndex == null) continue;
                    classes.add(firstByJaxbIndex);
                }
                catch (IOException e) {
                    throw new JAXBException(e);
                }
            }
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Resolved classes from context path: {0}", classes);
        }
        return classes.toArray(new Class[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static Class findFirstByJaxbIndex(String pkg, ClassLoader classLoader) throws IOException, JAXBException {
        String resource = pkg.replace('.', '/') + "/jaxb.index";
        InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
        if (resourceAsStream == null) {
            return null;
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));){
            String className = in.readLine();
            while (className != null) {
                if ((className = className.trim()).startsWith("#") || className.length() == 0) {
                    className = in.readLine();
                    continue;
                }
                try {
                    Class<?> clazz = classLoader.loadClass(pkg + "." + className);
                    return clazz;
                }
                catch (ClassNotFoundException e) {
                    try {
                        throw new JAXBException(Messages.format("ContextFinder.ErrorLoadClass", className, pkg), e);
                    }
                    catch (Throwable throwable) {
                        throw throwable;
                        return null;
                    }
                }
            }
        }
    }

    public static void delegateAddOpensToImplModule(Class[] classes, Class<?> factorySPI) throws JAXBException {
        Module implModule = factorySPI.getModule();
        Module jaxbModule = JAXBContext.class.getModule();
        for (Class<?> clazz : classes) {
            Class<?> jaxbClass = clazz.isArray() ? clazz.getComponentType() : clazz;
            Module classModule = jaxbClass.getModule();
            String packageName = jaxbClass.getPackageName();
            if (!classModule.isNamed() || classModule.getName().equals("java.base")) continue;
            if (!classModule.isOpen(packageName, jaxbModule)) {
                throw new JAXBException(Messages.format("JAXBClasses.notOpen", packageName, jaxbClass.getName(), classModule.getName()));
            }
            classModule.addOpens(packageName, implModule);
            if (!logger.isLoggable(Level.FINE)) continue;
            logger.log(Level.FINE, "Propagating openness of package {0} in {1} to {2}.", new String[]{packageName, classModule.getName(), implModule.getName()});
        }
    }
}

