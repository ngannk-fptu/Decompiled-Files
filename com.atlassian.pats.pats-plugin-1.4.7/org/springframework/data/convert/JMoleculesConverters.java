/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jmolecules.spring.AssociationToPrimitivesConverter
 *  org.jmolecules.spring.IdentifierToPrimitivesConverter
 *  org.jmolecules.spring.PrimitivesToAssociationConverter
 *  org.jmolecules.spring.PrimitivesToIdentifierConverter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import org.jmolecules.spring.AssociationToPrimitivesConverter;
import org.jmolecules.spring.IdentifierToPrimitivesConverter;
import org.jmolecules.spring.PrimitivesToAssociationConverter;
import org.jmolecules.spring.PrimitivesToIdentifierConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ClassUtils;

public class JMoleculesConverters {
    private static final boolean JMOLECULES_PRESENT = ClassUtils.isPresent((String)"org.jmolecules.spring.IdentifierToPrimitivesConverter", (ClassLoader)JMoleculesConverters.class.getClassLoader());

    public static Collection<Object> getConvertersToRegister() {
        if (!JMOLECULES_PRESENT) {
            return Collections.emptyList();
        }
        ArrayList<Object> converters = new ArrayList<Object>();
        Supplier<ConversionService> conversionService = () -> DefaultConversionService.getSharedInstance();
        IdentifierToPrimitivesConverter toPrimitives = new IdentifierToPrimitivesConverter(conversionService);
        PrimitivesToIdentifierConverter toIdentifier = new PrimitivesToIdentifierConverter(conversionService);
        converters.add(toPrimitives);
        converters.add(toIdentifier);
        converters.add(new AssociationToPrimitivesConverter(toPrimitives));
        converters.add(new PrimitivesToAssociationConverter(toIdentifier));
        return converters;
    }
}

