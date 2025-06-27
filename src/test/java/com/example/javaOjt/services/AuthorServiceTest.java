package com.example.javaOjt.services;

import com.example.javaOjt.beans.dtos.AuthorWithBookDTO;
import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.entities.AuthorPK;
import com.example.javaOjt.beans.entities.Book;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.example.javaOjt.repositories.AuthorRepository;
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
    mockAuthors.get(0).setId(1);
    mockAuthors.get(0).setName("Test Author1");
    Mockito.when(authorRepository.getAuthorsList(AuthorSortBy.ID, null, null))
        .thenReturn(mockAuthors);
    // Call the service method
    GetAuthorsResponse response = authorService.getAuthorsList(null, null, null);
    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Assertions.assertEquals(mockAuthors.size(), response.getAuthorsSummaryList().size());
    Assertions.assertEquals(mockAuthors.getFirst().getId(),
        response.getAuthorsSummaryList().getFirst().id());
    Assertions.assertEquals(mockAuthors.getFirst().getName(),
        response.getAuthorsSummaryList().getFirst().name());
    Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsList(AuthorSortBy.ID, null, null);
  }

  @Test
  void getAuthorsResponse_allParams() {
    int targetId1 = 1;
    int targetId2 = 2;

    // Mocking the Authors response
    List<Author> mockAuthors = List.of(new Author(), new Author());
    mockAuthors.get(0).setId(targetId1);
    mockAuthors.get(1).setId(targetId2);
    mockAuthors.get(0).setName("Author B");
    mockAuthors.get(1).setName("Author A");
    Mockito.when(
            authorRepository.getAuthorsList(AuthorSortBy.NAME, Order.DSC,
                List.of(targetId1, targetId2)))
        .thenReturn(mockAuthors);

    // Call the service method
    GetAuthorsResponse response = authorService.getAuthorsList(AuthorSortBy.NAME, Order.DSC,
        List.of(targetId1, targetId2));

    // Assertions to verify the response
    Assertions.assertNotNull(response);
    Assertions.assertEquals(mockAuthors.size(), response.getAuthorsSummaryList().size());
    Assertions.assertEquals(mockAuthors.get(0).getId(),
        response.getAuthorsSummaryList().get(0).id());
    Assertions.assertEquals(mockAuthors.get(1).getId(),
        response.getAuthorsSummaryList().get(1).id());
    Assertions.assertEquals(mockAuthors.get(0).getName(),
        response.getAuthorsSummaryList().get(0).name());
    Assertions.assertEquals(mockAuthors.get(1).getName(),
        response.getAuthorsSummaryList().get(1).name());
    Mockito.verify(authorRepository, Mockito.times(1))
        .getAuthorsList(AuthorSortBy.NAME, Order.DSC, List.of(targetId1, targetId2));
  }

  @Test
  void postAuthor() { // エッジケースが思いつかない
    String name = "三島由紀夫";
    Author savedAuthor = new Author();
    savedAuthor.setName(name);
    savedAuthor.setId(1);

    // Mocking the response
    Mockito.when(authorRepository.save(Mockito.any(Author.class))).thenReturn(savedAuthor);

    // Call the service method
    GetAuthorResponse newAuthor = authorService.postAuthor(name);

    // Assertions to verify the response
    Assertions.assertNotNull(newAuthor);
    Assertions.assertEquals(savedAuthor.getName(), newAuthor.getName());
    Assertions.assertNotNull(newAuthor.getId());
  }

  @Test
  void patchAuthor() {
    Author toUpdateAuthor = new Author();
    toUpdateAuthor.setName("三島由紀夫");
    toUpdateAuthor.setId(1);

    // Mocking the response
    Mockito.when(authorRepository.findById(new AuthorPK(1)))
        .thenReturn(Optional.of(toUpdateAuthor));
    Mockito.when(authorRepository.save(Mockito.any(Author.class))).thenReturn(toUpdateAuthor);

    // Call the service method
    GetAuthorResponse updatedAuthor = authorService.patchAuthor(toUpdateAuthor.getId(),
        toUpdateAuthor.getName());

    // Assertions to verify the response
    Assertions.assertNotNull(updatedAuthor);
    Assertions.assertEquals(toUpdateAuthor.getName(), updatedAuthor.getName());
    Assertions.assertEquals(toUpdateAuthor.getId(), updatedAuthor.getId());
  }

}