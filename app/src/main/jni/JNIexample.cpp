#include "pavle_vukovic_memorygame_JNIexample.h"

JNIEXPORT jint JNICALL Java_pavle_vukovic_memorygame_JNIexample_points
  (JNIEnv *, jobject, jboolean flag, jint poeni){

    if(flag == true){
        return poeni += 5;
    }else{
        return --poeni;
    }
  }
