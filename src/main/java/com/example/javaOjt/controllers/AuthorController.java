package com.example.javaOjt.controllers;

import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.example.javaOjt.exceptions.OjtNotFoundException;
import com.example.javaOjt.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authors/")
@RequiredArgsConstructor
@Validated
public class AuthorController {

  private final AuthorService authorService;

  /**
   * Retrieves a list of all authors.
   * The list will be sorted when attribute OR order is specified.
   *
   * @param attribute attribute to sort by (id, name)
   *                  id: Author ID (default)
   *                  name: Author's name
   * @param order     sorting order (asc, dsc)
   *                  asc: ascending (default)
   *                  dsc: descending
   * @return a ResponseEntity containing the GetAuthorsResponse
   */
  @GetMapping("/")
  public ResponseEntity<GetAuthorsResponse> getAuthorsList(
      @RequestParam(value = "sort_by", required = false) AuthorSortBy attribute,
      @RequestParam(value = "order", required = false) Order order
  ) { // param, Optional<>にするのも考えましたが逆に複雑になりそうで断念した（後でそのツケを払うかも？）
    GetAuthorsResponse response = authorService.getAuthorsList(attribute, order);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves an author by his ID.
   *
   * @param id        the ID of the author to retrieve
   * @param withBooks whether to include the author's books in the response
   * @return a ResponseEntity containing the GetAuthorResponse
   * @throws OjtNotFoundException if the author does not exist
   */
  @GetMapping("/{id}")
  public ResponseEntity<GetAuthorResponse> getAuthor(
    @PathVariable(value = "id") Integer id,
    @RequestParam(value = "withBooks", required = false) boolean withBooks
  ) {
    GetAuthorResponse response = authorService.getAuthorById(id, withBooks);
    if (!response.isExists()) {
      throw new OjtNotFoundException("Author not found with ID: " + id);
    }
    return ResponseEntity.ok(response);
  }

}
