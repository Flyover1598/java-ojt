package com.example.javaOjt.repositories.daos;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.QAuthor;
import com.example.javaOjt.beans.entities.QBook;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;

// @Repository
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {
// https://docs.spring.io/spring-data/jpa/reference/repositories/custom-implementations.html
  private final JPAQueryFactory jpaQueryFactory;

  public List<AuthorWithBookDTO> getAuthorWithBookDTOsById(Integer id) {
    return jpaQueryFactory.select(
        Projections.constructor(
          AuthorWithBookDTO.class,
          QAuthor.author,
          QBook.book
        )
      )
      .from(QAuthor.author)
      .leftJoin(QBook.book)
      .on(QAuthor.author.id.eq(QBook.book.authorId))
      .where(QAuthor.author.id.eq(id))
      .fetch();
  }

  @PersistenceContext
  private EntityManager entityManager;
  public List<Author> authorsList(String attribute, String order) {

//    Native SQLだとどうしてもuncheckedになるのでSuppressする（か、DTO書く？）
//    String sqlQuery = "SELECT * FROM Authors ORDER BY " + attribute + " " + order;
//    return entityManager.createNativeQuery(sqlQuery, Author.class).getResultList();

//    JPQL触ったことがないので今回はJPQLで書いてみたけど注文多いわこの方言
    String jpqlQuery = "SELECT a FROM Author a ORDER BY a." + attribute.toLowerCase() + " " + order;
    return entityManager.createQuery(jpqlQuery, Author.class).getResultList();
  }
}
