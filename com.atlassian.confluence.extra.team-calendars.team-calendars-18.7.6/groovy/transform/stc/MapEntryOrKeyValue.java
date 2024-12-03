/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import groovy.transform.stc.ClosureSignatureHint;
import groovy.transform.stc.IncorrectTypeHintException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class MapEntryOrKeyValue
extends ClosureSignatureHint {
    private static final ClassNode MAPENTRY_TYPE = ClassHelper.make(Map.Entry.class);

    @Override
    public List<ClassNode[]> getClosureSignatures(MethodNode node, SourceUnit sourceUnit, CompilationUnit compilationUnit, String[] options, ASTNode usage) {
        ClassNode[] secondSig;
        ClassNode[] firstSig;
        Options opt;
        try {
            opt = Options.parse(node, usage, options);
        }
        catch (IncorrectTypeHintException e) {
            sourceUnit.addError(e);
            return Collections.emptyList();
        }
        GenericsType[] genericsTypes = node.getParameters()[opt.parameterIndex].getOriginType().getGenericsTypes();
        if (genericsTypes == null) {
            genericsTypes = new GenericsType[]{new GenericsType(ClassHelper.OBJECT_TYPE), new GenericsType(ClassHelper.OBJECT_TYPE)};
        }
        ClassNode mapEntry = MAPENTRY_TYPE.getPlainNodeReference();
        mapEntry.setGenericsTypes(genericsTypes);
        if (opt.generateIndex) {
            firstSig = new ClassNode[]{genericsTypes[0].getType(), genericsTypes[1].getType(), ClassHelper.int_TYPE};
            secondSig = new ClassNode[]{mapEntry, ClassHelper.int_TYPE};
        } else {
            firstSig = new ClassNode[]{genericsTypes[0].getType(), genericsTypes[1].getType()};
            secondSig = new ClassNode[]{mapEntry};
        }
        return Arrays.asList(firstSig, secondSig);
    }

    private static final class Options {
        final int parameterIndex;
        final boolean generateIndex;

        private Options(int parameterIndex, boolean generateIndex) {
            this.parameterIndex = parameterIndex;
            this.generateIndex = generateIndex;
        }

        static Options parse(MethodNode mn, ASTNode source, String[] options) throws IncorrectTypeHintException {
            int pIndex = 0;
            boolean generateIndex = false;
            for (String option : options) {
                String[] keyValue = option.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    if ("argNum".equals(key)) {
                        pIndex = Integer.parseInt(value);
                        continue;
                    }
                    if ("index".equals(key)) {
                        generateIndex = Boolean.valueOf(value);
                        continue;
                    }
                    throw new IncorrectTypeHintException(mn, "Unrecognized option: " + key, source.getLineNumber(), source.getColumnNumber());
                }
                throw new IncorrectTypeHintException(mn, "Incorrect option format. Should be argNum=<num> or index=<boolean> ", source.getLineNumber(), source.getColumnNumber());
            }
            return new Options(pIndex, generateIndex);
        }
    }
}

