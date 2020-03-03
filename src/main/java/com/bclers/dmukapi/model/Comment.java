package com.bclers.dmukapi.model;

import com.bclers.dmukapi.enums.NewsSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "local_site_id")
    private String localSiteCmtID;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Column(name = "body")
    private String body;

    @Column(name = "author")
    private String author;

    @Column(name = "score")
    private int score;

    @Column(name = "date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime date;

    @Column(name = "comment_source")
    @Enumerated(EnumType.STRING)
    private NewsSource commentSource;

    @Column(name = "article_title")
    private String articleTitle;

    @Column(name = "created_date", columnDefinition= "TIMESTAMP WITH TIME ZONE", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDateTime;

    public Comment(String body)
    {
        this.body = body;
    }

    public Comment(String body, String author)
    {
        this.body = body;
        this.author = author;
    }

    public Comment(int id, String body, String author)
    {
        this.id = id;
        this.body = body;
        this.author = author;
    }

    public Comment(String body, String author, LocalDateTime date)
    {
        this.body = body;
        this.author = author;
        this.date = date;
    }

    public String toString()
    {
        return id + " " + body + " " + author + " " + score + " " + date + " " + commentSource + " " + articleTitle;
    }
}
