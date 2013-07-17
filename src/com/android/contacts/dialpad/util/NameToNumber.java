package com.android.contacts.dialpad.util;

import com.android.contacts.util.HanziToPinyin;

import android.text.TextUtils;

import android.util.Log;

/**
 * @author Barami
 * Default implements of normalization for alphabet search.
 * Other languages need additional work for search may inherite this and overriding convert function.
 */
public class NameToNumber {
    protected String t9Chars;
    protected String t9Digits;
    
    protected static final char UNICODE_FIRST_HAN = '\u3400';
    protected static final char UNICODE_LAST_HAN = '\u4DB5';

    // Work is based t9 characters and digits map.
    public NameToNumber(final String t9Chars, final String t9Digits) {
        this.t9Chars = t9Chars;
        this.t9Digits = t9Digits;
    }

    // Copied from https://github.com/CyanogenMod/android_packages_apps_Contacts/commit/63a531957818d631e957e8e0157d45298906e3fb
    public String convert(final String name) {
        StringBuilder sb = new StringBuilder();
    	if (TextUtils.isEmpty(name)) {
    		return sb.toString();
    	}

    	if (hasChineseCharacter(name)) {
    		sb.append(convertChinese(name));
    		sb.append(" ");
    	}

    	sb.append(convertLatin(name));
        return sb.toString();
    }
    
    private boolean hasChineseCharacter(final String name) {
    	int len = name.length();
    	for (int i = 0; i < len; ++i) {
    		if (name.charAt(i) > UNICODE_FIRST_HAN && name.charAt(i) < UNICODE_LAST_HAN) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private String convertLatin(final String name) {
        int len = name.length();
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++){
            int pos = t9Chars.indexOf(Character.toLowerCase(name.charAt(i)));
            if (pos == -1) {
                pos = 0;
            }
            sb.append(t9Digits.charAt(pos));
        }
        return sb.toString();

    }
    
    private String convertChinese(final String name) {
        final HanziToPinyin pinyin = HanziToPinyin.getInstance();
        String hzPinYin = pinyin.getFirstPinYin(name).toLowerCase();

        if (TextUtils.isEmpty(hzPinYin)) {
            return convertLatin(name);
        }

        String result = convertLatin(hzPinYin);

        //Append the full ping yin at the end of the first ping yin
        hzPinYin = pinyin.getFullPinYin(name).toLowerCase();
        if (!TextUtils.isEmpty(hzPinYin)) {
            result += " " + convertLatin(hzPinYin);
        }

        return result;
    }
}
