package com.example.javaOjt.daos;

import com.example.javaOjt.DBTestBase;
import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.example.javaOjt.repositories.daos.AuthorDaoImpl;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthorDaoImplTest extends DBTestBase {

  @Autowired
  private AuthorDaoImpl authorDaoImpl;

  @Test
  void getAuthorWithBookBeanById() {
    int targetId = 1;

    List<AuthorWithBookDTO> result = authorDaoImpl.getAuthorWithBookDTOsById(targetId);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(targetId, result.getFirst().author().getId());
    Assertions.assertEquals("門畑顕博", result.getFirst().author().getName());
    Assertions.assertEquals(1, result.getFirst().book().getId());
    Assertions.assertEquals("AWSコスト最適化ガイドブック", result.getFirst().book().getTitle());
    Assertions.assertEquals(LocalDate.parse("2023-03-29"),
        result.getFirst().book().getPublishedAt());
  }

  @Test
  void getAuthorsList_noParam() {
    List<Author> result = authorDaoImpl.getAuthorsList(AuthorSortBy.ID, null, null);

    Assertions.assertEquals(4, result.size());
    for (int i = 0; i < 3; i++) {
      Assertions.assertEquals(i + 1, result.get(i).getId());
    }
    Assertions.assertEquals("門畑顕博", result.getFirst().getName());
    Assertions.assertEquals("J. K. Rowling", result.getLast().getName());
  }

  @Test
  void getAuthorsList_allParam() {
    List<Author> result = authorDaoImpl.getAuthorsList(AuthorSortBy.NAME, Order.DSC,
        List.of(2, 4, 10));

    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals(4, result.getLast().getId());
    Assertions.assertEquals(2, result.getFirst().getId());
    Assertions.assertEquals("J. K. Rowling", result.getLast().getName());
    Assertions.assertEquals("夏目漱石", result.getFirst().getName());

  }
}