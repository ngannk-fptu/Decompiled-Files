/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.units;

import com.ibm.icu.impl.IllegalIcuArgumentException;
import com.ibm.icu.impl.units.ConversionRates;
import com.ibm.icu.impl.units.MeasureUnitImpl;
import com.ibm.icu.impl.units.SingleUnitImpl;
import com.ibm.icu.util.MeasureUnit;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class UnitsConverter {
    private BigDecimal conversionRate;
    private boolean reciprocal;
    private BigDecimal offset;

    public UnitsConverter(String sourceIdentifier, String targetIdentifier) {
        this(MeasureUnitImpl.forIdentifier(sourceIdentifier), MeasureUnitImpl.forIdentifier(targetIdentifier), new ConversionRates());
    }

    public UnitsConverter(MeasureUnitImpl source, MeasureUnitImpl target, ConversionRates conversionRates) {
        Convertibility convertibility = UnitsConverter.extractConvertibility(source, target, conversionRates);
        if (convertibility != Convertibility.CONVERTIBLE && convertibility != Convertibility.RECIPROCAL) {
            throw new IllegalIcuArgumentException("input units must be convertible or reciprocal");
        }
        Factor sourceToBase = conversionRates.getFactorToBase(source);
        Factor targetToBase = conversionRates.getFactorToBase(target);
        if (convertibility == Convertibility.CONVERTIBLE) {
            this.conversionRate = sourceToBase.divide(targetToBase).getConversionRate();
        } else {
            assert (convertibility == Convertibility.RECIPROCAL);
            this.conversionRate = sourceToBase.multiply(targetToBase).getConversionRate();
        }
        this.reciprocal = convertibility == Convertibility.RECIPROCAL;
        this.offset = conversionRates.getOffset(source, target, sourceToBase, targetToBase, convertibility);
        assert (convertibility != Convertibility.RECIPROCAL || this.offset == BigDecimal.ZERO);
    }

    public static Convertibility extractConvertibility(MeasureUnitImpl source, MeasureUnitImpl target, ConversionRates conversionRates) {
        ArrayList<SingleUnitImpl> sourceSingleUnits = conversionRates.extractBaseUnits(source);
        ArrayList<SingleUnitImpl> targetSingleUnits = conversionRates.extractBaseUnits(target);
        HashMap<String, Integer> dimensionMap = new HashMap<String, Integer>();
        UnitsConverter.insertInMap(dimensionMap, sourceSingleUnits, 1);
        UnitsConverter.insertInMap(dimensionMap, targetSingleUnits, -1);
        if (UnitsConverter.areDimensionsZeroes(dimensionMap)) {
            return Convertibility.CONVERTIBLE;
        }
        UnitsConverter.insertInMap(dimensionMap, targetSingleUnits, 2);
        if (UnitsConverter.areDimensionsZeroes(dimensionMap)) {
            return Convertibility.RECIPROCAL;
        }
        return Convertibility.UNCONVERTIBLE;
    }

    private static void insertInMap(HashMap<String, Integer> dimensionMap, ArrayList<SingleUnitImpl> singleUnits, int multiplier) {
        for (SingleUnitImpl singleUnit : singleUnits) {
            if (dimensionMap.containsKey(singleUnit.getSimpleUnitID())) {
                dimensionMap.put(singleUnit.getSimpleUnitID(), dimensionMap.get(singleUnit.getSimpleUnitID()) + singleUnit.getDimensionality() * multiplier);
                continue;
            }
            dimensionMap.put(singleUnit.getSimpleUnitID(), singleUnit.getDimensionality() * multiplier);
        }
    }

    private static boolean areDimensionsZeroes(HashMap<String, Integer> dimensionMap) {
        for (Integer value : dimensionMap.values()) {
            if (value.equals(0)) continue;
            return false;
        }
        return true;
    }

    public BigDecimal convert(BigDecimal inputValue) {
        BigDecimal result = inputValue.multiply(this.conversionRate).add(this.offset);
        if (this.reciprocal) {
            assert (this.offset == BigDecimal.ZERO);
            if (result.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            result = BigDecimal.ONE.divide(result, MathContext.DECIMAL128);
        }
        return result;
    }

    public BigDecimal convertInverse(BigDecimal inputValue) {
        BigDecimal result = inputValue;
        if (this.reciprocal) {
            assert (this.offset == BigDecimal.ZERO);
            if (result.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            result = BigDecimal.ONE.divide(result, MathContext.DECIMAL128);
        }
        result = result.subtract(this.offset).divide(this.conversionRate, MathContext.DECIMAL128);
        return result;
    }

    public ConversionInfo getConversionInfo() {
        ConversionInfo result = new ConversionInfo();
        result.conversionRate = this.conversionRate;
        result.offset = this.offset;
        result.reciprocal = this.reciprocal;
        return result;
    }

    public String toString() {
        return "UnitsConverter [conversionRate=" + this.conversionRate + ", offset=" + this.offset + "]";
    }

    static class Factor {
        private BigDecimal factorNum = BigDecimal.valueOf(1L);
        private BigDecimal factorDen = BigDecimal.valueOf(1L);
        private int exponentFtToM = 0;
        private int exponentPi = 0;
        private int exponentGravity = 0;
        private int exponentG = 0;
        private int exponentGalImpToM3 = 0;
        private int exponentLbToKg = 0;
        private int exponentGlucoseMolarMass = 0;
        private int exponentItemPerMole = 0;
        private int exponentMetersPerAU = 0;
        private int exponentSecPerJulianYear = 0;
        private int exponentSpeedOfLightMetersPerSecond = 0;

        public static Factor processFactor(String factor) {
            assert (!factor.isEmpty());
            factor = factor.replaceAll("\\s+", "");
            String[] fractions = factor.split("/");
            assert (fractions.length == 1 || fractions.length == 2);
            if (fractions.length == 1) {
                return Factor.processFactorWithoutDivision(fractions[0]);
            }
            Factor num = Factor.processFactorWithoutDivision(fractions[0]);
            Factor den = Factor.processFactorWithoutDivision(fractions[1]);
            return num.divide(den);
        }

        private static Factor processFactorWithoutDivision(String factorWithoutDivision) {
            Factor result = new Factor();
            for (String poweredEntity : factorWithoutDivision.split(Pattern.quote("*"))) {
                result.addPoweredEntity(poweredEntity);
            }
            return result;
        }

        protected Factor copy() {
            Factor result = new Factor();
            result.factorNum = this.factorNum;
            result.factorDen = this.factorDen;
            result.exponentFtToM = this.exponentFtToM;
            result.exponentPi = this.exponentPi;
            result.exponentGravity = this.exponentGravity;
            result.exponentG = this.exponentG;
            result.exponentGalImpToM3 = this.exponentGalImpToM3;
            result.exponentLbToKg = this.exponentLbToKg;
            result.exponentGlucoseMolarMass = this.exponentGlucoseMolarMass;
            result.exponentItemPerMole = this.exponentItemPerMole;
            result.exponentMetersPerAU = this.exponentMetersPerAU;
            result.exponentSecPerJulianYear = this.exponentSecPerJulianYear;
            result.exponentSpeedOfLightMetersPerSecond = this.exponentSpeedOfLightMetersPerSecond;
            return result;
        }

        public BigDecimal getConversionRate() {
            Factor resultCollector = this.copy();
            resultCollector.multiply(new BigDecimal("0.3048"), this.exponentFtToM);
            resultCollector.multiply(new BigDecimal("411557987.0").divide(new BigDecimal("131002976.0"), MathContext.DECIMAL128), this.exponentPi);
            resultCollector.multiply(new BigDecimal("9.80665"), this.exponentGravity);
            resultCollector.multiply(new BigDecimal("6.67408E-11"), this.exponentG);
            resultCollector.multiply(new BigDecimal("0.00454609"), this.exponentGalImpToM3);
            resultCollector.multiply(new BigDecimal("0.45359237"), this.exponentLbToKg);
            resultCollector.multiply(new BigDecimal("180.1557"), this.exponentGlucoseMolarMass);
            resultCollector.multiply(new BigDecimal("6.02214076E+23"), this.exponentItemPerMole);
            resultCollector.multiply(new BigDecimal("149597870700"), this.exponentMetersPerAU);
            resultCollector.multiply(new BigDecimal("31557600"), this.exponentSecPerJulianYear);
            resultCollector.multiply(new BigDecimal("299792458"), this.exponentSpeedOfLightMetersPerSecond);
            return resultCollector.factorNum.divide(resultCollector.factorDen, MathContext.DECIMAL128);
        }

        private void multiply(BigDecimal value, int power) {
            if (power == 0) {
                return;
            }
            BigDecimal absPoweredValue = value.pow(Math.abs(power), MathContext.DECIMAL128);
            if (power > 0) {
                this.factorNum = this.factorNum.multiply(absPoweredValue);
            } else {
                this.factorDen = this.factorDen.multiply(absPoweredValue);
            }
        }

        public Factor applyPrefix(MeasureUnit.MeasurePrefix unitPrefix) {
            Factor result = this.copy();
            if (unitPrefix == MeasureUnit.MeasurePrefix.ONE) {
                return result;
            }
            int base = unitPrefix.getBase();
            int power = unitPrefix.getPower();
            BigDecimal absFactor = BigDecimal.valueOf(base).pow(Math.abs(power), MathContext.DECIMAL128);
            if (power < 0) {
                result.factorDen = this.factorDen.multiply(absFactor);
                return result;
            }
            result.factorNum = this.factorNum.multiply(absFactor);
            return result;
        }

        public Factor power(int power) {
            Factor result = new Factor();
            if (power == 0) {
                return result;
            }
            if (power > 0) {
                result.factorNum = this.factorNum.pow(power);
                result.factorDen = this.factorDen.pow(power);
            } else {
                result.factorNum = this.factorDen.pow(power * -1);
                result.factorDen = this.factorNum.pow(power * -1);
            }
            result.exponentFtToM = this.exponentFtToM * power;
            result.exponentPi = this.exponentPi * power;
            result.exponentGravity = this.exponentGravity * power;
            result.exponentG = this.exponentG * power;
            result.exponentGalImpToM3 = this.exponentGalImpToM3 * power;
            result.exponentLbToKg = this.exponentLbToKg * power;
            result.exponentGlucoseMolarMass = this.exponentGlucoseMolarMass * power;
            result.exponentItemPerMole = this.exponentItemPerMole * power;
            result.exponentMetersPerAU = this.exponentMetersPerAU * power;
            result.exponentSecPerJulianYear = this.exponentSecPerJulianYear * power;
            result.exponentSpeedOfLightMetersPerSecond = this.exponentSpeedOfLightMetersPerSecond * power;
            return result;
        }

        public Factor divide(Factor other) {
            Factor result = new Factor();
            result.factorNum = this.factorNum.multiply(other.factorDen);
            result.factorDen = this.factorDen.multiply(other.factorNum);
            result.exponentFtToM = this.exponentFtToM - other.exponentFtToM;
            result.exponentPi = this.exponentPi - other.exponentPi;
            result.exponentGravity = this.exponentGravity - other.exponentGravity;
            result.exponentG = this.exponentG - other.exponentG;
            result.exponentGalImpToM3 = this.exponentGalImpToM3 - other.exponentGalImpToM3;
            result.exponentLbToKg = this.exponentLbToKg - other.exponentLbToKg;
            result.exponentGlucoseMolarMass = this.exponentGlucoseMolarMass - other.exponentGlucoseMolarMass;
            result.exponentItemPerMole = this.exponentItemPerMole - other.exponentItemPerMole;
            result.exponentMetersPerAU = this.exponentMetersPerAU - other.exponentMetersPerAU;
            result.exponentSecPerJulianYear = this.exponentSecPerJulianYear - other.exponentSecPerJulianYear;
            result.exponentSpeedOfLightMetersPerSecond = this.exponentSpeedOfLightMetersPerSecond - other.exponentSpeedOfLightMetersPerSecond;
            return result;
        }

        public Factor multiply(Factor other) {
            Factor result = new Factor();
            result.factorNum = this.factorNum.multiply(other.factorNum);
            result.factorDen = this.factorDen.multiply(other.factorDen);
            result.exponentFtToM = this.exponentFtToM + other.exponentFtToM;
            result.exponentPi = this.exponentPi + other.exponentPi;
            result.exponentGravity = this.exponentGravity + other.exponentGravity;
            result.exponentG = this.exponentG + other.exponentG;
            result.exponentGalImpToM3 = this.exponentGalImpToM3 + other.exponentGalImpToM3;
            result.exponentLbToKg = this.exponentLbToKg + other.exponentLbToKg;
            result.exponentGlucoseMolarMass = this.exponentGlucoseMolarMass + other.exponentGlucoseMolarMass;
            result.exponentItemPerMole = this.exponentItemPerMole + other.exponentItemPerMole;
            result.exponentMetersPerAU = this.exponentMetersPerAU + other.exponentMetersPerAU;
            result.exponentSecPerJulianYear = this.exponentSecPerJulianYear + other.exponentSecPerJulianYear;
            result.exponentSpeedOfLightMetersPerSecond = this.exponentSpeedOfLightMetersPerSecond + other.exponentSpeedOfLightMetersPerSecond;
            return result;
        }

        private void addPoweredEntity(String poweredEntity) {
            String[] entities = poweredEntity.split(Pattern.quote("^"));
            assert (entities.length == 1 || entities.length == 2);
            int power = entities.length == 2 ? Integer.parseInt(entities[1]) : 1;
            this.addEntity(entities[0], power);
        }

        private void addEntity(String entity, int power) {
            if ("ft_to_m".equals(entity)) {
                this.exponentFtToM += power;
            } else if ("ft2_to_m2".equals(entity)) {
                this.exponentFtToM += 2 * power;
            } else if ("ft3_to_m3".equals(entity)) {
                this.exponentFtToM += 3 * power;
            } else if ("in3_to_m3".equals(entity)) {
                this.exponentFtToM += 3 * power;
                this.factorDen = this.factorDen.multiply(BigDecimal.valueOf(Math.pow(12.0, 3.0)));
            } else if ("gal_to_m3".equals(entity)) {
                this.factorNum = this.factorNum.multiply(BigDecimal.valueOf(231L));
                this.exponentFtToM += 3 * power;
                this.factorDen = this.factorDen.multiply(BigDecimal.valueOf(1728L));
            } else if ("gal_imp_to_m3".equals(entity)) {
                this.exponentGalImpToM3 += power;
            } else if ("G".equals(entity)) {
                this.exponentG += power;
            } else if ("gravity".equals(entity)) {
                this.exponentGravity += power;
            } else if ("lb_to_kg".equals(entity)) {
                this.exponentLbToKg += power;
            } else if ("glucose_molar_mass".equals(entity)) {
                this.exponentGlucoseMolarMass += power;
            } else if ("item_per_mole".equals(entity)) {
                this.exponentItemPerMole += power;
            } else if ("meters_per_AU".equals(entity)) {
                this.exponentMetersPerAU += power;
            } else if ("PI".equals(entity)) {
                this.exponentPi += power;
            } else if ("sec_per_julian_year".equals(entity)) {
                this.exponentSecPerJulianYear += power;
            } else if ("speed_of_light_meters_per_second".equals(entity)) {
                this.exponentSpeedOfLightMetersPerSecond += power;
            } else {
                BigDecimal decimalEntity = new BigDecimal(entity).pow(power, MathContext.DECIMAL128);
                this.factorNum = this.factorNum.multiply(decimalEntity);
            }
        }
    }

    public static class ConversionInfo {
        public BigDecimal conversionRate;
        public BigDecimal offset;
        public boolean reciprocal;
    }

    public static enum Convertibility {
        CONVERTIBLE,
        RECIPROCAL,
        UNCONVERTIBLE;

    }
}

