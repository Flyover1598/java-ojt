package com.example.javaOjt.repositories.daos;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.QAuthor;
import com.example.javaOjt.beans.entities.QBook;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
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

  public List<Author> getAuthorsList(AuthorSortBy attribute, @Nullable Order order,
      @Nullable List<Integer> ids) {
    OrderSpecifier<?> orderSpecifier = switch (attribute) {
      case AuthorSortBy.NAME ->
          (order == Order.DSC) ? QAuthor.author.name.desc() : QAuthor.author.name.asc();
      case AuthorSortBy.ID ->
          (order == Order.DSC) ? QAuthor.author.id.desc() : QAuthor.author.id.asc();
    };
    List<Predicate> predicates = new ArrayList<>();
    if (ids != null) {
      predicates.add(QAuthor.author.id.in(ids));
    }
    return jpaQueryFactory.selectFrom(QAuthor.author)
        .where(predicates.toArray(new Predicate[0]))
        .orderBy(orderSpecifier)
        .fetch();
  }

  @Modifying
  @Transactional
  public void deleteByIdLogical(Integer id) {
    ZonedDateTime now = ZonedDateTime.now(); // Calculate the timestamp once
    jpaQueryFactory.update(QAuthor.author)
        .set(QAuthor.author.deletedTimestamp, now)
        .set(QAuthor.author.updatedTimestamp, now) // Use the same timestamp for both fields
        .where(QAuthor.author.id.eq(id))
        .execute();
  }

  @Modifying
  @Transactional
  public void deleteByIdPhysical(Integer id) {
    jpaQueryFactory.delete(QAuthor.author)
        .where(QAuthor.author.id.eq(id))
        .execute();
  }

}
