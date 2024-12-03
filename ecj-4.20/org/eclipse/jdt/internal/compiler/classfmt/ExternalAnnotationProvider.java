/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ElementValuePairInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.SignatureWrapper;

public class ExternalAnnotationProvider {
    public static final String ANNOTATION_FILE_EXTENSION = "eea";
    public static final String CLASS_PREFIX = "class ";
    public static final String SUPER_PREFIX = "super ";
    public static final char NULLABLE = '0';
    public static final char NONNULL = '1';
    public static final char NO_ANNOTATION = '@';
    public static final String ANNOTATION_FILE_SUFFIX = ".eea";
    private static final String TYPE_PARAMETER_PREFIX = " <";
    private String typeName;
    String typeParametersAnnotationSource;
    Map<String, String> supertypeAnnotationSources;
    private Map<String, String> methodAnnotationSources;
    private Map<String, String> fieldAnnotationSources;
    SingleMarkerAnnotation NULLABLE_ANNOTATION;
    SingleMarkerAnnotation NONNULL_ANNOTATION;

    public ExternalAnnotationProvider(InputStream input, String typeName) throws IOException {
        this.typeName = typeName;
        this.initialize(input);
    }

    private void initialize(InputStream input) throws IOException {
        Throwable throwable = null;
        Object var3_4 = null;
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(input));){
            String pendingLine;
            ExternalAnnotationProvider.assertClassHeader(reader.readLine(), this.typeName);
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            if (line.startsWith(TYPE_PARAMETER_PREFIX)) {
                line = reader.readLine();
                if (line == null) {
                    return;
                }
                if (line.startsWith(TYPE_PARAMETER_PREFIX)) {
                    this.typeParametersAnnotationSource = line.substring(TYPE_PARAMETER_PREFIX.length());
                    line = reader.readLine();
                    if (line == null) {
                        return;
                    }
                }
            }
            do {
                pendingLine = null;
                if ((line = line.trim()).isEmpty()) continue;
                String rawSig = null;
                String annotSig = null;
                String selector = line;
                boolean isSuper = selector.startsWith(SUPER_PREFIX);
                if (isSuper) {
                    selector = selector.substring(SUPER_PREFIX.length());
                }
                int errLine = -1;
                try {
                    line = reader.readLine();
                    if (line != null && !line.isEmpty() && line.charAt(0) == ' ') {
                        rawSig = line.substring(1);
                    } else {
                        errLine = reader.getLineNumber();
                    }
                    line = reader.readLine();
                    if (line == null || line.isEmpty()) continue;
                    if (line.charAt(0) != ' ') {
                        pendingLine = line;
                        continue;
                    }
                    annotSig = line.substring(1);
                }
                catch (Exception exception) {}
                if (rawSig == null || annotSig == null) {
                    if (errLine == -1) {
                        errLine = reader.getLineNumber();
                    }
                    throw new IOException("Illegal format in annotation file for " + this.typeName + " at line " + errLine);
                }
                annotSig = ExternalAnnotationProvider.trimTail(annotSig);
                if (isSuper) {
                    if (this.supertypeAnnotationSources == null) {
                        this.supertypeAnnotationSources = new HashMap<String, String>();
                    }
                    this.supertypeAnnotationSources.put(String.valueOf('L') + selector + rawSig + ';', annotSig);
                    continue;
                }
                if (rawSig.contains("(")) {
                    if (this.methodAnnotationSources == null) {
                        this.methodAnnotationSources = new HashMap<String, String>();
                    }
                    this.methodAnnotationSources.put(String.valueOf(selector) + rawSig, annotSig);
                    continue;
                }
                if (this.fieldAnnotationSources == null) {
                    this.fieldAnnotationSources = new HashMap<String, String>();
                }
                this.fieldAnnotationSources.put(String.valueOf(selector) + ':' + rawSig, annotSig);
            } while ((line = pendingLine) != null || (line = reader.readLine()) != null);
        }
        catch (Throwable throwable2) {
            if (throwable == null) {
                throwable = throwable2;
            } else if (throwable != throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
    }

    public static void assertClassHeader(String line, String typeName) throws IOException {
        if (line == null || !line.startsWith(CLASS_PREFIX)) {
            throw new IOException("missing class header in annotation file for " + typeName);
        }
        line = line.substring(CLASS_PREFIX.length());
        if (!ExternalAnnotationProvider.trimTail(line).equals(typeName)) {
            throw new IOException("mismatching class name in annotation file, expected " + typeName + ", but header said " + line);
        }
    }

    public static String extractSignature(String line) {
        if (line == null || line.isEmpty() || line.charAt(0) != ' ') {
            return null;
        }
        return ExternalAnnotationProvider.trimTail(line.substring(1));
    }

    protected static String trimTail(String line) {
        int tail = line.indexOf(32);
        if (tail == -1) {
            tail = line.indexOf(9);
        }
        if (tail != -1) {
            return line.substring(0, tail);
        }
        return line;
    }

    public ITypeAnnotationWalker forTypeHeader(LookupEnvironment environment) {
        if (this.typeParametersAnnotationSource != null || this.supertypeAnnotationSources != null) {
            return new DispatchingAnnotationWalker(environment);
        }
        return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }

    public ITypeAnnotationWalker forMethod(char[] selector, char[] signature, LookupEnvironment environment) {
        String source;
        Map<String, String> sources = this.methodAnnotationSources;
        if (sources != null && (source = sources.get(String.valueOf(CharOperation.concat(selector, signature)))) != null) {
            return new MethodAnnotationWalker(source.toCharArray(), 0, environment);
        }
        return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }

    public ITypeAnnotationWalker forField(char[] selector, char[] signature, LookupEnvironment environment) {
        String source;
        if (this.fieldAnnotationSources != null && (source = this.fieldAnnotationSources.get(String.valueOf(CharOperation.concat(selector, signature, ':')))) != null) {
            return new FieldAnnotationWalker(source.toCharArray(), 0, environment);
        }
        return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("External Annotations for ").append(this.typeName).append('\n');
        sb.append("Methods:\n");
        if (this.methodAnnotationSources != null) {
            for (Map.Entry<String, String> e : this.methodAnnotationSources.entrySet()) {
                sb.append('\t').append(e.getKey()).append('\n');
            }
        }
        return sb.toString();
    }

    void initAnnotations(final LookupEnvironment environment) {
        if (this.NULLABLE_ANNOTATION == null) {
            this.NULLABLE_ANNOTATION = new SingleMarkerAnnotation(){

                @Override
                public char[] getTypeName() {
                    return this.getBinaryTypeName(environment.getNullableAnnotationName());
                }
            };
        }
        if (this.NONNULL_ANNOTATION == null) {
            this.NONNULL_ANNOTATION = new SingleMarkerAnnotation(){

                @Override
                public char[] getTypeName() {
                    return this.getBinaryTypeName(environment.getNonNullAnnotationName());
                }
            };
        }
    }

    abstract class BasicAnnotationWalker
    implements ITypeAnnotationWalker {
        char[] source;
        SignatureWrapper wrapper;
        int pos;
        int prevTypeArgStart;
        int currentTypeBound;
        LookupEnvironment environment;

        BasicAnnotationWalker(char[] source, int pos, LookupEnvironment environment) {
            this.source = source;
            this.pos = pos;
            this.environment = environment;
            ExternalAnnotationProvider.this.initAnnotations(environment);
        }

        SignatureWrapper wrapperWithStart(int start) {
            if (this.wrapper == null) {
                this.wrapper = new SignatureWrapper(this.source);
            }
            this.wrapper.start = start;
            this.wrapper.bracket = -1;
            return this.wrapper;
        }

        @Override
        public ITypeAnnotationWalker toReceiver() {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toTypeBound(short boundIndex) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toSupertype(short index, char[] superTypeSignature) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toTypeArgument(int rank) {
            if (rank == 0) {
                int start;
                this.prevTypeArgStart = start = CharOperation.indexOf('<', this.source, this.pos) + 1;
                return new MethodAnnotationWalker(this.source, start, this.environment);
            }
            int next = this.prevTypeArgStart;
            switch (this.source[next]) {
                case '*': {
                    next = this.skipNullAnnotation(next + 1);
                    break;
                }
                case '+': 
                case '-': {
                    next = this.skipNullAnnotation(next + 1);
                }
                default: {
                    next = this.wrapperWithStart(next).computeEnd();
                    ++next;
                }
            }
            this.prevTypeArgStart = next;
            return new MethodAnnotationWalker(this.source, next, this.environment);
        }

        @Override
        public ITypeAnnotationWalker toWildcardBound() {
            switch (this.source[this.pos]) {
                case '+': 
                case '-': {
                    int newPos = this.skipNullAnnotation(this.pos + 1);
                    return new MethodAnnotationWalker(this.source, newPos, this.environment);
                }
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toNextArrayDimension() {
            if (this.source[this.pos] == '[') {
                int newPos = this.skipNullAnnotation(this.pos + 1);
                return new MethodAnnotationWalker(this.source, newPos, this.environment);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toNextNestedType() {
            return this;
        }

        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(int currentTypeId, boolean mayApplyArrayContentsDefaultNullness) {
            if (this.pos != -1 && this.pos < this.source.length - 2) {
                switch (this.source[this.pos]) {
                    case '*': 
                    case '+': 
                    case '-': 
                    case 'L': 
                    case 'T': 
                    case '[': {
                        switch (this.source[this.pos + 1]) {
                            case '0': {
                                return new IBinaryAnnotation[]{ExternalAnnotationProvider.this.NULLABLE_ANNOTATION};
                            }
                            case '1': {
                                return new IBinaryAnnotation[]{ExternalAnnotationProvider.this.NONNULL_ANNOTATION};
                            }
                        }
                    }
                }
            }
            return NO_ANNOTATIONS;
        }

        int skipNullAnnotation(int cur) {
            if (cur >= this.source.length) {
                return cur;
            }
            switch (this.source[cur]) {
                case '0': 
                case '1': {
                    return cur + 1;
                }
            }
            return cur;
        }
    }

    class DispatchingAnnotationWalker
    implements ITypeAnnotationWalker {
        private LookupEnvironment environment;
        private TypeParametersAnnotationWalker typeParametersWalker;

        public DispatchingAnnotationWalker(LookupEnvironment environment) {
            this.environment = environment;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
            String source = ExternalAnnotationProvider.this.typeParametersAnnotationSource;
            if (source != null) {
                if (this.typeParametersWalker == null) {
                    this.typeParametersWalker = new TypeParametersAnnotationWalker(source.toCharArray(), 0, 0, null, this.environment);
                }
                return this.typeParametersWalker.toTypeParameter(isClassTypeParameter, rank);
            }
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
            if (this.typeParametersWalker != null) {
                return this.typeParametersWalker.toTypeParameterBounds(isClassTypeParameter, parameterRank);
            }
            return this;
        }

        @Override
        public ITypeAnnotationWalker toSupertype(short index, char[] superTypeSignature) {
            String source;
            Map<String, String> sources = ExternalAnnotationProvider.this.supertypeAnnotationSources;
            if (sources != null && (source = sources.get(String.valueOf(superTypeSignature))) != null) {
                return new SuperTypesAnnotationWalker(source.toCharArray(), this.environment);
            }
            return this;
        }

        @Override
        public ITypeAnnotationWalker toField() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toThrows(int rank) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeArgument(int rank) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toMethodParameter(short index) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toTypeBound(short boundIndex) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toReceiver() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toWildcardBound() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toNextArrayDimension() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toNextNestedType() {
            return this;
        }

        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(int currentTypeId, boolean mayApplyArrayContentsDefaultNullness) {
            return NO_ANNOTATIONS;
        }
    }

    class FieldAnnotationWalker
    extends BasicAnnotationWalker {
        public FieldAnnotationWalker(char[] source, int pos, LookupEnvironment environment) {
            super(source, pos, environment);
        }

        @Override
        public ITypeAnnotationWalker toField() {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            throw new UnsupportedOperationException("Field has no method return");
        }

        @Override
        public ITypeAnnotationWalker toMethodParameter(short index) {
            throw new UnsupportedOperationException("Field has no method parameter");
        }

        @Override
        public ITypeAnnotationWalker toThrows(int index) {
            throw new UnsupportedOperationException("Field has no throws");
        }
    }

    public static interface IMethodAnnotationWalker
    extends ITypeAnnotationWalker {
        public int getParameterCount();
    }

    class MethodAnnotationWalker
    extends BasicAnnotationWalker
    implements IMethodAnnotationWalker {
        int prevParamStart;
        TypeParametersAnnotationWalker typeParametersWalker;

        MethodAnnotationWalker(char[] source, int pos, LookupEnvironment environment) {
            super(source, pos, environment);
        }

        int typeEnd(int start) {
            while (this.source[start] == '[') {
                ++start;
                start = this.skipNullAnnotation(start);
            }
            SignatureWrapper wrapper1 = this.wrapperWithStart(start);
            int end = wrapper1.skipAngleContents(wrapper1.computeEnd());
            return end;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
            if (this.source[0] == '<') {
                if (this.typeParametersWalker == null) {
                    this.typeParametersWalker = new TypeParametersAnnotationWalker(this.source, this.pos + 1, rank, null, this.environment);
                    return this.typeParametersWalker;
                }
                return this.typeParametersWalker.toTypeParameter(isClassTypeParameter, rank);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
            if (this.typeParametersWalker != null) {
                return this.typeParametersWalker.toTypeParameterBounds(isClassTypeParameter, parameterRank);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            int close = CharOperation.indexOf(')', this.source);
            if (close != -1) {
                this.pos = close + 1;
                return this;
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toMethodParameter(short index) {
            if (index == 0) {
                int start;
                this.prevParamStart = start = CharOperation.indexOf('(', this.source) + 1;
                this.pos = start;
                return this;
            }
            int end = this.typeEnd(this.prevParamStart);
            this.prevParamStart = ++end;
            this.pos = end;
            return this;
        }

        @Override
        public ITypeAnnotationWalker toThrows(int index) {
            return this;
        }

        @Override
        public ITypeAnnotationWalker toField() {
            throw new UnsupportedOperationException("Methods have no fields");
        }

        @Override
        public int getParameterCount() {
            int count = 0;
            int start = CharOperation.indexOf('(', this.source) + 1;
            while (start < this.source.length && this.source[start] != ')') {
                start = this.typeEnd(start) + 1;
                ++count;
            }
            return count;
        }
    }

    static abstract class SingleMarkerAnnotation
    implements IBinaryAnnotation {
        SingleMarkerAnnotation() {
        }

        @Override
        public IBinaryElementValuePair[] getElementValuePairs() {
            return ElementValuePairInfo.NoMembers;
        }

        @Override
        public boolean isExternalAnnotation() {
            return true;
        }

        protected char[] getBinaryTypeName(char[][] name) {
            return CharOperation.concat('L', CharOperation.concatWith(name, '/'), ';');
        }
    }

    class SuperTypesAnnotationWalker
    extends BasicAnnotationWalker {
        SuperTypesAnnotationWalker(char[] source, LookupEnvironment environment) {
            super(source, 0, environment);
        }

        @Override
        public ITypeAnnotationWalker toField() {
            throw new UnsupportedOperationException("Supertype has no field annotations");
        }

        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            throw new UnsupportedOperationException("Supertype has no method return");
        }

        @Override
        public ITypeAnnotationWalker toMethodParameter(short index) {
            throw new UnsupportedOperationException("Supertype has no method parameter");
        }

        @Override
        public ITypeAnnotationWalker toThrows(int index) {
            throw new UnsupportedOperationException("Supertype has no throws");
        }
    }

    public class TypeParametersAnnotationWalker
    extends BasicAnnotationWalker {
        int[] rankStarts;
        int currentRank;

        TypeParametersAnnotationWalker(char[] source, int pos, int rank, int[] rankStarts, LookupEnvironment environment) {
            super(source, pos, environment);
            this.currentRank = rank;
            if (rankStarts != null) {
                this.rankStarts = rankStarts;
            } else {
                int length = source.length;
                rankStarts = new int[length];
                int curRank = 0;
                int depth = 0;
                boolean pendingVariable = true;
                int i = pos;
                block6: while (i < length) {
                    switch (this.source[i]) {
                        case '<': {
                            ++depth;
                            break;
                        }
                        case '>': {
                            if (--depth >= 0) break;
                            break block6;
                        }
                        case ';': {
                            if (depth != 0 || i + 1 >= length || this.source[i + 1] == ':') break;
                            pendingVariable = true;
                            break;
                        }
                        case ':': {
                            if (depth == 0) {
                                pendingVariable = true;
                            }
                            ++i;
                            while (i < length && this.source[i] == '[') {
                                ++i;
                            }
                            if (i < length && this.source[i] == 'L') {
                                int currentdepth = depth;
                                while (i < length && (currentdepth != depth || this.source[i] != ';')) {
                                    if (this.source[i] == '<') {
                                        ++currentdepth;
                                    }
                                    if (this.source[i] == '>') {
                                        --currentdepth;
                                    }
                                    ++i;
                                }
                            }
                            --i;
                            break;
                        }
                        default: {
                            if (!pendingVariable) break;
                            pendingVariable = false;
                            rankStarts[curRank++] = i;
                        }
                    }
                    ++i;
                }
                this.rankStarts = new int[curRank];
                System.arraycopy(rankStarts, 0, this.rankStarts, 0, curRank);
            }
        }

        @Override
        public ITypeAnnotationWalker toTypeParameter(boolean isClassTypeParameter, int rank) {
            if (rank == this.currentRank) {
                return this;
            }
            if (rank < this.rankStarts.length) {
                return new TypeParametersAnnotationWalker(this.source, this.rankStarts[rank], rank, this.rankStarts, this.environment);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }

        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(boolean isClassTypeParameter, int parameterRank) {
            return new TypeParametersAnnotationWalker(this.source, this.rankStarts[parameterRank], parameterRank, this.rankStarts, this.environment);
        }

        @Override
        public ITypeAnnotationWalker toTypeBound(short boundIndex) {
            int p = this.pos;
            int i = this.currentTypeBound;
            while (true) {
                int colon;
                if ((colon = CharOperation.indexOf(':', this.source, p)) != -1) {
                    p = colon + 1;
                }
                if (++i > boundIndex) break;
                p = this.wrapperWithStart(p).computeEnd() + 1;
            }
            this.pos = p;
            this.currentTypeBound = boundIndex;
            return this;
        }

        @Override
        public ITypeAnnotationWalker toField() {
            throw new UnsupportedOperationException("Cannot navigate to fields");
        }

        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            throw new UnsupportedOperationException("Cannot navigate to method return");
        }

        @Override
        public ITypeAnnotationWalker toMethodParameter(short index) {
            throw new UnsupportedOperationException("Cannot navigate to method parameter");
        }

        @Override
        public ITypeAnnotationWalker toThrows(int index) {
            throw new UnsupportedOperationException("Cannot navigate to throws");
        }

        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(int currentTypeId, boolean mayApplyArrayContentsDefaultNullness) {
            if (this.pos != -1 && this.pos < this.source.length - 1) {
                switch (this.source[this.pos]) {
                    case '0': {
                        return new IBinaryAnnotation[]{ExternalAnnotationProvider.this.NULLABLE_ANNOTATION};
                    }
                    case '1': {
                        return new IBinaryAnnotation[]{ExternalAnnotationProvider.this.NONNULL_ANNOTATION};
                    }
                }
            }
            return super.getAnnotationsAtCursor(currentTypeId, mayApplyArrayContentsDefaultNullness);
        }
    }
}

