/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.ConfigException;
import org.bedework.util.misc.Logged;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class ConfigBase<T extends ConfigBase>
extends Logged
implements Comparable<T>,
Serializable {
    public static final String ns = "http://bedework.org/ns/";
    private String name;
    private long lastChanged;

    public void setName(String val) {
        this.name = val;
    }

    public String getName() {
        return this.name;
    }

    public void markChanged() {
        this.lastChanged = System.currentTimeMillis();
    }

    public long getLastChanged() {
        return this.lastChanged;
    }

    public void toStringSegment(ToString ts) {
        ts.append("name", this.getName());
        ts.append("lastChanged", this.getLastChanged());
    }

    @Override
    public int compareTo(ConfigBase that) {
        return this.getName().compareTo(that.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }

    public <L extends List> L addListProperty(L list, String name, String val) {
        Object theList = list;
        if (list == null) {
            theList = new ArrayList();
        }
        theList.add((String)(name + "=" + val));
        return theList;
    }

    @ConfInfo(dontSave=true)
    public String getProperty(Collection<String> col, String name) {
        String key = name + "=";
        for (String p : col) {
            if (!p.startsWith(key)) continue;
            return p.substring(key.length());
        }
        return null;
    }

    public void removeProperty(Collection<String> col, String name) {
        try {
            String v = this.getProperty(col, name);
            if (v == null) {
                return;
            }
            col.remove(name + "=" + v);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public <L extends List> L setListProperty(L list, String name, String val) {
        this.removeProperty(list, name);
        return this.addListProperty(list, name, val);
    }

    public static Properties toProperties(List<String> vals) {
        try {
            StringBuilder sb = new StringBuilder();
            for (String p : vals) {
                sb.append(p);
                sb.append("\n");
            }
            Properties pr = new Properties();
            pr.load(new StringReader(sb.toString()));
            return pr;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void toXml(Writer wtr) throws ConfigException {
        try {
            XmlEmit xml = new XmlEmit();
            xml.addNs(new XmlEmit.NameSpace(ns, "BW"), true);
            xml.startEmit(wtr);
            this.dump(xml, false);
            xml.flush();
        }
        catch (ConfigException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    public ConfigBase fromXml(InputStream is) throws ConfigException {
        return this.fromXml(is, null);
    }

    public ConfigBase fromXml(InputStream is, Class cl) throws ConfigException {
        try {
            return this.fromXml(ConfigBase.parseXml(is), cl);
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    public ConfigBase fromXml(Element rootEl, Class cl) throws ConfigException {
        try {
            ConfigBase cb = (ConfigBase)this.getObject(rootEl, cl);
            if (cb == null) {
                return null;
            }
            for (Element el : XmlUtil.getElementsArray(rootEl)) {
                this.populate(el, cb, null, null);
            }
            return cb;
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    private Object getObject(Element el, Class cl) throws Throwable {
        Class<?> objClass = cl;
        String type = XmlUtil.getAttrVal(el, "type");
        if (type == null && objClass == null) {
            this.error("Must supply a class or have type attribute");
            return null;
        }
        if (objClass == null) {
            objClass = Class.forName(type);
        }
        return objClass.newInstance();
    }

    private static Element parseXml(InputStream is) throws Throwable {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(is));
        if (doc == null) {
            return null;
        }
        return doc.getDocumentElement();
    }

    private void populate(Element subroot, Object o, Collection<Object> col, Class cl) throws Throwable {
        Object val;
        String name = subroot.getNodeName();
        Method meth = null;
        Class<?> elClass = null;
        if (col == null) {
            meth = this.findSetter(o, name);
            if (meth == null) {
                this.error("No setter for " + name);
                return;
            }
            Class<?>[] parClasses = meth.getParameterTypes();
            if (parClasses.length != 1) {
                this.error("Invalid setter method " + name);
                throw new ConfigException("Invalid setter method " + name);
            }
            elClass = parClasses[0];
        } else {
            elClass = cl;
        }
        if (!XmlUtil.hasChildren(subroot)) {
            val = this.simpleValue(elClass, subroot, name);
            if (val != null) {
                this.assign(val, col, o, meth);
            }
            return;
        }
        if (Collection.class.isAssignableFrom(elClass)) {
            TreeSet<Object> colVal = null;
            if (elClass.getName().equals("java.util.Set")) {
                colVal = new TreeSet();
            } else if (elClass.getName().equals("java.util.List")) {
                colVal = new ArrayList();
            } else {
                this.error("Unsupported element class " + elClass + " for field " + name);
                return;
            }
            this.assign(colVal, col, o, meth);
            ConfInfo ci = meth.getAnnotation(ConfInfo.class);
            String colElTypeName = ci == null ? "java.lang.String" : ci.elementType();
            for (Element el : XmlUtil.getElementsArray(subroot)) {
                this.populate(el, o, colVal, Class.forName(colElTypeName));
            }
            return;
        }
        val = this.getObject(subroot, elClass);
        this.assign(val, col, o, meth);
        for (Element el : XmlUtil.getElementsArray(subroot)) {
            this.populate(el, val, null, null);
        }
    }

    private Method findSetter(Object val, String name) throws Throwable {
        String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        Method[] meths = val.getClass().getMethods();
        Method meth = null;
        for (int i = 0; i < meths.length; ++i) {
            Method m = meths[i];
            ConfInfo ci = m.getAnnotation(ConfInfo.class);
            if (ci != null && ci.dontSave() || !m.getName().equals(methodName)) continue;
            if (meth != null) {
                throw new ConfigException("Multiple setters for field " + name);
            }
            meth = m;
        }
        if (meth == null) {
            this.error("No setter method for property " + name + " for class " + val.getClass().getName());
            return null;
        }
        return meth;
    }

    private void assign(Object val, Collection<Object> col, Object o, Method meth) throws Throwable {
        if (col != null) {
            col.add(val);
        } else {
            Object[] pars = new Object[]{val};
            meth.invoke(o, pars);
        }
    }

    private Object simpleValue(Class cl, Element el, String name) throws Throwable {
        if (XmlUtil.hasChildren(el)) {
            return null;
        }
        String ndval = XmlUtil.getElementContent(el);
        if (ndval.length() == 0) {
            return null;
        }
        if (cl.getName().equals("java.lang.String")) {
            return Util.propertyReplace(ndval, new Util.PropertiesPropertyFetcher(System.getProperties()));
        }
        if (cl.getName().equals("int") || cl.getName().equals("java.lang.Integer")) {
            return Integer.valueOf(ndval);
        }
        if (cl.getName().equals("long") || cl.getName().equals("java.lang.Long")) {
            return Long.valueOf(ndval);
        }
        if (cl.getName().equals("boolean") || cl.getName().equals("java.lang.Boolean")) {
            return Boolean.valueOf(ndval);
        }
        this.error("Unsupported par class " + cl + " for field " + name);
        throw new ConfigException("Unsupported par class " + cl + " for field " + name);
    }

    private void dump(XmlEmit xml, boolean fromCollection) throws Throwable {
        Class c;
        Class thisClass = this.getClass();
        ConfInfo ciCl = thisClass.getAnnotation(ConfInfo.class);
        Class defClass = thisClass;
        String defClassName = null;
        if (ciCl != null && ciCl.type().length() != 0) {
            defClassName = ciCl.type();
        }
        if (defClassName != null && !defClassName.equals(thisClass.getCanonicalName()) && (c = this.findClass(thisClass, defClassName)) != null) {
            defClass = c;
        }
        QName qn = this.startElement(xml, thisClass, ciCl);
        Collection<ComparableMethod> ms = this.findGetters(defClass);
        for (ComparableMethod cm : ms) {
            Method m = cm.m;
            ConfInfo ci = m.getAnnotation(ConfInfo.class);
            this.dumpValue(xml, m, ci, m.invoke((Object)this, (Object[])null), fromCollection);
        }
        if (qn != null) {
            this.closeElement(xml, qn);
        }
    }

    private Class findClass(Class cl, String cname) throws Throwable {
        for (Class<?> c : cl.getInterfaces()) {
            if (c.getCanonicalName().equals(cname)) {
                return c;
            }
            Class ic = this.findClass(c, cname);
            if (ic == null) continue;
            return ic;
        }
        Class c = cl.getSuperclass();
        if (c == null) {
            return null;
        }
        if (c.getCanonicalName().equals(cname)) {
            return c;
        }
        return this.findClass(c, cname);
    }

    private QName startElement(XmlEmit xml, Class c, ConfInfo ci) throws Throwable {
        QName qn = ci == null ? new QName(ns, c.getName()) : new QName(ns, ci.elementName());
        xml.openTag(qn, "type", c.getCanonicalName());
        return qn;
    }

    private QName startElement(XmlEmit xml, Method m, ConfInfo ci, boolean fromCollection) throws Throwable {
        QName qn = this.getTag(m, ci, fromCollection);
        if (qn != null) {
            xml.openTag(qn);
        }
        return qn;
    }

    private QName getTag(Method m, ConfInfo ci, boolean fromCollection) {
        String tagName = null;
        if (ci != null) {
            if (!fromCollection) {
                if (ci.elementName().length() > 0) {
                    tagName = ci.elementName();
                }
            } else if (ci.collectionElementName().length() > 0) {
                tagName = ci.collectionElementName();
            }
        }
        if (tagName == null && !fromCollection) {
            tagName = this.fieldName(m.getName());
        }
        if (tagName == null) {
            return null;
        }
        return new QName(tagName);
    }

    private String fieldName(String val) {
        if (val.length() < 4) {
            return null;
        }
        return val.substring(3, 4).toLowerCase() + val.substring(4);
    }

    private void closeElement(XmlEmit xml, QName qn) throws Throwable {
        xml.closeTag(qn);
    }

    private boolean dumpValue(XmlEmit xml, Method m, ConfInfo ci, Object methVal, boolean fromCollection) throws Throwable {
        if (methVal instanceof ConfigBase) {
            ConfigBase de = (ConfigBase)methVal;
            QName mqn = this.startElement(xml, m, ci, fromCollection);
            de.dump(xml, false);
            if (mqn != null) {
                this.closeElement(xml, mqn);
            }
            return true;
        }
        if (methVal instanceof Collection) {
            Collection c = (Collection)methVal;
            if (c.isEmpty()) {
                return false;
            }
            QName mqn = null;
            for (Object o : c) {
                if (mqn == null) {
                    mqn = this.startElement(xml, m, ci, fromCollection);
                }
                this.dumpValue(xml, m, ci, o, true);
            }
            if (mqn != null) {
                this.closeElement(xml, mqn);
            }
            return true;
        }
        this.property(xml, m, ci, methVal, fromCollection);
        return true;
    }

    private void property(XmlEmit xml, Method m, ConfInfo d, Object p, boolean fromCollection) throws ConfigException {
        if (p == null) {
            return;
        }
        try {
            String sval;
            QName qn = this.getTag(m, d, fromCollection);
            if (qn == null) {
                qn = new QName(p.getClass().getName());
            }
            if ((sval = p instanceof char[] ? new String((char[])p) : String.valueOf(p)).indexOf(38) < 0 && sval.indexOf(60) < 0) {
                xml.property(qn, sval);
            } else {
                xml.cdataProperty(qn, sval);
            }
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    private Collection<ComparableMethod> findGetters(Class cl) throws ConfigException {
        Method[] meths = cl.getMethods();
        TreeSet<ComparableMethod> getters = new TreeSet<ComparableMethod>();
        for (int i = 0; i < meths.length; ++i) {
            Class<?>[] parClasses;
            String mname;
            Method m = meths[i];
            ConfInfo ci = m.getAnnotation(ConfInfo.class);
            if (ci != null && ci.dontSave() || (mname = m.getName()).length() < 4 || !mname.startsWith("get") || mname.equals("getClass") || (parClasses = m.getParameterTypes()).length != 0) continue;
            getters.add(new ComparableMethod(m));
        }
        return getters;
    }

    private static class ComparableMethod
    implements Comparable<ComparableMethod> {
        Method m;

        ComparableMethod(Method m) {
            this.m = m;
        }

        @Override
        public int compareTo(ComparableMethod that) {
            return this.m.getName().compareTo(that.m.getName());
        }
    }
}

