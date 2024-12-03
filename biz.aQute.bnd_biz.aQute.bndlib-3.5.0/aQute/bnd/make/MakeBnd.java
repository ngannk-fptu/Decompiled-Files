/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.MakePlugin;
import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

public class MakeBnd
implements MakePlugin,
Constants {
    static final Pattern JARFILE = Pattern.compile("(.+)\\.(jar|ipa)");

    @Override
    public Resource make(Builder builder, String destination, Map<String, String> argumentsOnMake) throws Exception {
        String type = argumentsOnMake.get("type");
        if (!"bnd".equals(type)) {
            return null;
        }
        String recipe = argumentsOnMake.get("recipe");
        if (recipe == null) {
            builder.error("No recipe specified on a make instruction for %s", destination);
            return null;
        }
        File bndfile = builder.getFile(recipe);
        if (bndfile.isFile()) {
            Builder bchild = builder.getSubBuilder();
            bchild.removeBundleSpecificHeaders();
            bchild.setProperty("Include-Resource", "");
            bchild.setProperty("-includeresource", "");
            bchild.setProperties(bndfile, builder.getBase());
            Jar jar = bchild.build();
            Jar dot = builder.getJar();
            if (builder.hasSources()) {
                for (String key : jar.getResources().keySet()) {
                    if (!key.startsWith("OSGI-OPT/src")) continue;
                    dot.putResource(key, jar.getResource(key));
                }
            }
            builder.getInfo(bchild, bndfile.getName() + ": ");
            return new JarResource(jar);
        }
        return null;
    }
}

