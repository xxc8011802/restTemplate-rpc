package com.example.client.http;
public class ClassUtil
{
    public static Class getArgTypeClass(String typeStr) throws ClassNotFoundException {
        switch (typeStr) {
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            default:
                return Class.forName(typeStr);
        }
    }

}
