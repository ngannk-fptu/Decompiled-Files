/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.el.ELException;
import javax.el.JreCompat;
import javax.el.Util;

public class ImportHandler {
    private static final Map<String, Set<String>> standardPackages = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> packageNames = new ConcurrentHashMap<String, Set<String>>();
    private Map<String, String> classNames = new ConcurrentHashMap<String, String>();
    private Map<String, Class<?>> clazzes = new ConcurrentHashMap();
    private Map<String, Class<?>> statics = new ConcurrentHashMap();

    public ImportHandler() {
        this.importPackage("java.lang");
    }

    public void importStatic(String name) throws ELException {
        int modifiers;
        int lastPeriod = name.lastIndexOf(46);
        if (lastPeriod < 0) {
            throw new ELException(Util.message(null, "importHandler.invalidStaticName", name));
        }
        String className = name.substring(0, lastPeriod);
        String fieldOrMethodName = name.substring(lastPeriod + 1);
        Class<?> clazz = this.findClass(className, true);
        if (clazz == null) {
            throw new ELException(Util.message(null, "importHandler.invalidClassNameForStatic", className, name));
        }
        boolean found = false;
        for (Field field : clazz.getFields()) {
            if (!field.getName().equals(fieldOrMethodName) || !Modifier.isStatic(modifiers = field.getModifiers()) || !Modifier.isPublic(modifiers)) continue;
            found = true;
            break;
        }
        if (!found) {
            for (AccessibleObject accessibleObject : clazz.getMethods()) {
                if (!((Method)accessibleObject).getName().equals(fieldOrMethodName) || !Modifier.isStatic(modifiers = ((Method)accessibleObject).getModifiers()) || !Modifier.isPublic(modifiers)) continue;
                found = true;
                break;
            }
        }
        if (!found) {
            throw new ELException(Util.message(null, "importHandler.staticNotFound", fieldOrMethodName, className, name));
        }
        Class<?> conflict = this.statics.get(fieldOrMethodName);
        if (conflict != null) {
            throw new ELException(Util.message(null, "importHandler.ambiguousStaticImport", name, conflict.getName() + '.' + fieldOrMethodName));
        }
        this.statics.put(fieldOrMethodName, clazz);
    }

    public void importClass(String name) throws ELException {
        int lastPeriodIndex = name.lastIndexOf(46);
        if (lastPeriodIndex < 0) {
            throw new ELException(Util.message(null, "importHandler.invalidClassName", name));
        }
        String unqualifiedName = name.substring(lastPeriodIndex + 1);
        String currentName = this.classNames.putIfAbsent(unqualifiedName, name);
        if (currentName != null && !currentName.equals(name)) {
            throw new ELException(Util.message(null, "importHandler.ambiguousImport", name, currentName));
        }
    }

    public void importPackage(String name) {
        Set<String> preloaded = standardPackages.get(name);
        if (preloaded == null) {
            this.packageNames.put(name, Collections.emptySet());
        } else {
            this.packageNames.put(name, preloaded);
        }
    }

    public Class<?> resolveClass(String name) {
        Class<?> clazz;
        if (name == null || name.contains(".")) {
            return null;
        }
        Class<?> result = this.clazzes.get(name);
        if (result != null) {
            if (NotFound.class.equals(result)) {
                return null;
            }
            return result;
        }
        String className = this.classNames.get(name);
        if (className != null && (clazz = this.findClass(className, true)) != null) {
            this.clazzes.put(name, clazz);
            return clazz;
        }
        for (Map.Entry<String, Set<String>> entry : this.packageNames.entrySet()) {
            Class<?> clazz2;
            if (!entry.getValue().isEmpty() && !entry.getValue().contains(name) || (clazz2 = this.findClass(className = entry.getKey() + '.' + name, false)) == null) continue;
            if (result != null) {
                throw new ELException(Util.message(null, "importHandler.ambiguousImport", className, result.getName()));
            }
            result = clazz2;
        }
        if (result == null) {
            this.clazzes.put(name, NotFound.class);
        } else {
            this.clazzes.put(name, result);
        }
        return result;
    }

