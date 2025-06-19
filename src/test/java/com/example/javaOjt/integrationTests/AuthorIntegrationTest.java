package com.example.javaOjt.integrationTests;

import com.example.javaOjt.DBTestBase;
import com.example.javaOjt.beans.responses.author.GetAuthorResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse;
import com.example.javaOjt.beans.responses.author.GetAuthorsResponse.AuthorInfo;
import com.example.javaOjt.beans.responses.common.OjtExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
class AuthorIntegrationTest extends DBTestBase {

  private static final String AUTHOR_BASE_URL = "/authors";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void getAuthor_isExists() throws Exception {
    int targetId = 1;

    // Perform the GET request to retrieve an existing author
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/" + targetId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the response to GetAuthorResponse object
    GetAuthorResponse resultAuthor = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        GetAuthorResponse.class);

    // Assertions to verify the author details
    Assertions.assertNotNull(resultAuthor);
    Assertions.assertEquals(targetId, resultAuthor.getId());
    Assertions.assertEquals("門畑顕博", resultAuthor.getName());
  }

  @Test
  void getAuthorTest_notFound() throws Exception {
    int targetId = Integer.MAX_VALUE;

    // Attempt to get an author that does not exist
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/" + targetId))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the error response
    OjtExceptionResponse resultErr = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OjtExceptionResponse.class);

    // Assertions to verify the error response
    Assertions.assertNotNull(resultErr);
    Assertions.assertNotNull(resultErr.getError());
    Assertions.assertEquals("Author not found with ID: " + targetId,
        resultErr.getError().message());
    Assertions.assertEquals(404, resultErr.getError().code());
  }

  @Test
  void getAuthorTest_withBooks_true() throws Exception {
    int targetId = 1;

    // Perform the GET request to retrieve an existing author
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL + "/" + targetId)
                .param("withBooks", "true"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the response to GetAuthorResponse object
    GetAuthorResponse resultAuthor = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        GetAuthorResponse.class);

    // Assertions to verify the author details
    Assertions.assertNotNull(resultAuthor);
    Assertions.assertEquals(targetId, resultAuthor.getId());
    Assertions.assertEquals("門畑顕博", resultAuthor.getName());
    Assertions.assertTrue(resultAuthor.isExists());
    Assertions.assertFalse(resultAuthor.getBooks().isEmpty());
    Assertions.assertEquals(1, resultAuthor.getBooks().size());
    Assertions.assertEquals(1, resultAuthor.getBooks().getFirst().id());
    Assertions.assertEquals("AWSコスト最適化ガイドブック",
        resultAuthor.getBooks().getFirst().title());
    Assertions.assertEquals("2023-03-29", resultAuthor.getBooks().getFirst().publishedAt());
  }

  @Test
  void getAuthorsTest_noParam() throws Exception {
    // Perform the GET request to retrieve authors
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the response to GetAuthorsResponse object
    GetAuthorsResponse resultAuthors = objectMapper.readValue(
        result.getResponse().getContentAsString(), GetAuthorsResponse.class);

    // Assertions to verify the details
    Assertions.assertNotNull(resultAuthors);
    Assertions.assertEquals(4, resultAuthors.getAuthorsSummaryList().size());
    Assertions.assertEquals(1, resultAuthors.getAuthorsSummaryList().getFirst().id());
    Assertions.assertEquals("門畑顕博", resultAuthors.getAuthorsSummaryList().getFirst().name());
    Assertions.assertEquals(4, resultAuthors.getAuthorsSummaryList().getLast().id());
    Assertions.assertEquals("J. K. Rowling",
        resultAuthors.getAuthorsSummaryList().getLast().name());
  }

  @Test
  void getAuthorsTest_allParam() throws Exception {
    // Perform the GET request to retrieve authors
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("sort_by", "name").param("order", "dsc")
                .param("ids", "2, 3, 10"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the response to GetAuthorsResponse object
    GetAuthorsResponse resultAuthors = objectMapper.readValue(
        result.getResponse().getContentAsString(), GetAuthorsResponse.class);

    // Assertions to verify the details
    Assertions.assertNotNull(resultAuthors);
    Assertions.assertEquals(2, resultAuthors.getAuthorsSummaryList().size());
    Assertions.assertEquals(3, resultAuthors.getAuthorsSummaryList().getFirst().id());
    Assertions.assertEquals("太宰治", resultAuthors.getAuthorsSummaryList().getFirst().name());
    Assertions.assertEquals(2, resultAuthors.getAuthorsSummaryList().getLast().id());
    Assertions.assertEquals("夏目漱石",
        resultAuthors.getAuthorsSummaryList().getLast().name());
  }

  @Test
  void getAuthorsTest_illegalAttribute() throws Exception {
    // Perform the GET request to retrieve authors
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("sort_by", "v50"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the error response
    OjtExceptionResponse resultErr = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OjtExceptionResponse.class);

    // Assertions to verify the error response
    Assertions.assertNotNull(resultErr);
    Assertions.assertNotNull(resultErr.getError());
    Assertions.assertEquals("Invalid sort_by attribute",
        resultErr.getError().message());
    Assertions.assertEquals(400, resultErr.getError().code());
  }

  @Test
  void getAuthorsTest_illegalOrder() throws Exception {
    // Perform the GET request to retrieve authors
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("order", "kfc"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the error response
    OjtExceptionResponse resultErr = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        OjtExceptionResponse.class);

    // Assertions to verify the error response
    Assertions.assertNotNull(resultErr);
    Assertions.assertNotNull(resultErr.getError());
    Assertions.assertEquals("Invalid order",
        resultErr.getError().message());
    Assertions.assertEquals(400, resultErr.getError().code());
  }

  @Test
  void getAuthorsTest_emptyList() throws Exception {
    // Perform the GET request to retrieve authors
    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(AUTHOR_BASE_URL).param("ids", "65535"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Parse the error response
    GetAuthorsResponse resultEmptyList = objectMapper.readValue(
        result.getResponse().getContentAsString(), GetAuthorsResponse.class);

    // Assertions to verify the error response
    Assertions.assertNotNull(resultEmptyList);
    Assertions.assertEquals(new ArrayList<AuthorInfo>(), resultEmptyList.getAuthorsSummaryList());
  }

}