package com.example.demo;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.Test;

import java.io.*;

/**
 * Created by Chen on 2019/3/4.
 */
public class PinyinTest {

    @Test
    public void testPinyin() throws BadHanyuPinyinOutputFormatCombination {
        String name = "序号";
        char[] charArray = name.toCharArray();
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        //设置大小写格式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //设置声调格式：
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < charArray.length; i++) {
            //匹配中文,非中文转换会转换成null
            if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i], defaultFormat);
                String string = hanyuPinyinStringArray[0];
                pinyin.append(string);
            } else {
                pinyin.append(charArray[i]);
            }
        }
        System.err.println(pinyin);
    }


    //获取中文的首字母
    @Test
    public void testPinyinFirst() throws BadHanyuPinyinOutputFormatCombination {
        String name = "序号";
        char[] charArray = name.toCharArray();
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 设置大小写格式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置声调格式：
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < charArray.length; i++) {
            //匹配中文,非中文转换会转换成null
            if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i], defaultFormat);
                if (hanyuPinyinStringArray != null) {
                    pinyin.append(hanyuPinyinStringArray[0].charAt(0));
                }
            }
        }
        System.err.println(pinyin);
    }

    @Test
    public void test() {
        String str = "包装材质(1:金属,2:塑料)";
        int a = str.indexOf("(");
        int b = str.indexOf(")");
        String str1 = str.substring(0, str.indexOf("("));
        String str2 = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        System.out.print(str2);
    }

    @Test
    public void testFile() {
        File myFilePath = new File("G://src/api/baseinfo/bz.js");
        try {
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            myFile.println();
            resultFile.close();
        } catch (Exception e) {
            System.out.println("新建文件操作出错");
            e.printStackTrace();
        }
    }

    @Test
    public void testAppendFile() {
        String str =
                "\n" +
                "//容器\n" +
                "Mock.mock(/\\/baseinfo\\/rqController\\/rqList/, 'get', loginAPI.rqList)\n" +
                "Mock.mock(/\\/baseinfo\\/rqController\\/rqDetail/, 'get', loginAPI.rqDetail)   \n" +
                "Mock.mock(/\\/baseinfo\\/rqController\\/rqEdit/, 'post', loginAPI.rqEdit)\n" +
                "Mock.mock(/\\/baseinfo\\/rqController\\/rqAdd/, 'post', loginAPI.rqAdd)\n" +
                "Mock.mock(/\\/baseinfo\\/rqController\\/getRoleList/, 'get', loginAPI.getRoleList)  ";
        BufferedWriter out = null;
        try {
            File file = new File("G://src/mock/index.js");
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"utf-8"));
            out.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
