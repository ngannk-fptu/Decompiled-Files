/*
 * Decompiled with CFR 0.152.
 */
package org.jfree;

import java.util.Arrays;
import java.util.ResourceBundle;
import org.jfree.base.Library;
import org.jfree.ui.about.Contributor;
import org.jfree.ui.about.Licences;
import org.jfree.ui.about.ProjectInfo;
import org.jfree.util.ResourceBundleWrapper;

public class JCommonInfo
extends ProjectInfo {
    private static JCommonInfo singleton;
    static /* synthetic */ Class class$org$jfree$base$BaseBoot;

    public static synchronized JCommonInfo getInstance() {
        if (singleton == null) {
            singleton = new JCommonInfo();
        }
        return singleton;
    }

    private JCommonInfo() {
        String baseResourceClass = "org.jfree.resources.JCommonResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.resources.JCommonResources");
        this.setName(resources.getString("project.name"));
        this.setVersion(resources.getString("project.version"));
        this.setInfo(resources.getString("project.info"));
        this.setCopyright(resources.getString("project.copyright"));
        this.setLicenceName("LGPL");
        this.setLicenceText(Licences.getInstance().getLGPL());
        this.setContributors(Arrays.asList(new Contributor("Anthony Boulestreau", "-"), new Contributor("Jeremy Bowman", "-"), new Contributor("J. David Eisenberg", "-"), new Contributor("Paul English", "-"), new Contributor("David Gilbert", "david.gilbert@object-refinery.com"), new Contributor("Hans-Jurgen Greiner", "-"), new Contributor("Arik Levin", "-"), new Contributor("Achilleus Mantzios", "-"), new Contributor("Thomas Meier", "-"), new Contributor("Aaron Metzger", "-"), new Contributor("Thomas Morgner", "-"), new Contributor("Krzysztof Paz", "-"), new Contributor("Nabuo Tamemasa", "-"), new Contributor("Mark Watson", "-"), new Contributor("Matthew Wright", "-"), new Contributor("Hari", "-"), new Contributor("Sam (oldman)", "-")));
        this.addOptionalLibrary(new Library("JUnit", "3.8", "IBM Public Licence", "http://www.junit.org/"));
        this.setBootClass((class$org$jfree$base$BaseBoot == null ? (class$org$jfree$base$BaseBoot = JCommonInfo.class$("org.jfree.base.BaseBoot")) : class$org$jfree$base$BaseBoot).getName());
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

