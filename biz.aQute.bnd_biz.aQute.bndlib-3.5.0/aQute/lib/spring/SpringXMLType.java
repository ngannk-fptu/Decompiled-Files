/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.spring;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.osgi.Analyzer;
import aQute.lib.spring.XMLType;
import aQute.lib.spring.XMLTypeProcessor;
import java.util.ArrayList;
import java.util.List;

@BndPlugin(name="blueprint")
public class SpringXMLType
extends XMLTypeProcessor {
    @Override
    protected List<XMLType> getTypes(Analyzer analyzer) throws Exception {
        ArrayList<XMLType> types = new ArrayList<XMLType>();
        String header = analyzer.getProperty("Bundle-Blueprint", "OSGI-INF/blueprint");
        this.process(types, "extract.xsl", header, ".*\\.xml");
        header = analyzer.getProperty("Spring-Context", "META-INF/spring");
        this.process(types, "extract.xsl", header, ".*\\.xml");
        return types;
    }
}

