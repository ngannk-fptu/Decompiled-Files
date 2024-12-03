/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.RecursiveFieldNamingPolicy;
import com.google.gson.internal.$Gson$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ModifyFirstLetterNamingPolicy
extends RecursiveFieldNamingPolicy {
    private final LetterModifier letterModifier;

    ModifyFirstLetterNamingPolicy(LetterModifier modifier) {
        this.letterModifier = $Gson$Preconditions.checkNotNull(modifier);
    }

    @Override
    protected String translateName(String target, Type fieldType, Collection<Annotation> annotations) {
        boolean capitalizeFirstLetter;
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = target.charAt(index);
        while (index < target.length() - 1 && !Character.isLetter(firstCharacter)) {
            fieldNameBuilder.append(firstCharacter);
            firstCharacter = target.charAt(++index);
        }
        if (index == target.length()) {
            return fieldNameBuilder.toString();
        }
        boolean bl = capitalizeFirstLetter = this.letterModifier == LetterModifier.UPPER;
        if (capitalizeFirstLetter && !Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = this.modifyString(Character.toUpperCase(firstCharacter), target, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        }
        if (!capitalizeFirstLetter && Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = this.modifyString(Character.toLowerCase(firstCharacter), target, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        }
        return target;
    }

    private String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return indexOfSubstring < srcString.length() ? firstCharacter + srcString.substring(indexOfSubstring) : String.valueOf(firstCharacter);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum LetterModifier {
        UPPER,
        LOWER;

    }
}

