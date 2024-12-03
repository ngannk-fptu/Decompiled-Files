/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.description;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import org.apache.axis.AxisProperties;
import org.apache.axis.AxisServiceConfig;
import org.apache.axis.Constants;
import org.apache.axis.InternalException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.bytecode.ParamNameExtractor;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.commons.logging.Log;

public class JavaServiceDesc
implements ServiceDesc {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$description$JavaServiceDesc == null ? (class$org$apache$axis$description$JavaServiceDesc = JavaServiceDesc.class$("org.apache.axis.description.JavaServiceDesc")) : class$org$apache$axis$description$JavaServiceDesc).getName());
    private String name = null;
    private String documentation = null;
    private Style style = Style.RPC;
    private Use use = Use.ENCODED;
    private boolean useSet = false;
    private ArrayList operations = new ArrayList();
    private List namespaceMappings = null;
    private String wsdlFileName = null;
    private String endpointURL = null;
    private HashMap properties = null;
    private HashMap name2OperationsMap = null;
    private HashMap qname2OperationsMap = null;
    private transient HashMap method2OperationMap = new HashMap();
    private List allowedMethods = null;
    private List disallowedMethods = null;
    private Class implClass = null;
    private boolean isSkeletonClass = false;
    private transient Method skelMethod = null;
    private ArrayList stopClasses = null;
    private transient HashMap method2ParamsMap = new HashMap();
    private OperationDesc messageServiceDefaultOp = null;
    private ArrayList completedNames = new ArrayList();
    private TypeMapping tm = null;
    private TypeMappingRegistry tmr = null;
    private boolean haveAllSkeletonMethods = false;
    private boolean introspectionComplete = false;
    static /* synthetic */ Class class$org$apache$axis$description$JavaServiceDesc;
    static /* synthetic */ Class class$org$apache$axis$wsdl$Skeleton;
    static /* synthetic */ Class class$javax$xml$rpc$holders$Holder;
    static /* synthetic */ Class array$Lorg$w3c$dom$Element;
    static /* synthetic */ Class array$Lorg$apache$axis$message$SOAPBodyElement;
    static /* synthetic */ Class class$org$w3c$dom$Document;
    static /* synthetic */ Class class$org$apache$axis$message$SOAPEnvelope;
    static /* synthetic */ Class class$javax$xml$soap$SOAPEnvelope;
    static /* synthetic */ Class class$javax$xml$rpc$server$ServiceLifecycle;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$rmi$RemoteException;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
        if (!this.useSet) {
            this.use = style == Style.RPC ? Use.ENCODED : Use.LITERAL;
        }
    }

    public Use getUse() {
        return this.use;
    }

    public void setUse(Use use) {
        this.useSet = true;
        this.use = use;
    }

    public boolean isWrapped() {
        return this.style == Style.RPC || this.style == Style.WRAPPED;
    }

    public String getWSDLFile() {
        return this.wsdlFileName;
    }

    public void setWSDLFile(String wsdlFileName) {
        this.wsdlFileName = wsdlFileName;
    }

    public List getAllowedMethods() {
        return this.allowedMethods;
    }

    public void setAllowedMethods(List allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public Class getImplClass() {
        return this.implClass;
    }

    public void setImplClass(Class implClass) {
        if (this.implClass != null) {
            throw new IllegalArgumentException(Messages.getMessage("implAlreadySet"));
        }
        this.implClass = implClass;
        if ((class$org$apache$axis$wsdl$Skeleton == null ? (class$org$apache$axis$wsdl$Skeleton = JavaServiceDesc.class$("org.apache.axis.wsdl.Skeleton")) : class$org$apache$axis$wsdl$Skeleton).isAssignableFrom(implClass)) {
            this.isSkeletonClass = true;
            this.loadSkeletonOperations();
        }
    }

    private void loadSkeletonOperations() {
        Method method = null;
        try {
            method = this.implClass.getDeclaredMethod("getOperationDescs", new Class[0]);
        }
        catch (NoSuchMethodException e) {
        }
        catch (SecurityException e) {
            // empty catch block
        }
        if (method == null) {
            return;
        }
        try {
            Collection opers = (Collection)method.invoke((Object)this.implClass, null);
            Iterator i = opers.iterator();
            while (i.hasNext()) {
                OperationDesc skelDesc = (OperationDesc)i.next();
                this.addOperationDesc(skelDesc);
            }
        }
        catch (IllegalAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
            return;
        }
        catch (IllegalArgumentException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
            return;
        }
        catch (InvocationTargetException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
            return;
        }
        this.haveAllSkeletonMethods = true;
    }

    public TypeMapping getTypeMapping() {
        if (this.tm == null) {
            return DefaultTypeMappingImpl.getSingletonDelegate();
        }
        return this.tm;
    }

    public void setTypeMapping(TypeMapping tm) {
        this.tm = tm;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public ArrayList getStopClasses() {
        return this.stopClasses;
    }

    public void setStopClasses(ArrayList stopClasses) {
        this.stopClasses = stopClasses;
    }

    public List getDisallowedMethods() {
        return this.disallowedMethods;
    }

    public void setDisallowedMethods(List disallowedMethods) {
        this.disallowedMethods = disallowedMethods;
    }

    public void removeOperationDesc(OperationDesc operation) {
        Method method;
        QName qname;
        ArrayList list;
        String name;
        ArrayList overloads;
        this.operations.remove(operation);
        operation.setParent(null);
        if (this.name2OperationsMap != null && (overloads = (ArrayList)this.name2OperationsMap.get(name = operation.getName())) != null) {
            overloads.remove(operation);
            if (overloads.size() == 0) {
                this.name2OperationsMap.remove(name);
            }
        }
        if (this.qname2OperationsMap != null && (list = (ArrayList)this.qname2OperationsMap.get(qname = operation.getElementQName())) != null) {
            list.remove(operation);
        }
        if (this.method2OperationMap != null && (method = operation.getMethod()) != null) {
            this.method2OperationMap.remove(method);
        }
    }

    public void addOperationDesc(OperationDesc operation) {
        String name;
        ArrayList<OperationDesc> overloads;
        this.operations.add(operation);
        operation.setParent(this);
        if (this.name2OperationsMap == null) {
            this.name2OperationsMap = new HashMap();
        }
        if ((overloads = (ArrayList<OperationDesc>)this.name2OperationsMap.get(name = operation.getName())) == null) {
            overloads = new ArrayList<OperationDesc>();
            this.name2OperationsMap.put(name, overloads);
        } else if (JavaUtils.isTrue(AxisProperties.getProperty("axis.ws-i.bp11.compatibility")) && overloads.size() > 0) {
            throw new RuntimeException(Messages.getMessage("noOverloadedOperations", name));
        }
        overloads.add(operation);
    }

    public ArrayList getOperations() {
        this.loadServiceDescByIntrospection();
        return this.operations;
    }

    public OperationDesc[] getOperationsByName(String methodName) {
        this.getSyncedOperationsForName(this.implClass, methodName);
        if (this.name2OperationsMap == null) {
            return null;
        }
        ArrayList overloads = (ArrayList)this.name2OperationsMap.get(methodName);
        if (overloads == null) {
            return null;
        }
        OperationDesc[] array = new OperationDesc[overloads.size()];
        return overloads.toArray(array);
    }

    public OperationDesc getOperationByName(String methodName) {
        this.getSyncedOperationsForName(this.implClass, methodName);
        if (this.name2OperationsMap == null) {
            return null;
        }
        ArrayList overloads = (ArrayList)this.name2OperationsMap.get(methodName);
        if (overloads == null) {
            return null;
        }
        return (OperationDesc)overloads.get(0);
    }

    public OperationDesc getOperationByElementQName(QName qname) {
        OperationDesc[] overloads = this.getOperationsByQName(qname);
        if (overloads != null && overloads.length > 0) {
            return overloads[0];
        }
        return null;
    }

    public OperationDesc[] getOperationsByQName(QName qname) {
        this.initQNameMap();
        ArrayList overloads = (ArrayList)this.qname2OperationsMap.get(qname);
        if (overloads == null) {
            if (this.name2OperationsMap != null) {
                if (this.isWrapped() || this.style == Style.MESSAGE && this.getDefaultNamespace() == null) {
                    overloads = (ArrayList)this.name2OperationsMap.get(qname.getLocalPart());
                } else {
                    Object ops = this.name2OperationsMap.get(qname.getLocalPart());
                    if (ops != null) {
                        overloads = new ArrayList((Collection)ops);
                        Iterator iter = overloads.iterator();
                        while (iter.hasNext()) {
                            OperationDesc operationDesc = (OperationDesc)iter.next();
                            if (Style.WRAPPED == operationDesc.getStyle()) continue;
                            iter.remove();
                        }
                    }
                }
            }
            if (this.style == Style.MESSAGE && this.messageServiceDefaultOp != null) {
                return new OperationDesc[]{this.messageServiceDefaultOp};
            }
            if (overloads == null) {
                return null;
            }
        }
        this.getSyncedOperationsForName(this.implClass, ((OperationDesc)overloads.get(0)).getName());
        Collections.sort(overloads, new Comparator(){

            public int compare(Object o1, Object o2) {
                Method meth1 = ((OperationDesc)o1).getMethod();
                Method meth2 = ((OperationDesc)o2).getMethod();
                return meth1.getParameterTypes().length - meth2.getParameterTypes().length;
            }
        });
        OperationDesc[] array = new OperationDesc[overloads.size()];
        return overloads.toArray(array);
    }

    private synchronized void initQNameMap() {
        if (this.qname2OperationsMap == null) {
            this.loadServiceDescByIntrospection();
            this.qname2OperationsMap = new HashMap();
            Iterator i = this.operations.iterator();
            while (i.hasNext()) {
                OperationDesc operationDesc = (OperationDesc)i.next();
                QName qname = operationDesc.getElementQName();
                ArrayList<OperationDesc> list = (ArrayList<OperationDesc>)this.qname2OperationsMap.get(qname);
                if (list == null) {
                    list = new ArrayList<OperationDesc>();
                    this.qname2OperationsMap.put(qname, list);
                }
                list.add(operationDesc);
            }
        }
    }

    private void syncOperationToClass(OperationDesc oper, Class implClass) {
        if (oper.getMethod() != null) {
            return;
        }
        Method[] methods = this.getMethods(implClass);
        Method possibleMatch = null;
        for (int i = 0; i < methods.length; ++i) {
            int j;
            Method method = methods[i];
            if (!Modifier.isPublic(method.getModifiers()) || !method.getName().equals(oper.getName()) || this.method2OperationMap.get(method) != null) continue;
            if (this.style == Style.MESSAGE) {
                int messageOperType = this.checkMessageMethod(method);
                if (messageOperType == -4) continue;
                if (messageOperType == -1) {
                    throw new InternalException("Couldn't match method to any of the allowable message-style patterns!");
                }
                oper.setMessageOperationStyle(messageOperType);
                possibleMatch = method;
                break;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != oper.getNumParams()) continue;
            boolean conversionNecessary = false;
            for (j = 0; j < paramTypes.length; ++j) {
                ParameterDesc param;
                QName typeQName;
                Class type;
                Class actualType = type = paramTypes[j];
                if ((class$javax$xml$rpc$holders$Holder == null ? JavaServiceDesc.class$("javax.xml.rpc.holders.Holder") : class$javax$xml$rpc$holders$Holder).isAssignableFrom(type)) {
                    actualType = JavaUtils.getHolderValueType(type);
                }
                if ((typeQName = (param = oper.getParameter(j)).getTypeQName()) == null) {
                    typeQName = this.getTypeMapping().getTypeQName(actualType);
                    param.setTypeQName(typeQName);
                } else {
                    Class paramClass = param.getJavaType();
                    if (paramClass != null && JavaUtils.getHolderValueType(paramClass) != null) {
                        paramClass = JavaUtils.getHolderValueType(paramClass);
                    }
                    if (paramClass == null) {
                        paramClass = this.getTypeMapping().getClassForQName(param.getTypeQName(), type);
                    }
                    if (paramClass != null) {
                        if (!JavaUtils.isConvertable(paramClass, actualType)) break;
                        if (!actualType.isAssignableFrom(paramClass)) {
                            conversionNecessary = true;
                        }
                    }
                }
                param.setJavaType(type);
            }
            if (j != paramTypes.length) continue;
            possibleMatch = method;
            if (!conversionNecessary) break;
        }
        if (possibleMatch != null) {
            Class<?> returnClass = possibleMatch.getReturnType();
            oper.setReturnClass(returnClass);
            QName returnType = oper.getReturnType();
            if (returnType == null) {
                oper.setReturnType(this.getTypeMapping().getTypeQName(returnClass));
            }
            this.createFaultMetadata(possibleMatch, oper);
            oper.setMethod(possibleMatch);
            this.method2OperationMap.put(possibleMatch, oper);
            return;
        }
        Class superClass = implClass.getSuperclass();
        if (!(superClass == null || superClass.getName().startsWith("java.") || superClass.getName().startsWith("javax.") || this.stopClasses != null && this.stopClasses.contains(superClass.getName()))) {
            this.syncOperationToClass(oper, superClass);
        }
        if (oper.getMethod() == null) {
            InternalException ie = new InternalException(Messages.getMessage("serviceDescOperSync00", oper.getName(), implClass.getName()));
            throw ie;
        }
    }

    private Method[] getMethods(Class implClass) {
        if (implClass.isInterface()) {
            return implClass.getMethods();
        }
        return implClass.getDeclaredMethods();
    }

    private int checkMessageMethod(Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length == 1) {
            if (params[0] == (array$Lorg$w3c$dom$Element == null ? (array$Lorg$w3c$dom$Element = JavaServiceDesc.class$("[Lorg.w3c.dom.Element;")) : array$Lorg$w3c$dom$Element) && method.getReturnType() == (array$Lorg$w3c$dom$Element == null ? (array$Lorg$w3c$dom$Element = JavaServiceDesc.class$("[Lorg.w3c.dom.Element;")) : array$Lorg$w3c$dom$Element)) {
                return 3;
            }
            if (params[0] == (array$Lorg$apache$axis$message$SOAPBodyElement == null ? (array$Lorg$apache$axis$message$SOAPBodyElement = JavaServiceDesc.class$("[Lorg.apache.axis.message.SOAPBodyElement;")) : array$Lorg$apache$axis$message$SOAPBodyElement) && method.getReturnType() == (array$Lorg$apache$axis$message$SOAPBodyElement == null ? (array$Lorg$apache$axis$message$SOAPBodyElement = JavaServiceDesc.class$("[Lorg.apache.axis.message.SOAPBodyElement;")) : array$Lorg$apache$axis$message$SOAPBodyElement)) {
                return 1;
            }
            if (params[0] == (class$org$w3c$dom$Document == null ? (class$org$w3c$dom$Document = JavaServiceDesc.class$("org.w3c.dom.Document")) : class$org$w3c$dom$Document) && method.getReturnType() == (class$org$w3c$dom$Document == null ? (class$org$w3c$dom$Document = JavaServiceDesc.class$("org.w3c.dom.Document")) : class$org$w3c$dom$Document)) {
                return 4;
            }
        } else if (params.length == 2 && (params[0] == (class$org$apache$axis$message$SOAPEnvelope == null ? (class$org$apache$axis$message$SOAPEnvelope = JavaServiceDesc.class$("org.apache.axis.message.SOAPEnvelope")) : class$org$apache$axis$message$SOAPEnvelope) && params[1] == (class$org$apache$axis$message$SOAPEnvelope == null ? (class$org$apache$axis$message$SOAPEnvelope = JavaServiceDesc.class$("org.apache.axis.message.SOAPEnvelope")) : class$org$apache$axis$message$SOAPEnvelope) || params[0] == (class$javax$xml$soap$SOAPEnvelope == null ? (class$javax$xml$soap$SOAPEnvelope = JavaServiceDesc.class$("javax.xml.soap.SOAPEnvelope")) : class$javax$xml$soap$SOAPEnvelope) && params[1] == (class$javax$xml$soap$SOAPEnvelope == null ? (class$javax$xml$soap$SOAPEnvelope = JavaServiceDesc.class$("javax.xml.soap.SOAPEnvelope")) : class$javax$xml$soap$SOAPEnvelope) && method.getReturnType() == Void.TYPE)) {
            return 2;
        }
        if (null != this.allowedMethods && !this.allowedMethods.isEmpty()) {
            throw new InternalException(Messages.getMessage("badMsgMethodParams", method.getName()));
        }
        return -4;
    }

    public void loadServiceDescByIntrospection() {
        this.loadServiceDescByIntrospection(this.implClass);
        this.completedNames = null;
    }

    public void loadServiceDescByIntrospection(Class implClass) {
        String allowedMethodsStr;
        if (this.introspectionComplete || implClass == null) {
            return;
        }
        this.implClass = implClass;
        if ((class$org$apache$axis$wsdl$Skeleton == null ? (class$org$apache$axis$wsdl$Skeleton = JavaServiceDesc.class$("org.apache.axis.wsdl.Skeleton")) : class$org$apache$axis$wsdl$Skeleton).isAssignableFrom(implClass)) {
            this.isSkeletonClass = true;
            this.loadSkeletonOperations();
        }
        AxisServiceConfig axisConfig = null;
        try {
            Method method = implClass.getDeclaredMethod("getAxisServiceConfig", new Class[0]);
            if (method != null && Modifier.isStatic(method.getModifiers())) {
                axisConfig = (AxisServiceConfig)method.invoke(null, null);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        if (axisConfig != null && (allowedMethodsStr = axisConfig.getAllowedMethods()) != null && !"*".equals(allowedMethodsStr)) {
            ArrayList<String> methodList = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(allowedMethodsStr, " ,");
            while (tokenizer.hasMoreTokens()) {
                methodList.add(tokenizer.nextToken());
            }
            this.setAllowedMethods(methodList);
        }
        this.loadServiceDescByIntrospectionRecursive(implClass);
        Iterator iterator = this.operations.iterator();
        while (iterator.hasNext()) {
            OperationDesc operation = (OperationDesc)iterator.next();
            if (operation.getMethod() != null) continue;
            throw new InternalException(Messages.getMessage("badWSDDOperation", operation.getName(), "" + operation.getNumParams()));
        }
        if (this.style == Style.MESSAGE && this.operations.size() == 1) {
            this.messageServiceDefaultOp = (OperationDesc)this.operations.get(0);
        }
        this.introspectionComplete = true;
    }

    private boolean isServiceLifeCycleMethod(Class implClass, Method m) {
        Class<?>[] classes;
        String methodName;
        return (class$javax$xml$rpc$server$ServiceLifecycle == null ? (class$javax$xml$rpc$server$ServiceLifecycle = JavaServiceDesc.class$("javax.xml.rpc.server.ServiceLifecycle")) : class$javax$xml$rpc$server$ServiceLifecycle).isAssignableFrom(implClass) && ((methodName = m.getName()).equals("init") ? (classes = m.getParameterTypes()) != null && classes.length == 1 && classes[0] == (class$java$lang$Object == null ? (class$java$lang$Object = JavaServiceDesc.class$("java.lang.Object")) : class$java$lang$Object) && m.getReturnType() == Void.TYPE : methodName.equals("destroy") && (classes = m.getParameterTypes()) != null && classes.length == 0 && m.getReturnType() == Void.TYPE);
    }

    private void loadServiceDescByIntrospectionRecursive(Class implClass) {
        if ((class$org$apache$axis$wsdl$Skeleton == null ? (class$org$apache$axis$wsdl$Skeleton = JavaServiceDesc.class$("org.apache.axis.wsdl.Skeleton")) : class$org$apache$axis$wsdl$Skeleton).equals(implClass)) {
            return;
        }
        Method[] methods = this.getMethods(implClass);
        for (int i = 0; i < methods.length; ++i) {
            if (!Modifier.isPublic(methods[i].getModifiers()) || this.isServiceLifeCycleMethod(implClass, methods[i])) continue;
            this.getSyncedOperationsForName(implClass, methods[i].getName());
        }
        if (implClass.isInterface()) {
            Class<?>[] superClasses = implClass.getInterfaces();
            for (int i = 0; i < superClasses.length; ++i) {
                Class<?> superClass = superClasses[i];
                if (superClass.getName().startsWith("java.") || superClass.getName().startsWith("javax.") || this.stopClasses != null && this.stopClasses.contains(superClass.getName())) continue;
                this.loadServiceDescByIntrospectionRecursive(superClass);
            }
        } else {
            Class superClass = implClass.getSuperclass();
            if (!(superClass == null || superClass.getName().startsWith("java.") || superClass.getName().startsWith("javax.") || this.stopClasses != null && this.stopClasses.contains(superClass.getName()))) {
                this.loadServiceDescByIntrospectionRecursive(superClass);
            }
        }
    }

    public void loadServiceDescByIntrospection(Class cls, TypeMapping tm) {
        this.implClass = cls;
        this.tm = tm;
        if ((class$org$apache$axis$wsdl$Skeleton == null ? (class$org$apache$axis$wsdl$Skeleton = JavaServiceDesc.class$("org.apache.axis.wsdl.Skeleton")) : class$org$apache$axis$wsdl$Skeleton).isAssignableFrom(this.implClass)) {
            this.isSkeletonClass = true;
            this.loadSkeletonOperations();
        }
        this.loadServiceDescByIntrospection();
    }

    private void getSyncedOperationsForName(Class implClass, String methodName) {
        ArrayList currentOverloads;
        Iterator i;
        if (this.isSkeletonClass && (methodName.equals("getOperationDescByName") || methodName.equals("getOperationDescs"))) {
            return;
        }
        if (implClass == null) {
            return;
        }
        if (this.completedNames == null || this.completedNames.contains(methodName)) {
            return;
        }
        if (this.allowedMethods != null && !this.allowedMethods.contains(methodName)) {
            return;
        }
        if (this.disallowedMethods != null && this.disallowedMethods.contains(methodName)) {
            return;
        }
        if (this.isSkeletonClass && !this.haveAllSkeletonMethods) {
            if (this.skelMethod == null) {
                try {
                    this.skelMethod = implClass.getDeclaredMethod("getOperationDescByName", class$java$lang$String == null ? (class$java$lang$String = JavaServiceDesc.class$("java.lang.String")) : class$java$lang$String);
                }
                catch (NoSuchMethodException e) {
                }
                catch (SecurityException e) {
                    // empty catch block
                }
                if (this.skelMethod == null) {
                    return;
                }
            }
            try {
                List skelList = (List)this.skelMethod.invoke((Object)implClass, methodName);
                if (skelList != null) {
                    i = skelList.iterator();
                    while (i.hasNext()) {
                        this.addOperationDesc((OperationDesc)i.next());
                    }
                }
            }
            catch (IllegalAccessException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
                return;
            }
            catch (IllegalArgumentException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
                return;
            }
            catch (InvocationTargetException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                }
                return;
            }
        }
        if (this.name2OperationsMap != null && (currentOverloads = (ArrayList)this.name2OperationsMap.get(methodName)) != null) {
            i = currentOverloads.iterator();
            while (i.hasNext()) {
                OperationDesc oper = (OperationDesc)i.next();
                if (oper.getMethod() != null) continue;
                this.syncOperationToClass(oper, implClass);
            }
        }
        this.createOperationsForName(implClass, methodName);
        this.completedNames.add(methodName);
    }

    private String getUniqueOperationName(String name) {
        String candidate;
        int i = 1;
        while (this.name2OperationsMap.get(candidate = name + i++) != null) {
        }
        return candidate;
    }

    private void createOperationsForName(Class implClass, String methodName) {
        if (this.isSkeletonClass && (methodName.equals("getOperationDescByName") || methodName.equals("getOperationDescs"))) {
            return;
        }
        Method[] methods = this.getMethods(implClass);
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (!Modifier.isPublic(method.getModifiers()) || !method.getName().equals(methodName) || this.isServiceLifeCycleMethod(implClass, method)) continue;
            this.createOperationForMethod(method);
        }
        Class superClass = implClass.getSuperclass();
        if (!(superClass == null || superClass.getName().startsWith("java.") || superClass.getName().startsWith("javax.") || this.stopClasses != null && this.stopClasses.contains(superClass.getName()))) {
            this.createOperationsForName(superClass, methodName);
        }
    }

    private void createOperationForMethod(Method method) {
        ArrayList overloads;
        if (this.method2OperationMap.get(method) != null) {
            return;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        ArrayList arrayList = overloads = this.name2OperationsMap == null ? null : (ArrayList)this.name2OperationsMap.get(method.getName());
        if (overloads != null && !overloads.isEmpty()) {
            for (int i = 0; i < overloads.size(); ++i) {
                int j;
                Class<?>[] others;
                OperationDesc op = (OperationDesc)overloads.get(i);
                Method checkMethod = op.getMethod();
                if (checkMethod == null || paramTypes.length != (others = checkMethod.getParameterTypes()).length) continue;
                for (j = 0; j < others.length && others[j].equals(paramTypes[j]); ++j) {
                }
                if (j != others.length) continue;
                return;
            }
        }
        boolean isWSICompliant = JavaUtils.isTrue(AxisProperties.getProperty("axis.ws-i.bp11.compatibility"));
        OperationDesc operation = new OperationDesc();
        String name = method.getName();
        if (isWSICompliant && this.name2OperationsMap != null) {
            Set methodNames = this.name2OperationsMap.keySet();
            name = JavaUtils.getUniqueValue(methodNames, name);
        }
        operation.setName(name);
        String defaultNS = "";
        if (this.namespaceMappings != null && !this.namespaceMappings.isEmpty()) {
            defaultNS = (String)this.namespaceMappings.get(0);
        }
        if (defaultNS.length() == 0) {
            defaultNS = Namespaces.makeNamespace(method.getDeclaringClass().getName());
        }
        operation.setElementQName(new QName(defaultNS, name));
        operation.setMethod(method);
        if (this.style == Style.MESSAGE) {
            int messageOperType = this.checkMessageMethod(method);
            if (messageOperType == -4) {
                return;
            }
            if (messageOperType == -1) {
                throw new InternalException("Couldn't match method to any of the allowable message-style patterns!");
            }
            operation.setMessageOperationStyle(messageOperType);
            operation.setReturnClass(class$java$lang$Object == null ? (class$java$lang$Object = JavaServiceDesc.class$("java.lang.Object")) : class$java$lang$Object);
            operation.setReturnType(Constants.XSD_ANYTYPE);
        } else {
            Class<?> retClass = method.getReturnType();
            operation.setReturnClass(retClass);
            QName typeQName = this.getTypeQName(retClass);
            operation.setReturnType(typeQName);
            String[] paramNames = this.getParamNames(method);
            for (int k = 0; k < paramTypes.length; ++k) {
                String paramNamespace;
                Class<?> type = paramTypes[k];
                ParameterDesc paramDesc = new ParameterDesc();
                String string = paramNamespace = this.style == Style.RPC ? "" : operation.getElementQName().getNamespaceURI();
                if (paramNames != null && paramNames[k] != null && paramNames[k].length() > 0) {
                    paramDesc.setQName(new QName(paramNamespace, paramNames[k]));
                } else {
                    paramDesc.setQName(new QName(paramNamespace, "in" + k));
                }
                Class heldClass = JavaUtils.getHolderValueType(type);
                if (heldClass != null) {
                    paramDesc.setMode((byte)3);
                    paramDesc.setTypeQName(this.getTypeQName(heldClass));
                } else {
                    paramDesc.setMode((byte)1);
                    paramDesc.setTypeQName(this.getTypeQName(type));
                }
                paramDesc.setJavaType(type);
                operation.addParameter(paramDesc);
            }
        }
        this.createFaultMetadata(method, operation);
        this.addOperationDesc(operation);
        this.method2OperationMap.put(method, operation);
    }

    private QName getTypeQName(Class javaClass) {
        QName typeQName;
        TypeMapping tm = this.getTypeMapping();
        typeQName = this.style == Style.RPC ? tm.getTypeQName(javaClass) : ((typeQName = tm.getTypeQNameExact(javaClass)) == null && javaClass.isArray() ? tm.getTypeQName(javaClass.getComponentType()) : tm.getTypeQName(javaClass));
        return typeQName;
    }

    private void createFaultMetadata(Method method, OperationDesc operation) {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        for (int i = 0; i < exceptionTypes.length; ++i) {
            boolean isNew;
            Class<?> ex = exceptionTypes[i];
            if (ex == (class$java$rmi$RemoteException == null ? JavaServiceDesc.class$("java.rmi.RemoteException") : class$java$rmi$RemoteException) || ex == (class$org$apache$axis$AxisFault == null ? JavaServiceDesc.class$("org.apache.axis.AxisFault") : class$org$apache$axis$AxisFault) || ex.getName().startsWith("java.") || ex.getName().startsWith("javax.")) continue;
            FaultDesc fault = operation.getFaultByClass(ex, false);
            if (fault == null) {
                fault = new FaultDesc();
                isNew = true;
            } else {
                isNew = false;
            }
            QName xmlType = fault.getXmlType();
            if (xmlType == null) {
                fault.setXmlType(this.getTypeMapping().getTypeQName(ex));
            }
            String pkgAndClsName = ex.getName();
            if (fault.getClassName() == null) {
                fault.setClassName(pkgAndClsName);
            }
            if (fault.getName() == null) {
                String name = pkgAndClsName.substring(pkgAndClsName.lastIndexOf(46) + 1, pkgAndClsName.length());
                fault.setName(name);
            }
            if (fault.getParameters() == null) {
                QName qname;
                if (xmlType == null) {
                    xmlType = this.getTypeMapping().getTypeQName(ex);
                }
                if ((qname = fault.getQName()) == null) {
                    qname = new QName("", "fault");
                }
                ParameterDesc param = new ParameterDesc(qname, 1, xmlType);
                param.setJavaType(ex);
                ArrayList<ParameterDesc> exceptionParams = new ArrayList<ParameterDesc>();
                exceptionParams.add(param);
                fault.setParameters(exceptionParams);
            }
            if (fault.getQName() == null) {
                fault.setQName(new QName(pkgAndClsName));
            }
            if (!isNew) continue;
            operation.addFault(fault);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String[] getParamNames(Method method) {
        HashMap hashMap = this.method2ParamsMap;
        synchronized (hashMap) {
            String[] paramNames = (String[])this.method2ParamsMap.get(method);
            if (paramNames != null) {
                return paramNames;
            }
            paramNames = ParamNameExtractor.getParameterNamesFromDebugInfo(method);
            this.method2ParamsMap.put(method, paramNames);
            return paramNames;
        }
    }

    public void setNamespaceMappings(List namespaces) {
        this.namespaceMappings = namespaces;
    }

    public String getDefaultNamespace() {
        if (this.namespaceMappings == null || this.namespaceMappings.isEmpty()) {
            return null;
        }
        return (String)this.namespaceMappings.get(0);
    }

    public void setDefaultNamespace(String namespace) {
        if (this.namespaceMappings == null) {
            this.namespaceMappings = new ArrayList();
        }
        this.namespaceMappings.add(0, namespace);
    }

    public void setProperty(String name, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap();
        }
        this.properties.put(name, value);
    }

    public Object getProperty(String name) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(name);
    }

    public String getEndpointURL() {
        return this.endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        if (this.tmr == null) {
            this.tmr = new TypeMappingRegistryImpl(false);
        }
        return this.tmr;
    }

    public void setTypeMappingRegistry(TypeMappingRegistry tmr) {
        this.tmr = tmr;
    }

    public boolean isInitialized() {
        return this.implClass != null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

