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

@BndPlugin(name="jpa")
public class JPAComponent
extends XMLTypeProcessor {
    @Override
    protected List<XMLType> getTypes(Analyzer analyzer) throws Exception {
        ArrayList<XMLType> types = new ArrayList<XMLType>();
        this.process(types, "jpa.xsl", "META-INF", "persistence.xml");
        return types;
    }
}

