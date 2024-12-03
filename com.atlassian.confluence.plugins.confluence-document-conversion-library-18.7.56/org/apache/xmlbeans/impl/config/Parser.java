/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.javaparser.ParseResult
 *  com.github.javaparser.ParserConfiguration
 *  com.github.javaparser.ParserConfiguration$LanguageLevel
 *  com.github.javaparser.ast.CompilationUnit
 *  com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
 *  com.github.javaparser.resolution.SymbolResolver
 *  com.github.javaparser.symbolsolver.JavaSymbolSolver
 *  com.github.javaparser.symbolsolver.model.resolution.TypeSolver
 *  com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver
 *  com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
 *  com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
 *  com.github.javaparser.utils.ProjectRoot
 *  com.github.javaparser.utils.SourceRoot
 */
package org.apache.xmlbeans.impl.config;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.xmlbeans.impl.config.ChildSolverCollectionStrategy;

class Parser {
    private final File[] javaFiles;
    private final File[] classpath;
    private final ParserConfiguration pc;
    private final ProjectRoot projectRoot;
    private final CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver(new TypeSolver[0]);

    public Parser(File[] javaFiles, File[] classpath) {
        this.javaFiles = javaFiles != null ? (File[])javaFiles.clone() : new File[]{};
        this.classpath = classpath != null ? (File[])classpath.clone() : new File[]{};
        this.pc = new ParserConfiguration();
        this.pc.setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE);
        URL[] urls = (URL[])Stream.of(this.classpath).map(Parser::fileToURL).filter(Objects::nonNull).toArray(URL[]::new);
        this.combinedTypeSolver.add((TypeSolver)new ClassLoaderTypeSolver((ClassLoader)new URLClassLoader(urls, this.getClass().getClassLoader())));
        this.combinedTypeSolver.add((TypeSolver)new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver((TypeSolver)this.combinedTypeSolver);
        this.pc.setSymbolResolver((SymbolResolver)symbolSolver);
        if (this.javaFiles.length > 0) {
            ChildSolverCollectionStrategy solver = new ChildSolverCollectionStrategy(this.pc, this.combinedTypeSolver);
            Stream.of(this.javaFiles).map(f -> f.isDirectory() ? f : f.getParentFile()).map(File::toPath).distinct().forEach(solver::collect);
            this.projectRoot = solver.collectAll();
        } else {
            this.projectRoot = null;
        }
    }

    public ClassOrInterfaceDeclaration loadSource(String className) {
        String fileName = className.replace('.', '/') + ".java";
        if (this.projectRoot == null) {
            return null;
        }
        return this.projectRoot.getSourceRoots().stream().map(sr -> this.parseOrNull((SourceRoot)sr, fileName)).filter(Objects::nonNull).filter(ParseResult::isSuccessful).map(ParseResult::getResult).map(Optional::get).flatMap(cu -> cu.getTypes().stream()).filter(ClassOrInterfaceDeclaration.class::isInstance).filter(t -> className.equals(t.getFullyQualifiedName().orElse(null))).map(ClassOrInterfaceDeclaration.class::cast).findFirst().orElse(null);
    }

    private static URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        }
        catch (MalformedURLException ignored) {
            return null;
        }
    }

    private ParseResult<CompilationUnit> parseOrNull(SourceRoot sr, String fileName) {
        try {
            return sr.tryToParse("", fileName, this.pc);
        }
        catch (IOException ignroed) {
            return null;
        }
    }
}

