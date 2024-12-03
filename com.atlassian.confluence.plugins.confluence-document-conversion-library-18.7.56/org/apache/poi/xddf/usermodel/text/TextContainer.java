/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public interface TextContainer {
    public <R> Optional<R> findDefinedParagraphProperty(Predicate<CTTextParagraphProperties> var1, Function<CTTextParagraphProperties, R> var2);

    public <R> Optional<R> findDefinedRunProperty(Predicate<CTTextCharacterProperties> var1, Function<CTTextCharacterProperties, R> var2);
}

