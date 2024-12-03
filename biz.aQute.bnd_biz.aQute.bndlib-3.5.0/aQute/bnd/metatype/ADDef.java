/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.bnd.metatype.MetatypeVersion;
import aQute.bnd.metatype.OCDDef;
import aQute.bnd.metatype.OptionDef;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.xmlattribute.ExtensionDef;
import aQute.bnd.xmlattribute.Namespaces;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.tag.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;

public class ADDef
extends ExtensionDef {
    AttributeDefinition ad;
    Annotation a;
    String id;
    String name;
    String description;
    AttributeType type;
    private String typeString;
    int cardinality;
    String min;
    String max;
    String[] defaults;
    boolean required = true;
    final List<OptionDef> options = new ArrayList<OptionDef>();
    private static final Pattern escapes = Pattern.compile("[ ,\\\\]");

    public ADDef(XMLAttributeFinder finder) {
        super(finder);
    }

    public void prepare(OCDDef ocdDef) {
        this.typeString = this.type == AttributeType.CHARACTER && ocdDef.version == MetatypeVersion.VERSION_1_2 ? "Char" : (this.type == null ? "*INVALID*" : this.type.toString());
    }

    Tag getTag(Namespaces namespaces) {
        Tag ad = new Tag("AD", new Object[0]).addAttribute("id", this.id).addAttribute("type", this.typeString);
        if (this.cardinality != 0) {
            ad.addAttribute("cardinality", this.cardinality);
        }
        if (!this.required) {
            ad.addAttribute("required", this.required);
        }
        if (this.name != null) {
            ad.addAttribute("name", this.name);
        }
        if (this.description != null) {
            ad.addAttribute("description", this.description);
        }
        if (this.min != null) {
            ad.addAttribute("min", this.min);
        }
        if (this.max != null) {
            ad.addAttribute("max", this.max);
        }
        if (this.defaults != null) {
            StringBuffer b = new StringBuffer();
            String sep = "";
            for (String defaultValue : this.defaults) {
                b.append(sep);
                this.escape(defaultValue, b);
                sep = ",";
            }
            ad.addAttribute("default", b.toString());
        }
        for (OptionDef option : this.options) {
            ad.addContent(option.getTag());
        }
        this.addAttributes(ad, namespaces);
        return ad;
    }

    private void escape(String defaultValue, StringBuffer b) {
        Matcher m = escapes.matcher(defaultValue);
        while (m.find()) {
            String match = m.group();
            if (match.equals("\\")) {
                m.appendReplacement(b, "\\\\\\\\");
                continue;
            }
            m.appendReplacement(b, "\\\\" + match);
        }
        m.appendTail(b);
    }
}

