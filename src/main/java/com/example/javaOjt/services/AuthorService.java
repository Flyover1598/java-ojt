package com.example.javaOjt.services;

import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;

import com.example.javaOjt.beans.responses.author.GetAuthorsResponse.AuthorInfo;
import com.example.javaOjt.controllers.AuthorSortBy;
import com.example.javaOjt.controllers.Order;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.AuthorPK;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

  final private AuthorRepository authorRepository;

  /**
   * Fetches an author by his ID.
   *
   * @param id        author ID
   * @param withBooks if true, fetches the author along with their books; if false, fetches only the
   *                  author details
   * @return GetAuthorResponse containing author details and optionally their books
   */
  public GetAuthorResponse getAuthorById(Integer id, boolean withBooks) {
    GetAuthorResponse response;
    // If withBooks is true, fetch the author along with their books
    if (withBooks) {
      List<AuthorWithBookDTO> beans = authorRepository.getAuthorWithBookDTOsById(id);
      if (beans.isEmpty()) {
        return GetAuthorResponse.notFoundResponse();
      }
      response = new GetAuthorResponse();
      response.setExists(true);
      response.setId(beans.getFirst().author().getId());
      response.setName(beans.getFirst().author().getName());
      response.setBooks(beans.stream().map(
          bean -> new GetAuthorResponse.Book(
            bean.book().getId(),
            bean.book().getTitle(),
            bean.book().getPublishedAt() == null ? null : bean.book().getPublishedAt().toString()
          )
        ).toList()
      );
    }
    // If withBooks is false, fetch only the author details
    else {
      Optional<Author> author = authorRepository.findById(new AuthorPK(id));
      if (author.isEmpty()) {
        return GetAuthorResponse.notFoundResponse();
      } else {
        response = new GetAuthorResponse(author.get());
      }
    }
    return response;
  }

  /**

   * Fetches the list of authors and sort if needed.
   */
  public GetAuthorsResponse getAuthorsList(@Nullable AuthorSortBy attribute, @Nullable Order order) { // Should return an empty list on empty DB
    GetAuthorsResponse response = new GetAuthorsResponse(authorRepository.findAll());
    if (attribute == null && order == null) return response;
    if (attribute == null) attribute = AuthorSortBy.id;
    switch (attribute) { // 今はnameとidしかないから一つのswitchで完結、めちゃくちゃ増えたらまずcomparator決めるかも
      case AuthorSortBy.name -> {
        Comparator<String> stringComparator = (order == Order.dsc)
            ? Comparator.reverseOrder()
            : Comparator.naturalOrder(); // Orderはascとdsc以外ありえないと思う
        response.getAuthorsSummaryList().sort(
            Comparator.comparing(AuthorInfo::name, stringComparator)
        );
      }
      case AuthorSortBy.id -> {
        Comparator<Integer> intComparator = (order == Order.dsc)
            ? Comparator.reverseOrder()
            : Comparator.naturalOrder();
        response.getAuthorsSummaryList().sort(
            Comparator.comparing(AuthorInfo::id, intComparator)
        );
      }
    }
    return response;
  }

}
