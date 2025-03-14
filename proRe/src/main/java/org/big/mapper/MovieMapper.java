package org.big.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.big.dto.MovieDto;

@Mapper
public interface MovieMapper {
	List<MovieDto> getAllMovies();
    MovieDto findById(Long id);
	List<MovieDto> getBookmarkedMoviesByUser(Long userId);
	void updateMovie(MovieDto movie);
	void updateMovieCount(MovieDto movie);
	void insertMovie(MovieDto movie);
	void save(MovieDto movieEntity);
	void deleteMovie(Long id);
	int getReviewCountByMovieId(Long movieId);
	List<MovieDto> getReviewsByMovieId(Long movieId, int offset, int pageSize);
	List<MovieDto> getAllMoviesWithPaging(@Param("offset") int offset, @Param("pageSize") int pageSize);

}
