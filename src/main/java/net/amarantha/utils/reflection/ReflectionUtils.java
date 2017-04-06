package net.amarantha.utils.reflection;

import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.midi.entity.MidiCommand;
import net.amarantha.utils.osc.entity.OscCommand;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T reflectiveGet(Object object, String fieldName) {
        final Object[] value = {null};
        iterateAnnotatedFields(object, null, (field, a) -> {
            if ( field.getName().equals(fieldName) ) {
                try {
                    value[0] = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        return (T) value[0];
    }

    public static boolean reflectiveSet(Object object, Field field, String value) {
        return reflectiveSet(object, field, value, null);

    }

    public static boolean reflectiveSet(Object object, Field field, String value, BiFunction<Class<?>, String, Object> customType) {
        try {
            Class<?> type = field.getType();
            field.setAccessible(true);
            if (type == String.class || value == null) {
                field.set(object, value);
            } else if (type == int.class || type == Integer.class) {
                field.set(object, Integer.parseInt(value));
            } else if (type == long.class || type == Long.class) {
                field.set(object, Long.parseLong(value));
            } else if (type == double.class || type == Double.class) {
                field.set(object, Double.parseDouble(value));
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(object, Boolean.parseBoolean(value));
            } else if (type == RGB.class) {
                field.set(object, RGB.parse(value));
            } else if (type == Class.class) {
                field.set(object, getClass(value));
            } else if ( type == HttpCommand.class ) {
                field.set(object, HttpCommand.fromString(value));
            } else if ( type == MidiCommand.class ) {
                field.set(object, MidiCommand.fromString(value));
            } else if ( type == OscCommand.class ) {
                field.set(object, OscCommand.fromString(value));
            } else if (customType != null) {
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
            field.setAccessible(true);
            if ( annotationClass==null ) {
                consumer.accept(field, null);
            } else {
                A annotation = field.getAnnotation(annotationClass);
                if (annotation != null) {
                    consumer.accept(field, annotation);
                }
            }
        }
    }

}
