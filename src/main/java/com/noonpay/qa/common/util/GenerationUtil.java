package com.noonpay.qa.common.util;

import java.util.Random;

public class GenerationUtil {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    private static String generateBase(int keySize) {
        String base = "";
        for (int i = 0; i < keySize; i++) {
            base += String.valueOf(RANDOM.nextInt(9));
        }
        return base;
    }

    public static String generateWord(int keySize) {
        StringBuilder result = new StringBuilder();
        String base = generateBase(keySize);
        int position = RANDOM.nextInt(ALPHABET.length() - 1);
        int sign = -1;
        for (int i = 0; i < keySize; i++) {

            int step = Integer.valueOf(base.substring(i, i + 1)) * sign;
            if (position + step > 0 && position + step < ALPHABET.length() - 1) {
                position += step;
            } else {
                position -= step;
            }
            result.append(ALPHABET.charAt(position));
            sign *= -1;
        }
        return result.toString();
    }

}
