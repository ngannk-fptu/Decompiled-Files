/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

import java.util.ArrayList;
import org.aspectj.util.GenericSignature;

public class GenericSignatureParser {
    private String inputString;
    private String[] tokenStream;
    private int tokenIndex = 0;

    public GenericSignature.ClassSignature parseAsClassSignature(String sig) {
        this.inputString = sig;
        this.tokenStream = this.tokenize(sig);
        this.tokenIndex = 0;
        GenericSignature.ClassSignature classSig = new GenericSignature.ClassSignature();
        if (this.maybeEat("<")) {
            ArrayList<GenericSignature.FormalTypeParameter> formalTypeParametersList = new ArrayList<GenericSignature.FormalTypeParameter>();
            do {
                formalTypeParametersList.add(this.parseFormalTypeParameter());
            } while (!this.maybeEat(">"));
            classSig.formalTypeParameters = new GenericSignature.FormalTypeParameter[formalTypeParametersList.size()];
            formalTypeParametersList.toArray(classSig.formalTypeParameters);
        }
        classSig.superclassSignature = this.parseClassTypeSignature();
        ArrayList<GenericSignature.ClassTypeSignature> superIntSigs = new ArrayList<GenericSignature.ClassTypeSignature>();
        while (this.tokenIndex < this.tokenStream.length) {
            superIntSigs.add(this.parseClassTypeSignature());
        }
        classSig.superInterfaceSignatures = new GenericSignature.ClassTypeSignature[superIntSigs.size()];
        superIntSigs.toArray(classSig.superInterfaceSignatures);
        return classSig;
    }

    public GenericSignature.MethodTypeSignature parseAsMethodSignature(String sig) {
        this.inputString = sig;
        this.tokenStream = this.tokenize(sig);
        this.tokenIndex = 0;
        GenericSignature.FormalTypeParameter[] formals = new GenericSignature.FormalTypeParameter[]{};
        GenericSignature.TypeSignature returnType = null;
        if (this.maybeEat("<")) {
            ArrayList<GenericSignature.FormalTypeParameter> formalTypeParametersList = new ArrayList<GenericSignature.FormalTypeParameter>();
            do {
                formalTypeParametersList.add(this.parseFormalTypeParameter());
            } while (!this.maybeEat(">"));
            formals = new GenericSignature.FormalTypeParameter[formalTypeParametersList.size()];
            formalTypeParametersList.toArray(formals);
        }
        this.eat("(");
        ArrayList<GenericSignature.TypeSignature> paramList = new ArrayList<GenericSignature.TypeSignature>();
        while (!this.maybeEat(")")) {
            GenericSignature.FieldTypeSignature fsig = this.parseFieldTypeSignature(true);
            if (fsig != null) {
                paramList.add(fsig);
                continue;
            }
            paramList.add(new GenericSignature.BaseTypeSignature(this.eatIdentifier()));
        }
        GenericSignature.TypeSignature[] params = new GenericSignature.TypeSignature[paramList.size()];
        paramList.toArray(params);
        returnType = this.parseFieldTypeSignature(true);
        if (returnType == null) {
            returnType = new GenericSignature.BaseTypeSignature(this.eatIdentifier());
        }
        ArrayList<GenericSignature.FieldTypeSignature> throwsList = new ArrayList<GenericSignature.FieldTypeSignature>();
        while (this.maybeEat("^")) {
            GenericSignature.FieldTypeSignature fsig = this.parseFieldTypeSignature(false);
            throwsList.add(fsig);
        }
        GenericSignature.FieldTypeSignature[] throwsSigs = new GenericSignature.FieldTypeSignature[throwsList.size()];
        throwsList.toArray(throwsSigs);
        return new GenericSignature.MethodTypeSignature(formals, params, returnType, throwsSigs);
    }

    public GenericSignature.FieldTypeSignature parseAsFieldSignature(String sig) {
        this.inputString = sig;
        this.tokenStream = this.tokenize(sig);
        this.tokenIndex = 0;
        return this.parseFieldTypeSignature(false);
    }

    private GenericSignature.FormalTypeParameter parseFormalTypeParameter() {
        GenericSignature.FormalTypeParameter ftp = new GenericSignature.FormalTypeParameter();
        ftp.identifier = this.eatIdentifier();
        this.eat(":");
        ftp.classBound = this.parseFieldTypeSignature(true);
        if (ftp.classBound == null) {
            ftp.classBound = new GenericSignature.ClassTypeSignature("Ljava/lang/Object;", "Ljava/lang/Object");
        }
        ArrayList<GenericSignature.FieldTypeSignature> optionalBounds = new ArrayList<GenericSignature.FieldTypeSignature>();
        while (this.maybeEat(":")) {
            optionalBounds.add(this.parseFieldTypeSignature(false));
        }
        ftp.interfaceBounds = new GenericSignature.FieldTypeSignature[optionalBounds.size()];
        optionalBounds.toArray(ftp.interfaceBounds);
        return ftp;
    }

