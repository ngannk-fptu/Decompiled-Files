/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.make.component;

import aQute.bnd.component.error.DeclarativeServicesAnnotationError;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.service.reporter.Reporter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentAnnotationReader
extends ClassDataCollector {
    private static final Logger logger = LoggerFactory.getLogger(ComponentAnnotationReader.class);
    String[] EMPTY = new String[0];
    private static final String V1_1 = "1.1.0";
    static Pattern BINDDESCRIPTOR = Pattern.compile("\\(L([^;]*);(Ljava/util/Map;|Lorg/osgi/framework/ServiceReference;)*\\)V");
    static Pattern BINDMETHOD = Pattern.compile("(set|bind|add)(.)(.*)");
    static Pattern ACTIVATEDESCRIPTOR = Pattern.compile("\\(((Lorg/osgi/service/component/ComponentContext;)|(Lorg/osgi/framework/BundleContext;)|(Ljava/util/Map;))*\\)V");
    static Pattern OLDACTIVATEDESCRIPTOR = Pattern.compile("\\(Lorg/osgi/service/component/ComponentContext;\\)V");
    static Pattern OLDBINDDESCRIPTOR = Pattern.compile("\\(L([^;]*);\\)V");
    static Pattern REFERENCEBINDDESCRIPTOR = Pattern.compile("\\(Lorg/osgi/framework/ServiceReference;\\)V");
    static String[] ACTIVATE_ARGUMENTS = new String[]{"org.osgi.service.component.ComponentContext", "org.osgi.framework.BundleContext", Map.class.getName(), "org.osgi.framework.BundleContext"};
    static String[] OLD_ACTIVATE_ARGUMENTS = new String[]{"org.osgi.service.component.ComponentContext"};
    Reporter reporter = new Processor();
    Clazz.MethodDef method;
    Clazz.FieldDef field;
    Descriptors.TypeRef className;
    Clazz clazz;
    Descriptors.TypeRef[] interfaces;
    Set<String> multiple = new HashSet<String>();
    Set<String> optional = new HashSet<String>();
    Set<String> dynamic = new HashSet<String>();
    Map<String, String> map = new TreeMap<String, String>();
    Set<String> descriptors = new HashSet<String>();
    List<String> properties = new ArrayList<String>();
    String version = null;
    Map<String, List<DeclarativeServicesAnnotationError>> mismatchedAnnotations = new HashMap<String, List<DeclarativeServicesAnnotationError>>();
    static Pattern PROPERTY_PATTERN = Pattern.compile("\\s*([^=\\s]+)\\s*=(.+)");

    ComponentAnnotationReader(Clazz clazz) {
        this.clazz = clazz;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public Reporter getReporter() {
        return this.reporter;
    }

    public static Map<String, String> getDefinition(Clazz c) throws Exception {
        try (Processor p = new Processor();){
            Map<String, String> map = ComponentAnnotationReader.getDefinition(c, p);
            return map;
        }
    }

    public static Map<String, String> getDefinition(Clazz c, Reporter reporter) throws Exception {
        ComponentAnnotationReader r = new ComponentAnnotationReader(c);
        r.setReporter(reporter);
        c.parseClassFileWithCollector(r);
        r.finish();
        return r.map;
    }

    @Override
    public void annotation(Annotation annotation) {
        String fqn = annotation.getName().getFQN();
        if (fqn.equals("aQute.bnd.annotation.component.Component")) {
            String[] p;
            Descriptors.TypeRef configs;
            if (!this.mismatchedAnnotations.isEmpty()) {
                String componentName = (String)annotation.get("name");
                componentName = componentName == null ? this.className.getFQN() : componentName;
                for (Map.Entry<String, List<DeclarativeServicesAnnotationError>> e : this.mismatchedAnnotations.entrySet()) {
                    for (DeclarativeServicesAnnotationError errorDetails : e.getValue()) {
                        if (errorDetails.fieldName != null) {
                            this.reporter.error("The DS component %s uses bnd annotations to declare it as a component, but also uses the standard DS annotation: %s on field %s. It is an error to mix these two types of annotations", componentName, e.getKey(), errorDetails.fieldName).details(errorDetails);
                            continue;
                        }
                        if (errorDetails.methodName != null) {
                            this.reporter.error("The DS component %s uses bnd annotations to declare it as a component, but also uses the standard DS annotation: %s on method %s with signature %s. It is an error to mix these two types of annotations", componentName, e.getKey(), errorDetails.methodName, errorDetails.methodSignature).details(errorDetails);
                            continue;
                        }
                        this.reporter.error("The DS component %s uses bnd annotations to declare it as a component, but also uses the standard DS annotation: %s. It is an error to mix these two types of annotations", componentName, e.getKey()).details(errorDetails);
                    }
                }
                return;
            }
            this.set("name:", annotation.get("name"), "<>");
            this.set("factory:", annotation.get("factory"), false);
            this.setBoolean("enabled:", annotation.get("enabled"), true);
            this.setBoolean("immediate:", annotation.get("immediate"), false);
            this.setBoolean("servicefactory:", annotation.get("servicefactory"), false);
            if (annotation.get("designate") != null && (configs = (Descriptors.TypeRef)annotation.get("designate")) != null) {
                this.set("designate:", configs.getFQN(), "");
            }
            if (annotation.get("designateFactory") != null && (configs = (Descriptors.TypeRef)annotation.get("designateFactory")) != null) {
                this.set("designateFactory:", configs.getFQN(), "");
            }
            this.setVersion((String)annotation.get("version"));
            String configurationPolicy = (String)annotation.get("configurationPolicy");
            if (configurationPolicy != null) {
                this.set("configuration-policy:", configurationPolicy.toLowerCase(), "");
            }
            this.doProperties(annotation);
            Object[] provides = (Object[])annotation.get("provide");
            if (provides == null) {
                if (this.interfaces != null) {
                    ArrayList<String> result = new ArrayList<String>();
                    for (int i = 0; i < this.interfaces.length; ++i) {
                        if (this.interfaces[i].getBinary().equals("scala/ScalaObject")) continue;
                        result.add(this.interfaces[i].getFQN());
                    }
                    p = result.toArray(this.EMPTY);
                } else {
                    p = this.EMPTY;
                }
            } else {
                p = new String[provides.length];
                for (int i = 0; i < provides.length; ++i) {
                    p[i] = ((Descriptors.TypeRef)provides[i]).getFQN();
                }
            }
            if (p.length > 0) {
                this.set("provide:", Processor.join(Arrays.asList(p)), "<>");
            }
        } else if (fqn.equals("aQute.bnd.annotation.component.Activate")) {
            if (!this.checkMethod()) {
                this.setVersion(V1_1);
            }
            if (!ACTIVATEDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches()) {
                this.reporter.error("Activate method for %s does not have an acceptable prototype, only Map, ComponentContext, or BundleContext is allowed. Found: %s", this.className, this.method.getDescriptor()).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), this.method.getName(), this.method.getDescriptor().toString(), DeclarativeServicesAnnotationError.ErrorType.ACTIVATE_SIGNATURE_ERROR));
            }
            if (!this.method.getName().equals("activate") || !OLDACTIVATEDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches()) {
                this.setVersion(V1_1);
                this.set("activate:", this.method, "<>");
            }
        } else if (fqn.equals("aQute.bnd.annotation.component.Deactivate")) {
            if (!this.checkMethod()) {
                this.setVersion(V1_1);
            }
            if (!ACTIVATEDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches()) {
                this.reporter.error("Deactivate method for %s does not have an acceptable prototype, only Map, ComponentContext, or BundleContext is allowed. Found: %s", this.className, this.method.getDescriptor()).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), this.method.getName(), this.method.getDescriptor().toString(), DeclarativeServicesAnnotationError.ErrorType.DEACTIVATE_SIGNATURE_ERROR));
            }
            if (!this.method.getName().equals("deactivate") || !OLDACTIVATEDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches()) {
                this.setVersion(V1_1);
                this.set("deactivate:", this.method, "<>");
            }
        } else if (fqn.equals("aQute.bnd.annotation.component.Modified")) {
            if (!ACTIVATEDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches()) {
                this.reporter.error("Modified method for %s does not have an acceptable prototype, only Map, ComponentContext, or BundleContext is allowed. Found: %s", this.className, this.method.getDescriptor()).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), this.method.getName(), this.method.getDescriptor().toString(), DeclarativeServicesAnnotationError.ErrorType.MODIFIED_SIGNATURE_ERROR));
            }
            this.set("modified:", this.method, "<>");
            this.setVersion(V1_1);
        } else if (fqn.equals("aQute.bnd.annotation.component.Reference")) {
            Integer c;
            String target;
            String name = (String)annotation.get("aQute.bnd.annotation.component.Reference");
            String bind = this.method.getName();
            String unbind = null;
            if (name == null) {
                Matcher m = BINDMETHOD.matcher(this.method.getName());
                name = m.matches() ? m.group(2).toLowerCase() + m.group(3) : this.method.getName().toLowerCase();
            }
            String simpleName = name;
            unbind = (String)annotation.get("unbind");
            if (bind != null) {
                name = name + "/" + bind;
                if (unbind != null) {
                    name = name + "/" + unbind;
                }
            }
            String service = null;
            Descriptors.TypeRef serviceTR = (Descriptors.TypeRef)annotation.get("service");
            if (serviceTR != null) {
                service = serviceTR.getFQN();
            }
            if (service == null) {
                Matcher m = BINDDESCRIPTOR.matcher(this.method.getDescriptor().toString());
                if (m.matches()) {
                    service = Descriptors.binaryToFQN(m.group(1));
                } else {
                    throw new IllegalArgumentException("Cannot detect the type of a Component Reference from the descriptor: " + this.method.getDescriptor());
                }
            }
            if ((target = (String)annotation.get("target")) != null) {
                String error = Verifier.validateFilter(target);
                if (error != null) {
                    this.reporter.error("Invalid target filter %s for %s: %s", target, name, error).details(new DeclarativeServicesAnnotationError(this.className.getFQN(), bind, this.method.getDescriptor().toString(), DeclarativeServicesAnnotationError.ErrorType.INVALID_TARGET_FILTER));
                }
                service = service + target;
            }
            if ((c = (Integer)annotation.get("type")) != null && !c.equals(0) && !c.equals(49)) {
                service = service + (char)c.intValue();
            }
            if (this.map.containsKey(name)) {
                this.reporter.error("In component %s, Multiple references with the same name: %s. Previous def: %s, this def: %s", name, this.map.get(name), service, "").details(new DeclarativeServicesAnnotationError(this.className.getFQN(), null, null, DeclarativeServicesAnnotationError.ErrorType.MULTIPLE_REFERENCES_SAME_NAME));
            }
            this.map.put(name, service);
            if (this.isTrue(annotation.get("multiple"))) {
                this.multiple.add(simpleName);
            }
            if (this.isTrue(annotation.get("optional"))) {
                this.optional.add(simpleName);
            }
            if (this.isTrue(annotation.get("dynamic"))) {
                this.dynamic.add(simpleName);
            }
            if (!this.checkMethod()) {
                this.setVersion(V1_1);
            } else if (REFERENCEBINDDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches() || !OLDBINDDESCRIPTOR.matcher(this.method.getDescriptor().toString()).matches()) {
                this.setVersion(V1_1);
            }
        } else if (fqn.startsWith("org.osgi.service.component.annotations")) {
            DeclarativeServicesAnnotationError errorDetails;
            switch (annotation.getElementType()) {
                case METHOD: {
                    errorDetails = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.method.getName(), this.method.getDescriptor().toString(), DeclarativeServicesAnnotationError.ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_BND);
                    break;
                }
                case FIELD: {
                    errorDetails = new DeclarativeServicesAnnotationError(this.className.getFQN(), this.field.getName(), DeclarativeServicesAnnotationError.ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_BND);
                    break;
                }
                default: {
                    errorDetails = new DeclarativeServicesAnnotationError(this.className.getFQN(), null, DeclarativeServicesAnnotationError.ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_BND);
                }
            }
            List<DeclarativeServicesAnnotationError> errors = this.mismatchedAnnotations.get(fqn);
            if (errors == null) {
                errors = new ArrayList<DeclarativeServicesAnnotationError>();
                this.mismatchedAnnotations.put(fqn, errors);
            }
            errors.add(errorDetails);
        }
    }

    private void setVersion(String v) {
        if (v == null) {
            return;
        }
        if (this.version == null) {
            this.version = v;
        } else if (v.compareTo(this.version) > 0) {
            this.version = v;
        }
    }

    private boolean checkMethod() {
        return Modifier.isPublic(this.method.getAccess()) || Modifier.isProtected(this.method.getAccess());
    }

    private void doProperties(Annotation annotation) {
        Object[] properties = (Object[])annotation.get("properties");
        if (properties != null) {
            for (Object o : properties) {
                String p = (String)o;
                Matcher m = PROPERTY_PATTERN.matcher(p);
                if (!m.matches()) {
                    throw new IllegalArgumentException("Malformed property '" + p + "' on: " + annotation.get("name"));
                }
                this.properties.add(m.group(1) + "=" + m.group(2));
            }
        }
    }

    private boolean isTrue(Object object) {
        if (object == null) {
            return false;
        }
        return (Boolean)object;
    }

    private void setBoolean(String string, Object object, boolean b) {
        Boolean bb;
        if (object == null) {
            object = b;
        }
        if ((bb = (Boolean)object) == b) {
            return;
        }
        this.map.put(string, bb.toString());
    }

    private void set(String string, Object object, Object deflt) {
        if (object == null || object.equals(deflt)) {
            return;
        }
        this.map.put(string, object.toString());
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
        this.method = method;
        this.descriptors.add(method.getName());
    }

    @Override
    public void field(Clazz.FieldDef field) {
        this.field = field;
    }

    void set(String name, Collection<String> l) {
        if (l.size() == 0) {
            return;
        }
        this.set(name, Processor.join(l), "<>");
    }

    public void finish() {
        this.set("multiple:", this.multiple);
        this.set("dynamic:", this.dynamic);
        this.set("optional:", this.optional);
        this.set("implementation:", this.clazz.getFQN(), "<>");
        this.set("properties:", this.properties);
        if (this.version != null) {
            this.set("version:", this.version, "<>");
            logger.debug("Component {} is v1.1", this.map);
        }
        this.set(".descriptors:", this.descriptors);
    }
}

