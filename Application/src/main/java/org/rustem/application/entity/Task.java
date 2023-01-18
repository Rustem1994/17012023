package org.rustem.application.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "count_find_word", nullable = false)
    private int count_find_word;

    @Column(name = "file_name", nullable = false)
    private String file_name;


}