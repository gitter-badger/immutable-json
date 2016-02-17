package net.hamnaberg.json;

import javaslang.Tuple2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionIso {
    public <A, B, C> Iso<A, Tuple2<B, C>> fromFields2(Class<A> type, String field1, String field2) {
        try {
            final Field declaredField1 = getDeclaredAccessibleField(type, field1);
            final Field declaredField2 = getDeclaredAccessibleField(type, field2);
            final Constructor<?> ctor = type.getConstructor(declaredField1.getType(), declaredField2.getType());
            return new Iso<A, Tuple2<B, C>>() {
                @Override
                public A reverseGet(Tuple2<B, C> t) {
                    return newInstance(ctor, t._1, t._2);
                }

                @Override
                public Tuple2<B, C> get(A a) {
                    B b = getValue(declaredField1, a);
                    C c = getValue(declaredField2, a);
                    return new Tuple2<>(b, c);
                }
            };
        } catch (Exception e) {
            throw toRuntime(e);
        }
    }

    private RuntimeException toRuntime(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException)e;
        }
        return new RuntimeException(e);
    }

    private Field getDeclaredAccessibleField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @SuppressWarnings("unchecked")
    private <A> A getValue(Field field, Object from) {
        try {
            return (A)field.get(from);
        } catch (IllegalAccessException e) {
            throw toRuntime(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <A> A newInstance(Constructor<?> ctor, Object... args) {
        try {
            return (A)ctor.newInstance(args);
        } catch (Exception e) {
            throw toRuntime(e);
        }
    }
}
