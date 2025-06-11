package com.example.javaOjt.beans.responses.author;

import com.example.javaOjt.beans.entities.Author;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true) // JavaになくてJSONにあるプロパティは無視すること
@NoArgsConstructor
@Data
public class GetAuthorSummaryResponse { // Authorを返すだけでも良い気がするが、Authorの構造が変わるとResponseも変わるのでこれを挟もうと思った

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("name")
  private String name;

  public GetAuthorSummaryResponse(@NonNull Author author) {
    this.id = author.getId();
    this.name = author.getName();
  }
}
