/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class UnlikelyArgumentCheck {
    public final TypeConstants.DangerousMethod dangerousMethod;
    public final TypeBinding typeToCheck;
    public final TypeBinding expectedType;
    public final TypeBinding typeToReport;

    private UnlikelyArgumentCheck(TypeConstants.DangerousMethod dangerousMethod, TypeBinding typeToCheck, TypeBinding expectedType, TypeBinding typeToReport) {
        this.dangerousMethod = dangerousMethod;
        this.typeToCheck = typeToCheck;
        this.expectedType = expectedType;
        this.typeToReport = typeToReport;
    }

    public boolean isDangerous(BlockScope currentScope) {
        TypeBinding expectedType2;
        TypeBinding typeToCheck2 = this.typeToCheck;
        if (typeToCheck2.isBaseType()) {
            typeToCheck2 = currentScope.boxing(typeToCheck2);
        }
        if ((expectedType2 = this.expectedType).isBaseType()) {
            expectedType2 = currentScope.boxing(expectedType2);
        }
        if (this.dangerousMethod != TypeConstants.DangerousMethod.Equals && currentScope.compilerOptions().reportUnlikelyCollectionMethodArgumentTypeStrict) {
            return !typeToCheck2.isCompatibleWith(expectedType2, currentScope);
        }
        if (typeToCheck2.isCapture() || !typeToCheck2.isTypeVariable() || expectedType2.isCapture() || !expectedType2.isTypeVariable()) {
            typeToCheck2 = typeToCheck2.erasure();
            expectedType2 = expectedType2.erasure();
        }
        return !typeToCheck2.isCompatibleWith(expectedType2, currentScope) && !expectedType2.isCompatibleWith(typeToCheck2, currentScope);
    }

    public static UnlikelyArgumentCheck determineCheckForNonStaticSingleArgumentMethod(TypeBinding argumentType, Scope scope, char[] selector, TypeBinding actualReceiverType, TypeBinding[] parameters) {
        TypeConstants.DangerousMethod suspect;
        int paramTypeId;
        block21: {
            block23: {
                ReferenceBinding collectionType;
                block22: {
                    if (parameters.length != 1) {
                        return null;
                    }
                    paramTypeId = parameters[0].original().id;
                    if (paramTypeId != 1 && paramTypeId != 59) {
                        return null;
                    }
                    suspect = TypeConstants.DangerousMethod.detectSelector(selector);
                    if (suspect == null) {
                        return null;
                    }
                    if (actualReceiverType.hasTypeBit(256) && paramTypeId == 1) {
                        switch (suspect) {
                            case Remove: 
                            case Get: 
                            case ContainsKey: {
                                ReferenceBinding mapType = actualReceiverType.findSuperTypeOriginatingFrom(91, false);
                                if (mapType == null || !mapType.isParameterizedType()) break;
                                return new UnlikelyArgumentCheck(suspect, argumentType, ((ParameterizedTypeBinding)mapType).typeArguments()[0], mapType);
                            }
                            case ContainsValue: {
                                ReferenceBinding mapType = actualReceiverType.findSuperTypeOriginatingFrom(91, false);
                                if (mapType == null || !mapType.isParameterizedType()) break;
                                return new UnlikelyArgumentCheck(suspect, argumentType, ((ParameterizedTypeBinding)mapType).typeArguments()[1], mapType);
                            }
                        }
                    }
                    if (!actualReceiverType.hasTypeBit(512)) break block21;
                    if (paramTypeId != 1) break block22;
                    switch (suspect) {
                        case Contains: 
                        case Remove: {
                            collectionType = actualReceiverType.findSuperTypeOriginatingFrom(59, false);
                            if (collectionType != null && collectionType.isParameterizedType()) {
                                return new UnlikelyArgumentCheck(suspect, argumentType, ((ParameterizedTypeBinding)collectionType).typeArguments()[0], collectionType);
                            } else {
                                break;
                            }
                        }
                    }
                    break block23;
                }
                if (paramTypeId == 59) {
                    switch (suspect) {
                        case RemoveAll: 
                        case ContainsAll: 
                        case RetainAll: {
                            collectionType = actualReceiverType.findSuperTypeOriginatingFrom(59, false);
                            ReferenceBinding argumentCollectionType = argumentType.findSuperTypeOriginatingFrom(59, false);
                            if (collectionType == null || argumentCollectionType == null || !argumentCollectionType.isParameterizedTypeWithActualArguments() || !collectionType.isParameterizedTypeWithActualArguments()) break;
                            return new UnlikelyArgumentCheck(suspect, ((ParameterizedTypeBinding)argumentCollectionType).typeArguments()[0], ((ParameterizedTypeBinding)collectionType).typeArguments()[0], collectionType);
                        }
                    }
                }
            }
            if (actualReceiverType.hasTypeBit(1024) && paramTypeId == 1) {
                switch (suspect) {
                    case IndexOf: 
                    case LastIndexOf: {
                        ReferenceBinding listType = actualReceiverType.findSuperTypeOriginatingFrom(92, false);
                        if (listType == null || !listType.isParameterizedType()) break;
                        return new UnlikelyArgumentCheck(suspect, argumentType, ((ParameterizedTypeBinding)listType).typeArguments()[0], listType);
                    }
                }
            }
        }
        if (paramTypeId == 1 && suspect == TypeConstants.DangerousMethod.Equals) {
            return new UnlikelyArgumentCheck(suspect, argumentType, actualReceiverType, actualReceiverType);
        }
        return null;
    }

    public static UnlikelyArgumentCheck determineCheckForStaticTwoArgumentMethod(TypeBinding secondParameter, Scope scope, char[] selector, TypeBinding firstParameter, TypeBinding[] parameters, TypeBinding actualReceiverType) {
        if (parameters.length != 2) {
            return null;
        }
        int paramTypeId1 = parameters[0].original().id;
        int paramTypeId2 = parameters[1].original().id;
        if (paramTypeId1 != 1 || paramTypeId2 != 1) {
            return null;
        }
        TypeConstants.DangerousMethod suspect = TypeConstants.DangerousMethod.detectSelector(selector);
        if (suspect == null) {
            return null;
        }
        if (actualReceiverType.id == 74 && suspect == TypeConstants.DangerousMethod.Equals) {
            return new UnlikelyArgumentCheck(suspect, secondParameter, firstParameter, firstParameter);
        }
        return null;
    }
}

