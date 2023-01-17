package org.rustem.application.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TaskDto implements Serializable {
    // имя задания
    private String name;
    // статус
    private String status;
    // найденный текст
    private String text;
    // ключевое слово
    private String word;
    // количество совпадении
    private int count_find_word;
    //имя файла где нашли ключевые слова
    private String file_name;
}

