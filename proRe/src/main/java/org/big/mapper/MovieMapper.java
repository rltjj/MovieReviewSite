package org.big.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.big.dto.MovieDto;

@Mapper
public interface MovieMapper {
    List<MovieDto> getAllMovies();
    MovieDto findById(Long id);
	List<MovieDto> getBookmarkedMoviesByUser(Long userId);
	void updateMovie(MovieDto movie);
	void updateMovieCount(MovieDto movie);
}
