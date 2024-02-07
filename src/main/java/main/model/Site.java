package main.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "site")
@Data
public class Site implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "enum('INDEXING','INDEXED','FAILED','REMOVING')",
            nullable = true)
    private String type;
    @Column(name = "status_time", columnDefinition = "DATETIME", nullable = true)
    private LocalDateTime statusTime ;
    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;
    @Column(nullable = true)
    private String url;


  //  @Column(nullable = false)
    private String name;

}
