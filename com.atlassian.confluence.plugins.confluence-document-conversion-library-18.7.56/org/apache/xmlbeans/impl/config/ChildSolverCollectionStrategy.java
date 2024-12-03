/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.javaparser.ParserConfiguration
 *  com.github.javaparser.symbolsolver.model.resolution.TypeSolver
 *  com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
 *  com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
 *  com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
 *  com.github.javaparser.utils.CollectionStrategy
 *  com.github.javaparser.utils.ProjectRoot
 */
package org.apache.xmlbeans.impl.config;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.CollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ChildSolverCollectionStrategy
implements CollectionStrategy {
    private static final Logger LOG = LogManager.getLogger(ChildSolverCollectionStrategy.class);
    private final ParserConfiguration config;
    private final PathMatcher javaMatcher = this.getPathMatcher("glob:**.java");
    private final PathMatcher jarMatcher = this.getPathMatcher("glob:**.jar");
    private final List<Path> roots = new ArrayList<Path>();
    private final CombinedTypeSolver combinedTypeSolver;

    ChildSolverCollectionStrategy(ParserConfiguration config, CombinedTypeSolver combinedTypeSolver) {
        this.config = config;
        this.combinedTypeSolver = combinedTypeSolver;
    }

    public ParserConfiguration getParserConfiguration() {
        return this.config;
    }

    public ProjectRoot collect(Path path) {
        try {
            Files.walkFileTree(path, new FileVisitor());
        }
        catch (IOException e) {
            LOG.atWarn().withThrowable(e).log("Unable to walk {}", (Object)path);
        }
        return this.roots.isEmpty() ? null : new ProjectRoot(this.roots.get(this.roots.size() - 1), this.config);
    }

    public ProjectRoot collectAll() {
        Path root = null;
        for (Path p : this.roots) {
            if (root == null) {
                root = p;
                continue;
            }
            if ((root = ChildSolverCollectionStrategy.commonRoot(root, p)) != null) continue;
            break;
        }
        if (root == null) {
            throw new IllegalStateException("Unable to construct a common project root - giving up.");
        }
        ProjectRoot pr = new ProjectRoot(root, this.config);
        this.roots.forEach(arg_0 -> ((ProjectRoot)pr).addSourceRoot(arg_0));
        return pr;
    }

    private static Path commonRoot(Path path1, Path path2) {
        ArrayList l1 = new ArrayList();
        path1.toAbsolutePath().iterator().forEachRemaining(l1::add);
        ArrayList l2 = new ArrayList();
        path2.toAbsolutePath().iterator().forEachRemaining(l2::add);
        l1.retainAll(l2);
        return l1.isEmpty() ? null : (Path)l1.get(l1.size() - 1);
    }

    private CombinedTypeSolver getSolver() {
        return this.combinedTypeSolver;
    }

    private class FileVisitor
    extends SimpleFileVisitor<Path> {
        private FileVisitor() {
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (ChildSolverCollectionStrategy.this.javaMatcher.matches(file)) {
                if (ChildSolverCollectionStrategy.this.roots.stream().map(Path::toAbsolutePath).noneMatch(file.toAbsolutePath()::startsWith)) {
                    ChildSolverCollectionStrategy.this.getRoot(file).ifPresent(r -> {
                        ChildSolverCollectionStrategy.this.getSolver().add((TypeSolver)new JavaParserTypeSolver(r, ChildSolverCollectionStrategy.this.getParserConfiguration()));
                        ChildSolverCollectionStrategy.this.roots.add(r);
                    });
                }
            } else if (ChildSolverCollectionStrategy.this.jarMatcher.matches(file)) {
                ChildSolverCollectionStrategy.this.getSolver().add((TypeSolver)new JarTypeSolver(file));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return Files.isHidden(dir) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
        }
    }
}

