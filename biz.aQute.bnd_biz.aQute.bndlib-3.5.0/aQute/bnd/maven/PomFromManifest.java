/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.WriteResource;
import aQute.bnd.version.Version;
import aQute.lib.io.IO;
import aQute.lib.tag.Tag;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PomFromManifest
extends WriteResource {
    final Manifest manifest;
    private List<String> scm = new ArrayList<String>();
    private List<String> developers = new ArrayList<String>();
    static final Pattern NAME_URL = Pattern.compile("(.*)(http://.*)");
    String xbsn;
    String xversion;
    String xgroupId;
    String xartifactId;
    private String projectURL;

    public String getBsn() {
        if (this.xbsn == null) {
            this.xbsn = this.manifest.getMainAttributes().getValue("Bundle-SymbolicName");
        }
        if (this.xbsn == null) {
            throw new RuntimeException("Cannot create POM unless bsn is set");
        }
        this.xbsn = this.xbsn.trim();
        int n = this.xbsn.lastIndexOf(46);
        if (n < 0) {
            n = this.xbsn.length();
            this.xbsn = this.xbsn + "." + this.xbsn;
        }
        if (this.xgroupId == null) {
            this.xgroupId = this.xbsn.substring(0, n);
        }
        if (this.xartifactId == null) {
            this.xartifactId = this.xbsn.substring(n + 1);
            n = this.xartifactId.indexOf(59);
            if (n > 0) {
                this.xartifactId = this.xartifactId.substring(0, n).trim();
            }
        }
        return this.xbsn;
    }

    public String getGroupId() {
        this.getBsn();
        return this.xgroupId;
    }

    public String getArtifactId() {
        this.getBsn();
        return this.xartifactId;
    }

    public Version getVersion() {
        if (this.xversion != null) {
            return new Version(this.xversion);
        }
        String version = this.manifest.getMainAttributes().getValue("Bundle-Version");
        Version v = new Version(version);
        return new Version(v.getMajor(), v.getMinor(), v.getMicro());
    }

    public PomFromManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        PrintWriter ps = IO.writer(out);
        String name = this.manifest.getMainAttributes().getValue("Bundle-Name");
        String description = this.manifest.getMainAttributes().getValue("Bundle-Description");
        String docUrl = this.manifest.getMainAttributes().getValue("Bundle-DocURL");
        String bundleVendor = this.manifest.getMainAttributes().getValue("Bundle-Vendor");
        String licenses = this.manifest.getMainAttributes().getValue("Bundle-License");
        Tag project = new Tag("project", new Object[0]);
        project.addAttribute("xmlns", "http://maven.apache.org/POM/4.0.0");
        project.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        project.addAttribute("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd");
        project.addContent(new Tag("modelVersion", new Object[0]).addContent("4.0.0"));
        project.addContent(new Tag("groupId", new Object[0]).addContent(this.getGroupId()));
        project.addContent(new Tag("artifactId", new Object[0]).addContent(this.getArtifactId()));
        project.addContent(new Tag("version", new Object[0]).addContent(this.getVersion().toString()));
        if (description != null) {
            new Tag(project, "description", new Object[0]).addContent(description);
        }
        if (name != null) {
            new Tag(project, "name", new Object[0]).addContent(name);
        }
        if (this.projectURL != null) {
            new Tag(project, "url", new Object[0]).addContent(this.projectURL);
        } else if (docUrl != null) {
            new Tag(project, "url", new Object[0]).addContent(docUrl);
        } else {
            new Tag(project, "url", new Object[0]).addContent("http://no-url");
        }
        String scmheader = this.manifest.getMainAttributes().getValue("Bundle-SCM");
        if (scmheader != null) {
            this.scm.add(scmheader);
        }
        Tag scmtag = new Tag(project, "scm", new Object[0]);
        if (this.scm != null && !this.scm.isEmpty()) {
            for (String cm : this.scm) {
                new Tag(scmtag, "url", new Object[0]).addContent(cm);
                new Tag(scmtag, "connection", new Object[0]).addContent(cm);
                new Tag(scmtag, "developerConnection", new Object[0]).addContent(cm);
            }
        } else {
            new Tag(scmtag, "url", new Object[0]).addContent("private");
            new Tag(scmtag, "connection", new Object[0]).addContent("private");
            new Tag(scmtag, "developerConnection", new Object[0]).addContent("private");
        }
        if (bundleVendor != null) {
            Matcher m = NAME_URL.matcher(bundleVendor);
            String namePart = bundleVendor;
            String urlPart = this.projectURL;
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
        if (!this.developers.isEmpty()) {
            Tag d = new Tag(project, "developers", new Object[0]);
            Iterator<String> i$ = this.developers.iterator();
            while (i$.hasNext()) {
                String email;
                String id = email = i$.next();
                String xname = email;
                String organization = null;
                Matcher m = Pattern.compile("([^@]+)@([\\d\\w\\-_\\.]+)\\.([\\d\\w\\-_\\.]+)").matcher(email);
                if (m.matches()) {
                    xname = m.group(1);
                    organization = m.group(2);
                }
                Tag developer = new Tag(d, "developer", new Object[0]);
                new Tag(developer, "id", new Object[0]).addContent(id);
                new Tag(developer, "name", new Object[0]).addContent(xname);
                new Tag(developer, "email", new Object[0]).addContent(email);
                if (organization == null) continue;
                new Tag(developer, "organization", new Object[0]).addContent(organization);
            }
        }
        if (licenses != null) {
            Tag ls = new Tag(project, "licenses", new Object[0]);
            Parameters map = Processor.parseHeader(licenses, null);
            for (Map.Entry<String, Attrs> entry : map.entrySet()) {
                Tag l = new Tag(ls, "license", new Object[0]);
                Map values = entry.getValue();
                String url = entry.getKey();
                if (values.containsKey("description")) {
                    this.tagFromMap(l, values, "description", "name", url);
                } else {
                    this.tagFromMap(l, values, "name", "name", url);
                }
                this.tagFromMap(l, values, "url", "url", url);
                this.tagFromMap(l, values, "distribution", "distribution", "repo");
            }
        }
        project.print(0, ps);
        ps.flush();
    }

    private Tag tagFromMap(Tag parent, Map<String, String> values, String string, String tag, String object) {
        String value = values.get(string);
        if (value == null) {
            value = object;
        }
        if (value == null) {
            return parent;
        }
        new Tag(parent, tag, new Object[0]).addContent(value.trim());
        return parent;
    }

    public void setSCM(String scm) {
        this.scm.add(scm);
    }

    public void setURL(String url) {
        this.projectURL = url;
    }

    public void setBsn(String bsn) {
        this.xbsn = bsn;
    }

    public void addDeveloper(String email) {
        this.developers.add(email);
    }

    public void setVersion(String version) {
        this.xversion = version;
    }

    public void setArtifact(String artifact) {
        this.xartifactId = artifact;
    }

    public void setGroup(String group) {
        this.xgroupId = group;
    }
}

