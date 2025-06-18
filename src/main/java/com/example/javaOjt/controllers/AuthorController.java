package com.example.javaOjt.controllers;

import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.IEnum;
import com.example.javaOjt.enums.Order;
import com.example.javaOjt.exceptions.OjtNotFoundException;
import com.example.javaOjt.services.AuthorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Validated
public class AuthorController {

  private final AuthorService authorService;

  /**
   * Retrieves a list of all authors. The list will ALWAYS be sorted.
   *
   * @param attributeString attribute to sort by (id, name); case-insensitive id: Author ID
   *                        (default) name: Author's name
   * @param orderString     sorting order (asc, dsc); case-insensitive asc: ascending (default) dsc:
   *                        descending
   * @param ids             list of integers delimited by comma returns empty response for
   *                        unexistent ids
   * @return a ResponseEntity containing the GetAuthorsResponse
   */
  @GetMapping("")
  public ResponseEntity<GetAuthorsResponse> getAuthorsList(
      @RequestParam(value = "sort_by", required = false) String attributeString,
      @RequestParam(value = "order", required = false) String orderString,
      @RequestParam(value = "ids", required = false) List<Integer> ids
  ) {
    AuthorSortBy attribute = IEnum.byString(AuthorSortBy.class, attributeString,
        "Invalid sort_by attribute");
    Order order = IEnum.byString(Order.class, orderString, "Invalid order");
    GetAuthorsResponse response = authorService.getAuthorsList(attribute, order, ids);
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
