/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.bnd.annotation.xml.XMLAttribute;
import aQute.bnd.metatype.ADDef;
import aQute.bnd.metatype.DesignateDef;
import aQute.bnd.metatype.IconDef;
import aQute.bnd.metatype.MetatypeAnnotations;
import aQute.bnd.metatype.MetatypeVersion;
import aQute.bnd.metatype.OCDDef;
import aQute.bnd.metatype.OptionDef;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Icon;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

class OCDReader {
    final Analyzer analyzer;
    private final Clazz clazz;
    final EnumSet<MetatypeAnnotations.Options> options;
    private final Set<Descriptors.TypeRef> analyzed = new HashSet<Descriptors.TypeRef>();
    private final OCDDef ocd;
    final XMLAttributeFinder finder;
    static final Pattern GENERIC = Pattern.compile("((" + Collection.class.getName() + "|" + Set.class.getName() + "|" + List.class.getName() + "|" + Iterable.class.getName() + ")|(.*))<(L.+;)>");
    static final Pattern COLLECTION = Pattern.compile("(" + Collection.class.getName() + "|" + Set.class.getName() + "|" + List.class.getName() + "|" + Queue.class.getName() + "|" + Stack.class.getName() + "|" + Deque.class.getName() + ")");
    static final Pattern IDENTIFIERTOPROPERTY = Pattern.compile("(__)|(_)|(\\$_\\$)|(\\$\\$)|(\\$)");

    private OCDReader(Analyzer analyzer, Clazz clazz, EnumSet<MetatypeAnnotations.Options> options, XMLAttributeFinder finder, MetatypeVersion minVersion) {
        this.analyzer = analyzer;
        this.clazz = clazz;
        this.options = options;
        this.finder = finder;
        this.ocd = new OCDDef(finder, minVersion);
    }

    static OCDDef getOCDDef(Clazz c, Analyzer analyzer, EnumSet<MetatypeAnnotations.Options> options, XMLAttributeFinder finder, MetatypeVersion minVersion) throws Exception {
        OCDReader r = new OCDReader(analyzer, c, options, finder, minVersion);
        return r.getDef();
    }

    private OCDDef getDef() throws Exception {
        this.clazz.parseClassFileWithCollector(new OCDDataCollector(this.ocd));
        if (this.ocd.id == null) {
            return null;
        }
        this.parseExtends(this.clazz);
        return this.ocd;
    }

    private void parseExtends(Clazz clazz) {
        Descriptors.TypeRef[] inherits = clazz.getInterfaces();
        if (inherits != null) {
            for (Descriptors.TypeRef typeRef : inherits) {
                if (typeRef.isJava() || !this.analyzed.add(typeRef)) continue;
                try {
                    Clazz inherit = this.analyzer.findClass(typeRef);
                    if (inherit != null) {
                        inherit.parseClassFileWithCollector(new OCDDataCollector(this.ocd));
                        this.parseExtends(inherit);
                        continue;
                    }
                    this.analyzer.error("Could not obtain super class %s of class %s", typeRef.getFQN(), clazz.getClassName().getFQN());
                }
                catch (Exception e) {
                    this.analyzer.exception(e, "Could not obtain super class %s of class %s; exception %s", typeRef.getFQN(), clazz.getClassName().getFQN(), e);
                }
            }
        }
    }

