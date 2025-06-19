package com.example.javaOjt.services;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.AuthorPK;
import com.example.javaOjt.beans.entities.Book;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.repositories.AuthorRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  private AuthorService authorService;

  @Mock
  private AuthorRepository authorRepository;

  @BeforeEach
  void setUp() {
    authorService = new AuthorService(authorRepository);
  }

  @Test
  void getAuthorById_withBooks_true() {
    int targetId = 1;

    // Mocking the AuthorWithBooksBean response
    Author mockAuthor = new Author();
    mockAuthor.setId(targetId);
    mockAuthor.setName("Mock Author");
    Book mockBook = new Book();
    mockBook.setId(2);
    mockBook.setTitle("Mock Book Title");

    AuthorWithBookDTO mockBean = new AuthorWithBookDTO(mockAuthor, mockBook);
    Mockito.when(authorRepository.getAuthorWithBookDTOsById(targetId))
        .thenReturn(List.of(mockBean));

    // Call the service method
    GetAuthorResponse response = authorService.getAuthorById(targetId, true);

    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Assertions.assertEquals(mockBean.author().getId(), response.getId());
    Assertions.assertEquals(1, response.getBooks().size());
    Assertions.assertEquals(mockBean.book().getId(), response.getBooks().getFirst().id());
    Mockito.verify(authorRepository, Mockito.times(1)).getAuthorWithBookDTOsById(targetId);
    Mockito.verify(authorRepository, Mockito.never()).findById(Mockito.any());
  }

  @Test
  void getAuthorById_withBooks_false() {
    int targetId = 1;

    // Mocking the Author response
    Author mockAuthor = new Author();
    mockAuthor.setId(targetId);
    AuthorPK authorPK = new AuthorPK(targetId);
    Mockito.when(authorRepository.findById(authorPK)).thenReturn(Optional.of(mockAuthor));

    // Call the service method
    GetAuthorResponse response = authorService.getAuthorById(targetId, false);

    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Assertions.assertEquals(mockAuthor.getId(), response.getId());
    Assertions.assertTrue(response.isExists());
    Mockito.verify(authorRepository, Mockito.never()).getAuthorWithBookDTOsById(targetId);
    Mockito.verify(authorRepository, Mockito.times(1)).findById(Mockito.any());
  }

  @Test
  void getAuthorsResponse_noParams() {
    // Mocking the Authors response
    List<Author> mockAuthors = List.of(new Author());
    Mockito.when(authorRepository.getAuthorsList(AuthorSortBy.ID, null, null))
        .thenReturn(mockAuthors);
    // Call the service method
    GetAuthorsResponse response = authorService.getAuthorsList(null, null, null);
    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsList(AuthorSortBy.ID, null, null);
  }

  @Test
  void getAuthorsResponse_ids() {
    int targetId1 = 1;
    int targetId2 = 2;

    // Mocking the Authors response
    List<Author> mockAuthors = List.of(new Author(), new Author());
    mockAuthors.get(0).setId(targetId1);
    mockAuthors.get(1).setId(targetId2);
    Mockito.when(
            authorRepository.getAuthorsList(AuthorSortBy.ID, null, List.of(targetId1, targetId2)))
        .thenReturn(mockAuthors);

    // Call the service method
    GetAuthorsResponse response = authorService.getAuthorsList(null, null,
        List.of(targetId1, targetId2));

    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Assertions.assertEquals(mockAuthors.size(), response.getAuthorsSummaryList().size());
    Assertions.assertEquals(mockAuthors.get(0).getId(),
        response.getAuthorsSummaryList().get(0).id());
    Assertions.assertEquals(mockAuthors.get(1).getId(),
        response.getAuthorsSummaryList().get(1).id());
    Mockito.verify(authorRepository, Mockito.times(1))
        .getAuthorsList(AuthorSortBy.ID, null, List.of(targetId1, targetId2));
  }

  @Test
  void getAuthorsResponse_invalidId() {
    int targetId = 2147483647;

    // Mocking the Authors response
    List<Author> mockAuthors = new ArrayList<>();
    Mockito.when(
            authorRepository.getAuthorsList(AuthorSortBy.ID, null, List.of(targetId)))
        .thenReturn(mockAuthors);

    // Call the service method
    GetAuthorsResponse response = authorService.getAuthorsList(null, null,
        List.of(targetId));

    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Assertions.assertEquals(mockAuthors, response.getAuthorsSummaryList());
    Mockito.verify(authorRepository, Mockito.times(1))
        .getAuthorsList(AuthorSortBy.ID, null, List.of(targetId));
  }
}