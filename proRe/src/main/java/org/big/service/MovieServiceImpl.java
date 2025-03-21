package org.big.service;

import org.big.dto.MovieDto;
import org.big.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieMapper movieMapper;
    
    public List<MovieDto> getAllMovies() throws Exception{
    	return movieMapper.getAllMovies();
    }

	/*
	 * @Override public List<MovieDto> getAllMovies(int page, int pageSize) throws
	 * Exception { int offset = (page - 1) * pageSize; return
	 * movieMapper.getAllMoviesWithPaging(offset, pageSize); }
	 */

    @Override
    public MovieDto getMovieById(Long id) throws Exception{
        return movieMapper.findById(id);
    }

    @Override
    public List<MovieDto> getBookmarkedMoviesByUser(Long userId) {
        return movieMapper.getBookmarkedMoviesByUser(userId);
    }

	@Override
	@Transactional
	public void addMovie(MovieDto movie) {
        movieMapper.insertMovie(movie);
		
	}

	@Override
	public void updateMovie(MovieDto movie) {
	    MovieDto movieEntity = movieMapper.findById(movie.getId());
	    if (movieEntity == null) {
	        throw new RuntimeException("영화를 찾을 수 없습니다.");
	    }

	    // 영화 정보 업데이트
	    movieEntity.setTitle(movie.getTitle());
	    movieEntity.setDirector(movie.getDirector());
	    movieEntity.setReleaseDate(movie.getReleaseDate());
	    movieEntity.setGenre(movie.getGenre());
	    movieEntity.setIsDomestic(movie.getIsDomestic());
	    movieEntity.setCountry(movie.getCountry());
	    movieEntity.setSynopsis(movie.getSynopsis());
	    movieEntity.setPosterImageName(movie.getPosterImageName());  // 포스터 파일명 업데이트
	    
	    // 영화 업데이트
	    movieMapper.updateMovie(movieEntity);
	}

	@Override
	public void deleteMovie(Long id) {
	    try {
	        movieMapper.deleteMovie(id);  // 영화 삭제
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("영화 삭제 중 오류가 발생했습니다.");
	    }
	}

	@Override
	public int getReviewCount(Long movieId) {
	    return movieMapper.getReviewCountByMovieId(movieId);
	}

	@Override
	public List<MovieDto> getReviewsByMovieId(Long movieId, int offset, int pageSize) {
	    return movieMapper.getReviewsByMovieId(movieId, offset, pageSize);
	}
	
	@Override
	public List<MovieDto> getAllMoviesWithPaging(int page, int pageSize) throws Exception {
	    int offset = (page - 1) * pageSize;
	    List<MovieDto> movies = movieMapper.getAllMoviesWithPaging(offset, pageSize);
	    return movies != null ? movies : new ArrayList<>();
	}

}
