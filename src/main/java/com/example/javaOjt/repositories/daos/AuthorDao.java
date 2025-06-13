package com.example.javaOjt.repositories.daos;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import java.util.List;

public interface AuthorDao {

  List<AuthorWithBookDTO> getAuthorWithBookDTOsById(Integer id);

  List<Author> authorsList(String attribute, String order);

}
