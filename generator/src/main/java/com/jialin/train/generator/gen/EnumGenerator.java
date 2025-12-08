package com.jialin.train.generator.gen;


import cn.hutool.core.util.StrUtil;
import com.jialin.train.business.enums.SeatTypeEnum;
import com.jialin.train.business.enums.TrainTypeEnum;
import com.jialin.train.member.enums.PassengerTypeEnum;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EnumGenerator {
     static String path = "admin/src/assets/js/enums.js";

    public static void main(String[] args) {
        StringBuffer bufferObject = new StringBuffer();
        StringBuffer bufferArray = new StringBuffer();
        long begin = System.currentTimeMillis();
        try {
            toJson(PassengerTypeEnum.class, bufferObject, bufferArray);
            toJson(TrainTypeEnum.class, bufferObject, bufferArray);
            toJson(SeatTypeEnum.class, bufferObject, bufferArray);

            StringBuffer buffer = bufferObject.append("\r\n").append(bufferArray);
            writeJs(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time taken:" + (end - begin) + " ms");
    }

    private static void toJson(Class clazz, StringBuffer bufferObject, StringBuffer bufferArray) throws Exception {
        String enumConst = StrUtil.toUnderlineCase(clazz.getSimpleName())
                .toUpperCase().replace("_ENUM", "");
        Object[] objects = clazz.getEnumConstants();
        Method name = clazz.getMethod("name");

        List<Field> targetFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isPrivate(field.getModifiers()) || "$VALUES".equals(field.getName())) {
                continue;
            }
            targetFields.add(field);
        }

        bufferObject.append(enumConst).append("={");
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            bufferObject.append(name.invoke(obj)).append(":");

            formatJsonObj(bufferObject, targetFields, clazz, obj);

            if (i < objects.length - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("};\r\n");

        bufferArray.append(enumConst).append("_ARRAY=[");
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];

            formatJsonObj(bufferArray, targetFields, clazz, obj);

            if (i < objects.length - 1) {
                bufferArray.append(",");
            }
        }
        bufferArray.append("];\r\n");
    }

    /**
     *
     * @param bufferObject
     * @param targetFields
     * @param clazz
     * @param obj
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private static void formatJsonObj(StringBuffer bufferObject, List<Field> targetFields, Class clazz, Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        bufferObject.append("{");
        for (int j = 0; j < targetFields.size(); j++) {
            Field field = targetFields.get(j);
            String fieldName = field.getName();
            String getMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            bufferObject.append(fieldName).append(":\"").append(clazz.getMethod(getMethod).invoke(obj)).append("\"");
            if (j < targetFields.size() - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("}");
    }

    /**
     *
     * @param stringBuffer
     */
    public static void writeJs(StringBuffer stringBuffer) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            System.out.println(path);
            osw.write(stringBuffer.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
