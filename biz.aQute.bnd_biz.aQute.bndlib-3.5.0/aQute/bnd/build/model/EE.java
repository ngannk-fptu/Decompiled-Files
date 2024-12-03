/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model;

import aQute.bnd.header.Parameters;
import aQute.bnd.version.Version;
import aQute.lib.utf8properties.UTF8Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

public enum EE {
    OSGI_Minimum_1_0("OSGi/Minimum-1.0", "OSGi/Minimum", new Version("1.0"), new EE[0]),
    OSGI_Minimum_1_1("OSGi/Minimum-1.1", "OSGi/Minimum", new Version("1.1"), OSGI_Minimum_1_0),
    OSGI_Minimum_1_2("OSGi/Minimum-1.2", "OSGi/Minimum", new Version("1.2"), OSGI_Minimum_1_1),
    JRE_1_1("JRE-1.1", "JRE", new Version("1.1"), new EE[0]),
    J2SE_1_2("J2SE-1.2", "JavaSE", new Version("1.2"), JRE_1_1),
    J2SE_1_3("J2SE-1.3", "JavaSE", new Version("1.3"), J2SE_1_2, OSGI_Minimum_1_1),
    J2SE_1_4("J2SE-1.4", "JavaSE", new Version("1.4"), J2SE_1_3, OSGI_Minimum_1_2),
    J2SE_1_5("J2SE-1.5", "JavaSE", new Version("1.5"), J2SE_1_4),
    JavaSE_1_6("JavaSE-1.6", "JavaSE", new Version("1.6"), J2SE_1_5),
    JavaSE_1_7("JavaSE-1.7", "JavaSE", new Version("1.7"), JavaSE_1_6),
    JavaSE_compact1_1_8("JavaSE/compact1-1.8", "JavaSE/compact1", new Version("1.8"), OSGI_Minimum_1_2),
    JavaSE_compact2_1_8("JavaSE/compact2-1.8", "JavaSE/compact2", new Version("1.8"), JavaSE_compact1_1_8),
    JavaSE_compact3_1_8("JavaSE/compact3-1.8", "JavaSE/compact3", new Version("1.8"), JavaSE_compact2_1_8),
    JavaSE_1_8("JavaSE-1.8", "JavaSE", new Version("1.8"), JavaSE_1_7, JavaSE_compact3_1_8),
    JavaSE_9_0("JavaSE-9", "JavaSE", new Version("9"), JavaSE_1_8),
    UNKNOWN("Unknown", "unknown", new Version(0), new EE[0]);

    private final String eeName;
    private final String capabilityName;
    private final Version capabilityVersion;
    private final EE[] compatible;
    private transient EnumSet<EE> compatibleSet;
    private transient Parameters packages = null;

    private EE(String name, String capabilityName, Version capabilityVersion, EE ... compatible) {
        this.eeName = name;
        this.capabilityName = capabilityName;
        this.capabilityVersion = capabilityVersion;
        this.compatible = compatible;
    }

    public String getEEName() {
        return this.eeName;
    }

    public EE[] getCompatible() {
        EnumSet<EE> set = this.getCompatibleSet();
        return set.toArray(new EE[0]);
    }

    private EnumSet<EE> getCompatibleSet() {
        if (this.compatibleSet != null) {
            return this.compatibleSet;
        }
        EnumSet<EE> set = EnumSet.noneOf(this.getDeclaringClass());
        if (this.compatible != null) {
            for (EE ee : this.compatible) {
                set.add(ee);
                set.addAll(ee.getCompatibleSet());
            }
        }
        this.compatibleSet = set;
        return this.compatibleSet;
    }

    public String getCapabilityName() {
        return this.capabilityName;
    }

    public Version getCapabilityVersion() {
        return this.capabilityVersion;
    }

    public static EE parse(String str) {
        for (EE ee : EE.values()) {
            if (!ee.eeName.equals(str)) continue;
            return ee;
        }
        return null;
    }

    public Parameters getPackages() throws IOException {
        if (this.packages == null) {
            try (InputStream stream = EE.class.getResourceAsStream(this.name() + ".properties");){
                if (stream == null) {
                    Parameters parameters = new Parameters();
                    return parameters;
                }
                UTF8Properties props = new UTF8Properties();
                props.load(stream);
                String exports = props.getProperty("org.osgi.framework.system.packages");
                this.packages = new Parameters(exports);
            }
        }
        return this.packages;
    }
}

