#include "nikola_dragomirovic_shoppinglist_Check.h"

JNIEXPORT jint JNICALL Java_nikola_dragomirovic_shoppinglist_Check_check
  (JNIEnv *env, jobject jobj, jstring s){

    return (env)->GetStringLength(s);

  }
