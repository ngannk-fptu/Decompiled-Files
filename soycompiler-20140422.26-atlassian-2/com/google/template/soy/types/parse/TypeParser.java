/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.types.parse;

import com.google.common.collect.Maps;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeRegistry;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.parse.ParseException;
import com.google.template.soy.types.parse.SimpleCharStream;
import com.google.template.soy.types.parse.Token;
import com.google.template.soy.types.parse.TokenMgrError;
import com.google.template.soy.types.parse.TypeParserConstants;
import com.google.template.soy.types.parse.TypeParserTokenManager;
import com.google.template.soy.types.primitive.UnknownType;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeParser
implements TypeParserConstants {
    private static final List<SoyType> EMPTY_TYPE_ARGS = Collections.emptyList();
    private SoyTypeRegistry typeRegistry;
    public TypeParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[9];
    private static int[] jj_la1_0;
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public TypeParser(String input, SoyTypeRegistry typeRegistry) {
        this(new StringReader(input));
        this.typeRegistry = typeRegistry;
    }

    public SoyType parseTypeExpression() throws TokenMgrError, ParseException {
        return this.TypeExpr();
    }

    public SoyType parseTypeDeclaration() throws TokenMgrError, ParseException {
        return this.TypeDecl();
    }

    private final SoyType TypeDecl() throws ParseException {
        SoyType type = this.TypeExpr();
        this.jj_consume_token(0);
        return type;
    }

    private final SoyType TypeExpr() throws ParseException {
        SoyType type = this.UnionTypeExpr();
        return type;
    }

    private final SoyType UnionTypeExpr() throws ParseException {
        ArrayList<SoyType> members = new ArrayList<SoyType>();
        SoyType type = this.Primary();
        members.add(type);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: {
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(6);
            type = this.Primary();
            members.add(type);
        }
        return members.size() > 1 ? UnionType.of(members) : (SoyType)members.get(0);
    }

    private final SoyType Primary() throws ParseException {
        SoyType type;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 12: {
                type = this.TypeName();
                break;
            }
            case 9: {
                type = this.UnknownType();
                break;
            }
            case 10: {
                type = this.ListType();
                break;
            }
            case 11: {
                type = this.MapType();
                break;
            }
            case 3: {
                type = this.RecordType();
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return type;
    }

    private final SoyType ListType() throws ParseException {
        List<SoyType> typeArgs = EMPTY_TYPE_ARGS;
        this.jj_consume_token(10);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.jj_consume_token(1);
                typeArgs = this.TypeList();
                this.jj_consume_token(2);
                break;
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
            }
        }
        if (typeArgs.size() == 1) {
            return this.typeRegistry.getOrCreateListType(typeArgs.get(0));
        }
        throw new ParseException("Expected 1 type parameter for type 'list', not " + typeArgs.size());
    }

    private final SoyType MapType() throws ParseException {
        List<SoyType> typeArgs = EMPTY_TYPE_ARGS;
        this.jj_consume_token(11);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.jj_consume_token(1);
                typeArgs = this.TypeList();
                this.jj_consume_token(2);
                break;
            }
            default: {
                this.jj_la1[3] = this.jj_gen;
            }
        }
        if (typeArgs.size() != 2) {
            throw new ParseException("Expected 2 type parameters for type 'map', not " + typeArgs.size());
        }
        return this.typeRegistry.getOrCreateMapType(typeArgs.get(0), typeArgs.get(1));
    }

    private final SoyType RecordType() throws ParseException {
        HashMap fields = Maps.newHashMap();
        this.jj_consume_token(3);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 12: {
                this.RecordField(fields);
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 5: {
                            break;
                        }
                        default: {
                            this.jj_la1[4] = this.jj_gen;
                            break block0;
                        }
                    }
                    this.jj_consume_token(5);
                    this.RecordField(fields);
                }
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
            }
        }
        this.jj_consume_token(4);
        return this.typeRegistry.getOrCreateRecordType(fields);
    }

    private final void RecordField(Map<String, SoyType> fields) throws ParseException {
        Token fieldName = this.jj_consume_token(12);
        this.jj_consume_token(7);
        SoyType fieldType = this.TypeExpr();
        if (fields.containsKey(fieldName.image)) {
            throw new ParseException("Duplicate field definition for record: " + fieldName.image);
        }
        fields.put(fieldName.image, fieldType);
    }

    private final SoyType TypeName() throws ParseException {
        String ident = this.DottedIdent();
        SoyType typeName = this.typeRegistry.getType(ident);
        if (typeName == null) {
            throw new ParseException("Unknown type '" + ident + "'.");
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.jj_consume_token(1);
                throw new ParseException("Template parameters not allowed for type ' " + ident + "'");
            }
        }
        this.jj_la1[6] = this.jj_gen;
        return typeName;
    }

    private final SoyType UnknownType() throws ParseException {
        this.jj_consume_token(9);
        return UnknownType.getInstance();
    }

    private final String DottedIdent() throws ParseException {
        StringBuilder sb = new StringBuilder();
        Token ident = this.jj_consume_token(12);
        sb.append(ident.image);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(8);
            ident = this.jj_consume_token(12);
            sb.append('.');
            sb.append(ident.image);
        }
        return sb.toString();
    }

    private final List<SoyType> TypeList() throws ParseException {
        ArrayList<SoyType> args = new ArrayList<SoyType>();
        SoyType type = this.TypeExpr();
        args.add(type);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: {
                    break;
                }
                default: {
                    this.jj_la1[8] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(5);
            type = this.TypeExpr();
            args.add(type);
        }
        return args;
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{64, 7688, 2, 2, 32, 4096, 2, 256, 32};
    }

    public TypeParser(InputStream stream) {
        this(stream, null);
    }

    public TypeParser(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new TypeParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 9; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 9; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public TypeParser(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new TypeParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 9; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 9; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public TypeParser(TypeParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 9; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(TypeParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 9; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[14];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 9; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 14; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    static {
        TypeParser.jj_la1_init_0();
    }
}

