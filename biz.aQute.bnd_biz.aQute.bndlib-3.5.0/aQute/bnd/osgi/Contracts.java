/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Processor;
import aQute.bnd.version.Version;
import aQute.lib.collections.MultiMap;
import aQute.service.reporter.Report;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Contracts {
    private static final Logger logger = LoggerFactory.getLogger(Contracts.class);
    private Analyzer analyzer;
    private final MultiMap<Descriptors.PackageRef, Contract> contracted = new MultiMap<Descriptors.PackageRef, Contract>(Descriptors.PackageRef.class, Contract.class, true);
    private MultiMap<Collection<Contract>, Descriptors.PackageRef> overlappingContracts = new MultiMap();
    private Instructions instructions;
    private final Set<Contract> contracts = new HashSet<Contract>();

    public Contracts(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    Instructions getFilter() {
        if (this.instructions == null) {
            String contract = this.analyzer.getProperty("-contract");
            this.instructions = new Instructions(contract);
        }
        return this.instructions;
    }

    public void clear() {
        this.contracted.clear();
        this.overlappingContracts.clear();
        this.contracts.clear();
    }

    void collectContracts(String from, Parameters pcs) {
        block2: for (Map.Entry<String, Attrs> p : pcs.entrySet()) {
            String namespace = p.getKey();
            if (!namespace.equals("osgi.contract")) continue;
            Attrs capabilityAttrs = p.getValue();
            String name = capabilityAttrs.get("osgi.contract");
            if (name == null) {
                this.analyzer.warning("No name (attr %s) defined in bundle %s from contract namespace: %s", "osgi.contract", from, capabilityAttrs);
                continue;
            }
            for (Map.Entry<Instruction, Attrs> i : this.getFilter().entrySet()) {
                Contract c;
                block10: {
                    Instruction instruction = i.getKey();
                    if (!instruction.matches(name)) continue;
                    if (instruction.isNegated()) {
                        logger.debug("{} rejected due to {}", (Object)namespace, (Object)this.instructions);
                        continue block2;
                    }
                    logger.debug("accepted {}", p);
                    c = new Contract();
                    c.name = name;
                    String list = capabilityAttrs.get("uses:");
                    if (list == null || list.length() == 0) {
                        this.analyzer.warning("Contract %s has no uses: directive in %s.", name, from);
                        continue block2;
                    }
                    c.uses = Processor.split(list);
                    try {
                        Version version = capabilityAttrs.getTyped(Attrs.VERSION, "version");
                        if (version == null) break block10;
                        c.version = version;
                    }
                    catch (IllegalArgumentException iae) {
                        List<Version> versions = capabilityAttrs.getTyped(Attrs.LIST_VERSION, "version");
                        c.version = versions.get(0);
                        for (Version version : versions) {
                            if (version.compareTo(c.version) <= 0) continue;
                            c.version = version;
                        }
                    }
                }
                c.from = from;
                if (c.version == null) {
                    c.version = Version.LOWEST;
                    this.analyzer.warning("%s does not declare a version, assumed 0.0.0.", c);
                }
                c.decorators = new Attrs(i.getValue());
                for (String pname : c.uses) {
                    this.contracted.add(this.analyzer.getPackageRef(pname), c);
                }
            }
        }
    }

    boolean isContracted(Descriptors.PackageRef packageRef) {
        List list = (List)this.contracted.get(packageRef);
        if (list == null || list.isEmpty()) {
            return false;
        }
        if (list.size() > 1) {
            this.overlappingContracts.add(list, packageRef);
        }
        this.contracts.addAll(list);
        return true;
    }

    void addToRequirements(Parameters requirements) {
        for (Contract contract : this.contracts) {
            Attrs attrs = new Attrs(contract.decorators);
            attrs.put("osgi.contract", contract.name);
            String name = "osgi.contract";
            while (requirements.containsKey(name)) {
                name = name + "~";
            }
            Formatter f = new Formatter();
            Throwable throwable = null;
            try {
                f.format("(&(%s=%s)(version=%s))", "osgi.contract", contract.name, contract.version);
                attrs.put("filter:", f.toString());
                requirements.put(name, attrs);
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (f == null) continue;
                if (throwable != null) {
                    try {
                        f.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                    continue;
                }
                f.close();
            }
        }
        for (Map.Entry entry : this.overlappingContracts.entrySet()) {
            Report.Location location = this.analyzer.error("Contracts %s declare the same packages in their uses: directive: %s. Contracts are found in declaring bundles (see their 'from' field), it is possible to control the findingwith the -contract instruction", entry.getKey(), entry.getValue()).location();
            location.header = "-contract";
        }
    }

    public class Contract {
        public String name;
        public Attrs decorators;
        public Collection<String> uses;
        public Version version;
        public String from;

        public String toString() {
            return "Contract [name=" + this.name + ";version=" + this.version + ";from=" + this.from + "]";
        }
    }
}