    private GenericSignature.FieldTypeSignature parseFieldTypeSignature(boolean isOptional) {
        if (isOptional && !this.tokenStream[this.tokenIndex].startsWith("L") && !this.tokenStream[this.tokenIndex].startsWith("T") && !this.tokenStream[this.tokenIndex].startsWith("[")) {
            return null;
        }
        if (this.maybeEat("[")) {
            return this.parseArrayTypeSignature();
        }
        if (this.tokenStream[this.tokenIndex].startsWith("L")) {
            return this.parseClassTypeSignature();
        }
        if (this.tokenStream[this.tokenIndex].startsWith("T")) {
            return this.parseTypeVariableSignature();
        }
        throw new IllegalStateException("Expecting [,L, or T, but found " + this.tokenStream[this.tokenIndex] + " while unpacking " + this.inputString);
    }

    private GenericSignature.ArrayTypeSignature parseArrayTypeSignature() {
        GenericSignature.FieldTypeSignature fieldType = this.parseFieldTypeSignature(true);
        if (fieldType != null) {
            return new GenericSignature.ArrayTypeSignature(fieldType);
        }
        return new GenericSignature.ArrayTypeSignature(new GenericSignature.BaseTypeSignature(this.eatIdentifier()));
    }

    private GenericSignature.ClassTypeSignature parseClassTypeSignature() {
        GenericSignature.SimpleClassTypeSignature outerType = null;
        GenericSignature.SimpleClassTypeSignature[] nestedTypes = new GenericSignature.SimpleClassTypeSignature[]{};
        StringBuffer ret = new StringBuffer();
        String identifier = this.eatIdentifier();
        ret.append(identifier);
        while (this.maybeEat("/")) {
            ret.append("/");
            ret.append(this.eatIdentifier());
        }
        identifier = ret.toString();
        while (!this.maybeEat(";")) {
            if (this.tokenStream[this.tokenIndex].equals(".")) {
                outerType = new GenericSignature.SimpleClassTypeSignature(identifier);
                nestedTypes = this.parseNestedTypesHelper(ret);
                continue;
            }
            if (this.tokenStream[this.tokenIndex].equals("<")) {
                ret.append("<");
                GenericSignature.TypeArgument[] tArgs = this.maybeParseTypeArguments();
                for (int i = 0; i < tArgs.length; ++i) {
                    ret.append(tArgs[i].toString());
                }
                ret.append(">");
                outerType = new GenericSignature.SimpleClassTypeSignature(identifier, tArgs);
                nestedTypes = this.parseNestedTypesHelper(ret);
                continue;
            }
            throw new IllegalStateException("Expecting .,<, or ;, but found " + this.tokenStream[this.tokenIndex] + " while unpacking " + this.inputString);
        }
        ret.append(";");
        if (outerType == null) {
            outerType = new GenericSignature.SimpleClassTypeSignature(ret.toString());
        }
        return new GenericSignature.ClassTypeSignature(ret.toString(), outerType, nestedTypes);
    }

    private GenericSignature.SimpleClassTypeSignature[] parseNestedTypesHelper(StringBuffer ret) {
        boolean brokenSignature = false;
        ArrayList<GenericSignature.SimpleClassTypeSignature> nestedTypeList = new ArrayList<GenericSignature.SimpleClassTypeSignature>();
        while (this.maybeEat(".")) {
            ret.append(".");
            GenericSignature.SimpleClassTypeSignature sig = this.parseSimpleClassTypeSignature();
            if (this.tokenStream[this.tokenIndex].equals("/")) {
                if (!brokenSignature) {
                    System.err.println("[See bug 406167] Bad class file signature encountered, nested types appear package qualified, ignoring those incorrect pieces. Signature: " + this.inputString);
                }
                brokenSignature = true;
                ++this.tokenIndex;
                while (this.tokenStream[this.tokenIndex + 1].equals("/")) {
                    this.tokenIndex += 2;
                }
                sig = this.parseSimpleClassTypeSignature();
            }
            ret.append(sig.toString());
            nestedTypeList.add(sig);
        }
        GenericSignature.SimpleClassTypeSignature[] nestedTypes = new GenericSignature.SimpleClassTypeSignature[nestedTypeList.size()];
        nestedTypeList.toArray(nestedTypes);
        return nestedTypes;
    }

