/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.version.MavenVersion;
import aQute.bnd.version.Version;
import aQute.lib.strings.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.resource.Capability;

public class OSInformation {
    String osnames = null;
    Version osversion = null;
    private static String regexQualifierNotAllowedChars = "[^\\p{Alnum}-_]";
    private static Pattern digitPattern = Pattern.compile("(\\d+).*");
    static final String[][] processorFamilies = new String[][]{{"x86-64", "amd64", "em64t", "x86_64"}, {"x86", "pentium", "i386", "i486", "i586", "i686"}, {"68k"}, {"ARM"}, {"ARM_be"}, {"ARM_le"}, {"Alpha"}, {"ia64n"}, {"ia64w"}, {"Ignite", "psc1k"}, {"Mips"}, {"PARisc"}, {"PowerPC", "power", "ppc"}, {"Sh4"}, {"Sparc"}, {"Sparcv9"}, {"S390"}, {"V850e"}};
    static String[] osarch = OSInformation.getProcessorAliases(System.getProperty("os.arch"));

    public static String[] getProcessorAliases(String osArch) {
        String[][] arr$ = processorFamilies;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; ++i$) {
            String[] pnames;
            for (String pname : pnames = arr$[i$]) {
                if (!pname.equalsIgnoreCase(osArch)) continue;
                return pnames;
            }
        }
        return null;
    }

    public static String[] getProcessorAliases() {
        return osarch;
    }

    static Version convertUnixKernelVersion(String sysPropOsVersion) {
        Version osversion = new Version(0, 0, 0);
        String s = sysPropOsVersion.trim();
        int index = 0;
        do {
            int matchedDigitNumber;
            Matcher matcher;
            if (!(matcher = digitPattern.matcher(s)).matches()) continue;
            String matchedDigit = matcher.group(1);
            try {
                matchedDigitNumber = Integer.parseInt(matchedDigit);
            }
            catch (NumberFormatException e) {
                assert (false);
                break;
            }
            switch (index) {
                case 0: {
                    osversion = new Version(matchedDigitNumber, osversion.getMinor(), osversion.getMicro());
                    break;
                }
                case 1: {
                    osversion = new Version(osversion.getMajor(), matchedDigitNumber, osversion.getMicro());
                    break;
                }
                case 2: {
                    osversion = new Version(osversion.getMajor(), osversion.getMinor(), matchedDigitNumber);
                    break;
                }
                default: {
                    assert (false);
                    break;
                }
            }
            s = s.substring(matchedDigit.length());
            if (s.length() == 0 || s.charAt(0) != '.') break;
            s = s.substring(1);
            ++index;
        } while (index < 3);
        if (s.length() != 0) {
            String qualifier = s.replaceAll(regexQualifierNotAllowedChars, "_");
            osversion = new Version(osversion.getMajor(), osversion.getMinor(), osversion.getMicro(), qualifier);
        }
        return osversion;
    }

    public OSInformation() throws IllegalArgumentException {
        this(System.getProperty("os.name"), System.getProperty("os.version"));
    }

    public OSInformation(String sysPropOsName, String sysPropOsVersion) throws IllegalArgumentException {
        if (sysPropOsName == null || sysPropOsName.length() == 0 || sysPropOsVersion == null || sysPropOsVersion.length() == 0) {
            return;
        }
        OSNameVersion pair = OSInformation.getOperatingSystemAliases(sysPropOsName, sysPropOsVersion);
        if (pair == null) {
            throw new IllegalArgumentException("Unknown OS/version combination: " + sysPropOsName + " " + sysPropOsVersion);
        }
        this.osversion = pair.osversion;
        this.osnames = pair.osnames;
    }

    public static String getNativeCapabilityClause(Processor p, String[] args) throws Exception {
        NativeCapability clause = new NativeCapability();
        OSInformation.parseNativeCapabilityArgs(p, args, clause);
        OSInformation.validateNativeCapability(clause);
        Capability cap = OSInformation.createCapability(clause);
        return ResourceUtils.toProvideCapability(cap);
    }

    static Capability createCapability(NativeCapability clause) throws Exception {
        CapabilityBuilder c = new CapabilityBuilder("osgi.native");
        c.addAttribute("osgi.native.osname", clause.osname);
        c.addAttribute("osgi.native.osversion", clause.osversion);
        c.addAttribute("osgi.native.processor", clause.processor);
        c.addAttribute("osgi.native.language", clause.language);
        Capability cap = c.synthetic();
        return cap;
    }

    static void validateNativeCapability(NativeCapability clause) {
        if (clause.osversion == null) {
            throw new IllegalArgumentException("osversion/osgi.native.osversion not set in ${native_capability}");
        }
        if (clause.osname.isEmpty()) {
            throw new IllegalArgumentException("osname/osgi.native.osname not set in ${native_capability}");
        }
        if (clause.processor.isEmpty()) {
            throw new IllegalArgumentException("processor/osgi.native.processor not set in ${native_capability}");
        }
    }

    static void parseNativeCapabilityArgs(Processor p, String[] args, NativeCapability clause) throws Exception {
        if (args.length == 1) {
            OSInformation osi = new OSInformation();
            clause.osname.addAll(Strings.split(osi.osnames));
            clause.osversion = osi.osversion;
            clause.processor.addAll(Arrays.asList(OSInformation.getProcessorAliases(System.getProperty("os.arch"))));
            clause.language = Locale.getDefault().toString();
            StringBuilder sb = new StringBuilder();
            sb.append("osname=").append(System.getProperty("os.name"));
            sb.append(";").append("osversion=").append(MavenVersion.cleanupVersion(System.getProperty("os.version")));
            sb.append(";").append("processor=").append(System.getProperty("os.arch"));
            sb.append(";").append("lang=").append(clause.language);
            String advice = sb.toString();
        } else {
            String osname = null;
            block18: for (int i = 1; i < args.length; ++i) {
                String[] parts = args[i].split("\\s*=\\s*");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Illegal property syntax in \"" + args[i] + "\", use \"key=value\"");
                }
                String key = Strings.trim(parts[0]);
                String value = Strings.trim(parts[1]);
                boolean isList = value.indexOf(44) > 0;
                switch (key) {
                    case "processor": 
                    case "osgi.native.processor": {
                        String[] processorAliases;
                        if (isList) {
                            clause.processor.addAll(Strings.split(value));
                            continue block18;
                        }
                        if ("arm".equals(value)) {
                            p.warning("The 'arm' processor is deprecated. Specify either 'arm_le' or 'arm_be'", new Object[0]);
                        }
                        if ((processorAliases = OSInformation.getProcessorAliases(value)) != null && processorAliases.length > 0) {
                            clause.processor.addAll(Arrays.asList(processorAliases));
                            continue block18;
                        }
                        clause.processor.add(value);
                        continue block18;
                    }
                    case "osname": 
                    case "osgi.native.osname": {
                        if (isList) {
                            clause.osname.addAll(Strings.split(value));
                            continue block18;
                        }
                        if (osname == null) {
                            osname = value;
                            continue block18;
                        }
                        clause.osname.add(osname);
                        osname = value;
                        continue block18;
                    }
                    case "osversion": 
                    case "osgi.native.osversion": {
                        if (clause.osversion == null) {
                            clause.osversion = Version.parseVersion(value);
                            continue block18;
                        }
                        throw new IllegalArgumentException("osversion/osgi.native.osversion can only be set once in ${native_capability}");
                    }
                    case "osgi.native.language": 
                    case "lang": {
                        if (clause.language != null) {
                            throw new IllegalArgumentException("lang/osgi.native.lang can only be set once in ${native_capability}");
                        }
                        clause.language = value;
                    }
                }
            }
            if (osname != null) {
                try {
                    OSInformation osi = new OSInformation(osname, clause.osversion.toString());
                    clause.osname.addAll(Strings.split(osi.osnames));
                }
                catch (Exception e) {
                    clause.osname.add(osname);
                }
            }
        }
    }

    public static OSNameVersion getOperatingSystemAliases(String sysPropOsName, String sysPropOsVersion) {
        OSNameVersion nc = new OSNameVersion();
        if (sysPropOsName.startsWith("Windows")) {
            if (sysPropOsVersion.startsWith("10.0")) {
                nc.osversion = new Version(10, 0, 0);
                nc.osnames = "Windows10,Windows 10,Win32";
            } else if (sysPropOsVersion.startsWith("6.3")) {
                nc.osversion = new Version(6, 3, 0);
                nc.osnames = "Windows8.1,Windows 8.1,Win32";
            } else if (sysPropOsVersion.startsWith("6.2")) {
                nc.osversion = new Version(6, 2, 0);
                nc.osnames = "Windows8,Windows 8,Win32";
            } else if (sysPropOsVersion.startsWith("6.1")) {
                nc.osversion = new Version(6, 1, 0);
                nc.osnames = "Windows7,Windows 7,Win32";
            } else if (sysPropOsVersion.startsWith("6.0")) {
                nc.osversion = new Version(6, 0, 0);
                nc.osnames = "WindowsVista,WinVista,Windows Vista,Win32";
            } else if (sysPropOsVersion.startsWith("5.1")) {
                nc.osversion = new Version(5, 1, 0);
                nc.osnames = "WindowsXP,WinXP,Windows XP,Win32";
            } else {
                nc = null;
            }
        } else {
            if (sysPropOsName.startsWith("Mac OS X")) {
                nc.osversion = OSInformation.convertUnixKernelVersion(sysPropOsVersion);
                nc.osnames = "MacOSX,Mac OS X";
                return nc;
            }
            if (sysPropOsName.toLowerCase().startsWith("linux")) {
                nc.osversion = OSInformation.convertUnixKernelVersion(sysPropOsVersion);
                nc.osnames = "Linux";
            } else if (sysPropOsName.startsWith("Solaris")) {
                nc.osversion = OSInformation.convertUnixKernelVersion(sysPropOsVersion);
                nc.osnames = "Solaris";
            } else if (sysPropOsName.startsWith("AIX")) {
                nc.osversion = OSInformation.convertUnixKernelVersion(sysPropOsVersion);
                nc.osnames = "AIX";
            } else if (sysPropOsName.startsWith("HP-UX")) {
                nc.osversion = OSInformation.convertUnixKernelVersion(sysPropOsVersion);
                nc.osnames = "HPUX,hp-ux";
            }
        }
        return nc;
    }

    public static class OSNameVersion {
        public Version osversion;
        public String osnames;
    }

    static class NativeCapability {
        public List<String> processor = new ArrayList<String>();
        public List<String> osname = new ArrayList<String>();
        public Version osversion;
        public String language = "en";

        NativeCapability() {
        }
    }
}

