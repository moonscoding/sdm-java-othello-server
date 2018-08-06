package test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Tester {

    public static void main(String[] args) {

        System.out.println(Arrays.toString(new byte[]{50, 50}));
        System.out.println(new String(new char[]{50, 50}));
        try {
            System.out.println(new String(new byte[]{50, 50}, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(new String(new byte[]{50, 50}).getBytes()));
    }
}
