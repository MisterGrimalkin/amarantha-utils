package net.amarantha.utils.reflection;

import net.amarantha.utils.colour.RGB;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T reflectiveGet(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public static boolean reflectiveSet(Object object, Field field, String value) {
        return reflectiveSet(object, field, value, null);

    }

    public static boolean reflectiveSet(Object object, Field field, String value, BiFunction<Class<?>, String, Object> customType) {
        try {
            Class<?> type = field.getType();
            field.setAccessible(true);
            if (type == String.class) {
                field.set(object, value);
            } else if ( type == int.class || type == Integer.class ) {
                field.set(object, Integer.parseInt(value));
            } else if ( type == double.class || type == Double.class ) {
                field.set(object, Double.parseDouble(value));
            } else if ( type == boolean.class || type == Boolean.class ) {
                field.set(object, Boolean.parseBoolean(value));
            } else if ( type == RGB.class ) {
                field.set(object, RGB.parse(value));
            } else if ( type == Class.class ) {
                field.set(object, getClass(value));
            } else if ( customType!=null ) {
                field.set(object, customType.apply(type, value));
            }
            return true;
        } catch (IllegalAccessException | NumberFormatException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String className) {
        try {
            return (Class<T>)Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <A extends Annotation> void iterateAnnotatedFields(Object object, Class<A> annotationClass, BiConsumer<Field, A> consumer) {
        Class<?> clazz = object.getClass();
        while ( clazz!=null ) {
            iterateFields(clazz.getDeclaredFields(), annotationClass, consumer);
            clazz = clazz.getSuperclass();
        }
    }

    private static <A extends Annotation> void iterateFields(Field[] fields, Class<A> annotationClass, BiConsumer<Field, A> consumer) {
        for (Field field : fields) {
            A annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                consumer.accept(field, annotation);
            }
        }
    }

}
