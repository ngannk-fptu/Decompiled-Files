/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make.component;

import aQute.bnd.component.HeaderReader;
import aQute.bnd.component.TagResource;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.make.component.ComponentAnnotationReader;
import aQute.bnd.make.metatype.MetaTypeReader;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.lib.tag.Tag;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class ServiceComponent
implements AnalyzerPlugin {
    @Override
    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        ComponentMaker m = new ComponentMaker(analyzer);
        Map<String, Map<String, String>> l = m.doServiceComponent();
        analyzer.setProperty("Service-Component", Processor.printClauses(l));
        analyzer.getInfo(m, "Service-Component: ");
        m.close();
        return false;
    }

    private static class ComponentMaker
    extends Processor {
        Analyzer analyzer;

        ComponentMaker(Analyzer analyzer) {
            super(analyzer);
            this.analyzer = analyzer;
        }

        Map<String, Map<String, String>> doServiceComponent() throws Exception {
            Map<String, Map<String, String>> serviceComponents = ComponentMaker.newMap();
            String header = this.getProperty("Service-Component");
            Parameters sc = this.parseHeader(header);
            for (Map.Entry<String, Attrs> entry : sc.entrySet()) {
                String name = entry.getKey();
                Map info = entry.getValue();
                try {
                    if (name.indexOf(47) >= 0 || name.endsWith(".xml")) {
                        serviceComponents.put(name, EMPTY);
                        continue;
                    }
                    this.componentEntry(serviceComponents, name, info);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.error("Invalid Service-Component header: %s %s, throws %s", name, info, e);
                    throw e;
                }
            }
            return serviceComponents;
        }

        private void componentEntry(Map<String, Map<String, String>> serviceComponents, String name, Map<String, String> info) throws Exception, IOException {
            boolean annotations = !Processor.isTrue(info.get("-noannotations"));
            boolean fqn = Verifier.isFQN(name);
            if (annotations) {
                Collection<Clazz> annotatedComponents = this.analyzer.getClasses("", Clazz.QUERY.ANNOTATED.toString(), "aQute.bnd.annotation.component.Component", Clazz.QUERY.NAMED.toString(), name);
                if (fqn) {
                    if (annotatedComponents.isEmpty()) {
                        this.createComponentResource(serviceComponents, name, info);
                    } else {
                        for (Clazz c : annotatedComponents) {
                            this.annotated(serviceComponents, c, info);
                        }
                    }
                } else if (annotatedComponents.isEmpty()) {
                    this.checkAnnotationsFeasible(name);
                } else {
                    for (Clazz c : annotatedComponents) {
                        this.annotated(serviceComponents, c, info);
                    }
                }
            } else if (fqn) {
                this.createComponentResource(serviceComponents, name, info);
            } else {
                this.error("Set to %s but entry %s is not an FQN ", "-noannotations", name);
            }
        }

        private Collection<Clazz> checkAnnotationsFeasible(String name) throws Exception {
            Collection<Clazz> not = this.analyzer.getClasses("", Clazz.QUERY.NAMED.toString(), name);
            if (not.isEmpty()) {
                if ("*".equals(name)) {
                    return not;
                }
                this.error("Specified %s but could not find any class matching this pattern", name);
            }
            for (Clazz c : not) {
                if (!c.getFormat().hasAnnotations()) continue;
                return not;
            }
            this.warning("Wildcards are used (%s) requiring annotations to decide what is a component. Wildcard maps to classes that are compiled with java.target < 1.5. Annotations were introduced in Java 1.5", name);
            return not;
        }

        void annotated(Map<String, Map<String, String>> components, Clazz c, Map<String, String> info) throws Exception {
            String merged;
            this.analyzer.warning("%s annotation used in class %s. Bnd DS annotations are deprecated as of Bnd 3.2 and support will be removed in Bnd 4.0. Please change to use OSGi DS annotations.", "aQute.bnd.annotation.component.Component", c);
            Map<String, String> map = ComponentAnnotationReader.getDefinition(c, this);
            String localname = map.get("name:");
            if (localname == null) {
                localname = c.getFQN();
            }
            if ((merged = Processor.merge(info.remove("properties:"), map.remove("properties:"))) != null && merged.length() > 0) {
                map.put("properties:", merged);
            }
            map.putAll(info);
            this.createComponentResource(components, localname, map);
        }

        private void createComponentResource(Map<String, Map<String, String>> components, String name, Map<String, String> info) throws Exception {
            boolean designate;
            if (info.containsKey("name:")) {
                name = info.get("name:");
            }
            String impl = name;
            if (info.containsKey("implementation:")) {
                impl = info.get("implementation:");
            }
            Descriptors.TypeRef implRef = this.analyzer.getTypeRefFromFQN(impl);
            this.analyzer.referTo(implRef);
            boolean bl = designate = this.designate(name, info.get("designate:"), false) || this.designate(name, info.get("designateFactory:"), true);
            if (designate && info.get("configuration-policy:") == null) {
                info.put("configuration-policy:", "require");
            }
            Resource resource = this.createComponentResource(name, impl, info);
            String pathSegment = this.analyzer.validResourcePath(name, "Invalid component name");
            this.analyzer.getJar().putResource("OSGI-INF/" + pathSegment + ".xml", resource);
            components.put("OSGI-INF/" + pathSegment + ".xml", EMPTY);
        }

        private boolean designate(String name, String config, boolean factory) throws Exception {
            if (config == null) {
                return false;
            }
            for (String c : Processor.split(config)) {
                Descriptors.TypeRef ref = this.analyzer.getTypeRefFromFQN(c);
                Clazz clazz = this.analyzer.findClass(ref);
                if (clazz != null) {
                    this.analyzer.referTo(ref);
                    MetaTypeReader r = new MetaTypeReader(clazz, this.analyzer);
                    r.setDesignate(name, factory);
                    String rname = "OSGI-INF/metatype/" + name + ".xml";
                    this.analyzer.getJar().putResource(rname, r);
                    continue;
                }
                this.analyzer.error("Cannot find designated configuration class %s for component %s", c, name);
            }
            return true;
        }

        Resource createComponentResource(String name, String impl, Map<String, String> info) throws Exception {
            HeaderReader hr = new HeaderReader(this.analyzer);
            Tag tag = hr.createComponentTag(name, impl, info);
            hr.close();
            return new TagResource(tag);
        }
    }
}

