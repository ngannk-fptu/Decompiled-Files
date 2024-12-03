/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.ResourceBuilder;
import aQute.bnd.util.dto.DTO;
import aQute.lib.collections.MultiMap;
import aQute.lib.converter.Converter;
import aQute.lib.hex.Hex;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class PersistentResource
extends DTO
implements Resource {
    public Namespace[] namespaces;
    transient Resource resource;
    public byte[] sha;

    public PersistentResource() {
    }

    public PersistentResource(Resource resource) {
        MultiMap<String, Capability> capMap = new MultiMap<String, Capability>();
        for (Capability cap : resource.getCapabilities(null)) {
            capMap.add(cap.getNamespace(), cap);
        }
        MultiMap<String, Requirement> reqMap = new MultiMap<String, Requirement>();
        for (Requirement req : resource.getRequirements(null)) {
            reqMap.add(req.getNamespace(), req);
        }
        HashSet names = new HashSet(capMap.keySet());
        names.addAll(reqMap.keySet());
        this.namespaces = new Namespace[names.size()];
        int i = 0;
        for (String name : names) {
            List capabilities;
            Namespace ns = new Namespace();
            ns.name = name;
            List requirements = (List)reqMap.get(name);
            if (requirements != null && requirements.size() > 0) {
                ns.requirements = new RCData[requirements.size()];
                int rqi = 0;
                for (Requirement r : requirements) {
                    ns.requirements[rqi++] = PersistentResource.getData(true, r.getAttributes(), r.getDirectives());
                }
            }
            if ((capabilities = (List)capMap.get(name)) != null && capabilities.size() > 0) {
                ns.capabilities = new RCData[capabilities.size()];
                int rci = 0;
                for (Capability c : capabilities) {
                    ns.capabilities[rci++] = PersistentResource.getData(false, c.getAttributes(), c.getDirectives());
                }
            }
            this.namespaces[i++] = ns;
        }
        Arrays.sort(this.namespaces);
    }

    public Resource getResource() throws Exception {
        if (this.resource == null) {
            ResourceBuilder rb = new ResourceBuilder();
            for (Namespace ns : this.namespaces) {
                if (ns.capabilities != null) {
                    for (RCData rcdata : ns.capabilities) {
                        CapReqBuilder capb = new CapReqBuilder(ns.name);
                        for (Attr attrs : rcdata.properties) {
                            if (attrs.directive) {
                                capb.addDirective(attrs.key, (String)attrs.value);
                                continue;
                            }
                            capb.addAttribute(attrs.key, attrs.getValue());
                        }
                        rb.addCapability(capb);
                    }
                }
                if (ns.requirements == null) continue;
                for (RCData rcdata : ns.requirements) {
                    CapReqBuilder reqb = new CapReqBuilder(ns.name);
                    for (Attr attrs : rcdata.properties) {
                        if (attrs.directive) {
                            reqb.addDirective(attrs.key, (String)attrs.value);
                            continue;
                        }
                        reqb.addAttribute(attrs.key, attrs.getValue());
                    }
                    rb.addRequirement(reqb);
                }
            }
            this.resource = rb.build();
        }
        return this.resource;
    }

    private static int getType(Object value) {
        if (value == null || value instanceof String) {
            return DataType.STRING.ordinal();
        }
        if (value instanceof Version) {
            return DataType.VERSION.ordinal();
        }
        if (value instanceof Long) {
            return DataType.LONG.ordinal();
        }
        if (value instanceof Double) {
            return DataType.DOUBLE.ordinal();
        }
        return DataType.STRING.ordinal();
    }

    private static Attr getAttr(String key, Object value, boolean directive) {
        Attr attr = new Attr();
        attr.key = key;
        if (directive) {
            attr.type = DataType.STRING.ordinal();
            attr.value = value.toString();
            attr.directive = true;
            return attr;
        }
        attr.value = value;
        if (value instanceof Collection) {
            if (((Collection)value).size() > 0) {
                Object member = ((Collection)value).iterator().next();
                attr.type = PersistentResource.getType(member);
            } else {
                attr.type = DataType.STRING.ordinal();
            }
            return attr;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (length > 0) {
                Object member = Array.get(value, 0);
                attr.type = PersistentResource.getType(member);
            } else {
                attr.type = DataType.STRING.ordinal();
            }
        }
        attr.type = PersistentResource.getType(value);
        return attr;
    }

    private static RCData getData(boolean require, Map<String, Object> attributes, Map<String, String> directives) {
        RCData data = new RCData();
        data.require = require;
        ArrayList<Attr> props = new ArrayList<Attr>(attributes.size() + directives.size());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            props.add(PersistentResource.getAttr(entry.getKey(), entry.getValue(), false));
        }
        for (Map.Entry<String, Object> entry : directives.entrySet()) {
            props.add(PersistentResource.getAttr(entry.getKey(), entry.getValue(), true));
            ++data.directives;
        }
        Collections.sort(props);
        data.properties = props.toArray(new Attr[0]);
        return data;
    }

    @Override
    public String toString() {
        try {
            return "P-" + this.getResource();
        }
        catch (Exception e) {
            return "P-" + Hex.toHexString(this.sha);
        }
    }

    @Override
    @Deprecated
    public List<Capability> getCapabilities(String ns) {
        return null;
    }

    @Override
    @Deprecated
    public List<Requirement> getRequirements(String ns) {
        return null;
    }

    @Deprecated
    public static RCData getData(Map<String, Object> attributes, Map<String, String> directives) {
        return null;
    }

    @Deprecated
    public PersistentResource(byte[] digest, List<Capability> caps, List<Requirement> reqs) {
    }

    @Deprecated
    public class RC
    implements Requirement,
    Capability {
        public RC(RCData data, String ns) {
        }

        @Override
        public String getNamespace() {
            return null;
        }

        @Override
        public Resource getResource() {
            return null;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return null;
        }

        @Override
        public Map<String, String> getDirectives() {
            return null;
        }
    }

    public static class Attr
    extends DTO
    implements Comparable<Attr> {
        public String key;
        public int type;
        public Object value;
        public boolean directive = false;
        transient Object converted;

        @Override
        public int compareTo(Attr o) {
            return this.key.compareTo(o.key);
        }

        public Object getValue() {
            if (this.converted == null && this.value != null) {
                DataType t = DataType.values()[this.type];
                if (this.value instanceof Collection) {
                    Object[] cnv = ((Collection)this.value).toArray();
                    for (int i = 0; i < cnv.length; ++i) {
                        cnv[i] = this.convert(t, cnv[i]);
                    }
                    this.converted = cnv;
                } else {
                    this.converted = this.convert(t, this.value);
                }
            }
            return this.converted;
        }

        private Object convert(DataType t, Object value) {
            try {
                switch (t) {
                    case DOUBLE: {
                        return Converter.cnv(Double.class, value);
                    }
                    case LONG: {
                        return Converter.cnv(Long.class, value);
                    }
                    case STRING: {
                        return Converter.cnv(String.class, value);
                    }
                    case VERSION: {
                        if (value instanceof String) {
                            return Version.parseVersion((String)((String)value));
                        }
                        return Converter.cnv(Version.class, value);
                    }
                }
                return null;
            }
            catch (Exception e) {
                return null;
            }
        }
    }

    public static enum DataType {
        STRING,
        LONG,
        DOUBLE,
        VERSION;

    }

    public static class RCData
    extends DTO {
        public boolean require;
        public Attr[] properties;
        public int directives;
    }

    public static class Namespace
    extends DTO
    implements Comparable<Namespace> {
        public String name;
        public RCData[] capabilities;
        public RCData[] requirements;

        @Override
        public int compareTo(Namespace o) {
            return this.name.compareTo(o.name);
        }
    }
}

