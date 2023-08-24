package com.example.webjpa.entities.db1;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.Date;

@Entity
@Data
@ToString
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Person.findAllPersons",
                query = "SELECT * FROM User",
                resultClass = User.class
        ),
        @NamedNativeQuery(
                name = "Person.findById",
                query = "SELECT * FROM User u WHERE u.id = :id",
                resultClass = User.class
        )
})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(
            name = "pooled",
            parameters = {
                    @Parameter(name = "sequence_name", value = "sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "3"),
                    @Parameter(name = "optimizer", value = "pooled")
            }
    )
    private Long id;
    private String name;
    private Long status;
    private Date statDate;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, Long status, Date statDate) {
        this.name = name;
        this.status = status;
        this.statDate = statDate;
    }
}
