/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.clauses;

import aQute.bnd.build.model.clauses.ComponentSvcReference;
import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.header.Attrs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceComponent
extends HeaderClause
implements Cloneable {
    public static final String COMPONENT_FACTORY = "factory:";
    public static final String COMPONENT_SERVICEFACTORY = "servicefactory:";
    public static final String COMPONENT_IMMEDIATE = "immediate:";
    public static final String COMPONENT_ENABLED = "enabled:";
    public static final String COMPONENT_DYNAMIC = "dynamic:";
    public static final String COMPONENT_MULTIPLE = "multiple:";
    public static final String COMPONENT_PROVIDE = "provide:";
    public static final String COMPONENT_OPTIONAL = "optional:";
    public static final String COMPONENT_PROPERTIES = "properties:";
    public static final String COMPONENT_VERSION = "version:";
    public static final String COMPONENT_CONFIGURATION_POLICY = "configuration-policy:";
    public static final String COMPONENT_MODIFIED = "modified:";
    public static final String COMPONENT_ACTIVATE = "activate:";
    public static final String COMPONENT_DEACTIVATE = "deactivate:";
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("([^(]+)(\\(.+\\))?");

    public ServiceComponent(String name, Attrs attribs) {
        super(name, attribs);
    }

    public boolean isPath() {
        return this.name.indexOf(47) >= 0 || this.name.endsWith(".xml");
    }

    private Set<String> getStringSet(String attrib) {
        List<String> list = this.getListAttrib(attrib);
        return list != null ? new HashSet<String>(list) : new HashSet();
    }

    public void setPropertiesMap(Map<String, String> properties) {
        ArrayList<String> strings = new ArrayList<String>(properties.size());
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String line = entry.getKey() + "=" + entry.getValue();
            strings.add(line);
        }
        this.setListAttrib(COMPONENT_PROPERTIES, strings);
    }

    public Map<String, String> getPropertiesMap() {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        List<String> list = this.getListAttrib(COMPONENT_PROPERTIES);
        if (list != null) {
            for (String entryStr : list) {
                String value;
                String name;
                int index = entryStr.lastIndexOf(61);
                if (index == -1) {
                    name = entryStr;
                    value = null;
                } else {
                    name = entryStr.substring(0, index);
                    value = entryStr.substring(index + 1);
                }
                result.put(name, value);
            }
        }
        return result;
    }

    public void setSvcRefs(List<? extends ComponentSvcReference> refs) {
        Iterator<String> iter = this.attribs.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            if (name.endsWith(":")) continue;
            iter.remove();
        }
        HashSet<String> dynamic = new HashSet<String>();
        HashSet<String> optional = new HashSet<String>();
        HashSet<String> multiple = new HashSet<String>();
        for (ComponentSvcReference componentSvcReference : refs) {
            String cardinalitySuffix;
            String expandedRefName = componentSvcReference.getName();
            if (componentSvcReference.getBind() != null) {
                expandedRefName = expandedRefName + "/" + componentSvcReference.getBind();
                if (componentSvcReference.getUnbind() != null) {
                    expandedRefName = expandedRefName + "/" + componentSvcReference.getUnbind();
                }
            }
            StringBuilder buffer = new StringBuilder();
            buffer.append(componentSvcReference.getServiceClass());
            if (componentSvcReference.getTargetFilter() != null) {
                buffer.append('(').append(componentSvcReference.getTargetFilter()).append(')');
            }
            if (componentSvcReference.isDynamic()) {
                if (componentSvcReference.isOptional()) {
                    cardinalitySuffix = componentSvcReference.isMultiple() ? "*" : "?";
                } else if (componentSvcReference.isMultiple()) {
                    cardinalitySuffix = "+";
                } else {
                    cardinalitySuffix = null;
                    dynamic.add(componentSvcReference.getName());
                }
            } else if (componentSvcReference.isOptional()) {
                if (componentSvcReference.isMultiple()) {
                    cardinalitySuffix = null;
                    optional.add(componentSvcReference.getName());
                    multiple.add(componentSvcReference.getName());
                } else {
                    cardinalitySuffix = "~";
                }
            } else if (componentSvcReference.isMultiple()) {
                multiple.add(componentSvcReference.getName());
                cardinalitySuffix = null;
            } else {
                cardinalitySuffix = null;
            }
            if (cardinalitySuffix != null) {
                buffer.append(cardinalitySuffix);
            }
            this.attribs.put(expandedRefName, buffer.toString());
        }
        this.setListAttrib(COMPONENT_OPTIONAL, optional);
        this.setListAttrib(COMPONENT_MULTIPLE, multiple);
        this.setListAttrib(COMPONENT_DYNAMIC, dynamic);
    }

    public List<ComponentSvcReference> getSvcRefs() {
        ArrayList<ComponentSvcReference> result = new ArrayList<ComponentSvcReference>();
        Set<String> dynamicSet = this.getStringSet(COMPONENT_DYNAMIC);
        Set<String> optionalSet = this.getStringSet(COMPONENT_OPTIONAL);
        Set<String> multipleSet = this.getStringSet(COMPONENT_MULTIPLE);
        for (Map.Entry<String, String> entry : this.attribs.entrySet()) {
            String referenceName = entry.getKey();
            if (referenceName.endsWith(":")) continue;
            ComponentSvcReference svcRef = new ComponentSvcReference();
            String bind = null;
            String unbind = null;
            if (referenceName.indexOf(47) >= 0) {
                String[] parts = referenceName.split("/");
                referenceName = parts[0];
                bind = parts[1];
                if (parts.length > 2) {
                    unbind = parts[2];
                }
            }
            svcRef.setName(referenceName);
            svcRef.setBind(bind);
            svcRef.setUnbind(unbind);
            String interfaceName = entry.getValue();
            if (interfaceName == null || interfaceName.length() == 0) continue;
            svcRef.setServiceClass(interfaceName);
            char c = interfaceName.charAt(interfaceName.length() - 1);
            if ("?+*~".indexOf(c) >= 0) {
                if (c == '?' || c == '*' || c == '~') {
                    optionalSet.add(referenceName);
                }
                if (c == '+' || c == '*') {
                    multipleSet.add(referenceName);
                }
                if (c == '+' || c == '*' || c == '?') {
                    dynamicSet.add(referenceName);
                }
                interfaceName = interfaceName.substring(0, interfaceName.length() - 1);
            }
            svcRef.setOptional(optionalSet.contains(referenceName));
            svcRef.setMultiple(multipleSet.contains(referenceName));
            svcRef.setDynamic(dynamicSet.contains(referenceName));
            String target = null;
            Matcher m = REFERENCE_PATTERN.matcher(interfaceName);
            if (m.matches()) {
                interfaceName = m.group(1);
                target = m.group(2);
            }
            svcRef.setTargetFilter(target);
            result.add(svcRef);
        }
        return result;
    }

    @Override
    public ServiceComponent clone() {
        return new ServiceComponent(this.name, new Attrs(this.attribs));
    }

    @Override
    protected boolean newlinesBetweenAttributes() {
        return true;
    }

    public static ServiceComponent error(String msg) {
        return new ServiceComponent(msg, null);
    }
}

