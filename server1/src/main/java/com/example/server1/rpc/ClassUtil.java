package com.example.server1.rpc;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class ClassUtil
{

    public static Class[] getArgTypes(String argTypes) throws ClassNotFoundException {
        List<String> argTypeList = JSON.parseArray(argTypes, String.class);
        List<Class> argClassList = new ArrayList<>();
        for (String argType : argTypeList) {
            Class argTypeClass = getArgTypeClass(argType);
            argClassList.add(argTypeClass);
        }
        Class[] argClassArray = new Class[argClassList.size()];
        return argClassList.toArray(argClassArray);
    }

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


    public static Object[] getArgObjects(String argValues, Class[] argClassArray) {
        List<String> argValueStringList = JSON.parseArray(argValues, String.class);

        List<Object> argValueList = new ArrayList<>();
        for (int i = 0; i < argClassArray.length; i++) {
            if (argClassArray[i].equals(String.class)) {
                argValueList.add(argValueStringList.get(i));
            } else {
                argValueList.add(JSON.parseObject(argValueStringList.get(i), argClassArray[i]));
            }
        }
        Object[] args = new Object[argValueList.size()];
        return argValueList.toArray(args);
    }
}
