/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.Felix;
import org.osgi.dto.DTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.framework.wiring.dto.BundleWireDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;
import org.osgi.framework.wiring.dto.FrameworkWiringDTO;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.resource.dto.CapabilityDTO;
import org.osgi.resource.dto.CapabilityRefDTO;
import org.osgi.resource.dto.RequirementDTO;
import org.osgi.resource.dto.RequirementRefDTO;

public class DTOFactory {
    private DTOFactory() {
    }

    static <T> T createDTO(Bundle bundle, Class<T> type) {
        if (1 == bundle.getState()) {
            return null;
        }
        if (type == BundleDTO.class) {
            return type.cast(DTOFactory.createBundleDTO(bundle));
        }
        if (type == BundleStartLevelDTO.class) {
            return type.cast(DTOFactory.createBundleStartLevelDTO(bundle));
        }
        if (type == BundleRevisionDTO.class) {
            return type.cast(DTOFactory.createBundleRevisionDTO(bundle));
        }
        if (type == BundleRevisionDTO[].class) {
            return type.cast(DTOFactory.createBundleRevisionDTOArray(bundle));
        }
        if (type == BundleWiringDTO.class) {
            return type.cast(DTOFactory.createBundleWiringDTO(bundle));
        }
        if (type == BundleWiringDTO[].class) {
            return type.cast(DTOFactory.createBundleWiringDTOArray(bundle));
        }
        if (type == ServiceReferenceDTO[].class) {
            return type.cast(DTOFactory.createServiceReferenceDTOArray(bundle));
        }
        if (type == FrameworkDTO.class && bundle instanceof Felix) {
            return type.cast(DTOFactory.createFrameworkDTO((Felix)bundle));
        }
        if (type == FrameworkStartLevelDTO.class && bundle instanceof Framework) {
            return type.cast(DTOFactory.createFrameworkStartLevelDTO((Framework)bundle));
        }
        if (type == FrameworkWiringDTO.class && bundle instanceof Felix) {
            return type.cast(DTOFactory.createFrameworkWiringDTO((Felix)bundle));
        }
        return null;
    }

    static ServiceReferenceDTO createDTO(ServiceReference ref) {
        return DTOFactory.createServiceReferenceDTO(ref);
    }

    private static BundleDTO createBundleDTO(Bundle bundle) {
        BundleDTO dto = new BundleDTO();
        dto.id = bundle.getBundleId();
        dto.lastModified = bundle.getLastModified();
        dto.state = bundle.getState();
        dto.symbolicName = bundle.getSymbolicName();
        dto.version = "" + bundle.getVersion();
        return dto;
    }

    private static BundleRevisionDTO createBundleRevisionDTO(Bundle bundle) {
        BundleRevision br = bundle.adapt(BundleRevision.class);
        if (!(br instanceof BundleRevisionImpl)) {
            return null;
        }
        return DTOFactory.createBundleRevisionDTO(bundle, (BundleRevisionImpl)br, new HashSet<BundleRevisionDTO>());
    }

    private static BundleRevisionDTO[] createBundleRevisionDTOArray(Bundle bundle) {
        BundleRevisions brs = bundle.adapt(BundleRevisions.class);
        if (brs == null || brs.getRevisions() == null) {
            return null;
        }
        List<BundleRevision> revisions = brs.getRevisions();
        BundleRevisionDTO[] dtos = new BundleRevisionDTO[revisions.size()];
        for (int i = 0; i < revisions.size(); ++i) {
            if (!(revisions.get(i) instanceof BundleRevisionImpl)) continue;
            dtos[i] = DTOFactory.createBundleRevisionDTO(bundle, (BundleRevisionImpl)revisions.get(i), new HashSet<BundleRevisionDTO>());
        }
        return dtos;
    }

    private static BundleRevisionDTO createBundleRevisionDTO(BundleRevision revision, Set<BundleRevisionDTO> resources) {
        if (revision instanceof BundleRevisionImpl) {
            return DTOFactory.createBundleRevisionDTO(revision.getBundle(), (BundleRevisionImpl)revision, resources);
        }
        return null;
    }

