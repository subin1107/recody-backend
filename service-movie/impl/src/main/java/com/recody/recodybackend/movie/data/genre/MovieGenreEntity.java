package com.recody.recodybackend.movie.data.genre;

import com.recody.recodybackend.movie.data.MovieBaseEntity;
import com.recody.recodybackend.movie.data.movie.MovieEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "movie_genre")
public class MovieGenreEntity extends MovieBaseEntity {
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "movie_genre_contains_movie_id"))
    private MovieEntity movie;
    
    @ManyToOne
    @JoinColumn(name = "genre_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "movie_genre_contains_genre_id"))
    private MovieGenreCodeEntity genre;
    
    @Override
    public String toString() {
        return "[{\"MovieGenreEntity\":{" + "\"id\":" + id + ", \"movie\":" + movie + ", \"genre\":" + genre + "}}, " + super.toString() + "]";
    }
}
