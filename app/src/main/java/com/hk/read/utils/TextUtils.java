package com.hk.read.utils;

/**
 * Created by changfeng on 2017/11/11.
 */

public class TextUtils {
    //默认是36个字符。
    public static final int LINE_WORDS_SUM = 33;

    private static int line_char_sum = LINE_WORDS_SUM;

    public static String[] ENG_PUNCTUATION_SINGLE= {",",".","?","!",":",";","<<",">>","(",")"};
    public static String[] ENG_PUNCTUATION_PAIR= {"\"","'"};

    public static String[] CHA_PUNCTUATION_SINGLE= {"，","。","？","！","：","；","《","》","（","）"};
    public static String[] CHA_PUNCTUATION_PAIR= {"“","”","’","‘"};

    public static void setLine_char_sum(int num) {
        line_char_sum = num;
    }

    /**
     * 查看是否是段落的最后一行
     *
     * @param line 这行的文字
     * @return
     */
    public static boolean isParagraphLastLine(String line) {
        if (line!=null) {
            if (line.length() < line_char_sum&&line.length()!=0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 格式化
     * @param pre
     * @param line
     * @return
     */
    public static String formatLine(String pre,String line){
        if (pre==null||line==null){
            return "";
        }
        StringBuilder builder = new StringBuilder(line);
        if (isParagraphLastLine(pre)) {
            //如果前一行为段落最后一行
            builder.insert(0,"    ");
        }

        return replacePunctuation(builder.toString());
    }

    /**
     * 英文标点符号换为中文
     * @param text
     * @return
     */
    public static String replacePunctuation(String text){

        for (int i = 0; i < ENG_PUNCTUATION_SINGLE.length; i++) {
            //替换单一标点符号
            text.replace(ENG_PUNCTUATION_SINGLE[i],CHA_PUNCTUATION_SINGLE[i]);
            //替换成对标点符号
        }

//        for (int i = 0; i < ENG_PUNCTUATION_PAIR.length; i++) {
//
////            text.replaceAll()
//        }


        return text;
    }
}
