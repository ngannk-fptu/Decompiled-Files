/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make.coverage;

import aQute.bnd.make.coverage.Coverage;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.WriteResource;
import aQute.lib.tag.Tag;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CoverageResource
extends WriteResource {
    Collection<Clazz> testsuite;
    Collection<Clazz> service;

    public CoverageResource(Collection<Clazz> testsuite, Collection<Clazz> service) {
        this.testsuite = testsuite;
        this.service = service;
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream out) throws IOException {
        try {
            Map<Clazz.MethodDef, List<Clazz.MethodDef>> table = Coverage.getCrossRef(this.testsuite, this.service);
            Tag coverage = CoverageResource.toTag(table);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, Constants.DEFAULT_CHARSET));
            try {
                coverage.print(0, pw);
            }
            finally {
                pw.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Tag toTag(Map<Clazz.MethodDef, List<Clazz.MethodDef>> catalog) {
        Tag coverage = new Tag("coverage", new Object[0]);
        String currentClass = null;
        Tag classTag = null;
        for (Map.Entry<Clazz.MethodDef, List<Clazz.MethodDef>> m : catalog.entrySet()) {
            String className = m.getKey().getContainingClass().getFQN();
            if (!className.equals(currentClass)) {
                classTag = new Tag("class", new Object[0]);
                classTag.addAttribute("name", className);
                classTag.addAttribute("package", Descriptors.getPackage(className));
                classTag.addAttribute("short", Descriptors.getShortName(className));
                coverage.addContent(classTag);
                currentClass = className;
            }
            Tag method = CoverageResource.doMethod(new Tag("method", new Object[0]), m.getKey());
            if (classTag != null) {
                classTag.addContent(method);
            }
            for (Clazz.MethodDef r : m.getValue()) {
                Tag ref = CoverageResource.doMethod(new Tag("ref", new Object[0]), r);
                method.addContent(ref);
            }
        }
        return coverage;
    }

    private static Tag doMethod(Tag tag, Clazz.MethodDef method) {
        tag.addAttribute("pretty", method.toString());
        if (method.isPublic()) {
            tag.addAttribute("public", true);
        }
        if (method.isStatic()) {
            tag.addAttribute("static", true);
        }
        if (method.isProtected()) {
            tag.addAttribute("protected", true);
        }
        if (method.isInterface()) {
            tag.addAttribute("interface", true);
        }
        tag.addAttribute("constructor", method.isConstructor());
        if (!method.isConstructor()) {
            tag.addAttribute("name", method.getName());
        }
        tag.addAttribute("descriptor", method.getDescriptor());
        return tag;
    }
}