    private static BundleRevisionDTO createBundleRevisionDTO(Bundle bundle, BundleRevisionImpl revision, Set<BundleRevisionDTO> resources) {
        BundleRevisionDTO dto = new BundleRevisionDTO();
        dto.id = DTOFactory.getRevisionID(revision);
        DTOFactory.addBundleRevisionDTO(dto, resources);
        dto.bundle = bundle.getBundleId();
        dto.symbolicName = revision.getSymbolicName();
        dto.type = revision.getTypes();
        dto.version = revision.getVersion().toString();
        dto.capabilities = new ArrayList();
        for (Capability cap : revision.getCapabilities(null)) {
            CapabilityDTO cdto = new CapabilityDTO();
            cdto.id = DTOFactory.getCapabilityID(cap);
            cdto.namespace = cap.getNamespace();
            cdto.attributes = DTOFactory.convertAttrsToDTO(cap.getAttributes());
            cdto.directives = new HashMap<String, String>(cap.getDirectives());
            cdto.resource = DTOFactory.getResourceIDAndAdd(cap.getResource(), resources);
            dto.capabilities.add(cdto);
        }
        dto.requirements = new ArrayList();
        for (Requirement req : revision.getRequirements(null)) {
            RequirementDTO rdto = new RequirementDTO();
            rdto.id = DTOFactory.getRequirementID(req);
            rdto.namespace = req.getNamespace();
            rdto.attributes = DTOFactory.convertAttrsToDTO(req.getAttributes());
            rdto.directives = new HashMap<String, String>(req.getDirectives());
            rdto.resource = DTOFactory.getResourceIDAndAdd(req.getResource(), resources);
            dto.requirements.add(rdto);
        }
        return dto;
    }

    private static BundleWiringDTO createBundleWiringDTO(Bundle bundle) {
        BundleWiring bw = bundle.adapt(BundleWiring.class);
        return bw != null ? DTOFactory.createBundleWiringDTO(bw) : null;
    }

    private static BundleWiringDTO createBundleWiringDTO(BundleWiring wiring) {
        BundleWiringDTO dto = new BundleWiringDTO();
        dto.bundle = wiring.getBundle().getBundleId();
        dto.root = DTOFactory.getWiringID(wiring);
        dto.nodes = new HashSet<BundleWiringDTO.NodeDTO>();
        dto.resources = new HashSet<BundleRevisionDTO>();
        DTOFactory.createBundleRevisionDTO(wiring.getRevision(), dto.resources);
        DTOFactory.createBundleWiringNodeDTO(wiring, dto.resources, dto.nodes);
        return dto;
    }

    private static BundleWiringDTO[] createBundleWiringDTOArray(Bundle bundle) {
        BundleRevisions brs = bundle.adapt(BundleRevisions.class);
        if (brs == null || brs.getRevisions() == null) {
            return null;
        }
        List<BundleRevision> revisions = brs.getRevisions();
        BundleWiringDTO[] dtos = new BundleWiringDTO[revisions.size()];
        for (int i = 0; i < revisions.size(); ++i) {
            BundleWiring wiring = revisions.get(i).getWiring();
            dtos[i] = DTOFactory.createBundleWiringDTO(wiring);
        }
        return dtos;
    }

    private static void createBundleWiringNodeDTO(BundleWiring bw, Set<BundleRevisionDTO> resources, Set<BundleWiringDTO.NodeDTO> nodes) {
        BundleWiringDTO.NodeDTO node = new BundleWiringDTO.NodeDTO();
        node.id = DTOFactory.getWiringID(bw);
        DTOFactory.addNodeDTO(node, nodes);
        node.current = bw.isCurrent();
        node.inUse = bw.isInUse();
        node.resource = DTOFactory.getResourceIDAndAdd(bw.getResource(), resources);
        node.capabilities = new ArrayList();
        for (Capability capability : bw.getCapabilities(null)) {
            CapabilityRefDTO cdto = new CapabilityRefDTO();
            cdto.capability = DTOFactory.getCapabilityID(capability);
            cdto.resource = DTOFactory.getResourceIDAndAdd(capability.getResource(), resources);
            node.capabilities.add(cdto);
        }
        node.requirements = new ArrayList();
        for (Requirement requirement : bw.getRequirements(null)) {
            RequirementRefDTO rdto = new RequirementRefDTO();
            rdto.requirement = DTOFactory.getRequirementID(requirement);
            rdto.resource = DTOFactory.getResourceIDAndAdd(requirement.getResource(), resources);
            node.requirements.add(rdto);
        }
        node.providedWires = new ArrayList();
        for (Wire wire : bw.getProvidedWires(null)) {
            node.providedWires.add(DTOFactory.createBundleWireDTO(wire, resources, nodes));
        }
        node.requiredWires = new ArrayList();
        for (Wire wire : bw.getRequiredWires(null)) {
            node.requiredWires.add(DTOFactory.createBundleWireDTO(wire, resources, nodes));
        }
    }