    private final class OCDDataCollector
    extends ClassDataCollector {
        private final OCDDef ocd;
        private final Map<Clazz.MethodDef, ADDef> methods = new LinkedHashMap<Clazz.MethodDef, ADDef>();
        private Clazz clazz;
        private Descriptors.TypeRef name;
        private int hasNoDefault = 0;
        private boolean hasValue = false;
        private Clazz.FieldDef prefixField = null;
        private ADDef current;

        OCDDataCollector(OCDDef ocd) {
            this.ocd = ocd;
        }

        @Override
        public boolean classStart(Clazz clazz) {
            this.clazz = clazz;
            this.name = clazz.getClassName();
            return true;
        }

        @Override
        public void field(Clazz.FieldDef defined) {
            if (defined.isStatic() && defined.getName().equals("PREFIX_")) {
                this.prefixField = defined;
            }
        }

        @Override
        public void method(Clazz.MethodDef defined) {
            if (defined.isStatic()) {
                this.current = null;
                return;
            }
            this.current = new ADDef(OCDReader.this.finder);
            this.methods.put(defined, this.current);
            if (this.clazz.isAnnotation()) {
                if (defined.getName().equals("value")) {
                    this.hasValue = true;
                } else {
                    ++this.hasNoDefault;
                }
            }
        }

        @Override
        public void annotationDefault(Clazz.MethodDef defined, Object value) {
            if (!defined.getName().equals("value")) {
                --this.hasNoDefault;
            }
        }

        @Override
        public void annotation(Annotation annotation) throws Exception {
            try {
                Object a = annotation.getAnnotation();
                if (a instanceof ObjectClassDefinition) {
                    this.doOCD((ObjectClassDefinition)a, annotation);
                } else if (a instanceof AttributeDefinition) {
                    this.current.ad = (AttributeDefinition)a;
                    this.current.a = annotation;
                } else {
                    XMLAttribute xmlAttr = OCDReader.this.finder.getXMLAttribute(annotation);
                    if (xmlAttr != null) {
                        this.doXmlAttribute(annotation, xmlAttr);
                    }
                }
            }
            catch (Exception e) {
                OCDReader.this.analyzer.exception(e, "During generation of a component on class %s, exception %s", this.clazz, e);
            }
        }

        @Override
        public void memberEnd() {
            this.current = null;
        }

        @Override
        public void classEnd() throws Exception {
            this.current = null;
            if (this.ocd.id == null) {
                return;
            }
            String prefix = null;
            if (this.prefixField != null) {
                Object c = this.prefixField.getConstant();
                if (this.prefixField.isFinal() && this.prefixField.getType() == OCDReader.this.analyzer.getTypeRef("java/lang/String") && c instanceof String) {
                    prefix = (String)c;
                    this.ocd.updateVersion(MetatypeVersion.VERSION_1_4);
                } else {
                    OCDReader.this.analyzer.warning("Field PREFIX_ in %s is not a static final String field with a compile-time constant value: %s", this.name.getFQN(), c);
                }
            }
            String singleElementAnnotation = null;
            if (this.hasValue && this.hasNoDefault == 0) {
                StringBuilder sb = new StringBuilder(this.name.getShorterName());
                boolean lastLowerCase = false;
                for (int i = 0; i < sb.length(); ++i) {
                    char c = sb.charAt(i);
                    if (Character.isUpperCase(c)) {
                        sb.setCharAt(i, Character.toLowerCase(c));
                        if (lastLowerCase) {
                            sb.insert(i++, '.');
                        }
                        lastLowerCase = false;
                        continue;
                    }
                    lastLowerCase = Character.isLowerCase(c);
                }
                singleElementAnnotation = sb.toString();
                this.ocd.updateVersion(MetatypeVersion.VERSION_1_4);
            }
            for (Map.Entry<Clazz.MethodDef, ADDef> entry : this.methods.entrySet()) {
                Matcher m;
                Clazz.MethodDef defined = entry.getKey();
                if (defined.isConstructor()) {
                    OCDReader.this.analyzer.error("Constructor %s for %s.%s found; only interfaces and annotations allowed for OCDs", defined.getName(), this.clazz.getClassName().getFQN(), defined.getName());
                }
                if (defined.getPrototype().length > 0) {
                    OCDReader.this.analyzer.error("Element %s for %s.%s has parameters; only no-parameter elements in an OCD interface allowed", defined.getName(), this.clazz.getClassName().getFQN(), defined.getName());
                    continue;
                }
                ADDef ad = entry.getValue();
                this.ocd.attributes.add(ad);
                String key = defined.getName();
                key = singleElementAnnotation != null && key.equals("value") ? singleElementAnnotation : this.identifierToPropertyName(key);
                if (prefix != null) {
                    key = prefix + key;
                }
                ad.id = key;
                ad.name = this.space(defined.getName());
                String rtype = defined.getGenericReturnType();
                if (rtype.endsWith("[]")) {
                    ad.cardinality = Integer.MAX_VALUE;
                    rtype = rtype.substring(0, rtype.length() - 2);
                }
                if ((m = GENERIC.matcher(rtype)).matches()) {
                    boolean collection;
                    boolean knownCollection = m.group(2) != null;
                    boolean bl = collection = knownCollection || this.identifiableCollection(m.group(3), false, true);
                    if (collection) {
                        if (ad.cardinality != 0) {
                            OCDReader.this.analyzer.error("AD for %s.%s uses an array of collections in return type (%s), Metatype allows either Vector or array", this.clazz.getClassName().getFQN(), defined.getName(), defined.getType().getFQN());
                        }
                        rtype = Clazz.objectDescriptorToFQN(m.group(4));
                        ad.cardinality = Integer.MIN_VALUE;
                    }
                }
                if (rtype.indexOf(60) > 0) {
                    rtype = rtype.substring(0, rtype.indexOf(60));
                }
                ad.type = this.getType(rtype);
                ad.required = true;
                Descriptors.TypeRef typeRef = OCDReader.this.analyzer.getTypeRefFromFQN(rtype);
                try {
                    Clazz c = OCDReader.this.analyzer.findClass(typeRef);
                    if (c != null && c.isEnum()) {
                        this.parseOptionValues(c, ad.options);
                    }
                }
                catch (Exception e) {
                    OCDReader.this.analyzer.exception(e, "AD for %s.%s Can not parse option values from type (%s), %s", this.clazz.getClassName().getFQN(), defined.getName(), defined.getType().getFQN(), e);
                }
                if (ad.ad != null) {
                    this.doAD(ad);
                }
                if (ad.defaults != null || !this.clazz.isAnnotation() || defined.getConstant() == null) continue;
                Object value = defined.getConstant();
                boolean isClass = false;
                Descriptors.TypeRef type = defined.getType().getClassRef();
                if (!type.isPrimitive()) {
                    if (Class.class.getName().equals(type.getFQN())) {
                        isClass = true;
                    } else {
                        try {
                            Clazz r = OCDReader.this.analyzer.findClass(type);
                            if (r.isAnnotation()) {
                                OCDReader.this.analyzer.warning("Nested annotation type found in field %s, %s", defined.getName(), type.getFQN());
                                return;
                            }
                        }
                        catch (Exception e) {
                            OCDReader.this.analyzer.exception(e, "Exception looking at annotation type default for element with descriptor %s,  type %s", defined, type);
                        }
                    }
                }
                if (value == null) continue;
                if (value.getClass().isArray()) {
                    ad.defaults = new String[Array.getLength(value)];
                    for (int i = 0; i < Array.getLength(value); ++i) {
                        Object element = Array.get(value, i);
                        ad.defaults[i] = this.valueToProperty(element, isClass);
                    }
                    continue;
                }
                ad.defaults = new String[]{this.valueToProperty(value, isClass)};
            }
        }

        private void doOCD(ObjectClassDefinition o, Annotation annotation) {
            if (this.ocd.id == null) {
                if (this.clazz.isInterface()) {
                    String[] pids;
                    this.ocd.id = o.id() == null ? this.name.getFQN() : o.id();
                    this.ocd.name = o.name() == null ? this.space(this.ocd.id) : o.name();
                    this.ocd.description = o.description() == null ? "" : o.description();
                    String string = this.ocd.localization = o.localization() == null ? "OSGI-INF/l10n/" + this.name.getFQN() : o.localization();
                    if (annotation.get("pid") != null) {
                        pids = o.pid();
                        this.designates(this.name.getFQN(), pids, false);
                    }
                    if (annotation.get("factoryPid") != null) {
                        pids = o.factoryPid();
                        this.designates(this.name.getFQN(), pids, true);
                    }
                    if (annotation.get("icon") != null) {
                        Icon[] icons;
                        for (Icon icon : icons = o.icon()) {
                            this.ocd.icons.add(new IconDef(icon.resource(), icon.size()));
                        }
                    }
                } else {
                    OCDReader.this.analyzer.error("ObjectClassDefinition applied to non-interface, non-annotation class %s", this.clazz);
                }
            }
        }

        private void doAD(ADDef adDef) throws Exception {
            AttributeDefinition ad = adDef.ad;
            Annotation a = adDef.a;
            if (ad.name() != null) {
                adDef.name = ad.name();
            }
            adDef.description = (String)a.get("description");
            if (a.get("type") != null) {
                adDef.type = ad.type();
            }
            if (a.get("cardinality") != null) {
                adDef.cardinality = ad.cardinality();
            }
            adDef.max = ad.max();
            adDef.min = ad.min();
            if (a.get("defaultValue") != null) {
                adDef.defaults = ad.defaultValue();
            }
            if (a.get("required") != null) {
                adDef.required = ad.required();
            }
            if (a.get("options") != null) {
                adDef.options.clear();
                for (Object o : (Object[])a.get("options")) {
                    Option opt = (Option)((Annotation)o).getAnnotation();
                    adDef.options.add(new OptionDef(opt.label(), opt.value()));
                }
            }
        }

        private void doXmlAttribute(Annotation annotation, XMLAttribute xmlAttr) {
            if (this.current == null) {
                if (this.clazz.isInterface()) {
                    this.ocd.addExtensionAttribute(xmlAttr, annotation);
                }
            } else {
                this.current.addExtensionAttribute(xmlAttr, annotation);
            }
        }

        private boolean identifiableCollection(String type, boolean intface, boolean topLevel) {
            try {
                Clazz clazz = OCDReader.this.analyzer.findClass(OCDReader.this.analyzer.getTypeRefFromFQN(type));
                if (!(clazz == null || topLevel && clazz.isAbstract() || !((intface && clazz.isInterface()) ^ clazz.hasPublicNoArgsConstructor()))) {
                    Descriptors.TypeRef ext;
                    Descriptors.TypeRef[] intfs = clazz.getInterfaces();
                    if (intfs != null) {
                        for (Descriptors.TypeRef intf : intfs) {
                            if (!COLLECTION.matcher(intf.getFQN()).matches() && !this.identifiableCollection(intf.getFQN(), true, false)) continue;
                            return true;
                        }
                    }
                    return (ext = clazz.getSuper()) != null && this.identifiableCollection(ext.getFQN(), false, false);
                }
            }
            catch (Exception e) {
                return false;
            }
            return false;
        }

        private String valueToProperty(Object value, boolean isClass) {
            if (isClass) {
                return ((Descriptors.TypeRef)value).getFQN();
            }
            return value.toString();
        }

        private void parseOptionValues(Clazz c, final List<OptionDef> options) throws Exception {
            c.parseClassFileWithCollector(new ClassDataCollector(){

                @Override
                public void field(Clazz.FieldDef def) {
                    if (def.isEnum()) {
                        OptionDef o = new OptionDef(def.getName(), def.getName());
                        options.add(o);
                    }
                }
            });
        }

        private AttributeType getType(String rtype) {
            if (rtype.endsWith("[]")) {
                OCDReader.this.analyzer.error("Can only handle array of depth one field , nested type %s", rtype);
                return null;
            }
            if ("boolean".equals(rtype) || Boolean.class.getName().equals(rtype)) {
                return AttributeType.BOOLEAN;
            }
            if ("byte".equals(rtype) || Byte.class.getName().equals(rtype)) {
                return AttributeType.BYTE;
            }
            if ("char".equals(rtype) || Character.class.getName().equals(rtype)) {
                return AttributeType.CHARACTER;
            }
            if ("short".equals(rtype) || Short.class.getName().equals(rtype)) {
                return AttributeType.SHORT;
            }
            if ("int".equals(rtype) || Integer.class.getName().equals(rtype)) {
                return AttributeType.INTEGER;
            }
            if ("long".equals(rtype) || Long.class.getName().equals(rtype)) {
                return AttributeType.LONG;
            }
            if ("float".equals(rtype) || Float.class.getName().equals(rtype)) {
                return AttributeType.FLOAT;
            }
            if ("double".equals(rtype) || Double.class.getName().equals(rtype)) {
                return AttributeType.DOUBLE;
            }
            if (String.class.getName().equals(rtype) || Class.class.getName().equals(rtype) || this.acceptableType(rtype)) {
                return AttributeType.STRING;
            }
            return null;
        }

        private boolean acceptableType(String rtype) {
            Descriptors.TypeRef ref = OCDReader.this.analyzer.getTypeRefFromFQN(rtype);
            try {
                Clazz returnType = OCDReader.this.analyzer.findClass(ref);
                if (returnType.isEnum()) {
                    return true;
                }
                if (!returnType.isAbstract() || returnType.isInterface() && OCDReader.this.options.contains((Object)MetatypeAnnotations.Options.nested)) {
                    return true;
                }
                if (!returnType.isInterface()) {
                    OCDReader.this.analyzer.error("Abstract classes not allowed as interface method return values: %s", rtype);
                } else {
                    OCDReader.this.analyzer.error("Nested metatype only allowed with option: nested type %s", rtype);
                }
                return false;
            }
            catch (Exception e) {
                OCDReader.this.analyzer.exception(e, "could not examine class for return type %s, exception message: %s", rtype, e);
                return false;
            }
        }

        private String identifierToPropertyName(String name) {
            Matcher m = IDENTIFIERTOPROPERTY.matcher(name);
            StringBuffer b = new StringBuffer();
            while (m.find()) {
                String replacement;
                if (m.group(1) != null) {
                    replacement = "_";
                } else if (m.group(2) != null) {
                    replacement = ".";
                } else if (m.group(3) != null) {
                    replacement = "-";
                    this.ocd.updateVersion(MetatypeVersion.VERSION_1_4);
                } else {
                    replacement = m.group(4) != null ? "\\$" : "";
                }
                m.appendReplacement(b, replacement);
            }
            m.appendTail(b);
            return b.toString();
        }

        private String space(String name) {
            return Clazz.unCamel(name);
        }

        private void designates(String name, String[] pids, boolean factory) {
            for (String pid : pids) {
                if ("$".equals(pid)) {
                    pid = name;
                }
                this.ocd.designates.add(new DesignateDef(this.ocd.id, pid, factory, OCDReader.this.finder));
            }
        }
    }
}

