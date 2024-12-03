/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.component.AnnotationReader;
import aQute.bnd.component.ComponentDef;
import aQute.bnd.component.ReferenceDef;
import aQute.bnd.component.error.DeclarativeServicesAnnotationError;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.version.Version;
import aQute.lib.tag.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

public class HeaderReader
extends Processor {
    static final Pattern PROPERTY_PATTERN = Pattern.compile("(([^=:@]+)([:@](Boolean|Byte|Char|Short|Integer|Long|Float|Double|String))?)\\s*=(.*)");
    private static final Set<String> LIFECYCLE_METHODS = new HashSet<String>(Arrays.asList("activate", "deactivate", "modified"));
    private final Analyzer analyzer;
    private static final String ComponentContextTR = "org.osgi.service.component.ComponentContext";
    private static final String BundleContextTR = "org.osgi.framework.BundleContext";
    private static final String MapTR = Map.class.getName();
    private static final String IntTR = Integer.TYPE.getName();
    static final Set<String> allowed = new HashSet<String>(Arrays.asList("org.osgi.service.component.ComponentContext", "org.osgi.framework.BundleContext", MapTR));
    static final Set<String> allowedDeactivate = new HashSet<String>(Arrays.asList("org.osgi.service.component.ComponentContext", "org.osgi.framework.BundleContext", MapTR, IntTR));
    private static final String ServiceReferenceTR = "org.osgi.framework.ServiceReference";
    public static final Pattern REFERENCE = Pattern.compile("([^(]+)(\\(.+\\))?");

    public HeaderReader(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Tag createComponentTag(String name, String impl, Map<String, String> info) throws Exception {
        final ComponentDef cd = new ComponentDef(null, AnnotationReader.V1_0);
        cd.name = name;
        if (info.get("enabled:") != null) {
            cd.enabled = Boolean.valueOf(info.get("enabled:"));
        }
        cd.factory = info.get("factory:");
        if (info.get("immediate:") != null) {
            cd.immediate = Boolean.valueOf(info.get("immediate:"));
        }
        if (info.get("configuration-policy:") != null) {
            cd.configurationPolicy = ConfigurationPolicy.valueOf(info.get("configuration-policy:").toUpperCase());
        }
        cd.activate = this.checkIdentifier("activate:", info.get("activate:"));
        cd.deactivate = this.checkIdentifier("deactivate:", info.get("deactivate:"));
        cd.modified = this.checkIdentifier("modified:", info.get("modified:"));
        cd.implementation = this.analyzer.getTypeRefFromFQN(impl == null ? name : impl);
        String provides = info.get("provide:");
        if (info.get("servicefactory:") != null) {
            if (provides != null) {
                cd.scope = Boolean.valueOf(info.get("servicefactory:")) != false ? ServiceScope.BUNDLE : ServiceScope.SINGLETON;
            } else {
                this.warning("The servicefactory:=true directive is set but no service is provided, ignoring it", new Object[0]);
            }
        }
        if (cd.scope == ServiceScope.BUNDLE && cd.immediate != null && cd.immediate.booleanValue()) {
            this.warning("For a Service Component, the immediate option and the servicefactory option are mutually exclusive for %s(%s)", name, impl);
        }
        final HashMap<String, Clazz.MethodDef> lifecycleMethods = new HashMap<String, Clazz.MethodDef>();
        final HashMap<String, Clazz.MethodDef> bindmethods = new HashMap<String, Clazz.MethodDef>();
        Descriptors.TypeRef typeRef = this.analyzer.getTypeRefFromFQN(impl);
        Clazz clazz = this.analyzer.findClass(typeRef);
        boolean privateAllowed = true;
        boolean defaultAllowed = true;
        String topPackage = typeRef.getPackageRef().getFQN();
        while (clazz != null) {
            final boolean pa = privateAllowed;
            final boolean da = defaultAllowed;
            final HashMap classLifecyclemethods = new HashMap();
            final HashMap classBindmethods = new HashMap();
            clazz.parseClassFileWithCollector(new ClassDataCollector(){

                @Override
                public void method(Clazz.MethodDef md) {
                    boolean isLifecycle;
                    Set<String> allowedParams = allowed;
                    Object lifecycleName = null;
                    boolean bl = isLifecycle = (cd.activate == null ? "activate" : cd.activate).equals(md.getName()) || md.getName().equals(cd.modified);
                    if (!isLifecycle && (cd.deactivate == null ? "deactivate" : cd.deactivate).equals(md.getName())) {
                        isLifecycle = true;
                        allowedParams = allowedDeactivate;
                    }
                    if (isLifecycle && !lifecycleMethods.containsKey(md.getName()) && (md.isPublic() || md.isProtected() || md.isPrivate() && pa || !md.isPrivate() && da) && this.isBetter(md, (Clazz.MethodDef)classLifecyclemethods.get(md.getName()), allowedParams)) {
                        classLifecyclemethods.put(md.getName(), md);
                    }
                    if (!bindmethods.containsKey(md.getName()) && (md.isPublic() || md.isProtected() || md.isPrivate() && pa || !md.isPrivate() && da) && this.isBetterBind(md, (Clazz.MethodDef)classBindmethods.get(md.getName()))) {
                        classBindmethods.put(md.getName(), md);
                    }
                }

                private boolean isBetter(Clazz.MethodDef test, Clazz.MethodDef existing, Set<String> allowedParams) {
                    int testRating = HeaderReader.this.rateLifecycle(test, allowedParams);
                    if (existing == null) {
                        return testRating < 6;
                    }
                    return testRating < HeaderReader.this.rateLifecycle(existing, allowedParams);
                }

                private boolean isBetterBind(Clazz.MethodDef test, Clazz.MethodDef existing) {
                    int testRating = HeaderReader.this.rateBind(test);
                    if (existing == null) {
                        return testRating < 6;
                    }
                    return testRating < HeaderReader.this.rateBind(existing);
                }
            });
            lifecycleMethods.putAll(classLifecyclemethods);
            bindmethods.putAll(classBindmethods);
            typeRef = clazz.getSuper();
            if (typeRef == null) break;
            clazz = this.analyzer.findClass(typeRef);
            privateAllowed = false;
            defaultAllowed = defaultAllowed && topPackage.equals(typeRef.getPackageRef().getFQN());
        }
        if (cd.activate != null && !lifecycleMethods.containsKey(cd.activate)) {
            this.error("in component %s, activate method %s specified but not found", cd.implementation.getFQN(), cd.activate);
            cd.activate = null;
        }
        if (cd.deactivate != null && !lifecycleMethods.containsKey(cd.deactivate)) {
            this.error("in component %s, deactivate method %s specified but not found", cd.implementation.getFQN(), cd.deactivate);
            cd.activate = null;
        }
        if (cd.modified != null && !lifecycleMethods.containsKey(cd.modified)) {
            this.error("in component %s, modified method %s specified but not found", cd.implementation.getFQN(), cd.modified);
            cd.activate = null;
        }
        this.provide(cd, provides, impl);
        this.properties(cd, info, name);
        this.reference(info, impl, cd, bindmethods);
        this.getNamespace(info, cd, lifecycleMethods);
        cd.prepare(this.analyzer);
        return cd.getTag();
    }

    private String checkIdentifier(String name, String value) {
        if (value != null && !Verifier.isIdentifier(value)) {
            this.error("Component attribute %s has value %s but is not a Java identifier", name, value);
            return null;
        }
        return value;
    }

    private void getNamespace(Map<String, String> info, ComponentDef cd, Map<String, Clazz.MethodDef> descriptors) {
        String version;
        String namespace = info.get("xmlns:");
        if (namespace != null) {
            cd.xmlns = namespace;
        }
        if ((version = info.get("version:")) != null) {
            try {
                Version v = new Version(version);
                cd.updateVersion(v);
            }
            catch (Exception e) {
                this.error("version: specified on component header but not a valid version: %s", version);
                return;
            }
        }
        for (String key : info.keySet()) {
            if (!SET_COMPONENT_DIRECTIVES_1_2.contains(key)) continue;
            cd.updateVersion(AnnotationReader.V1_2);
            return;
        }
        for (ReferenceDef rd : cd.references.values()) {
            if (rd.updated == null) continue;
            cd.updateVersion(AnnotationReader.V1_2);
            return;
        }
        for (String key : info.keySet()) {
            if (!SET_COMPONENT_DIRECTIVES_1_1.contains(key)) continue;
            cd.updateVersion(AnnotationReader.V1_1);
            return;
        }
        for (String lifecycle : LIFECYCLE_METHODS) {
            Clazz.MethodDef test = descriptors.get(lifecycle);
            if (!descriptors.containsKey(lifecycle) || (test.isPublic() || test.isProtected()) && this.rateLifecycle(test, "deactivate".equals(lifecycle) ? allowedDeactivate : allowed) <= 1) continue;
            cd.updateVersion(AnnotationReader.V1_1);
            return;
        }
    }

    void properties(ComponentDef cd, Map<String, String> info, String name) {
        Collection<String> properties = HeaderReader.split(info.get("properties:"));
        for (String p : properties) {
            Matcher m = PROPERTY_PATTERN.matcher(p);
            if (m.matches()) {
                String value;
                String[] parts;
                String key = m.group(2);
                String type = m.group(4);
                if (type == null) {
                    type = "String";
                }
                if ((parts = (value = m.group(5)).split("\\s*(\\||\\n)\\s*")).length == 1 && value.endsWith("|")) {
                    String v = parts[0];
                    parts = new String[]{v, ComponentDef.MARKER};
                }
                cd.propertyType.put(key, type);
                for (String part : parts) {
                    cd.property.add(key, part);
                }
                continue;
            }
            throw new IllegalArgumentException("Malformed property '" + p + "' on component: " + name);
        }
    }

    void provide(ComponentDef cd, String provides, String impl) {
        if (provides != null) {
            StringTokenizer st = new StringTokenizer(provides, ",");
            ArrayList<Descriptors.TypeRef> provide = new ArrayList<Descriptors.TypeRef>();
            while (st.hasMoreTokens()) {
                String interfaceName = st.nextToken();
                Descriptors.TypeRef ref = this.analyzer.getTypeRefFromFQN(interfaceName);
                provide.add(ref);
                this.analyzer.referTo(ref);
            }
            cd.service = provide.toArray(new Descriptors.TypeRef[0]);
        }
    }

    int rateLifecycle(Clazz.MethodDef test, Set<String> allowedParams) {
        Descriptors.TypeRef[] prototype = test.getDescriptor().getPrototype();
        if (prototype.length == 1 && ComponentContextTR.equals(prototype[0].getFQN())) {
            return 1;
        }
        if (prototype.length == 1 && BundleContextTR.equals(prototype[0].getFQN())) {
            return 2;
        }
        if (prototype.length == 1 && MapTR.equals(prototype[0].getFQN())) {
            return 3;
        }
        if (prototype.length > 1) {
            for (Descriptors.TypeRef tr : prototype) {
                if (allowedParams.contains(tr.getFQN())) continue;
                return 6;
            }
            return 5;
        }
        if (prototype.length == 0) {
            return 5;
        }
        return 6;
    }

    int rateBind(Clazz.MethodDef test) {
        Descriptors.TypeRef[] prototype = test.getDescriptor().getPrototype();
        if (prototype.length == 1 && ServiceReferenceTR.equals(prototype[0].getFQN())) {
            return 1;
        }
        if (prototype.length == 1) {
            return 2;
        }
        if (prototype.length == 2 && MapTR.equals(prototype[1].getFQN())) {
            return 3;
        }
        return 6;
    }

    void reference(Map<String, String> info, String impl, ComponentDef cd, Map<String, Clazz.MethodDef> descriptors) throws Exception {
        ArrayList<String> dynamic = new ArrayList<String>(HeaderReader.split(info.get("dynamic:")));
        ArrayList<String> optional = new ArrayList<String>(HeaderReader.split(info.get("optional:")));
        ArrayList<String> multiple = new ArrayList<String>(HeaderReader.split(info.get("multiple:")));
        ArrayList<String> greedy = new ArrayList<String>(HeaderReader.split(info.get("greedy:")));
        for (Map.Entry<String, String> entry : info.entrySet()) {
            char c;
            String referenceName = entry.getKey();
            if (referenceName.endsWith(":")) {
                if (SET_COMPONENT_DIRECTIVES.contains(referenceName)) continue;
                this.error("Unrecognized directive in Service-Component header: %s", referenceName);
                continue;
            }
            String bind = null;
            String unbind = null;
            String updated = null;
            boolean bindCalculated = true;
            boolean unbindCalculated = true;
            boolean updatedCalculated = true;
            if (referenceName.indexOf(47) >= 0) {
                String[] parts = referenceName.split("/");
                referenceName = parts[0];
                if (parts[1].length() > 0) {
                    bind = parts[1];
                    bindCalculated = false;
                } else {
                    bind = this.calculateBind(referenceName);
                }
                String string = bind = parts[1].length() == 0 ? this.calculateBind(referenceName) : parts[1];
                if (parts.length > 2 && parts[2].length() > 0) {
                    unbind = parts[2];
                    unbindCalculated = false;
                } else {
                    unbind = bind.startsWith("add") ? bind.replaceAll("add(.+)", "remove$1") : "un" + bind;
                }
                if (parts.length > 3) {
                    updated = parts[3];
                    updatedCalculated = false;
                }
            } else if (Character.isLowerCase(referenceName.charAt(0))) {
                bind = this.calculateBind(referenceName);
                unbind = "un" + bind;
                updated = "updated" + Character.toUpperCase(referenceName.charAt(0)) + referenceName.substring(1);
            }
            String interfaceName = entry.getValue();
            if (interfaceName == null || interfaceName.length() == 0) {
                this.error("Invalid Interface Name for references in Service Component: %s=%s", referenceName, interfaceName);
                continue;
            }
            if (descriptors.size() > 0) {
                if (!descriptors.containsKey(bind)) {
                    if (bindCalculated) {
                        bind = null;
                    } else {
                        this.error("In component %s, the bind method %s for %s not defined", cd.effectiveName(), bind, referenceName);
                    }
                }
                if (!descriptors.containsKey(unbind)) {
                    if (unbindCalculated) {
                        unbind = null;
                    } else {
                        this.error("In component %s, the unbind method %s for %s not defined", cd.effectiveName(), unbind, referenceName);
                    }
                }
                if (!descriptors.containsKey(updated)) {
                    if (updatedCalculated) {
                        updated = null;
                    } else {
                        this.error("In component %s, the updated method %s for %s is not defined", cd.effectiveName(), updated, referenceName);
                    }
                }
            }
            if ("?+*~".indexOf(c = interfaceName.charAt(interfaceName.length() - 1)) >= 0) {
                if (c == '?' || c == '*' || c == '~') {
                    optional.add(referenceName);
                }
                if (c == '+' || c == '*') {
                    multiple.add(referenceName);
                }
                if (c == '+' || c == '*' || c == '?') {
                    dynamic.add(referenceName);
                }
                interfaceName = interfaceName.substring(0, interfaceName.length() - 1);
            }
            String target = null;
            Matcher m = REFERENCE.matcher(interfaceName);
            if (m.matches()) {
                interfaceName = m.group(1);
                target = m.group(2);
            }
            Descriptors.TypeRef ref = this.analyzer.getTypeRefFromFQN(interfaceName);
            this.analyzer.referTo(ref);
            ReferenceDef rd = new ReferenceDef(null);
            rd.name = referenceName;
            rd.service = interfaceName;
            rd.cardinality = optional.contains(referenceName) ? (multiple.contains(referenceName) ? ReferenceCardinality.MULTIPLE : ReferenceCardinality.OPTIONAL) : (multiple.contains(referenceName) ? ReferenceCardinality.AT_LEAST_ONE : ReferenceCardinality.MANDATORY);
            if (bind != null) {
                rd.bind = bind;
                if (unbind != null) {
                    rd.unbind = unbind;
                }
                if (updated != null) {
                    rd.updated = updated;
                }
            }
            if (dynamic.contains(referenceName)) {
                rd.policy = ReferencePolicy.DYNAMIC;
                if (rd.unbind == null) {
                    this.error("In component %s, reference %s is dynamic but has no unbind method.", cd.effectiveName(), rd.name).details(new DeclarativeServicesAnnotationError(cd.implementation.getFQN(), null, null, DeclarativeServicesAnnotationError.ErrorType.DYNAMIC_REFERENCE_WITHOUT_UNBIND));
                }
            }
            if (greedy.contains(referenceName)) {
                rd.policyOption = ReferencePolicyOption.GREEDY;
            }
            if (target != null) {
                rd.target = target;
            }
            cd.references.put(referenceName, rd);
        }
    }

    private String calculateBind(String referenceName) {
        return "set" + Character.toUpperCase(referenceName.charAt(0)) + referenceName.substring(1);
    }
}

