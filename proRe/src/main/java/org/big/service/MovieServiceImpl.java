package org.big.service;

import org.big.dto.MovieDto;
import org.big.mapper.ProReMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private ProReMapper movieMapper;

    @Override
    public List<MovieDto> getAllMovies() throws Exception{
        return movieMapper.getAllMovies();
    }

    @Override
    public MovieDto getMovieById(Long id) throws Exception{
        return movieMapper.findById(id);
    }
}
