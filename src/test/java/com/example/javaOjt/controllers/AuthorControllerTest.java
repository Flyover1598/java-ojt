package com.example.javaOjt.controllers;

import com.example.javaOjt.beans.entities.Author;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.enums.AuthorSortBy;
import com.example.javaOjt.enums.Order;
import com.example.javaOjt.exceptions.handlers.OjtExceptionHandler;
import com.example.javaOjt.services.AuthorService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(controllers = AuthorController.class)
class AuthorControllerTest {

  private static final String AUTHOR_BASE_URL = "/authors";

  private MockMvc mockMvc;

  @Autowired
  private AuthorController authorController;

  @MockitoBean
  private AuthorService authorService;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(authorController)
        .setControllerAdvice(new OjtExceptionHandler()).build();
  }

  @Test
  void getAuthorsList_noParams() throws Exception {
    // Mocking the service response
    GetAuthorsResponse mockResponse = new GetAuthorsResponse();
    Mockito.when(authorService.getAuthorsList(null, null, null)).thenReturn(mockResponse);

    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called correctly
    Mockito.verify(authorService, Mockito.times(1)).getAuthorsList(null, null, null);
  }

  @Test
  void getAuthorsList_attribute() throws Exception {
    // Mocking the service response
    GetAuthorsResponse mockResponse = new GetAuthorsResponse();
    Mockito.when(authorService.getAuthorsList(AuthorSortBy.NAME, null, null))
        .thenReturn(mockResponse);

    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("sort_by", "name"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorsList(AuthorSortBy.NAME, null, null);
  }

  @Test
  void getAuthorsList_order() throws Exception {
    // Mocking the service response
    GetAuthorsResponse mockResponse = new GetAuthorsResponse();
    Mockito.when(authorService.getAuthorsList(null, Order.DSC, null))
        .thenReturn(mockResponse);

    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("order", "dsc"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorsList(null, Order.DSC, null);
  }

  @Test
  void getAuthorsList_illegalAttribute() throws Exception {
    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL)
                .param("sort_by", "v50"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .getAuthorsList(Mockito.any(AuthorSortBy.class), Mockito.any(Order.class),
            Mockito.anyList());
  }

  @Test
  void getAuthorsList_illegalOrder() throws Exception {
    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL)
                .param("order", "kfc"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .getAuthorsList(Mockito.any(AuthorSortBy.class), Mockito.any(Order.class),
            Mockito.anyList());
  }

  @Test
  void getAuthorsList_ids() throws Exception {
    List<Integer> targetIds = List.of(1, 2);
    // Mocking the service response
    GetAuthorsResponse mockResponse = new GetAuthorsResponse();
    Mockito.when(authorService.getAuthorsList(null, null, targetIds))
        .thenReturn(mockResponse);

    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("ids", "1", "2"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorsList(null, null, targetIds);
  }

  @Test
  void getAuthorsList_nonexistentID() throws Exception {
    List<Integer> targetIds = List.of(Integer.MAX_VALUE);
    // Mocking the service response
    GetAuthorsResponse mockResponse = new GetAuthorsResponse();
    Mockito.when(authorService.getAuthorsList(null, null, targetIds))
        .thenReturn(mockResponse);

    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("ids", "2147483647"))
        .andExpect(MockMvcResultMatchers.status().isOk()) // should *not* return bad request
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorsList(null, null, targetIds);
  }

  @Test
  void getAuthorsList_illegalId() throws Exception {
    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("ids", "a"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.never())
        .getAuthorsList(Mockito.any(AuthorSortBy.class), Mockito.any(Order.class),
            Mockito.anyList());
  }

  @Test
  void getAuthor_noWithBooks() throws Exception {
    int targetId = 1;

    // Mocking the service response
    GetAuthorResponse mockResponse = new GetAuthorResponse();
    mockResponse.setExists(true); // Assuming the response indicates the author exists
    mockResponse.setId(targetId);
    Mockito.when(authorService.getAuthorById(targetId, false)).thenReturn(mockResponse);

    // Perform the GET request
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/{id}", targetId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorById(targetId, false);
  }

  @Test
  void getAuthor_withBooks_true() throws Exception {
    int targetId = 1;

    // Mocking the service response
    GetAuthorResponse mockResponse = new GetAuthorResponse();
    mockResponse.setExists(true); // Assuming the response indicates the author exists
    mockResponse.setId(targetId);
    Mockito.when(authorService.getAuthorById(targetId, true)).thenReturn(mockResponse);

    // Perform the GET request with the 'withBooks' parameter set to true
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/{id}", targetId)
                .param("withBooks", "true"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorById(targetId, true);
  }

  @Test
  void getAuthor_withBooks_false() throws Exception {
    int targetId = 1;

    // Mocking the service response
    GetAuthorResponse mockResponse = new GetAuthorResponse();
    mockResponse.setExists(true); // Assuming the response indicates the author exists
    mockResponse.setId(targetId);
    Mockito.when(authorService.getAuthorById(targetId, false)).thenReturn(mockResponse);

    // Perform the GET request with the 'withBooks' parameter set to false
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/{id}", targetId)
                .param("withBooks", "false"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).getAuthorById(targetId, false);
  }

  @Test
  void getAuthor_invalidAuthorId() throws Exception {
    // Perform the GET request with an invalid author ID
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/{id}", "aaaa"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .getAuthorById(Mockito.anyInt(), Mockito.anyBoolean());
  }

  @Test
  void getAuthor_invalidWithBooks() throws Exception {
    long targetId = 1;
    // Perform the GET request with an invalid 'withBooks' parameter
    mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/{id}", targetId)
                .param("withBooks", "invalid"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .getAuthorById(Mockito.anyInt(), Mockito.anyBoolean());
  }

  @Test
  void postAuthor() throws Exception {
    String requestBody = "{\"name\": \"三島由紀夫\"}";
    Author savedAuthor = new Author();
    savedAuthor.setId(1);
    savedAuthor.setName("三島由紀夫");

    // Mocking the service response
    Mockito.when(authorService.postAuthor(Mockito.any(String.class)))
        .thenReturn(new GetAuthorResponse(savedAuthor));

    // Perform the POST request with the POST request body
    mockMvc.perform(
            MockMvcRequestBuilders.post(AUTHOR_BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.header().string("Location", "/authors/1"))
        .andExpect(MockMvcResultMatchers.content().string(""))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).postAuthor("三島由紀夫");
  }

  @Test
  void postAuthor_empty() throws Exception {
    String requestBody = "{\"name\": \"\"}";

    // Perform the POST request with the POST request body and expect 400
    mockMvc.perform(
        MockMvcRequestBuilders.post(AUTHOR_BASE_URL).contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
    ).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never()).postAuthor(Mockito.any(String.class));
  }

  @Test
  void postAuthor_illegalKey() throws Exception {
    String requestBody = "{\"ability\": \"\"}";

    // Perform the POST request with the POST request body and expect 400
    mockMvc.perform(
        MockMvcRequestBuilders.post(AUTHOR_BASE_URL).contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
    ).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never()).postAuthor(Mockito.any(String.class));
  }

  @Test
  void postAuthor_nameAndIllegalKey() throws Exception {
    String requestBody = "{\"ability\": \"\", \"name\": \"三島由紀夫\"}";
    Author savedAuthor = new Author();
    savedAuthor.setId(1);
    savedAuthor.setName("三島由紀夫");

    // Mocking the service response
    Mockito.when(authorService.postAuthor(Mockito.any(String.class)))
        .thenReturn(new GetAuthorResponse(savedAuthor));

    // Perform the POST request with the POST request body
    mockMvc.perform(
            MockMvcRequestBuilders.post(AUTHOR_BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.header().string("Location", "/authors/1"))
        .andExpect(MockMvcResultMatchers.content().string(""))
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).postAuthor("三島由紀夫");
  }

  @Test
  void updateAuthor() throws Exception {
    String requestBody = "{\"name\": \"三島由紀夫\"}";
    Author updatedAuthor = new Author();
    updatedAuthor.setName("三島由紀夫");
    updatedAuthor.setId(1);

    // Mocking the service response
    Mockito.when(authorService.patchAuthor(updatedAuthor.getId(), updatedAuthor.getName()))
        .thenReturn(new GetAuthorResponse(updatedAuthor));

    // Perform the PATCH request to update an existing author
    mockMvc.perform(
            MockMvcRequestBuilders.patch(AUTHOR_BASE_URL + "/" + updatedAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1))
        .patchAuthor(updatedAuthor.getId(), updatedAuthor.getName());
  }

  @Test
  void updateAuthor_empty() throws Exception {
    int targetId = 1;
    String requestBody = "{\"name\": \"\"}";

    // Perform the PATCH request and expect 400
    mockMvc.perform(
        MockMvcRequestBuilders.patch(AUTHOR_BASE_URL + "/" + targetId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
    ).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .patchAuthor(Mockito.any(Integer.class), Mockito.any(String.class));
  }

  @Test
  void updateAuthor_illegalKey() throws Exception {
    int targetId = 1;
    String requestBody = "{\"kfc\": \"v50\"}";

    // Perform the PATCH request and expect 400
    mockMvc.perform(
        MockMvcRequestBuilders.patch(AUTHOR_BASE_URL + "/" + targetId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
    ).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .patchAuthor(Mockito.any(Integer.class), Mockito.any(String.class));
  }

  @Test
  void updateAuthor_nameAndIllegalKey() throws Exception {
    String requestBody = "{\"name\": \"三島由紀夫\", \"kfc\": \"v50\"}";
    Author updatedAuthor = new Author();
    updatedAuthor.setName("三島由紀夫");
    updatedAuthor.setId(1);

    // Mocking the service response
    Mockito.when(authorService.patchAuthor(updatedAuthor.getId(), updatedAuthor.getName()))
        .thenReturn(new GetAuthorResponse(updatedAuthor));

    // Perform the PATCH request to update an existing author
    mockMvc.perform(
            MockMvcRequestBuilders.patch(AUTHOR_BASE_URL + "/" + updatedAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1))
        .patchAuthor(updatedAuthor.getId(), updatedAuthor.getName());
  }

  @Test
  void updateAuthor_nonexistentId() throws Exception {
    int targetId = Integer.MAX_VALUE;
    String requestBody = "{\"name\": \"三島由紀夫\"}";

    // Mocking the service response
    Mockito.when(authorService.patchAuthor(targetId, "三島由紀夫"))
        .thenReturn(GetAuthorResponse.notFoundResponse());

    // Perform the PATCH request and expect 404
    mockMvc.perform(MockMvcRequestBuilders.patch(AUTHOR_BASE_URL + "/" + targetId)
            .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).patchAuthor(targetId, "三島由紀夫");
  }

  @Test
  void updateAuthor_illegalId() throws Exception {
    String requestBody = "{\"name\": \"三島由紀夫\"}";

    // Perform the PATCH request and expect 400
    mockMvc.perform(MockMvcRequestBuilders.patch(AUTHOR_BASE_URL + "/" + "kfc")
            .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .patchAuthor(Mockito.any(Integer.class), Mockito.any(String.class));
  }

  /* deleteAuthor's Controller method takes param id, which should be int,
   * and param permanent, which should be bool,
   * checks the types, and returns 400 if incompatible.
   * Otherwise, it calls the according service method,
   * produces 404 on a notFoundResponse return, and 204 otherwise. */

  @Test
  void deleteAuthorLogical() throws Exception {
    int targetId = 1;

    // Mocking the service response
    GetAuthorResponse mockResponse = new GetAuthorResponse();
    mockResponse.setExists(true); // Assuming the response indicates the author exists
    mockResponse.setId(targetId);
    Mockito.when(authorService.deleteAuthor(targetId, false)).thenReturn(mockResponse);

    // Perform the DELETE request to delete an existing author
    mockMvc.perform(
            MockMvcRequestBuilders.delete(AUTHOR_BASE_URL + "/" + targetId)
                .param("deletePermanently", "false")
        )
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).deleteAuthor(targetId, false);
  }

  @Test
  void deleteAuthorPhysical() throws Exception {
    int targetId = 1;

    // Mocking the service response
    GetAuthorResponse mockResponse = new GetAuthorResponse();
    mockResponse.setExists(true); // Assuming the response indicates the author exists
    mockResponse.setId(targetId);
    Mockito.when(authorService.deleteAuthor(targetId, true)).thenReturn(mockResponse);

    // Perform the DELETE request to delete an existing author
    mockMvc.perform(
            MockMvcRequestBuilders.delete(AUTHOR_BASE_URL + "/" + targetId)
                .param("deletePermanently", "true")
        )
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();

    // Verify that the service method was called with the correct parameters
    Mockito.verify(authorService, Mockito.times(1)).deleteAuthor(targetId, true);
  }

  @Test
  void deleteAuthor_illegalId() throws Exception {
    // Perform the DELETE request and expect 400
    mockMvc.perform(
            MockMvcRequestBuilders.delete(AUTHOR_BASE_URL + "/" + "kfc")
                .param("deletePermanently", "true")
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .deleteAuthor(Mockito.any(Integer.class), Mockito.any(Boolean.class));
  }

  @Test
  void deleteAuthor_notBool() throws Exception {
    int targetId = 1;

    // Perform the DELETE request and expect 400
    mockMvc.perform(
            MockMvcRequestBuilders.delete(AUTHOR_BASE_URL + "/" + targetId)
                .param("deletePermanently", "kfc")
        )
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();

    // Verify that the service method was never called
    Mockito.verify(authorService, Mockito.never())
        .deleteAuthor(Mockito.any(Integer.class), Mockito.any(Boolean.class));
  }

  @Test
  void deleteAuthor_nonexistentId() throws Exception {
    int targetId = Integer.MIN_VALUE;

    // Mocking the service response
    GetAuthorResponse mockResponse = new GetAuthorResponse();
    mockResponse.setExists(false); // Assuming the response indicates the author exists
    mockResponse.setId(targetId);
    Mockito.when(authorService.deleteAuthor(targetId, false)).thenReturn(mockResponse);
    // will test if the default is set to false as well

    // Perform the DELETE request and expect 404
    mockMvc.perform(
            MockMvcRequestBuilders.delete(AUTHOR_BASE_URL + "/" + targetId)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();

    // Verify that the service method was called
    Mockito.verify(authorService, Mockito.times(1))
        .deleteAuthor(targetId, false);
  }

}