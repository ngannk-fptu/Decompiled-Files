/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package aQute.bnd.obr;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.ResourceBuilder;
import aQute.bnd.version.VersionRange;
import aQute.libg.cryptography.SHA1;
import aQute.libg.map.MAP;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.util.Formatter;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;

public class OBRFragment {
    static final String MIME_TYPE_OSGI_BUNDLE = "application/vnd.osgi.bundle";
    static Pattern EE_PATTERN = Pattern.compile("[^.]-([\\d]+(?:\\.[\\d]+(?:\\.[\\d]+(?:\\.)?)?)?)");

    public static Reporter parse(Jar jar, ResourceBuilder resource) throws Exception {
        Manifest m = jar.getManifest();
        if (m == null) {
            return null;
        }
        Domain d = Domain.domain(m);
        d.setTranslation(jar);
        Map.Entry<String, Attrs> bundleSymbolicName = d.getBundleSymbolicName();
        if (bundleSymbolicName == null) {
            return null;
        }
        boolean singleton = "true".equals(bundleSymbolicName.getValue().get("singleton:"));
        boolean isFragment = d.get("Fragment-Host") != null;
        Version version = d.getBundleVersion() == null ? Version.emptyVersion : new Version(d.getBundleVersion());
        CapReqBuilder identity = new CapReqBuilder("osgi.identity");
        identity.addAttribute("osgi.identity", bundleSymbolicName.getKey());
        identity.addAttribute("copyright", d.translate("Bundle-Copyright"));
        identity.addAttribute("description", d.translate("Bundle-Description"));
        identity.addAttribute("documentation", d.translate("Bundle-DocURL"));
        identity.addAttribute("license", d.translate("Bundle-License"));
        if (singleton) {
            identity.addAttribute("singleton", "true");
        }
        identity.addAttribute("type", isFragment ? "osgi.fragment" : "osgi.bundle");
        identity.addAttribute("version", new Version(d.getBundleVersion()));
        resource.addCapability(identity);
        if (isFragment) {
            Map.Entry<String, Attrs> fragmentHost = d.getFragmentHost();
            CapReqBuilder fragment = new CapReqBuilder("osgi.wiring.host");
            String v = fragmentHost.getValue().get("version");
            if (v == null) {
                v = "0";
            }
            Version version2 = new Version(v);
            String filter = OBRFragment.filter("osgi.wiring.package", fragmentHost.getKey(), fragmentHost.getValue());
            fragment.addDirective("filter", filter);
            resource.addRequirement(fragment);
        } else {
            CapReqBuilder bundle = new CapReqBuilder("osgi.wiring.bundle");
            CapReqBuilder host = new CapReqBuilder("osgi.wiring.host");
            bundle.addAttribute("version", version);
            host.addAttribute("version", version);
            for (Map.Entry<String, String> entry : bundleSymbolicName.getValue().entrySet()) {
                String key = entry.getKey();
                if (key.endsWith(":")) {
                    String directive = key.substring(0, key.length() - 1);
                    if ("fragment-attachment".equalsIgnoreCase(directive)) {
                        if ("never".equalsIgnoreCase(entry.getValue())) {
                            host = null;
                        }
                    } else if (!"singleton".equalsIgnoreCase(directive)) {
                        bundle.addDirective(directive, entry.getValue());
                    }
                    if (host != null) {
                        host.addDirective(directive, entry.getValue());
                    }
                    bundle.addDirective(directive, entry.getValue());
                    continue;
                }
                if (host != null) {
                    host.addAttribute(key, entry.getValue());
                }
                bundle.addAttribute(key, entry.getValue());
            }
            if (host != null) {
                resource.addCapability(host);
            }
            resource.addCapability(bundle);
        }
        Parameters exports = d.getExportPackage();
        for (Map.Entry<String, Attrs> entry : exports.entrySet()) {
            CapReqBuilder capReqBuilder = new CapReqBuilder("osgi.wiring.package");
            String pkgName = Processor.removeDuplicateMarker(entry.getKey());
            capReqBuilder.addAttribute("osgi.wiring.package", pkgName);
            String versionStr = entry.getValue().get("version");
            Version v = Version.parseVersion((String)entry.getValue().get("version"));
            capReqBuilder.addAttribute("version", version);
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                String key = entry2.getKey();
                if (key.endsWith(":")) {
                    String directive = key.substring(0, key.length() - 1);
                    capReqBuilder.addDirective(directive, entry2.getValue());
                    continue;
                }
                if (key.equals("specification-version") || key.equals("version")) {
                    capReqBuilder.addAttribute("version", Version.parseVersion((String)entry2.getValue()));
                    continue;
                }
                capReqBuilder.addAttribute(key, entry2.getValue());
            }
            capReqBuilder.addAttribute("bundle-symbolic-name", bundleSymbolicName.getKey());
            capReqBuilder.addAttribute("bundle-version", version);
            resource.addCapability(capReqBuilder);
        }
        Parameters imports = d.getImportPackage();
        for (Map.Entry<String, Object> entry : imports.entrySet()) {
            CapReqBuilder imported = new CapReqBuilder("osgi.wiring.package");
            String name = Processor.removeDuplicateMarker(entry.getKey());
            String filter = OBRFragment.filter("osgi.wiring.package", Processor.removeDuplicateMarker(entry.getKey()), (Map)entry.getValue());
            imported.addDirective("filter", filter);
            resource.addRequirement(imported);
        }
        Parameters requires = d.getRequireBundle();
        for (Map.Entry<String, Attrs> entry : requires.entrySet()) {
            CapReqBuilder req = new CapReqBuilder("osgi.wiring.bundle");
            String bsn = Processor.removeDuplicateMarker(entry.getKey());
            String filter = OBRFragment.filter("osgi.wiring.bundle", bsn, entry.getValue());
            req.addDirective("filter", filter);
            resource.addRequirement(req);
        }
        Parameters parameters = d.getBundleRequiredExecutionEnvironment();
        try (Formatter formatter = new Formatter();){
            String service;
            formatter.format("(|", new Object[0]);
            for (Map.Entry<String, Attrs> bree : parameters.entrySet()) {
                String string = Processor.removeDuplicateMarker(bree.getKey());
                Matcher matcher = EE_PATTERN.matcher(string);
                if (!matcher.matches()) continue;
                String string2 = matcher.group(1);
                Version v = Version.parseVersion((String)matcher.group(2));
                formatter.format("%s", OBRFragment.filter("osgi.ee", string2, MAP.$("version", v.toString())));
            }
            formatter.format(")", new Object[0]);
            CapReqBuilder breeReq = new CapReqBuilder("osgi.ee");
            breeReq.addDirective("filter", formatter.toString());
            for (Map.Entry<String, Object> entry : d.getParameters("Export-Service").entrySet()) {
                CapReqBuilder exportedService = new CapReqBuilder("osgi.service");
                service = Processor.removeDuplicateMarker(entry.getKey());
                exportedService.addAttribute("osgi.service", service);
                exportedService.addAttribute("objectClass", ((Attrs)entry.getValue()).get("objectclass"));
                resource.addCapability(exportedService);
            }
            for (Map.Entry<String, Object> entry : d.getParameters("Import-Service").entrySet()) {
                CapReqBuilder importedService = new CapReqBuilder("osgi.service");
                service = Processor.removeDuplicateMarker(entry.getKey());
                importedService.addDirective("filter", OBRFragment.filter("osgi.service", service, (Map)entry.getValue()));
                resource.addRequirement(importedService);
            }
            for (Map.Entry<String, Object> entry : d.getProvideCapability().entrySet()) {
                resource.addCapability(OBRFragment.toCapability(entry.getKey(), (Attrs)entry.getValue()));
            }
            for (Map.Entry<String, Object> entry : d.getRequireCapability().entrySet()) {
                resource.addCapability(OBRFragment.toRequirement(entry.getKey(), (Attrs)entry.getValue()));
            }
        }
        return null;
    }

    private static Capability toRequirement(String key, Attrs value) {
        return null;
    }

    private static Capability toCapability(String key, Attrs value) {
        return null;
    }

    public static Reporter parse(File file, ResourceBuilder resource, String base) throws Exception {
        try (Jar jar = new Jar(file);){
            Reporter reporter = OBRFragment.parse(jar, resource);
            if (!reporter.isOk()) {
                Reporter reporter2 = reporter;
                return reporter2;
            }
            CapReqBuilder content = new CapReqBuilder("osgi.content");
            String sha = SHA1.digest(file).asHex();
            content.addAttribute("osgi.content", sha);
            content.addAttribute("size", file.length());
            content.addAttribute("mime", MIME_TYPE_OSGI_BUNDLE);
            if (base != null) {
                String path = file.getAbsolutePath();
                if (base.startsWith(path)) {
                    content.addAttribute("url", path.substring(base.length()).replace(File.separatorChar, '/'));
                } else {
                    reporter.error("Base path %s is not parent of file path: %s", base, file.getAbsolutePath());
                }
            }
            resource.addCapability(content);
            Reporter reporter3 = reporter;
            return reporter3;
        }
    }

    private static String filter(String ns, String primary, Map<String, String> value) {
        try (Formatter f = new Formatter();){
            f.format("(&(%s=%s)", ns, primary);
            for (String key : value.keySet()) {
                if (key.equals("version") || key.equals("bundle-version")) {
                    VersionRange vr = new VersionRange(value.get(key));
                    continue;
                }
                f.format("(%s=%s)", key, value.get(key));
            }
            f.format(")", new Object[0]);
        }
        return null;
    }
}

