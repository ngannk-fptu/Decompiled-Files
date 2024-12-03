/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers;

import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class ImportCustomizer
extends CompilationCustomizer {
    private final List<Import> imports = new LinkedList<Import>();

    public ImportCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        ModuleNode ast = source.getAST();
        for (Import anImport : this.imports) {
            switch (anImport.type) {
                case regular: {
                    ast.addImport(anImport.alias, anImport.classNode);
                    break;
                }
                case staticImport: {
                    ast.addStaticImport(anImport.classNode, anImport.field, anImport.alias);
                    break;
                }
                case staticStar: {
                    ast.addStaticStarImport(anImport.alias, anImport.classNode);
                    break;
                }
                case star: {
                    ast.addStarImport(anImport.star);
                }
            }
        }
    }

    public ImportCustomizer addImport(String alias, String className) {
        this.imports.add(new Import(ImportType.regular, alias, ClassHelper.make(className)));
        return this;
    }

    public ImportCustomizer addStaticImport(String className, String fieldName) {
        ClassNode node = ClassHelper.make(className);
        this.imports.add(new Import(ImportType.staticImport, fieldName, node, fieldName));
        return this;
    }

    public ImportCustomizer addStaticStars(String ... classNames) {
        for (String className : classNames) {
            this.addStaticStar(className);
        }
        return this;
    }

    public ImportCustomizer addStaticImport(String alias, String className, String fieldName) {
        this.imports.add(new Import(ImportType.staticImport, alias, ClassHelper.make(className), fieldName));
        return this;
    }

    public ImportCustomizer addImports(String ... imports) {
        for (String anImport : imports) {
            this.addImport(anImport);
        }
        return this;
    }

    public ImportCustomizer addStarImports(String ... packageNames) {
        for (String packageName : packageNames) {
            this.addStarImport(packageName);
        }
        return this;
    }

    private void addImport(String className) {
        ClassNode node = ClassHelper.make(className);
        this.imports.add(new Import(ImportType.regular, node.getNameWithoutPackage(), node));
    }

    private void addStaticStar(String className) {
        this.imports.add(new Import(ImportType.staticStar, className, ClassHelper.make(className)));
    }

    private void addStarImport(String packagename) {
        String packageNameEndingWithDot = packagename.endsWith(".") ? packagename : packagename + '.';
        this.imports.add(new Import(ImportType.star, packageNameEndingWithDot));
    }

    private static enum ImportType {
        regular,
        staticImport,
        staticStar,
        star;

    }

    private static final class Import {
        final ImportType type;
        final ClassNode classNode;
        final String alias;
        final String field;
        final String star;

        private Import(ImportType type, String alias, ClassNode classNode, String field) {
            this.alias = alias;
            this.classNode = classNode;
            this.field = field;
            this.type = type;
            this.star = null;
        }

        private Import(ImportType type, String alias, ClassNode classNode) {
            this.alias = alias;
            this.classNode = classNode;
            this.type = type;
            this.field = null;
            this.star = null;
        }

        private Import(ImportType type, String star) {
            this.type = type;
            this.star = star;
            this.alias = null;
            this.classNode = null;
            this.field = null;
        }
    }
}

