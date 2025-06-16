package com.example.javaOjt.beans.responses.author;

import com.example.javaOjt.beans.entities.Author;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true) // JavaになくてJSONにあるプロパティは無視すること
@NoArgsConstructor
@Data
public class GetAuthorsResponse { // Authorを返すだけでも良い気がするが、Authorの構造が変わるとResponseも変わるのでこれを挟もうと思った

  public record AuthorInfo( // 確かに著者情報ではなくこれをラップしたものが良い

      @JsonProperty("id") Integer id,
      @JsonProperty("name") String name
  ) {}

  @JsonProperty("Authors") // JsonPropertyの存在を忘れてた（！？）
  private List<AuthorInfo> authorsSummaryList = new ArrayList<>();

  public GetAuthorsResponse(@NonNull Iterable<Author> authorsList) {
    authorsList.forEach(
        author -> this.authorsSummaryList.add(new AuthorInfo(author.getId(), author.getName()))
    );
  }

  public GetAuthorsResponse(@NonNull List<Author> authorsList) {
    authorsList.forEach(
        author -> this.authorsSummaryList.add(new AuthorInfo(author.getId(), author.getName()))
    );
  }

}
