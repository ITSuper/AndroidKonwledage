package com.ccsuper.androidkonwledage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @Author Chen
 * @Date 2022/10/28-16:21
 * 类描述：
 */
public class Test {
    public static void main(String[] args) {
        String s = findStr("aaaccrssdead");
        System.out.print("--------" + s);
    }

    private static String findStr(String str) {
        char[] chars = str.toCharArray();
        List<String> repeat = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            int repeatTime = 0;
            for (int j = 0; j < chars.length; j++) {
                if (c == chars[j]) {
                    repeatTime++;
                }
            }
            if (repeatTime > 1) {
                repeat.add(String.valueOf(c));
            }
        }
        String noRepeat = str;
        for (String s : repeat) {
            noRepeat = noRepeat.replace(s, "");
        }
        return noRepeat.substring(0, 1);
    }

    private static void bubbleSort(int[] nums) {
        int temp;
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 0; j < nums.length - 1 - i; j++) {
                 if(nums[j]>nums[j+1]){
                     temp = nums[j];
                     nums[j] = nums[j+1];
                     nums[j+1] = temp;
                 }
            }
        }
    }
}
