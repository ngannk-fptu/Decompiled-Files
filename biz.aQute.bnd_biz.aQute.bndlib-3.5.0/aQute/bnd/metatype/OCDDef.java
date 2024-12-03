/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.bnd.metatype.ADDef;
import aQute.bnd.metatype.DesignateDef;
import aQute.bnd.metatype.IconDef;
import aQute.bnd.metatype.MetatypeVersion;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.xmlattribute.ExtensionDef;
import aQute.bnd.xmlattribute.Namespaces;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.tag.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OCDDef
extends ExtensionDef {
    final List<ADDef> attributes = new ArrayList<ADDef>();
    final List<IconDef> icons = new ArrayList<IconDef>();
    final List<DesignateDef> designates = new ArrayList<DesignateDef>();
    String id;
    String name;
    String localization;
    String description;
    MetatypeVersion version;

    public OCDDef(XMLAttributeFinder finder, MetatypeVersion minVersion) {
        super(finder);
        this.version = minVersion;
    }

    void prepare(Analyzer analyzer) {
        if (this.attributes.isEmpty()) {
            this.updateVersion(MetatypeVersion.VERSION_1_3);
        }
        HashSet<String> adIds = new HashSet<String>();
        for (ADDef ad : this.attributes) {
            ad.prepare(this);
            if (adIds.add(ad.id)) continue;
            analyzer.error("OCD for %s.%s has duplicate AD id %s due to colliding munged element names", this.id, this.name, ad.id);
        }
    }

    Tag getTag() {
        Tag metadata = new Tag("metatype:MetaData", new Object[0]);
        Namespaces namespaces = new Namespaces();
        String xmlns = this.version.getNamespace();
        namespaces.registerNamespace("metatype", xmlns);
        this.addNamespaces(namespaces, xmlns);
        for (ADDef ad : this.attributes) {
            ad.addNamespaces(namespaces, xmlns);
        }
        for (DesignateDef dd : this.designates) {
            dd.addNamespaces(namespaces, xmlns);
        }
        namespaces.addNamespaces(metadata);
        if (this.localization != null) {
            metadata.addAttribute("localization", this.localization);
        }
        Tag ocd = new Tag(metadata, "OCD", new Object[0]).addAttribute("id", this.id);
        if (this.name != null) {
            ocd.addAttribute("name", this.name);
        }
        if (this.description != null) {
            ocd.addAttribute("description", this.description);
        }
        this.addAttributes(ocd, namespaces);
        for (ADDef ad : this.attributes) {
            ocd.addContent(ad.getTag(namespaces));
        }
        for (IconDef icon : this.icons) {
            ocd.addContent(icon.getTag());
        }
        for (DesignateDef designate : this.designates) {
            metadata.addContent(designate.getInnerTag(namespaces));
        }
        return metadata;
    }

    void updateVersion(MetatypeVersion version) {
        this.version = OCDDef.max(this.version, version);
    }

    static <T extends Comparable<T>> T max(T a, T b) {
        int n = a.compareTo(b);
        if (n >= 0) {
            return a;
        }
        return b;
    }
}

