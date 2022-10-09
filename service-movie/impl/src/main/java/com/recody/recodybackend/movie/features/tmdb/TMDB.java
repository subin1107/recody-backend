package com.recody.recodybackend.movie.features.tmdb;

public class TMDB {
    public static final String POSTER_PATH_PREFIX = "https://image.tmdb.org/t/p/w500";
    
    public static String fullPosterPath(String posterPath) {
        if (posterPath == null) {
            return null;
        }
        return POSTER_PATH_PREFIX.concat(posterPath);
    }
}
