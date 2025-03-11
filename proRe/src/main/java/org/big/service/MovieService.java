package org.big.service;

import org.big.dto.MovieDto;
import java.util.List;

public interface MovieService {
    List<MovieDto> getAllMovies() throws Exception;
    MovieDto getMovieById(Long id) throws Exception;
}
