/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.bnd.annotation.xml.XMLAttribute;
import aQute.bnd.metatype.DesignateDef;
import aQute.bnd.metatype.OCDDef;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import java.util.Arrays;
import java.util.Map;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

public class DesignateReader
extends ClassDataCollector {
    private Analyzer analyzer;
    private Clazz clazz;
    private Map<Descriptors.TypeRef, OCDDef> classToOCDMap;
    private String[] pids;
    private String pid;
    private Annotation designate;
    private final XMLAttributeFinder finder;
    private DesignateDef def;

    DesignateReader(Analyzer analyzer, Clazz clazz, Map<Descriptors.TypeRef, OCDDef> classToOCDMap, XMLAttributeFinder finder) {
        this.analyzer = analyzer;
        this.clazz = clazz;
        this.classToOCDMap = classToOCDMap;
        this.finder = finder;
    }

    static DesignateDef getDesignate(Clazz c, Analyzer analyzer, Map<Descriptors.TypeRef, OCDDef> classToOCDMap, XMLAttributeFinder finder) throws Exception {
        DesignateReader r = new DesignateReader(analyzer, c, classToOCDMap, finder);
        return r.getDef();
    }

    private DesignateDef getDef() throws Exception {
        this.clazz.parseClassFileWithCollector(this);
        if (this.pid != null && this.designate != null) {
            boolean factoryPid;
            if (this.pids != null && this.pids.length > 1) {
                this.analyzer.error("DS Component %s specifies multiple pids %s, and a Designate which requires exactly one pid", this.clazz.getClassName().getFQN(), Arrays.asList(this.pids));
                return null;
            }
            Descriptors.TypeRef ocdClass = (Descriptors.TypeRef)this.designate.get("ocd");
            OCDDef ocd = this.classToOCDMap.get(ocdClass);
            if (ocd == null) {
                this.analyzer.error("DS Component %s specifies ocd class %s which cannot be found; known classes %s", this.clazz.getClassName().getFQN(), ocdClass, this.classToOCDMap.keySet());
                return null;
            }
            String id = ocd.id;
            boolean bl = factoryPid = Boolean.TRUE == this.designate.get("factory");
            if (this.def == null) {
                this.def = new DesignateDef(this.finder);
            }
            this.def.ocdRef = id;
            this.def.pid = this.pid;
            this.def.factory = factoryPid;
            ocd.designates.add(this.def);
            return this.def;
        }
        return null;
    }

    @Override
    public void annotation(Annotation annotation) throws Exception {
        try {
            Object a = annotation.getAnnotation();
            if (a instanceof Designate) {
                this.designate = annotation;
            } else if (a instanceof Component) {
                this.doComponent((java.lang.annotation.Annotation)a);
            } else {
                XMLAttribute xmlAttr = this.finder.getXMLAttribute(annotation);
                if (xmlAttr != null) {
                    this.doXmlAttribute(annotation, xmlAttr);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.analyzer.error("During generation of a component on class %s, exception %s", this.clazz, e);
        }
    }

    void doComponent(java.lang.annotation.Annotation a) {
        Component component = (Component)a;
        this.pids = component.configurationPid();
        if (this.pids != null) {
            this.pid = this.pids[0];
        }
        if (this.pids == null || "$".equals(this.pid)) {
            this.pid = component.name();
            if (this.pid == null) {
                this.pid = this.clazz.getClassName().getFQN();
            }
        }
    }

    private void doXmlAttribute(Annotation annotation, XMLAttribute xmlAttr) {
        if (this.def == null) {
            this.def = new DesignateDef(this.finder);
        }
        this.def.addExtensionAttribute(xmlAttr, annotation);
    }
}

