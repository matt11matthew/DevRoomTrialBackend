package me.matthewe.devroomtrialbackend.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private String imageUrl;

    private String status;

    private String checkedOutBy;

    public Book(String title, String description, String imageUrl, String status, String checkedOutBy) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.checkedOutBy = checkedOutBy;
    }

    public void setCheckedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", status='" + status + '\'' +
                ", checkedOutBy='" + checkedOutBy + '\'' +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getCheckedOutBy() {
        return checkedOutBy;
    }

    public Book() {

    }

    public void setStatus(String status) {
        this.status = status;
    }

}
