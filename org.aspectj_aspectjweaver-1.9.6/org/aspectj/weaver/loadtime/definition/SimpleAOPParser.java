/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime.definition;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.loadtime.definition.LightXMLParser;
import org.xml.sax.SAXException;

public class SimpleAOPParser {
    private static final String ASPECTJ_ELEMENT = "aspectj";
    private static final String WEAVER_ELEMENT = "weaver";
    private static final String DUMP_ELEMENT = "dump";
    private static final String DUMP_BEFOREANDAFTER_ATTRIBUTE = "beforeandafter";
    private static final String DUMP_PERCLASSLOADERDIR_ATTRIBUTE = "perclassloaderdumpdir";
    private static final String INCLUDE_ELEMENT = "include";
    private static final String EXCLUDE_ELEMENT = "exclude";
    private static final String OPTIONS_ATTRIBUTE = "options";
    private static final String ASPECTS_ELEMENT = "aspects";
    private static final String ASPECT_ELEMENT = "aspect";
    private static final String CONCRETE_ASPECT_ELEMENT = "concrete-aspect";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SCOPE_ATTRIBUTE = "scope";
    private static final String REQUIRES_ATTRIBUTE = "requires";
    private static final String EXTEND_ATTRIBUTE = "extends";
    private static final String PRECEDENCE_ATTRIBUTE = "precedence";
    private static final String PERCLAUSE_ATTRIBUTE = "perclause";
    private static final String POINTCUT_ELEMENT = "pointcut";
    private static final String WITHIN_ATTRIBUTE = "within";
    private static final String EXPRESSION_ATTRIBUTE = "expression";
    private static final String DECLARE_ANNOTATION = "declare-annotation";
    private static final String ANNONATION_TAG = "annotation";
    private static final String ANNO_KIND_TYPE = "type";
    private static final String ANNO_KIND_METHOD = "method";
    private static final String ANNO_KIND_FIELD = "field";
    private static final String BEFORE_ELEMENT = "before";
    private static final String AFTER_ELEMENT = "after";
    private static final String AROUND_ELEMENT = "around";
    private final Definition m_definition = new Definition();
    private boolean m_inAspectJ;
    private boolean m_inWeaver;
    private boolean m_inAspects;
    private Definition.ConcreteAspect m_lastConcreteAspect;

    private SimpleAOPParser() {
    }