    public Class<?> resolveStatic(String name) {
        return this.statics.get(name);
    }

    private Class<?> findClass(String name, boolean throwException) {
        Class<?> clazz;
        ClassLoader cl = Util.getContextClassLoader();
        try {
            clazz = cl.loadClass(name);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        JreCompat jreCompat = JreCompat.getInstance();
        int modifiers = clazz.getModifiers();
        if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !jreCompat.isExported(clazz)) {
            if (throwException) {
                throw new ELException(Util.message(null, "importHandler.invalidClass", name));
            }
            return null;
        }
        return clazz;
    }

    static {
        HashSet<String> servletClassNames = new HashSet<String>();
        servletClassNames.add("AsyncContext");
        servletClassNames.add("AsyncListener");
        servletClassNames.add("Filter");
        servletClassNames.add("FilterChain");
        servletClassNames.add("FilterConfig");
        servletClassNames.add("FilterRegistration");
        servletClassNames.add("FilterRegistration.Dynamic");
        servletClassNames.add("ReadListener");
        servletClassNames.add("Registration");
        servletClassNames.add("Registration.Dynamic");
        servletClassNames.add("RequestDispatcher");
        servletClassNames.add("Servlet");
        servletClassNames.add("ServletConfig");
        servletClassNames.add("ServletContainerInitializer");
        servletClassNames.add("ServletContext");
        servletClassNames.add("ServletContextAttributeListener");
        servletClassNames.add("ServletContextListener");
        servletClassNames.add("ServletRegistration");
        servletClassNames.add("ServletRegistration.Dynamic");
        servletClassNames.add("ServletRequest");
        servletClassNames.add("ServletRequestAttributeListener");
        servletClassNames.add("ServletRequestListener");
        servletClassNames.add("ServletResponse");
        servletClassNames.add("SessionCookieConfig");
        servletClassNames.add("SingleThreadModel");
        servletClassNames.add("WriteListener");
        servletClassNames.add("AsyncEvent");
        servletClassNames.add("GenericFilter");
        servletClassNames.add("GenericServlet");
        servletClassNames.add("HttpConstraintElement");
        servletClassNames.add("HttpMethodConstraintElement");
        servletClassNames.add("MultipartConfigElement");
        servletClassNames.add("ServletContextAttributeEvent");
        servletClassNames.add("ServletContextEvent");
        servletClassNames.add("ServletInputStream");
        servletClassNames.add("ServletOutputStream");
        servletClassNames.add("ServletRequestAttributeEvent");
        servletClassNames.add("ServletRequestEvent");
        servletClassNames.add("ServletRequestWrapper");
        servletClassNames.add("ServletResponseWrapper");
        servletClassNames.add("ServletSecurityElement");
        servletClassNames.add("DispatcherType");
        servletClassNames.add("SessionTrackingMode");
        servletClassNames.add("ServletException");
        servletClassNames.add("UnavailableException");
        standardPackages.put("javax.servlet", servletClassNames);
        HashSet<String> servletHttpClassNames = new HashSet<String>();
        servletHttpClassNames.add("HttpServletMapping");
        servletHttpClassNames.add("HttpServletRequest");
        servletHttpClassNames.add("HttpServletResponse");
        servletHttpClassNames.add("HttpSession");
        servletHttpClassNames.add("HttpSessionActivationListener");
        servletHttpClassNames.add("HttpSessionAttributeListener");
        servletHttpClassNames.add("HttpSessionBindingListener");
        servletHttpClassNames.add("HttpSessionContext");
        servletHttpClassNames.add("HttpSessionIdListener");
        servletHttpClassNames.add("HttpSessionListener");
        servletHttpClassNames.add("HttpUpgradeHandler");
        servletHttpClassNames.add("Part");
        servletHttpClassNames.add("PushBuilder");
        servletHttpClassNames.add("WebConnection");
        servletHttpClassNames.add("Cookie");
        servletHttpClassNames.add("HttpFilter");
        servletHttpClassNames.add("HttpServlet");
        servletHttpClassNames.add("HttpServletRequestWrapper");
        servletHttpClassNames.add("HttpServletResponseWrapper");
        servletHttpClassNames.add("HttpSessionBindingEvent");
        servletHttpClassNames.add("HttpSessionEvent");
        servletHttpClassNames.add("HttpUtils");
        servletHttpClassNames.add("MappingMatch");
        standardPackages.put("javax.servlet.http", servletHttpClassNames);
        HashSet<String> servletJspClassNames = new HashSet<String>();
        servletJspClassNames.add("HttpJspPage");
        servletJspClassNames.add("JspApplicationContext");
        servletJspClassNames.add("JspPage");
        servletJspClassNames.add("ErrorData");
        servletJspClassNames.add("JspContext");
        servletJspClassNames.add("JspEngineInfo");
        servletJspClassNames.add("JspFactory");
        servletJspClassNames.add("JspWriter");
        servletJspClassNames.add("PageContext");
        servletJspClassNames.add("Exceptions");
        servletJspClassNames.add("JspException");
        servletJspClassNames.add("JspTagException");
        servletJspClassNames.add("SkipPageException");
        standardPackages.put("javax.servlet.jsp", servletJspClassNames);
        HashSet<String> javaLangClassNames = new HashSet<String>();
        javaLangClassNames.add("Appendable");
        javaLangClassNames.add("AutoCloseable");
        javaLangClassNames.add("CharSequence");
        javaLangClassNames.add("Cloneable");
        javaLangClassNames.add("Comparable");
        javaLangClassNames.add("Iterable");
        javaLangClassNames.add("ProcessHandle");
        javaLangClassNames.add("ProcessHandle.Info");
        javaLangClassNames.add("Readable");
        javaLangClassNames.add("Runnable");
        javaLangClassNames.add("StackWalker.StackFrame");
        javaLangClassNames.add("System.Logger");
        javaLangClassNames.add("Thread.Builder");
        javaLangClassNames.add("Thread.Builder.OfPlatform");
        javaLangClassNames.add("Thread.Builder.OfVirtual");
        javaLangClassNames.add("Thread.UncaughtExceptionHandler");
        javaLangClassNames.add("Boolean");
        javaLangClassNames.add("Byte");
        javaLangClassNames.add("Character");
        javaLangClassNames.add("Character.Subset");
        javaLangClassNames.add("Character.UnicodeBlock");
        javaLangClassNames.add("Class");
        javaLangClassNames.add("ClassLoader");
        javaLangClassNames.add("ClassValue");
        javaLangClassNames.add("Compiler");
        javaLangClassNames.add("Double");
        javaLangClassNames.add("Enum");
        javaLangClassNames.add("Enum.EnumDesc");
        javaLangClassNames.add("Float");
        javaLangClassNames.add("InheritableThreadLocal");
        javaLangClassNames.add("Integer");
        javaLangClassNames.add("Long");
        javaLangClassNames.add("Math");
        javaLangClassNames.add("Module");
        javaLangClassNames.add("ModuleLayer");
        javaLangClassNames.add("ModuleLayer.Controller");
        javaLangClassNames.add("Number");
        javaLangClassNames.add("Object");
        javaLangClassNames.add("Package");
        javaLangClassNames.add("Process");
        javaLangClassNames.add("ProcessBuilder");
        javaLangClassNames.add("ProcessBuilder.Redirect");
        javaLangClassNames.add("Record");
        javaLangClassNames.add("Runtime");
        javaLangClassNames.add("Runtime.Version");
        javaLangClassNames.add("RuntimePermission");
        javaLangClassNames.add("ScopedValue");
        javaLangClassNames.add("ScopedValue.Carrier");
        javaLangClassNames.add("SecurityManager");
        javaLangClassNames.add("Short");
        javaLangClassNames.add("StackTraceElement");
        javaLangClassNames.add("StackWalker");
        javaLangClassNames.add("StrictMath");
        javaLangClassNames.add("String");
        javaLangClassNames.add("StringBuffer");
        javaLangClassNames.add("StringBuilder");
        javaLangClassNames.add("StringTemplate");
        javaLangClassNames.add("StringTemplate.Processor");
        javaLangClassNames.add("StringTemplate.Processor.Linkage");
        javaLangClassNames.add("System");
        javaLangClassNames.add("System.LoggerFinder");
        javaLangClassNames.add("Thread");
        javaLangClassNames.add("ThreadGroup");
        javaLangClassNames.add("ThreadLocal");
        javaLangClassNames.add("Throwable");
        javaLangClassNames.add("Void");
        javaLangClassNames.add("Character.UnicodeScript");
        javaLangClassNames.add("ProcessBuilder.Redirect.Type");
        javaLangClassNames.add("StackWalker.Option");
        javaLangClassNames.add("System.Logger.Level");
        javaLangClassNames.add("Thread.State");
        javaLangClassNames.add("ArithmeticException");
        javaLangClassNames.add("ArrayIndexOutOfBoundsException");
        javaLangClassNames.add("ArrayStoreException");
        javaLangClassNames.add("ClassCastException");
        javaLangClassNames.add("ClassNotFoundException");
        javaLangClassNames.add("CloneNotSupportedException");
        javaLangClassNames.add("EnumConstantNotPresentException");
        javaLangClassNames.add("Exception");
        javaLangClassNames.add("IllegalAccessException");
        javaLangClassNames.add("IllegalArgumentException");
        javaLangClassNames.add("IllegalCallerException");
        javaLangClassNames.add("IllegalMonitorStateException");
        javaLangClassNames.add("IllegalStateException");
        javaLangClassNames.add("IllegalThreadStateException");
        javaLangClassNames.add("IndexOutOfBoundsException");
        javaLangClassNames.add("InstantiationException");
        javaLangClassNames.add("InterruptedException");
        javaLangClassNames.add("LayerInstantiationException");
        javaLangClassNames.add("MatchException");
        javaLangClassNames.add("NegativeArraySizeException");
        javaLangClassNames.add("NoSuchFieldException");
        javaLangClassNames.add("NoSuchMethodException");
        javaLangClassNames.add("NullPointerException");
        javaLangClassNames.add("NumberFormatException");
        javaLangClassNames.add("ReflectiveOperationException");
        javaLangClassNames.add("RuntimeException");
        javaLangClassNames.add("SecurityException");
        javaLangClassNames.add("StringIndexOutOfBoundsException");
        javaLangClassNames.add("TypeNotPresentException");
        javaLangClassNames.add("UnsupportedOperationException");
        javaLangClassNames.add("WrongThreadException");
        javaLangClassNames.add("AbstractMethodError");
        javaLangClassNames.add("AssertionError");
        javaLangClassNames.add("BootstrapMethodError");
        javaLangClassNames.add("ClassCircularityError");
        javaLangClassNames.add("ClassFormatError");
        javaLangClassNames.add("Error");
        javaLangClassNames.add("ExceptionInInitializerError");
        javaLangClassNames.add("IllegalAccessError");
        javaLangClassNames.add("IncompatibleClassChangeError");
        javaLangClassNames.add("InstantiationError");
        javaLangClassNames.add("InternalError");
        javaLangClassNames.add("LinkageError");
        javaLangClassNames.add("NoClassDefFoundError");
        javaLangClassNames.add("NoSuchFieldError");
        javaLangClassNames.add("NoSuchMethodError");
        javaLangClassNames.add("OutOfMemoryError");
        javaLangClassNames.add("StackOverflowError");
        javaLangClassNames.add("ThreadDeath");
        javaLangClassNames.add("UnknownError");
        javaLangClassNames.add("UnsatisfiedLinkError");
        javaLangClassNames.add("UnsupportedClassVersionError");
        javaLangClassNames.add("VerifyError");
        javaLangClassNames.add("VirtualMachineError");
        javaLangClassNames.add("Deprecated");
        javaLangClassNames.add("FunctionalInterface");
        javaLangClassNames.add("Override");
        javaLangClassNames.add("SafeVarargs");
        javaLangClassNames.add("SuppressWarnings");
        standardPackages.put("java.lang", javaLangClassNames);
    }

    private static class NotFound {
        private NotFound() {
        }
    }
}

