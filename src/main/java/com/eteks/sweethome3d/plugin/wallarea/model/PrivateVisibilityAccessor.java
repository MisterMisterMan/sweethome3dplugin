package com.eteks.sweethome3d.plugin.wallarea.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PrivateVisibilityAccessor {

    public <T> T invoke(Object object, String methodName, Object... args) {
        try {
            Method method = findMethod(object.getClass(), methodName, args);
            method.setAccessible(true);
            return (T) method.invoke(object, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Method not found: " + object.getClass().getSimpleName()
                + " -> " + methodName);
    }

    private Method findMethod(Class object, String methodName, Object... args) throws NoSuchMethodException {
        for (Method method : object.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    if (i > args.length || !method.getParameterTypes()[i].equals(args[0].getClass()))
                        break;
                }
                return method;
            }
        }
        Class superClass = object.getSuperclass();
        if (superClass != Object.class && superClass != null)
            return findMethod(superClass, methodName, args);
        throw new NoSuchMethodException();
    }

    private Class[] toClass(Object... args) {
        if (args == null) return null;
        Class[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }
        return classes;
    }

    public <T> T getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Field not found: " + object.getClass().getSimpleName()
                + " -> " + fieldName);
    }

}