    private static BundleWireDTO createBundleWireDTO(Wire wire, Set<BundleRevisionDTO> resources, Set<BundleWiringDTO.NodeDTO> nodes) {
        BundleWireDTO wdto = new BundleWireDTO();
        if (wire instanceof BundleWire) {
            BundleWire w = (BundleWire)wire;
            BundleWiring pw = w.getProviderWiring();
            DTOFactory.addWiringNodeIfNotPresent(pw, resources, nodes);
            wdto.providerWiring = DTOFactory.getWiringID(pw);
            BundleWiring rw = w.getRequirerWiring();
            DTOFactory.addWiringNodeIfNotPresent(rw, resources, nodes);
            wdto.requirerWiring = DTOFactory.getWiringID(rw);
        }
        wdto.provider = DTOFactory.getResourceIDAndAdd(wire.getProvider(), resources);
        wdto.requirer = DTOFactory.getResourceIDAndAdd(wire.getRequirer(), resources);
        wdto.capability = new CapabilityRefDTO();
        wdto.capability.capability = DTOFactory.getCapabilityID(wire.getCapability());
        wdto.capability.resource = DTOFactory.getResourceIDAndAdd(wire.getCapability().getResource(), resources);
        wdto.requirement = new RequirementRefDTO();
        wdto.requirement.requirement = DTOFactory.getRequirementID(wire.getRequirement());
        wdto.requirement.resource = DTOFactory.getResourceIDAndAdd(wire.getRequirement().getResource(), resources);
        return wdto;
    }

    private static BundleStartLevelDTO createBundleStartLevelDTO(Bundle bundle) {
        BundleStartLevelDTO dto = new BundleStartLevelDTO();
        dto.bundle = bundle.getBundleId();
        BundleStartLevel sl = bundle.adapt(BundleStartLevel.class);
        dto.activationPolicyUsed = sl.isActivationPolicyUsed();
        dto.persistentlyStarted = sl.isPersistentlyStarted();
        dto.startLevel = sl.getStartLevel();
        return dto;
    }

    private static ServiceReferenceDTO[] createServiceReferenceDTOArray(Bundle bundle) {
        BundleContext ctx = ((BundleImpl)bundle)._getBundleContext();
        if (ctx == null) {
            return null;
        }
        ServiceReference<?>[] svcs = bundle.getRegisteredServices();
        if (svcs == null) {
            return new ServiceReferenceDTO[0];
        }
        ServiceReferenceDTO[] dtos = new ServiceReferenceDTO[svcs.length];
        for (int i = 0; i < svcs.length; ++i) {
            dtos[i] = DTOFactory.createServiceReferenceDTO(svcs[i]);
        }
        return dtos;
    }

    private static ServiceReferenceDTO createServiceReferenceDTO(ServiceReference<?> svc) {
        ServiceReferenceDTO dto = new ServiceReferenceDTO();
        dto.bundle = (Long)svc.getProperty("service.bundleid");
        dto.id = (Long)svc.getProperty("service.id");
        HashMap<String, Object> props = new HashMap<String, Object>();
        for (String key : svc.getPropertyKeys()) {
            props.put(key, svc.getProperty(key));
        }
        dto.properties = new HashMap<String, Object>(props);
        Bundle[] ubs = svc.getUsingBundles();
        if (ubs == null) {
            dto.usingBundles = new long[0];
        } else {
            dto.usingBundles = new long[ubs.length];
            for (int j = 0; j < ubs.length; ++j) {
                dto.usingBundles[j] = ubs[j].getBundleId();
            }
        }
        return dto;
    }

    private static FrameworkDTO createFrameworkDTO(Felix framework) {
        FrameworkDTO dto = new FrameworkDTO();
        dto.properties = DTOFactory.convertAttrsToDTO(framework.getConfig());
        dto.bundles = new ArrayList<BundleDTO>();
        for (Bundle b : framework._getBundleContext().getBundles()) {
            dto.bundles.add(DTOFactory.createDTO(b, BundleDTO.class));
        }
        dto.services = new ArrayList<ServiceReferenceDTO>();
        ServiceReference<?>[] refs = null;
        try {
            refs = framework._getBundleContext().getAllServiceReferences(null, null);
        }
        catch (InvalidSyntaxException invalidSyntaxException) {
            // empty catch block
        }
        for (ServiceReference<?> sr : refs) {
            dto.services.add(DTOFactory.createServiceReferenceDTO(sr));
        }
        return dto;
    }

