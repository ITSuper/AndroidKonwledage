package com.ccsuper.androidkonwledage;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.time.chrono.MinguoChronology;

/**
 * @Author Chen
 * @Date 2022/11/1-10:03
 * 类描述：
 */
public class Sort implements Parcelable {

    protected Sort(Parcel in) {
    }

    public static final Creator<Sort> CREATOR = new Creator<Sort>() {
        @Override
        public Sort createFromParcel(Parcel in) {
            return new Sort(in);
        }

        @Override
        public Sort[] newArray(int size) {
            return new Sort[size];
        }
    };

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int index = binSearch(arr, arr.length, 3);
        System.out.print("----" + arr[index] + "--------");
    }


    /**
     * 从上到下对相邻的两个数进行比较，较大的往下沉较小的往上冒
     * 空间效率 只占用一个辅助单位
     * 时间复杂度 O(n^2)
     *
     * @param array 未排序序数组
     */
    public static void bubbleSort(int[] array) {
        int temp;
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }


    /**
     * @param array [2.3.4.1]
     */
    public static void insertSort(int[] array) {
        int temp;
        for (int i = 1; i < array.length; i++) {
            temp = array[i];
            int j;
            for (j = i - 1; j >= 0; j--) {
                if (temp < array[j]) {
                    array[j + 1] = array[j];
                    array[j] = temp;
                }
            }
        }
    }


    /**
     * 选择排序
     * 从第一个开始，假设第一个为min，比较后面n-1个数，找到最小值与之替换
     */
    public static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[minIndex] > arr[j]) {
                    minIndex = j;
                }
            }
            if (arr[i] > arr[minIndex]) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
            }
        }
    }

    public static void quickSort(int[] arr, int start, int end) {
        if (start < end) {
            int div = getIndex(arr, start, end);
            quickSort(arr, 0, div - 1);
            quickSort(arr, div + 1, end);
        }
    }

    public static int getIndex(int[] arr, int low, int high) {
        int temp = arr[low];
        while (low < high) {
            while (low < high && arr[high] == temp) {
                high--;
            }
        }
        return 0;
    }

    /**
     * @param array
     * @param size
     * @param value
     * @return [1, 2, 3, 4, 5, 6, 7, 8]
     */
    public static int binSearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final int midValue = array[mid];
            if (midValue < value) {
                lo = mid + 1;
            } else if (midValue > value) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    //递归写法
    public static int binSearch(int[] arr, int start, int end, int value) {
        int mid = (start + end) >>> 1;
        int midValue = arr[mid];
        if (value == midValue) {
            return mid;
        }
        if (start >= end) {
            return -1;
        } else if (midValue > value) {
            return binSearch(arr, start, mid - 1, value);
        } else if (midValue < value) {
            return binSearch(arr, mid + 1, end, value);
        }
        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
