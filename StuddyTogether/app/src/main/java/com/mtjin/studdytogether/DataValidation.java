package com.mtjin.studdytogether;

import android.util.Log;

import java.util.regex.Pattern;

public class DataValidation {
    /*
     *  영어 + 숫자 만 포함된 문자 체크
     *  @Param 텍스트
     *  @Return 문자열 검증
     */
    public static boolean checkEngAndNum(String text) {
        String regExp = "^[a-z|0-9]*$";
        return Pattern.matches(regExp, text);
    }


    /*
     *  한글 + 영어 + 숫자 만 포함된 문자 체크
     *  @Param 텍스트
     *  @Return 문자열 검증
     */
    public static boolean checkOnlyCharacters(String text) {
        String regExp = "^[ㄱ-ㅎ가-힣a-z0-9]*$";
        return Pattern.matches(regExp, text);
    }


    /*
     *  아이디@주소.국가코드
     *  @Param 이메일
     *  @Return 이메일 검증
     */
    public static boolean checkEmail(String email) {
        String regExp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(regExp, email);
    }

    /*
    * 이메일 아이디 @ㅇㅇㅇ.com 에서 .com 전까지 파싱
    * @Param 이메일 (wlatmd210@gmail.com)
    * @Return 뒤에 .com 없앤 값 (wlatmd210@gmail)
    *
    * */
    public static String parsingEmail(String email){
        String result = "";
        String[] str = email.split("@"); //@이전값 사용할거다
        Log.d("Datavaild_TAG" , str[0]);  //wlatmd210
        Log.d("Datavaild_TAG" , str[1]); //gmail.com
        result += (str[0] + "@");
        Log.d("Datavaild_TAG" , result); //wlatmd210@
        String[] str2 = str[1].trim().split("\\."); // .이전값 사용할거다
        Log.d("Datavaild_TAG" , str2[0]);  // ==> ArrayIndexOutOfBoundsException 에러
        result += str2[0];
        return  result;
    }


}
