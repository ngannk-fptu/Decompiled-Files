/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package org.apache.felix.framework.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.felix.framework.util.ClassParser;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public class ClassFileVisitor
extends SimpleFileVisitor<Path> {
    private final Set<String> m_imports;
    private final Set<String> m_exports;
    private final ClassParser m_classParser;
    private final SortedMap<String, SortedSet<String>> m_result;

    public ClassFileVisitor(Set<String> imports, Set<String> exports, ClassParser classParser, SortedMap<String, SortedSet<String>> result) {
        this.m_imports = imports;
        this.m_exports = exports;
        this.m_classParser = classParser;
        this.m_result = result;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        block7: {
            String name;
            if (file.getNameCount() > 3 && this.m_exports.contains(name = file.subpath(2, file.getNameCount() - 1).toString().replace("/", ".")) && file.toString().endsWith(".class")) {
                TreeSet<String> strings = (TreeSet<String>)this.m_result.get(name);
                if (!name.startsWith("java.")) {
                    try {
                        Set<String> refs = this.m_classParser.parseClassFileUses(file.toString(), Files.newInputStream(file, new OpenOption[0]));
                        refs.retainAll(this.m_imports);
                        refs.remove(name);
                        if (strings == null) {
                            strings = new TreeSet<String>(refs);
                            this.m_result.put(name, strings);
                            break block7;
                        }
                        strings.addAll(refs);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (strings == null) {
                    this.m_result.put(name, new TreeSet());
                }
            }
        }
        return FileVisitResult.CONTINUE;
    }
}

