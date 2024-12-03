/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.bnd.component.TagResource;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.metatype.DesignateDef;
import aQute.bnd.metatype.DesignateReader;
import aQute.bnd.metatype.MetatypeVersion;
import aQute.bnd.metatype.OCDDef;
import aQute.bnd.metatype.OCDReader;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.libg.generics.Create;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MetatypeAnnotations
implements AnalyzerPlugin {
    MetatypeVersion minVersion;

    @Override
    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        this.minVersion = MetatypeVersion.VERSION_1_2;
        Parameters header = OSGiHeader.parseHeader(analyzer.getProperty("-metatypeannotations", "*"));
        if (header.size() == 0) {
            return false;
        }
        Parameters optionsHeader = OSGiHeader.parseHeader(analyzer.getProperty("-metatypeannotations-options"));
        EnumSet<Options> options = EnumSet.noneOf(Options.class);
        for (Map.Entry<String, Attrs> entry : optionsHeader.entrySet()) {
            try {
                Options.parseOption(entry, options, this);
            }
            catch (IllegalArgumentException e) {
                analyzer.error("Unrecognized %s value %s with attributes %s, expected values are %s", "-metatypeannotations-options", entry.getKey(), entry.getValue(), EnumSet.allOf(Options.class));
            }
        }
        HashMap<Descriptors.TypeRef, OCDDef> classToOCDMap = new HashMap<Descriptors.TypeRef, OCDDef>();
        HashSet<String> ocdIds = new HashSet<String>();
        HashSet<String> pids = new HashSet<String>();
        Instructions instructions = new Instructions(header);
        XMLAttributeFinder finder = new XMLAttributeFinder(analyzer);
        List<Clazz> list = Create.list();
        block3: for (Clazz clazz : analyzer.getClassspace().values()) {
            for (Instruction instruction : instructions.keySet()) {
                if (!instruction.matches(clazz.getFQN())) continue;
                if (instruction.isNegated()) continue block3;
                list.add(clazz);
                OCDDef definition = OCDReader.getOCDDef(clazz, analyzer, options, finder, this.minVersion);
                if (definition == null) continue block3;
                classToOCDMap.put(clazz.getClassName(), definition);
                continue block3;
            }
        }
        for (Clazz clazz : list) {
            DesignateReader.getDesignate(clazz, analyzer, classToOCDMap, finder);
        }
        for (Map.Entry entry : classToOCDMap.entrySet()) {
            Descriptors.TypeRef c = (Descriptors.TypeRef)entry.getKey();
            OCDDef definition = (OCDDef)entry.getValue();
            definition.prepare(analyzer);
            if (!ocdIds.add(definition.id)) {
                analyzer.error("Duplicate OCD id %s from class %s; known ids %s", definition.id, c.getFQN(), ocdIds);
            }
            for (DesignateDef dDef : definition.designates) {
                if (dDef.pid == null || pids.add(dDef.pid)) continue;
                analyzer.error("Duplicate pid %s from class %s", dDef.pid, c.getFQN());
            }
            String name = "OSGI-INF/metatype/" + analyzer.validResourcePath(definition.id, "Invalid resource name") + ".xml";
            analyzer.getJar().putResource(name, new TagResource(definition.getTag()));
        }
        return false;
    }

    public String toString() {
        return "MetatypeAnnotations";
    }

    static enum Options {
        nested,
        version{

            @Override
            void process(MetatypeAnnotations anno, Attrs attrs) {
                String v = attrs.get("minimum");
                if (v != null && v.length() > 0) {
                    anno.minVersion = MetatypeVersion.valueFor(v);
                }
            }

            @Override
            void reset(MetatypeAnnotations anno) {
                anno.minVersion = MetatypeVersion.VERSION_1_2;
            }
        };


        void process(MetatypeAnnotations anno, Attrs attrs) {
        }

        void reset(MetatypeAnnotations anno) {
        }

        static void parseOption(Map.Entry<String, Attrs> entry, EnumSet<Options> options, MetatypeAnnotations state) {
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

