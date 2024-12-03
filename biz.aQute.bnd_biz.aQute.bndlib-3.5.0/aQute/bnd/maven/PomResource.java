/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.WriteResource;
import aQute.lib.io.IO;
import aQute.lib.tag.Tag;
import aQute.libg.glob.Glob;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PomResource
extends WriteResource {
    private static final String VERSION = "version";
    private static final String ARTIFACTID = "artifactid";
    private static final String GROUPID = "groupid";
    private static final String WHERE = "where";
    final Manifest manifest;
    private Map<String, String> scm;
    final Processor processor;
    static final Pattern NAME_URL = Pattern.compile("(.*)(https?://.*)", 2);
    private String where;
    private String groupId;
    private String artifactId;
    private String version;
    private String name;

    public PomResource(Manifest manifest) {
        this(new Processor(), manifest);
    }

    public PomResource(Map<String, String> b, Manifest manifest) {
        this(PomResource.asProcessor(b), manifest);
    }

    private static Processor asProcessor(Map<String, String> b) {
        Processor p = new Processor();
        p.addProperties(b);
        return p;
    }

    public PomResource(Processor b, Manifest manifest) {
        this.manifest = manifest;
        this.processor = b;
        Domain domain = Domain.domain(manifest);
        String bsn = domain.getBundleSymbolicName().getKey();
        if (bsn == null) {
            throw new RuntimeException("Cannot create POM unless bsn is set");
        }
        this.groupId = this.augmentManifest(domain, bsn);
        this.name = domain.get("Bundle-Name");
        this.where = this.processor.get(WHERE);
        if (this.groupId == null) {
            this.groupId = this.processor.get(GROUPID);
        }
        if (this.groupId == null) {
            this.groupId = this.processor.get("-groupid");
        }
        if (this.groupId != null) {
            this.artifactId = this.processor.get(ARTIFACTID);
            if (this.artifactId == null) {
                this.artifactId = bsn;
            }
            if (this.where == null) {
                this.where = String.format("META-INF/maven/%s/%s/pom.xml", this.groupId, this.artifactId);
            }
        } else {
            int n = bsn.lastIndexOf(46);
            if (n <= 0) {
                throw new RuntimeException("\"groupid\" not set andBundle-SymbolicName does not contain a '.' to separate into a groupid and artifactid.");
            }
            this.artifactId = this.processor.get(ARTIFACTID);
            if (this.artifactId == null) {
                this.artifactId = bsn.substring(n + 1);
            }
            this.groupId = bsn.substring(0, n);
            if (this.where == null) {
                this.where = "pom.xml";
            }
        }
        if (this.name == null) {
            this.name = this.groupId + ":" + this.artifactId;
        }
        this.version = this.processor.get(VERSION);
        if (this.version == null) {
            this.version = domain.getBundleVersion();
        }
        if (this.version == null) {
            this.version = "0";
        }
    }

    public String augmentManifest(Domain domain, String bsn) {
        String groupid = null;
        Parameters augments = new Parameters(this.processor.mergeProperties("-pomaugment"), this.processor);
        for (Map.Entry<String, Attrs> augment : augments.entrySet()) {
            Glob g = new Glob(augment.getKey());
            if (!g.matcher(bsn).matches()) continue;
            Attrs attrs = augment.getValue();
            for (Map.Entry<String, String> attr : attrs.entrySet()) {
                String key = attr.getKey();
                boolean mandatory = false;
                if (key.startsWith("+")) {
                    key = key.substring(1);
                    mandatory = true;
                }
                if (key.length() > 0 && Character.isUpperCase(key.charAt(0))) {
                    if (!mandatory && domain.get(key) != null) continue;
                    domain.set(key, attr.getValue());
                    continue;
                }
                if (!GROUPID.equals(key)) continue;
                groupid = attr.getValue();
            }
        }
        return groupid;
    }

    public String getWhere() {
        return this.where;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getVersion() {
        return this.version;
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        String validate;
        Parameters developers;
        String description = this.manifest.getMainAttributes().getValue("Bundle-Description");
        String docUrl = this.manifest.getMainAttributes().getValue("Bundle-DocURL");
        String bundleVendor = this.manifest.getMainAttributes().getValue("Bundle-Vendor");
        String bundleLicense = this.manifest.getMainAttributes().getValue("Bundle-License");
        Tag project = new Tag("project", new Object[0]);
        project.addAttribute("xmlns", "http://maven.apache.org/POM/4.0.0");
        project.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        project.addAttribute("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd");
        project.addContent(new Tag("modelVersion", new Object[0]).addContent("4.0.0"));
        project.addContent(new Tag("groupId", new Object[0]).addContent(this.getGroupId()));
        project.addContent(new Tag("artifactId", new Object[0]).addContent(this.getArtifactId()));
        project.addContent(new Tag(VERSION, new Object[0]).addContent(this.getVersion()));
        if (description == null) {
            description = this.name;
        }
        new Tag(project, "description", new Object[0]).addContent(description);
        new Tag(project, "name", new Object[0]).addContent(this.name);
        if (docUrl != null) {
            new Tag(project, "url", new Object[0]).addContent(docUrl);
        }
        if (this.scm != null) {
            Tag scm = new Tag(project, "scm", new Object[0]);
            for (Map.Entry<String, String> e : this.scm.entrySet()) {
                new Tag(scm, e.getKey(), new Object[0]).addContent(e.getValue());
            }
        }
        if (bundleVendor != null) {
            Matcher m = NAME_URL.matcher(bundleVendor);
            String namePart = bundleVendor;
            String urlPart = null;
            if (m.matches()) {
                namePart = m.group(1);
                urlPart = m.group(2);
            }
            Tag organization = new Tag(project, "organization", new Object[0]);
            new Tag(organization, "name", new Object[0]).addContent(namePart.trim());
            if (urlPart != null) {
                new Tag(organization, "url", new Object[0]).addContent(urlPart.trim());
            }
        }
        Tag ls = null;
        Parameters licenses = new Parameters(bundleLicense, this.processor);
        for (Map.Entry<String, Attrs> license : licenses.entrySet()) {
            String identifier = license.getKey();
            if (identifier == null || (identifier = identifier.trim()).equals("<<EXTERNAL>>")) continue;
            if (ls == null) {
                ls = new Tag(project, "licenses", new Object[0]);
            }
            Tag l = new Tag(ls, "license", new Object[0]);
            Map attrs = license.getValue();
            this.tagFromMap(l, attrs, "name", "name", identifier);
            this.tagFromMap(l, attrs, "link", "url", identifier);
            this.tagFromMap(l, attrs, "distribution", "distribution", "repo");
            this.tagFromMap(l, attrs, "description", "comments", null);
        }
        String scm = this.manifest.getMainAttributes().getValue("Bundle-SCM");
        if (scm != null && scm.length() > 0) {
            Attrs pscm = OSGiHeader.parseProperties(scm);
            Tag tscm = new Tag(project, "scm", new Object[0]);
            for (String s : pscm.keySet()) {
                new Tag(tscm, s, pscm.get(s));
            }
        }
        if ((developers = new Parameters(this.manifest.getMainAttributes().getValue("Bundle-Developers"), this.processor)).size() > 0) {
            Tag tdevelopers = new Tag(project, "developers", new Object[0]);
            for (String id : developers.keySet()) {
                Tag tdeveloper = new Tag(tdevelopers, "developer", new Object[0]);
                new Tag(tdeveloper, "id", id);
                Attrs i = new Attrs(developers.get(id));
                if (!i.containsKey("email")) {
                    i.put("email", id);
                }
                i.remove("id");
                for (String s : i.keySet()) {
                    if (s.equals("roles")) {
                        String[] roles;
                        Tag troles = new Tag(tdeveloper, "roles", new Object[0]);
                        for (String role : roles = i.get(s).trim().split("\\s*,\\s*")) {
                            new Tag(troles, "role", role);
                        }
                        continue;
                    }
                    new Tag(tdeveloper, s, i.get(s));
                }
            }
        }
        if ((validate = project.validate()) != null) {
            throw new IllegalArgumentException(validate);
        }
        PrintWriter pw = IO.writer(out);
        pw.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        project.print(0, pw);
        pw.flush();
    }

    private Tag tagFromMap(Tag parent, Map<String, String> attrs, String key, String tag, String defaultValue) {
        String value = attrs.get(key);
        if (value == null) {
            value = attrs.get(tag);
        }
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            return parent;
        }
        new Tag(parent, tag, new Object[0]).addContent(value.trim());
        return parent;
    }

    public void setProperties(Map<String, String> scm) {
        this.scm = scm;
    }

    public String validate() {
        return null;
    }
}

