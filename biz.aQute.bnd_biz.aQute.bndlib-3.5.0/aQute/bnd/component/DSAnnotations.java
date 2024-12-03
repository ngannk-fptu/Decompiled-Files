/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.component.AnnotationReader;
import aQute.bnd.component.ComponentDef;
import aQute.bnd.component.MergedRequirement;
import aQute.bnd.component.ReferenceDef;
import aQute.bnd.component.TagResource;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.version.Version;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.strings.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.osgi.service.component.annotations.ReferenceCardinality;

public class DSAnnotations
implements AnalyzerPlugin {
    Version minVersion;

    @Override
    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        Parameters header = OSGiHeader.parseHeader(analyzer.getProperty("-dsannotations", "*"));
        if (header.size() == 0) {
            return false;
        }
        this.minVersion = AnnotationReader.V1_3;
        Parameters optionsHeader = OSGiHeader.parseHeader(analyzer.mergeProperties("-dsannotations-options"));
        EnumSet<Options> options = EnumSet.noneOf(Options.class);
        for (Map.Entry<String, Attrs> entry : optionsHeader.entrySet()) {
            try {
                Options.parseOption(entry, options, this);
            }
            catch (IllegalArgumentException e) {
                analyzer.error("Unrecognized %s value %s with attributes %s, expected values are %s", "-dsannotations-options", entry.getKey(), entry.getValue(), EnumSet.allOf(Options.class));
            }
        }
        if (Processor.isTrue(analyzer.getProperty("-dsannotations-inherit"))) {
            options.add(Options.inherit);
        }
        if (Processor.isTrue(analyzer.getProperty("-ds-felix-extensions"))) {
            options.add(Options.felixExtensions);
        }
        Instructions instructions = new Instructions(header);
        Collection<Clazz> list = analyzer.getClassspace().values();
        String sc = analyzer.getProperty("Service-Component");
        ArrayList<String> names = new ArrayList<String>();
        if (sc != null && sc.trim().length() > 0) {
            names.add(sc);
        }
        TreeSet<String> provides = new TreeSet<String>();
        TreeSet<String> requires = new TreeSet<String>();
        Version maxVersion = AnnotationReader.V1_0;
        XMLAttributeFinder finder = new XMLAttributeFinder(analyzer);
        boolean componentProcessed = false;
        block3: for (Clazz c : list) {
            for (Instruction instruction : instructions.keySet()) {
                if (!instruction.matches(c.getFQN())) continue;
                if (instruction.isNegated()) continue block3;
                ComponentDef definition = AnnotationReader.getDefinition(c, analyzer, options, finder, this.minVersion);
                if (definition == null) continue;
                componentProcessed = true;
                definition.sortReferences();
                definition.prepare(analyzer);
                String name = "OSGI-INF/" + analyzer.validResourcePath(definition.name, "Invalid component name") + ".xml";
                names.add(name);
                analyzer.getJar().putResource(name, new TagResource(definition.getTag()));
                if (definition.service != null && !options.contains((Object)Options.nocapabilities)) {
                    Object[] objectClass = new String[definition.service.length];
                    for (int i = 0; i < definition.service.length; ++i) {
                        Descriptors.TypeRef tr = definition.service[i];
                        objectClass[i] = tr.getFQN();
                    }
                    Arrays.sort(objectClass);
                    this.addServiceCapability((String[])objectClass, provides);
                }
                if (!options.contains((Object)Options.norequirements)) {
                    MergedRequirement serviceReqMerge = new MergedRequirement("osgi.service");
                    for (ReferenceDef ref : definition.references.values()) {
                        this.addServiceRequirement(ref, serviceReqMerge);
                    }
                    requires.addAll(serviceReqMerge.toStringList());
                }
                maxVersion = ComponentDef.max(maxVersion, definition.version);
            }
        }
        if (componentProcessed && (options.contains((Object)Options.extender) || maxVersion.compareTo(AnnotationReader.V1_3) >= 0)) {
            maxVersion = ComponentDef.max(maxVersion, AnnotationReader.V1_3);
            this.addExtenderRequirement(requires, maxVersion);
        }
        sc = Processor.append(names.toArray(new String[0]));
        analyzer.setProperty("Service-Component", sc);
        this.updateHeader(analyzer, "Require-Capability", requires);
        this.updateHeader(analyzer, "Provide-Capability", provides);
        return false;
    }

    private void addServiceCapability(String[] objectClass, Set<String> provides) {
        if (objectClass.length > 0) {
            Parameters p = new Parameters();
            Attrs a = new Attrs();
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for (String oc : objectClass) {
                sb.append(sep).append(oc);
                sep = ",";
            }
            a.put("objectClass:List<String>", sb.toString());
            p.put("osgi.service", a);
            String s = p.toString();
            provides.add(s);
        }
    }

    private void addServiceRequirement(ReferenceDef ref, MergedRequirement requires) {
        String objectClass = ref.service;
        ReferenceCardinality cardinality = ref.cardinality;
        boolean optional = cardinality == ReferenceCardinality.OPTIONAL || cardinality == ReferenceCardinality.MULTIPLE;
        boolean multiple = cardinality == ReferenceCardinality.MULTIPLE || cardinality == ReferenceCardinality.AT_LEAST_ONE;
        String filter = "(objectClass=" + objectClass + ")";
        requires.put(filter, "active", optional, multiple);
    }

    private void addExtenderRequirement(Set<String> requires, Version version) {
        Version next = new Version(version.getMajor() + 1);
        Parameters p = new Parameters();
        Attrs a = new Attrs();
        a.put("filter:", "\"(&(osgi.extender=osgi.component)(version>=" + version + ")(!(version>=" + next + ")))\"");
        p.put("osgi.extender", a);
        String s = p.toString();
        requires.add(s);
    }

    private void updateHeader(Analyzer analyzer, String name, TreeSet<String> set) {
        if (!set.isEmpty()) {
            String value = analyzer.getProperty(name);
            if (value != null) {
                Parameters p = OSGiHeader.parseHeader(value);
                for (Map.Entry<String, Attrs> entry : p.entrySet()) {
                    StringBuilder sb = new StringBuilder(entry.getKey());
                    if (entry.getValue() != null) {
                        sb.append(";");
                        entry.getValue().append(sb);
                    }
                    set.add(sb.toString());
                }
            }
            String header = Strings.join(set);
            analyzer.setProperty(name, header);
        }
    }

    public String toString() {
        return "DSAnnotations";
    }

    public static enum Options {
        inherit,
        felixExtensions,
        extender,
        nocapabilities,
        norequirements,
        version{

            @Override
            void process(DSAnnotations anno, Attrs attrs) {
                String v = attrs.get("minimum");
                if (v != null && v.length() > 0) {
                    anno.minVersion = new Version(v);
                }
            }

            @Override
            void reset(DSAnnotations anno) {
                anno.minVersion = AnnotationReader.V1_3;
            }
        };


        void process(DSAnnotations anno, Attrs attrs) {
        }

        void reset(DSAnnotations anno) {
        }

        static void parseOption(Map.Entry<String, Attrs> entry, EnumSet<Options> options, DSAnnotations state) {
            String s = entry.getKey();
            boolean negation = false;
            if (s.startsWith("!")) {
                negation = true;
                s = s.substring(1);
            }
            Options option = Options.valueOf(s);
            if (negation) {
                options.remove((Object)option);
                option.reset(state);
            } else {
                options.add(option);
                Attrs attrs = entry.getValue();
                if (attrs != null) {
                    option.process(state, attrs);
                }
            }
        }
    }
}

