/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime.definition;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.loadtime.definition.SimpleAOPParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class DocumentParser
extends DefaultHandler {
    private static final String DTD_PUBLIC_ID = "-//AspectJ//DTD 1.5.0//EN";
    private static final String DTD_PUBLIC_ID_ALIAS = "-//AspectJ//DTD//EN";
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
    private static final String BEFORE_ELEMENT = "before";
    private static final String AFTER_ELEMENT = "after";
    private static final String AFTER_RETURNING_ELEMENT = "after-returning";
    private static final String AFTER_THROWING_ELEMENT = "after-throwing";
    private static final String AROUND_ELEMENT = "around";
    private static final String WITHIN_ATTRIBUTE = "within";
    private static final String EXPRESSION_ATTRIBUTE = "expression";
    private static final String DECLARE_ANNOTATION_ELEMENT = "declare-annotation";
    private final Definition definition = new Definition();
    private boolean inAspectJ;
    private boolean inWeaver;
    private boolean inAspects;
    private Definition.ConcreteAspect activeConcreteAspectDefinition;
    private static Hashtable<String, Definition> parsedFiles = new Hashtable();
    private static boolean CACHE;
    private static final boolean LIGHTPARSER;

    private DocumentParser() {
    }

    public static Definition parse(URL url) throws Exception {
        if (CACHE && parsedFiles.containsKey(url.toString())) {
            return parsedFiles.get(url.toString());
        }
        Definition def = null;
        def = LIGHTPARSER ? SimpleAOPParser.parse(url) : DocumentParser.saxParsing(url);
        if (CACHE && def.getAspectClassNames().size() > 0) {
            parsedFiles.put(url.toString(), def);
        }
        return def;
    }

    private static Definition saxParsing(URL url) throws SAXException, ParserConfigurationException, IOException {
        DocumentParser parser = new DocumentParser();
        XMLReader xmlReader = DocumentParser.getXMLReader();
        xmlReader.setContentHandler(parser);
        xmlReader.setErrorHandler(parser);
        try {
            xmlReader.setFeature("http://xml.org/sax/features/validation", false);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        try {
            xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        try {
            xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        xmlReader.setEntityResolver(parser);
        InputStream in = url.openStream();
        xmlReader.parse(new InputSource(in));
        return parser.definition;
    }

    private static XMLReader getXMLReader() throws SAXException, ParserConfigurationException {
        XMLReader xmlReader = null;
        try {
            xmlReader = XMLReaderFactory.createXMLReader();
        }
        catch (SAXException ex) {
            xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        }
        return xmlReader;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if (publicId.equals(DTD_PUBLIC_ID) || publicId.equals(DTD_PUBLIC_ID_ALIAS)) {
            InputStream in = DocumentParser.class.getResourceAsStream("/aspectj_1_5_0.dtd");
            if (in == null) {
                System.err.println("AspectJ - WARN - could not read DTD " + publicId);
                return null;
            }
            return new InputSource(in);
        }
        System.err.println("AspectJ - WARN - unknown DTD " + publicId + " - consider using " + DTD_PUBLIC_ID);
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (ASPECT_ELEMENT.equals(qName)) {
            String name = attributes.getValue(NAME_ATTRIBUTE);
            String scopePattern = DocumentParser.replaceXmlAnd(attributes.getValue(SCOPE_ATTRIBUTE));
            String requiredType = attributes.getValue(REQUIRES_ATTRIBUTE);
            if (!this.isNull(name)) {
                this.definition.getAspectClassNames().add(name);
                if (scopePattern != null) {
                    this.definition.addScopedAspect(name, scopePattern);
                }
                if (requiredType != null) {
                    this.definition.setAspectRequires(name, requiredType);
                }
            }
        } else if (WEAVER_ELEMENT.equals(qName)) {
            String options = attributes.getValue(OPTIONS_ATTRIBUTE);
            if (!this.isNull(options)) {
                this.definition.appendWeaverOptions(options);
            }
            this.inWeaver = true;
        } else if (CONCRETE_ASPECT_ELEMENT.equals(qName)) {
            String name = attributes.getValue(NAME_ATTRIBUTE);
            String extend = attributes.getValue(EXTEND_ATTRIBUTE);
            String precedence = attributes.getValue(PRECEDENCE_ATTRIBUTE);
            String perclause = attributes.getValue(PERCLAUSE_ATTRIBUTE);
            if (!this.isNull(name)) {
                this.activeConcreteAspectDefinition = new Definition.ConcreteAspect(name, extend, precedence, perclause);
                this.definition.getConcreteAspects().add(this.activeConcreteAspectDefinition);
            }
        } else if (POINTCUT_ELEMENT.equals(qName) && this.activeConcreteAspectDefinition != null) {
            String name = attributes.getValue(NAME_ATTRIBUTE);
            String expression = attributes.getValue(EXPRESSION_ATTRIBUTE);
            if (!this.isNull(name) && !this.isNull(expression)) {
                this.activeConcreteAspectDefinition.pointcuts.add(new Definition.Pointcut(name, DocumentParser.replaceXmlAnd(expression)));
            }
        } else if (DECLARE_ANNOTATION_ELEMENT.equals(qName) && this.activeConcreteAspectDefinition != null) {
            String methodSig = attributes.getValue("method");
            String fieldSig = attributes.getValue("field");
            String typePat = attributes.getValue("type");
            String anno = attributes.getValue("annotation");
            if (this.isNull(anno)) {
                throw new SAXException("Badly formed <declare-annotation> element, 'annotation' value is missing");
            }
            if (this.isNull(methodSig) && this.isNull(fieldSig) && this.isNull(typePat)) {
                throw new SAXException("Badly formed <declare-annotation> element, need one of 'method'/'field'/'type' specified");
            }
            if (!this.isNull(methodSig)) {
                this.activeConcreteAspectDefinition.declareAnnotations.add(new Definition.DeclareAnnotation(Definition.DeclareAnnotationKind.Method, methodSig, anno));
            } else if (!this.isNull(fieldSig)) {
                this.activeConcreteAspectDefinition.declareAnnotations.add(new Definition.DeclareAnnotation(Definition.DeclareAnnotationKind.Field, fieldSig, anno));
            } else if (!this.isNull(typePat)) {
                this.activeConcreteAspectDefinition.declareAnnotations.add(new Definition.DeclareAnnotation(Definition.DeclareAnnotationKind.Type, typePat, anno));
            }
        } else if (BEFORE_ELEMENT.equals(qName) && this.activeConcreteAspectDefinition != null) {
            String pointcut = attributes.getValue(POINTCUT_ELEMENT);
            String adviceClass = attributes.getValue("invokeClass");
            String adviceMethod = attributes.getValue("invokeMethod");
            if (this.isNull(pointcut) || this.isNull(adviceClass) || this.isNull(adviceMethod)) throw new SAXException("Badly formed <before> element");
            this.activeConcreteAspectDefinition.pointcutsAndAdvice.add(new Definition.PointcutAndAdvice(Definition.AdviceKind.Before, DocumentParser.replaceXmlAnd(pointcut), adviceClass, adviceMethod));
        } else if (AFTER_ELEMENT.equals(qName) && this.activeConcreteAspectDefinition != null) {
            String pointcut = attributes.getValue(POINTCUT_ELEMENT);
            String adviceClass = attributes.getValue("invokeClass");
            String adviceMethod = attributes.getValue("invokeMethod");
            if (this.isNull(pointcut) || this.isNull(adviceClass) || this.isNull(adviceMethod)) throw new SAXException("Badly formed <after> element");
            this.activeConcreteAspectDefinition.pointcutsAndAdvice.add(new Definition.PointcutAndAdvice(Definition.AdviceKind.After, DocumentParser.replaceXmlAnd(pointcut), adviceClass, adviceMethod));
        } else if (AROUND_ELEMENT.equals(qName) && this.activeConcreteAspectDefinition != null) {
            String pointcut = attributes.getValue(POINTCUT_ELEMENT);
            String adviceClass = attributes.getValue("invokeClass");
            String adviceMethod = attributes.getValue("invokeMethod");
            if (this.isNull(pointcut) || this.isNull(adviceClass) || this.isNull(adviceMethod)) throw new SAXException("Badly formed <before> element");
            this.activeConcreteAspectDefinition.pointcutsAndAdvice.add(new Definition.PointcutAndAdvice(Definition.AdviceKind.Around, DocumentParser.replaceXmlAnd(pointcut), adviceClass, adviceMethod));
        } else if (ASPECTJ_ELEMENT.equals(qName)) {
            if (this.inAspectJ) {
                throw new SAXException("Found nested <aspectj> element");
            }
            this.inAspectJ = true;
        } else if (ASPECTS_ELEMENT.equals(qName)) {
            this.inAspects = true;
        } else if (INCLUDE_ELEMENT.equals(qName) && this.inWeaver) {
            String typePattern = this.getWithinAttribute(attributes);
            if (!this.isNull(typePattern)) {
                this.definition.getIncludePatterns().add(typePattern);
            }
        } else if (EXCLUDE_ELEMENT.equals(qName) && this.inWeaver) {
            String typePattern = this.getWithinAttribute(attributes);
            if (!this.isNull(typePattern)) {
                this.definition.getExcludePatterns().add(typePattern);
            }
        } else if (DUMP_ELEMENT.equals(qName) && this.inWeaver) {
            String perWeaverDumpDir;
            String beforeAndAfter;
            String typePattern = this.getWithinAttribute(attributes);
            if (!this.isNull(typePattern)) {
                this.definition.getDumpPatterns().add(typePattern);
            }
            if (this.isTrue(beforeAndAfter = attributes.getValue(DUMP_BEFOREANDAFTER_ATTRIBUTE))) {
                this.definition.setDumpBefore(true);
            }
            if (this.isTrue(perWeaverDumpDir = attributes.getValue(DUMP_PERCLASSLOADERDIR_ATTRIBUTE))) {
                this.definition.setCreateDumpDirPerClassloader(true);
            }
        } else if (EXCLUDE_ELEMENT.equals(qName) && this.inAspects) {
            String typePattern = this.getWithinAttribute(attributes);
            if (!this.isNull(typePattern)) {
                this.definition.getAspectExcludePatterns().add(typePattern);
            }
        } else {
            if (!INCLUDE_ELEMENT.equals(qName) || !this.inAspects) throw new SAXException("Unknown element while parsing <aspectj> element: " + qName);
            String typePattern = this.getWithinAttribute(attributes);
            if (!this.isNull(typePattern)) {
                this.definition.getAspectIncludePatterns().add(typePattern);
            }
        }
        super.startElement(uri, localName, qName, attributes);
    }

    private String getWithinAttribute(Attributes attributes) {
        return DocumentParser.replaceXmlAnd(attributes.getValue(WITHIN_ATTRIBUTE));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (CONCRETE_ASPECT_ELEMENT.equals(qName)) {
            this.activeConcreteAspectDefinition = null;
        } else if (ASPECTJ_ELEMENT.equals(qName)) {
            this.inAspectJ = false;
        } else if (WEAVER_ELEMENT.equals(qName)) {
            this.inWeaver = false;
        } else if (ASPECTS_ELEMENT.equals(qName)) {
            this.inAspects = false;
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        super.warning(e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        super.error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        super.fatalError(e);
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

    public static void deactivateCaching() {
        CACHE = false;
    }

    static {
        boolean value = false;
        try {
            value = System.getProperty("org.aspectj.weaver.loadtime.configuration.cache", "true").equalsIgnoreCase("true");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        CACHE = value;
        value = false;
        try {
            value = System.getProperty("org.aspectj.weaver.loadtime.configuration.lightxmlparser", "false").equalsIgnoreCase("true");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        LIGHTPARSER = value;
    }
}

