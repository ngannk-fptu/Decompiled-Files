/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.OutputPropertiesFactory
 *  org.apache.xml.serializer.OutputPropertyUtils
 */
package org.apache.xalan.templates;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.OutputPropertyUtils;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.QName;

public class OutputProperties
extends ElemTemplateElement
implements Cloneable {
    static final long serialVersionUID = -6975274363881785488L;
    private Properties m_properties = null;

    public OutputProperties() {
        this("xml");
    }

    public OutputProperties(Properties defaults) {
        this.m_properties = new Properties(defaults);
    }

    public OutputProperties(String method) {
        this.m_properties = new Properties(OutputPropertiesFactory.getDefaultMethodProperties((String)method));
    }

    public Object clone() {
        try {
            OutputProperties cloned = (OutputProperties)super.clone();
            cloned.m_properties = (Properties)cloned.m_properties.clone();
            return cloned;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void setProperty(QName key, String value) {
        this.setProperty(key.toNamespacedString(), value);
    }

    public void setProperty(String key, String value) {
        if (key.equals("method")) {
            this.setMethodDefaults(value);
        }
        if (key.startsWith("{http://xml.apache.org/xslt}")) {
            key = "{http://xml.apache.org/xalan}" + key.substring(OutputPropertiesFactory.S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN);
        }
        this.m_properties.put(key, value);
    }

    public String getProperty(QName key) {
        return this.m_properties.getProperty(key.toNamespacedString());
    }

    public String getProperty(String key) {
        if (key.startsWith("{http://xml.apache.org/xslt}")) {
            key = "{http://xml.apache.org/xalan}" + key.substring(OutputPropertiesFactory.S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN);
        }
        return this.m_properties.getProperty(key);
    }

    public void setBooleanProperty(QName key, boolean value) {
        this.m_properties.put(key.toNamespacedString(), value ? "yes" : "no");
    }

    public void setBooleanProperty(String key, boolean value) {
        this.m_properties.put(key, value ? "yes" : "no");
    }

    public boolean getBooleanProperty(QName key) {
        return this.getBooleanProperty(key.toNamespacedString());
    }

    public boolean getBooleanProperty(String key) {
        return OutputPropertyUtils.getBooleanProperty((String)key, (Properties)this.m_properties);
    }

    public void setIntProperty(QName key, int value) {
        this.setIntProperty(key.toNamespacedString(), value);
    }

    public void setIntProperty(String key, int value) {
        this.m_properties.put(key, Integer.toString(value));
    }

    public int getIntProperty(QName key) {
        return this.getIntProperty(key.toNamespacedString());
    }

    public int getIntProperty(String key) {
        return OutputPropertyUtils.getIntProperty((String)key, (Properties)this.m_properties);
    }

    public void setQNameProperty(QName key, QName value) {
        this.setQNameProperty(key.toNamespacedString(), value);
    }

    public void setMethodDefaults(String method) {
        String defaultMethod = this.m_properties.getProperty("method");
        if (null == defaultMethod || !defaultMethod.equals(method) || defaultMethod.equals("xml")) {
            Properties savedProps = this.m_properties;
            Properties newDefaults = OutputPropertiesFactory.getDefaultMethodProperties((String)method);
            this.m_properties = new Properties(newDefaults);
            this.copyFrom(savedProps, false);
        }
    }

    public void setQNameProperty(String key, QName value) {
        this.setProperty(key, value.toNamespacedString());
    }

    public QName getQNameProperty(QName key) {
        return this.getQNameProperty(key.toNamespacedString());
    }

    public QName getQNameProperty(String key) {
        return OutputProperties.getQNameProperty(key, this.m_properties);
    }

    public static QName getQNameProperty(String key, Properties props) {
        String s = props.getProperty(key);
        if (null != s) {
            return QName.getQNameFromString(s);
        }
        return null;
    }

    public void setQNameProperties(QName key, Vector v) {
        this.setQNameProperties(key.toNamespacedString(), v);
    }

    public void setQNameProperties(String key, Vector v) {
        int s = v.size();
        FastStringBuffer fsb = new FastStringBuffer(9, 9);
        for (int i = 0; i < s; ++i) {
            QName qname = (QName)v.elementAt(i);
            fsb.append(qname.toNamespacedString());
            if (i >= s - 1) continue;
            fsb.append(' ');
        }
        this.m_properties.put(key, fsb.toString());
    }

    public Vector getQNameProperties(QName key) {
        return this.getQNameProperties(key.toNamespacedString());
    }

    public Vector getQNameProperties(String key) {
        return OutputProperties.getQNameProperties(key, this.m_properties);
    }

    public static Vector getQNameProperties(String key, Properties props) {
        String s = props.getProperty(key);
        if (null != s) {
            Vector<QName> v = new Vector<QName>();
            int l = s.length();
            boolean inCurly = false;
            FastStringBuffer buf = new FastStringBuffer();
            for (int i = 0; i < l; ++i) {
                char c = s.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (!inCurly) {
                        if (buf.length() <= 0) continue;
                        QName qname = QName.getQNameFromString(buf.toString());
                        v.addElement(qname);
                        buf.reset();
                        continue;
                    }
                } else if ('{' == c) {
                    inCurly = true;
                } else if ('}' == c) {
                    inCurly = false;
                }
                buf.append(c);
            }
            if (buf.length() > 0) {
                QName qname = QName.getQNameFromString(buf.toString());
                v.addElement(qname);
                buf.reset();
            }
            return v;
        }
        return null;
    }

    @Override
    public void recompose(StylesheetRoot root) throws TransformerException {
        root.recomposeOutput(this);
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
    }

    public Properties getProperties() {
        return this.m_properties;
    }

    public void copyFrom(Properties src) {
        this.copyFrom(src, true);
    }

    public void copyFrom(Properties src, boolean shouldResetDefaults) {
        Enumeration<Object> keys = src.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (!OutputProperties.isLegalPropertyKey(key)) {
                throw new IllegalArgumentException(XSLMessages.createMessage("ER_OUTPUT_PROPERTY_NOT_RECOGNIZED", new Object[]{key}));
            }
            Object oldValue = this.m_properties.get(key);
            if (null == oldValue) {
                String val = (String)src.get(key);
                if (shouldResetDefaults && key.equals("method")) {
                    this.setMethodDefaults(val);
                }
                this.m_properties.put(key, val);
                continue;
            }
            if (!key.equals("cdata-section-elements")) continue;
            this.m_properties.put(key, (String)oldValue + " " + (String)src.get(key));
        }
    }

    public void copyFrom(OutputProperties opsrc) throws TransformerException {
        this.copyFrom(opsrc.getProperties());
    }

    public static boolean isLegalPropertyKey(String key) {
        return key.equals("cdata-section-elements") || key.equals("doctype-public") || key.equals("doctype-system") || key.equals("encoding") || key.equals("indent") || key.equals("media-type") || key.equals("method") || key.equals("omit-xml-declaration") || key.equals("standalone") || key.equals("version") || key.length() > 0 && key.charAt(0) == '{' && key.lastIndexOf(123) == 0 && key.indexOf(125) > 0 && key.lastIndexOf(125) == key.indexOf(125);
    }

    public static Properties getDefaultMethodProperties(String method) {
        return OutputPropertiesFactory.getDefaultMethodProperties((String)method);
    }
}