    private GenericSignature.SimpleClassTypeSignature parseSimpleClassTypeSignature() {
        String identifier = this.eatIdentifier();
        GenericSignature.TypeArgument[] tArgs = this.maybeParseTypeArguments();
        if (tArgs != null) {
            return new GenericSignature.SimpleClassTypeSignature(identifier, tArgs);
        }
        return new GenericSignature.SimpleClassTypeSignature(identifier);
    }

    private GenericSignature.TypeArgument parseTypeArgument() {
        boolean isPlus = false;
        boolean isMinus = false;
        if (this.maybeEat("*")) {
            return new GenericSignature.TypeArgument();
        }
        if (this.maybeEat("+")) {
            isPlus = true;
        } else if (this.maybeEat("-")) {
            isMinus = true;
        }
        GenericSignature.FieldTypeSignature sig = this.parseFieldTypeSignature(false);
        return new GenericSignature.TypeArgument(isPlus, isMinus, sig);
    }

    private GenericSignature.TypeArgument[] maybeParseTypeArguments() {
        if (this.maybeEat("<")) {
            ArrayList<GenericSignature.TypeArgument> typeArgs = new ArrayList<GenericSignature.TypeArgument>();
            do {
                GenericSignature.TypeArgument arg = this.parseTypeArgument();
                typeArgs.add(arg);
            } while (!this.maybeEat(">"));
            GenericSignature.TypeArgument[] tArgs = new GenericSignature.TypeArgument[typeArgs.size()];
            typeArgs.toArray(tArgs);
            return tArgs;
        }
        return null;
    }

    private GenericSignature.TypeVariableSignature parseTypeVariableSignature() {
        GenericSignature.TypeVariableSignature tv = new GenericSignature.TypeVariableSignature(this.eatIdentifier());
        this.eat(";");
        return tv;
    }

    private boolean maybeEat(String token) {
        if (this.tokenStream.length <= this.tokenIndex) {
            return false;
        }
        if (this.tokenStream[this.tokenIndex].equals(token)) {
            ++this.tokenIndex;
            return true;
        }
        return false;
    }

    private void eat(String token) {
        if (!this.tokenStream[this.tokenIndex].equals(token)) {
            throw new IllegalStateException("Expecting " + token + " but found " + this.tokenStream[this.tokenIndex] + " while unpacking " + this.inputString);
        }
        ++this.tokenIndex;
    }

    private String eatIdentifier() {
        return this.tokenStream[this.tokenIndex++];
    }

    public String[] tokenize(String signatureString) {
        char[] chars = signatureString.toCharArray();
        int index = 0;
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuffer identifier = new StringBuffer();
        boolean inParens = false;
        boolean inArray = false;
        boolean couldSeePrimitive = false;
        do {
            switch (chars[index]) {
                case '<': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    tokens.add("<");
                    break;
                }
                case '>': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    tokens.add(">");
                    break;
                }
                case ':': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    tokens.add(":");
                    break;
                }
                case '/': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    tokens.add("/");
                    couldSeePrimitive = false;
                    break;
                }
                case ';': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    tokens.add(";");
                    couldSeePrimitive = true;
                    inArray = false;
                    break;
                }
                case '^': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    tokens.add("^");
                    break;
                }
                case '+': {
                    tokens.add("+");
                    break;
                }
                case '-': {
                    tokens.add("-");
                    break;
                }
                case '*': {
                    tokens.add("*");
                    break;
                }
                case '.': {
                    if (identifier.length() > 0) {
                        tokens.add(identifier.toString());
                    }
                    identifier = new StringBuffer();
                    couldSeePrimitive = false;
                    tokens.add(".");
                    break;
                }
                case '(': {
                    tokens.add("(");
                    inParens = true;
                    couldSeePrimitive = true;
                    break;
                }
                case ')': {
                    tokens.add(")");
                    inParens = false;
                    break;
                }
                case '[': {
                    tokens.add("[");
                    couldSeePrimitive = true;
                    inArray = true;
                    break;
                }
                case 'B': 
                case 'C': 
                case 'D': 
                case 'F': 
                case 'I': 
                case 'J': 
                case 'S': 
                case 'V': 
                case 'Z': {
                    if ((inParens || inArray) && couldSeePrimitive && identifier.length() == 0) {
                        tokens.add(new String("" + chars[index]));
                    } else {
                        identifier.append(chars[index]);
                    }
                    inArray = false;
                    break;
                }
                case 'L': {
                    couldSeePrimitive = false;
                }
                default: {
                    identifier.append(chars[index]);
                }
            }
        } while (++index < chars.length);
        if (identifier.length() > 0) {
            tokens.add(identifier.toString());
        }
        String[] tokenArray = new String[tokens.size()];
        tokens.toArray(tokenArray);
        return tokenArray;
    }
}

