/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make.metatype;

import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.WriteResource;
import aQute.lib.io.IO;
import aQute.lib.tag.Tag;
import aQute.libg.generics.Create;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaTypeReader
extends WriteResource {
    final Analyzer reporter;
    Clazz clazz;
    String[] interfaces;
    Tag metadata = new Tag("metatype:MetaData", new String[]{"xmlns:metatype", "http://www.osgi.org/xmlns/metatype/v1.1.0"});
    Tag ocd = new Tag(this.metadata, "OCD", new Object[0]);
    Tag designate = new Tag(this.metadata, "Designate", new Object[0]);
    Tag object = new Tag(this.designate, "Object", new Object[0]);
    String extra;
    boolean inherit;
    boolean finished;
    boolean override;
    String designatePid;
    boolean factory;
    Map<Clazz.MethodDef, Meta.AD> methods = new LinkedHashMap<Clazz.MethodDef, Meta.AD>();
    Annotation ocdAnnotation;
    Clazz.MethodDef method;
    static Pattern COLLECTION = Pattern.compile("(.*(Collection|Set|List|Queue|Stack|Deque))<(L.+;)>");

    public MetaTypeReader(Clazz clazz, Analyzer reporter) {
        this.clazz = clazz;
        this.reporter = reporter;
        this.inherit = Processor.isTrue(reporter.getProperty("-metatype-inherit"));
    }

    private void addMethod(Clazz.MethodDef method, Meta.AD ad) throws Exception {
        if (method.isStatic()) {
            return;
        }
        String rtype = method.getGenericReturnType();
        String id = Configurable.mangleMethodName(method.getName());
        String name = Clazz.unCamel(id);
        int cardinality = 0;
        if (rtype.endsWith("[]")) {
            cardinality = Integer.MAX_VALUE;
            rtype = rtype.substring(0, rtype.length() - 2);
        }
        if (rtype.indexOf(60) > 0) {
            Matcher m;
            if (cardinality != 0) {
                this.reporter.error("AD for %s.%s uses an array of collections in return type (%s), Metatype allows either Vector or array", this.clazz.getClassName().getFQN(), method.getName(), method.getType().getFQN());
            }
            if ((m = COLLECTION.matcher(rtype)).matches()) {
                rtype = Clazz.objectDescriptorToFQN(m.group(3));
                cardinality = Integer.MIN_VALUE;
            }
        }
        Meta.Type type = this.getType(rtype);
        boolean required = ad == null || ad.required();
        String deflt = null;
        String max = null;
        String min = null;
        String[] optionLabels = null;
        String[] optionValues = null;
        String description = null;
        Descriptors.TypeRef typeRef = this.reporter.getTypeRefFromFQN(rtype);
        Clazz c = this.reporter.findClass(typeRef);
        if (c != null && c.isEnum()) {
            optionValues = this.parseOptionValues(c);
        }
        if (ad != null) {
            if (ad.id() != null) {
                id = ad.id();
            }
            if (ad.name() != null) {
                name = ad.name();
            }
            if (ad.cardinality() != 0) {
                cardinality = ad.cardinality();
            }
            if (ad.type() != null) {
                type = ad.type();
            }
            if (ad.description() != null) {
                description = ad.description();
            }
            if (ad.optionLabels() != null) {
                optionLabels = ad.optionLabels();
            }
            if (ad.optionValues() != null) {
                optionValues = ad.optionValues();
            }
            if (ad.min() != null) {
                min = ad.min();
            }
            if (ad.max() != null) {
                max = ad.max();
            }
            if (ad.deflt() != null) {
                deflt = ad.deflt();
            }
        }
        if (optionValues != null) {
            if (optionLabels == null || optionLabels.length == 0) {
                optionLabels = new String[optionValues.length];
                for (int i = 0; i < optionValues.length; ++i) {
                    optionLabels[i] = Clazz.unCamel(optionValues[i]);
                }
            }
            if (optionLabels.length != optionValues.length) {
                this.reporter.error("Option labels and option values not the same length for %s", id);
                optionLabels = optionValues;
            }
        }
        Tag adt = new Tag(this.ocd, "AD", new Object[0]);
        adt.addAttribute("name", name);
        adt.addAttribute("id", id);
        adt.addAttribute("cardinality", cardinality);
        adt.addAttribute("required", required);
        adt.addAttribute("default", deflt);
        adt.addAttribute("type", (Object)type);
        adt.addAttribute("max", max);
        adt.addAttribute("min", min);
        adt.addAttribute("description", description);
        if (optionLabels != null && optionValues != null) {
            for (int i = 0; i < optionLabels.length; ++i) {
                Tag option = new Tag(adt, "Option", new Object[0]);
                option.addAttribute("label", optionLabels[i]);
                option.addAttribute("value", optionValues[i]);
            }
        }
    }

    private String[] parseOptionValues(Clazz c) throws Exception {
        final List<String> values = Create.list();
        c.parseClassFileWithCollector(new ClassDataCollector(){

            @Override
            public void field(Clazz.FieldDef def) {
                if (def.isEnum()) {
                    values.add(def.getName());
                }
            }
        });
        return values.toArray(new String[0]);
    }

    Meta.Type getType(String rtype) {
        if (rtype.endsWith("[]") && (rtype = rtype.substring(0, rtype.length() - 2)).endsWith("[]")) {
            throw new IllegalArgumentException("Can only handle array of depth one");
        }
        if ("boolean".equals(rtype) || Boolean.class.getName().equals(rtype)) {
            return Meta.Type.Boolean;
        }
        if ("byte".equals(rtype) || Byte.class.getName().equals(rtype)) {
            return Meta.Type.Byte;
        }
        if ("char".equals(rtype) || Character.class.getName().equals(rtype)) {
            return Meta.Type.Character;
        }
        if ("short".equals(rtype) || Short.class.getName().equals(rtype)) {
            return Meta.Type.Short;
        }
        if ("int".equals(rtype) || Integer.class.getName().equals(rtype)) {
            return Meta.Type.Integer;
        }
        if ("long".equals(rtype) || Long.class.getName().equals(rtype)) {
            return Meta.Type.Long;
        }
        if ("float".equals(rtype) || Float.class.getName().equals(rtype)) {
            return Meta.Type.Float;
        }
        if ("double".equals(rtype) || Double.class.getName().equals(rtype)) {
            return Meta.Type.Double;
        }
        return Meta.Type.String;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        try {
            this.finish();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        PrintWriter pw = IO.writer(out, StandardCharsets.UTF_8);
        pw.print("<?xml version='1.0' encoding='UTF-8'?>\n");
        this.metadata.print(0, pw);
        pw.flush();
    }

    void finish() throws Exception {
        if (!this.finished) {
            this.finished = true;
            this.clazz.parseClassFileWithCollector(new Find());
            Meta.OCD ocd = null;
            ocd = this.ocdAnnotation != null ? this.ocdAnnotation.getAnnotation(Meta.OCD.class) : Configurable.createConfigurable(Meta.OCD.class, new HashMap());
            String id = this.clazz.getClassName().getFQN();
            String name = Clazz.unCamel(this.clazz.getClassName().getShortName());
            String description = null;
            String localization = id;
            boolean factory = this.factory;
            if (ocd.id() != null) {
                id = ocd.id();
            }
            if (ocd.name() != null) {
                name = ocd.name();
            }
            if (ocd.localization() != null) {
                localization = ocd.localization();
            }
            if (ocd.description() != null) {
                description = ocd.description();
            }
            String pid = id;
            if (this.override) {
                pid = this.designatePid;
                factory = this.factory;
                id = this.designatePid;
            } else if (this.ocdAnnotation.get("factory") != null) {
                factory = true;
            }
            this.ocd.addAttribute("name", name);
            this.ocd.addAttribute("id", id);
            this.ocd.addAttribute("description", description);
            this.metadata.addAttribute("localization", localization);
            for (Map.Entry<Clazz.MethodDef, Meta.AD> entry : this.methods.entrySet()) {
                this.addMethod(entry.getKey(), entry.getValue());
            }
            this.designate.addAttribute("pid", pid);
            if (factory) {
                this.designate.addAttribute("factoryPid", pid);
            }
            this.object.addAttribute("ocdref", id);
            if (this.inherit) {
                this.handleInheritedClasses(this.clazz);
            }
        }
    }

    private void handleInheritedClasses(Clazz child) throws Exception {
        Descriptors.TypeRef superClazz;
        Descriptors.TypeRef[] ifaces = child.getInterfaces();
        if (ifaces != null) {
            for (Descriptors.TypeRef ref : ifaces) {
                this.parseAndMergeInheritedMetadata(ref, child);
            }
        }
        if ((superClazz = child.getSuper()) != null) {
            this.parseAndMergeInheritedMetadata(superClazz, child);
        }
    }

    private void parseAndMergeInheritedMetadata(Descriptors.TypeRef ref, Clazz child) throws Exception {
        if (ref.isJava()) {
            return;
        }
        Clazz ec = this.reporter.findClass(ref);
        if (ec == null) {
            this.reporter.error("Missing inherited class for Metatype annotations: %s from %s", ref, child.getClassName());
        } else {
            MetaTypeReader mtr = new MetaTypeReader(ec, this.reporter);
            mtr.setDesignate(this.designatePid, this.factory);
            mtr.finish();
            for (Map.Entry<Clazz.MethodDef, Meta.AD> entry : mtr.methods.entrySet()) {
                this.addMethod(entry.getKey(), entry.getValue());
            }
            this.handleInheritedClasses(ec);
        }
    }

    public void setDesignate(String pid, boolean factory) {
        this.override = true;
        this.factory = factory;
        this.designatePid = pid;
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    class Find
    extends ClassDataCollector {
        Find() {
        }

        @Override
        public void method(Clazz.MethodDef mdef) {
            MetaTypeReader.this.method = mdef;
            MetaTypeReader.this.methods.put(mdef, null);
        }

        @Override
        public void annotation(Annotation annotation) {
            block7: {
                try {
                    Meta.OCD ocd = annotation.getAnnotation(Meta.OCD.class);
                    Meta.AD ad = annotation.getAnnotation(Meta.AD.class);
                    if (ocd != null) {
                        MetaTypeReader.this.ocdAnnotation = annotation;
                    }
                    if (ad == null) break block7;
                    assert (MetaTypeReader.this.method != null);
                    try {
                        if (annotation.get("required") == null) {
                            annotation.put("required", true);
                        }
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                    MetaTypeReader.this.methods.put(MetaTypeReader.this.method, ad);
                }
                catch (Exception e) {
                    MetaTypeReader.this.reporter.error("Error during annotation parsing %s : %s", MetaTypeReader.this.clazz, e);
                    e.printStackTrace();
                }
            }
        }
    }
}

