package com.example.javaOjt.repositories.daos;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.QAuthor;
import com.example.javaOjt.beans.entities.QBook;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

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


  public List<Author> getAuthorsList(AuthorSortBy attribute, @Nullable Order order, @Nullable List<Integer> ids) {
    OrderSpecifier<?> orderSpecifier = switch (attribute) {
      case AuthorSortBy.NAME -> (order == Order.DSC) ? QAuthor.author.name.desc() : QAuthor.author.name.asc();
      case AuthorSortBy.ID -> (order == Order.DSC) ? QAuthor.author.id.desc() : QAuthor.author.id.asc();
    };
    JPAQuery<Author> allEntries = jpaQueryFactory.selectFrom(QAuthor.author);
    JPAQuery<Author> select = (ids == null)
        ? allEntries
        : allEntries.where(QAuthor.author.id.in(ids));
    return select
        .orderBy(orderSpecifier)
        .fetch();
  }
}