    private static FrameworkStartLevelDTO createFrameworkStartLevelDTO(Framework framework) {
        FrameworkStartLevel fsl = framework.adapt(FrameworkStartLevel.class);
        FrameworkStartLevelDTO dto = new FrameworkStartLevelDTO();
        dto.initialBundleStartLevel = fsl.getInitialBundleStartLevel();
        dto.startLevel = fsl.getStartLevel();
        return dto;
    }

    private static FrameworkWiringDTO createFrameworkWiringDTO(Felix framework) {
        FrameworkWiringDTO dto = new FrameworkWiringDTO();
        dto.resources = new HashSet<BundleRevisionDTO>();
        dto.wirings = new HashSet<BundleWiringDTO.NodeDTO>();
        LinkedHashSet<Bundle> bundles = new LinkedHashSet<Bundle>(Arrays.asList(framework.getBundles()));
        bundles.addAll(framework.getRemovalPendingBundles());
        for (Bundle bundle : bundles) {
            DTOFactory.addBundleWiring(bundle, dto.resources, dto.wirings);
        }
        return dto;
    }

    private static void addBundleWiring(Bundle bundle, Set<BundleRevisionDTO> resources, Set<BundleWiringDTO.NodeDTO> wirings) {
        BundleRevisions brs = bundle.adapt(BundleRevisions.class);
        for (BundleRevision revision : brs.getRevisions()) {
            BundleWiring wiring = revision.getWiring();
            if (wiring == null) continue;
            DTOFactory.createBundleWiringNodeDTO(wiring, resources, wirings);
        }
    }

    private static void addBundleRevisionDTO(BundleRevisionDTO dto, Set<BundleRevisionDTO> resources) {
        for (BundleRevisionDTO r : resources) {
            if (r.id != dto.id) continue;
            return;
        }
        resources.add(dto);
    }

    private static void addNodeDTO(BundleWiringDTO.NodeDTO dto, Set<BundleWiringDTO.NodeDTO> nodes) {
        for (BundleWiringDTO.NodeDTO nodeDTO : nodes) {
            if (nodeDTO.id != dto.id) continue;
            return;
        }
        nodes.add(dto);
    }

    private static void addWiringNodeIfNotPresent(BundleWiring bw, Set<BundleRevisionDTO> resources, Set<BundleWiringDTO.NodeDTO> nodes) {
        int wiringID = DTOFactory.getWiringID(bw);
        for (BundleWiringDTO.NodeDTO n : nodes) {
            if (n.id != wiringID) continue;
            return;
        }
        DTOFactory.createBundleWiringNodeDTO(bw, resources, nodes);
    }

    private static Map<String, Object> convertAttrsToDTO(Map<String, Object> map) {
        HashMap<String, Object> m = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = DTOFactory.convertAttrToDTO(entry.getValue());
            if (value == null) continue;
            m.put(entry.getKey(), value);
        }
        return m;
    }

    private static Object convertAttrToDTO(Object value) {
        if (value instanceof Version) {
            return value.toString();
        }
        if (DTOFactory.isPermissibleAttribute(value.getClass()) || value.getClass().isArray() && DTOFactory.isPermissibleAttribute(value.getClass().getComponentType())) {
            return value;
        }
        if (value instanceof List) {
            ArrayList<Object> result = new ArrayList<Object>();
            for (Object v : (List)value) {
                Object vv = DTOFactory.convertAttrToDTO(v);
                if (vv == null) continue;
                result.add(vv);
            }
            return result.isEmpty() ? null : result;
        }
        return null;
    }

    private static boolean isPermissibleAttribute(Class clazz) {
        return clazz == Boolean.class || clazz == String.class || DTO.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz);
    }

    private static int getWiringID(Wiring bw) {
        Resource res = bw.getResource();
        if (res != null) {
            return DTOFactory.getResourceIDAndAdd(res, null);
        }
        return bw.hashCode();
    }

    private static int getCapabilityID(Capability capability) {
        return capability.hashCode();
    }

    private static int getRequirementID(Requirement requirement) {
        return requirement.hashCode();
    }

    private static int getResourceIDAndAdd(Resource res, Set<BundleRevisionDTO> resources) {
        if (res instanceof BundleRevisionImpl) {
            BundleRevisionImpl bres = (BundleRevisionImpl)res;
            int id = bres.getId().hashCode();
            if (resources == null) {
                return id;
            }
            for (BundleRevisionDTO rdto : resources) {
                if (rdto.id != id) continue;
                return id;
            }
            DTOFactory.createBundleRevisionDTO(bres, resources);
            return id;
        }
        return res.hashCode();
    }

    private static int getRevisionID(BundleRevisionImpl revision) {
        return revision.getId().hashCode();
    }
}