    public static Definition parse(URL url) throws Exception {
        InputStream in = url.openStream();
        LightXMLParser xml = new LightXMLParser();
        xml.parseFromReader(new InputStreamReader(in));
        SimpleAOPParser sap = new SimpleAOPParser();
        SimpleAOPParser.traverse(sap, xml);
        return sap.m_definition;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void startElement(String qName, Map attrMap) throws Exception {
        if (ASPECT_ELEMENT.equals(qName)) {
            String name = (String)attrMap.get(NAME_ATTRIBUTE);
            String scopePattern = SimpleAOPParser.replaceXmlAnd((String)attrMap.get(SCOPE_ATTRIBUTE));
            String requiredType = (String)attrMap.get(REQUIRES_ATTRIBUTE);
            if (this.isNull(name)) return;
            this.m_definition.getAspectClassNames().add(name);
            if (scopePattern != null) {
                this.m_definition.addScopedAspect(name, scopePattern);
            }
            if (requiredType == null) return;
            this.m_definition.setAspectRequires(name, requiredType);
            return;
        } else if (WEAVER_ELEMENT.equals(qName)) {
            String options = (String)attrMap.get(OPTIONS_ATTRIBUTE);
            if (!this.isNull(options)) {
                this.m_definition.appendWeaverOptions(options);
            }
            this.m_inWeaver = true;
            return;
        } else if (CONCRETE_ASPECT_ELEMENT.equals(qName)) {
            String name = (String)attrMap.get(NAME_ATTRIBUTE);
            String extend = (String)attrMap.get(EXTEND_ATTRIBUTE);
            String precedence = (String)attrMap.get(PRECEDENCE_ATTRIBUTE);
            String perclause = (String)attrMap.get(PERCLAUSE_ATTRIBUTE);
            if (this.isNull(name)) return;
            this.m_lastConcreteAspect = new Definition.ConcreteAspect(name, extend, precedence, perclause);
            this.m_definition.getConcreteAspects().add(this.m_lastConcreteAspect);
            return;
        } else if (POINTCUT_ELEMENT.equals(qName) && this.m_lastConcreteAspect != null) {
            String name = (String)attrMap.get(NAME_ATTRIBUTE);
            String expression = (String)attrMap.get(EXPRESSION_ATTRIBUTE);
            if (this.isNull(name) || this.isNull(expression)) return;
            this.m_lastConcreteAspect.pointcuts.add(new Definition.Pointcut(name, SimpleAOPParser.replaceXmlAnd(expression)));
            return;
        } else if (ASPECTJ_ELEMENT.equals(qName)) {
            if (this.m_inAspectJ) {
                throw new Exception("Found nested <aspectj> element");
            }
            this.m_inAspectJ = true;
            return;
        } else if (ASPECTS_ELEMENT.equals(qName)) {
            this.m_inAspects = true;
            return;
        } else if (INCLUDE_ELEMENT.equals(qName) && this.m_inWeaver) {
            String typePattern = this.getWithinAttribute(attrMap);
            if (this.isNull(typePattern)) return;
            this.m_definition.getIncludePatterns().add(typePattern);
            return;
        } else if (EXCLUDE_ELEMENT.equals(qName) && this.m_inWeaver) {
            String typePattern = this.getWithinAttribute(attrMap);
            if (this.isNull(typePattern)) return;
            this.m_definition.getExcludePatterns().add(typePattern);
            return;
        } else if (DUMP_ELEMENT.equals(qName) && this.m_inWeaver) {
            String perWeaverDumpDir;
            String beforeAndAfter;
            String typePattern = this.getWithinAttribute(attrMap);
            if (!this.isNull(typePattern)) {
                this.m_definition.getDumpPatterns().add(typePattern);
            }
            if (this.isTrue(beforeAndAfter = (String)attrMap.get(DUMP_BEFOREANDAFTER_ATTRIBUTE))) {
                this.m_definition.setDumpBefore(true);
            }
            if (!this.isTrue(perWeaverDumpDir = (String)attrMap.get(DUMP_PERCLASSLOADERDIR_ATTRIBUTE))) return;
            this.m_definition.setCreateDumpDirPerClassloader(true);
            return;
        } else if (EXCLUDE_ELEMENT.equals(qName) && this.m_inAspects) {
            String typePattern = this.getWithinAttribute(attrMap);
            if (this.isNull(typePattern)) return;
            this.m_definition.getAspectExcludePatterns().add(typePattern);
            return;
        } else if (INCLUDE_ELEMENT.equals(qName) && this.m_inAspects) {
            String typePattern = this.getWithinAttribute(attrMap);
            if (this.isNull(typePattern)) return;
            this.m_definition.getAspectIncludePatterns().add(typePattern);
            return;
        } else if (DECLARE_ANNOTATION.equals(qName) && this.m_inAspects) {
            String anno = (String)attrMap.get(ANNONATION_TAG);
            if (this.isNull(anno)) return;
            String pattern = (String)attrMap.get(ANNO_KIND_FIELD);
            if (pattern != null) {
                this.m_lastConcreteAspect.declareAnnotations.add(new Definition.DeclareAnnotation(Definition.DeclareAnnotationKind.Field, pattern, anno));
                return;
            } else {
                pattern = (String)attrMap.get(ANNO_KIND_METHOD);
                if (pattern != null) {
                    this.m_lastConcreteAspect.declareAnnotations.add(new Definition.DeclareAnnotation(Definition.DeclareAnnotationKind.Method, pattern, anno));
                    return;
                } else {
                    pattern = (String)attrMap.get(ANNO_KIND_TYPE);
                    if (pattern == null) return;
                    this.m_lastConcreteAspect.declareAnnotations.add(new Definition.DeclareAnnotation(Definition.DeclareAnnotationKind.Type, pattern, anno));
                }
            }
            return;
        } else if (BEFORE_ELEMENT.equals(qName) && this.m_inAspects) {
            String pointcut = (String)attrMap.get(POINTCUT_ELEMENT);
            String adviceClass = (String)attrMap.get("invokeClass");
            String adviceMethod = (String)attrMap.get("invokeMethod");
            if (this.isNull(pointcut) || this.isNull(adviceClass) || this.isNull(adviceMethod)) throw new SAXException("Badly formed <before> element");
            this.m_lastConcreteAspect.pointcutsAndAdvice.add(new Definition.PointcutAndAdvice(Definition.AdviceKind.Before, SimpleAOPParser.replaceXmlAnd(pointcut), adviceClass, adviceMethod));
            return;
        } else if (AFTER_ELEMENT.equals(qName) && this.m_inAspects) {
            String pointcut = (String)attrMap.get(POINTCUT_ELEMENT);
            String adviceClass = (String)attrMap.get("invokeClass");
            String adviceMethod = (String)attrMap.get("invokeMethod");
            if (this.isNull(pointcut) || this.isNull(adviceClass) || this.isNull(adviceMethod)) throw new SAXException("Badly formed <after> element");
            this.m_lastConcreteAspect.pointcutsAndAdvice.add(new Definition.PointcutAndAdvice(Definition.AdviceKind.After, SimpleAOPParser.replaceXmlAnd(pointcut), adviceClass, adviceMethod));
            return;
        } else {
            if (!AROUND_ELEMENT.equals(qName) || !this.m_inAspects) throw new Exception("Unknown element while parsing <aspectj> element: " + qName);
            String pointcut = (String)attrMap.get(POINTCUT_ELEMENT);
            String adviceClass = (String)attrMap.get("invokeClass");
            String adviceMethod = (String)attrMap.get("invokeMethod");
            if (this.isNull(pointcut) || this.isNull(adviceClass) || this.isNull(adviceMethod)) return;
            this.m_lastConcreteAspect.pointcutsAndAdvice.add(new Definition.PointcutAndAdvice(Definition.AdviceKind.Around, SimpleAOPParser.replaceXmlAnd(pointcut), adviceClass, adviceMethod));
        }
    }

    private void endElement(String qName) throws Exception {
        if (CONCRETE_ASPECT_ELEMENT.equals(qName)) {
            this.m_lastConcreteAspect = null;
        } else if (ASPECTJ_ELEMENT.equals(qName)) {
            this.m_inAspectJ = false;
        } else if (WEAVER_ELEMENT.equals(qName)) {
            this.m_inWeaver = false;
        } else if (ASPECTS_ELEMENT.equals(qName)) {
            this.m_inAspects = false;
        }
    }

    private String getWithinAttribute(Map attributes) {
        return SimpleAOPParser.replaceXmlAnd((String)attributes.get(WITHIN_ATTRIBUTE));
    }

    private static String replaceXmlAnd(String expression) {
        return LangUtil.replace(expression, " AND ", " && ");
    }

    private boolean isNull(String s) {
        return s == null || s.length() <= 0;
    }

    private boolean isTrue(String s) {
        return s != null && s.equals("true");
    }

    private static void traverse(SimpleAOPParser sap, LightXMLParser xml) throws Exception {
        sap.startElement(xml.getName(), xml.getAttributes());
        ArrayList childrens = xml.getChildrens();
        for (int i = 0; i < childrens.size(); ++i) {
            LightXMLParser child = (LightXMLParser)childrens.get(i);
            SimpleAOPParser.traverse(sap, child);
        }
        sap.endElement(xml.getName());
    }
}

