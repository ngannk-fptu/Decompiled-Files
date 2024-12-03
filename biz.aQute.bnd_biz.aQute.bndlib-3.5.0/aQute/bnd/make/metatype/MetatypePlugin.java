/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make.metatype;

import aQute.bnd.header.Parameters;
import aQute.bnd.make.metatype.MetaTypeReader;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Jar;
import aQute.bnd.service.AnalyzerPlugin;
import java.util.Collection;

public class MetatypePlugin
implements AnalyzerPlugin {
    @Override
    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        Parameters map = analyzer.parseHeader(analyzer.getProperty("-metatype"));
        Jar jar = analyzer.getJar();
        for (String name : map.keySet()) {
            Collection<Clazz> metatypes = analyzer.getClasses("", Clazz.QUERY.ANNOTATED.toString(), "aQute.bnd.annotation.metatype.Meta$OCD", Clazz.QUERY.NAMED.toString(), name);
            for (Clazz c : metatypes) {
                analyzer.warning("%s annotation used in class %s. Bnd metatype annotations are deprecated as of Bnd 3.2 and support will be removed in Bnd 4.0. Please change to use OSGi Metatype annotations.", "aQute.bnd.annotation.metatype.Meta$OCD", c);
                jar.putResource("OSGI-INF/metatype/" + c.getFQN() + ".xml", new MetaTypeReader(c, analyzer));
            }
        }
        return false;
    }

    public String toString() {
        return "MetatypePlugin";
    }
}

