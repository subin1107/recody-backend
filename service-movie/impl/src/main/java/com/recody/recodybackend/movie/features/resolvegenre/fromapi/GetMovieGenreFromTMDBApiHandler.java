package com.recody.recodybackend.movie.features.resolvegenre.fromapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.recody.recodybackend.common.openapi.JsonAPIResponse;
import com.recody.recodybackend.movie.data.genre.MovieGenreCodeEntity;
import com.recody.recodybackend.movie.features.manager.MovieGenreCodeManager;
import com.recody.recodybackend.movie.general.MovieGenre;
import com.recody.recodybackend.movie.general.MovieSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
class GetMovieGenreFromTMDBApiHandler implements com.recody.recodybackend.movie.features.resolvegenres.fromapi.GetMovieGenreFromTMDBApiHandler {
    
    private static final String GENRES = "genres";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String baseUrl = "https://api.themoviedb.org/3";
    private static final String GENRE_PATH = "/genre/movie/list";
    public static final String API_KEY_PARAM_NAME = "api_key";
    private final Map<Integer, String> tmdbGenreMap = new HashMap<>();
    private final MovieGenreCodeManager movieGenreCodeManager;
    private final RestTemplate restTemplate = new RestTemplate();
    private boolean hasGenres = false;
    @Value("${movie.tmdb.api-key}")
    private String apiKey;
    
    public GetMovieGenreFromTMDBApiHandler(MovieGenreCodeManager movieGenreCodeManager) {
        this.movieGenreCodeManager = movieGenreCodeManager;
    }
    
    public boolean hasGenres() {
        return hasGenres;
    }
    
    @Override
    @PostConstruct
    public void initTmdbGenre() {
        URI url = makeUrl();
        RequestEntity<Void> request = RequestEntity.get(url).build();
        
        JsonNode jsonNode1 = restTemplate.exchange(request, JsonNode.class).getBody();
        JsonAPIResponse response = new JsonAPIResponse(jsonNode1);
        try {
            response.rawStream().map(jsonNode -> {
                List<JsonNode> parents = jsonNode.withArray(GENRES).findParents(ID);
                HashMap<Integer, String> map = new HashMap<>();
                for (JsonNode parent : parents) {
                    JsonNode idField = parent.get(ID);
                    JsonNode nameField = parent.get(NAME);
                    tmdbGenreMap.put(idField.asInt(), nameField.textValue());
                }
                return map;
            }).findAny().orElseThrow(() -> new RuntimeException("json 스트림을 처리하지 못했습니다."));
            this.hasGenres = true;
            log.info("Initiated Genres From TMDB API");
        } catch (Exception exception) {
            log.warn("exception: {}", exception.getMessage());
            this.hasGenres = false;
        }
        
        for (Integer key : tmdbGenreMap.keySet()) {
            MovieGenre genre = new MovieGenre(key, tmdbGenreMap.get(key));
            genre.setSource(MovieSource.TMDB);
            MovieGenreCodeEntity genreId = movieGenreCodeManager.register(genre);
        }
    }
    
    @Override
    public MovieGenre getMovieGenre(Integer tmdbGenreId) {
        String genreName = tmdbGenreMap.get(tmdbGenreId);
        if (genreName == null) {
            // TMDB 장르 맵에 없음.
            log.warn("TMDB Genre Map 에서 장르를 찾을 수 없었습니다. tmdbGenreId: {}", tmdbGenreId);
        }
        log.trace("for TMDB Genre Id: {},  GenreName: {}", tmdbGenreId, genreName);
        return new MovieGenre(tmdbGenreId, genreName);
    }
    
    private URI makeUrl() {
        return UriComponentsBuilder.fromUriString(baseUrl)
                                   .path(GENRE_PATH)
                                   .queryParam(API_KEY_PARAM_NAME, apiKey)
                                   .encode()
                                   .build()
                                   .toUri();
    }
}
