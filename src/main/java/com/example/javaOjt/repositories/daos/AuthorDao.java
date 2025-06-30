package com.example.javaOjt.repositories.daos;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import java.util.List;
import org.springframework.lang.Nullable;

public interface AuthorDao {

  List<AuthorWithBookDTO> getAuthorWithBookDTOsById(Integer id);

  List<Author> getAuthorsList(AuthorSortBy attribute, @Nullable Order order,
      @Nullable List<Integer> ids);

  void deleteByIdLogical(Author author);

}
