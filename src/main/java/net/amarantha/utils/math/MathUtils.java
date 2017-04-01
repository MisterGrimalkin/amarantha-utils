package net.amarantha.utils.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class MathUtils {

    public static int round(double value) {
        return (int)Math.round(value);
    }

    public static int bound(int min, int max, int value) {
        return Math.round(Math.max(min, Math.min(max, value)));
    }

    public static <T> T applyBiFunction(BiFunction<T, T, T> function, T... values) {
        T result = null;
        for (T value : values) {
            result = result==null ? value : function.apply(result, value);
        }
        return result;
    }

    public static Double max(Double... values) {
        return applyBiFunction(Math::max, values);
    }

    public static Integer max(Integer... values) {
        return applyBiFunction(Math::max, values);
    }

    public static Double min(Double... values) {
        return applyBiFunction(Math::min, values);
    }

    public static Integer min(Integer... values) {
        return applyBiFunction(Math::min, values);
    }

    public static int randomBetween(int min, int max) {
        return (int)Math.round(Math.random()*(max-min)) + min;
    }

    public static double randomBetween(double min, double max) {
        return Math.random()*(max-min) + min;
    }

    public static int randomFlip(int number) {
        return Math.random() >= 0.5 ? number : -number;
    }

    public static double randomFlip(double number) {
        return Math.random() >= 0.5 ? number : -number;
    }

    public static <T> T randomFrom(Collection<T> items) {
        List<T> list = new ArrayList<>(items);
        return list.get(randomBetween(0, list.size()-1));
    }

}
