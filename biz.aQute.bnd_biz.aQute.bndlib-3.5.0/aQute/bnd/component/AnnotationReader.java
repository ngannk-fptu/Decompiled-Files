/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.annotation.xml.XMLAttribute;
import aQute.bnd.component.ComponentDef;
import aQute.bnd.component.DSAnnotations;
import aQute.bnd.component.FieldCollectionType;
import aQute.bnd.component.ReferenceDef;
import aQute.bnd.component.error.DeclarativeServicesAnnotationError;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.version.Version;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.collections.MultiMap;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.metatype.annotations.Designate;

public class AnnotationReader
extends ClassDataCollector {
    static final Descriptors.TypeRef[] EMPTY = new Descriptors.TypeRef[0];
    static final Pattern PROPERTY_PATTERN = Pattern.compile("\\s*([^=\\s:]+)\\s*(?::\\s*(Boolean|Byte|Character|Short|Integer|Long|Float|Double|String)\\s*)?=(.*)");
    public static final Version V1_0 = new Version("1.0.0");
    public static final Version V1_1 = new Version("1.1.0");
    public static final Version V1_2 = new Version("1.2.0");
    public static final Version V1_3 = new Version("1.3.0");
    public static final Version V1_4 = new Version("1.4.0");
    static final Pattern BINDNAME = Pattern.compile("(set|add|bind)?(.*)");
    static final Pattern BINDDESCRIPTORDS10 = Pattern.compile("\\(L(((org/osgi/framework/ServiceReference)|(org/osgi/service/component/ComponentServiceObjects)|(java/util/Map\\$Entry)|(java/util/Map))|([^;]+));\\)(V|(Ljava/util/Map;))");
    static final Pattern BINDDESCRIPTORDS11 = Pattern.compile("\\(L([^;]+);(Ljava/util/Map;)?\\)(V|(Ljava/util/Map;))");
    static final Pattern BINDDESCRIPTORDS13 = Pattern.compile("\\(((Lorg/osgi/framework/ServiceReference;)|(Lorg/osgi/service/component/ComponentServiceObjects;)|(Ljava/util/Map;)|(Ljava/util/Map\\$Entry;)|(L([^;]+);))+\\)(V|(Ljava/util/Map;))");
    static final Pattern LIFECYCLEDESCRIPTORDS10 = Pattern.compile("\\((Lorg/osgi/service/component/ComponentContext;)\\)(V|(Ljava/util/Map;))");
    static final Pattern LIFECYCLEDESCRIPTORDS11 = Pattern.compile("\\(((Lorg/osgi/service/component/ComponentContext;)|(Lorg/osgi/framework/BundleContext;)|(Ljava/util/Map;))*\\)(V|(Ljava/util/Map;))");
    static final Pattern LIFECYCLEDESCRIPTORDS13 = Pattern.compile("\\((L([^;]+);)*\\)(V|(Ljava/util/Map;))");
    static final Pattern LIFECYCLEARGUMENT = Pattern.compile("((Lorg/osgi/service/component/ComponentContext;)|(Lorg/osgi/framework/BundleContext;)|(Ljava/util/Map;)|(L([^;]+);))");
    static final Pattern IDENTIFIERTOPROPERTY = Pattern.compile("(__)|(_)|(\\$_\\$)|(\\$\\$)|(\\$)");
    static final Pattern DEACTIVATEDESCRIPTORDS11 = Pattern.compile("\\(((Lorg/osgi/service/component/ComponentContext;)|(Lorg/osgi/framework/BundleContext;)|(Ljava/util/Map;)|(Ljava/lang/Integer;)|(I))*\\)(V|(Ljava/util/Map;))");
    static final Pattern DEACTIVATEDESCRIPTORDS13 = Pattern.compile("\\(((L([^;]+);)|(I))*\\)(V|(Ljava/util/Map;))");
    static final Map<String, Class<?>> wrappers;
    ComponentDef component;
    Clazz clazz;
    Descriptors.TypeRef[] interfaces;
    Clazz.FieldDef member;
    Descriptors.TypeRef className;
    Analyzer analyzer;
    MultiMap<String, Clazz.MethodDef> methods = new MultiMap();
    Descriptors.TypeRef extendsClass;
    boolean baseclass = true;
    final EnumSet<DSAnnotations.Options> options;
    final Map<Clazz.FieldDef, ReferenceDef> referencesByMember = new HashMap<Clazz.FieldDef, ReferenceDef>();
    final XMLAttributeFinder finder;
    Map<String, List<DeclarativeServicesAnnotationError>> mismatchedAnnotations = new HashMap<String, List<DeclarativeServicesAnnotationError>>();

    AnnotationReader(Analyzer analyzer, Clazz clazz, EnumSet<DSAnnotations.Options> options, XMLAttributeFinder finder, Version minVersion) {
        this.analyzer = analyzer;
        this.clazz = clazz;
        this.options = options;
        this.finder = finder;
        this.component = new ComponentDef(finder, minVersion);
    }

    public static ComponentDef getDefinition(Clazz c, Analyzer analyzer, EnumSet<DSAnnotations.Options> options, XMLAttributeFinder finder, Version minVersion) throws Exception {
        AnnotationReader r = new AnnotationReader(analyzer, c, options, finder, minVersion);
        return r.getDef();
    }

    private ComponentDef getDef() throws Exception {
        this.clazz.parseClassFileWithCollector(this);
        if (this.component.implementation == null) {
            return null;
        }
        if (this.options.contains((Object)DSAnnotations.Options.inherit)) {
            this.baseclass = false;
            while (this.extendsClass != null && !this.extendsClass.isJava()) {
                Clazz ec = this.analyzer.findClass(this.extendsClass);
                if (ec == null) {
                    this.analyzer.error("Missing super class for DS annotations: %s from %s", this.extendsClass, this.clazz.getClassName()).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), null, null, DeclarativeServicesAnnotationError.ErrorType.UNABLE_TO_LOCATE_SUPER_CLASS));
                    break;
                }
                ec.parseClassFileWithCollector(this);
            }
        }
        for (ReferenceDef rdef : this.component.references.values()) {
            if (rdef.bind == null) continue;
            rdef.unbind = this.referredMethod(this.analyzer, rdef, rdef.unbind, "add(.*)", "remove$1", "(.*)", "un$1");
            rdef.updated = this.referredMethod(this.analyzer, rdef, rdef.updated, "(add|set|bind)(.*)", "updated$2", "(.*)", "updated$1");
            if (rdef.policy != ReferencePolicy.DYNAMIC || rdef.unbind != null) continue;
            this.analyzer.error("In component class %s, reference %s is dynamic but has no unbind method.", this.className.getFQN(), rdef.name).details(this.getDetails(rdef, DeclarativeServicesAnnotationError.ErrorType.DYNAMIC_REFERENCE_WITHOUT_UNBIND));
        }
        return this.component;
    }

    protected String referredMethod(Analyzer analyzer, ReferenceDef rdef, String value, String ... matches) {
        if (value == null) {
            String bind = rdef.bind;
            for (int i = 0; i < matches.length; i += 2) {
                Matcher m = Pattern.compile(matches[i]).matcher(bind);
                if (!m.matches()) continue;
                value = m.replaceFirst(matches[i + 1]);
                break;
            }
        } else if (value.equals("-")) {
            return null;
        }
        if (this.methods.containsKey(value)) {
            for (Clazz.MethodDef method : (List)this.methods.get(value)) {
                String service = this.determineReferenceType(method.getDescriptor().toString(), rdef, rdef.service, null);
                if (service == null) continue;
                if (!method.isProtected()) {
                    this.component.updateVersion(V1_1);
                }
                return value;
            }
            analyzer.warning("None of the methods related to '%s' in the class '%s' named '%s' for service type '%s' have an acceptable signature. The descriptors found are:", rdef.bind, this.component.implementation, value, rdef.service);
            for (Clazz.MethodDef method : (List)this.methods.get(value)) {
                analyzer.warning("  methodname: %s descriptor: %s", value, method.getDescriptor().toString()).details(this.getDetails(rdef, DeclarativeServicesAnnotationError.ErrorType.UNSET_OR_MODIFY_WITH_WRONG_SIGNATURE));
            }
        }
        return null;
    }

    @Override
    public void classEnd() throws Exception {
        this.member = null;
    }

    @Override
    public void memberEnd() {
        this.member = null;
    }

    @Override
    public void annotation(Annotation annotation) {
        try {
            Object a = annotation.getAnnotation();
            if (a instanceof Component) {
                this.doComponent((Component)a, annotation);
            } else if (a instanceof Activate) {
                this.doActivate();
            } else if (a instanceof Deactivate) {
                this.doDeactivate();
            } else if (a instanceof Modified) {
                this.doModified();
            } else if (a instanceof Reference) {
                this.doReference((Reference)a, annotation);
            } else if (a instanceof Designate) {
                this.doDesignate((Designate)a);
            } else if (annotation.getName().getFQN().startsWith("aQute.bnd.annotation.component")) {
                this.handleMixedUsageError(annotation);
            } else {
                XMLAttribute xmlAttr = this.finder.getXMLAttribute(annotation);
                if (xmlAttr != null) {
                    this.doXmlAttribute(annotation, xmlAttr);
                }
            }
        }
        catch (Exception e) {
            this.analyzer.exception(e, "During generation of a component on class %s, exception %s", this.clazz, e);
        }
    }

    private void handleMixedUsageError(Annotation annotation) throws Exception {
        DeclarativeServicesAnnotationError errorDetails;
        String fqn = annotation.getName().getFQN();
        switch (annotation.getElementType()) {
            case METHOD: {
                errorDetails = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.member.getName(), this.member.getDescriptor().toString(), DeclarativeServicesAnnotationError.ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_STD);
                break;
            }
            case FIELD: {
                errorDetails = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.member.getName(), DeclarativeServicesAnnotationError.ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_STD);
                break;
            }
            default: {
                errorDetails = new DeclarativeServicesAnnotationError(this.className.getFQN(), null, DeclarativeServicesAnnotationError.ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_STD);
            }
        }
        List<DeclarativeServicesAnnotationError> errors = this.mismatchedAnnotations.get(fqn);
        if (errors == null) {
            errors = new ArrayList<DeclarativeServicesAnnotationError>();
            this.mismatchedAnnotations.put(fqn, errors);
        }
        errors.add(errorDetails);
    }

    private void doXmlAttribute(Annotation annotation, XMLAttribute xmlAttr) {
        this.component.updateVersion(V1_1);
        if (this.member == null) {
            this.component.addExtensionAttribute(xmlAttr, annotation);
        } else {
            ReferenceDef ref = this.referencesByMember.get(this.member);
            if (ref == null) {
                ref = new ReferenceDef(this.finder);
                this.referencesByMember.put(this.member, ref);
            }
            ref.addExtensionAttribute(xmlAttr, annotation);
        }
    }

    protected void doDesignate(Designate a) {
        if (a.factory() && this.component.configurationPolicy == null) {
            this.component.configurationPolicy = ConfigurationPolicy.REQUIRE;
        }
    }

    protected void doActivate() {
        String methodDescriptor = this.member.getDescriptor().toString();
        DeclarativeServicesAnnotationError details = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.member.getName(), methodDescriptor, DeclarativeServicesAnnotationError.ErrorType.ACTIVATE_SIGNATURE_ERROR);
        if (!(this.member instanceof Clazz.MethodDef)) {
            this.analyzer.error("Activate annotation on a field %s.%s", this.clazz, this.member.getDescriptor()).details(details);
            return;
        }
        boolean hasMapReturnType = false;
        Matcher m = LIFECYCLEDESCRIPTORDS10.matcher(methodDescriptor);
        if ("activate".equals(this.member.getName()) && m.matches()) {
            this.component.activate = this.member.getName();
            boolean bl = hasMapReturnType = m.group(3) != null;
            if (!this.member.isProtected()) {
                this.component.updateVersion(V1_1);
            }
        } else {
            m = LIFECYCLEDESCRIPTORDS11.matcher(methodDescriptor);
            if (m.matches()) {
                this.component.activate = this.member.getName();
                this.component.updateVersion(V1_1);
                hasMapReturnType = m.group(6) != null;
            } else {
                m = LIFECYCLEDESCRIPTORDS13.matcher(methodDescriptor);
                if (m.matches()) {
                    this.component.activate = this.member.getName();
                    this.component.updateVersion(V1_3);
                    hasMapReturnType = m.group(4) != null;
                    this.processAnnotationArguments(methodDescriptor, details);
                } else {
                    this.analyzer.error("Activate method for %s descriptor %s is not acceptable.", this.clazz, this.member.getDescriptor()).details(details);
                }
            }
        }
        this.checkMapReturnType(hasMapReturnType, details);
    }

    protected void doDeactivate() {
        String methodDescriptor = this.member.getDescriptor().toString();
        DeclarativeServicesAnnotationError details = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.member.getName(), methodDescriptor, DeclarativeServicesAnnotationError.ErrorType.DEACTIVATE_SIGNATURE_ERROR);
        if (!(this.member instanceof Clazz.MethodDef)) {
            this.analyzer.error("Deactivate annotation on a field %s.%s", this.clazz, this.member.getDescriptor()).details(details);
            return;
        }
        boolean hasMapReturnType = false;
        Matcher m = LIFECYCLEDESCRIPTORDS10.matcher(methodDescriptor);
        if ("deactivate".equals(this.member.getName()) && m.matches()) {
            this.component.deactivate = this.member.getName();
            boolean bl = hasMapReturnType = m.group(3) != null;
            if (!this.member.isProtected()) {
                this.component.updateVersion(V1_1);
            }
        } else {
            m = DEACTIVATEDESCRIPTORDS11.matcher(methodDescriptor);
            if (m.matches()) {
                this.component.deactivate = this.member.getName();
                this.component.updateVersion(V1_1);
                hasMapReturnType = m.group(8) != null;
            } else {
                m = DEACTIVATEDESCRIPTORDS13.matcher(methodDescriptor);
                if (m.matches()) {
                    this.component.deactivate = this.member.getName();
                    this.component.updateVersion(V1_3);
                    hasMapReturnType = m.group(6) != null;
                    this.processAnnotationArguments(methodDescriptor, details);
                } else {
                    this.analyzer.error("Deactivate method for %s descriptor %s is not acceptable.", this.clazz, this.member.getDescriptor()).details(details);
                }
            }
        }
        this.checkMapReturnType(hasMapReturnType, details);
    }

    protected void doModified() {
        String methodDescriptor = this.member.getDescriptor().toString();
        DeclarativeServicesAnnotationError details = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.member.getName(), methodDescriptor, DeclarativeServicesAnnotationError.ErrorType.MODIFIED_SIGNATURE_ERROR);
        if (!(this.member instanceof Clazz.MethodDef)) {
            this.analyzer.error("Modified annotation on a field %s.%s", this.clazz, this.member.getDescriptor()).details(details);
            return;
        }
        boolean hasMapReturnType = false;
        Matcher m = LIFECYCLEDESCRIPTORDS11.matcher(methodDescriptor);
        if (m.matches()) {
            this.component.modified = this.member.getName();
            this.component.updateVersion(V1_1);
            hasMapReturnType = m.group(6) != null;
        } else {
            m = LIFECYCLEDESCRIPTORDS13.matcher(methodDescriptor);
            if (m.matches()) {
                this.component.modified = this.member.getName();
                this.component.updateVersion(V1_3);
                hasMapReturnType = m.group(4) != null;
                this.processAnnotationArguments(methodDescriptor, details);
            } else {
                this.analyzer.error("Modified method for %s descriptor %s is not acceptable.", this.clazz, this.member.getDescriptor()).details(details);
            }
        }
        this.checkMapReturnType(hasMapReturnType, details);
    }

    private void processAnnotationArguments(String methodDescriptor, DeclarativeServicesAnnotationError details) {
        Matcher m = LIFECYCLEARGUMENT.matcher(methodDescriptor);
        while (m.find()) {
            String type = m.group(6);
            if (type == null) continue;
            Descriptors.TypeRef typeRef = this.analyzer.getTypeRef(type);
            try {
                Clazz clazz = this.analyzer.findClass(typeRef);
                if (clazz.isAnnotation()) {
                    clazz.parseClassFileWithCollector(new ComponentPropertyTypeDataCollector(methodDescriptor, details));
                    continue;
                }
                if (clazz.isInterface() && this.options.contains((Object)DSAnnotations.Options.felixExtensions)) continue;
                this.analyzer.error("Non annotation argument to lifecycle method with descriptor %s,  type %s", methodDescriptor, type).details(details);
            }
            catch (Exception e) {
                this.analyzer.exception(e, "Exception looking at annotation argument to lifecycle method with descriptor %s,  type %s", methodDescriptor, type).details(details);
            }
        }
    }

    protected void doReference(Reference reference, Annotation raw) throws Exception {
        String error;
        ReferenceDef def;
        if (this.member == null) {
            def = new ReferenceDef(this.finder);
        } else if (this.referencesByMember.containsKey(this.member)) {
            def = this.referencesByMember.get(this.member);
        } else {
            def = new ReferenceDef(this.finder);
            this.referencesByMember.put(this.member, def);
        }
        def.className = this.className.getFQN();
        def.name = reference.name();
        def.bind = reference.bind();
        def.unbind = reference.unbind();
        def.updated = reference.updated();
        def.field = reference.field();
        def.fieldOption = reference.fieldOption();
        def.cardinality = reference.cardinality();
        def.policy = reference.policy();
        def.policyOption = reference.policyOption();
        def.scope = reference.scope();
        def.target = reference.target();
        DeclarativeServicesAnnotationError details = this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.REFERENCE);
        if (def.target != null && (error = Verifier.validateFilter(def.target)) != null) {
            this.analyzer.error("Invalid target filter %s for %s: %s", def.target, def.name, error).details(this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.INVALID_TARGET_FILTER));
        }
        String annoService = null;
        Descriptors.TypeRef annoServiceTR = (Descriptors.TypeRef)raw.get("service");
        if (annoServiceTR != null) {
            annoService = annoServiceTR.getFQN();
        }
        if (this.member != null) {
            if (this.member instanceof Clazz.MethodDef) {
                def.bindDescriptor = this.member.getDescriptor().toString();
                if (!this.member.isProtected()) {
                    def.updateVersion(V1_1);
                }
                def.bind = this.member.getName();
                if (def.name == null) {
                    Matcher m = BINDNAME.matcher(this.member.getName());
                    if (m.matches()) {
                        def.name = m.group(2);
                    } else {
                        this.analyzer.error("Invalid name for bind method %s", this.member.getName()).details(this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.INVALID_REFERENCE_BIND_METHOD_NAME));
                    }
                }
                def.service = this.determineReferenceType(def.bindDescriptor, def, annoService, this.member.getSignature());
                if (def.service == null) {
                    this.analyzer.error("In component %s, method %s,  cannot recognize the signature of the descriptor: %s", this.component.effectiveName(), def.name, this.member.getDescriptor());
                }
            } else if (this.member instanceof Clazz.FieldDef) {
                String sig;
                def.updateVersion(V1_3);
                def.field = this.member.getName();
                if (def.name == null) {
                    def.name = def.field;
                }
                if (def.policy == null && this.member.isVolatile()) {
                    def.policy = ReferencePolicy.DYNAMIC;
                }
                if ((sig = this.member.getSignature()) == null) {
                    sig = this.member.getDescriptor().toString();
                }
                String[] sigs = sig.split("[<;>]");
                int sigLength = sigs.length;
                int index = 0;
                boolean isCollection = false;
                if ("Ljava/util/Collection".equals(sigs[index]) || "Ljava/util/List".equals(sigs[index])) {
                    ++index;
                    isCollection = true;
                }
                FieldCollectionType fieldCollectionType = null;
                if (this.sufficientGenerics(index, sigLength, def, sig)) {
                    if ("Lorg/osgi/framework/ServiceReference".equals(sigs[index])) {
                        if (this.sufficientGenerics(index++, sigLength, def, sig)) {
                            fieldCollectionType = FieldCollectionType.reference;
                        }
                    } else if ("Lorg/osgi/service/component/ComponentServiceObjects".equals(sigs[index])) {
                        if (this.sufficientGenerics(index++, sigLength, def, sig)) {
                            fieldCollectionType = FieldCollectionType.serviceobjects;
                        }
                    } else if ("Ljava/util/Map".equals(sigs[index])) {
                        if (this.sufficientGenerics(index++, sigLength, def, sig)) {
                            fieldCollectionType = FieldCollectionType.properties;
                        }
                    } else if ("Ljava/util/Map$Entry".equals(sigs[index]) && this.sufficientGenerics(index++ + 5, sigLength, def, sig)) {
                        if ("Ljava/util/Map".equals(sigs[index++]) && "Ljava/lang/String".equals(sigs[index++])) {
                            if ("Ljava/lang/Object".equals(sigs[index]) || "+Ljava/lang/Object".equals(sigs[index])) {
                                fieldCollectionType = FieldCollectionType.tuple;
                                index += 3;
                            } else if ("*".equals(sigs[index])) {
                                fieldCollectionType = FieldCollectionType.tuple;
                                index += 2;
                            } else {
                                index = sigLength;
                            }
                        }
                    } else {
                        fieldCollectionType = FieldCollectionType.service;
                    }
                }
                if (isCollection) {
                    if (def.cardinality == null) {
                        def.cardinality = ReferenceCardinality.MULTIPLE;
                    }
                    def.fieldCollectionType = fieldCollectionType;
                }
                if (def.policy == ReferencePolicy.DYNAMIC && (def.cardinality == ReferenceCardinality.MULTIPLE || def.cardinality == ReferenceCardinality.AT_LEAST_ONE) && this.member.isFinal()) {
                    if (def.fieldOption == FieldOption.REPLACE) {
                        this.analyzer.error("In component %s, collection type field: %s is final and dynamic but marked with 'replace' fieldOption. Changing this to 'update'.", this.className, def.field).details(this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.DYNAMIC_FINAL_FIELD_WITH_REPLACE));
                    }
                    def.fieldOption = FieldOption.UPDATE;
                }
                if (annoService == null && index < sigs.length) {
                    annoService = sigs[index].substring(1).replace('/', '.');
                }
                def.service = annoService;
                if (def.service == null) {
                    this.analyzer.error("In component %s, method %s,  cannot recognize the signature of the descriptor: %s", this.component.effectiveName(), def.name, this.member.getDescriptor()).details(details);
                }
            }
        } else {
            def.service = annoService;
            if (def.name == null) {
                this.analyzer.error("Name must be supplied for a @Reference specified in the @Component annotation. Service: %s", def.service).details(this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.MISSING_REFERENCE_NAME));
                return;
            }
        }
        if (this.component.references.containsKey(def.name)) {
            this.analyzer.error("In component %s, multiple references with the same name: %s. Previous def: %s, this def: %s", this.className, this.component.references.get(def.name), def.service, "").details(this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.MULTIPLE_REFERENCES_SAME_NAME));
        } else {
            this.component.references.put(def.name, def);
        }
    }

    private DeclarativeServicesAnnotationError getDetails(ReferenceDef def, DeclarativeServicesAnnotationError.ErrorType type) {
        if (def == null) {
            return null;
        }
        return new DeclarativeServicesAnnotationError(this.className.getFQN(), def.bind, def.bindDescriptor, type);
    }

    private boolean sufficientGenerics(int index, int sigLength, ReferenceDef def, String sig) {
        if (index + 1 > sigLength) {
            this.analyzer.error("In component %s, method %s,  signature: %s does not have sufficient generic type information", this.component.effectiveName(), def.name, sig);
            return false;
        }
        return true;
    }

    private String determineReferenceType(String methodDescriptor, ReferenceDef def, String annoService, String signature) {
        String[] sigs;
        int start;
        boolean hasMapReturnType;
        String inferredService = null;
        String plainType = null;
        Version minVersion = null;
        DeclarativeServicesAnnotationError details = this.getDetails(def, DeclarativeServicesAnnotationError.ErrorType.REFERENCE);
        Matcher m = BINDDESCRIPTORDS10.matcher(methodDescriptor);
        if (m.matches()) {
            inferredService = Descriptors.binaryToFQN(m.group(1));
            if (m.group(3) == null && this.noMatch(annoService, inferredService) && m.group(7) == null) {
                minVersion = V1_3;
            }
            if (m.group(3) != null) {
                plainType = "Lorg/osgi/framework/ServiceReference<";
                inferredService = null;
            } else if (m.group(4) != null) {
                plainType = "Lorg/osgi/service/component/ComponentServiceObjects<";
                inferredService = null;
            } else if (m.group(5) != null) {
                plainType = "Ljava/util/Map$Entry<Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;";
                inferredService = null;
            } else if (m.group(6) != null) {
                inferredService = null;
            }
            hasMapReturnType = m.group(9) != null;
        } else {
            m = BINDDESCRIPTORDS11.matcher(methodDescriptor);
            if (m.matches()) {
                inferredService = Descriptors.binaryToFQN(m.group(1));
                minVersion = V1_1;
                hasMapReturnType = m.group(4) != null;
            } else {
                m = BINDDESCRIPTORDS13.matcher(methodDescriptor);
                if (m.matches()) {
                    inferredService = m.group(7);
                    if (inferredService != null) {
                        inferredService = Descriptors.binaryToFQN(inferredService);
                    }
                    minVersion = V1_3;
                    if (ReferenceScope.PROTOTYPE != def.scope && m.group(3) != null) {
                        this.analyzer.error("In component %s, to use ComponentServiceObjects the scope must be 'prototype'", this.component.implementation, "").details(details);
                    }
                    if (annoService == null) {
                        if (m.group(2) != null) {
                            plainType = "Lorg/osgi/framework/ServiceReference<";
                        } else if (m.group(3) != null) {
                            plainType = "Lorg/osgi/service/component/ComponentServiceObjects<";
                        } else if (m.group(5) != null) {
                            plainType = "Ljava/util/Map$Entry<Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;";
                        }
                    }
                    hasMapReturnType = m.group(9) != null;
                } else {
                    return null;
                }
            }
        }
        String service = annoService;
        if (inferredService == null && signature != null && plainType != null && (start = signature.indexOf(plainType)) > -1 && (sigs = signature.substring(start += plainType.length()).split("[<;>]")).length > 0) {
            String sig = sigs[0];
            if (sig.startsWith("-")) {
                inferredService = Object.class.getName();
            } else {
                int index = sig.startsWith("+") ? 2 : 1;
                inferredService = sig.substring(index).replace('/', '.');
            }
        }
        if (!this.analyzer.assignable(annoService, inferredService)) {
            return null;
        }
        if (service == null) {
            service = inferredService;
        }
        this.checkMapReturnType(hasMapReturnType, details);
        if (minVersion != null) {
            def.updateVersion(minVersion);
        }
        return service;
    }

    private void checkMapReturnType(boolean hasMapReturnType, DeclarativeServicesAnnotationError details) {
        if (hasMapReturnType && !this.options.contains((Object)DSAnnotations.Options.felixExtensions)) {
            this.analyzer.error("In component %s, to use a return type of Map you must specify the -dsannotations-options felixExtensions flag  and use a felix extension attribute or explicitly specify the appropriate xmlns.", this.component.implementation, "").details(details);
        }
    }

    private boolean noMatch(String annoService, String inferredService) {
        if (annoService == null) {
            return false;
        }
        return !annoService.equals(inferredService);
    }

    protected void doComponent(Component comp, Annotation annotation) throws Exception {
        String[] properties;
        if (!this.mismatchedAnnotations.isEmpty()) {
            String componentName = comp.name();
            componentName = componentName == null ? this.className.getFQN() : componentName;
            for (Map.Entry<String, List<DeclarativeServicesAnnotationError>> e : this.mismatchedAnnotations.entrySet()) {
                for (DeclarativeServicesAnnotationError errorDetails : e.getValue()) {
                    if (errorDetails.fieldName != null) {
                        this.analyzer.error("The DS component %s uses standard annotations to declare it as a component, but also uses the bnd DS annotation: %s on field %s. It is an error to mix these two types of annotations", componentName, e.getKey(), errorDetails.fieldName).details(errorDetails);
                        continue;
                    }
                    if (errorDetails.methodName != null) {
                        this.analyzer.error("The DS component %s uses standard annotations to declare it as a component, but also uses the bnd DS annotation: %s on method %s with signature %s. It is an error to mix these two types of annotations", componentName, e.getKey(), errorDetails.methodName, errorDetails.methodSignature).details(errorDetails);
                        continue;
                    }
                    this.analyzer.error("The DS component %s uses standard annotations to declare it as a component, but also uses the bnd DS annotation: %s. It is an error to mix these two types of annotations", componentName, e.getKey()).details(errorDetails);
                }
            }
            return;
        }
        if (this.component.implementation != null) {
            return;
        }
        this.component.implementation = this.clazz.getClassName();
        this.component.name = comp.name();
        this.component.factory = comp.factory();
        this.component.configurationPolicy = comp.configurationPolicy();
        if (annotation.get("enabled") != null) {
            this.component.enabled = comp.enabled();
        }
        if (annotation.get("factory") != null) {
            this.component.factory = comp.factory();
        }
        if (annotation.get("immediate") != null) {
            this.component.immediate = comp.immediate();
        }
        if (annotation.get("servicefactory") != null) {
            ServiceScope serviceScope = this.component.scope = comp.servicefactory() ? ServiceScope.BUNDLE : ServiceScope.SINGLETON;
        }
        if (annotation.get("scope") != null && comp.scope() != ServiceScope.DEFAULT) {
            this.component.scope = comp.scope();
            if (comp.scope() == ServiceScope.PROTOTYPE) {
                this.component.updateVersion(V1_3);
            }
        }
        if (annotation.get("configurationPid") != null) {
            this.component.configurationPid = comp.configurationPid();
            if (this.component.configurationPid.length > 1) {
                this.component.updateVersion(V1_3);
            } else {
                this.component.updateVersion(V1_2);
            }
        }
        if (annotation.get("xmlns") != null) {
            this.component.xmlns = comp.xmlns();
        }
        if ((properties = comp.properties()) != null) {
            for (String entry : properties) {
                if (entry.contains("=")) {
                    this.analyzer.error("Found an = sign in an OSGi DS Component annotation on %s. In the bnd annotation this is an actual property but in the OSGi, this element must refer to a path with Java properties. However, found a path with an '=' sign which looks like a mixup (%s) with the 'property' element.", this.clazz, entry).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), null, null, DeclarativeServicesAnnotationError.ErrorType.COMPONENT_PROPERTIES_ERROR));
                }
                this.component.properties.add(entry);
            }
        }
        this.doProperty(comp.property());
        Object[] x = (Object[])annotation.get("service");
        if (x == null) {
            if (this.interfaces != null) {
                ArrayList<Descriptors.TypeRef> result = new ArrayList<Descriptors.TypeRef>();
                for (int i = 0; i < this.interfaces.length; ++i) {
                    if (this.interfaces[i].equals(this.analyzer.getTypeRef("scala/ScalaObject"))) continue;
                    result.add(this.interfaces[i]);
                }
                this.component.service = result.toArray(EMPTY);
            }
        } else {
            this.component.service = new Descriptors.TypeRef[x.length];
            for (int i = 0; i < x.length; ++i) {
                Descriptors.TypeRef typeRef = (Descriptors.TypeRef)x[i];
                Clazz service = this.analyzer.findClass(typeRef);
                if (!this.analyzer.assignable(this.clazz, service)) {
                    this.analyzer.error("Class %s is not assignable to specified service %s", this.clazz.getFQN(), typeRef.getFQN()).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), null, null, DeclarativeServicesAnnotationError.ErrorType.INCOMPATIBLE_SERVICE));
                }
                this.component.service[i] = typeRef;
            }
        }
        this.member = null;
        Object[] refAnnotations = (Object[])annotation.get("reference");
        if (refAnnotations != null) {
            for (Object o : refAnnotations) {
                Annotation refAnnotation = (Annotation)o;
                Reference ref = (Reference)refAnnotation.getAnnotation();
                this.doReference(ref, refAnnotation);
            }
        }
    }

    private void doProperty(String[] properties) {
        if (properties != null && properties.length > 0) {
            MultiMap<String, String> props = new MultiMap<String, String>();
            for (String p : properties) {
                Matcher m = PROPERTY_PATTERN.matcher(p);
                if (m.matches()) {
                    String key = m.group(1);
                    String type = m.group(2);
                    if (type == null) {
                        type = "String";
                    }
                    this.component.propertyType.put(key, type);
                    String value = m.group(3);
                    props.add(key, value);
                    continue;
                }
                this.analyzer.error("Malformed property '%s' on component: %s", p, this.className);
            }
            this.component.property.putAll(props);
        }
    }

    @Override
    public void classBegin(int access, Descriptors.TypeRef name) {
        this.className = name;
    }

    @Override
    public void implementsInterfaces(Descriptors.TypeRef[] interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public void method(Clazz.MethodDef method) {
        if (method.isAbstract() || method.isStatic() || method.isBridge()) {
            return;
        }
        if (!this.baseclass && method.isPrivate()) {
            return;
        }
        this.member = method;
        this.methods.add(method.getName(), method);
    }

    @Override
    public void field(Clazz.FieldDef field) {
        this.member = field;
    }

    @Override
    public void extendsClass(Descriptors.TypeRef name) {
        this.extendsClass = name;
    }

    static {
        HashMap<String, Class> map = new HashMap<String, Class>();
        map.put("boolean", Boolean.class);
        map.put("byte", Byte.class);
        map.put("short", Short.class);
        map.put("char", Character.class);
        map.put("int", Integer.class);
        map.put("long", Long.class);
        map.put("float", Float.class);
        map.put("double", Double.class);
        wrappers = Collections.unmodifiableMap(map);
    }

    private final class ComponentPropertyTypeDataCollector
    extends ClassDataCollector {
        private final String methodDescriptor;
        private final DeclarativeServicesAnnotationError details;
        private final MultiMap<String, String> props = new MultiMap();
        private final Map<String, String> propertyTypes = new HashMap<String, String>();
        private int hasNoDefault = 0;
        private boolean hasValue = false;
        private Clazz.FieldDef prefixField = null;
        private Descriptors.TypeRef typeRef = null;

        private ComponentPropertyTypeDataCollector(String methodDescriptor, DeclarativeServicesAnnotationError details) {
            this.methodDescriptor = methodDescriptor;
            this.details = details;
        }

        @Override
        public void classBegin(int access, Descriptors.TypeRef name) {
            this.typeRef = name;
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
                return;
            }
            if (defined.getName().equals("value")) {
                this.hasValue = true;
            } else {
                ++this.hasNoDefault;
            }
        }

        @Override
        public void annotationDefault(Clazz.MethodDef defined, Object value) {
            if (!defined.getName().equals("value")) {
                --this.hasNoDefault;
            }
            boolean isClass = false;
            Class<?> typeClass = null;
            Descriptors.TypeRef type = defined.getType().getClassRef();
            if (!type.isPrimitive()) {
                if (type == AnnotationReader.this.analyzer.getTypeRef("java/lang/Class")) {
                    isClass = true;
                } else {
                    try {
                        Clazz r = AnnotationReader.this.analyzer.findClass(type);
                        if (r.isAnnotation()) {
                            AnnotationReader.this.analyzer.warning("Nested annotation type found in member %s, %s", defined.getName(), type.getFQN()).details(this.details);
                            return;
                        }
                    }
                    catch (Exception e) {
                        AnnotationReader.this.analyzer.exception(e, "Exception looking at annotation type to lifecycle method with descriptor %s,  type %s", this.methodDescriptor, type).details(this.details);
                    }
                }
            } else {
                typeClass = wrappers.get(type.getFQN());
            }
            if (value != null) {
                String name = defined.getName();
                if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; ++i) {
                        Object element = Array.get(value, i);
                        this.valueToProperty(name, element, isClass, typeClass);
                    }
                    if (len == 1) {
                        this.props.add(name, ComponentDef.MARKER);
                    }
                } else {
                    this.valueToProperty(name, value, isClass, typeClass);
                }
            }
        }

        @Override
        public void classEnd() throws Exception {
            String prefix = null;
            if (this.prefixField != null) {
                Object c = this.prefixField.getConstant();
                if (this.prefixField.isFinal() && this.prefixField.getType() == AnnotationReader.this.analyzer.getTypeRef("java/lang/String") && c instanceof String) {
                    prefix = (String)c;
                    AnnotationReader.this.component.updateVersion(V1_4);
                } else {
                    AnnotationReader.this.analyzer.warning("Field PREFIX_ in %s is not a static final String field with a compile-time constant value: %s", this.typeRef.getFQN(), c).details(this.details);
                }
            }
            String singleElementAnnotation = null;
            if (this.hasValue && this.hasNoDefault == 0) {
                StringBuilder sb = new StringBuilder(this.typeRef.getShorterName());
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
                AnnotationReader.this.component.updateVersion(V1_4);
            }
            for (Map.Entry entry : this.props.entrySet()) {
                String key = (String)entry.getKey();
                List value = (List)entry.getValue();
                String type = this.propertyTypes.get(key);
                key = singleElementAnnotation != null && key.equals("value") ? singleElementAnnotation : this.identifierToPropertyName(key);
                if (prefix != null) {
                    key = prefix + key;
                }
                AnnotationReader.this.component.property.put(key, (String)((Object)value));
                AnnotationReader.this.component.propertyType.put(key, type);
            }
        }

        private void valueToProperty(String name, Object value, boolean isClass, Class<?> typeClass) {
            if (isClass) {
                value = ((Descriptors.TypeRef)value).getFQN();
            }
            if (typeClass == null) {
                typeClass = value.getClass();
            }
            String type = typeClass.getSimpleName();
            this.propertyTypes.put(name, type);
            this.props.add(name, value.toString());
        }

        private String identifierToPropertyName(String name) {
            Matcher m = IDENTIFIERTOPROPERTY.matcher(name);
            StringBuffer b = new StringBuffer();
            while (m.find()) {
                String replace;
                if (m.group(1) != null) {
                    replace = "_";
                } else if (m.group(2) != null) {
                    replace = ".";
                } else if (m.group(3) != null) {
                    replace = "-";
                    AnnotationReader.this.component.updateVersion(V1_4);
                } else {
                    replace = m.group(4) != null ? "\\$" : "";
                }
                m.appendReplacement(b, replace);
            }
            m.appendTail(b);
            return b.toString();
        }
    }
}

