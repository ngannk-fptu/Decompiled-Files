/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.metadataparser.ClassUtility;
import org.apache.felix.bundlerepository.metadataparser.ReplaceUtility;
import org.apache.felix.bundlerepository.metadataparser.kxmlsax.KXml2SAXHandler;

public class XmlCommonHandler
implements KXml2SAXHandler {
    private static final String PI_MAPPING = "mapping";
    public static final String METADATAPARSER_PIS = "METADATAPARSER_PIS";
    public static final String METADATAPARSER_TYPES = "METADATAPARSER_TYPES";
    private int m_columnNumber;
    private int m_lineNumber;
    private boolean m_traceFlag = false;
    private static String VALUE = "value";
    private XmlStackElement m_root;
    private Stack m_elementStack;
    private Map m_pis;
    private boolean m_missingPIExceptionFlag;
    private Map m_types;
    private TypeEntry m_defaultType;
    private StringBuffer m_currentText;
    private Map m_context;
    private final Logger m_logger;
    static /* synthetic */ Class class$java$util$Map;
    static /* synthetic */ Class class$java$lang$String;

    public XmlCommonHandler(Logger logger) {
        this.m_logger = logger;
        this.m_elementStack = new Stack();
        this.m_pis = new HashMap();
        this.m_missingPIExceptionFlag = false;
        this.m_types = new HashMap();
        this.m_context = new HashMap();
        this.m_context.put(METADATAPARSER_PIS, this.m_pis);
        this.m_context.put(METADATAPARSER_TYPES, this.m_types);
    }

    public void addPI(String piname, Class clazz) {
        this.m_pis.put(piname, clazz);
    }

    public void setMissingPIExceptionFlag(boolean flag) {
        this.m_missingPIExceptionFlag = flag;
    }

    public void addType(String qname, Object instanceFactory, Class castClass, Method defaultAddMethod) throws Exception {
        TypeEntry typeEntry;
        try {
            typeEntry = new TypeEntry(instanceFactory, castClass, defaultAddMethod);
        }
        catch (Exception e) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + qname + " : " + e.getMessage());
        }
        this.m_types.put(qname, typeEntry);
        this.trace("element " + qname + " : " + typeEntry.toString());
    }

    public void setDefaultType(Object instanceFactory, Class castClass, Method defaultAddMethod) throws Exception {
        TypeEntry typeEntry;
        try {
            typeEntry = new TypeEntry(instanceFactory, castClass, defaultAddMethod);
        }
        catch (Exception e) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ": default element : " + e.getMessage());
        }
        this.m_defaultType = typeEntry;
        this.trace("default element  : " + typeEntry.toString());
    }

    public void setContext(Map context) {
        this.m_context = context;
    }

    public Map getContext() {
        return this.m_context;
    }

    public Object getRoot() {
        return this.m_root.m_object;
    }

    public void characters(char[] ch, int offset, int length) throws Exception {
        if (this.m_currentText != null) {
            this.m_currentText.append(ch, offset, length);
        }
    }

    private String adderOf(Class clazz) {
        return "add" + ClassUtility.capitalize(ClassUtility.classOf(clazz.getName()));
    }

    private String adderOf(String key) {
        return "add" + ClassUtility.capitalize(key);
    }

    private String setterOf(Class clazz) {
        return "set" + ClassUtility.capitalize(ClassUtility.classOf(clazz.getName()));
    }

    private String setterOf(String key) {
        return "set" + ClassUtility.capitalize(key);
    }

    private void setObjectContext(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = null;
        try {
            method = object.getClass().getDeclaredMethod("setContext", class$java$util$Map == null ? (class$java$util$Map = XmlCommonHandler.class$("java.util.Map")) : class$java$util$Map);
        }
        catch (NoSuchMethodException e) {
            // empty catch block
        }
        if (method != null) {
            this.trace(method.getName());
            try {
                method.invoke(object, this.m_context);
            }
            catch (InvocationTargetException e) {
                this.m_logger.log(1, "Error parsing repository metadata", e.getTargetException());
                throw e;
            }
        }
    }

    private void invokeProcess(Object object) throws Throwable {
        Method method = null;
        try {
            method = object.getClass().getDeclaredMethod("process", null);
        }
        catch (NoSuchMethodException e) {
            // empty catch block
        }
        if (method != null) {
            this.trace(method.getName());
            try {
                method.invoke(object, null);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    private void setObjectParent(Object object, Object parent) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        Method method = null;
        try {
            method = object.getClass().getDeclaredMethod("setParent", parent.getClass());
        }
        catch (NoSuchMethodException e) {
            // empty catch block
        }
        if (method != null) {
            this.trace(method.getName());
            try {
                method.invoke(object, parent);
            }
            catch (InvocationTargetException e) {
                this.m_logger.log(1, "Error parsing repository metadata", e.getTargetException());
                throw e;
            }
        }
    }

    public void startElement(String uri, String localName, String qName, Properties attrib) throws Exception {
        this.trace("START (" + this.m_lineNumber + "," + this.m_columnNumber + "):" + uri + ":" + qName);
        TypeEntry type = (TypeEntry)this.m_types.get(qName);
        if (type == null) {
            type = this.m_defaultType;
        }
        Object obj = null;
        if (type != null) {
            try {
                type.m_newInstanceMethod.setAccessible(true);
                obj = type.m_newInstanceMethod.invoke(type.m_instanceFactory, null);
            }
            catch (InvocationTargetException e) {
                this.m_logger.log(1, "Error parsing repository metadata", e.getTargetException());
            }
            if (!this.m_elementStack.isEmpty()) {
                XmlStackElement parent = (XmlStackElement)this.m_elementStack.peek();
                this.setObjectParent(obj, parent.m_object);
            }
            this.setObjectContext(obj);
            Set<Object> keyset = attrib.keySet();
            Iterator<Object> iter = keyset.iterator();
            while (iter.hasNext()) {
                String key = (String)iter.next();
                String value = ReplaceUtility.replace((String)attrib.get(key), this.m_context);
                Method method = null;
                if (!(obj instanceof String)) {
                    try {
                        method = type.m_instanceClass.getDeclaredMethod(this.setterOf(key), class$java$lang$String == null ? XmlCommonHandler.class$("java.lang.String") : class$java$lang$String);
                    }
                    catch (NoSuchMethodException e) {
                        // empty catch block
                    }
                    if (method == null) {
                        try {
                            method = type.m_instanceClass.getDeclaredMethod(this.adderOf(key), class$java$lang$String == null ? XmlCommonHandler.class$("java.lang.String") : class$java$lang$String);
                        }
                        catch (NoSuchMethodException e) {
                            // empty catch block
                        }
                    }
                }
                if (method != null) {
                    this.trace(method.getName());
                    try {
                        method.invoke(obj, (Object[])new String[]{value});
                        continue;
                    }
                    catch (InvocationTargetException e) {
                        this.m_logger.log(1, "Error parsing repository metadata", e.getTargetException());
                        throw e;
                    }
                }
                if (obj instanceof String) {
                    if (key.equals(VALUE)) {
                        obj = value;
                        continue;
                    }
                    throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + "String element " + qName + " cannot have other attribute than value");
                }
                if (type.m_defaultAddMethod != null) {
                    Class<?>[] parameterTypes = type.m_defaultAddMethod.getParameterTypes();
                    if (parameterTypes.length == 2 && parameterTypes[0].isAssignableFrom(class$java$lang$String == null ? XmlCommonHandler.class$("java.lang.String") : class$java$lang$String) && parameterTypes[1].isAssignableFrom(class$java$lang$String == null ? XmlCommonHandler.class$("java.lang.String") : class$java$lang$String)) {
                        type.m_defaultAddMethod.invoke(obj, (Object[])new String[]{key, value});
                        continue;
                    }
                    if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(class$java$lang$String == null ? XmlCommonHandler.class$("java.lang.String") : class$java$lang$String)) {
                        type.m_defaultAddMethod.invoke(obj, (Object[])new String[]{value});
                        continue;
                    }
                    throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + "class " + type.m_instanceFactory.getClass().getName() + " for element " + qName + " does not support the attribute " + key);
                }
                throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + "class " + type.m_instanceFactory.getClass().getName() + " for element " + qName + " does not support the attribute " + key);
            }
        } else {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + "this element " + qName + " has not corresponding class");
        }
        XmlStackElement element = new XmlStackElement(qName, obj);
        if (this.m_root == null) {
            this.m_root = element;
        }
        this.m_elementStack.push(element);
        this.m_currentText = new StringBuffer();
        this.trace("START/ (" + this.m_lineNumber + "," + this.m_columnNumber + "):" + uri + ":" + qName);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void endElement(String uri, String localName, String qName) throws Exception {
        this.trace("END (" + this.m_lineNumber + "," + this.m_columnNumber + "):" + uri + ":" + qName);
        XmlStackElement element = (XmlStackElement)this.m_elementStack.pop();
        TypeEntry elementType = (TypeEntry)this.m_types.get(element.m_qname);
        if (elementType == null) {
            elementType = this.m_defaultType;
        }
        if (this.m_currentText != null && this.m_currentText.length() != 0) {
            String currentStr = ReplaceUtility.replace(this.m_currentText.toString(), this.m_context).trim();
            this.trace("current text:" + currentStr);
            Method method = null;
            try {
                method = elementType.m_castClass.getDeclaredMethod("addText", class$java$lang$String == null ? (class$java$lang$String = XmlCommonHandler.class$("java.lang.String")) : class$java$lang$String);
            }
            catch (NoSuchMethodException e) {
                try {
                    method = elementType.m_castClass.getDeclaredMethod("setText", class$java$lang$String == null ? (class$java$lang$String = XmlCommonHandler.class$("java.lang.String")) : class$java$lang$String);
                }
                catch (NoSuchMethodException e2) {
                    // empty catch block
                }
            }
            if (method != null) {
                this.trace(method.getName());
                try {
                    method.invoke(element.m_object, (Object[])new String[]{currentStr});
                }
                catch (InvocationTargetException e) {
                    this.m_logger.log(1, "Error parsing repository metadata", e.getTargetException());
                    throw e;
                }
            } else if ((class$java$lang$String == null ? (class$java$lang$String = XmlCommonHandler.class$("java.lang.String")) : class$java$lang$String).isAssignableFrom(elementType.m_castClass)) {
                String str = (String)element.m_object;
                if (str.length() != 0) {
                    throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + "String element " + qName + " cannot have both PCDATA and an attribute value");
                }
                element.m_object = currentStr;
            }
        }
        this.m_currentText = null;
        if (!this.m_elementStack.isEmpty()) {
            XmlStackElement parent = (XmlStackElement)this.m_elementStack.peek();
            TypeEntry parentType = (TypeEntry)this.m_types.get(parent.m_qname);
            if (parentType == null) {
                parentType = this.m_defaultType;
            }
            String capqName = ClassUtility.capitalize(qName);
            Method method = null;
            try {
                method = parentType.m_instanceClass.getDeclaredMethod(this.adderOf(capqName), elementType.m_castClass);
            }
            catch (NoSuchMethodException e) {
                this.trace("NoSuchMethodException: " + this.adderOf(capqName) + "(" + elementType.m_castClass.getName() + ")");
            }
            if (method == null) {
                try {
                    method = parentType.m_instanceClass.getDeclaredMethod(this.setterOf(capqName), elementType.m_castClass);
                }
                catch (NoSuchMethodException e) {
                    this.trace("NoSuchMethodException: " + this.setterOf(capqName) + "(" + elementType.m_castClass.getName() + ")");
                }
            }
            if (method != null) {
                this.trace(method.getName());
                try {
                    method.setAccessible(true);
                    method.invoke(parent.m_object, element.m_object);
                }
                catch (InvocationTargetException e) {
                    this.m_logger.log(1, "Error parsing repository metadata", e.getTargetException());
                    throw e;
                }
            } else {
                if (parentType.m_defaultAddMethod == null) throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " element " + parent.m_qname + " cannot have an attribute " + qName + " of type " + elementType.m_castClass);
                Class<?>[] parameterTypes = parentType.m_defaultAddMethod.getParameterTypes();
                if (parameterTypes.length == 2 && parameterTypes[0].isAssignableFrom(class$java$lang$String == null ? (class$java$lang$String = XmlCommonHandler.class$("java.lang.String")) : class$java$lang$String) && parameterTypes[1].isAssignableFrom(elementType.m_castClass)) {
                    parentType.m_defaultAddMethod.invoke(parent.m_object, qName, element.m_object);
                } else {
                    if (parameterTypes.length != 1 || !parameterTypes[0].isAssignableFrom(elementType.m_castClass)) throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " element " + parent.m_qname + " cannot have an attribute " + qName + " of type " + elementType.m_castClass);
                    parentType.m_defaultAddMethod.invoke(parent.m_object, element.m_object);
                }
            }
        }
        try {
            this.invokeProcess(element);
        }
        catch (Throwable e) {
            this.m_logger.log(1, "Error parsing repository metadata", e);
            throw new Exception(e);
        }
        this.trace("END/ (" + this.m_lineNumber + "," + this.m_columnNumber + "):" + uri + ":" + qName);
    }

    public void setTrace(boolean trace) {
        this.m_traceFlag = trace;
    }

    private void trace(String msg) {
        if (this.m_traceFlag) {
            this.m_logger.log(4, msg);
        }
    }

    public void setLineNumber(int lineNumber) {
        this.m_lineNumber = lineNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.m_columnNumber = columnNumber;
    }

    public void processingInstruction(String target, String data) throws Exception {
        this.trace("PI:" + target + ";" + data);
        this.trace("ignore PI : " + data);
    }

    public void processingInstructionForMapping(String target, String data) throws Exception {
        if (target == null ? !data.startsWith(PI_MAPPING) : !target.equals(PI_MAPPING)) {
            return;
        }
        String datt = "defaultclass=\"";
        int dstart = data.indexOf(datt);
        if (dstart != -1) {
            int dend = data.indexOf("\"", dstart + datt.length());
            if (dend == -1) {
                throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " \"defaultclass\" attribute in \"mapping\" PI is not quoted");
            }
            String classname = data.substring(dstart + datt.length(), dend);
            Class<?> clazz = null;
            try {
                clazz = this.getClass().getClassLoader().loadClass(classname);
            }
            catch (ClassNotFoundException e) {
                throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " cannot found class " + classname + " for \"mapping\" PI");
            }
            Method defaultdefaultAddMethod = null;
            this.setDefaultType(clazz, null, defaultdefaultAddMethod);
            return;
        }
        String eatt = "element=\"";
        int estart = data.indexOf(eatt);
        if (estart == -1) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " missing \"element\" attribute in \"mapping\" PI");
        }
        int eend = data.indexOf("\"", estart + eatt.length());
        if (eend == -1) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " \"element\" attribute in \"mapping\" PI is not quoted");
        }
        String element = data.substring(estart + eatt.length(), eend);
        String catt = "class=\"";
        int cstart = data.indexOf(catt);
        if (cstart == -1) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " missing \"class\" attribute in \"mapping\" PI");
        }
        int cend = data.indexOf("\"", cstart + catt.length());
        if (cend == -1) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " \"class\" attribute in \"mapping\" PI is not quoted");
        }
        String classname = data.substring(cstart + catt.length(), cend);
        String castname = null;
        String castatt = "cast=\"";
        int caststart = data.indexOf(castatt);
        if (caststart != -1) {
            int castend = data.indexOf("\"", cstart + castatt.length());
            if (castend == -1) {
                throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " \"cast\" attribute in \"mapping\" PI is not quoted");
            }
            castname = data.substring(caststart + castatt.length(), castend);
        }
        Class<?> clazz = null;
        try {
            clazz = this.getClass().getClassLoader().loadClass(classname);
        }
        catch (ClassNotFoundException e) {
            throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " cannot found class " + classname + " for \"mapping\" PI");
        }
        Class castClazz = null;
        if (castname != null) {
            try {
                clazz = this.getClass().getClassLoader().loadClass(castname);
            }
            catch (ClassNotFoundException e) {
                throw new Exception(this.m_lineNumber + "," + this.m_columnNumber + ":" + " cannot found cast class " + classname + " for \"mapping\" PI");
            }
        }
        Method defaultAddMethod = null;
        this.addType(element, clazz, castClazz, defaultAddMethod);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public class TypeEntry {
        public final Object m_instanceFactory;
        public final Class m_instanceClass;
        public final Method m_newInstanceMethod;
        public final Class m_castClass;
        public final Method m_defaultAddMethod;

        public TypeEntry(Object instanceFactory, Class castClass, Method defaultAddMethod) throws Exception {
            this.m_instanceFactory = instanceFactory;
            try {
                if (instanceFactory instanceof Class) {
                    this.m_newInstanceMethod = instanceFactory.getClass().getDeclaredMethod("newInstance", null);
                    if (castClass == null) {
                        this.m_castClass = (Class)instanceFactory;
                    } else {
                        if (!castClass.isAssignableFrom((Class)instanceFactory)) {
                            throw new Exception("instanceFactory " + instanceFactory.getClass().getName() + " could not instanciate objects assignable to " + castClass.getName());
                        }
                        this.m_castClass = castClass;
                    }
                    this.m_instanceClass = (Class)instanceFactory;
                } else {
                    this.m_newInstanceMethod = instanceFactory.getClass().getDeclaredMethod("newInstance", null);
                    Class<?> returnType = this.m_newInstanceMethod.getReturnType();
                    if (castClass == null) {
                        this.m_castClass = returnType;
                    } else {
                        if (!castClass.isAssignableFrom(returnType)) {
                            throw new Exception("instanceFactory " + instanceFactory.getClass().getName() + " could not instanciate objects assignable to " + castClass.getName());
                        }
                        this.m_castClass = castClass;
                    }
                    this.m_instanceClass = returnType;
                }
            }
            catch (NoSuchMethodException e) {
                throw new Exception("instanceFactory " + instanceFactory.getClass().getName() + " should have a newInstance method");
            }
            this.m_defaultAddMethod = defaultAddMethod;
            if (this.m_defaultAddMethod != null) {
                this.m_defaultAddMethod.setAccessible(true);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            if (this.m_instanceFactory instanceof Class) {
                sb.append("instanceFactory=").append(((Class)this.m_instanceFactory).getName());
            } else {
                sb.append("instanceFactory=").append(this.m_instanceFactory.getClass().getName());
            }
            sb.append(",instanceClass=").append(this.m_instanceClass.getName());
            sb.append(",castClass=").append(this.m_castClass.getName());
            sb.append(",defaultAddMethod=");
            if (this.m_defaultAddMethod == null) {
                sb.append("");
            } else {
                sb.append(this.m_defaultAddMethod.getName());
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private class XmlStackElement {
        public final String m_qname;
        public Object m_object;

        public XmlStackElement(String qname, Object object) {
            this.m_qname = qname;
            this.m_object = object;
        }
    }
}

