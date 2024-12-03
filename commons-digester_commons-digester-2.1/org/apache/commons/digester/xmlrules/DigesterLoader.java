/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.xmlrules;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoadingException;
import org.apache.commons.digester.xmlrules.FromXmlRuleSet;
import org.apache.commons.digester.xmlrules.XmlLoadException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DigesterLoader {
    public static Digester createDigester(InputSource rulesSource) {
        FromXmlRuleSet ruleSet = new FromXmlRuleSet(rulesSource);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    public static Digester createDigester(InputSource rulesSource, Digester rulesDigester) {
        FromXmlRuleSet ruleSet = new FromXmlRuleSet(rulesSource, rulesDigester);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    public static Digester createDigester(URL rulesXml) {
        FromXmlRuleSet ruleSet = new FromXmlRuleSet(rulesXml);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    public static Digester createDigester(URL rulesXml, Digester rulesDigester) {
        FromXmlRuleSet ruleSet = new FromXmlRuleSet(rulesXml, rulesDigester);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    public static Object load(URL digesterRules, ClassLoader classLoader, URL fileURL) throws IOException, SAXException, DigesterLoadingException {
        return DigesterLoader.load(digesterRules, classLoader, fileURL.openStream());
    }

    public static Object load(URL digesterRules, ClassLoader classLoader, InputStream input) throws IOException, SAXException, DigesterLoadingException {
        Digester digester = DigesterLoader.createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        try {
            return digester.parse(input);
        }
        catch (XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }

    public static Object load(URL digesterRules, ClassLoader classLoader, Reader reader) throws IOException, SAXException, DigesterLoadingException {
        Digester digester = DigesterLoader.createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        try {
            return digester.parse(reader);
        }
        catch (XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }

    public static Object load(URL digesterRules, ClassLoader classLoader, URL fileURL, Object rootObject) throws IOException, SAXException, DigesterLoadingException {
        return DigesterLoader.load(digesterRules, classLoader, fileURL.openStream(), rootObject);
    }

    public static Object load(URL digesterRules, ClassLoader classLoader, InputStream input, Object rootObject) throws IOException, SAXException, DigesterLoadingException {
        Digester digester = DigesterLoader.createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        digester.push(rootObject);
        try {
            return digester.parse(input);
        }
        catch (XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }

    public static Object load(URL digesterRules, ClassLoader classLoader, Reader input, Object rootObject) throws IOException, SAXException, DigesterLoadingException {
        Digester digester = DigesterLoader.createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        digester.push(rootObject);
        try {
            return digester.parse(input);
        }
        catch (XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
}

