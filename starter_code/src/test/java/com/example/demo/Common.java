package com.example.demo;

import java.lang.reflect.Field;

public class Common {
    public static void DependencyInjection(Object target, String fieldName, Object dependency) {
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }

            field.set(target, dependency);

            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
