/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.differ;

import aQute.bnd.differ.Element;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Type;
import aQute.bnd.version.Version;
import java.util.ArrayList;

public class RepositoryElement {
    public static Element getTree(RepositoryPlugin repo) throws Exception {
        ArrayList<Element> programs = new ArrayList<Element>();
        for (String bsn : repo.list(null)) {
            ArrayList<Element> versions = new ArrayList<Element>();
            for (Version version : repo.versions(bsn)) {
                versions.add(new Element(Type.VERSION, version.toString()));
            }
            programs.add(new Element(Type.PROGRAM, bsn, versions, Delta.MINOR, Delta.MAJOR, null));
        }
        return new Element(Type.REPO, repo.getName(), programs, Delta.MINOR, Delta.MAJOR, repo.getLocation());
    }
}

