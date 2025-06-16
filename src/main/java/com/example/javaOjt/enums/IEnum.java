package com.example.javaOjt.enums;

import com.example.javaOjt.exceptions.OjtBadRequestException;

public interface IEnum<E extends Enum<E>> {
// 各段使えるenumを限定する必要がないようにも思えるが、一応型安全のため
  static <E extends Enum<E> & IEnum<E>> E byString(Class<E> enumClass, String source, String exceptionMessage) {
    if (source == null || source.isEmpty()) return null; // should short-circuit when source == null
    try {
      return Enum.valueOf(enumClass, source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new OjtBadRequestException(exceptionMessage); // param以外で使う見込みがないため
    }
  }
}
