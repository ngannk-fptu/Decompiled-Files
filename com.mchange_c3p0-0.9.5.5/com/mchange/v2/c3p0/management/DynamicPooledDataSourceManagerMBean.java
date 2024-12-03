/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.lang.ClassUtils
 *  com.mchange.v2.lang.ObjectUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.management.ManagementUtils
 */
package com.mchange.v2.c3p0.management;

import com.mchange.v1.lang.ClassUtils;
import com.mchange.v2.c3p0.AbstractComboPooledDataSource;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;
import com.mchange.v2.c3p0.management.ActiveManagementCoordinator;
import com.mchange.v2.lang.ObjectUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.management.ManagementUtils;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

public class DynamicPooledDataSourceManagerMBean
implements DynamicMBean {
    static final MLogger logger = MLog.getLogger(DynamicPooledDataSourceManagerMBean.class);
    static final Set HIDE_PROPS;
    static final Set HIDE_OPS;
    static final Set FORCE_OPS;
    static final Set FORCE_READ_ONLY_PROPS;
    static final MBeanOperationInfo[] OP_INFS;
    MBeanInfo info = null;
    PooledDataSource pds;
    String mbeanName;
    MBeanServer mbs;
    ConnectionPoolDataSource cpds;
    DataSource unpooledDataSource;
    Map pdsAttrInfos;
    Map cpdsAttrInfos;
    Map unpooledDataSourceAttrInfos;
    PropertyChangeListener pcl = new PropertyChangeListener(){

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            Object val = evt.getNewValue();
            if ("nestedDataSource".equals(propName) || "connectionPoolDataSource".equals(propName) || "dataSourceName".equals(propName)) {
                DynamicPooledDataSourceManagerMBean.this.reinitialize();
            }
        }
    };

    public DynamicPooledDataSourceManagerMBean(PooledDataSource pds, String mbeanName, MBeanServer mbs) throws Exception {
        this.pds = pds;
        this.mbeanName = mbeanName;
        this.mbs = mbs;
        if (pds instanceof AbstractComboPooledDataSource) {
            ((AbstractComboPooledDataSource)pds).addPropertyChangeListener(this.pcl);
        } else if (pds instanceof AbstractPoolBackedDataSource) {
            ((AbstractPoolBackedDataSource)pds).addPropertyChangeListener(this.pcl);
        } else {
            logger.warning(this + "managing an unexpected PooledDataSource. Only top-level attributes will be available. PooledDataSource: " + pds);
        }
        Exception e = this.reinitialize();
        if (e != null) {
            throw e;
        }
    }

    private synchronized Exception reinitialize() {
        try {
            if (!(this.pds instanceof AbstractComboPooledDataSource) && this.pds instanceof AbstractPoolBackedDataSource) {
                if (this.cpds instanceof WrapperConnectionPoolDataSource) {
                    ((WrapperConnectionPoolDataSource)this.cpds).removePropertyChangeListener(this.pcl);
                }
                this.cpds = null;
                this.unpooledDataSource = null;
                this.cpds = ((AbstractPoolBackedDataSource)this.pds).getConnectionPoolDataSource();
                if (this.cpds instanceof WrapperConnectionPoolDataSource) {
                    this.unpooledDataSource = ((WrapperConnectionPoolDataSource)this.cpds).getNestedDataSource();
                    ((WrapperConnectionPoolDataSource)this.cpds).addPropertyChangeListener(this.pcl);
                }
            }
            this.pdsAttrInfos = DynamicPooledDataSourceManagerMBean.extractAttributeInfos(this.pds);
            this.cpdsAttrInfos = DynamicPooledDataSourceManagerMBean.extractAttributeInfos(this.cpds);
            this.unpooledDataSourceAttrInfos = DynamicPooledDataSourceManagerMBean.extractAttributeInfos(this.unpooledDataSource);
            HashSet allAttrNames = new HashSet();
            allAttrNames.addAll(this.pdsAttrInfos.keySet());
            allAttrNames.addAll(this.cpdsAttrInfos.keySet());
            allAttrNames.addAll(this.unpooledDataSourceAttrInfos.keySet());
            HashSet allAttrs = new HashSet();
            for (String name : allAttrNames) {
                Object attrInfo = this.pdsAttrInfos.get(name);
                if (attrInfo == null) {
                    attrInfo = this.cpdsAttrInfos.get(name);
                }
                if (attrInfo == null) {
                    attrInfo = this.unpooledDataSourceAttrInfos.get(name);
                }
                allAttrs.add(attrInfo);
            }
            String className = this.getClass().getName();
            MBeanAttributeInfo[] attrInfos = allAttrs.toArray(new MBeanAttributeInfo[allAttrs.size()]);
            Class[] ctorArgClasses = new Class[]{PooledDataSource.class, String.class, MBeanServer.class};
            MBeanConstructorInfo[] constrInfos = new MBeanConstructorInfo[]{new MBeanConstructorInfo("Constructor from PooledDataSource", this.getClass().getConstructor(ctorArgClasses))};
            this.info = new MBeanInfo(this.getClass().getName(), "An MBean to monitor and manage a PooledDataSource", attrInfos, constrInfos, OP_INFS, null);
            try {
                ObjectName oname = ObjectName.getInstance(this.mbeanName);
                if (this.mbs.isRegistered(oname)) {
                    this.mbs.unregisterMBean(oname);
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "MBean: " + oname.toString() + " unregistered, in order to be reregistered after update.");
                    }
                }
                this.mbeanName = ActiveManagementCoordinator.getPdsObjectNameStr(this.pds);
                oname = ObjectName.getInstance(this.mbeanName);
                this.mbs.registerMBean(this, oname);
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, "MBean: " + oname.toString() + " registered.");
                }
                return null;
            }
            catch (Exception e) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "An Exception occurred while registering/reregistering mbean " + this.mbeanName + ". MBean may not be registered, or may not work properly.", (Throwable)e);
                }
                return e;
            }
        }
        catch (NoSuchMethodException e) {
            if (logger.isLoggable(MLevel.SEVERE)) {
                logger.log(MLevel.SEVERE, "Huh? We can't find our own constructor?? The one we're in?", (Throwable)e);
            }
            return e;
        }
    }

    private static MBeanOperationInfo[] extractOpInfs() {
        MBeanParameterInfo user = new MBeanParameterInfo("user", "java.lang.String", "The database username of a pool-owner.");
        MBeanParameterInfo pwd = new MBeanParameterInfo("password", "java.lang.String", "The database password of a pool-owner.");
        MBeanParameterInfo[] userPass = new MBeanParameterInfo[]{user, pwd};
        MBeanParameterInfo[] empty = new MBeanParameterInfo[]{};
        Method[] meths = PooledDataSource.class.getMethods();
        TreeSet<MBeanOperationInfo> attrInfos = new TreeSet<MBeanOperationInfo>(ManagementUtils.OP_INFO_COMPARATOR);
        for (int i = 0; i < meths.length; ++i) {
            int impact;
            Method meth = meths[i];
            if (HIDE_OPS.contains(meth)) continue;
            String mname = meth.getName();
            Class<?>[] params = meth.getParameterTypes();
            if (!FORCE_OPS.contains(mname) && (mname.startsWith("set") && params.length == 1 || (mname.startsWith("get") || mname.startsWith("is")) && params.length == 0)) continue;
            Class<?> retType = meth.getReturnType();
            int n = impact = retType == Void.TYPE ? 1 : 0;
            Object pi = params.length == 2 && params[0] == String.class && params[1] == String.class ? userPass : (params.length == 0 ? empty : null);
            MBeanOperationInfo opi = pi != null ? new MBeanOperationInfo(mname, null, (MBeanParameterInfo[])pi, retType.getName(), impact) : new MBeanOperationInfo(meth.toString(), meth);
            attrInfos.add(opi);
        }
        return attrInfos.toArray(new MBeanOperationInfo[attrInfos.size()]);
    }

    @Override
    public synchronized Object getAttribute(String attr) throws AttributeNotFoundException, MBeanException, ReflectionException {
        try {
            AttrRec rec = this.attrRecForAttribute(attr);
            if (rec == null) {
                throw new AttributeNotFoundException(attr);
            }
            MBeanAttributeInfo ai = rec.attrInfo;
            if (!ai.isReadable()) {
                throw new IllegalArgumentException(attr + " not readable.");
            }
            String name = ai.getName();
            String pfx = ai.isIs() ? "is" : "get";
            String mname = pfx + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Object target = rec.target;
            Method m = target.getClass().getMethod(mname, null);
            return m.invoke(target, (Object[])null);
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Failed to get requested attribute: " + attr, (Throwable)e);
            }
            throw new MBeanException(e);
        }
    }

    @Override
    public synchronized AttributeList getAttributes(String[] attrs) {
        AttributeList al = new AttributeList();
        for (String attr : attrs) {
            try {
                Object val = this.getAttribute(attr);
                al.add(new Attribute(attr, val));
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.log(MLevel.WARNING, "Failed to get requested attribute (for list): " + attr, (Throwable)e);
            }
        }
        return al;
    }

    private AttrRec attrRecForAttribute(String attr) {
        assert (Thread.holdsLock(this));
        if (this.pdsAttrInfos.containsKey(attr)) {
            return new AttrRec(this.pds, (MBeanAttributeInfo)this.pdsAttrInfos.get(attr));
        }
        if (this.cpdsAttrInfos.containsKey(attr)) {
            return new AttrRec(this.cpds, (MBeanAttributeInfo)this.cpdsAttrInfos.get(attr));
        }
        if (this.unpooledDataSourceAttrInfos.containsKey(attr)) {
            return new AttrRec(this.unpooledDataSource, (MBeanAttributeInfo)this.unpooledDataSourceAttrInfos.get(attr));
        }
        return null;
    }

    @Override
    public synchronized MBeanInfo getMBeanInfo() {
        if (this.info == null) {
            this.reinitialize();
        }
        return this.info;
    }

    @Override
    public synchronized Object invoke(String operation, Object[] paramVals, String[] signature) throws MBeanException, ReflectionException {
        try {
            int slen = signature.length;
            Class[] paramTypes = new Class[slen];
            for (int i = 0; i < slen; ++i) {
                paramTypes[i] = ClassUtils.forName((String)signature[i]);
            }
            Method m = this.pds.getClass().getMethod(operation, paramTypes);
            return m.invoke((Object)this.pds, paramVals);
        }
        catch (NoSuchMethodException e) {
            try {
                boolean two = false;
                if (signature.length == 0 && (operation.startsWith("get") || (two = operation.startsWith("is")))) {
                    int i = two ? 2 : 3;
                    String attr = Character.toLowerCase(operation.charAt(i)) + operation.substring(i + 1);
                    return this.getAttribute(attr);
                }
                if (signature.length == 1 && operation.startsWith("set")) {
                    this.setAttribute(new Attribute(Character.toLowerCase(operation.charAt(3)) + operation.substring(4), paramVals[0]));
                    return null;
                }
                throw new MBeanException(e);
            }
            catch (Exception e2) {
                throw new MBeanException(e2);
            }
        }
        catch (Exception e) {
            throw new MBeanException(e);
        }
    }

    @Override
    public synchronized void setAttribute(Attribute attrObj) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        try {
            String attr = attrObj.getName();
            Object curVal = this.getAttribute(attr);
            Object newVal = attrObj.getValue();
            if (!ObjectUtils.eqOrBothNull((Object)curVal, (Object)newVal)) {
                AttrRec rec;
                if (attr == "factoryClassLocation") {
                    if (this.pds instanceof AbstractComboPooledDataSource) {
                        ((AbstractComboPooledDataSource)this.pds).setFactoryClassLocation((String)newVal);
                        return;
                    }
                    if (this.pds instanceof AbstractPoolBackedDataSource) {
                        String strval = (String)newVal;
                        AbstractPoolBackedDataSource apbds = (AbstractPoolBackedDataSource)this.pds;
                        apbds.setFactoryClassLocation(strval);
                        ConnectionPoolDataSource checkDs1 = apbds.getConnectionPoolDataSource();
                        if (checkDs1 instanceof WrapperConnectionPoolDataSource) {
                            WrapperConnectionPoolDataSource wcheckDs1 = (WrapperConnectionPoolDataSource)checkDs1;
                            wcheckDs1.setFactoryClassLocation(strval);
                            DataSource checkDs2 = wcheckDs1.getNestedDataSource();
                            if (checkDs2 instanceof DriverManagerDataSource) {
                                ((DriverManagerDataSource)checkDs2).setFactoryClassLocation(strval);
                            }
                        }
                        return;
                    }
                }
                if ((rec = this.attrRecForAttribute(attr)) == null) {
                    throw new AttributeNotFoundException(attr);
                }
                MBeanAttributeInfo ai = rec.attrInfo;
                if (!ai.isWritable()) {
                    throw new IllegalArgumentException(attr + " not writable.");
                }
                Class attrType = ClassUtils.forName((String)rec.attrInfo.getType());
                String name = ai.getName();
                String pfx = "set";
                String mname = pfx + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                Object target = rec.target;
                Method m = target.getClass().getMethod(mname, attrType);
                m.invoke(target, newVal);
                if (target != this.pds) {
                    if (this.pds instanceof AbstractPoolBackedDataSource) {
                        ((AbstractPoolBackedDataSource)this.pds).resetPoolManager(false);
                    } else if (logger.isLoggable(MLevel.WARNING)) {
                        logger.warning("MBean set a nested ConnectionPoolDataSource or DataSource parameter on an unknown PooledDataSource type. Could not reset the pool manager, so the changes may not take effect. c3p0 may need to be updated for PooledDataSource type " + this.pds.getClass() + ".");
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Failed to set requested attribute: " + attrObj, (Throwable)e);
            }
            throw new MBeanException(e);
        }
    }

    @Override
    public synchronized AttributeList setAttributes(AttributeList al) {
        AttributeList out = new AttributeList();
        int len = al.size();
        for (int i = 0; i < len; ++i) {
            Attribute attrObj = (Attribute)al.get(i);
            try {
                this.setAttribute(attrObj);
                out.add(attrObj);
                continue;
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) continue;
                logger.log(MLevel.WARNING, "Failed to set requested attribute (from list): " + attrObj, (Throwable)e);
            }
        }
        return out;
    }

    private static Map extractAttributeInfos(Object bean) {
        if (bean != null) {
            try {
                HashMap<String, MBeanAttributeInfo> out = new HashMap<String, MBeanAttributeInfo>();
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    String name = pd.getName();
                    if (HIDE_PROPS.contains(name)) continue;
                    String desc = DynamicPooledDataSourceManagerMBean.getDescription(name);
                    Method getter = pd.getReadMethod();
                    Method setter = pd.getWriteMethod();
                    if (FORCE_READ_ONLY_PROPS.contains(name)) {
                        setter = null;
                    }
                    try {
                        out.put(name, new MBeanAttributeInfo(name, desc, getter, setter));
                    }
                    catch (javax.management.IntrospectionException e) {
                        if (!logger.isLoggable(MLevel.WARNING)) continue;
                        logger.log(MLevel.WARNING, "IntrospectionException while setting up MBean attribute '" + name + "'", (Throwable)e);
                    }
                }
                return Collections.synchronizedMap(out);
            }
            catch (IntrospectionException e) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "IntrospectionException while setting up MBean attributes for " + bean, (Throwable)e);
                }
                return Collections.EMPTY_MAP;
            }
        }
        return Collections.EMPTY_MAP;
    }

    private static String getDescription(String attrName) {
        return null;
    }

    static {
        HashSet<String> hpTmp = new HashSet<String>();
        hpTmp.add("connectionPoolDataSource");
        hpTmp.add("nestedDataSource");
        hpTmp.add("reference");
        hpTmp.add("connection");
        hpTmp.add("password");
        hpTmp.add("pooledConnection");
        hpTmp.add("properties");
        hpTmp.add("logWriter");
        hpTmp.add("lastAcquisitionFailureDefaultUser");
        hpTmp.add("lastCheckoutFailureDefaultUser");
        hpTmp.add("lastCheckinFailureDefaultUser");
        hpTmp.add("lastIdleTestFailureDefaultUser");
        hpTmp.add("lastConnectionTestFailureDefaultUser");
        HIDE_PROPS = Collections.unmodifiableSet(hpTmp);
        Class[] userPassArgs = new Class[]{String.class, String.class};
        HashSet<Method> hoTmp = new HashSet<Method>();
        try {
            hoTmp.add(PooledDataSource.class.getMethod("close", Boolean.TYPE));
            hoTmp.add(PooledDataSource.class.getMethod("getConnection", userPassArgs));
            hoTmp.add(PooledDataSource.class.getMethod("getLastAcquisitionFailure", userPassArgs));
            hoTmp.add(PooledDataSource.class.getMethod("getLastCheckinFailure", userPassArgs));
            hoTmp.add(PooledDataSource.class.getMethod("getLastCheckoutFailure", userPassArgs));
            hoTmp.add(PooledDataSource.class.getMethod("getLastIdleTestFailure", userPassArgs));
            hoTmp.add(PooledDataSource.class.getMethod("getLastConnectionTestFailure", userPassArgs));
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, "Tried to hide an operation from being exposed by mbean, but failed to find the operation!", (Throwable)e);
        }
        HIDE_OPS = Collections.unmodifiableSet(hoTmp);
        HashSet<String> fropTmp = new HashSet<String>();
        fropTmp.add("identityToken");
        FORCE_READ_ONLY_PROPS = Collections.unmodifiableSet(fropTmp);
        HashSet foTmp = new HashSet();
        FORCE_OPS = Collections.unmodifiableSet(foTmp);
        OP_INFS = DynamicPooledDataSourceManagerMBean.extractOpInfs();
    }

    private static class AttrRec {
        Object target;
        MBeanAttributeInfo attrInfo;

        AttrRec(Object target, MBeanAttributeInfo attrInfo) {
            this.target = target;
            this.attrInfo = attrInfo;
        }
    }
}

