package com.example.javaOjt.services;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.AuthorPK;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.example.javaOjt.repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
   *
   * @param attribute the attribute to sort by (id, name)
   * @param order     the order to sort by (asc, dsc)
   * @param ids       the list of author IDs to filter by; if null, all authors are returned
   * @return GetAuthorsResponse containing the list of authors
   */
  public GetAuthorsResponse getAuthorsList(@Nullable AuthorSortBy attribute, @Nullable Order order,
      @Nullable List<Integer> ids) { // Should return an empty list on empty DB
    attribute = (attribute == null) ? AuthorSortBy.ID : attribute;
    return new GetAuthorsResponse(authorRepository.getAuthorsList(attribute, order, ids));
  }

  /**
   * Saves a new author to the DB with the given String as the name.
   *
   * @param name the name of the author to be added
   * @return GetAuthorResponse of the author that was saved to the DB
   */

  @Transactional
  public GetAuthorResponse postAuthor(String name) {
    Author newAuthor = new Author();
    newAuthor.setName(name);
    return new GetAuthorResponse(authorRepository.save(newAuthor));
  }

  /**
   * Update the name of the author of the given id.
   *
   * @param id   the id of the author that is to be updated
   * @param name new name of the author
   * @return GetAuthorResponse of the author that was updated
   */
  @Transactional
  public GetAuthorResponse patchAuthor(Integer id, String name) {
    Optional<Author> author = authorRepository.findById(new AuthorPK(id));
    if (author.isEmpty()) {
      return GetAuthorResponse.notFoundResponse();
    }
    Author toUpdateAuthor = author.get();
    toUpdateAuthor.setName(name);
    return new GetAuthorResponse(authorRepository.save(toUpdateAuthor));
  }

}
