package org.big.service;

import org.big.dto.MovieDto;
import org.big.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieMapper movieMapper;

    @Override
    public List<MovieDto> getAllMovies() throws Exception{
        return movieMapper.getAllMovies();
    }

    @Override
    public MovieDto getMovieById(Long id) throws Exception{
        return movieMapper.findById(id);
    }

    @Override
    public List<MovieDto> getBookmarkedMoviesByUser(Long userId) {
        return movieMapper.getBookmarkedMoviesByUser(userId);
    }

}
