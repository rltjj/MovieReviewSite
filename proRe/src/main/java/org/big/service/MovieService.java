package org.big.service;

import org.big.dto.MovieDto;
import java.util.List;

public interface MovieService {
	List<MovieDto> getAllMovies() throws Exception;
    MovieDto getMovieById(Long id) throws Exception;
	List<MovieDto> getBookmarkedMoviesByUser(Long id);
	void addMovie(MovieDto movie);
	void updateMovie(MovieDto movie);
	void deleteMovie(Long id);
	int getReviewCount(Long movieId);
	List<MovieDto> getReviewsByMovieId(Long movieId, int offset, int pageSize);
	List<MovieDto> getAllMoviesWithPaging(int page, int pageSize) throws Exception;
	
	List<MovieDto> searchByTitle(String keyword);
	List<MovieDto> searchByGenre(String keyword);
	List<MovieDto> searchByCountry(String keyword);
	List<MovieDto> searchBySummary(String keyword);
	List<MovieDto> searchByAll(String keyword);
}
